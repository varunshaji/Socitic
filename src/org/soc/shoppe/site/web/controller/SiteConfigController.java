package org.soc.shoppe.site.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.SocConstant;
import org.soc.common.UrlProvider;
import org.soc.shoppe.account.exception.AccountsException;
import org.soc.shoppe.account.service.AccountService;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.site.exception.ShopException;
import org.soc.shoppe.site.facade.ShopFacade;
import org.soc.shoppe.site.service.ShopService;
import org.soc.shoppe.site.shopInitVosaoAdapter.ShopInitVosaoAdapter;
import org.soc.shoppe.site.shopInitVosaoAdapter.VosaoException;
import org.soc.shoppe.site.vo.ShopVO;
import org.soc.shoppe.site.web.model.SiteConfigModel;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.appengine.api.NamespaceManager;

@Controller
@RequestMapping(value="soc/config")
public class SiteConfigController {
	
	@Inject
	private ShopFacade shopFacade;
	
	@Inject
	private ShopService shopService;
	
	@Inject
	private AccountService accountService;
	
	@Inject
	private ShopInitVosaoAdapter shopInitVosaoAdapter;
	
	@Inject
	private ResourceBundleMessageSource messageSource;
	
	Logger log = Logger.getLogger(SiteConfigController.class);
	
	@RequestMapping(value="/main.htm",method=RequestMethod.GET)
	public String viewMain(){
		return "site/main";
	}
	
	@RequestMapping(value="/create.htm",method=RequestMethod.GET)
	public String viewSiteConfig(ModelMap map,@ModelAttribute SiteConfigModel siteConfigModel,HttpSession session,HttpServletRequest request)
	{
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		map.put("page_include", "../site/createpage.jspx");
		return "account/adminhome";
	}
	
	@RequestMapping(value="/create.htm",method=RequestMethod.POST)
	public String addSiteConfig(@Valid @ModelAttribute("siteConfigModel") SiteConfigModel siteConfigModel,BindingResult error,ModelMap map, HttpSession session, HttpServletRequest request) {
		
		boolean result = false;
		AccountVO admin = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", admin.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		BindException errors = new BindException(new Object(), "shop");
		if(error.hasErrors()) {
			map.put("page_include", "../site/createpage.jspx");
			errors.reject("server.validation.error");
			map.put("errors", errors);
			return "account/adminhome";
		}
		
		List<AccountVO> accounts = new ArrayList<AccountVO>();

		if (NamespaceManager.get() != null)
			NamespaceManager.set(null);
		try {
			accounts = shopFacade.createShop(siteConfigModel.getShopVO());
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: creating shop:",shopException);
			errors.reject(shopException.getMessage());
		}
		if (accounts != null) {
			String shopId = siteConfigModel.getShopVO().getShopId();

			shopInitVosaoAdapter.setCurrentNameSpace(shopId);
			AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
			try {
				result = shopInitVosaoAdapter.createCmsPage(siteConfigModel.getShopVO(),accounts, accountVO.getEmail());
			}catch(VosaoException vosaoException) {
				log.log(Priority.DEBUG,"Error: creating vosao shop website:",vosaoException);
				errors.reject(vosaoException.getMessage());
			}
			if (result)
				map.put("page_include", "../site/contact_details.jspx");
			else
				map.put("page_include", "../site/createpage.jspx");
			ShopVO shopVO = new ShopVO();
			shopVO.setShopId(siteConfigModel.getShopVO().getShopId());
			siteConfigModel.setShopVO(shopVO);
			map.put("siteConfigModel", siteConfigModel);
			
		} else {
			/*siteConfigModel.getShopVO().setSiteAdmins(null);
			errors.rejectValue("shopVO.shopId", "validation.exists");
			map.put("page_include", "../site/createpage.jspx");*/
			return "redirect:../tohome.htm";
			
		}
		map.put("errors", errors);
		return "account/adminhome";
	}

