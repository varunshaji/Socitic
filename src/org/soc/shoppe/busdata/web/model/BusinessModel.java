package org.soc.shoppe.busdata.web.model;

import java.util.List;

import javax.validation.Valid;

import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.busdata.vo.BusinessVO;

public class BusinessModel {
	@Valid
	private BusinessVO businessVO;
	
	private List<BusinessVO> businesses;

	public BusinessVO getBusinessVO() {
		return businessVO;
	}

	public void setBusinessVO(BusinessVO businessVO) {
		this.businessVO = businessVO;
	}

	public void setBusinesses(List<BusinessVO> businesses) {
		this.businesses=businesses;
		
	}

	public List<BusinessVO> getBusinesses() {
		return businesses;
	}
}
