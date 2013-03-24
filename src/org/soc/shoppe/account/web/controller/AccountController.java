package org.soc.shoppe.account.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
import org.soc.shoppe.account.web.model.AccountModel;
import org.soc.shoppe.site.shopInitVosaoAdapter.ShopInitVosaoAdapter;
import org.soc.shoppe.site.shopInitVosaoAdapter.VosaoException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="soc/account")
public class AccountController {
	
	Logger log = Logger.getLogger(AccountController.class);
	
	@Inject
	private AccountService accountService;
	
	@Inject 
	private ShopInitVosaoAdapter shopInitVosaoAdapter;
	
	@Inject
	private ResourceBundleMessageSource messageSource;

	@RequestMapping(value="/add.htm",method=RequestMethod.GET)
	public String accountPg(@ModelAttribute("accountModel") AccountModel accountModel,ModelMap map,HttpSession session,HttpServletRequest request)
	{
		accountModel.setRoles(Arrays.asList("ROLE_ADMIN"));
		//return "account/newuser";
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		boolean valid = false;
		for(RoleVO roleVO : accountVO.getRoles()) {
			if(roleVO.getRole().equals(umode))
			  valid = true;
		}
		if(valid) {
			map.put("name", accountVO.getUsername());
			map.put("umode", umode);
			map.put("homeid", umode.toLowerCase());
	        if (umode.equals("ROLE_ADMIN")) {
				
				String serverPath = UrlProvider.getServicePath(request);
				map.put("serverPath", serverPath);
				map.put("accountModel", accountModel);
				map.put("page_include", "newuser.jspx");
			}
	        else {
	        	map.put("errors", "org.soc.shoppee.account.add");
	        	return "redirect:../security_login.htm";
	        }
		}
		else {
			map.put("errors", "org.soc.shoppee.account.add");
			return "redirect:../security_login.htm";
		}
        return "account/adminhome";
	}
	
	/*@RequestMapping(value="/contactinfo",method=RequestMethod.GET)
	public String contactPg(@ModelAttribute("accountModel") AccountModel accountModel,ModelMap map)
	{
		accountModel.setRoles(Arrays.asList("ADMIN","NORMAL"));
		map.put("accountModel", accountModel);
		return "account/account";
		return "account/account_contact";
	}
	
	@RequestMapping(value="/addconfirm",method=RequestMethod.GET)
	public String confirmPg(@ModelAttribute("accountModel") AccountModel accountModel,ModelMap map)
	{
		accountModel.setRoles(Arrays.asList("ADMIN","NORMAL"));
		map.put("accountModel", accountModel);
		return "account/account";
		return "account/account_confirm";
	}*/
	
	/**
	 * 
	 * This method adds new user account. and calls {@link SiteInitService} for signing in with new user
	 * account  
	 * 
	 * @since 2/8/2011
	 * @author Girish
	 * @param {@link AccountModel}  
	 * @return 
	 * 
	 */	
	
	@RequestMapping(value="add.htm",method=RequestMethod.POST)
	public String addNewAccount(@ModelAttribute("accountModel") AccountModel accountModel,ModelMap map,HttpSession session,Errors errors,HttpServletRequest request)
	{
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		String result = null;
		boolean status = false;
	    map.put("name", accountVO.getUsername());
	    map.put("umode", umode);
		AccountVO newAccountVO = accountModel.getAccountVO();
		accountVO.setEnabled(false);               //################### user disabled
		try {
			status = accountService.createAccount(newAccountVO);
		}catch(AccountsException accountsException) {
			log.log(Priority.DEBUG,"Error: Add new account:",accountsException);
			errors.reject(accountsException.getMessage());
		}
	    
	    if(status && newAccountVO.getRoles().get(0).getRole().equals("ROLE_ADMIN")) {
	    	try{
	    		shopInitVosaoAdapter.adminCmsSignup(newAccountVO);
	    		map.put("accountModel", accountModel);
	    		map.put("page_include", "user.jspx");
	    	}catch(VosaoException vosaoException) {
	    		log.log(Priority.DEBUG,"Error: cms signup",vosaoException);
	    		errors.reject(vosaoException.getMessage());
	    	}
	    }
	    String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
	    map.put("profilemode", true);
        map.put("errors", errors);
		return "account/adminhome";
	}
	
