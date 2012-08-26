package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.JiveGlobals;
import org.xmpp.packet.JID;

import com.metly.openfire.exception.MetlyException;
import com.metly.openfire.logic.MetlyServiceClient;
import com.metly.openfire.logic.MetlyUser;

public class MetlyServiceDBClient extends AbstractDB implements MetlyServiceClient {
	private static final String NEAR_USERS = "select u.xmpp, l.created_at from  " +
			JiveGlobals.getProperty("metly.login_locations_db") + " l join " + JiveGlobals.getProperty("metly.users_db") + 
			" u on u.id=l.login_id join " + JiveGlobals.getProperty("metly.user_connection_statuses_db") + 
			" s on u.id=s.user_id where user_status='W' order by POW(X(location) - ?, 2) + POW(Y(location) - ?, 2) ASC limit 100";

	
	private static final String USER_CORDINATES = "select X(location), Y(location) from " +
			JiveGlobals.getProperty("metly.login_locations_db") + " l join " + JiveGlobals.getProperty("metly.users_db") + " u on u.id=l.login_id  " +
					"where xmpp=? and resource=? order by l.created_at DESC";
	
	private static final String INSERT_USER_CONNECTION_STATUESE = "insert into " + JiveGlobals.getProperty("metly.user_connection_statuses_db") + 
			"(user_id, stranger_id, user_status, created_at, updated_at) values(?, ?, ?, ?, ?)";

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
            prepareStatement.setLong(1, this.getUserId(new JID(userJID)));
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
				return new Point(result.getDouble(0), result.getDouble(1));
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
