package org.soc.shoppe.site.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.exception.DaoException;
import org.soc.shoppe.site.dao.ShopDao;
import org.soc.shoppe.site.exception.ShopException;
import org.soc.shoppe.site.service.ShopService;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;

public class ShopServiceImpl implements ShopService {

	Logger log = Logger.getLogger(ShopServiceImpl.class); 
	
	private ShopDao shopDao;
	
	@Override
	public ShopDao getShopDao() {
		return shopDao;
	}

	public void setShopDao(ShopDao shopDao) {
		this.shopDao = shopDao;
	}
	
	@Override
	public String createShop(ShopVO shopVO) throws ShopException {
		log.debug("inside createShopVO..............");
		
		boolean valid = false;
		try{
			valid = shopDao.validateShopId(shopVO.getShopId());
		}/*catch (DaoException daoException) {
			throw new ShopException(ShopException.ERROR_RETRIEVE, daoException);
		}*/catch(Exception exception){
			log.log(Priority.DEBUG,"Error: validating shopId",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		String result = null;
		if(valid) {
			try{
				result = shopDao.addShop(shopVO);
			}catch (DaoException daoException) {
				log.log(Priority.DEBUG,"Error: creating shop",daoException);
				throw new ShopException(ShopException.ERROR_ADD, daoException);
			}catch(Exception exception){
				log.log(Priority.DEBUG,"Error: creating shop",exception);
				throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
			}
		}		  
	    else
	    	return null;
		
		return result;
	}
	
	@Override
	public boolean checkShopId(String shopId) throws ShopException {
		boolean result = false;
		try{
			result = shopDao.validateShopId(shopId);
		}/*catch (DaoException daoException) {
			throw new ShopException(ShopException.ERROR_ADD, daoException);
		}*/catch(Exception exception){
			log.log(Priority.DEBUG,"Error: checking shopId"+shopId,exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public ShopVO loadShop(String shopId) throws ShopException {
		
		ShopVO result = null;
		try{
			result = shopDao.getShop(shopId);
		}catch (DaoException daoException) {
			log.log(Priority.DEBUG,"Error: loading shop:"+shopId,daoException);
			throw new ShopException(ShopException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: loading shop:"+shopId,exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public List<ShopVO> loadAllShops() throws ShopException {
		List<ShopVO> result = new ArrayList<ShopVO>();
		try {
			result = shopDao.getAllShops();
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: loading all shops",daoException);
			throw new ShopException(ShopException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: loading all shops",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public ShopVO modifyShop(ShopVO shopVO) throws ShopException {
		
		ShopVO result = null;
		try{
			result = shopDao.updateShop(shopVO);
		}catch (DaoException daoException) {
			log.log(Priority.DEBUG,"Error: modifying shop",daoException);
			throw new ShopException(ShopException.ERROR_UPDATE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: modifying shop",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}

	@Override
	public Boolean addDeal(DealVO dealVO,String shopId) throws ShopException {
		
		Boolean result = false;
		try {
			result = shopDao.saveDeal(dealVO,shopId);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: adding deal of shop:"+shopId,daoException);
			throw new ShopException(ShopException.ERROR_ADD, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: adding deal of shop:"+shopId,exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}

	@Override
	public DealVO loadDeal(String dealId,String shopId) throws ShopException {
		DealVO result = null;
		try {
			result = shopDao.getDeal(dealId,shopId);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: loading deal:"+dealId+" of shop:"+shopId,daoException);
			throw new ShopException(ShopException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: loading deal:"+dealId+" of shop:"+shopId,exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public DealVO modifyDeal(DealVO dealVO,String shopId) throws ShopException {
		DealVO result = null;
		try {
			result = shopDao.updateDeal(dealVO, shopId);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: modifying deal of shop:"+shopId,daoException);
			throw new ShopException(ShopException.ERROR_UPDATE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: modifying deal of shop:"+shopId,exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public List<DealVO> loadDeals(List<String> shopIds) throws ShopException {
		
		List<DealVO> result = new ArrayList<DealVO>();
		List<DealVO> dealVOList = new ArrayList();
		try {
			for(String shopId : shopIds) {
				dealVOList = shopDao.getDeals(shopId);
				if(dealVOList != null) {
					for(DealVO dealVO : dealVOList)
						result.add(dealVO);
				}
			}
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: loading all deals",daoException);
			throw new ShopException(ShopException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: loading all deals",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public List<DealVO> loadAllDeals() throws ShopException {
		List<DealVO> result = null;
		try {
			result = shopDao.getAllDeals();
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: loading all deals",daoException);
			throw new ShopException(ShopException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: loading all deals",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public Boolean removeDeal(String dealId,String shopId) throws ShopException {
		Boolean result = false;
		try {
			result = shopDao.deleteShopDeal(dealId, shopId);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: removing deal:"+dealId+" of shop:"+shopId,daoException);
			throw new ShopException(ShopException.ERROR_DELETE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: removing deal:"+dealId+" of shop:"+shopId,exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public ShopVO setShopContacts(ShopVO shopVO) throws ShopException {
		ShopVO result = null;
		try {
			result = shopDao.addShopContacts(shopVO);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: adding shop contact details",daoException);
			throw new ShopException(ShopException.ERROR_UPDATE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: adding shop contact details",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public Boolean setBusinessKey(ShopVO shopVO) throws ShopException {
		boolean result = false;
		try {
			result = shopDao.saveBusinessKey(shopVO);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: adding rdfBlobkey",daoException);
			throw new ShopException(ShopException.ERROR_UPDATE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: adding rdfBlobkey",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public String loadBusinessKey(String shopId) throws ShopException {
		
		String result = null;
		try {
			result = shopDao.getBusinessKey(shopId);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: adding businessskey",daoException);
			throw new ShopException(ShopException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: adding businesskey",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
}
