package org.uwpr.metagomics.webutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.uwpr.metagomics.db.DBConnectionFactory;

public class UniqueIdTester {

	private static final UniqueIdTester _INSTANCE = new UniqueIdTester();
	public static UniqueIdTester getInstance() { return _INSTANCE; }
	private UniqueIdTester() { }
	
	/**
	 * Check (in the database) that the given uniqueId is actually unique
	 * @param uniqueId
	 * @return
	 */
	public boolean isUnique(String uniqueId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM fasta_upload WHERE unique_id = ?";
				
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, uniqueId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				return false;
			}
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}

		return true;
	}
	
}
