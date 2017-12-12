package org.uwpr.metagomics.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

public class UploadFastaActionForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -836875513334371132L;

	private static final Logger log = Logger.getLogger(UploadFastaActionForm.class);

	
	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();


		if ( fastaFile == null  ) {

			log.error( "FASTA file was null." );
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError( "FASTA file is required." ) );

		} else {

			String contentType = fastaFile.getContentType();

			if ( StringUtils.isEmpty( fastaFile.getFileName() ) ) {
				log.error( "No FASTA file name." );
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError( "FASTA file is required." ) );

			} else {

				int fileSize = fastaFile.getFileSize();


				if ( fileSize == 0 ) {
					log.error( "FASTA file was empty." );
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("errors.required", "FASTA file" ) );
				}
			}
		}

		if ( StringUtils.isEmpty( emailAddress ) ) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError( "Email address is required." ) );
		} else {
			EmailValidator validator = EmailValidator.getInstance();

			if ( !validator.isValid(emailAddress) ) {

				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("Email address looks to be invalid." ));
			}
		}
		
		
		if( cutoff == null || cutoff.length() < 1 ) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("Must enter a BLAST cutoff." ));
		}
		
		try {
			
			double d = Double.parseDouble( cutoff );
			
		} catch (Exception e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("Blast cutoff must be a number." ));
		}
		
		
		if( nickname == null || nickname.length() < 1 ) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError("Must enter a value for the nickname." ));
		}

		return errors;
	}
	
	
	
	private FormFile fastaFile;
	private String nickname;
	private int annotationDatabase = 1;
	private String cutoff = "1E-10";
	private Boolean useTopHit = true;
	private String emailAddress;
	
	
	public FormFile getFastaFile() {
		return fastaFile;
	}
	public void setFastaFile(FormFile fastaFile) {
		this.fastaFile = fastaFile;
		System.out.println( "filename (form): " + fastaFile.getFileName() );
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getAnnotationDatabase() {
		return annotationDatabase;
	}
	public void setAnnotationDatabase(int annotationDatabase) {
		this.annotationDatabase = annotationDatabase;
	}
	public String getCutoff() {
		return cutoff;
	}
	public void setCutoff(String cutoff) {
		this.cutoff = cutoff;
	}
	public Boolean getUseTopHit() {
		return useTopHit;
	}
	public void setUseTopHit(Boolean useTopHit) {
		this.useTopHit = useTopHit;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
