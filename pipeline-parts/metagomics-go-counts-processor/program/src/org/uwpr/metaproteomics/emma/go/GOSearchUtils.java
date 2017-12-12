package org.uwpr.metaproteomics.emma.go;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.uwpr.metagomics.database.DBConnectionManager;

public class GOSearchUtils {
	
	// caching for parent nodes
	private static Map<GONode, Collection<GONode>> DIRECT_PARENTS_FOR_GO_NODES;
	private static Map<GONode, Collection<GONode>> ALL_PARENTS_FOR_GO_NODES;
	
	/**
	 * Get direct parents of this go node (i.e., get more general terms connected directly to this term)
	 * @param gnode
	 * @return
	 * @throws Exception
	 */
	public static Collection<GONode> getDirectParentNodes( GONode gnode ) throws Exception {
		
		if( DIRECT_PARENTS_FOR_GO_NODES == null )
			DIRECT_PARENTS_FOR_GO_NODES = new HashMap<>();
		
		if( !DIRECT_PARENTS_FOR_GO_NODES.containsKey( gnode ) )
			DIRECT_PARENTS_FOR_GO_NODES.put( gnode, getDirectParentNodesFromDatabase( gnode ) );
		
		return DIRECT_PARENTS_FOR_GO_NODES.get( gnode );
	}

	
	
	/**
	 * Get direct parents of this go node (i.e., get more general terms connected directly to this term)
	 * from the database
	 * 
	 * @param gnode
	 * @return
	 * @throws Exception
	 */
	public static Collection<GONode> getDirectParentNodesFromDatabase( GONode gnode ) throws Exception {
		Collection<GONode> nodes = new HashSet<GONode>();
		
		if( gnode.getId() == GOUtils.UNKNOWN_COMPONENT_ID || gnode.getId() == GOUtils.UNKNOWN_FUNCTION_ID || gnode.getId() == GOUtils.UNKNOWN_PROCESS_ID ) {
			nodes.add( GOUtils.getAspectRootNode( gnode.getTermType() ) );
			return nodes;
		}
		
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.GO_DB );
			
			// Our SQL statement, relationship_type = 1 means that it is a "is_a" relationship
			String sqlStr =  "SELECT term1_id FROM term2term WHERE term2_id = ? AND relationship_type_id = 1 AND term1_id <> term2_id";
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt( 1, gnode.getId() );

			// Our results
			rs = stmt.executeQuery();
		
			while( rs.next() ) {
				nodes.add( GONodeFactory.getInstance().getGONode( rs.getInt( 1 ) ) );
			}			
			
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
		
		nodes.remove( GOUtils.getAllNode() );
		return nodes;
	}
	
	
	/**
	 * Get all parents of this go node (i.e., get more general terms connected directly and indirectly to this term)
	 * 
	 * @param gnode
	 * @return
	 * @throws Exception
	 */
	public static Collection<GONode> getAllParentNodes( GONode gnode ) throws Exception {
		
		if( ALL_PARENTS_FOR_GO_NODES == null )
			ALL_PARENTS_FOR_GO_NODES = new HashMap<>();
		
		if( !ALL_PARENTS_FOR_GO_NODES.containsKey( gnode ) )
			ALL_PARENTS_FOR_GO_NODES.put( gnode, getAllParentNodesFromDatabase( gnode ) );
		
		return ALL_PARENTS_FOR_GO_NODES.get( gnode );
	}
	
	/**
	 * Get all parents of this go node (i.e., get more general terms connected directly and indirectly to this term)
	 * from the database
	 * 
	 * @param gnode
	 * @return
	 * @throws Exception
	 */
	public static Collection<GONode> getAllParentNodesFromDatabase( GONode gnode ) throws Exception {
		Collection<GONode> nodes = new HashSet<GONode>();
		
		if( gnode.getId() == GOUtils.UNKNOWN_COMPONENT_ID || gnode.getId() == GOUtils.UNKNOWN_FUNCTION_ID || gnode.getId() == GOUtils.UNKNOWN_PROCESS_ID ) {
			nodes.add( GOUtils.getAspectRootNode( gnode.getTermType() ) );
			return nodes;
		}
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.GO_DB );
			
			// Our SQL statement, relationship_type = 1 means that it is a "is_a" relationship
			String sqlStr =  "SELECT term1_id FROM graph_path WHERE term2_id = ? AND relationship_type_id = 1 AND term1_id <> term2_id";
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt( 1, gnode.getId() );

			// Our results
			rs = stmt.executeQuery();
		
			while( rs.next() ) {
				nodes.add( GONodeFactory.getInstance().getGONode( rs.getInt( 1 ) ) );
			}			
			
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
		
		nodes.remove( GOUtils.getAllNode() );
		return nodes;
	}

	/**
	 * Get direct children of this go node (i.e., get more specific terms connected directly to this term)
	 * @param gnode
	 * @return
	 * @throws Exception
	 */
	public static Collection<GONode> getDirectChildNodes( GONode gnode ) throws Exception {
		Collection<GONode> nodes = new HashSet<GONode>();
		
		// if this is an aspect root node, add the unknown node as a child
		if( !(gnode.getAcc().equals( "all" )) && gnode.equals( GOUtils.getAspectRootNode( gnode.getTermType() ) ) ) {
			nodes.add( GOUtils.getUnknownNodeForAspect( gnode.getTermType() ) );
		}
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.GO_DB );
			
			// Our SQL statement, relationship_type = 1 means that it is a "is_a" relationship
			String sqlStr =  "SELECT term2_id FROM term2term WHERE term1_id = ? AND relationship_type_id = 1 AND term1_id <> term2_id";
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt( 1, gnode.getId() );

			// Our results
			rs = stmt.executeQuery();
		
			while( rs.next() ) {
				nodes.add( GONodeFactory.getInstance().getGONode( rs.getInt( 1 ) ) );
			}			
			
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
		
		return nodes;
	}
	
	/**
	 * Get all children of this go node (i.e., get more specific terms connected directly and indirectly to this term)
	 * @param gnode
	 * @return
	 * @throws Exception
	 */
	public static Collection<GONode> getAllChildNodes( GONode gnode ) throws Exception {
		Collection<GONode> nodes = new HashSet<GONode>();
		
		// if this is an aspect root node, add the unknown node as a child
		if( gnode.equals( GOUtils.getAspectRootNode( gnode.getTermType() ) ) ) {
			nodes.add( GOUtils.getUnknownNodeForAspect( gnode.getTermType() ) );
		}
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.GO_DB );
			
			// Our SQL statement, relationship_type = 1 means that it is a "is_a" relationship
			String sqlStr =  "SELECT term2_id FROM graph_path WHERE term1_id = ? AND relationship_type_id = 1 AND term1_id <> term2_id";
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt( 1, gnode.getId() );

			// Our results
			rs = stmt.executeQuery();
		
			while( rs.next() ) {
				
				GONode tnode = GONodeFactory.getInstance().getGONode( rs.getInt( 1 ) );
				nodes.add( tnode );
				
				// if this is an aspect root node, add the unknown node as a child
				if( tnode.equals( GOUtils.getAspectRootNode( tnode.getTermType() ) ) ) {
					nodes.add( GOUtils.getUnknownNodeForAspect( tnode.getTermType() ) );
				}
				
				
				nodes.add( GONodeFactory.getInstance().getGONode( rs.getInt( 1 ) ) );
			}			
			
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
		
		return nodes;
	}
	
	
}
