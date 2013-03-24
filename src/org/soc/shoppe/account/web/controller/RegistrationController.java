/**
 * 
 * @author Girish
 * 
 * This is the main controller for handling all the open site
 * activities of socitic such a user registration,shop creation,
 * user validations etc. It uses the google recaptcha api to identify
 * valid users.
 */
package org.soc.shoppe.account.web.controller;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.JSONException;
import org.json.JSONObject;
import org.soc.common.SocConstant;
import org.soc.common.SocEmailManager;
import org.soc.common.UrlProvider;
import org.soc.common.gaeBlobstore.ChannelAPI;
import org.soc.common.sms.service.SmsService;
import org.soc.shoppe.account.exception.AccountsException;
import org.soc.shoppe.account.service.AccountService;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.RoleVO;
import org.soc.shoppe.account.vo.ValidateEmailVO;
import org.soc.shoppe.account.web.model.RegistrationModel;
import org.soc.shoppe.busdata.exception.BusinessException;
import org.soc.shoppe.busdata.service.BusinessDataService;
import org.soc.shoppe.busdata.vo.BusinessVO;
import org.soc.shoppe.site.exception.ShopException;
import org.soc.shoppe.site.exception.ThemeException;
import org.soc.shoppe.site.facade.ShopFacade;
import org.soc.shoppe.site.service.ShopService;
import org.soc.shoppe.site.service.ThemeService;
import org.soc.shoppe.site.shopInitVosaoAdapter.ShopInitVosaoAdapter;
import org.soc.shoppe.site.shopInitVosaoAdapter.VosaoException;
import org.soc.shoppe.site.vo.ShopVO;
import org.soc.shoppe.site.vo.ThemeVO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
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

import com.google.appengine.api.urlfetch.HTTPResponse;

@Controller
public class RegistrationController {
	Logger log = Logger.getLogger(RegistrationController.class);
	
	@Inject
	private AccountService accountService;
	
	@Inject
	private ShopFacade shopFacade;
	
	@Inject
	private ShopService shopService;
	
	@Inject
	private ThemeService themeService;
	
	@Inject
	private ShopInitVosaoAdapter shopInitVosaoAdapter;
	
	@Inject
	private BusinessDataService businessDataService;
	
	@Inject
	private SmsService smsService;
	
	static BytesKeyGenerator bytesKeyGenerator =  KeyGenerators.secureRandom(16);
	
	@RequestMapping(value="/soc/main.htm",method=RequestMethod.GET)
	public String viewRegstration(@ModelAttribute("registrationModel") RegistrationModel registrationModel) {
		return "main/soc_main";
	}
	
	@RequestMapping(value="/soc/validate_user.htm",method=RequestMethod.POST)
	@ResponseBody
	public String checkInputFormat(@RequestParam(required=false,value="key",defaultValue="") String key,@RequestParam(required=false,value="value",defaultValue="") String value,HttpServletRequest request) { 
		String result = null;
		if("".equals(key) || "".equals(value))
			return "ivalid";
		if("email".equals(key)){
			if(!SocEmailManager.isEmailValid(value)) {
				result = "invalid";
			}else {
				AccountVO accountVO = null;
				try {
					accountVO = accountService.loadAccountByEmail(value);
				}catch(AccountsException accountsException) {
					log.info("error occured during retrieving account:"+value);
					result = "incomplete";
				}
				if(accountVO == null)
					result = "valid";
				else
					result = "exist";
			}
		}else if("sms".equals(key)){
			result = "valid";
		}else{
			result = "invalid";
		}
		return result;
	}
	
	@RequestMapping(value="/soc/recaptcha.htm",method=RequestMethod.POST)
	@ResponseBody
	public String validateRecaptcha(HttpServletRequest request) {
		
		String remoteAddr = request.getRemoteAddr();
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey("6LeA5MkSAAAAAOLexEHSXvxeym0ZwhpwcSmStunL");

        String challenge = request.getParameter("recaptcha_challenge_field");
        String uresponse = request.getParameter("recaptcha_response_field");
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

        if (reCaptchaResponse.isValid()) {
          return "success";
        } else {
          return "Answer is wrong";
        }
	}
	
