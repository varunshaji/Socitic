package org.soc.shoppe.site.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.UrlProvider;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.RoleVO;
import org.soc.shoppe.busdata.exception.BusinessException;
import org.soc.shoppe.busdata.service.BusinessDataService;
import org.soc.shoppe.busdata.vo.BusinessVO;
import org.soc.shoppe.site.exception.ShopException;
import org.soc.shoppe.site.service.ShopService;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;
import org.soc.shoppe.site.web.model.SiteModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping(value = "soc/site")
//@SessionAttributes("siteModel")
public class SiteController {

	Logger log = Logger.getLogger(SiteController.class);
	
	@Inject
	ShopService shopService;
	
	@Inject
	private BusinessDataService businessDataService;
	
	@RequestMapping(value = "/shop.htm", method = RequestMethod.GET)
	public String viewShop(@ModelAttribute("siteModel") SiteModel siteModel,@RequestParam(value="shopId") String shopId,ModelMap map,HttpServletRequest request,HttpSession session,Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
	    map.put("name", accountVO.getUsername());
    	map.put("umode", umode);
    	map.put("homeid", umode.toLowerCase());
		ShopVO shopVO = null;
		BusinessVO businessVO = null;
		String businesscode = null;
		try {
			shopVO = shopService.loadShop(shopId);
			businesscode = shopVO.getBusinesscode();
			businessVO = businesscode == null ? null : businessDataService.loadBusiness(businesscode);
			if(businessVO!=null){
				session.setAttribute("businessVO", businessVO);
			}
		}catch(BusinessException businessException){
			log.log(Priority.DEBUG,"Error: loading bussiness:"+businesscode,businessException);
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: loading shop:"+shopId,shopException);
	    	errors.reject(shopException.getMessage());
	    }
		if(shopVO!=null) {
			siteModel = new SiteModel(shopVO);
			siteModel.setDealVOs(shopVO.getDeals());
			String serverPath = UrlProvider.getServicePath(request);
			String sitePrefix = UrlProvider.getServerPath(request);
		    map.put("serverPath", serverPath);
		    map.put("sitePrefix", sitePrefix);
			map.put("siteurl", shopVO.getShopId());
			map.put("siteModel", siteModel);
			map.put("shopmode", true);
			map.put("shopname", shopVO.getName());
			map.put("page_include", "../site/shop.jspx");
		}
		else {
			map.put("errors", errors);
			return "redirect:../tohome.htm";
		}
    	return "account/adminhome";
	}
	
	@RequestMapping(value = "/edit.htm", method = RequestMethod.GET)
	public String editShop(@RequestParam(value="shopId") String shopId,ModelMap map,HttpServletRequest request,HttpSession session,@ModelAttribute("siteModel") SiteModel siteModel,Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		String result = null;
		boolean valid = false;
		for(RoleVO roleVO : accountVO.getRoles()) {
			if(roleVO.getRole().equals(umode))
			  valid = true;
		}
	    map.put("name", accountVO.getUsername());
	    if(valid) {
	    	map.put("umode", umode);
	    	map.put("homeid", umode.toLowerCase());
	    	if(umode.equals("ROLE_SITE-ADMIN")) {
	    		ShopVO shopVO = null;
	    		try {
	    			shopVO = shopService.loadShop(shopId);
	    		}catch(ShopException shopException) {
	    			log.log(Priority.DEBUG,"Error: loading shop:"+shopId,shopException);
    		    	errors.reject(shopException.getMessage());
    		    }
	    		if(shopVO!=null) {
	    			siteModel = new SiteModel(shopVO);
	    			siteModel.setDealVOs(shopVO.getDeals());
	    			String serverPath = UrlProvider.getServicePath(request);
	    		    map.put("serverPath", serverPath);
	    			map.put("siteurl", shopVO.getShopId());
	    			map.put("siteModel", siteModel);
	    			map.put("shopmode", true);
	    			map.put("shopname", shopVO.getName());
	    			map.put("page_include", "../site/editshop.jspx");
	    		}
	    		else {
	    			map.put("errors", errors);
	    			return "redirect:../tohome.htm";
	    		}
	    	}
	    	result = "account/adminhome";
	    }
	    else
	        result = "account/login";
	    return result;
	}
	
