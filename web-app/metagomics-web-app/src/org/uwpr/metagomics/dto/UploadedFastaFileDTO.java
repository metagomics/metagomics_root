package org.uwpr.metagomics.dto;

public class UploadedFastaFileDTO {
	
	private String uniqueId;
	private int id;
	private int annotationDatabaseId;
	private String blastCutoff;
	private Boolean useTopHit;
	private String emailAddress;
	private int fastaFileId;
	private String nickname;
	
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAnnotationDatabaseId() {
		return annotationDatabaseId;
	}
	public void setAnnotationDatabaseId(int annotationDatabaseId) {
		this.annotationDatabaseId = annotationDatabaseId;
	}
	public String getBlastCutoff() {
		return blastCutoff;
	}
	public void setBlastCutoff(String blastCutoff) {
		this.blastCutoff = blastCutoff;
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
	public int getFastaFileId() {
		return fastaFileId;
	}
	public void setFastaFileId(int fastaFileId) {
		this.fastaFileId = fastaFileId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	


	
}
