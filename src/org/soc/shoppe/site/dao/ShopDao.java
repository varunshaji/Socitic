package org.soc.shoppe.site.dao;

import java.util.List;
import org.soc.common.exception.DaoException;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;

public interface ShopDao {
	
	public String addShop(ShopVO shopVO) throws DaoException;
	
	public boolean validateShopId(String shopId);
	
	public ShopVO getShop(String shopID) throws DaoException;
	
	public ShopVO updateShop(ShopVO shopVO) throws DaoException;
	
	public boolean saveDeal(DealVO dealVO,String shopId) throws DaoException;	
	
	public DealVO updateDeal(DealVO dealVO, String shopId) throws DaoException;
	
	public DealVO getDeal(String dealId,String shopId) throws DaoException;
	
	public List<DealVO> getDeals(String shopId) throws DaoException;

	public List<DealVO> getAllDeals() throws DaoException;

	public Boolean deleteShopDeal(String dealId, String shopId) throws DaoException;

	public List<ShopVO> getAllShops() throws DaoException;

	public ShopVO addShopContacts(ShopVO shopVO) throws DaoException;

	public Boolean saveBusinessKey(ShopVO shopVO) throws DaoException;

	public String getBusinessKey(String shopId) throws DaoException;
}
