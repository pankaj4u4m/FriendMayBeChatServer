package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.JiveGlobals;
import org.xmpp.packet.IQ;

import com.metly.openfire.exception.MetlyException;

public class IQDB extends AbstractDB{
    private static final String INSERT_NEW_IQ="INSERT INTO "
			+ JiveGlobals.getProperty("metly.stanza_archives_db") + "(sender_id, receiver_id, stanza_type, stanza, created_at, updated_at)" +
    		"VALUES(?, ?, ?, ?, ?, ?)";
    
    public void save(IQ packet){
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(INSERT_NEW_IQ);
            prepareStatement.setLong(1, this.getUserId(packet.getFrom()));
            prepareStatement.setLong(2, this.getUserId(packet.getTo()));
            prepareStatement.setString(3, "IQ");
            prepareStatement.setString(4, packet.toString());
            java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
            prepareStatement.setDate(5, date);
            prepareStatement.setDate(6, date);
            
            prepareStatement.execute();
        } catch (SQLException e) {
            throw new MetlyException("Error on saving message:" + packet, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
    }
}