	@RequestMapping(value="/soc/sendkey.htm",method=RequestMethod.POST)
	@ResponseBody
	public String sendMail(@RequestParam(required=false,value="key",defaultValue="") String key,@RequestParam(required=false,value="value",defaultValue="") String value,@RequestParam(required=false,value="location") String location,@RequestParam(required=false,value="lat") String latitude,@RequestParam(required=false,value="lng") String longitude,HttpSession session,HttpServletRequest request) {
		boolean status = false;
		AccountVO accountVO = null;
		ValidateEmailVO validateEmailVO = null;
		String validationKey = null;
		session.setAttribute("latitude", latitude);
		session.setAttribute("longitude", longitude);
		if("".equals(key) || "".equals(value)){
			log.log(Priority.DEBUG, "key="+key+" value="+value+"...failed");
			return "failed";
		}
		try {
			accountVO = "sms".equals(key) ? accountService.loadAccountByMobile(value) : accountService.loadAccountByEmail(value);
			if(accountVO == null) {		
				try {
					validateEmailVO = accountService.loadValidationEmailVOByValue(key, value);
				}catch(AccountsException accountsException) {
					log.info("error occured during retrieval of validation Key:"+value);
					log.log(Priority.DEBUG,"Error please",accountsException);
				}
				if(validateEmailVO == null) {
					byte[] byteKey = bytesKeyGenerator.generateKey();
					StringBuffer buffer = new StringBuffer();
					for(int i=0;i<byteKey.length;i++) {
						buffer.append(Integer.toString((byteKey[i] & 0xff) + 0x100,16).substring(1));
					}
					validationKey = buffer.toString();
					try {
						validateEmailVO = new ValidateEmailVO();
						if("sms".equals(key))  
							validateEmailVO.setSms(value); 
						else 
							validateEmailVO.setEmail(value);
						validateEmailVO.setValidationKey(validationKey);
						validateEmailVO.setLocation(location);
						validateEmailVO.setNo_of_attempt(1);
						status = accountService.setEmailValidation(validateEmailVO);
						log.log(Priority.DEBUG, "error occured while saving emailvalidationVO..result="+status);
					}catch(AccountsException accountsException) {
						log.info("error occured during save alidation Key:"+value);
						log.log(Priority.DEBUG,"Error please",accountsException);
					}
				}else if(validateEmailVO.getNo_of_attempt()<=2  && "sms".equals(key)){
					int count = validateEmailVO.getNo_of_attempt();
					validateEmailVO.setNo_of_attempt(++count);
					status = accountService.updateValitationDetails(validateEmailVO);
					validationKey = validateEmailVO.getValidationKey();
				}else if("sms".equals(key)){
					return "blocked";
				}else if("email".equals(key)) {
					int count = validateEmailVO.getNo_of_attempt();
					validateEmailVO.setNo_of_attempt(++count);
					status = accountService.updateValitationDetails(validateEmailVO);
					validationKey = validateEmailVO.getValidationKey();
				}else
					return "failed";
			}else
				return "exist";
		}catch(AccountsException accountsException) {
			log.info("error occured during send key at controller:"+value);
			return "incomplete";
		}
		if(status) {
			status = false;
			status = "sms".equals(key) ? sendSms(value, validationKey) : sendEmail(value, validationKey, request);
			log.log(Priority.DEBUG, "sms/email send ..+result="+false);
		}
		if(status) {
		   log.debug("######<<<<<validatonkey for "+value+" is "+validationKey+" >>>>>>###");
		   String url =  "<div id='pinner'\"><table cellspacing=\"0\" cellpadding=\"5\" width=\"530\"><tr><td valign='top' class='col1'><img src='../../../static/resources/images/main/3.png' alt='' /></td><td valign='top' class='col2'><b>Enter Your<br />Validation Key</b></td><td valign='top' width='160' class='col3'><input id=\"vKey\" type='text' name='vKey' value=\"\"/></td><td valign='top' class='col4'><a rel=\"coda-slider-1\" href=\"#\"  id=\"checkVal\"><img	src='../../../static/resources/images/main/continue.png' alt='Next step' onmouseover=\"this.src='../../../static/resources/images/main/continue1.png'\"	onmouseout=\"this.src='../../../static/resources/images/main/continue.png'\" /></a></td></tr></table><div id='status_bar'><img src='../../../static/resources/images/main/ajax-loadbar.gif' /></div></div>";
		   return url;
		}
		else{
			log.log(Priority.DEBUG, "failed due to server side exception");
			return "failed";
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/soc/retry.htm",method=RequestMethod.POST)
	public String reSendKey(@RequestParam(required=false,value="key",defaultValue="") String key,@RequestParam(required=false,value="value",defaultValue="") String value,HttpServletRequest request){
		String result = "failed";
		boolean status = false;
		try{
			ValidateEmailVO validateEmailVO = accountService.loadValidationEmailVOByValue(key, value);
			if(validateEmailVO == null)
				result = "notfound";
			else if((validateEmailVO.getEmail() == null && validateEmailVO.getSms()==null)|| validateEmailVO.getValidationKey()==null || validateEmailVO.getNo_of_attempt()<=0)
				result = "staledata";
			else {
				int count = validateEmailVO.getNo_of_attempt();
				if((count<2) && "sms".equals(key)) {
					validateEmailVO.setNo_of_attempt(++count);
					status = accountService.updateValitationDetails(validateEmailVO);
					if(status){
						result = "success";
					}
				}else if("email".equals(key))
					result = "success";
				else
					result = "blocked";
			}
		}catch(AccountsException accountsException) {
			log.info("error occured during retrieving/updating validationVO by value:"+value);
			result = "incomplete";
		}
		return result;
	}
	
	private boolean sendSms(String number,String validationKey){
		boolean result = false;
		String content = null;
		String start = "Welcome to socitic. '";
		String body = validationKey;
		String end = "' is your validation key. Thank you for showing interest with us.";
		content = start + body + end;
		try{
			smsService.sendSms(number, content);
			result = true;
		}catch (Exception exception) {
			log.log(Priority.DEBUG, "Sms sending process failed", exception);
		}
		return result;
	}
	
	private boolean sendEmail(String email,String validationKey,HttpServletRequest request){
		boolean result = false;
		try {
			Properties properties = new Properties();
			Session session = Session.getDefaultInstance(properties,null);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SocConstant.SOC_EMAIL));
			log.log(Priority.DEBUG, "Email sender : "+SocConstant.SOC_EMAIL);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			log.log(Priority.DEBUG, "Email receiver : "+email);
			message.setSubject(SocConstant.SOC_MAIL_SUBJECT);
			
		    StringBuffer content = new StringBuffer();
			content.append("<p>Hi..<br />");
			content.append("Thank you for showing interest with us...<br />");
			content.append("your email verification code is: "+validationKey+" \n");
			content.append("Click the link below to verify your email address and continue to create your website or copy and paste the validation key to the validation field in our website.<br />");
			String siteurlPath = UrlProvider.getServerPath(request);
			String validationLink = siteurlPath+SocConstant.SOC_REGISTRATION_URL+"?value="+email+"&key=email&validationKey="+validationKey;
			content.append("<a href=\""+validationLink+"\">"+validationLink+"</a> <br /><br /></p>" );
			content.append("<p>Our e-mail verification is intended to confirm that the email you entered is authentic.<br />");
			content.append("Thanks for your time with <a href=\""+siteurlPath+SocConstant.SOC_URL+"\">socitic</a>...");
			content.append("<br /><br /> For queries contact us: sociticindia@gmail.com</p>");
			
			message.setContent(content.toString(), "text/html");
			Transport.send(message);
			result = true;
		}catch(AddressException addressException) {
			log.log(Priority.DEBUG,"Error occured: addressException in java mail:"+email,addressException);
			
		}catch(MessagingException messagingException) {
			log.log(Priority.DEBUG,"Error occured: messsagingException in java mail:"+email,messagingException);
		}
		return result;
	}
	
