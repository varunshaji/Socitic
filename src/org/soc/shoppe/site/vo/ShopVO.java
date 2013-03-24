package org.soc.shoppe.site.vo;

import java.io.Serializable;
import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.soc.shoppe.account.vo.AccountVO;

import com.google.appengine.api.datastore.Text;

public class ShopVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
	@NotEmpty
	private String shopId;
	
	@NotEmpty
	private String themename;
	
	@NotEmpty
	private String name;
	
	@NotEmpty
	private String businesscode;
	
	private String location;
	
	private String latitude;
	  
	private String longitude;
	
	private String description;
	
    private String phone;
	
	private String mobile;
	
	private String email;
	
	private String address;
	
	private String businesskey;
	
	private List<String> users;

	private List<DealVO> deals;
	
	private List<AccountVO> siteAdmins;

	/**
	 * social connection variables
	 */
	private String facebookId;
	
	private String twitterId;
	
	private String gPlusId;
	
	private String flickrId;
	
	private String flickrPhotosetId;
	
	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	public String getgPlusId() {
		return gPlusId;
	}

	public void setgPlusId(String gPlusId) {
		this.gPlusId = gPlusId;
	}

	public String getFlickrId() {
		return flickrId;
	}

	public void setFlickrId(String flickrId) {
		this.flickrId = flickrId;
	}

	public String getFlickrPhotosetId() {
		return flickrPhotosetId;
	}

	public void setFlickrPhotosetId(String flickrPhotosetId) {
		this.flickrPhotosetId = flickrPhotosetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
	
	public List<AccountVO> getSiteAdmins() {
		return siteAdmins;
	}

	public void setSiteAdmins(List<AccountVO> siteAdmins) {
		this.siteAdmins = siteAdmins;
	}

	public String getBusinesscode() {
		return businesscode;
	}

	public void setBusinesscode(String businesscode) {
		this.businesscode = businesscode;
	}

	public List<DealVO> getDeals() {
		return deals;
	}

	public void setDeals(List<DealVO> deals) {
		this.deals = deals;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getPhone() {
		return phone;
	}

	public String getMobile() {
		return mobile;
	}

	public String getAddress() {
		return address;
	}

	public String getBusinesskey() {
		return businesskey;
	}

	public void setBusinesskey(String businesskey) {
		this.businesskey = businesskey;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getThemename() {
		return themename;
	}

	public void setThemename(String themename) {
		this.themename = themename;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
