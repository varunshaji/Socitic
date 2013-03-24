package org.soc.shoppe.site.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@Entity
public class Shop {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key shopKey;  
	
	private String shopId;
	
	private String bussinesscode;
	
    private String name;
	
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
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="shop")
	private List<Deal> deals;

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	public String getShopId() {
		return shopId;
	}
	
	public String setstype(String stype){
		return stype;
	}
	
	

	public void setShopid(String shopId) {
		this.shopId = shopId;
	}
	
	public List<String> getUsers() {
		return users;
	}
	
	public String getEmail() {
		return email;
	}
	public Shop(ShopVO shopVO)
	{
		this.shopId = shopVO.getShopId();
		this.name = shopVO.getName();
		this.location = shopVO.getLocation();
		this.description = shopVO.getDescription();
		this.users = shopVO.getUsers();
		this.phone = shopVO.getPhone();
		this.mobile = shopVO.getMobile();
		this.address = shopVO.getAddress();
		this.email = shopVO.getEmail();
		this.businesskey = shopVO.getBusinesskey();
		this.bussinesscode = shopVO.getBusinesscode();
		this.latitude = shopVO.getLatitude();
		this.longitude = shopVO.getLongitude();
	}
	
	public Shop() {
	}

	public List<Deal> getDeals() {
		return deals;
	}

	public void setDeals(List<Deal> deals) {
		this.deals = deals;
	}
	
	public ShopVO getShopVO()
	{
		ShopVO shopVO = new ShopVO();
		shopVO.setName(this.name);
		shopVO.setShopId(this.shopId);
		shopVO.setLocation(this.location);
		shopVO.setDescription(this.description);
		shopVO.setPhone(this.phone);
		shopVO.setMobile(this.mobile);
		shopVO.setAddress(this.address);
		shopVO.setUsers(this.users);
		shopVO.setDeals(convertToDealVOList(this.deals));
		shopVO.setEmail(this.email);
		shopVO.setBusinesskey(this.businesskey);
		shopVO.setBusinesscode(this.bussinesscode);
		shopVO.setLatitude(this.latitude);
		shopVO.setLongitude(this.longitude);
		return shopVO;
	}
	
	private List<DealVO> convertToDealVOList(List<Deal> deals){
		List<DealVO> dealVOList = new ArrayList();
		for(Deal deal:deals){
			dealVOList.add(deal.getDealVO());
		}
		return dealVOList;
		
	}

	public Key getShopKey() {
		return shopKey;
	}

	public void setShopKey(Key shopKey) {
		this.shopKey = shopKey;
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

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void update(ShopVO shopVO) {
		
		this.name = shopVO.getName();
		this.location = shopVO.getLocation();
		this.description = shopVO.getDescription();
		this.phone = shopVO.getPhone();
		this.mobile = shopVO.getMobile();
		this.email = shopVO.getEmail();
		this.address = shopVO.getAddress();
	}

	public String getBusinesskey() {
		return businesskey;
	}

	public void setBusinesskey(String businesskey) {
		this.businesskey = businesskey;
	}

	public void updateContacts(ShopVO shopVO) {
		
		this.location = shopVO.getLocation();
		this.phone = shopVO.getPhone();
		this.mobile = shopVO.getMobile();
		this.email = shopVO.getEmail();
		this.address = shopVO.getAddress();
		this.latitude = shopVO.getLatitude();
		this.longitude = shopVO.getLongitude();
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
