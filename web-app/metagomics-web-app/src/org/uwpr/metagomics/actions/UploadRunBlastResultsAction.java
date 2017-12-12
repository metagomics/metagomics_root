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
import org.uwpr.metagomics.webconstants.WebAppConstants;
import org.uwpr.metagomics.webutils.JobCenterUtils;



/**
 * Reads the input stream from the "HttpServletRequest request" object
 *
 */
public class UploadRunBlastResultsAction extends Action {

	private static final Logger log = Logger.getLogger(UploadRunBlastResultsAction.class);

	public static final int COPY_FILE_ARRAY_SIZE = 32 * 1024; // 32 KB
	
	public static final String QUERY_PARAM_UPLOAD_ID = "upload-id";
	public static final String QUERY_PARAM_JOBCENTER_REQUEST_ID = "jobcenter-request-id";

	public ActionForward execute( ActionMapping mapping,
			  ActionForm aform,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		

//		int ContentLength = request.getContentLength();
//		String getCharacterEncoding = request.getCharacterEncoding();
//		String getContentType = request.getContentType();
//		String getMethod = request.getMethod();
//		String getPathInfo = request.getPathInfo();
//		String getScheme = request.getScheme();
//		String getServerName = request.getServerName();
//		String getServletPath = request.getServletPath();
		
		
		String uploadIdString = request.getParameter( QUERY_PARAM_UPLOAD_ID );
		String jobcenterRequestIdString = request.getParameter( QUERY_PARAM_JOBCENTER_REQUEST_ID );
		
		
		// handle copying of FASTA file
		File blastResultsFile = new File( WebAppConstants.UPLOAD_BLAST_RESULTS_DIRECTORY, uploadIdString + ".txt" );
		
		if ( log.isInfoEnabled() ) {
			log.info( "Where Blast Results will be uploaded to: " + blastResultsFile.getAbsolutePath() );
		}
		
		InputStream inputStream = null;
		OutputStream outStream = null;

		try {

			inputStream = request.getInputStream();
			outStream = new FileOutputStream(blastResultsFile);

			byte[] buf = new byte[ COPY_FILE_ARRAY_SIZE ];
			int len;

			while ((len = inputStream.read(buf)) > 0){
				outStream.write(buf, 0, len);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed writing request to file";
			log.error(msg, e);
			response.setStatus( 500 );

		} finally {

			try {
				if ( inputStream != null ) {

					inputStream.close();
				}

			} catch(Exception e){ }

			try {
				if ( outStream != null ) {

					outStream.close();
				}
			} catch(Exception e){ }
		}
		
		
		// submit jobcenter request to parse this file
		try {
			JobCenterUtils.getInstance().submitBlastResultsParsingJob( Integer.parseInt( uploadIdString ), Integer.parseInt( jobcenterRequestIdString ) );
		} catch( Exception e ) {
			
			String msg = "Failed to submit jobcenter job.";
			log.error(msg, e);
			response.setStatus( 500 );
			
		}
		
		
		return null;
	}

}
