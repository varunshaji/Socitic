package org.soc.shoppe.plugin;

import org.soc.common.SpringApplicationContext;
import org.soc.shoppe.busdata.service.BusinessDataService;
import org.soc.shoppe.site.dao.ShopDao;
import org.soc.shoppe.site.service.ShopService;

public class ServiceFactory {
   
	private ShopService shopService;
	private BusinessDataService businessDataService;
	private ShopDao shopDao; 
	
	public ServiceFactory()	{
		
	}
	
	public ShopService getShopService()
	{
		if(shopService == null)
			shopService = (ShopService) SpringApplicationContext.getBean("shopService");
		
		return shopService;
	}
	public BusinessDataService getBusinessDataService() {
		if(businessDataService == null)
			businessDataService = (BusinessDataService) SpringApplicationContext.getBean("businessDataService");
		return businessDataService;
	}
}
