package org.uwpr.metagomics.actions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uwpr.metagomics.dao.UploadedFastaFileDAO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.webutils.DownloadDataUtils;

public class DownloadDataTwoRunsAction extends Action {

	private static final Logger log = Logger.getLogger(DownloadDataSingleRunAction.class);

	public static final int COPY_FILE_ARRAY_SIZE = 32 * 1024; // 32 KB
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm aform,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		String type = request.getParameter( "type" );
		String uniqueId = request.getParameter( "uniqueId" );
		int run1Id = Integer.parseInt( request.getParameter( "run1Id" ) );
		int run2Id = Integer.parseInt( request.getParameter( "run2Id" ) );
		String format = request.getParameter( "format" );
		String aspect = request.getParameter( "aspect" );
		
		
		
		// sanity check, make sure this unique id is real
		UploadedFastaFileDTO upload = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( uniqueId );
		if( upload == null ) {
			log.error( "Invalid uniqueId: " + uniqueId );
			return null;
		}
		
		
		if( type.equals( "report" ) ) {
			
			File file = DownloadDataUtils.getReportFileTwoRuns( uniqueId, run1Id, run2Id );
			if( file == null || !file.exists() ) {
				log.error( "Attempted to download non-existent file: " + file.getAbsolutePath() );
				return null;
			}
			
			
			
			response.setContentType( "text/plain" );
			response.setHeader("Content-Disposition", "attachment; filename=" + file.getName() );
			response.setContentLength( (int)file.length() );

			BufferedOutputStream bos = null;
			FileInputStream fis = null;
			
			try {


				ServletOutputStream out = response.getOutputStream();
				bos = new BufferedOutputStream(out);

				fis = new FileInputStream( file );
				
				byte[] buf = new byte[ COPY_FILE_ARRAY_SIZE ];
				int len;

				while ((len = fis.read(buf)) > 0){
					out.write(buf, 0, len);
				}
				
				
			} finally {
				
				try {
					if( fis != null ) {
						fis.close();
						fis = null;
					}
				} catch ( Exception e ) {
					log.error( "fis.close():Exception " + e.toString(), e );
				}
				
				try {
					if ( bos != null ) {
						bos.close();
					}

				} catch ( Exception ex ) {

					log.error( "bos.close():Exception " + ex.toString(), ex );
				}

				try {
					response.flushBuffer();
				} catch ( Exception ex ) {

					log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
				}

			}
			
		} else if( type.equals( "image" ) ) {
			
			
			File file = DownloadDataUtils.getImageFileTwoRuns( uniqueId, run1Id, run2Id, aspect, format );
			if( file == null || !file.exists() ) {
				log.error( "Attempted to download non-existent file: " + file.getAbsolutePath() );
			}
			
			
			if( type.equals( "png" ) ) {
				response.setContentType( "image/png" );
			} else if( type.equals( "svg" ) ) {
				response.setContentType( "image/svg+xml" );
			}
			response.setHeader("Content-Disposition", "attachment; filename=" + file.getName() );
			response.setContentLength( (int)file.length() );

			BufferedOutputStream bos = null;
			FileInputStream fis = null;
			
			try {


				ServletOutputStream out = response.getOutputStream();
				bos = new BufferedOutputStream(out);

				fis = new FileInputStream( file );
				
				byte[] buf = new byte[ COPY_FILE_ARRAY_SIZE ];
				int len;

				while ((len = fis.read(buf)) > 0){
					out.write(buf, 0, len);
				}
				
				
			} finally {
				
				try {
					if( fis != null ) {
						fis.close();
						fis = null;
					}
				} catch ( Exception e ) {
					log.error( "fis.close():Exception " + e.toString(), e );
				}
				
				try {
					if ( bos != null ) {
						bos.close();
					}

				} catch ( Exception ex ) {

					log.error( "bos.close():Exception " + ex.toString(), ex );
				}

				try {
					response.flushBuffer();
				} catch ( Exception ex ) {

					log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
				}

			}
			
		}

		
		return null;
	}
		
}
