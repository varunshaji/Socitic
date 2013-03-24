package org.soc.shoppe.account.web.controller;

import java.security.Principal;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.SocConstant;
import org.soc.common.UrlProvider;
import org.soc.shoppe.account.exception.AccountsException;
import org.soc.shoppe.account.service.AccountService;
import org.soc.shoppe.account.vo.AccountVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.appengine.api.NamespaceManager;

@Controller
public class SigninController {
	
	Logger log = Logger.getLogger(SigninController.class);
	
	@Inject
	private AccountService accountService;
	
	@RequestMapping(value="/soc/security_login.htm", method=RequestMethod.GET)
	public String loginPg(@RequestParam(required=false,value="argument") String argument,ModelMap map) {
		BindException errors = new BindException(new Object(), "accounts");
		if(argument!=null)
		{
			if(argument.equals("illegal")) {
				errors.reject("org.soc.shoppee.argument.illegal");
				map.put("errors", errors);
			}
		}
		return "account/login";
	}
	
	@RequestMapping(value="/soc/login.htm")
    public String loginError(@RequestParam("login_error") String login_error,ModelMap map) {
		 BindException errors = new BindException(new Object(), "accounts");
		 if(login_error.equals("1")) {
			 errors.reject("org.soc.shoppee.account.login");
			 map.put("errors", errors);
		 }
		 return "account/login";
    }
	
	@RequestMapping(value="soc/selectuser.htm",method=RequestMethod.GET)
	public String userSelection(HttpSession session,ModelMap map,HttpServletRequest request,Principal principal)
	{
        String result = null;
        BindException errors = new BindException(new Object(), "accounts");
        String username  = principal.getName();
		if(NamespaceManager.get()!=SocConstant.SOC_NAMESPACE)
		   NamespaceManager.set(SocConstant.SOC_NAMESPACE);
	
		AccountVO accountVO = null;
		try {
			accountVO = accountService.validateUser(username);
		}catch(AccountsException accountsException) {
			log.log(Priority.DEBUG,"Error: validating account "+username,accountsException);
			errors.reject(accountsException.getMessage());
		}
		if(accountVO!=null) {
			if(accountVO.isEnabled()) {
				session.setAttribute("accountVO", accountVO);
				String serverPath = UrlProvider.getServicePath(request);
			    map.put("serverPath", serverPath);
			    map.put("name", accountVO.getUsername());
				result =  "account/userselection";
			}else{
				errors.reject("org.soc.shoppee.account.inactive");
				 map.put("errors", errors);
				 result = "account/login";
			}
			
		}else {
			errors.reject("org.soc.shoppee.account.failed");
			map.put("errors", errors);
			result = "account/login";
		}
		return result;
	}
	
	@RequestMapping(value="soc/logout.htm",method=RequestMethod.GET)
	public String logOut(HttpSession session) {
		
		/*NamespaceManager.set(null);
		session.removeAttribute("accountVO");
		session.removeAttribute("shop");
		session.invalidate();*/
		return "redirect:j_spring_security_logout";
	}
}
