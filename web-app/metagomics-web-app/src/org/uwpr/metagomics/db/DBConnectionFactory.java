package org.uwpr.metagomics.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


public class DBConnectionFactory {

	public static final String DB = "metagomics";

	public static String _COMPGO_DATABASE = null;
	
	/**
	 * 
	 * @param db
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(String db) throws Exception {

		try {

			//if( !db.equals( _GO_DATABASE ) && !db.equals( _COMPGO_DATABASE ) )
			//	throw new Exception( "Did not get a valid database." );
			
			Context ctx = new InitialContext();
			DataSource ds;
			Connection conn;
			
			if (db.equals(DB)) { ds = (DataSource)ctx.lookup("java:comp/env/jdbc/metagomics"); }
			else { throw new SQLException("Invalid database name passed into DBConnectionManager."); }

			if (ds != null) {
				conn = ds.getConnection();
				if (conn != null) { return conn; }
				else { throw new SQLException("Got a null connection..."); }
			}

			throw new SQLException("Got a null DataSource...");
			
			
		} catch (Throwable t) {
			throw new SQLException("Exception: " + t.getMessage());
		}
	}

}
