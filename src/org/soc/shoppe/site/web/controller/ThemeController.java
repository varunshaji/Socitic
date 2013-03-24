package org.soc.shoppe.site.web.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.UrlProvider;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.site.exception.ThemeException;
import org.soc.shoppe.site.service.ThemeService;
import org.soc.shoppe.site.vo.ThemeVO;
import org.soc.shoppe.site.web.model.ThemeModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value="soc/theme")
public class ThemeController {
	
    Logger log = Logger.getLogger(ThemeController.class);
    
    @Inject
    private ThemeService themeService;
    		
	@RequestMapping(value="/addnew.htm",method=RequestMethod.GET)
	public String viewAddTheme(@ModelAttribute("themeModel") ThemeModel themeModel,HttpSession session,ModelMap map,HttpServletRequest request) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		map.put("page_include", "../site/addtheme.jspx");
		return "account/adminhome";
	}
	
	@RequestMapping(value="/addnew.htm",method=RequestMethod.POST)
	public String addTheme(@ModelAttribute("themeModel") ThemeModel themeModel,ModelMap map,HttpServletRequest request) {
		
		log.log(Priority.DEBUG,"inside addTheme");
		boolean result = false;
		String resultUrl = null;
		BindException errors = new BindException(new Object(), "Shop");
		try {
			result = themeService.addTheme(themeModel.getThemeVO());
			log.log(Priority.DEBUG,"theme saved status result:"+result);
		}catch (ThemeException themeException) {
			log.log(Priority.DEBUG,"catch themeException during addTheme",themeException);
			errors.reject(themeException.getMessage());
			map.put("errors", errors);
		}
		if(result)
			resultUrl = "redirect: themelist.htm";
		else
			resultUrl = "tohome.htm";
		return resultUrl;	
	}
	
	@RequestMapping(value="/themelist.htm",method=RequestMethod.GET)
	public String viewAllThemes(@ModelAttribute("themeModel") ThemeModel themeModel,ModelMap map,HttpServletRequest request,HttpSession session,Errors errors) {
		
		try{
			List<ThemeVO> themes = themeService.loadAllThemes();
			themeModel.setThemes(themes);
			map.put("page_include", "../site/themes.jspx");
			log.log(Priority.DEBUG,"ready to list themes");
		}catch(ThemeException themeException){
			log.log(Priority.DEBUG,"unable to view themeList",themeException);
			errors.reject(themeException.getMessage());
			map.put("errors", errors);
			map.put("page_include", "../site/createpage.jspx");
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
	
	@RequestMapping(value="/themeinfo.htm",method=RequestMethod.GET)
	public String viewThemeInfo(@RequestParam("themename") String themename,@ModelAttribute("themeModel") ThemeModel themeModel,ModelMap map,HttpServletRequest request,HttpSession session,Errors errors) {
		
		AccountVO accountVO = (AccountVO) session.getAttribute("accountVO");
		String umode = (String) session.getAttribute("currentmode");
		map.put("name", accountVO.getUsername());
		map.put("umode", umode);
		map.put("homeid", umode.toLowerCase());
		String serverPath = UrlProvider.getServicePath(request);
		map.put("serverPath", serverPath);
		try{
			ThemeVO themeVO = themeService.loadTheme(themename);
			themeModel.setThemeVO(themeVO);
			map.put("page_include", "../site/theme.jspx");
			log.log(Priority.DEBUG,"ready to view themeInfo");
		}catch(ThemeException themeException){
			log.log(Priority.DEBUG,"unable to view themeInfo",themeException);
			errors.reject(themeException.getMessage());
			map.put("errors", errors);
			map.put("page_include", "../site/createpage.jspx");
		}
		return "account/adminhome";
	}
	
	@RequestMapping(value="/preview.htm",method=RequestMethod.GET)
	public String viewTheme(@RequestParam("themename") String themename,ModelMap map) {
		
		String imageUrl = themeService.getThemeImage(themename);
		String filePath = themeService.getThemeFile(themename);
		log.log(Priority.DEBUG, "imageUrl:"+imageUrl);
		log.log(Priority.DEBUG, "filepath:"+filePath);
		map.put("imageUrl", imageUrl);
		map.put("filePath", filePath);
		return "test";
	}
	
	@RequestMapping(value="/readfile.htm",method=RequestMethod.GET)
	public String viewThemeFile(@RequestParam("themename") String themename,ModelMap map) {
		return null;
	}
}