	@RequestMapping(value="/soc/validate.htm",method=RequestMethod.POST)
	@ResponseBody
	public String checkvalidationKey(@RequestParam(value="validationKey",defaultValue="") String validationKey,@RequestParam(required=false,value="value",defaultValue="") String value,@RequestParam(required=false,value="key",defaultValue="") String key) {
		boolean result = false;
		if(!"".equals(validationKey) && !("".equals(key) && "".equals(value))) {
			try {
				result = accountService.checkEmailValidationKey(key,value,validationKey);
			}catch(AccountsException accountsException) {
				log.log(Priority.DEBUG,"Error please",accountsException);
			}
		}
		if(result)
			return "success";
		else
		    return "failed";
	}
	
	@RequestMapping (value="/soc/entrypoint.htm",method=RequestMethod.POST)
	public String shopCreateEntryPoint(ModelMap map,@RequestParam(required=false,value="value",defaultValue="") String value,@RequestParam(required=false,value="key",defaultValue="") String key,@RequestParam(required=false,value="validationKey",defaultValue="") String validationKey) {
		String resultUrl = null;
		BindException errors = new BindException(new Object(), "accounts");
		if(validationKey=="" || ("".equals(key) || "".equals(value))) {
			errors.reject("org.soc.main.invalid");
			map.put("errors", errors);
			resultUrl = "main/soc_main";
		}
		else {
			resultUrl = "redirect: newshop.htm?validationKey="+validationKey+"&key="+key+"&value="+value;
		}
		return resultUrl;
	}
	
