package org.uwpr.metagomics.go_counter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;

import org.uwpr.metagomics.database.DBConnectionManager;

public class BlastHitSearcher {

	public static BlastHitSearcher getInstance() { return new BlastHitSearcher(); }
	
	public Collection<String> getBlastHitsForProteinInFastaUpload( int proteinId, int fastaFileUploadId ) throws Exception {
		
		Collection<String> uniprotAccs = new HashSet<>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT uniprot_acc FROM blast_result WHERE fasta_upload_id = ? AND protein_sequence_id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaFileUploadId );
			pstmt.setInt( 2, proteinId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				uniprotAccs.add( rs.getString( 1 ) );
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
		
		return uniprotAccs;
	}
	
}
