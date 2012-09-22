package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.jivesoftware.database.DbConnectionManager;
import org.xmpp.packet.JID;

import com.metly.openfire.cache.Cache;
import com.metly.openfire.cache.CacheFactory;
import com.metly.openfire.dao.MetlyServiceDBClient.Point;
import com.metly.openfire.exception.MetlyException;
import com.metly.openfire.logic.MetlyServiceClient;
import com.metly.openfire.logic.MetlyUser;
import com.metly.openfire.utils.ApplicationProperties;

public class MetlyAnonymousClient extends AbstractDB implements MetlyServiceClient {
    private static final Logger log = Logger.getLogger(MetlyAnonymousClient.class);

    private final String USER_CORDINATES;

    private final String LOCK_STRANGER;
    
    private final String LOCK_RANDOM_STRANGER;

    private final String INSERT_USER_CONNECTION_STATUSES;

    private final String DELETE_USER_CONNECTION_STATUSES;

    private final String GET_MATCHED_STRANGER_FOR_USER_JID;

    private final String GET_MATCHED_USER_FOR_STRANGER_JID;

    private final int MAX_WAIT_TIME = 20;

    private static final Cache cache = CacheFactory.getCache();

    public MetlyAnonymousClient() {
        // anonymous
        USER_CORDINATES =
                "SELECT X(location), Y(location) FROM "
                        + ApplicationProperties.getProperty("metly.login_locations_db")
                        + " WHERE resource=? ORDER BY created_at DESC";

        LOCK_STRANGER =
                "UPDATE " + ApplicationProperties.getProperty("metly.user_connection_statuses_db")
                        + " su SET su.user_status=?, su.stranger_id=?, su.stranger_jid=?, su.updated_at=? "
                        + " WHERE su.id in (SELECT * FROM (SELECT id from (SELECT s.id, s.created_at FROM "
                        + ApplicationProperties.getProperty("metly.login_locations_db") + " l JOIN "
                        + ApplicationProperties.getProperty("metly.user_connection_statuses_db")
                        + " s WHERE user_status='W' AND  TIMESTAMPDIFF(SECOND, ?, s.created_at) < "
                        + (MAX_WAIT_TIME - 5)
                        + " ORDER BY POW(X(location) - ?, 2) + POW(Y(location) - ?, 2) ASC LIMIT 1000) AS r "
                        + " ORDER BY created_at ASC LIMIT 1) AS t) ";

        LOCK_RANDOM_STRANGER =
                "UPDATE " + ApplicationProperties.getProperty("metly.user_connection_statuses_db")
                        + " su SET su.user_status=?, su.stranger_id=?, su.stranger_jid=?, su.updated_at=? "
                        + " WHERE su.id in (SELECT * FROM (SELECT id from "
                        + ApplicationProperties.getProperty("metly.user_connection_statuses_db")
                        + " WHERE user_status='W' AND  TIMESTAMPDIFF(SECOND, ?, created_at) < "
                        + (MAX_WAIT_TIME - 5) + " ORDER BY created_at ASC LIMIT 1) AS t) ";
        
        GET_MATCHED_USER_FOR_STRANGER_JID =
                "SELECT user_jid FROM "
                        + ApplicationProperties.getProperty("metly.user_connection_statuses_db")
                        + " s WHERE user_status='C' AND stranger_jid=? AND s.updated_at=? ORDER BY s.created_at DESC LIMIT 1";

        GET_MATCHED_STRANGER_FOR_USER_JID =
                "SELECT stranger_jid FROM "
                        + ApplicationProperties.getProperty("metly.user_connection_statuses_db")
                        + " s WHERE user_status='C' AND user_jid=? ORDER BY s.created_at DESC LIMIT 1";

        INSERT_USER_CONNECTION_STATUSES =
                "INSERT INTO "
                        + ApplicationProperties.getProperty("metly.user_connection_statuses_db")
                        + " (user_id, user_jid, user_status, stranger_jid, created_at, updated_at) VALUES(?, ?, ?, ?, ?, ?)";

        DELETE_USER_CONNECTION_STATUSES =
                "DELETE FROM "
                        + ApplicationProperties.getProperty("metly.user_connection_statuses_db")
                        + " WHERE user_jid=? OR stranger_jid=? OR ( user_status='W' AND TIMESTAMPDIFF(SECOND, ?, created_at) > "
                        + MAX_WAIT_TIME + " ) OR TIMESTAMPDIFF(DAY, ?, updated_at) > 1";
    }

