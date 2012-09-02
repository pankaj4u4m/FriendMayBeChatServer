package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.xmpp.packet.Message;

import com.metly.openfire.exception.MetlyException;
import com.metly.openfire.utils.ApplicationProperties;

import de.javawi.jstun.attribute.MessageAttributeInterface.MessageAttributeType;

public class MessageDB extends AbstractDB {
    private final String INSERT_NEW_MESSAGE;

    public MessageDB() {
        super();
        INSERT_NEW_MESSAGE =
                "INSERT INTO " + ApplicationProperties.getProperty("metly.messages_archives_db")
                        + "(sender_id, receiver_id, body, created_at, updated_at)" + "VALUES(?, ?, ?, ?, ?)";
    }

    public void save(Message packet) {
        if(packet.getType() == Message.Type.error ){
            return;
        }
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(INSERT_NEW_MESSAGE);
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

            prepareStatement.setString(3, packet.getBody());
            java.sql.Timestamp time = new java.sql.Timestamp(System.currentTimeMillis());
            prepareStatement.setTimestamp(4, time);
            prepareStatement.setTimestamp(5, time);

            prepareStatement.execute();
        } catch (SQLException e) {
            throw new MetlyException("Error on saving message:" + packet + ", SQL:\n" + INSERT_NEW_MESSAGE, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }
}
