package org.soc.shoppe.busdata.web.model;

import org.soc.shoppe.busdata.vo.ResourceVO;

public class BusinessDataInputModel {
	
	private ResourceVO resourceVO;
	
	private String current;
	
	public ResourceVO getResourceVO() {
		return resourceVO;
	}

	public void setResourceVO(ResourceVO resourceVO) {
		this.resourceVO = resourceVO;
	}
	
	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}
	
}
