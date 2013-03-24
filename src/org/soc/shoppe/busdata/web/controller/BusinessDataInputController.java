package org.soc.shoppe.busdata.web.controller;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.UrlProvider;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.busdata.exception.BusinessException;
import org.soc.shoppe.busdata.service.BusinessDataService;
import org.soc.shoppe.busdata.vo.BusinessVO;
import org.soc.shoppe.busdata.vo.ResourceVO;
import org.soc.shoppe.busdata.web.model.BusinessDataInputModel;
import org.soc.shoppe.busdata.web.model.BusinessModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value="soc/busdata")
public class BusinessDataInputController {
	
	Logger log = Logger.getLogger(BusinessDataInputController.class);
	
	@Inject
	private BusinessDataService businessDataService;
	

	//private static int 
	
	@RequestMapping(value="/view/root.htm",method=RequestMethod.GET)
	public String loadData(HttpSession session,ModelMap map,HttpServletRequest request){
		
		String result = "account/login";
		int status = 0;
		BindException errors = new BindException(new Object(), "business");
		AccountVO accountVO = session.getAttribute("accountVO")==null ? null : (AccountVO) session.getAttribute("accountVO");
		BusinessVO businessVO = session.getAttribute("businessVO")==null ? null : (BusinessVO) session.getAttribute("businessVO");
		if(accountVO!=null && businessVO!=null){
			status = 1;
			String umode = (String) session.getAttribute("currentmode");
		    map.put("name", accountVO.getUsername());
	    	map.put("umode", umode);
	    	String serverPath = UrlProvider.getServicePath(request);
	    	map.put("serverPath", serverPath);
	    	String serverUrl = UrlProvider.getServerPath(request);
	    	map.put("serverUrl", serverUrl);
			///get shop type data from session
			//if session doest contain the semModel load it and save in session
			List<String> resources = businessDataService.listResourceNames(businessVO.getbusinessCode());
			if(resources!=null) {
				map.put("commonPrefix", "/soc/busdata/view/");
		    	map.put("page_include", "../busdata/listall.jspx");
		    	map.put("businesscode", businessVO.getbusinessCode());
				String pathtrace = "view/";
				map.put("pathtrace", pathtrace);
				map.put("current", "root");
				map.put("prev", "");
		        map.put("resources", resources);
		    	status = 2;
			}
				
			if(status == 1)
				result = "redirect: ../../tohome.htm";
			else if(status == 2)
	        	result = "account/adminhome";//"busdata/listall";	
	        else
	        	errors.reject("org.soc.common.error");
		}
		return result;
	}
	
	@RequestMapping(value="/view/**/{resourceId}",method=RequestMethod.GET)
	public String loadResource(@PathVariable("resourceId") String resourceId,@ModelAttribute("businessDataInputModel") BusinessDataInputModel businessDataInputModel,HttpServletRequest request,ModelMap map,HttpSession session){
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
	    map.put("name", accountVO.getUsername());
    	map.put("umode", umode);
    	String serverPath = UrlProvider.getServicePath(request);
    	map.put("serverPath", serverPath);
    	String serverUrl = UrlProvider.getServerPath(request);
    	map.put("serverUrl", serverUrl);
    	map.put("commonPrefix", "/soc/busdata/view/");
    	map.put("page_include", "../busdata/viewresource.jspx");  	
		ResourceVO resourceVO = businessDataService.loadResource(resourceId, "acadamy"); 
		businessDataInputModel.setResourceVO(resourceVO);
		businessDataInputModel.setCurrent(resourceId);
		String requestURI = request.getRequestURI();
		String temp = requestURI.replaceAll("/"+resourceId+".htm","");
		int index = temp.lastIndexOf("/")+1;
		String prev = temp.substring(index).trim();
		map.put("prev", prev);
		map.put("current", resourceId);
		return "account/adminhome";
	}
	
