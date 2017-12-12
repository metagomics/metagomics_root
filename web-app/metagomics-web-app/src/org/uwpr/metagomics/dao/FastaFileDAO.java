package org.uwpr.metagomics.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.uwpr.metagomics.db.DBConnectionFactory;
import org.uwpr.metagomics.dto.FastaFileDTO;

public class FastaFileDAO {

	public static FastaFileDAO getInstance() { return new FastaFileDAO(); }
	
	public void markComplete( int fastaFileId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "UPDATE fasta_file SET is_processed = 'T' WHERE id = ?";
				
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaFileId );
			
			pstmt.executeUpdate();

		} finally {
			
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
	
	public FastaFileDTO getFastaFile( int fastaFileId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT filename, sha1sum, is_processed FROM fasta_file WHERE id = ?";
				
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaFileId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				FastaFileDTO ff = new FastaFileDTO();
				
				ff.setFilename( rs.getString( 1 ) );
				ff.setSha1sum( rs.getString( 2 ) );
				
				if( rs.getString( 3 ).equals( "T" ) )
					ff.setProcessed( true );
				else
					ff.setProcessed( false );
				
				ff.setId( fastaFileId );
				
				return ff;
				
				
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
	 * Get the id for the given fasta file entry. If it is not in the database, insert
	 * it and return the generated id.
	 * 
	 * @param filename
	 * @param sha1sum
	 * @return
	 * @throws Exception
	 */
	public int getIdForFastaFileEntry( String filename, String sha1sum ) throws Exception {

		Integer id = getIdForFastaFileEntryInDatabase( filename, sha1sum );
		if( id != null ) {
			return id;
		}
		
		id = insertFastaFileEntryInDatabase( filename, sha1sum );
		return id;
	}
	
	/**
	 * Get the id for the given fasta file entry from the database. If it does not
	 * exist, return null.
	 * 
	 * @param filename
	 * @param sha1sum
	 * @return
	 * @throws Exception
	 */
	private Integer getIdForFastaFileEntryInDatabase( String filename, String sha1sum ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM fasta_file WHERE filename = ? AND sha1sum = ?";
				
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, filename );
			pstmt.setString( 2, sha1sum );
			
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
	 * Insert fasta file entry into database, return generated id.
	 * 
	 * @param filename
	 * @param sha1sum
	 * @return
	 * @throws Exception
	 */
	private int insertFastaFileEntryInDatabase( String filename, String sha1sum ) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO fasta_file (filename, sha1sum) VALUES (?,?)";
				
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.DB );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, filename );
			pstmt.setString( 2, sha1sum );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();			
			
			if( rs.next() ) {
				return rs.getInt( 1 );
			} else {
				throw new Exception( "Error inserting fasta file entry." );
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
	 * Return true if the given fasta file has been processed. False if not (or if
	 * it's not in the database.)
	 * 
	 * @param fastaFileId
	 * @return
	 * @throws Exception
	 */
	public boolean isProcessed( int fastaFileId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT is_processed FROM fasta_file WHERE id = ?";
				
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaFileId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				String isProcessed = rs.getString( 1 );
				
				if( isProcessed.equals( "T" ) )
					return true;
				else
					return false;
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
