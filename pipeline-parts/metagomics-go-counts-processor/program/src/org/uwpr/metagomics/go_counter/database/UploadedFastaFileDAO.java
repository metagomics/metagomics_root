package org.uwpr.metagomics.go_counter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.uwpr.metagomics.database.DBConnectionManager;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;

public class UploadedFastaFileDAO {

	public static UploadedFastaFileDAO getInstance() { return new UploadedFastaFileDAO(); }
	
	private UploadedFastaFileDAO() { }
	
	
	public UploadedFastaFileDTO getUploadedFastaFile( String uniqueId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM fasta_upload WHERE unique_id = ?";
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, uniqueId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				UploadedFastaFileDTO fastaUpload = new UploadedFastaFileDTO();
				
				fastaUpload.setAnnotationDatabaseId( rs.getInt( "blast_database_id" ) );
				fastaUpload.setBlastCutoff( rs.getString( "blast_cutoff" ) );
				fastaUpload.setEmailAddress( rs.getString( "email_address" ) );
				fastaUpload.setFastaFileId( rs.getInt( "fasta_file_id" ) );
				fastaUpload.setId( rs.getInt( "id" ) );
				fastaUpload.setNickname( rs.getString( "nickname" ) );
				fastaUpload.setUniqueId( rs.getString( "unique_id" ) );

				if( rs.getString( "blast_only_top_hit" ).equals( "T" ) )
					fastaUpload.setUseTopHit( true );
				else
					fastaUpload.setUseTopHit( false );
				
				return fastaUpload;				
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
		
		return null;
	}
	
	/**
	 * Get the uploaded fasta file for the given upload id.
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public UploadedFastaFileDTO getUploadedFastaFile( int id ) throws Exception {
	
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM fasta_upload WHERE id = ?";
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				UploadedFastaFileDTO fastaUpload = new UploadedFastaFileDTO();
				
				fastaUpload.setAnnotationDatabaseId( rs.getInt( "blast_database_id" ) );
				fastaUpload.setBlastCutoff( rs.getString( "blast_cutoff" ) );
				fastaUpload.setEmailAddress( rs.getString( "email_address" ) );
				fastaUpload.setFastaFileId( rs.getInt( "fasta_file_id" ) );
				fastaUpload.setId( id );
				fastaUpload.setNickname( rs.getString( "nickname" ) );
				fastaUpload.setUniqueId( rs.getString( "unique_id" ) );

				if( rs.getString( "blast_only_top_hit" ).equals( "T" ) )
					fastaUpload.setUseTopHit( true );
				else
					fastaUpload.setUseTopHit( false );
				
				return fastaUpload;				
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
		
		return null;
	}
	
	/**
	 * Get all uploaded fastas that use the given fasta file id.
	 * 
	 * @param fastaFileId
	 * @return
	 * @throws Exception
	 */
	public Collection<UploadedFastaFileDTO> getUploadedFastaFilesForFastaFileId( int fastaFileId ) throws Exception {
		
		Collection<UploadedFastaFileDTO> uploads = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM fasta_upload WHERE fasta_file_id = ?";
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaFileId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				UploadedFastaFileDTO upload = getUploadedFastaFile( rs.getInt( 1 ) );
				if( upload != null )
					uploads.add( upload );
			
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
		
		return uploads;
	}

	
	/**
	 * Save the supplied UploadedFastaFileDTO to the database as a NEW entry. The id
	 * property of the supplied UploadedFastaFileDTO will be updated with the assigned id
	 * from the database.
	 * 
	 * @param uploadedFastaFile
	 * @throws Exception
	 */
	public void save( UploadedFastaFileDTO uploadedFastaFile ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO fasta_upload (unique_id, fasta_file_id, nickname, blast_database_id, ";
		sql += "blast_cutoff, blast_only_top_hit, email_address) VALUES (?,?,?,?,?,?,?)";
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );

			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );

			pstmt.setString( 1, uploadedFastaFile.getUniqueId() );
			pstmt.setInt( 2, uploadedFastaFile.getFastaFileId() );
			pstmt.setString( 3, uploadedFastaFile.getNickname() );
			pstmt.setInt( 4, uploadedFastaFile.getAnnotationDatabaseId() );
			pstmt.setString( 5, uploadedFastaFile.getBlastCutoff() );
			
			if( uploadedFastaFile.getUseTopHit() == null || !uploadedFastaFile.getUseTopHit() ) {
				pstmt.setString( 6, "F" );

			} else {
				pstmt.setString( 6, "T" );

			}
			
			
			pstmt.setString( 7, uploadedFastaFile.getEmailAddress() );
			
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();			
			
			if( rs.next() ) {
				uploadedFastaFile.setId( rs.getInt( 1 ) );
			} else {
				String msg = "Failed to insert URLShortenerDTO, generated key not found.";
				throw new Exception( msg );
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
