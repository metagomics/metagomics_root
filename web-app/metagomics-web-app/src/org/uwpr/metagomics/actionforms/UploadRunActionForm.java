package org.uwpr.metagomics.actionforms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class UploadRunActionForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8263878243194493180L;
	private String nickname;
	private FormFile dataFile;
	private String uniqueId;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public FormFile getDataFile() {
		return dataFile;
	}
	public void setDataFile(FormFile dataFile) {
		this.dataFile = dataFile;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}



}
