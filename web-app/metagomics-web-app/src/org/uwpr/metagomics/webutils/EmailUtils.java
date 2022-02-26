package org.uwpr.metagomics.webutils;

import org.apache.log4j.Logger;
import org.uwpr.metagomics.dao.FastaFileDAO;
import org.uwpr.metagomics.dao.RunDAO;
import org.uwpr.metagomics.dao.UploadedFastaFileDAO;
import org.uwpr.metagomics.dto.FastaFileDTO;
import org.uwpr.metagomics.dto.RunDTO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.webconstants.WebAppConstants;
import org.yeastrc.sendmail.SendMail;

public class EmailUtils {

	public static EmailUtils getInstance() { return new EmailUtils(); }

	private static final String EMAIL_FROM_ADDRESS = "do_not_reply@yeastrc.org";
	private static final String SUBJECT_FASTA_NOTIFICATION_SUCCESS = "Metagomics FASTA file upload complete";
	private static final String SUBJECT_RUN_READY_NOTIFICATION_SUCCESS = "Metagomics run upload complete";
	
	private static final Logger log = Logger.getLogger(EmailUtils.class);

	
	public void notifyOfReadyFastaUpload( UploadedFastaFileDTO uploadedFastaFile ) throws Exception {
		

		String message = getNotificationOfReadyFastaEmailBody();
		
		FastaFileDTO ff = FastaFileDAO.getInstance().getFastaFile( uploadedFastaFile.getFastaFileId() );
		
		message = message.replace( "#FASTA_FILENAME#",  ff.getFilename() );
		message = message.replace( "#UPLOAD_FASTA_FILE_URL#",  WebAppConstants.VIEW_UPLOADED_FASTA_FILE_URL + "?uid=" + uploadedFastaFile.getUniqueId() );

		SendMail sendMail = new SendMail();

		try {
			sendMail.send( uploadedFastaFile.getEmailAddress(), EMAIL_FROM_ADDRESS, SUBJECT_FASTA_NOTIFICATION_SUCCESS, message );

		} catch (Throwable e) {
			log.error( "Error sending email: " + e.getMessage() );
			System.out.println( "Error sending email: " + e.getMessage() );
		}
		
	}
	
	
	
	public void notifyOfReadyRun( int runId ) throws Exception {
		

		String message = getNotificationOfReadyRunEmailBody();
		
		RunDTO run = RunDAO.getInstance().getRun( runId );
		if( run == null )
			throw new Exception( "Could not get run for runId: " + runId );

		UploadedFastaFileDTO uploadedFastaFile = UploadedFastaFileDAO.getInstance().getUploadedFastaFile( run.getFastaUploadId() );
		if( uploadedFastaFile == null )
			throw new Exception( "Could not find uploaded fasta file id for run id: " + runId );
		
		message = message.replace( "#UPLOAD_FASTA_FILE_URL#",  WebAppConstants.VIEW_UPLOADED_FASTA_FILE_URL + "?uid=" + uploadedFastaFile.getUniqueId() );

		SendMail sendMail = new SendMail();

		try {
			sendMail.send( uploadedFastaFile.getEmailAddress(), EMAIL_FROM_ADDRESS, SUBJECT_RUN_READY_NOTIFICATION_SUCCESS, message );

		} catch (Throwable e) {
			log.error( "Error sending email: " + e.getMessage() );
			System.out.println( "Error sending email: " + e.getMessage() );
		}
		
	}
	
	
	private String getNotificationOfReadyFastaEmailBody() {
		String src = "Greetings,\n\n";

		src += "Your recent upload of #FASTA_FILENAME# to the metagomics server is\n";
		src += "now ready to use. You may now upload MS/MS results for metagomics\n";
		src += "analysis at the following URL:\n\n";

		src += "#UPLOAD_FASTA_FILE_URL#\n\n";

		src += "All of the analysis of this FASTA file using the given BLAST parameters\n";
		src += "must be performed at this URL. Please bookmark or otherwise save\n";
		src += "this URL for future reference.\n\n";

		src += "-- Metagomics Server\n";

		return src;
	}
	
	
	
	private String getNotificationOfReadyRunEmailBody() {
		String src = "Greetings,\n\n";

		src += "Your recent upload of peptide counts to the metagomics server is\n";
		src += "now ready to view at the following URL:\n\n";

		src += "#UPLOAD_FASTA_FILE_URL#\n\n";

		src += "-- Metagomics Server\n";

		return src;
	}
	
}