    @Override
    public MetlyUser getNewStranger(String userJID) {
        MetlyUser matchedStranger = this.getCachedMatchedStranger(userJID);

        if (matchedStranger == null) {
            matchedStranger = this.getMatchedStranger(userJID);
        }
        if (matchedStranger != null) {
            return matchedStranger;
        }
        try {
            Object get = cache.get(userJID);
            if (get != null && !get.equals("null")) {
                log.info(get.getClass());
                cache.delete(MetlyUser.getUserFromJSON((String) get).getJID());
            }
            cache.delete(userJID);
        } catch (Exception e) {
            log.error("unable to DELETE cache key:" + userJID, e);
        }
        MetlyUser stranger = this.matchUser(userJID);
        if (stranger == null) {
            this.waitUser(userJID);
            int waitTime = 0;
            while (waitTime < 2 * MAX_WAIT_TIME && stranger == null) {
                stranger = this.getCachedMatchedStranger(userJID);
                ++waitTime;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new MetlyException(e);
                }
            }
        }
        return stranger;
    }

    private MetlyUser matchUser(String userJID) {

        Timestamp lockDateTime = null;

        Point point = this.getCordinates(userJID);
        if (point != null) {
            lockDateTime = this.lockStranger(point, userJID);
        }
        if(lockDateTime == null ) {
            lockDateTime = this.lockRandomStranger(userJID);
        }
        if(lockDateTime == null){
            return null;
        }
        MetlyUser stranger = getMatchedUserForStrangerJID(userJID, lockDateTime);
        if (stranger != null) {
            setMatchedStrangerForUser(userJID, stranger);
            MetlyUser user = getMatchedStranger(stranger.getJID());
            if (user != null) {
                try {
                    cache.set(stranger.getJID(), MetlyUser.getJSONString(user));
                    cache.set(user.getJID(), MetlyUser.getJSONString(stranger));
                } catch (Exception e) {
                    log.error("Error on cache SET key:" + userJID, e);
                }

                return stranger;
            }
        }
        return null;
    }

    private void setMatchedStrangerForUser(String userJID, MetlyUser stranger) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(INSERT_USER_CONNECTION_STATUSES);

            Long userId;
            if ((userId = this.getUserId(new JID(userJID))) != null) {
                prepareStatement.setLong(1, userId);
            } else {
                prepareStatement.setNull(1, java.sql.Types.BIGINT);
            }
            prepareStatement.setString(2, userJID);
            prepareStatement.setString(3, "C");
            prepareStatement.setString(4, stranger.getJID());

            java.sql.Timestamp dateTime = new java.sql.Timestamp(System.currentTimeMillis());
            prepareStatement.setTimestamp(5, dateTime);
            prepareStatement.setTimestamp(6, dateTime);

            log.info("INSERT_USER_CONNECTION_STATUSES:" + prepareStatement);

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new MetlyException("Error on saving user_statuses:" + userJID + " , SQL:\n" + prepareStatement, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }

    private MetlyUser getMatchedUserForStrangerJID(String strangerJID, Timestamp lockDateTime) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(GET_MATCHED_USER_FOR_STRANGER_JID);

            prepareStatement.setString(1, strangerJID);
            prepareStatement.setTimestamp(2, lockDateTime);

            log.info("GET_MATCHED_USER_FOR_STRANGER_JID : " + prepareStatement);

            ResultSet result = prepareStatement.executeQuery();

            if (result.next()) {
                MetlyUser metlyUser = new MetlyUser();
                metlyUser.setJID(result.getString(1));
                return metlyUser;
            }
            return null;

        } catch (SQLException e) {
            throw new MetlyException("Error on LOCK USER" + strangerJID + " , SQL:\n" + prepareStatement, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }

    private Timestamp lockStranger(Point point, String userJID) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;

        try {
            connection = DbConnectionManager.getConnection();

            prepareStatement = connection.prepareStatement(LOCK_STRANGER);

            prepareStatement.setString(1, "C");

            Long userId;
            if ((userId = this.getUserId(new JID(userJID))) != null) {
                prepareStatement.setLong(2, userId);
            } else {
                prepareStatement.setNull(2, java.sql.Types.BIGINT);

            }

            prepareStatement.setString(3, userJID);

            java.sql.Timestamp dateTime = new java.sql.Timestamp(System.currentTimeMillis());
            prepareStatement.setTimestamp(4, dateTime);
            prepareStatement.setTimestamp(5, dateTime);
            prepareStatement.setDouble(6, point.x);
            prepareStatement.setDouble(7, point.y);

            log.info("LOCK_STRANGER:" + prepareStatement);

            int update = prepareStatement.executeUpdate();
            if (update > 0) {
                return dateTime;
            }
            return null;
        } catch (SQLException e) {
            throw new MetlyException("Error on LOCK USER" + userJID + " , SQL:\n" + prepareStatement, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }

    private Timestamp lockRandomStranger(String userJID) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();

            prepareStatement = connection.prepareStatement(LOCK_RANDOM_STRANGER);

            prepareStatement.setString(1, "C");

            Long userId;
            if ((userId = this.getUserId(new JID(userJID))) != null) {
                prepareStatement.setLong(2, userId);
            } else {
                prepareStatement.setNull(2, java.sql.Types.BIGINT);

            }

            prepareStatement.setString(3, userJID);

            java.sql.Timestamp dateTime = new java.sql.Timestamp(System.currentTimeMillis());
            prepareStatement.setTimestamp(4, dateTime);
            prepareStatement.setTimestamp(5, dateTime);
            
            log.info("LOCK_RANDOM_STRANGER:" + prepareStatement);
            
            int updated = prepareStatement.executeUpdate();
            if(updated > 0){
                return dateTime;
            }
            return null;
        } catch (SQLException e) {
            throw new MetlyException("Error on LOCK USER" + userJID + " , SQL:\n" + prepareStatement, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }
    private MetlyUser getCachedMatchedStranger(String matchedKey) {
        Object get = cache.get(matchedKey);
        if (get == null) {
            return null;
        }
        return MetlyUser.getUserFromJSON((String) get);
    }

    private void waitUser(String userJID) {

        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(INSERT_USER_CONNECTION_STATUSES);

            Long userId;
            if ((userId = this.getUserId(new JID(userJID))) != null) {
                prepareStatement.setLong(1, userId);
            } else {
                prepareStatement.setNull(1, java.sql.Types.BIGINT);
            }
            prepareStatement.setString(2, userJID);
            prepareStatement.setString(3, "W");
            prepareStatement.setNull(4, java.sql.Types.VARCHAR);

            java.sql.Timestamp dateTime = new java.sql.Timestamp(System.currentTimeMillis());
            prepareStatement.setTimestamp(5, dateTime);
            prepareStatement.setTimestamp(6, dateTime);

            log.info("INSERT_USER_CONNECTION_STATUSES : " + prepareStatement);

            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new MetlyException("Error on saving user_statuses:" + userJID + " , SQL:\n" + prepareStatement, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }

    }

    private Point getCordinates(String userJID) {
        JID jid = new JID(userJID);
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(USER_CORDINATES);
            prepareStatement.setString(1, jid.getResource());

            log.info("USER_CORDINATES:" + prepareStatement);

            ResultSet result = prepareStatement.executeQuery();
            if (result.next()) {
                return new Point(result.getDouble(1), result.getDouble(2));
            }
            return null;
        } catch (SQLException e) {
            throw new MetlyException("Error on getting user cordinates:" + userJID + " , SQL:\n" + prepareStatement, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }

    @Override
    public MetlyUser getMatchedStranger(String userJID) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(GET_MATCHED_STRANGER_FOR_USER_JID);

            prepareStatement.setString(1, userJID);

            log.info("GET_MATCHED_STRANGER_FOR_USER_JID:" + prepareStatement);

            ResultSet result = prepareStatement.executeQuery();
            if (result.next()) {
                MetlyUser metlyUser = new MetlyUser();
                metlyUser.setJID(result.getString(1));

                return metlyUser;
            }
            log.warn("No user found for key:" + userJID);
            return null;

        } catch (SQLException e) {
            throw new MetlyException("Error on GET_MATCHED_STRANGER_FOR_USER_JID key:" + userJID + " , SQL:\n"
                    + prepareStatement, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }

    @Override
    public void clearMapping(String userJID) {
        try {
            Object get = cache.get(userJID);
            if (get != null && !get.equals("null")) {
                log.info(get.getClass());
                cache.delete(MetlyUser.getUserFromJSON((String) get).getJID());
            }
            cache.delete(userJID);
        } catch (Exception e) {
            log.error("unable to DELETE cache key:" + userJID, e);
        }
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(DELETE_USER_CONNECTION_STATUSES);

            prepareStatement.setString(1, userJID);
            prepareStatement.setString(2, userJID);
            java.sql.Timestamp dateTime = new java.sql.Timestamp(System.currentTimeMillis());
            prepareStatement.setTimestamp(3, dateTime);
            prepareStatement.setTimestamp(4, dateTime);

            log.info("DELETE_USER_CONNECTION_STATUSES:" + prepareStatement);

            prepareStatement.executeUpdate();

        } catch (SQLException e) {
            throw new MetlyException("Error on DELETE user_statuses:" + userJID + " , SQL:\n" + prepareStatement, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }

    static class Point {
        private double x;
        private double y;

        public Point(double d, double e) {
            this.x = d;
            this.y = e;
        }
    }
}
