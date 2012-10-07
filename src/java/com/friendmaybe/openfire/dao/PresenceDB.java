package com.friendmaybe.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.xmpp.packet.Message;
import org.xmpp.packet.Presence;

import com.friendmaybe.openfire.exception.FriendmaybeException;
import com.friendmaybe.openfire.utils.ApplicationProperties;

public class PresenceDB extends AbstractDB {
    private final String INSERT_NEW_PRESENCE;

    public PresenceDB() {
        super();
        INSERT_NEW_PRESENCE =
                "INSERT INTO " + ApplicationProperties.getProperty("friendmaybe.stanza_archives_db")
                        + "(sender_id, receiver_id, stanza_type, stanza, created_at, updated_at)"
                        + "VALUES(?, ?, ?, ?, ?, ?)";
        ;
    }

    public void save(Presence packet) {
        if(packet.getType() == Presence.Type.error ){
            return;
        }
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(INSERT_NEW_PRESENCE);
            Long userId;
            if ((userId = this.getUserId(packet.getFrom())) != null) {
                prepareStatement.setLong(1, userId);
            } else {
                prepareStatement.setNull(1, java.sql.Types.BIGINT);
            }
            if ((userId = this.getUserId(packet.getTo())) != null) {
                prepareStatement.setLong(2, userId);
            } else {
                prepareStatement.setNull(2, java.sql.Types.BIGINT);
            }
            prepareStatement.setString(3, "PRESENCE");
            prepareStatement.setString(4, packet.toString());
            java.sql.Timestamp time = new java.sql.Timestamp(System.currentTimeMillis());
            prepareStatement.setTimestamp(5, time);
            prepareStatement.setTimestamp(6, time);

            prepareStatement.execute();
        } catch (SQLException e) {
            throw new FriendmaybeException("Error on saving message:" + packet + ", SQL:\n" + INSERT_NEW_PRESENCE, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }
}
