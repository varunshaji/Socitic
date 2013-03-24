package org.soc.shoppe.site.service;

import java.util.List;

import org.soc.shoppe.site.dao.ShopDao;
import org.soc.shoppe.site.exception.ShopException;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;

public interface ShopService {
	
	public String createShop(ShopVO shopVO) throws ShopException;
	
	public boolean checkShopId(String shopId) throws ShopException;
	
	public ShopVO loadShop(String shopId) throws ShopException;
	
	public ShopVO modifyShop(ShopVO shopVO) throws ShopException;
	
	public Boolean addDeal(DealVO dealVO,String shopId) throws ShopException;
	
	public DealVO modifyDeal(DealVO dealVO, String shopId) throws ShopException;
	
	public DealVO loadDeal(String dealId,String shopId) throws ShopException;
	
	public List<DealVO> loadDeals(List<String> shopId) throws ShopException;
	
	public List<DealVO> loadAllDeals() throws ShopException;

	public Boolean removeDeal(String dealId, String shopId) throws ShopException;

	public List<ShopVO> loadAllShops() throws ShopException;

	public ShopVO setShopContacts(ShopVO shopVO) throws ShopException;

	public ShopDao getShopDao();

	public Boolean setBusinessKey(ShopVO shopVO) throws ShopException;

	public String loadBusinessKey(String shopId) throws ShopException;
}
