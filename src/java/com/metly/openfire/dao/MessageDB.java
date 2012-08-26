package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.JiveGlobals;
import org.xmpp.packet.Message;

import com.metly.openfire.exception.MetlyException;

public class MessageDB extends AbstractDB{
    private static final String INSERT_NEW_MESSAGE = "INSERT INTO "
			+ JiveGlobals.getProperty("metly.messages_archive_db") + "(sender_id, receiver_id, body, created_at, updated_at)" +
    		"VALUES(?, ?, ?, ?, ?)";
    
    public MessageDB(){
        
    }
    
    public void save(Message packet){
    	 Connection connection = null;
         PreparedStatement prepareStatement = null;
         try {
             connection = DbConnectionManager.getConnection();
             prepareStatement = connection.prepareStatement(INSERT_NEW_MESSAGE);
             prepareStatement.setLong(1, this.getUserId(packet.getFrom()));
             prepareStatement.setLong(2, this.getUserId(packet.getTo()));
             prepareStatement.setString(3, packet.getBody());
             java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
             prepareStatement.setDate(4, date);
             prepareStatement.setDate(5, date);
             
             prepareStatement.execute();
         } catch (SQLException e) {
             throw new MetlyException("Error on saving message:" + packet, e);
         } finally {
             DbConnectionManager.closeConnection(prepareStatement, connection);
         }
    }
}
