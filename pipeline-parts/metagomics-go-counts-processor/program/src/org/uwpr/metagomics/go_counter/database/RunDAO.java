package org.uwpr.metagomics.go_counter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.uwpr.metagomics.database.DBConnectionManager;
import org.uwpr.metagomics.dto.RunDTO;

public class RunDAO {

	public static RunDAO getInstance() { return new RunDAO(); }
	
	
	public List<RunDTO> getRunsForFastaUpload( int fastaUploadId ) throws Exception {
		
		List<RunDTO> runs = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM run WHERE fasta_upload_id = ? ORDER BY upload_date DESC";
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, fastaUploadId );
			
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {

				int runId = rs.getInt( 1 );
				RunDTO run = getRun( runId );
				runs.add( run );

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
		
		return runs;
		
	}
	
	/**
	 * Get the run with the given id.
	 * 
	 * @param runId
	 * @return
	 * @throws Exception
	 */
	public RunDTO getRun( int runId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM run WHERE id = ?";
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, runId );
			
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				RunDTO run = new RunDTO();
				
				run.setId( runId );
				run.setFastaUploadId( rs.getInt( "fasta_upload_id" ) );
				run.setFilename( rs.getString( "file_name" ) );
				run.setNickname( rs.getString( "nickname" ) );
				run.setUploadDate( new DateTime( rs.getDate( "upload_date" ) ) );
				
				if( rs.getString( "is_processed" ).equals( "T" ) )
					run.setProcessed( true );
				else
					run.setProcessed( false );
				
				return run;

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
	 * CREATE TABLE run (
		id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
		fasta_upload_id INT UNSIGNED NOT NULL,
		nickname VARCHAR(255),
		is_processed CHAR(1) DEFAULT 'F' NOT NULL,
		upload_date TIMESTAMP
	);

	 * @param run
	 * @throws Exception
	 */
	public void saveNewRun( RunDTO run ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO run (fasta_upload_id, nickname, file_name) VALUES (?,?,?)";
		
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );

			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );

			pstmt.setInt( 1, run.getFastaUploadId() );
			
			if( run.getNickname() != null )
				pstmt.setString( 2, run.getNickname() );
			else
				pstmt.setNull( 2, Types.VARCHAR );
			
			pstmt.setString( 3, run.getFilename() );
			
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();			
			
			if( rs.next() ) {
				run.setId( rs.getInt( 1 ) );
			} else {
				String msg = "Failed to insert run, generated key not found.";
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
	
	/**
	 * Mark the given run as complete.
	 * 
	 * @param runId
	 * @throws Exception
	 */
	public void markRunComplete( int runId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "UPDATE run SET is_processed = 'T' WHERE id = ?";
				
		try {

			conn = DBConnectionManager.getInstance().getConnection( DBConnectionManager.DB );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, runId );
			
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