	@RequestMapping(value="/contact.htm",method=RequestMethod.POST)
	public String addContactDetails(@ModelAttribute("siteConfigModel") SiteConfigModel siteConfigModel,ModelMap map, HttpSession session, HttpServletRequest request, Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		boolean result = false;
		String siteurl = null;
		String resultUrl = null;
		if (NamespaceManager.get() != null)
			NamespaceManager.set(null);
        ShopVO shopVO = null;
        try {
        	shopVO = shopService.setShopContacts(siteConfigModel.getShopVO());
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: updating shop contact details",shopException);
	    	errors.reject(shopException.getMessage());
	    }
        if(shopVO!=null) {
        	try {
        		session.setAttribute("current", shopVO.getShopId());
            	shopInitVosaoAdapter.setCurrentNameSpace(shopVO.getShopId());
            	result = shopInitVosaoAdapter.customiseShopSite(shopVO, accountVO.getEmail());
            	if(result) {
            		String siteurlPath = UrlProvider.getServerPath(request);
            		siteConfigModel.setShopVO(shopVO);
        			map.put("siteurl", shopVO.getShopId());
        			map.put("siteurlPath", siteurlPath);
        			map.put("siteConfigModel", siteConfigModel);
            		map.put("page_include", "../site/shop_summary.jspx");
            	}
            	else 
            		map.put("page_include", "../site/createpage.jspx");
        	}catch(VosaoException vosaoException) {
        		log.log(Priority.DEBUG,"Error: customising shop website",vosaoException);
        		errors.reject(vosaoException.getMessage());
        	}
        }
        else 
    		map.put("page_include", "../site/createpage.jspx");
		String umode = (String) session.getAttribute("currentmode");
		String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
    	map.put("homeid", umode.toLowerCase());
    	map.put("errors", errors);
        return "account/adminhome";
	}
	
	/*@RequestMapping(value="/tosite.htm",method=RequestMethod.GET)
	public String cmsPage(@RequestParam(value="siteurl") String siteurl,ModelMap map,HttpServletRequest request)
	{
		String serverPath = UrlProvider.getServerPath(request);
	    map.put("serverPath", serverPath);
		map.put("siteurl", siteurl);
		return "site/summary";
	}*/
	
	@ModelAttribute
	private SiteConfigModel init(){
		
		SiteConfigModel siteConfigModel = new SiteConfigModel();
		BindException errors = new BindException(new Object(), "accounts");
		NamespaceManager.set(null);
		try {
		    siteConfigModel.setAccountNames(accountService.getAllSiteAdmins());
		}catch(AccountsException accountsException){
			log.log(Priority.DEBUG,"Error: getting all admins",accountsException);
			errors.reject(accountsException.getMessage());
		}
		try {
			siteConfigModel.setThemes(shopInitVosaoAdapter.getAllThemes());
		}catch(VosaoException vosaoException) {
			log.log(Priority.DEBUG,"Error: getting all themes",vosaoException);
			errors.reject(vosaoException.getMessage());
		}
		return siteConfigModel;
	}
	
	@RequestMapping(value="/validate_shopId.htm",method=RequestMethod.GET)
	@ResponseBody
	public String isAvailableShopId(@RequestParam(value="shopId") String shopId) {
		
		log.debug("########Inside isValidateShopeId...shopId="+shopId);
		BindException errors = new BindException(new Object(), "accounts");
		boolean result = false;
		try {
			result = shopService.checkShopId(shopId);
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error: checking shopId:"+shopId,shopException);
	    	errors.reject(shopException.getMessage());
	    }
		if(result) {
			return "<h4 style='color:#0B0'>"+messageSource.getMessage("org.soc.shoppee.validate.available",null,Locale.UK)+"</h4";
		}
		else
		    return "<h4 style='color:#F00'>"+messageSource.getMessage("org.soc.shoppee.validate.unavailable",null,Locale.UK)+"</h4";
	}
	
	@RequestMapping(value="isconfig.htm",method=RequestMethod.POST)
	@ResponseBody
	public String isConfigureShop(@RequestParam("shopId") String shopId) {
		String result = "wait";
		shopInitVosaoAdapter.setCurrentNameSpace(shopId);
		int page_count = shopInitVosaoAdapter.countAllPages();
		if(page_count==SocConstant.DEFAULT_PAGE_COUNT) {
			result = "completed";
		 
		}
		return result;
	}
}
