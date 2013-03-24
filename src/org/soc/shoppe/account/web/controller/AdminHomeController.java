package org.soc.shoppe.account.web.controller;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.UrlProvider;
import org.soc.shoppe.account.exception.AccountsException;
import org.soc.shoppe.account.service.AccountService;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.RoleVO;
import org.soc.shoppe.site.exception.ShopException;
import org.soc.shoppe.site.service.ShopService;
import org.soc.shoppe.site.shopInitVosaoAdapter.ShopInitVosaoAdapter;
import org.soc.shoppe.site.shopInitVosaoAdapter.VosaoException;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;
import org.soc.shoppe.site.web.model.SiteConfigModel;
import org.soc.shoppe.site.web.model.SiteModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.appengine.api.NamespaceManager;

@Controller
@RequestMapping(value="soc")
public class AdminHomeController {

	Logger log = Logger.getLogger(AdminHomeController.class);
	
	@Inject
	private ShopService shopService;
	
	@Inject
	private AccountService accountService;	
	
	@Inject
	private ShopInitVosaoAdapter shopInitVosaoAdapter;
	
	@RequestMapping(value="/tohome.htm",method=RequestMethod.POST)
	public String homePg(HttpSession session,ModelMap map,@RequestParam(required=false,value="umode") String umode,HttpServletRequest request)
	{
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String result = null;
		boolean valid = false;
		for(RoleVO roleVO : accountVO.getRoles()) {
			if(roleVO.getRole().equals(umode))
			  valid = true;
		}
	    if(valid) {
	    	session.setAttribute("currentmode", umode);
	    	result = "redirect:tohome.htm";
	    }
	    else
	        result = "login?msg=Login_failed";
	    return result;
	}
	
	@RequestMapping(value="/tohome.htm",method=RequestMethod.GET)
	public String homePageRender(HttpSession session,ModelMap map,HttpServletRequest request,@ModelAttribute SiteConfigModel siteConfigModel,Errors errors)
	{
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		
		if (umode.equals("ROLE_ADMIN")) {
			
			String serverPath = UrlProvider.getServicePath(request);
			map.put("serverPath", serverPath);
			List<ShopVO> shops = null;
			try {
				shops = shopService.loadAllShops();
			}catch(ShopException shopException) {
				log.log(Priority.DEBUG,"Error: loading all shops",shopException);
				errors.reject("customError", shopException.getMessage());
			}
			map.put("shops", shops);
			map.put("page_include", "../site/shoplist.jspx");
			map.put("errors", errors);
	//		map.put("page_include", "../site/createpage.jspx");
		} else {
			List<ShopVO> shops = new ArrayList<ShopVO>();
			ShopVO shopVO = null;
			for (String shopId : accountVO.getShops()) {
				try {
					shopVO = shopService.loadShop(shopId);       //#####temp
				}catch(ShopException shopException) {
					log.log(Priority.DEBUG,"Error: loading shop",shopException);
			    	errors.reject(shopException.getMessage());
			    }

				if(shopVO != null) {
						shops.add(shopVO);
						shopVO = null;
				}	
			}
			String serverPath = UrlProvider.getServicePath(request);
			map.put("serverPath", serverPath);
			map.put("shops", shops);
			map.put("page_include", "../site/shoplist.jspx");
		}
		return "account/adminhome";
	}
	
	@ModelAttribute
	private SiteConfigModel init(){
		
		SiteConfigModel siteConfigModel = new SiteConfigModel();
		BindException errors = new BindException(new Object(), "accounts");
		NamespaceManager.set(null);
		try {
		    siteConfigModel.setAccountNames(accountService.getAllSiteAdmins());
		}catch(AccountsException accountsException){
			log.log(Priority.DEBUG,"Error :getting all accounts",accountsException);
			errors.reject(accountsException.getMessage());  //#####error not mapped
		}
		try {
			siteConfigModel.setThemes(shopInitVosaoAdapter.getAllThemes());
		}catch(VosaoException vosaoException) {
			log.log(Priority.DEBUG,"Error: getting all themes",vosaoException);
			errors.reject(vosaoException.getMessage());  //#####error not mapped
		}
		return siteConfigModel;
	}
	
	@RequestMapping(value="/alldeals.htm",method=RequestMethod.GET)
	public String viewallDeals(HttpSession session,ModelMap map,HttpServletRequest request)
	{
		List<DealVO> deals = null;
		BindException errors = new BindException(new Object(), "deals");
		//ValidationUtils.rejectIfEmpty(errors, "deals", errorCode)
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		String result = null;
		boolean valid = false;
		for(RoleVO roleVO : accountVO.getRoles()) {
			if(roleVO.getRole().equals(umode))
			  valid = true;
		}
		
	    if(valid) {
	    	map.put("name", accountVO.getUsername());
			map.put("umode", umode);
			map.put("homeid", umode.toLowerCase());
			if (umode.equals("ROLE_SITE-ADMIN")) {
				try {
					deals = shopService.loadDeals(accountVO.getShops());
				}catch(ShopException shopException) {
					log.log(Priority.DEBUG,"Error: loading deals",shopException);
    		    	errors.reject(shopException.getMessage());
    		    }
				SiteModel siteModel = new SiteModel();
				siteModel.setDealVOs(deals);
				map.put("siteModel", siteModel);
			}
	    	result = "account/adminhome";
	    }
	    else
	        result = "login?msg=Login_failed";
	    String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
	    map.put("page_include", "../site/deals.jspx");
	    map.put("errors",errors);
	    return result;
	}
	
	@RequestMapping(value="/shoplist.htm",method=RequestMethod.GET)
	public String viewShoplist(HttpSession session,ModelMap map,HttpServletRequest request,Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		List<ShopVO> shops = new ArrayList<ShopVO>();
		ShopVO shopVO = null;
		for(String shopId : accountVO.getShops()) {
			try {
				shopVO = shopService.loadShop(shopId);    //#####temp
			}
			catch(Exception exception) {
				log.log(Priority.DEBUG,"Error: loadingS hop"+shopId,exception);
				shopVO = null;
			}
			
			if(shopVO!=null) {
				log.info("adding shop:"+shopId);
				shops.add(shopVO);
				/*	try {  
					shops.add(shopService.loadShop(shopId));
				}catch(ShopException shopException) {
					log.log(Priority.DEBUG,"Error: loadingS hop"+shopId,shopException);
    		    	errors.reject(shopException.getMessage());
    		    }*/
			}   
		}
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		map.put("shops", shops);
		return "site/shoplist";
	}
}
