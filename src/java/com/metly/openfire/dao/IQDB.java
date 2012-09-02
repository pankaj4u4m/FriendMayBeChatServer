package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Presence;

import com.metly.openfire.exception.MetlyException;
import com.metly.openfire.utils.ApplicationProperties;

public class IQDB extends AbstractDB {
    private final String INSERT_NEW_IQ;

    public IQDB() {
        super();
        INSERT_NEW_IQ =
                "INSERT INTO " + ApplicationProperties.getProperty("metly.stanza_archives_db")
                        + "(sender_id, receiver_id, stanza_type, stanza, created_at, updated_at)"
                        + "VALUES(?, ?, ?, ?, ?, ?)";
    }

    public void save(IQ packet) {
        if(packet.getType() == IQ.Type.error ){
            return;
        }
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(INSERT_NEW_IQ);
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

            prepareStatement.setString(3, "IQ");
            prepareStatement.setString(4, packet.toString());
            java.sql.Timestamp time = new java.sql.Timestamp(System.currentTimeMillis());
            prepareStatement.setTimestamp(5, time);
            prepareStatement.setTimestamp(6, time);

            prepareStatement.execute();
        } catch (SQLException e) {
            throw new MetlyException("Error on saving message:" + packet + ", SQL:\n" + INSERT_NEW_IQ, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }
}
