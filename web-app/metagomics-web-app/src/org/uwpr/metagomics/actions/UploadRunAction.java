package org.uwpr.metagomics.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uwpr.metagomics.actionforms.UploadRunActionForm;
import org.uwpr.metagomics.dao.RunDAO;
import org.uwpr.metagomics.dao.UploadedFastaFileDAO;
import org.uwpr.metagomics.dto.RunDTO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.webconstants.WebAppConstants;
import org.uwpr.metagomics.webutils.JobCenterUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UploadRunAction extends Action {

	private static final Logger log = Logger.getLogger(UploadRunAction.class);

	public static final int COPY_FILE_ARRAY_SIZE = 32 * 1024; // 32 KB

	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {
		
		
		
		UploadRunActionForm form = (UploadRunActionForm)actionForm;
		
		// no uploading data to demo project
		if( form.getUniqueId().equals( WebAppConstants.DEMO_PROJECT_HASH ) ) {
			return null;
		}
		
		UploadedFastaFileDTO upload = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( form.getUniqueId() );
		
		int fastaUploadId = upload.getId();
		String nickname = form.getNickname();
		String filename = form.getDataFile().getFileName();
		
		// save the run to the database
		RunDTO run = null;
		
		
		try {
			run = new RunDTO();
			run.setFastaUploadId( fastaUploadId );
			run.setFilename( filename );
			run.setNickname( nickname );
	
			RunDAO.getInstance().saveNewRun( run );

		} catch( Exception e ) {
		
			String msg = "Exception caught: " + e.toString();

			log.error( msg, e );
			e.printStackTrace();

			response.setStatus( 500 );
			response.setContentType( "text" );
			response.getWriter().print( "Error saving run to database." );
			
			return null; //  Early return
		}

		
		// copy file into position
		File destinationFile = new File( WebAppConstants.UPLOAD_RUN_TEMP_DIRECTORY, run.getId() + ".txt" );
		
		if ( log.isInfoEnabled() ) {
			log.info( "Dest file: " + destinationFile.getAbsolutePath() );
		}
		
		InputStream uploadFileStream = null;
		OutputStream outFileStream = null;

		try {

			uploadFileStream =  form.getDataFile().getInputStream();
			outFileStream = new FileOutputStream( destinationFile );

			byte[] buf = new byte[ COPY_FILE_ARRAY_SIZE ];
			int len;

			while ((len = uploadFileStream.read(buf)) > 0){
				outFileStream.write(buf, 0, len);
			}

		} catch( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();

			log.error( msg, e );


			response.setStatus( 500 );
			response.setContentType( "text" );
			response.getWriter().print( "Error copying data." );
			
			return null; //  Early return
			
			
		} finally {

			try {
				if ( uploadFileStream != null ) {

					uploadFileStream.close();
				}

			} catch(Exception e){ }

			try {
				if ( outFileStream != null ) {

					outFileStream.close();
				}
			} catch(Exception e){ }
		}
		
		
		// submit job to jobcenter for processing the ms results
		JobCenterUtils.getInstance().submitMSResultsParsingJob( run.getId() );
		
		
		
		
		Message servletResponse = new Message();
		servletResponse.setMessage( "success" );
		
		OutputStream responseOutputStream = response.getOutputStream();
		
		// send the JSON response 
		ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
		mapper.writeValue( responseOutputStream, servletResponse );
		
		responseOutputStream.flush();
		responseOutputStream.close();
		
		return null;
	}

	public class Message {
		
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	
	
}
