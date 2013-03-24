package org.soc.shoppe.site.entity;

import java.util.Date; 

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.soc.shoppe.site.vo.DealVO;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Entity
public class Deal {

	
/*	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String keyString;

	@Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true")
	private String dealId;*/
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key dealKey;
	
	private Shop shop;

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	private String type;
	
	private String heading;
	
	private String dealurl;
	
	private String area;
	
	private Date startDate;

	private Date endDate;

	private String description;



	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getDescription() {
		return description;
	}

	

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getDealurl() {
		return dealurl;
	}

	public void setDealurl(String dealurl) {
		this.dealurl = dealurl;
	}

	public DealVO getDealVO() {
		DealVO dealVO = new DealVO();
		dealVO.setType(this.type);
		dealVO.setHeading(this.heading);
		dealVO.setDealurl(this.dealurl);
		dealVO.setDealId(Long.toString(dealKey.getId()));
		dealVO.setDescription(this.description);
		dealVO.setEndDate(this.endDate);
		dealVO.setStartDate(this.startDate);
		dealVO.setArea(this.area);
		return dealVO;
	}
    
	public Deal() {
		
	}
	
	public Deal(DealVO dealVO) {
		this.type = dealVO.getType();
		this.heading = dealVO.getHeading();
		this.dealurl = dealVO.getDealurl();
		this.description = dealVO.getDescription();
		this.startDate = dealVO.getStartDate();
		this.endDate = dealVO.getEndDate();
		this.area = dealVO.getArea();
		if(dealVO.getDealId()!=null){
			this.dealKey = KeyFactory.stringToKey(dealVO.getDealId());   
		}
	}
	
	public void update(DealVO dealVO) {
		this.heading = dealVO.getHeading();
		this.area = dealVO.getArea();
		this.description = dealVO.getDescription();
		this.startDate = dealVO.getStartDate();
		this.endDate = dealVO.getEndDate();
	}

	public Key getDealKey() {
		return dealKey;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
