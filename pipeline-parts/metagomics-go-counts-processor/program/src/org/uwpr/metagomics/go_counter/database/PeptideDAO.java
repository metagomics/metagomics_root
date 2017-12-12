package org.uwpr.metagomics.go_counter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.uwpr.metagomics.database.DBConnectionManager;

public class PeptideDAO {

	public static PeptideDAO getInstance() { return new PeptideDAO(); }
	
	
	/**
	 * Given a protein peptide, get its Id. If sequence is not in database, it
	 * will be inserted and the assigned Id returned.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public int getIdForPeptideSequence( String sequence ) throws Exception {

		Integer id = getIdForPeptideSequenceInDatabase( sequence );
		if( id != null ) {
			return id;
		}
		
		id = insertPeptideSequenceInDatabase( sequence );
		return id;
	}
	
	/**
	 * Get the Id for the given peptide sequence from the database. If not in the database,
	 * null is returned.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	private Integer getIdForPeptideSequenceInDatabase( String sequence ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM peptide WHERE peptide_sequence = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, sequence );
			
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
	 * Insert the given sequence into the database. Return assigned Id. Throws exception
	 * if the sequence is already in the database.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	private int insertPeptideSequenceInDatabase( String sequence ) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO peptide (peptide_sequence) VALUES (?)";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, sequence );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();			
			
			if( rs.next() ) {
				return rs.getInt( 1 );
			} else {
				throw new Exception( "Error inserting sequence." );
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