	@RequestMapping(value="/validate_uname.htm",method=RequestMethod.GET)
	@ResponseBody
	public String isAvalilableUsername(@RequestParam(value="username") String username) {
		boolean result = false;
		BindException errors = new BindException(new Object(), "accounts");
		try{
			result = accountService.checkUsername(username);
		}catch(AccountsException accountsException) {
			log.log(Priority.DEBUG,"Error: validating username",accountsException);
			errors.reject(accountsException.getMessage());  			//#####error not mapped
		}
		if(result)
			return "<h4 style='color:#0B0'>"+messageSource.getMessage("org.soc.shoppee.validate.available",null,Locale.UK)+"</h4";
		else
			return "<h4 style='color:#F00'>"+messageSource.getMessage("org.soc.shoppee.validate.unavailable",null,Locale.UK)+"</h4";
	}
	
	@RequestMapping(value="/validate_email.htm",method=RequestMethod.GET)
	@ResponseBody
	public String isAvalilableEmail(@RequestParam(value="email") String email) {
		boolean result = false;
		BindException errors = new BindException(new Object(), "accounts");
		try {
			result = accountService.checkEmail(email);
		}catch(AccountsException accountsException) {
			log.log(Priority.DEBUG,"Error: validating email",accountsException);
			errors.reject(accountsException.getMessage());       //#####error not mapped
		}
		if(result)
			return "available";
		else
		    return "already exist";
	}
	
	@RequestMapping(value="/users.htm",method=RequestMethod.GET)
	public String allUsers(@ModelAttribute("accountModel") AccountModel accountModel,ModelMap map,HttpSession session,Errors errors,HttpServletRequest request) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		List<AccountVO> accountVOs = new ArrayList<AccountVO>();
	    map.put("name", accountVO.getUsername());
	    map.put("umode", umode);    
		try {
			accountVOs = accountService.loadAllAccounts();
		}catch(AccountsException accountsException) {
			log.log(Priority.DEBUG,"Error: loading account",accountsException);
			errors.reject(accountsException.getMessage());
		}
		accountModel.setAccounts(accountVOs);
		String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
	    map.put("page_include", "users.jspx");
        map.put("errors", errors);
		return "account/adminhome";
	}
	
	@RequestMapping(value="/edituser.htm",method=RequestMethod.GET)
	public String editAccountRender(@RequestParam("user") String username,@ModelAttribute("accountModel") AccountModel accountModel,ModelMap map,HttpSession session,HttpServletRequest request,Errors errors) {
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
	    map.put("name", accountVO.getUsername());
	    map.put("umode", umode);    
		try {
			accountModel.setAccountVO(accountService.loadAccountVO(username));
			map.put("accountModel", accountModel);
    		map.put("page_include", "edituser.jspx");
		}catch(AccountsException accountsException) {
			log.log(Priority.DEBUG,"Error: loading account",accountsException);
			errors.reject(accountsException.getMessage());
		}
	    String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
	    map.put("profilemode", true);
        map.put("errors", errors);
		return "account/adminhome";
	}
	
	@RequestMapping(value="/edituser.htm",method=RequestMethod.POST)
	public String editAccount(@ModelAttribute("accountModel") AccountModel accountModel,ModelMap map,HttpSession session,HttpServletRequest request,Errors errors) {
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		AccountVO accountVO2 = null;
		String umode = (String) session.getAttribute("currentmode");
	    map.put("name", accountVO.getUsername());
	    map.put("umode", umode);    
		try {
			accountVO2 = accountService.modifyAccount(accountModel.getAccountVO());
			accountModel.setAccountVO(accountVO2);
			map.put("accountModel", accountModel);
    		map.put("page_include", "user.jspx");
		}catch(AccountsException accountsException) {
			log.log(Priority.DEBUG,"Error: editing account",accountsException);
			errors.reject(accountsException.getMessage());
		}
	    String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
	    map.put("profilemode", true);
        map.put("errors", errors);
		return "account/adminhome";
	}
	
	@RequestMapping(value="/user.htm",method=RequestMethod.GET)
	public String viewUser(@ModelAttribute("accountModel") AccountModel accountModel,@RequestParam("uname") String username,ModelMap map,HttpSession session,Errors errors,HttpServletRequest request) {
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		String result = null;
		boolean status = false;
	    map.put("name", accountVO.getUsername());
	    map.put("umode", umode);
	    AccountVO accountVO2 = null;
	    try {
	    	accountVO2 = accountService.loadAccountVO(username);
	    }catch(AccountsException accountsException) {
	    	log.log(Priority.DEBUG,"Error: loading account",accountsException);
	    	errors.reject(accountsException.getMessage());
	    }
	    accountModel.setAccountVO(accountVO2);
	    map.put("accountModel", accountModel);
		map.put("page_include", "user.jspx");
	    String serverPath = UrlProvider.getServicePath(request);
	    map.put("serverPath", serverPath);
	    map.put("profilemode", true);
        map.put("errors", errors);
		return "account/adminhome";
	}
}
