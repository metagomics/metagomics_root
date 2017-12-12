package org.uwpr.metagomics.fasta_importer.program;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import org.uwpr.metagomics.database.*;

public class DatabaseUtils {

	private static final DatabaseUtils _INSTANCE = new DatabaseUtils();
	public static DatabaseUtils getInstance() { return _INSTANCE; }
	private DatabaseUtils() { }
	

	
	
	/**
	 * Given a protein sequence, get its Id. If sequence is not in database, it
	 * will be inserted and the assigned Id returned.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public int getIdForProteinSequence( String sequence ) throws Exception {

		Integer id = getIdForProteinSequenceInDatabase( sequence );
		if( id != null ) {
			return id;
		}
		
		id = insertProteinSequenceInDatabase( sequence );
		return id;
	}
	
	/**
	 * Get the Id for the given protein sequence from the database. If not in the database,
	 * null is returned.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	private Integer getIdForProteinSequenceInDatabase( String sequence ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM protein_sequence WHERE sequence = ?";
				
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
	private int insertProteinSequenceInDatabase( String sequence ) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO protein_sequence (sequence) VALUES (?)";
				
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
	
	
	/**
	 * Insert a entry for a protein sequenec for a particular fasta file into the database.
	 * 
	 * @param fastaFileId
	 * @param proteinSequenceId
	 * @param name
	 * @param description
	 * @throws Exception
	 */
	public void insertFastaProtein( int fastaFileId, int proteinSequenceId, String name, String description ) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT INTO fasta_file_protein_sequence (protein_sequence_id, fasta_file_id, name, description) VALUES (?,?,?,?)";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, proteinSequenceId );
			pstmt.setInt( 2, fastaFileId );
			pstmt.setString( 3, name );
			
			if( description != null )
				pstmt.setString( 4, description );
			else
				pstmt.setNull( 4, Types.VARCHAR );
			
			
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
