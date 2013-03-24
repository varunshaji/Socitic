package org.soc.shoppe.site.vo;

import java.io.Serializable;
import java.util.Date;

public class DealVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String dealId;
	
	private String type;
	
	private String heading;
	
	private String dealurl;
	
	private String area;
	
	private Date startDate;
	
	private Date endDate;
	
	private String description;

	public String getDealId() {
		return dealId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDealId(String dealId) {
		this.dealId = dealId;
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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
