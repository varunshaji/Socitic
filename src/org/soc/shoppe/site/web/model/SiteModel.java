package org.soc.shoppe.site.web.model;

import java.util.List;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;

public class SiteModel {
	
	private List<DealVO> dealVOs;
	private ShopVO shopVO;
	private DealVO dealVO;

	
	public SiteModel()
	{
		
	}
	public SiteModel(ShopVO shopVO,DealVO dealVO) {
		
		this.shopVO = shopVO;
		this.dealVO = dealVO;
	}
	public SiteModel(ShopVO shopVO)
	{
		this.shopVO = shopVO;
	}
	 
	public SiteModel(DealVO dealVO)
	{
		this.dealVO = dealVO;
	}
	
	public ShopVO getShopVO() {
		return shopVO;
	}

	public void setShopVO(ShopVO shopVO) {
		this.shopVO = shopVO;
	}

	public DealVO getDealVO() {
		return dealVO;
	}

	public void setDealVO(DealVO dealVO) {
		this.dealVO = dealVO;
	}
	public List<DealVO> getDealVOs() {
		return dealVOs;
	}
	public void setDealVOs(List<DealVO> dealVOs) {
		this.dealVOs = dealVOs;
	}
}
