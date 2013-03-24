package org.soc.shoppe.account.web.model;

import java.util.List;

import javax.validation.Valid;

import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.busdata.vo.BusinessVO;
import org.soc.shoppe.site.vo.ShopVO;

public class RegistrationModel {
	
	@Valid
	private AccountVO accountVO;
	
	@Valid
	private ShopVO shopVO;
	
	private List<String> themeImages;
	
	private List<BusinessVO> businesses;
	
	private String channelKey;

	public AccountVO getAccountVO() {
		if(accountVO==null)
			accountVO = new AccountVO();
		return accountVO;
	}

	public ShopVO getShopVO() {
		if(shopVO==null)
			shopVO = new ShopVO();
		return shopVO;
	}

	public void setAccountVO(AccountVO accountVO) {
		this.accountVO = accountVO;
	}

	public void setShopVO(ShopVO shopVO) {
		this.shopVO = shopVO;
	}

	public List<String> getThemeImages() {
		return themeImages;
	}

	public void setThemeImages(List<String> themeImages) {
		this.themeImages = themeImages;
	}

	public List<BusinessVO> getBusinesses() {
		return businesses;
	}

	public void setBusinesses(List<BusinessVO> businesses) {
		this.businesses = businesses;
	}

	public String getChannelKey() {
		return channelKey;
	}

	public void setChannelKey(String channelKey) {
		this.channelKey = channelKey;
	}
}