	@RequestMapping(value = "/edit.htm", method = RequestMethod.POST)
	public String updateShop(@ModelAttribute("siteModel") SiteModel siteModel,HttpSession session,ModelMap map,HttpServletRequest request,Errors errors) {
		
		String result = null;
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
    	map.put("homeid", umode.toLowerCase());
		ShopVO shopVO = null;
		try {
			shopVO = shopService.modifyShop(siteModel.getShopVO());
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: editing shop",shopException);
	    	errors.reject(shopException.getMessage());
	    }
		if(shopVO!=null) {
			
			siteModel = new SiteModel(shopVO);
			siteModel.setDealVOs(shopVO.getDeals());
			String serverPath = UrlProvider.getServicePath(request);
		    map.put("serverPath", serverPath);
			map.put("siteurl", shopVO.getShopId());
			map.put("siteModel", siteModel);
			map.put("shopmode", true);
			map.put("shopname", shopVO.getName());
			map.put("page_include", "../site/shop.jspx");
			result = "account/adminhome";
		}
		else {
			map.put("errors", errors);
			return "redirect:../tohome.htm";
		}
		return result;
	}

	@RequestMapping(value="/shoplist.htm",method=RequestMethod.GET)
	public String viewShoplist(HttpSession session,ModelMap map,HttpServletRequest request) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
		map.put("accountVO", accountVO);
		return "site/shoplist";
	}
	
	@RequestMapping(value = "/deal.htm", method = RequestMethod.GET)
	public String viewAddDeal(@ModelAttribute("siteModel") SiteModel siteModel,ModelMap map,HttpSession session,@RequestParam(value="shopId") String shopId,HttpServletRequest request,Errors errors) {
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		String result = null;
		boolean valid = false;
		for(RoleVO roleVO : accountVO.getRoles()) {
			if(roleVO.getRole().equals(umode))
			  valid = true;
		}
		
	    map.put("name", accountVO.getUsername());
	    if(valid) {
	    	map.put("umode", umode);
	    	map.put("homeid", umode.toLowerCase());
	    	if(umode.equals("ROLE_SITE-ADMIN")) {
	    		    ShopVO shopVO = null;
	    		    try {
	    		    	shopVO = shopService.loadShop(shopId);
	    		    }catch(ShopException shopException) {
	    		    	log.log(Priority.DEBUG,"Error: load shop:"+shopId,shopException);
	    		    	errors.reject(shopException.getMessage());
	    		    }
	    		    if(shopVO!=null) {
	    		    	siteModel = new SiteModel(shopVO);
		    			siteModel.setDealVOs(shopVO.getDeals());
		    			String serverPath = UrlProvider.getServicePath(request);
		    		    map.put("serverPath", serverPath);
		    			map.put("siteurl", shopVO.getShopId());
		    			map.put("siteModel", siteModel);
		    			map.put("shopmode", true);
		    			map.put("shopname", shopVO.getName());
		    			map.put("page_include", "../site/addDeal.jspx");
	    		    }
	    		    else {
	    		    	return "../login";
	    		    }
	    	}
	    	result = "account/adminhome";
	    }
	    else
	        result = "account/login?msg=session_failed";
	    return result;
	}

	@RequestMapping(value = "/deal.htm", method = RequestMethod.POST)
	public String addDeal(@ModelAttribute("siteModel") SiteModel siteModel,ModelMap map,Errors errors) {
		String shopId = siteModel.getShopVO().getShopId();
		DealVO dealVO = siteModel.getDealVO();
		try {
			shopService.addDeal(dealVO, shopId);
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: addin deal for shop:"+shopId,shopException);
	    	errors.reject("customError", shopException.getMessage());
	    }
		return "redirect:shop.htm?shopId="+shopId;
	}
	
	@RequestMapping(value = "/viewdeal.htm", method = RequestMethod.GET)
	public String viewDeal(@RequestParam(value="shop") String shopId,@RequestParam(value="deal") String dealId,ModelMap map,HttpServletRequest request,HttpSession session,@ModelAttribute("siteModel") SiteModel siteModel,Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		String result = null;
		
	    map.put("name", accountVO.getUsername());
    	map.put("umode", umode);
    	map.put("homeid", umode.toLowerCase());
	    ShopVO shopVO = null;
	    try {
	    	shopVO = shopService.loadShop(shopId);
	    }catch(ShopException shopException) {
	    	log.log(Priority.DEBUG,"Error: loading shop:"+shopId,shopException);
	    	errors.reject(shopException.getMessage());
	    }
	    if(shopVO!=null) {
	    	
	    	DealVO dealVO = null;
	    	try {
	    		dealVO = shopService.loadDeal(dealId,shopVO.getShopId());
	    	}catch(ShopException shopException) {
	    		log.log(Priority.DEBUG,"Error: loading deal:"+dealId,shopException);
		    	errors.reject("customError", shopException.getMessage());
		    }
	    	siteModel = new SiteModel(shopVO,dealVO);
	    	siteModel.setDealVOs(shopVO.getDeals());
	    	String serverPath = UrlProvider.getServicePath(request);
		    map.put("serverPath", serverPath);
			map.put("siteurl", shopVO.getShopId());
			map.put("siteModel", siteModel);
			map.put("shopmode", true);
			if(dealVO!=null) {
				map.put("dealmode", true);
    			map.put("page_include", "../site/viewdeal.jspx");
			}
			else
			    map.put("page_include", "../site/shop.jspx");
		    }
	    	map.put("errors", errors);
	    	result = "account/adminhome";
	    
	    return result;
	}
	
	@RequestMapping(value = "/editdeal.htm", method = RequestMethod.GET)
	public String editDeal(@RequestParam(value="shop") String shopId,@RequestParam(value="deal") String dealId,ModelMap map,HttpServletRequest request,HttpSession session,@ModelAttribute("siteModel") SiteModel siteModel,Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		String result = null;
		boolean valid = false;
		for(RoleVO roleVO : accountVO.getRoles()) {
			if(roleVO.getRole().equals(umode))
			  valid = true;
		}
		
	    map.put("name", accountVO.getUsername());
	    if(valid) {
	    	map.put("umode", umode);
	    	map.put("homeid", umode.toLowerCase());
	    	if(umode.equals("ROLE_SITE-ADMIN")) {
	    		    ShopVO shopVO = null;
	    		    try {
	    		    	shopVO = shopService.loadShop(shopId);
	    		    }catch(ShopException shopException) {
	    		    	log.log(Priority.DEBUG,"Error: loading shop:"+shopId,shopException);
	    		    	errors.reject("customError", shopException.getMessage());
	    		    }
	    		    if(shopVO!=null) {
	    		    	DealVO dealVO = null;
	    		    	try {
	    		    		dealVO = shopService.loadDeal(dealId,shopVO.getShopId());
	    		    	}catch(ShopException shopException) {
		    		    	errors.reject("customError", shopException.getMessage());
		    		    }
	    		    	siteModel = new SiteModel(shopVO,dealVO);
	    		    	siteModel.setDealVOs(shopVO.getDeals());
	    		    	String serverPath = UrlProvider.getServicePath(request);
		    		    map.put("serverPath", serverPath);
		    			map.put("siteurl", shopVO.getShopId());
		    			map.put("siteModel", siteModel);
		    			map.put("shopmode", true);
		    			if(dealVO!=null) {
		    				map.put("dealmode", true);
			    			map.put("page_include", "../site/editdeal.jspx");
		    			}
		    			else
		    			    map.put("page_include", "../site/shop.jspx");
	    		    }
	    		    else {
	    		    	return "account/login?msg=session_failed";
	    		    }	
	    	}
	    	result = "account/adminhome";
	    }
	    else
	        result = "account/login?msg=session_failed";
	    return result;
	}
	
	@RequestMapping(value = "/editdeal.htm", method = RequestMethod.POST)
	public String updateDeal(@ModelAttribute("siteModel") SiteModel siteModel,HttpSession session,ModelMap map,HttpServletRequest request,Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
    	map.put("homeid", umode.toLowerCase());
		ShopVO shopVO = siteModel.getShopVO();
		DealVO dealVO = null;
		try {
			dealVO = shopService.modifyDeal(siteModel.getDealVO(), shopVO.getShopId());
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: editing deal",shopException);
			errors.reject("customError", shopException.getMessage());
		}
		siteModel = new SiteModel(shopVO,dealVO);
		siteModel.setDealVOs(shopVO.getDeals());
		String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
		map.put("siteurl", shopVO.getShopId());
		map.put("siteModel", siteModel);
		map.put("shopmode", true);
		if(dealVO!=null) {
			map.put("dealmode", true);
			map.put("page_include", "../site/viewdeal.jspx");
		}
		else
		    map.put("page_include", "../site/shop.jspx");
		map.put("errors", errors);
		return "account/adminhome";
	}

	@RequestMapping(value="/deletedeal.htm",method=RequestMethod.POST)
	public String deleteDeal(@ModelAttribute("siteModel") SiteModel siteModel,HttpSession session,ModelMap map,HttpServletRequest request,@RequestParam("shopId") String shopId,@RequestParam("dealId") String dealId,Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		String result = null;
		boolean valid = false;
		for(RoleVO roleVO : accountVO.getRoles()) {
			if(roleVO.getRole().equals(umode))
			  valid = true;
		}
		
	    map.put("name", accountVO.getUsername());
	    if(valid) {
	    	map.put("umode", umode);
	    	map.put("homeid", umode.toLowerCase());
	    	if(umode.equals("ROLE_SITE-ADMIN")) {
	    		boolean response=false;
	    		try{
	    			response  = shopService.removeDeal(dealId, shopId);
	    		}catch(ShopException shopException){
	    			log.log(Priority.DEBUG,"Error: removing deal:"+dealId+" of shop:"+shopId,shopException);
	    			errors.reject("customError", shopException.getMessage());
	    		}
	    		    if(response) {
	    		    	try {
	    		    		ShopVO shopVO = shopService.loadShop(shopId);
	    		    		siteModel = new SiteModel(shopVO);
			    			siteModel.setDealVOs(shopVO.getDeals());
			    			String serverPath = UrlProvider.getServicePath(request);
			    		    map.put("serverPath", serverPath);
			    			map.put("siteurl", shopVO.getShopId());
			    			map.put("siteModel", siteModel);
			    			map.put("shopmode", true);
			    			map.put("shopname", shopVO.getName());
		    			    map.put("page_include", "../site/shop.jspx");
	    		    	}catch(ShopException shopException){
	    		    		log.log(Priority.DEBUG,"Error: loading shop:"+shopId,shopException);
	    		    		errors.reject("customError", shopException.getMessage());
	    		    	}
		    		 }
	    			 else {
	    				 return "redirect:editdeal.htm?deal="+dealId+"&shop="+shopId+"";
	    			 }
	    	}
	    	result = "account/adminhome";
	    }
	    else
	        result = "account/login?msg=session_failed";
	    return result;
	}
	
	@RequestMapping(value="/deals.htm",method=RequestMethod.GET)
	public String retrieveAllDeals(@ModelAttribute("siteModel") SiteModel siteModel,ModelMap map,HttpServletRequest request,HttpSession session,Errors errors) {
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
    	map.put("homeid", umode.toLowerCase());
		List<DealVO> deals = null;
		try {
			deals= shopService.loadAllDeals();
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: loading all deals",shopException);
			errors.reject("customError", shopException.getMessage());
		}
		siteModel.setDealVOs(deals);
		String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
		map.put("siteModel", siteModel);
		map.put("page_include", "../site/deals.jspx");
		map.put("errors", errors);
		return "account/adminhome";
	}
	
	@RequestMapping(value="/allshops.htm",method=RequestMethod.GET)
	public String retrieveAllShops(@ModelAttribute("siteModel") SiteModel siteModel,ModelMap map,HttpServletRequest request,HttpSession session,Errors errors) {
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
    	map.put("homeid", umode.toLowerCase());
		List<ShopVO> shops = null;
		try {
			shops = shopService.loadAllShops();
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: loading all shops",shopException);
			errors.reject("customError", shopException.getMessage());
		}
		map.put("shops", shops);
		String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
		map.put("siteModel", siteModel);
		map.put("page_include", "../site/shoplist.jspx");
		map.put("errors", errors);
		return "account/adminhome";
	}
}
