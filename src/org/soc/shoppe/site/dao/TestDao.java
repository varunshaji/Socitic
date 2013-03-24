package org.soc.shoppe.site.dao;

import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;

public interface TestDao {
	
	public DealVO getDeal();
	
	public DealVO getDeal(ShopVO shopVO);

}
