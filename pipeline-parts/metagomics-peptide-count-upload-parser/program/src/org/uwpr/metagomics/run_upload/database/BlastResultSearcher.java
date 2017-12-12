package org.uwpr.metagomics.run_upload.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.uwpr.metagomics.database.DBConnectionManager;
import org.uwpr.metagomics.run_upload.dto.ProteinSequenceDTO;

public class BlastResultSearcher {

	public static BlastResultSearcher getInstance() { return new BlastResultSearcher(); }
	
	/**
	 * Return true if the given protein has any blast results for the given fasta upload id
	 * @param protein
	 * @param fastaFileId
	 * @return
	 * @throws Exception
	 */
	public boolean proteinHasBlastResults( ProteinSequenceDTO protein, int fastaUploadId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM blast_result WHERE fasta_upload_id = ? AND protein_sequence_id = ? LIMIT 1";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaUploadId );
			pstmt.setInt( 2, protein.getProteinSequenceId() );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				return true;
				
			} else {
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
		
	}
	
}