	@RequestMapping(value="/soc/newshop.htm",method=RequestMethod.GET) 
	public String viewRegistartionPage(ModelMap map,@RequestParam(value="value",required=false,defaultValue="") String value,@RequestParam(required=false,value="key",defaultValue="") String key,@RequestParam(value="validationKey",defaultValue="") String validationKey,@ModelAttribute("registrationModel") RegistrationModel registrationModel,HttpSession session,Errors errors) {
		String resultUrl = null;
		boolean result = false;
		String mode = null;
		ValidateEmailVO validateEmailVO = null;
		if(!"".equals(validationKey) && !("".equals(key) && "".equals(value))) {
			try {
				validateEmailVO = accountService.loadValidateEmailDataAfterValidation(key,value,validationKey);//accountService.checkEmailValidationKey(email, validationKey);
				if(validateEmailVO != null)
					result=true;
				if(!result)
				  errors.reject("org.soc.main.invalid");
			}catch(AccountsException accountsException) {
				log.log(Priority.DEBUG,"Error please",accountsException);
				errors.reject(accountsException.getMessage());
			}
			if(result) {
				result = false;
				try {
					List<ThemeVO> themes = themeService.loadAllThemes();
					List<String> images = new ArrayList<String>();
					for(ThemeVO theme : themes) {
						images.add(theme.getImageUrl());
					}
					if(images!=null) {
						registrationModel.setThemeImages(images);
						result = true;
					}
					if(!result)
						errors.reject("org.soc.common.error");
					if(result){
							List<BusinessVO> businessVOs = businessDataService.loadAllBusiness();
						    if(businessVOs != null){
						    	 registrationModel.setBusinesses(businessVOs);
						    	 log.log(Priority.DEBUG,"empty bussiness list");
						    }else
						    	result=false;
					}
				}catch(ThemeException themeException) {
					log.log(Priority.DEBUG,"Error to load all themes",themeException);
					errors.reject(themeException.getMessage());
				}catch (BusinessException businessException) {
					log.log(Priority.DEBUG,"Error to load all businesses",businessException);
				}
			}
		}
		if(result) {
			if("sms".equals(key))
				registrationModel.getAccountVO().setMobile(value);
			else
				registrationModel.getAccountVO().setEmail(value);
			registrationModel.getShopVO().setLocation(validateEmailVO.getLocation());
			registrationModel.getShopVO().setLatitude(session.getAttribute("latitude").toString());
			registrationModel.getShopVO().setLongitude(session.getAttribute("longitude").toString());
			String channelKey = ChannelAPI.getChannelKey(value);
			registrationModel.setChannelKey(channelKey);
			map.put("validation_mode", key);
		    map.put("token", ChannelAPI.getToken(channelKey));
			resultUrl = "main/soc_registration";
		}
		else {
			map.put("errors", errors);
			resultUrl = "main/soc_main";
		}
		return resultUrl;
	}
	
	@RequestMapping(value="/soc/newshop.htm",method=RequestMethod.POST, produces="application/json")
	
