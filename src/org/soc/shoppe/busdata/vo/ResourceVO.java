package org.soc.shoppe.busdata.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceVO {
	
	private Map<String,String> propertyMap;
	
	private Map<String,String> resultMap;
	
	private String resourceName;
	
	private String businessName;
	
	private String shopId;
	
	private List<String> properties;
	
	public ResourceVO(List<String> properties,Map<String,String> propertyMap) {
		this.setPropertyMap(propertyMap);
		this.setProperties(properties);
	}
	public ResourceVO() {
		this.propertyMap = new HashMap<String, String>();
	}
	public Map<String,String> getPropertyMap() {
		return propertyMap;
	}
	public void setPropertyMap(Map<String,String> propertyMap) {
		this.propertyMap = propertyMap;
	}
	public String getResourceName() {
		return resourceName;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public List<String> getProperties() {
		return properties;
	}
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
	public Map<String,String> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<String,String> resultMap) {
		this.resultMap = resultMap;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
}
