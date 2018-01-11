package org.uwpr.metagomics.go_counter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.uwpr.metagomics.database.DBConnectionManager;

public class GOAssociationSaver {

	public static GOAssociationSaver getInstance() { return new GOAssociationSaver(); }
	
	public void saveGOToRunAssocation( int runId, String goAcc, long count, double ratio ) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT IGNORE INTO run_go_counts (run_id, go_acc, go_count, ratio) VALUES (?,?,?,?)";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, runId );
			pstmt.setString( 2, goAcc );
			pstmt.setLong( 3, count );
			pstmt.setDouble( 4, ratio );
			
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
	
	public void saveGOToPeptideAssocation( int fastaUploadId, int peptideId, String goAcc ) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT IGNORE INTO go_peptide_map (fasta_upload_id, peptide_id, go_acc) VALUES (?,?,?)";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaUploadId );
			pstmt.setInt( 2, peptideId );
			pstmt.setString( 3, goAcc );
			
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
	
}
