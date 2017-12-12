package org.uwpr.metagomics.actions;

import java.io.File;
import java.io.FileInputStream;
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



/**
 * 
 *
 */
public class DownloadFastaForRunBlastAction extends Action {

	private static final Logger log = Logger.getLogger(DownloadFastaForRunBlastAction.class);

	public static final int COPY_FILE_ARRAY_SIZE = 32 * 1024; // 32 KB
	
	public static final String QUERY_PARAM_UPLOAD_ID = "upload-id";

	public ActionForward execute( ActionMapping mapping,
			  ActionForm aform,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		
		String uploadIdString = request.getParameter( QUERY_PARAM_UPLOAD_ID );
		
		
		// handle copying of FASTA file
		File fastaFile = new File( WebAppConstants.DOWNLOAD_FASTA_TO_RUN_BLAST_DIRECTORY, uploadIdString + ".fasta" );
	
		if ( log.isInfoEnabled() ) {
			log.info( "FASTA file: " + fastaFile.getAbsolutePath() );
		}
		
		InputStream inputFileStream = null;
		OutputStream outStream = null;

		try {

			inputFileStream = new FileInputStream( fastaFile );
			outStream = response.getOutputStream();

			byte[] buf = new byte[ COPY_FILE_ARRAY_SIZE ];
			int len;

			while ((len = inputFileStream.read(buf)) > 0){
				outStream.write(buf, 0, len);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed writing file to response";
			log.error(msg, e);
			response.setStatus( 500 );

		} finally {

			try {
				if ( inputFileStream != null ) {

					inputFileStream.close();
				}

			} catch(Exception e){ }

			try {
				if ( outStream != null ) {

					outStream.close();
				}
			} catch(Exception e){ }
		}
				
		
		return null;
	}

}
