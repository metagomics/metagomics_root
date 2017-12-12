package org.uwpr.metagomics.actions;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uwpr.metagomics.dao.FastaFileDAO;
import org.uwpr.metagomics.dao.UploadedFastaFileDAO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.webconstants.WebAppConstants;
import org.uwpr.metagomics.webutils.EmailUtils;
import org.uwpr.metagomics.webutils.JobCenterUtils;
import org.uwpr.metagomics.webutils.Sha1SumCalculator;
import org.uwpr.metagomics.webutils.UniqueIDGenerator;

public class UploadFastaAction extends Action {

	private static final Logger log = Logger.getLogger(DownloadFastaForRunBlastAction.class);

	public static final int COPY_FILE_ARRAY_SIZE = 32 * 1024; // 32 KB

	public ActionForward execute( ActionMapping mapping,
			  ActionForm aform,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		// the fasta fileId
		int fastaFileId = 0;
		String uniqueId = null;
		
		UploadedFastaFileDTO uploadedFastaFile = new UploadedFastaFileDTO();	// what we're saving to the database
		uploadedFastaFile.setUseTopHit( false );
		
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		log.info( "DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD: " + DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD );

		if ( diskFileItemFactory.getRepository() == null ) {
			log.info( "diskFileItemFactory.getRepository() == null" );
		} else {
			log.info( "diskFileItemFactory.getRepository().getAbsolutePath(): '" 
					+ diskFileItemFactory.getRepository().getAbsolutePath() + "'" );
		}
		log.info( "diskFileItemFactory.getSizeThreshold(): '" 
				+ diskFileItemFactory.getSizeThreshold() + "'" );
		
		
		ServletFileUpload servletFileUpload = new ServletFileUpload( diskFileItemFactory );
		List<FileItem> fileItemListFromServletFileUpload = null;
		
		try {
			//  This will throw an exception if the send is aborted in the browser
			fileItemListFromServletFileUpload = servletFileUpload.parseRequest( request );
		} catch ( FileUploadException e ) {
			log.error( "FileUploadException parsing the request to get the parts in 'servletFileUpload.parseRequest( request )'", e );
			throw e;
		} catch ( Exception e ) {
			log.error( "Exception parsing the request to get the parts in 'servletFileUpload.parseRequest( request )'", e );
			throw e;
		}
		
		try {
			for ( FileItem fileItem : fileItemListFromServletFileUpload ) {
			    if ( fileItem.isFormField() ) {
	
			    	// check for form items
			    	
			    	if( fileItem.getFieldName().equals( "nickname" ) ) {
			    		uploadedFastaFile.setNickname( fileItem.getString() );
			    	} else if( fileItem.getFieldName().equals( "annotationDatabase" ) ) {
			    		uploadedFastaFile.setAnnotationDatabaseId( Integer.parseInt( fileItem.getString() ) );
			    	} else if (fileItem.getFieldName().equals( "cutoff" ) ) {
			    		uploadedFastaFile.setBlastCutoff( fileItem.getString() );
			    	} else if( fileItem.getFieldName().equals( "useTopHit" ) ) {
			    		uploadedFastaFile.setUseTopHit( true );
			    	} else if( fileItem.getFieldName().equals( "emailAddress") ) {
			    		uploadedFastaFile.setEmailAddress( fileItem.getString() );
			    	} else {
			    		log.error( "Got unexpected form element: " + fileItem.getFieldName() + ":" + fileItem.getString() );
			    		throw new Exception( "Got unexpected form element." );
			    	}		    	
			    	
			    } else {
			    	
			    	// parse the uploaded file
			    	
			    	if( !fileItem.getFieldName().equals( "fastaFile" ) ) {
			    		log.error( "Got invalid field name for fastaFile. Got: " + fileItem.getFieldName() );
			    		throw new Exception( "Got invalid field name for fastaFile." );
			    	}
			    	
			    	String filename = fileItem.getName();
			    	String sha1sum = Sha1SumCalculator.getInstance().getSHA1Sum( fileItem.getInputStream() );
			    	
			    	fastaFileId = FastaFileDAO.getInstance().getIdForFastaFileEntry( filename,  sha1sum );
			    	
					// the unique ID to be associated with this upload
					uniqueId = UniqueIDGenerator.getInstance().generateNewUniqueID();
			    	
					uploadedFastaFile.setUniqueId( uniqueId );
					uploadedFastaFile.setFastaFileId( fastaFileId );
					
					// if this FASTA file has already been uploaded, there is no need to process it again
					if( !FastaFileDAO.getInstance().isProcessed( fastaFileId ) ) {
						// handle copying of FASTA file
						File destinationFile = new File( WebAppConstants.UPLOAD_FASTA_TEMP_DIRECTORY, fastaFileId + ".fasta" );
				    	if( destinationFile.exists() ) {
				    		throw new Exception( "File " + destinationFile.getAbsolutePath() + " already exists." );
				    	}
				    	
						log.info( "Dest file: " + destinationFile.getAbsolutePath() );
		
				    	fileItem.write( destinationFile );
					}
			    }
			}
		} catch( Exception e ) {
			log.error( "Got error processing form.", e );
			throw e;
		}
		
		try {
			// save to database	
			UploadedFastaFileDAO.getInstance().save( uploadedFastaFile );
		} catch( Exception e ) {
			log.error( "Error saving DAO.", e );
			
		}
		
		
		if( FastaFileDAO.getInstance().isProcessed( fastaFileId ) ) {
			
			// If fasta is already processed, send email link to project
			EmailUtils.getInstance().notifyOfReadyFastaUpload( uploadedFastaFile );
			
			
		} else {
		
			// if not already processed, submit jobcenter job for processing fasta
			JobCenterUtils.getInstance().submitFastaParsingJob( fastaFileId );

		}
				
		
		// redirect to "project page"
		
		ActionForward newForward = new ActionForward();
		ActionForward forward = mapping.findForward( "Success" );

		newForward.setContextRelative( forward.getContextRelative() );
		newForward.setModule( forward.getModule() );
		newForward.setName( forward.getName() );
		newForward.setPath( forward.getPath() + "?uid=" + uniqueId );
		newForward.setRedirect( forward.getRedirect() );
		
		return newForward;
	}

}
