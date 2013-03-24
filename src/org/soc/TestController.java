package org.soc;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.soc.common.exception.DaoException;
import org.soc.shoppe.site.dao.ThemeDao;
import org.soc.shoppe.site.vo.ThemeVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vosao.business.Business;
import org.vosao.common.VosaoContext;
import org.vosao.entity.UserEntity;
import org.vosao.service.BackService;
import org.vosao.service.FrontService;
import org.vosao.service.ServiceResponse;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.blobstore.BlobKey;

@Controller
public class TestController {
	
	private VosaoContext vosaoContext;
	
	@Inject
	private ThemeDao themeDao;
	
	public ThemeDao getThemeDao() {
		return themeDao;
	}

	public void setThemeDao(ThemeDao themeDao) {
		this.themeDao = themeDao;
	}

	@RequestMapping(value="test")
	public String test(){
		return "test";
	}
	
	BackService backService;
	
	FrontService frontService;
	
	Business business;

    public void init()
    {
     	
    	this.backService = VosaoContext.getInstance().getBackService();
    	
    	this.frontService = VosaoContext.getInstance().getFrontService();
    	
    	this.business = VosaoContext.getInstance().getBusiness();
    }
	
	@RequestMapping(value="/soc/tester.htm")
	public String createPage()
	{
		/*if(frontService == null || backService==null || business==null)
			init();
	//	NamespaceManager.set("zee");
		ServiceResponse logResponse = frontService.getLoginService().login("girish@test.com", "girish");
//	  UserEntity user =business.getDao().getUserDao().getByEmail(userEmail);
	//	UserEntity user = getDao().getUserDao().getByEmail(email);
        UserEntity user = business.getDao().getUserDao().getByEmail("admin@test.com");
		VosaoContext.getInstance().setUser(user);
		Map pageMap = new HashMap<String, String>();
		pageMap.put("title", "newone1sss");
	    pageMap.put("friendlyUrl", "/newone1sss");
	    pageMap.put("url", "newone1sss");
	    pageMap.put("id", "");
	 //   pageMap.put(key, value);
	    
	    ServiceResponse serviceResponse = backService.getPageService().savePage(pageMap);*/
	    
		try {
			ThemeVO themeVO = themeDao.getThemeByImageUrl("http://127.0.0.1:8889/_ah/img/JkQGkUdrnfQqMzZjlD68lw");
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:../cms/pages.vm";//?shop=zee";
	}
	
	@RequestMapping("/testlog")
	public String signUP()
	{
		if(frontService == null || backService == null || business==null)
			init();
		
		ServiceResponse logResponse = frontService.getLoginService().login("admin@test.com", "admin");
		
//		 UserEntity user = business.getDao().getUserDao().getByEmail("admin@test.com");
//			VosaoContext.getInstance().setUser(user);
		
		Map signupMap = new HashMap<String, String>();	
		signupMap.put("id", "");
		signupMap.put("timezone", "UTC");		
		signupMap.put("password1", "buzz");
		signupMap.put("password2","buzz" );
		signupMap.put("email", "buzz@test.com");
		signupMap.put("name", "buzz");
		signupMap.put("role", "ADMIN");
		signupMap.put("password", "buzz");
		ServiceResponse serviceResponse = backService.getUserService().save(signupMap);
		
		return "redirect:../cms/config/users.vm";
	}
}
