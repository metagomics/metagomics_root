package org.uwpr.metagomics.run_upload.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.uwpr.metagomics.database.DBConnectionManager;
import org.uwpr.metagomics.run_upload.dto.ProteinSequenceDTO;

public class ProteinSequenceDAO {

	public static ProteinSequenceDAO getInstance() { return new ProteinSequenceDAO(); }
	
	/**
	 * Get protein from the database.
	 * 
	 * @param proteinSequenceId
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceDTO getProteinSequenceDTO( int proteinSequenceId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT sequence FROM protein_sequence WHERE id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, proteinSequenceId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				ProteinSequenceDTO protein = new ProteinSequenceDTO();
				
				protein.setProteinSequenceId( proteinSequenceId );
				protein.setSequence( rs.getString( 1 ) );
				
				return protein;
				
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
