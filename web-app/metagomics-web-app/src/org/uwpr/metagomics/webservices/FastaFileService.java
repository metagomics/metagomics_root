package org.uwpr.metagomics.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.uwpr.metagomics.dao.FastaFileDAO;
import org.uwpr.metagomics.dao.RunDAO;
import org.uwpr.metagomics.dao.UploadedFastaFileDAO;
import org.uwpr.metagomics.dto.FastaFileDTO;
import org.uwpr.metagomics.dto.RunDTO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.webconstants.WebAppConstants;

/**
 * Get the referenced uploaded fasta file
 * 
 * @author mriffle
 *
 */

@Path("/fastaFile")
public class FastaFileService {

	private static final Logger log = Logger.getLogger(FastaFileService.class);
	
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("/getData")
		public WWWUploadedFastaFileDTO getData ( 
				@QueryParam("unique_id") String uniqueId,
				@Context HttpServletRequest request ) throws Exception {

			
			try {
				
				
				UploadedFastaFileDTO upload = null;
				
				try {
					upload = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( uniqueId );
				} catch( Exception e ) {
					
	
					throw new WebApplicationException(
							Response.status( 500 )  //  Send HTTP code
							.entity( "Error loading fasta upload " + uniqueId ) // This string will be passed to the client
							.build()
							);
				}
				
				
				if( upload == null ) {
					throw new WebApplicationException(
							Response.status( 500 )  //  Send HTTP code
							.entity( "Invalid fasta upload id: " + uniqueId ) // This string will be passed to the client
							.build()
							);
				}
				
	
				WWWUploadedFastaFileDTO wwwUpload = new WWWUploadedFastaFileDTO();
				
				wwwUpload.setBlastCutoff( upload.getBlastCutoff() );
				wwwUpload.setUniqueId( upload.getUniqueId() );
				wwwUpload.setUseTopHit( upload.getUseTopHit() );
				wwwUpload.setAnnotationDatabaseName( WebAppConstants.BLAST_DATABASE_DISPLAY_NAMES.get( upload.getAnnotationDatabaseId() ) );
				wwwUpload.setFastaFile( FastaFileDAO.getInstance().getFastaFile( upload.getFastaFileId() ) );
				wwwUpload.setNickname( upload.getNickname() );
				wwwUpload.setEmail( upload.getEmailAddress() );
				return wwwUpload;				
				
				
			} catch ( WebApplicationException e ) {

				log.error( "Got an error: ", e );

				throw e;
				
			} catch ( Exception e ) {
				
				String msg = "Exception caught: " + e.toString();
				
				log.error( msg, e );
				
				throw new WebApplicationException(
						Response.status( 500 )  //  Send HTTP code
						.entity( "Got an exception: " + e.getMessage() ) // This string will be passed to the client
						.build()
						);
			}			
				
		}
		
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("/getRuns")
		public List<WWWRunDTO> getRuns ( 
				@QueryParam("unique_id") String uniqueId,
				@Context HttpServletRequest request ) throws Exception {

			
			try {
				
				List<WWWRunDTO> wwwRuns = new ArrayList<>();
				
				UploadedFastaFileDTO upload = null;
				
				try {
					upload = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( uniqueId );
				} catch( Exception e ) {
					
	
					throw new WebApplicationException(
							Response.status( 500 )  //  Send HTTP code
							.entity( "Error loading fasta upload " + uniqueId ) // This string will be passed to the client
							.build()
							);
				}
				
				
				if( upload == null ) {
					throw new WebApplicationException(
							Response.status( 500 )  //  Send HTTP code
							.entity( "Invalid fasta upload id: " + uniqueId ) // This string will be passed to the client
							.build()
							);
				}
				
	
				List<RunDTO> runs = RunDAO.getInstance().getRunsForFastaUpload( upload.getId() );
				DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
				for( RunDTO run : runs ) {
					
					WWWRunDTO wwwRun = new WWWRunDTO();
					
					wwwRun.setFilename( run.getFilename() );
					wwwRun.setNickname( run.getNickname() );
					wwwRun.setProcessed( run.isProcessed() ) ;
					wwwRun.setUploadDate( fmt.print( run.getUploadDate() ) );
					wwwRun.setId( run.getId() );
					
					wwwRuns.add( wwwRun );					
				}
				
				return wwwRuns;
				
				
			} catch ( WebApplicationException e ) {

				log.error( "Got an error: ", e );

				throw e;
				
			} catch ( Exception e ) {
				
				String msg = "Exception caught: " + e.toString();
				
				log.error( msg, e );
				
				throw new WebApplicationException(
						Response.status( 500 )  //  Send HTTP code
						.entity( "Got an exception: " + e.getMessage() ) // This string will be passed to the client
						.build()
						);
			}
				
		}
		
		private class WWWRunDTO {
			
			private String nickname;
			private String filename;
			private boolean isProcessed;
			private String uploadDate;
			int id;
			
			public String getNickname() {
				return nickname;
			}
			public void setNickname(String nickname) {
				this.nickname = nickname;
			}
			public String getFilename() {
				return filename;
			}
			public void setFilename(String filename) {
				this.filename = filename;
			}
			public boolean isProcessed() {
				return isProcessed;
			}
			public void setProcessed(boolean isProcessed) {
				this.isProcessed = isProcessed;
			}
			public String getUploadDate() {
				return uploadDate;
			}
			public void setUploadDate(String uploadDate) {
				this.uploadDate = uploadDate;
			}
			public int getId() {
				return id;
			}
			public void setId(int id) {
				this.id = id;
			}
			

		}
		
		private class WWWUploadedFastaFileDTO {
			
			private String uniqueId;
			private String annotationDatabaseName;
			private String blastCutoff;
			private boolean useTopHit;
			private FastaFileDTO fastaFile;
			private String nickname;
			private String email;
			
			public String getUniqueId() {
				return uniqueId;
			}
			public void setUniqueId(String uniqueId) {
				this.uniqueId = uniqueId;
			}
			public String getAnnotationDatabaseName() {
				return annotationDatabaseName;
			}
			public void setAnnotationDatabaseName(String annotationDatabaseName) {
				this.annotationDatabaseName = annotationDatabaseName;
			}
			public String getBlastCutoff() {
				return blastCutoff;
			}
			public void setBlastCutoff(String blastCutoff) {
				this.blastCutoff = blastCutoff;
			}
			public boolean isUseTopHit() {
				return useTopHit;
			}
			public void setUseTopHit(boolean useTopHit) {
				this.useTopHit = useTopHit;
			}
			public FastaFileDTO getFastaFile() {
				return fastaFile;
			}
			public void setFastaFile(FastaFileDTO fastaFile) {
				this.fastaFile = fastaFile;
			}
			public String getNickname() {
				return nickname;
			}
			public void setNickname(String nickname) {
				this.nickname = nickname;
			}
			public String getEmail() {
				return email;
			}
			public void setEmail(String email) {
				this.email = email;
			}
		}	
}