	@RequestMapping(value="/view/**/load_resource.htm",method=RequestMethod.POST)
	public String addResourseDetails(@ModelAttribute("businessDataInputModel") BusinessDataInputModel businessDataInputModel,HttpServletRequest request) {
		BusinessVO businessVO = request.getAttribute("shopbusiness")==null ? null : (BusinessVO) request.getAttribute("businessVO");
		businessDataInputModel.getResourceVO().setBusinessName(businessVO.getbusinessName());
		businessDataInputModel.getResourceVO().setShopId("test2");
		businessDataService.addResource(businessDataInputModel.getResourceVO());
		String currentUrl = request.getRequestURL().toString();
		String oldUrlPart = "/"+businessDataInputModel.getCurrent();
		String newUrl = currentUrl.replace(oldUrlPart, "").trim();
		return "redirect:"+newUrl;
	}

	public BusinessDataService getBusinessDataService() {
		return businessDataService;
	}

	public void setBusinessDataService(BusinessDataService businessDataService) {
		this.businessDataService = businessDataService;
	}
	

	@RequestMapping(value="/addbusiness.htm",method=RequestMethod.GET)
	public String viewaddBusiness(@ModelAttribute("businessModel") BusinessModel businessModel,HttpServletRequest request,ModelMap map,HttpSession session){
	
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		map.put("page_include","../site/addbusiness.jspx");
		return "account/adminhome";
	}
	
	@RequestMapping(value="/addbusiness.htm",method=RequestMethod.POST)
	public String addBusiness(@ModelAttribute("businessModel") BusinessModel businessModel,HttpServletRequest request,ModelMap map,HttpSession session){
		log.log(Priority.DEBUG,"inside addBusiness");
		boolean result=false;
		String resulturl=null;
		BindException errors = new BindException(new Object(), "Business");
		try{
		result= businessDataService.addBusiness(businessModel.getBusinessVO());
		log.log(Priority.DEBUG,"business saved status result:"+result);
	}catch (BusinessException businessException) {
		log.log(Priority.DEBUG,"catch businessException during addBusiness",businessException);
		errors.reject(businessException.getMessage());
		map.put("errors", errors);
	}
		if(result)
			map.put("page_include", "../site/businessinfo.jspx");
		else
			map.put("page_include", "../site/allbusiness.jspx");
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		return "account/adminhome";
	}
	
	@RequestMapping(value="/allbusiness.htm",method=RequestMethod.GET)
	public String viewallbusiness(@ModelAttribute("businessModel") BusinessModel businessModel,HttpServletRequest request,ModelMap map, HttpSession session, Errors errors){
		try{
		List<BusinessVO> businesses= businessDataService.loadAllBusiness();
		businessModel.setBusinesses(businesses);
		map.put("page_include", "../site/allbusiness.jspx");
		log.log(Priority.DEBUG, "ready to list businesses");
		}
		catch (BusinessException businessException) {
			log.log(Priority.DEBUG, "cannot find any list of businesses");
			errors.reject(businessException.getMessage());
			map.put("errors", errors);
		}
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		return "account/adminhome";
	}
	@RequestMapping(value="/businessinfo.htm",method=RequestMethod.GET)
	public String viewBusinessInfo(@RequestParam("businesscode") String businesscode, @ModelAttribute("businessModel") BusinessModel businessModel, HttpServletRequest request,ModelMap map,HttpSession session, Errors errors){
	
		try{
			BusinessVO businessVO=businessDataService.loadBusiness(businesscode);
			businessModel.setBusinessVO(businessVO);
			map.put("page_include", "../site/businessinfo.jspx");
			log.log(Priority.DEBUG,"ready to view businessInfo");
		}catch(BusinessException businessException){
			log.log(Priority.DEBUG,"unable to view themeInfo",businessException);
			errors.reject(businessException.getMessage());
			map.put("errors", errors);
		}

		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		return "account/adminhome";
	}
}
