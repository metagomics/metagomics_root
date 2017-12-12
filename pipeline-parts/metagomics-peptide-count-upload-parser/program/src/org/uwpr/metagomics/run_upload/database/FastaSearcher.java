package org.uwpr.metagomics.run_upload.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.uwpr.metagomics.database.DBConnectionManager;

public class FastaSearcher {

	public static FastaSearcher getInstance() { return new FastaSearcher(); }
	
	/**
	 * Get the fasta file id associated with the given run
	 * 
	 * @param runId
	 * @return
	 * @throws Exception
	 */
	public Integer getFastaUploadIdForRun( int runId ) throws Exception {
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT fasta_upload_id FROM run WHERE id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, runId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				return rs.getInt( 1 );
			} else {
				return null;
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
		
	}
	
	/**
	 * Get the fasta file id associated with the given run
	 * 
	 * @param runId
	 * @return
	 * @throws Exception
	 */
	public Integer getFastaFileIdForRun( int runId ) throws Exception {
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT fasta_file.id FROM fasta_file INNER JOIN fasta_upload ON fasta_file.id = fasta_upload.fasta_file_id ";
			  sql += "INNER JOIN run ON fasta_upload.id = run.fasta_upload_id WHERE run.id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, runId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				return rs.getInt( 1 );
			} else {
				return null;
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
		
	}
	
}
