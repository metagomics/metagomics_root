package org.uwpr.metagomics.dto;

import org.joda.time.DateTime;

/**
 * A "run", which is a list of peptides and counts.
 * 
 * @author mriffle
 *
 */
public class RunDTO {



	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFastaUploadId() {
		return fastaUploadId;
	}
	public void setFastaUploadId(int fastaUploadId) {
		this.fastaUploadId = fastaUploadId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public boolean isProcessed() {
		return isProcessed;
	}
	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}
	public DateTime getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(DateTime uploadDate) {
		this.uploadDate = uploadDate;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getTotalPSMCount() {
		return totalPSMCount;
	}
	public void setTotalPSMCount(int totalPSMCount) {
		this.totalPSMCount = totalPSMCount;
	}


	private int id;
	private int fastaUploadId;
	private String nickname;
	private boolean isProcessed;
	private DateTime uploadDate;
	private String filename;
	private int totalPSMCount;
	
}
