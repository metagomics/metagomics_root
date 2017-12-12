package org.uwpr.metaproteomics.emma.go;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.uwpr.metagomics.database.DBConnectionManager;

public class GONodeFactory {

	private static final GONodeFactory INSTANCE = new GONodeFactory();
	private GONodeFactory() { }
	
	public static GONodeFactory getInstance() { return INSTANCE; }
	
	public static final GONode NODE_UNKNOWN_BIOLOGICAL_PROCESS;
	public static final GONode NODE_UNKNOWN_MOLECULAR_FUNCTION;
	public static final GONode NODE_UNKNOWN_CELLULAR_COMPONENT;
	
	// caches using soft references, so they may be garbage collected as necessary
	private Map<String, GONode> GO_NODE_CACHE_BY_ACC;
	private Map<Integer, GONode> GO_NODE_CACHE_BY_ID;
	
	static {
		
		NODE_UNKNOWN_BIOLOGICAL_PROCESS = new GONode();
		NODE_UNKNOWN_BIOLOGICAL_PROCESS.setAcc( GOUtils.UNKNOWN_PROCESS_ACC );
		NODE_UNKNOWN_BIOLOGICAL_PROCESS.setName( "unknown biological process" );
		NODE_UNKNOWN_BIOLOGICAL_PROCESS.setTermType( "biological_process" );
		NODE_UNKNOWN_BIOLOGICAL_PROCESS.setId( GOUtils.UNKNOWN_PROCESS_ID );
		NODE_UNKNOWN_BIOLOGICAL_PROCESS.setIsObsolete( 0 );
		NODE_UNKNOWN_BIOLOGICAL_PROCESS.setIsRelation( 0 );
		NODE_UNKNOWN_BIOLOGICAL_PROCESS.setIsRoot( 0 );
		
		NODE_UNKNOWN_MOLECULAR_FUNCTION = new GONode();
		NODE_UNKNOWN_MOLECULAR_FUNCTION.setAcc( GOUtils.UNKNOWN_FUNCTION_ACC );
		NODE_UNKNOWN_MOLECULAR_FUNCTION.setName( "unknown molecular function" );
		NODE_UNKNOWN_MOLECULAR_FUNCTION.setTermType( "molecular_function" );
		NODE_UNKNOWN_MOLECULAR_FUNCTION.setId( GOUtils.UNKNOWN_FUNCTION_ID );
		NODE_UNKNOWN_MOLECULAR_FUNCTION.setIsObsolete( 0 );
		NODE_UNKNOWN_MOLECULAR_FUNCTION.setIsRelation( 0 );
		NODE_UNKNOWN_MOLECULAR_FUNCTION.setIsRoot( 0 );
		
		NODE_UNKNOWN_CELLULAR_COMPONENT = new GONode();
		NODE_UNKNOWN_CELLULAR_COMPONENT.setAcc( GOUtils.UNKNOWN_COMPONENT_ACC );
		NODE_UNKNOWN_CELLULAR_COMPONENT.setName( "unknown cellular component" );
		NODE_UNKNOWN_CELLULAR_COMPONENT.setTermType( "cellular_component" );
		NODE_UNKNOWN_CELLULAR_COMPONENT.setId( GOUtils.UNKNOWN_COMPONENT_ID );
		NODE_UNKNOWN_CELLULAR_COMPONENT.setIsObsolete( 0 );
		NODE_UNKNOWN_CELLULAR_COMPONENT.setIsRelation( 0 );
		NODE_UNKNOWN_CELLULAR_COMPONENT.setIsRoot( 0 );
		
	}
	
	
	/**
	 * Get the given GONode from the cache.
	 * 
	 * @param acc
	 * @return
	 * @throws Exception
	 */
	public GONode getGONode( String acc ) throws Exception {
		
		// initialize the cache
		if( GO_NODE_CACHE_BY_ACC == null ) {
			GO_NODE_CACHE_BY_ACC = new HashMap<String, GONode>();
		}

		if( !GO_NODE_CACHE_BY_ACC.containsKey( acc ) ) {
			GO_NODE_CACHE_BY_ACC.put( acc, getGONodeFromDatabase( acc ) );
		}
		
		return GO_NODE_CACHE_BY_ACC.get( acc );		
	}
	
	
	/**
	 * Get the given GONode from the cache.
	 * 
	 * @param acc
	 * @return
	 * @throws Exception
	 */
	public GONode getGONode( int id ) throws Exception {
		
		// initialize the cache
		if( GO_NODE_CACHE_BY_ID == null ) {
			GO_NODE_CACHE_BY_ID = new HashMap<Integer, GONode>();
		}
				
		if( !GO_NODE_CACHE_BY_ID.containsKey( id ) ) {
			GO_NODE_CACHE_BY_ID.put( id, getGONodeFromDatabase( id ) );
		}
		
		return GO_NODE_CACHE_BY_ID.get( id );		
	}
	
	
	
