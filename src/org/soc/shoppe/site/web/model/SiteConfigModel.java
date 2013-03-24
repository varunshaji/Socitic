package org.soc.shoppe.site.web.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.site.vo.ShopVO;
import org.soc.shoppe.site.vo.ThemeVO;

public class SiteConfigModel {
	
	public SiteConfigModel() {
	}
	
	List<AccountVO> accounts =  LazyList.decorate(
		      new ArrayList(),
		      FactoryUtils.instantiateFactory(AccountVO.class));
	@Valid
	private ShopVO shopVO;
	
	private List<String> accountNames;
	
	private List<ThemeVO> themes;

	public ShopVO getShopVO() {
		return shopVO;
	}

	public void setShopVO(ShopVO shopVO) {
		this.shopVO = shopVO;
	}

	public List<AccountVO> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountVO> accounts) {
		this.accounts = accounts;
	}

	public List<String> getAccountNames() {
		return accountNames;
	}

	public void setAccountNames(List<String> accountNames) {
		this.accountNames = accountNames;
	}

	public List<ThemeVO> getThemes() {
		return themes;
	}

	public void setThemes(List<ThemeVO> themes) {
		this.themes = themes;
	}

}
