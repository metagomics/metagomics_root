package org.uwpr.metagomics.blast_parser.program;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.uwpr.metagomics.database.DBConnectionManager;

public class DatabaseUtils {

	public static DatabaseUtils getInstance() { return new DatabaseUtils(); }
	
	/**
	 * Save the given blast hit to the database.
	 * 
	 * @param fastaUploadId
	 * @param proteinSequenceId
	 * @param uniprotAcc
	 * @param evalue
	 * @throws Exception
	 */
	public void saveBlastHit( int fastaUploadId, int proteinSequenceId, String uniprotAcc, double evalue ) throws Exception {
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT IGNORE INTO blast_result (fasta_upload_id, protein_sequence_id, uniprot_acc, blast_score) VALUES (?,?,?,?)";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, fastaUploadId );
			pstmt.setInt( 2, proteinSequenceId );
			pstmt.setString( 3, uniprotAcc );
			pstmt.setDouble( 4, evalue );
			
			pstmt.executeUpdate();


		} finally {
			
			// be sure database handles are closed
			
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
	
	public Double getBlastCutoffForFastaUpload( int fastaUploadId ) throws Exception {
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT blast_cutoff FROM fasta_upload WHERE id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaUploadId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				return rs.getDouble( 1 );
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
	
	public Boolean getIsOnlyTopHit( int fastaUploadId ) throws Exception {
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT blast_only_top_hit FROM fasta_upload WHERE id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaUploadId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
			
				if( rs.getString( 1 ).equals( "T" ) )
					return true;
				
				return false;

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