	/**
	 * Get the go term with the given acc from the database

	 * @param acc
	 * @return
	 * @throws Exception
	 */
	public GONode getGONodeFromDatabase( String acc ) throws Exception {
		
		
		if (acc.equals( GOUtils.UNKNOWN_COMPONENT_ACC ) ) {
			return GONodeFactory.NODE_UNKNOWN_CELLULAR_COMPONENT;
		}
		if (acc.equals( GOUtils.UNKNOWN_FUNCTION_ACC ) ) {
			return GONodeFactory.NODE_UNKNOWN_MOLECULAR_FUNCTION;
		}
		if (acc.equals( GOUtils.UNKNOWN_PROCESS_ACC ) ) {
			return GONodeFactory.NODE_UNKNOWN_BIOLOGICAL_PROCESS;
		}
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		GONode gnode;
		
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.GO_DB );
			
			// Our SQL statement
			String sqlStr =  "SELECT id, name, term_type, acc, is_obsolete, is_root, is_relation FROM term WHERE acc = ?";
			stmt = conn.prepareStatement(sqlStr);
			stmt.setString( 1, acc );

			// Our results
			rs = stmt.executeQuery();

			// Not found, pass our problems off to the caller
			if (!rs.next())
				throw new IllegalArgumentException("Invalid acc: " + acc);
			
			gnode = new GONode();
			gnode.setId( rs.getInt( "id" ) );
			gnode.setName( rs.getString( "name" ) );
			gnode.setTermType( rs.getString( "term_type" ) );
			gnode.setAcc( rs.getString( "acc" ) );
			gnode.setIsObsolete( rs.getInt( "is_obsolete" ) );
			gnode.setIsRoot( rs.getInt( "is_root" ) );
			gnode.setIsRelation( rs.getInt( "is_relation" ) );
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
		}
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		
		return gnode;
	}
	
	/**
	 * Get the go term with the given id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public GONode getGONodeFromDatabase( int id ) throws Exception {
		
		if (id == GOUtils.UNKNOWN_COMPONENT_ID ) {
			return GONodeFactory.NODE_UNKNOWN_CELLULAR_COMPONENT;
		}
		if (id == GOUtils.UNKNOWN_FUNCTION_ID ) {
			return GONodeFactory.NODE_UNKNOWN_MOLECULAR_FUNCTION;
		}
		if (id == GOUtils.UNKNOWN_PROCESS_ID ) {
			return GONodeFactory.NODE_UNKNOWN_BIOLOGICAL_PROCESS;
		}		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		GONode gnode;
		
		
		try {
			
			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.GO_DB );
			
			// Our SQL statement
			String sqlStr =  "SELECT id, name, term_type, acc, is_obsolete, is_root, is_relation FROM term WHERE id = ?";
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt( 1, id );

			// Our results
			rs = stmt.executeQuery();

			// Not found, pass our problems off to the caller
			if (!rs.next())
				throw new IllegalArgumentException("Invalid id: " + id);
			
			gnode = new GONode();
			gnode.setId( rs.getInt( "id" ) );
			gnode.setName( rs.getString( "name" ) );
			gnode.setTermType( rs.getString( "term_type" ) );
			gnode.setAcc( rs.getString( "acc" ) );
			gnode.setIsObsolete( rs.getInt( "is_obsolete" ) );
			gnode.setIsRoot( rs.getInt( "is_root" ) );
			gnode.setIsRelation( rs.getInt( "is_relation" ) );
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
		}
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		
		return gnode;
	}
	
}
