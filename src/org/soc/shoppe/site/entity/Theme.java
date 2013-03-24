package org.soc.shoppe.site.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import org.soc.shoppe.site.vo.ThemeVO;
import com.google.appengine.api.blobstore.BlobKey;

@Entity
public class Theme {
	
	@Id
	private String themename;
	
	private String type;
	
	private boolean social_con;
	
	private String description;
	
	private BlobKey blobKey1;
	
	private BlobKey blobKey2;
	
	private String imageurl;

	public String getThemename() {
		return themename;
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
	
	public void setThemename(String themename) {
		this.themename = themename;
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
	
	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public Theme(ThemeVO themeVO) {
		this.themename = themeVO.getThemeName();
		this.type = themeVO.getType();
		this.social_con = themeVO.isSocial_con();
		this.description = themeVO.getDescription();
		this.imageurl = themeVO.getImageUrl();
		this.blobKey1 = themeVO.getBlobKey1();
		this.blobKey2 = themeVO.getBlobKey2();
	}
	
	public ThemeVO getThemeVO() {
		ThemeVO themeVO = new ThemeVO();
		themeVO.setThemeName(this.themename);
		themeVO.setType(this.type);
		themeVO.setSocial_con(this.social_con);
		themeVO.setDescription(this.description);
		themeVO.setImageUrl(this.imageurl);
		themeVO.setBlobKey1(this.blobKey1);
		themeVO.setBlobKey2(this.blobKey2);
		return themeVO;
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
