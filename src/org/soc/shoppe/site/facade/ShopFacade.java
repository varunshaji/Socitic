package org.soc.shoppe.site.facade;

import java.util.List;

import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.site.exception.ShopException;
import org.soc.shoppe.site.vo.ShopVO;

public interface ShopFacade {

	public List<AccountVO> createShop(ShopVO shopVO) throws ShopException;

	public Boolean registerAccountAndShop(ShopVO shopVO, AccountVO accountVO) throws ShopException;

}
