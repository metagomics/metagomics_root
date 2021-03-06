package org.uwpr.metagomics.email;

public class SendEmailDTO {

	private String toEmailAddress;
	private String fromEmailAddress;
	private String emailSubject;
	private String emailBody;
	
	
	public String getToEmailAddress() {
		return toEmailAddress;
	}
	public void setToEmailAddress(String toEmailAddress) {
		this.toEmailAddress = toEmailAddress;
	}
	public String getFromEmailAddress() {
		return fromEmailAddress;
	}
	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}
	public String getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public String getEmailBody() {
		return emailBody;
	}
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}
	
	
	@Override
	public String toString() {
		
		String src = "To: " + this.toEmailAddress + "\n";
		src += "From: " + this.fromEmailAddress + "\n";
		src += "Subject: " + this.emailSubject + "\n";
		src += "Message: " + this.emailBody + "\n";

		return src;
	}
	
}
