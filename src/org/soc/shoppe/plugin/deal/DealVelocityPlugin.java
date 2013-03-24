package org.soc.shoppe.plugin.deal;

import java.util.List;
import java.util.Map;

import org.soc.shoppe.plugin.ServiceFactory;
import org.soc.shoppe.site.service.ShopService;
import org.soc.shoppe.site.vo.DealVO;
import org.vosao.business.Business;
import org.vosao.entity.PluginEntity;
import org.vosao.velocity.plugin.AbstractVelocityPlugin;

import com.google.appengine.api.NamespaceManager;

public class DealVelocityPlugin extends AbstractVelocityPlugin{

	public DealVelocityPlugin(Business aBusiness) {
		setBusiness(aBusiness);
	}
	
	public String render() {
		try {
			ServiceFactory serviceFactory = new ServiceFactory();
			List<DealVO> deals = serviceFactory.getShopService().getShopDao().getDeals(NamespaceManager.get());
			return "deals";
		    	
		}
		catch(Exception ae) {
			ae.printStackTrace();
		}
		return null;
	}
}
