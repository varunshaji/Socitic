package org.soc.shoppe.busdata.vo;

import java.io.Serializable;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;

public class BusinessVO implements Serializable{
	
//	@NotEmpty
//	private Key businessKey;
	


	@NotEmpty
	private String businessName;
	
	@NotEmpty
	private String businessCode;
	
	private String description;
	
	private String businessKey;
	
	private MultipartFile file;
	
	private BlobKey blobKey;
	
private BusinessVO businessVO;

public BusinessVO getBusinessVO() {
	return businessVO;
}

public void setBusinessVO(BusinessVO businessVO) {
	this.businessVO = businessVO;
}
	
	public String getbusinessCode(){
		return businessCode;
	}
	
	public void setbusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	
	public String getbusinessName(){
		return businessName;
	}
	
	public void setbusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getbusinessKey(){
		return businessKey;
	}
	
	public void setbusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}



	public BlobKey getBlobKey() {
		return blobKey;
	}

	public void setBlobKey(BlobKey blobKey) {
		this.blobKey = blobKey;
	}


	
}
