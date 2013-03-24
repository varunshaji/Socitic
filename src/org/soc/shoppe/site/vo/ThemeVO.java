package org.soc.shoppe.site.vo;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import com.google.appengine.api.blobstore.BlobKey;

public class ThemeVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String themeName;
	
	private String type;
	
	private boolean social_con;
	
	private String description;
	
	private BlobKey blobKey1;
	
	private BlobKey blobKey2;
	
	private String imageUrl;
	
	private MultipartFile file1;
	
	private MultipartFile file2;

	private long themeId; //for vosao access
	
	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public String getType() {
		return type;
	}

	public boolean isSocial_con() {
		return social_con;
	}

	public String getDescription() {
		return description;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSocial_con(boolean social_con) {
		this.social_con = social_con;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getFile1() {
		return file1;
	}

	public void setFile1(MultipartFile file1) {
		this.file1 = file1;
	}

	public long getThemeId() {
		return themeId;
	}

	public void setThemeId(long themeId) {
		this.themeId = themeId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public MultipartFile getFile2() {
		return file2;
	}

	public void setFile2(MultipartFile file2) {
		this.file2 = file2;
	}

	public BlobKey getBlobKey1() {
		return blobKey1;
	}

	public BlobKey getBlobKey2() {
		return blobKey2;
	}

	public void setBlobKey1(BlobKey blobKey1) {
		this.blobKey1 = blobKey1;
	}

	public void setBlobKey2(BlobKey blobKey2) {
		this.blobKey2 = blobKey2;
	}
}
