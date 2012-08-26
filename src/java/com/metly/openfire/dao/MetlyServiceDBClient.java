package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.xmpp.packet.JID;

import com.metly.openfire.exception.MetlyException;
import com.metly.openfire.logic.MetlyServiceClient;
import com.metly.openfire.logic.MetlyUser;
import com.metly.openfire.utils.ApplicationProperties;

public class MetlyServiceDBClient extends AbstractDB implements MetlyServiceClient {
	
	private final String NEAR_USERS;
	
	private final String USER_CORDINATES;
	
	private final String INSERT_USER_CONNECTION_STATUESE;

	public MetlyServiceDBClient(){
		 NEAR_USERS = "SELECT u.xmpp, l.created_at FROM  " +
				 ApplicationProperties.getProperty("metly.login_locations_db") + " l JOIN " + ApplicationProperties.getProperty("metly.users_db") + 
					" u ON u.id=l.login_id JOIN " + ApplicationProperties.getProperty("metly.user_connection_statuses_db") + 
					" s ON u.id=s.user_id WHERE user_status='W' ORDER BY POW(X(location) - ?, 2) + POW(Y(location) - ?, 2) ASC LIMIT 100";
		 
		 USER_CORDINATES = "SELECT X(location), Y(location) FROM " +
				 ApplicationProperties.getProperty("metly.login_locations_db") + " l JOIN " + ApplicationProperties.getProperty("metly.users_db") + " u ON u.id=l.login_id  " +
							"WHERE xmpp=? AND resource=? ORDER BY l.created_at DESC";
		 
		 INSERT_USER_CONNECTION_STATUESE = "INSERT INTO " + ApplicationProperties.getProperty("metly.user_connection_statuses_db") + 
					"(user_id, stranger_id, user_status, created_at, updated_at) VALUES(?, ?, ?, ?, ?)";
		 
	}
    @Override
    public MetlyUser getNewStranger(String userJID, String systemJID){
    	Point point = this.getCordinates(userJID);
    	boolean connected = false;
    	if(!connected){
    		waitUser(userJID);
    	}
        return new MetlyUser("pankaj@localhost/f74317ec");
    }
    
	private void waitUser(String userJID) {
		Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(INSERT_USER_CONNECTION_STATUESE);
            
            Long userId;
            if((userId = this.getUserId(new JID(userJID)))!= null ){
            	prepareStatement.setLong(1, userId);
            } else {
            	prepareStatement.setNull(1, java.sql.Types.BIGINT);
            }
            
            prepareStatement.setString(3, "W");
            java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
            prepareStatement.setDate(5, date);
            prepareStatement.setDate(6, date);
            
            prepareStatement.execute();
        } catch (SQLException e) {
            throw new MetlyException("Error on saving user_statuses:" + userJID, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
		
	}
	private Point getCordinates(String userJID){
		JID jid = new JID(userJID);
		Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = DbConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(USER_CORDINATES);
            prepareStatement.setString(1, jid.getNode());
            prepareStatement.setString(2, jid.getResource());
            ResultSet result = prepareStatement.executeQuery();
			if(result.next()){
				return new Point(result.getDouble(1), result.getDouble(2));
			} 
			return new Point(0, 0);
        } catch (SQLException e) {
            throw new MetlyException("Error on getting user cordinates:" + userJID, e);
        } finally {
            DbConnectionManager.closeConnection(prepareStatement, connection);
        }
	}
	
    @Override
    public MetlyUser getMatchedStranger(String userJID, String systemJID) {
        return new MetlyUser("pankaj@localhost/f74317ec");
    }
    
	static class Point{
		private double x;
		private double y;
		public Point(double x, double y){
			this.x = x;
			this.y = y;
		}
		public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}
		
		
	}
}
