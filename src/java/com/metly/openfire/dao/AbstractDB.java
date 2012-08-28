package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.xmpp.packet.JID;

import com.metly.openfire.exception.MetlyException;
import com.metly.openfire.utils.ApplicationProperties;

public abstract class AbstractDB {
    private final String SELECT_USER_ID;
    private final String SELECT_USER_XMPP;

    public AbstractDB() {
        SELECT_USER_ID = "SELECT id FROM " + ApplicationProperties.getProperty("metly.users_db") + " WHERE xmpp=?";
        SELECT_USER_XMPP = "SELECT id FROM " + ApplicationProperties.getProperty("metly.users_db") + " WHERE id=?";

    }

    public Long getUserId(JID jid) {
        if (jid == null) {
            return null;
        }
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(SELECT_USER_ID);
            if (jid.getNode() != null) {
                prepareStatement.setString(1, jid.getNode());
            } else {
                prepareStatement.setNull(1, java.sql.Types.VARCHAR);
            }

            ResultSet result = prepareStatement.executeQuery();
            if (result.next()) {
                return result.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new MetlyException("Error on retrieveing user xmpp:" + jid + ", SQL:\n" + SELECT_USER_ID, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }

    public String getUserXmpp(Long id) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(SELECT_USER_XMPP);
            prepareStatement.setLong(1, id);

            ResultSet result = prepareStatement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
            return null;
        } catch (SQLException e) {
            throw new MetlyException("Error on tretrieveing user id:" + id + ", SQL:\n" + SELECT_USER_XMPP, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }
}
