package org.uwpr.metagomics.servlets_as_webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * NOT CURRENTLY USED since can do with a Struts Action.  See 
 * 
 * Servlet that accepts Blast Results and writes to file
 *
 */
public class UploadBlastResultsWebserviceServlet extends HttpServlet {

//	private static final long serialVersionUID = 1L;
//
//
//	private static final Logger log = Logger.getLogger(UploadBlastResultsWebserviceServlet.class);
//
//	public static final int COPY_FILE_ARRAY_SIZE = 32 * 1024; // 32 KB
//	
//	public static final String QUERY_PARAM_UPLOAD_ID = "upload-id";
//	public static final String QUERY_PARAM_JOBCENTER_REQUEST_ID = "jobcenter-request-id";
//
//
//
//	/* (non-Javadoc)
//	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
//	 */
//	@Override
//	protected void doPost(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//
//
////		int ContentLength = request.getContentLength();
////		String getCharacterEncoding = request.getCharacterEncoding();
////		String getContentType = request.getContentType();
////		String getMethod = request.getMethod();
////		String getPathInfo = request.getPathInfo();
////		String getScheme = request.getScheme();
////		String getServerName = request.getServerName();
////		String getServletPath = request.getServletPath();
//		
//		
//		String uploadIdString = request.getParameter( QUERY_PARAM_UPLOAD_ID );
//		String jobcenterRequestIdString = request.getParameter( QUERY_PARAM_JOBCENTER_REQUEST_ID );
//		
//		
//		
//		
//		// handle copying of FASTA file
////		File blastResultsFile = new File( WebAppConstants.DOWNLOAD_FASTA_TO_RUN_BLAST_DIRECTORY, uploadIdString + ".fasta" );
//		
//		//  TODO  TEMP hard coded file
//		
//		File blastResultsFile = new File( "/data/run_space/Metagomics_Mike_UWPR/Run_Blast/blastResults_from_Upload.xml" );
//		
//		
//		if ( log.isInfoEnabled() ) {
//			log.info( "Where Blast Results will be uploaded to: " + blastResultsFile.getAbsolutePath() );
//		}
//		
//		InputStream inputStream = null;
//		OutputStream outStream = null;
//
//		try {
//
//			inputStream = request.getInputStream();
//			outStream = new FileOutputStream(blastResultsFile);
//
//			byte[] buf = new byte[ COPY_FILE_ARRAY_SIZE ];
//			int len;
//
//			while ((len = inputStream.read(buf)) >= 0){
//				if ( len > 0 ) {
//					outStream.write(buf, 0, len);
//				}
//			}
//			
//		} catch ( Exception e ) {
//			
//			String msg = "Failed writing request to file";
//			log.error(msg, e);
//			response.setStatus( 500 );
//
//		} finally {
//
//			try {
//				if ( inputStream != null ) {
//
//					inputStream.close();
//				}
//
//			} catch(Exception e){ }
//
//			try {
//				if ( outStream != null ) {
//
//					outStream.close();
//				}
//			} catch(Exception e){ }
//		}
//				
//		
//		
//	}
}
