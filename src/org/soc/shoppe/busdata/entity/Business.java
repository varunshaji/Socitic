package org.soc.shoppe.busdata.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.soc.shoppe.busdata.vo.BusinessVO;

@Entity
public class Business {
	
	@Id
	private String businessCode;
	
	private String businessName;
	
	private String description;
	
	private String businessKey;
	
	
	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public Business(BusinessVO businessVO){
		this.businessCode=businessVO.getbusinessCode();
		this.businessName=businessVO.getbusinessName();
		this.description=businessVO.getDescription();
		this.businessKey=businessVO.getbusinessKey();
	}
	
	public BusinessVO getBusinessVO(){
		BusinessVO businessVO=new BusinessVO();
		businessVO.setbusinessCode(this.businessCode);
		businessVO.setbusinessName(this.businessName);
		businessVO.setDescription(this.description);
		businessVO.setbusinessKey(this.businessKey);
		return businessVO;
	}

}
