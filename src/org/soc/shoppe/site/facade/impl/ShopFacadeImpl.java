package org.soc.shoppe.site.facade.impl;

import java.util.List;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.exception.DaoException;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.site.exception.ShopException;
import org.soc.shoppe.site.facade.ShopFacade;
import org.soc.shoppe.site.facade.dao.ShopAccountUnifiedDao;
import org.soc.shoppe.site.vo.ShopVO;

public class ShopFacadeImpl implements ShopFacade {
	
	@Inject
	private ShopAccountUnifiedDao shopAccountUnifiedDao;

	public void setShopAccountUnifiedDao(ShopAccountUnifiedDao shopAccountUnifiedDao) {
		this.shopAccountUnifiedDao = shopAccountUnifiedDao;
	}

	Logger log = Logger.getLogger(ShopFacade.class); 
	
	@Override
	public List<AccountVO> createShop(ShopVO shopVO) throws ShopException {
		List<AccountVO> result = null;
		try {
			result = shopAccountUnifiedDao.saveShop(shopVO);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: creating shop",daoException);
			throw new ShopException(ShopException.ERROR_ADD, daoException);
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: creating shop",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	
	public Boolean registerAccountAndShop(ShopVO shopVO,AccountVO accountVO) throws ShopException {
		boolean result = false;
		try {
			result = shopAccountUnifiedDao.saveAccountAndShop(shopVO, accountVO);
		}catch(DaoException daoException) {
			log.log(Priority.DEBUG,"Error: creating shop and account",daoException);
			throw new ShopException(ShopException.ERROR_ADD, daoException);
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: creating shop and account",exception);
			throw new ShopException(ShopException.ERROR_UNKNOWN, exception);
		}
		return result;
	}

}
