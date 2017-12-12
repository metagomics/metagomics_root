package org.uwpr.metagomics.go_counter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;

import org.uwpr.metagomics.database.DBConnectionManager;

public class ProteinSearcher {

	public static ProteinSearcher getInstance() { return new ProteinSearcher(); }
	
	/**
	 * Get all protein ids to which the supplied peptide was matched for the given fasta file.
	 * 
	 * @param peptideId
	 * @param fastaFileId
	 * @return
	 * @throws Exception
	 */
	public Collection<Integer> getProteinsForPeptide( int peptideId, int fastaFileId ) throws Exception {
	
		
		Collection<Integer> proteinIds = ProteinSearcherCache.getInstance().getProteinIdsFromCache( fastaFileId, peptideId );
		if( proteinIds != null )
			return proteinIds;
		
		proteinIds = new HashSet<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT protein_sequence_id FROM peptide_protein_fasta_map WHERE fasta_file_id = ? AND peptide_id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaFileId );
			pstmt.setInt( 2, peptideId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				proteinIds.add( rs.getInt( 1 ) );
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
		
		ProteinSearcherCache.getInstance().addToCache( fastaFileId, peptideId, proteinIds );
		
		return proteinIds;		
	}
	
}
