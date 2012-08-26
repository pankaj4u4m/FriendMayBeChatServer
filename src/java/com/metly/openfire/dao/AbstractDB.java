package com.metly.openfire.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.JiveGlobals;
import org.xmpp.packet.JID;

import com.metly.openfire.exception.MetlyException;

public abstract class AbstractDB {
	private static final String SELECT_USER_ID = "SELECT id FROM "
			+ JiveGlobals.getProperty("metly.users_db") + " WHERE xmpp=?";
	private static final String SELECT_USER_XMPP = "SELECT id FROM "
			+ JiveGlobals.getProperty("metly.users_db") + " WHERE id=?";

	public Long getUserId(JID jid) {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = DbConnectionManager.getConnection();
			prepareStatement = connection.prepareStatement(SELECT_USER_ID);
			prepareStatement.setString(1,jid.getNode());

			ResultSet result = prepareStatement.executeQuery();
			if(result.next()){
				return result.getLong(0);
			} 
			return null;
		} catch (SQLException e) {
			throw new MetlyException("Error on tretrieveing user xmpp:" + jid, e);
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
			if(result.next()){
				return result.getString(0);
			} 
			return null;
		} catch (SQLException e) {
			throw new MetlyException("Error on tretrieveing user id:" + id, e);
		} finally {
			DbConnectionManager.closeConnection(prepareStatement, connection);
		}
	}
}