	public ResponseEntity<String> sendRegistrationMail(ModelMap map,@Valid @ModelAttribute("registrationModel") RegistrationModel registrationModel,BindingResult error,HttpSession session/*, HttpServletResponse response*/) throws VosaoException {
		//response.setContentType("application/json; charset=utf-8");
		BindException errors = new BindException(new Object(),"account");
		String message = null;
		if(error.hasErrors()) {
			/*String resultUrl = null;
			boolean result = false;
			try {
				List<ThemeVO> themes = themeService.loadAllThemes();
				List<String> images = new ArrayList<String>();
				for(ThemeVO theme : themes) {
					images.add(theme.getImageUrl());
				}
				if(!images.isEmpty()) {
					registrationModel.setThemeImages(images);
					result = true;
				}
				if(!result)
					errors.reject("org.soc.common.error");
			}catch(ThemeException themeException) {
				log.log(Priority.DEBUG,"Error to load all themes",themeException);
				errors.reject(themeException.getMessage());
			}
			if(result) {
				errors.reject("server.validation.error");
				map.put("errors", errors);
				resultUrl = "main/soc_registration";
			}
			else {
				map.put("errors", errors);
				resultUrl = "main/soc_main";
			}
			
			return resultUrl;*/
			errors.reject("server.validation.error");
			message = "error";
		}else{
			boolean result = false;
			boolean validShopId = false;
			boolean newRequest = false;
			AccountVO accountVO = registrationModel.getAccountVO();
			AccountVO userAccount = null;
			ShopVO shopVO = registrationModel.getShopVO();
			if(shopVO!=null) {
				try{
					validShopId = shopService.checkShopId(shopVO.getShopId());
				}catch(NullPointerException nullPointerException) {
					log.log(Priority.DEBUG,"Error:nullpointer when checking shopId",nullPointerException);
				}catch(ShopException shopException) {
					log.log(Priority.DEBUG,"Error: checking shopId",shopException);
			    	errors.reject(shopException.getMessage());
			    }
			}
			//Checking if the account is new one. if its a new account then create the shop...
			//Else the shop is not created..####TODO else condition
			try {
				userAccount = accountService.loadAccountByEmail(accountVO.getEmail());
				if(userAccount != null){
					message = "refresh";
				}
				else
					newRequest = true;
			}catch(AccountsException accountsException) {
				log.info("error occured during retrieving account:"+accountVO.getEmail());
				errors.reject(accountsException.getMessage());
			}
			
			//Going to register the shop to the account and create the shop via adapter
			if(accountVO!=null && shopVO!=null && validShopId && newRequest) {
				RoleVO roleVO = new RoleVO("ROLE_SITE-ADMIN");
				accountVO.getRoles().add(roleVO);
				accountVO.setEnabled(true);
				try {
					result=shopFacade.registerAccountAndShop(shopVO, accountVO);
				}catch(ShopException shopException) {
					log.log(Priority.DEBUG,"Error please",shopException);
					errors.reject(shopException.getMessage());
				}
				
				if(result) {
					result = false;
					try {
						String shopId = shopVO.getShopId();
						shopInitVosaoAdapter.setCurrentNameSpace(shopId);
						shopInitVosaoAdapter.setChannelKey(registrationModel.getChannelKey());
						List<AccountVO> accounts = new ArrayList<AccountVO>();
						accounts.add(accountVO);
						result = shopInitVosaoAdapter.createCmsPage(shopVO, accounts, SocConstant.SOC_DEFAULT_ADMIN_EMAIL);
					}catch(VosaoException vosaoException) {
						log.log(Priority.DEBUG,"Error please",vosaoException);
						errors.reject(vosaoException.getMessage());
					}
				}
			}else if(!validShopId) {
				errors.reject("validation.exist");
				message = "error_beHere";
			}
			if(result) {
				session.setAttribute("accountVO",accountVO);
				message = "success";
			}
			else {
				message = "error_toMain";
			}
		}
		JSONObject responsebody = new JSONObject();
		try {
			responsebody.put("error", errors);
			responsebody.put("message", message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json",Charset.forName("utf-8"));
		headers.setContentType(mediaType);
		
		return new ResponseEntity<String>(responsebody.toString(),headers,HttpStatus.OK);
	}
	
	@RequestMapping(value="/soc/newshopcontact.htm",method=RequestMethod.GET)
	public String pageRedirecter(ModelMap map) {
		BindException errors = new BindException(new Object(), "account");
		errors.reject("org.soc.main.invalid");
		map.put("errors", errors);
		return "main/soc_main";
	}

	
	@RequestMapping(value="/soc/newshopcontact.htm",method=RequestMethod.POST)
	public String finalRegistration(@ModelAttribute("registrationModel") RegistrationModel registrationModel,HttpServletRequest request,ModelMap map,HttpSession session,Errors errors) {
		
		String resultUrl = null;
		ShopVO shopVO = null;
		boolean result = false;
        try {
        	shopVO = registrationModel.getShopVO();
    	if(shopVO.getLatitude()==null && shopVO.getLongitude()==null){
    		shopVO.setLatitude(session.getAttribute("latitude").toString());
    		shopVO.setLongitude(session.getAttribute("longitude").toString());
    	}
    	shopVO = shopService.setShopContacts(shopVO);
		}catch(ShopException shopException) {
			log.log(Priority.DEBUG,"Error please",shopException);
	    	errors.reject(shopException.getMessage());
	    }
        if(shopVO!=null) {
        	/*if(shopVO.getLocation().isEmpty() || shopVO.getAddress().isEmpty()) {
        		errors.reject("server.validation.error");
        		map.put("errors", errors);
        		return "main/final_registration";
        	}*/
        	try {
        		session.setAttribute("current", shopVO.getShopId());
            	shopInitVosaoAdapter.setCurrentNameSpace(shopVO.getShopId());
            	result = shopInitVosaoAdapter.customiseShopSite(shopVO, SocConstant.SOC_DEFAULT_ADMIN_EMAIL);	
        	}catch(VosaoException vosaoException) {
        		log.log(Priority.DEBUG,"Error please",vosaoException);
        		errors.reject(vosaoException.getMessage());
        	}
        }
        if(result) {
    		registrationModel.setShopVO(shopVO);
			map.put("registrationModel", registrationModel);
			resultUrl = "main/social_connection";
    	}
    	else {
    	   map.put("errors", errors);
    	   resultUrl = "main/soc_main";
    	}
		return resultUrl;
	}
	
	@RequestMapping(value="/soc/social.htm",method=RequestMethod.POST)
	public String configSocialConnection(@ModelAttribute("registrationModel") RegistrationModel registrationModel,HttpServletRequest request,ModelMap map,HttpSession session,Errors errors) {
		String resultUrl = null;
		String shopid = registrationModel.getShopVO().getShopId();
		ShopVO shopVO = null;
		if(shopid != null){
	        try {
	        	shopVO = shopService.loadShop(shopid);
			}catch(ShopException shopException) {
				log.log(Priority.DEBUG,"Error during final shop retrieval",shopException);
		    }
		}
		if(shopVO == null){
			shopVO = new ShopVO();
	    	shopVO.setName("updates pending...");
	    	shopVO.setLocation("updates pending...");
	    	shopVO.setShopId(shopid);
		}
		boolean result = false;
    	try {
    		shopVO.setFacebookId(registrationModel.getShopVO().getFacebookId());
    		shopVO.setTwitterId(registrationModel.getShopVO().getTwitterId());
    		shopVO.setgPlusId(registrationModel.getShopVO().getgPlusId());
    		shopVO.setFlickrId(registrationModel.getShopVO().getFlickrId());
    		shopVO.setFlickrPhotosetId(registrationModel.getShopVO().getFlickrPhotosetId());
    		session.setAttribute("current", shopVO.getShopId());
        	shopInitVosaoAdapter.setCurrentNameSpace(shopVO.getShopId());
        	result = shopInitVosaoAdapter.configureSocialConnection(shopVO, SocConstant.SOC_DEFAULT_ADMIN_EMAIL);	
    	}catch(VosaoException vosaoException) {
    		log.log(Priority.DEBUG,"Error please",vosaoException);
    		errors.reject(vosaoException.getMessage());
    	}
    	
        if(!result) {
        	errors.reject("org.soc.common.socialnetwork.interrupt");
        	map.put("errors", errors);
    	}
        String siteurlPath = UrlProvider.getServerPath(request);
        AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
        if(accountVO!=null)
        	sendConfirmationMail(shopVO.getShopId(),accountVO,request);
		registrationModel.setShopVO(shopVO);
		map.put("siteurl", shopVO.getShopId());
		map.put("siteurlPath", siteurlPath);
		map.put("mainsite", siteurlPath+SocConstant.SOC_URL);
		map.put("registrationModel", registrationModel);
		resultUrl = "main/finish_registration";
		return resultUrl;
	}
	
	@RequestMapping(value="/soc/skip.htm",method=RequestMethod.POST)
	public String skipSocialConfig(@ModelAttribute("registrationModel") RegistrationModel registrationModel,HttpServletRequest request,ModelMap map,Errors errors,HttpSession session){
		
		String siteurlPath = UrlProvider.getServerPath(request);
		String shopid = registrationModel.getShopVO().getShopId();
		if(shopid==null)
			shopid = (String) session.getAttribute("current");
		ShopVO shopVO = null;
		if(shopid != null){
	        try {
	        	shopVO = shopService.loadShop(shopid);
			}catch(ShopException shopException) {
				log.log(Priority.DEBUG,"Error during final shop retrieval",shopException);
		    }
		}
		if(shopVO == null){
			shopVO = new ShopVO();
	    	shopVO.setName("updates pending...");
	    	shopVO.setLocation("updates pending...");
	    	shopVO.setShopId(shopid);
		}
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
        if(accountVO!=null)
        	sendConfirmationMail(shopVO.getShopId(),accountVO,request);
        registrationModel.setShopVO(shopVO);
		map.put("siteurl", shopVO.getShopId());
		map.put("siteurlPath", siteurlPath);
		map.put("mainsite", siteurlPath+SocConstant.SOC_URL);
		map.put("registrationModel", registrationModel);
		return "main/finish_registration";
	}
	
	private void sendConfirmationMail(String domain,AccountVO accountVO,HttpServletRequest request){
		String email = accountVO.getEmail();
		String user = accountVO.getFirstName()+" "+accountVO.getLastName();
		try {
			
			Properties properties = new Properties();
			Session session = Session.getDefaultInstance(properties,null);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SocConstant.SOC_EMAIL));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject(SocConstant.SOC_MAIL_SUBJECT);
			
		    StringBuffer content = new StringBuffer();
			content.append("<p>Hello "+user+"..<br />");
			content.append("Thank you for using socitic...<br />");
			String siteurlPath = UrlProvider.getServerPath(request);
			content.append("Now visit your shop using the url : <a href=\""+siteurlPath+"/?shop="+domain+"\">"+siteurlPath+"/?shop="+domain+"</a><br />");
			content.append("your socitic username : "+accountVO.getUsername()+" and password: "+accountVO.getPassword()+"<br />");
			content.append("To customise your website,visit the cms @: <a href=\""+siteurlPath+"/login.vm?shop="+domain+"\">"+siteurlPath+"/login.vm?shop="+domain+"</a><br />");
			content.append("Login to cms using your email and password<br />");
			content.append("To add/edit/remove your shop data,visit the soc admin site @ : <a href=\""+siteurlPath+SocConstant.SOC_URL+"\">"+siteurlPath+SocConstant.SOC_URL+"</a><br />");
			content.append("Login to socitic admin site using your username and password<br />");
			content.append("<br /><br /> For queries contact us: sociticindia@gmail.com</p>");
			
			message.setContent(content.toString(), "text/html");
			Transport.send(message);
			log.log(Priority.DEBUG,"successfully send confirmation mail");
		}catch(AddressException addressException) {
			log.log(Priority.DEBUG,"Error occured: addressException in java mail:"+email,addressException);
			
		}catch(MessagingException messagingException) {
			log.log(Priority.DEBUG,"Error occured: messagingException in java mail:"+email,messagingException);
		}
	}	
	
	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public ShopFacade getShopFacade() {
		return shopFacade;
	}

	public void setShopFacade(ShopFacade shopFacade) {
		this.shopFacade = shopFacade;
	}

	public ShopInitVosaoAdapter getShopInitVosaoAdapter() {
		return shopInitVosaoAdapter;
	}

	public void setShopInitVosaoAdapter(ShopInitVosaoAdapter shopInitVosaoAdapter) {
		this.shopInitVosaoAdapter = shopInitVosaoAdapter;
	}

	public ShopService getShopService() {
		return shopService;
	}

	public void setShopService(ShopService shopService) {
		this.shopService = shopService;
	}

	public BusinessDataService getBusinessDataService() {
		return businessDataService;
	}

	public void setBusinessDataService(BusinessDataService businessDataService) {
		this.businessDataService = businessDataService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public SmsService getSmsService() {
		return smsService;
	}
}
