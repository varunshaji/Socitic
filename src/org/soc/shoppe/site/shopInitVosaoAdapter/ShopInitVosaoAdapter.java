/**
 * 
 * This is an adapter class for integerating SOC shop services with Vosao cms.
 * @author Girish.G.H
 */
package org.soc.shoppe.site.shopInitVosaoAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.SocConstant;
import org.soc.common.exception.DaoException;
import org.soc.common.gaeBlobstore.ChainedBlobstoreInputStream;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.site.dao.ThemeDao;
import org.soc.shoppe.site.exception.ThemeException;
import org.soc.shoppe.site.service.ThemeService;
import org.soc.shoppe.site.vo.ShopVO;
import org.soc.shoppe.site.vo.ThemeVO;
import org.vosao.business.Business;
import org.vosao.business.mq.MessageQueue;
import org.vosao.business.mq.message.ImportMessage;
import org.vosao.common.BCrypt;
import org.vosao.common.VosaoContext;
import org.vosao.entity.ConfigEntity;
import org.vosao.entity.FolderEntity;
import org.vosao.entity.FormConfigEntity;
import org.vosao.entity.GroupEntity;
import org.vosao.entity.LanguageEntity;
import org.vosao.entity.PageEntity;
import org.vosao.entity.TemplateEntity;
import org.vosao.entity.UserEntity;
import org.vosao.enums.FolderPermissionType;
import org.vosao.enums.UserRole;
import org.vosao.filter.AuthenticationFilter;
import org.vosao.service.BackService;
import org.vosao.service.FrontService;
import org.vosao.service.ServiceResponse;
import org.vosao.service.vo.PageVO;
import org.vosao.service.vo.UserVO;
import org.vosao.utils.StreamUtil;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;

public class ShopInitVosaoAdapter {
	
	Logger log = Logger.getLogger(ShopInitVosaoAdapter.class);
    
	FrontService frontService;
	
	BackService backService;
	
	Business business;
	
	MessageQueue messageQueue;
	
    private long groupId;
    
    private String currentNameSpace;
    
    private String channelKey;
    
    private String SITE_BACKUP;
    
    private GroupEntity guests;
    
    @Inject
    private ThemeService themeService;

	public ThemeService getThemeService() {
		return themeService;
	}

	public void setThemeService(ThemeService themeService) {
		this.themeService = themeService;
	}

	/**
	 * 
	 * This method initialises Vosao cms bussiness and service classes
	 * It initialises {@link FrontService, BackService, Business, MessageQueue}
	 * The classes are initialised through {@link VosaoContext} 
	 * @since 16/09/2011
	 * @author Girish.G.H
	 * @return nothing
	 */
  
	public void init() {
    	try {
    		this.frontService = VosaoContext.getInstance().getFrontService();
    		
    		this.backService = VosaoContext.getInstance().getBackService();
    		
    		this.business = VosaoContext.getInstance().getBusiness();
    		
    		this.messageQueue = VosaoContext.getInstance().getMessageQueue();
    		
    		log.debug("*******Inside init()..backService="+backService+"frontService="+frontService+"business="+business);
    	}catch(Exception exception) {
    		log.info("failed to get VosaoContext Instance");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
	}
	
	/**
	 * 
	 * This method retrieves all the available templates for creating a new shop
	 * @since 16/09/2011
	 * @author Girish.G.H
	 * @return list of {@link ThemeVO}
	 * @throws VosaoException
	 */
	
	public List<ThemeVO> getAllThemes() throws VosaoException {
		List<ThemeVO> themes = new ArrayList<ThemeVO>();
		try {
			if(backService == null)
				init();
			List<TemplateEntity> templates = backService.getTemplateService().getTemplates();
			if(templates!=null) {
				for(TemplateEntity template : templates) {
					ThemeVO themeVO = new ThemeVO();
					themeVO.setThemeId(template.getId());
					themeVO.setThemeName(template.getTitle());
					themes.add(themeVO);
				}
			}
		}catch (Exception exception) {
			log.log(Priority.DEBUG,"Error:getAllThemes",exception);
			throw new VosaoException(VosaoException.THEME_RETRIEVE_ERROR, exception);
		}
		
		return themes;
	}
	
	/**
	 * 
	 * This method creates Vosao cms account for new admins
	 * @since 11/09/2011
	 * @author Girish.G.H
	 * @return {@link Boolean}
	 * @throws VosaoException
	 */
	
	public boolean adminCmsSignup(AccountVO accountVO) throws VosaoException
	{
		boolean result = false;
		try {
			
			if(NamespaceManager.get()==SocConstant.SOC_NAMESPACE)
				NamespaceManager.set(SocConstant.SOC_NAMESPACE);
			
			Map<String, String> signupMap = new HashMap<String, String>();	
			String psw = accountVO.getPassword();
			signupMap.put("id", "");
			signupMap.put("timezone", "UTC");		
			signupMap.put("password1", psw);
			signupMap.put("password2", psw);
			signupMap.put("email", accountVO.getEmail());
			signupMap.put("name", accountVO.getUsername());
	        signupMap.put("role", "ADMIN");
			signupMap.put("password", psw);
			
			if(backService == null)
				init();
			ServiceResponse serviceResponse = backService.getUserService().save(signupMap);
			log.debug("*******Inside adminCmsSignup: SerrviceResponse="+serviceResponse+",with result="+serviceResponse.SUCCESS_RESULT+"and message="+serviceResponse.getMessage());
			result = true;
		}catch (Exception exception) {
			log.log(Priority.DEBUG,"Error: vosao signup",exception);
			throw new VosaoException(VosaoException.CMS_SIGNUP_ERROR, exception);
		}
		return result;
	}
	
	/**
	 * 
	 * This method is used for initialising Vosao cms configurations for each shops
	 * @since 06/10/2011
	 * @author Girish.G.H
	 * @return nothing
	 */
	
    private void initShop(List<AccountVO> accounts,String admin_email,ShopVO shopVO) {
    	try {
    			setupCms(accounts);
    			loadShopTheme(shopVO.getThemename());
    	}catch(Exception exception) {
    		log.info("failed to init shop");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
    }
    
    private boolean setupCms(List<AccountVO> accounts) {
    	try {
	    	initGroups();
			initUsers(accounts);
			initTemplates();
			initFolders();
			initConfigs();
			initForms();
			initLanguages();
    	}catch(Exception exception) {
    		log.info("problem occured during setup shop");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
    	return false;
    }
    //-----------start initGroups-----------
	private void initGroups() {
		try {
			if (business == null)
				init();
			    guests = business.getDao().getGroupDao().getByName("guests");
			if (guests == null) {
				guests = new GroupEntity("guests");	
				business.getDao().getGroupDao().save(guests);
			}
		}catch(Exception exception) {
    		log.info("problem occured during initialising groups");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
	}
	//-----------end initGroups-----------
    
	//-----------start initUsers-----------
	private void initUsers(List<AccountVO> accounts) {
		try {
			if (business == null)
				init();
			List<UserEntity> admins = business.getDao().getUserDao().getByRole(UserRole.ADMIN);
			if (admins.size() == 0) {
				for(AccountVO accountVO : accounts) {
					UserEntity admin = new UserEntity(accountVO.getUsername(),BCrypt.hashpw(accountVO.getPassword(), BCrypt.gensalt()),accountVO.getEmail(),UserRole.ADMIN);
					business.getDao().getUserDao().save(admin);
					log.debug("Adding admin user: "+accountVO.getUsername());
				}
			}
		}catch(Exception exception) {
    		log.info("problem occured during initialising users");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
	}
	//-----------end initUsers-----------
    
	//-----------start initTemplates-----------
	public static final String SIMPLE_TEMPLATE_FILE = "org/vosao/resources/html/simple.html";
	
	private void initTemplates() {
		try {
			if (business == null)
				init();
			List<TemplateEntity> list = business.getDao().getTemplateDao().select();
			if (list.size() == 0) {
				String content = loadResource(SIMPLE_TEMPLATE_FILE);
				TemplateEntity template = new TemplateEntity("Simple", content,	"simple");
				business.getDao().getTemplateDao().save(template);
		        log.debug("Adding default template.");
			}
		}catch(Exception exception) {
    		log.info("problem occured during initialising templates");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
	}
  
	private String loadResource(final String url) {
		try {
			return StreamUtil.getTextResource(url);
		}
		catch(IOException e) {
			log.debug("Can't read comments template." + e);
			log.log(Priority.DEBUG,"Error please",e);
			return "Error during load resources " + url;
		}
	}
	//-----------end initTemplates-----------
	
	//-----------start initFolders-----------
	private void initFolders() {
		try {
			if (business == null)
				init();
			List<FolderEntity> roots = business.getDao().getFolderDao().getByParent(null);
			if (roots.size() == 0) {
		        log.info("Adding default folders.");
				FolderEntity root = new FolderEntity("file", "/", null);
				business.getDao().getFolderDao().save(root);
				FolderEntity theme = new FolderEntity("Themes resources", "theme", root.getId());
				business.getDao().getFolderDao().save(theme);
				FolderEntity simple = new FolderEntity("Simple", "simple", theme.getId());
				business.getDao().getFolderDao().save(simple);
				business.getFolderPermissionBusiness().setPermission(
						root, guests, FolderPermissionType.READ);
				FolderEntity tmp = new FolderEntity("tmp", "tmp", root.getId());
				business.getDao().getFolderDao().save(tmp);
				business.getFolderPermissionBusiness().setPermission(
						tmp, guests, FolderPermissionType.WRITE);
				FolderEntity page = new FolderEntity("page", "page", root.getId());
				business.getDao().getFolderDao().save(page);
			}
		}catch(Exception exception) {
    		log.info("problem occured during initialising folders");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
	}
	//-----------end initFolders-----------
	
	//-----------start initConfigs-----------
	
	private void initConfigs() {
		String COMMENTS_TEMPLATE_FILE = "org/vosao/resources/html/comments.html";
		String VERSION = "1.0";//"${vosao.version}";  ??//#######to change
		try {
			if (business == null)
				init();
			ConfigEntity config = business.getConfigBusiness().getConfig();
			if (config.getId() == null || config.getId() == 0) {
		        config.setVersion(VERSION);
				config.setGoogleAnalyticsId("");
		        config.setSiteEmail("");
		        config.setSiteDomain("");
		        config.setEditExt("css,js,xml");
		        config.setSiteUserLoginUrl("/login");
		        config.setCommentsTemplate(loadResource(
		        		COMMENTS_TEMPLATE_FILE));
		        business.getDao().getConfigDao().save(config);
			}
		}catch(Exception exception) {
			log.info("problem occured during initialising configs");
			log.log(Priority.DEBUG,"Error please",exception);
		}
	}
	//-----------end initConfigs-----------
	
	//-----------start initForms-----------
		
	private void initForms() {
		String FORM_TEMPLATE_FILE = "org/vosao/resources/html/form-template.html";
		String FORM_LETTER_FILE = "org/vosao/resources/html/form-letter.html";
		try {
			if (business == null)
				init();
			FormConfigEntity config = business.getDao().getFormConfigDao().getConfig();
			if (config.getId() == null) {
				config.setFormTemplate(loadResource(FORM_TEMPLATE_FILE));
				config.setLetterTemplate(loadResource(FORM_LETTER_FILE));
				business.getDao().getFormConfigDao().save(config);			
			}
		}catch(Exception exception) {
			log.info("problem occured during initialising forms");
			log.log(Priority.DEBUG,"Error please",exception);
		}
	}
	//-----------end initForms-----------	
	
	//-----------start initLanguages-----------
	private void initLanguages() {
		try {
			if (business == null)
				init();
			LanguageEntity lang = business.getDao().getLanguageDao().getByCode(
					LanguageEntity.ENGLISH_CODE); 
			if (lang == null) {
				lang = new LanguageEntity(
					LanguageEntity.ENGLISH_CODE, LanguageEntity.ENGLISH_TITLE);
				business.getDao().getLanguageDao().save(lang);
			}
		}catch(Exception exception) {
			log.info("problem occured during initialising languages");
			log.log(Priority.DEBUG,"Error please",exception);
		}
	}
	//-----------end initLanguages-----------	
	
	//-----------start loadShopTheme-----------
	
	public void loadShopTheme(String theme) {
		try {
			NamespaceManager.set(SocConstant.SOC_NAMESPACE);
			BlobKey blobKey = themeService.getFileBlobKeyFromImageUrl(theme);
		    String DEFAULT_SITE = "default_theme.vz";//getFileName(blobKey);//theme+".vz";
		    System.out.println("====_____default_site:___ "+DEFAULT_SITE);
			if (business == null || messageQueue == null)
				init();
		//	byte[] file = StreamUtil.getBytesResource(DEFAULT_SITE);
			byte[] file = getThemeResource(blobKey);
			NamespaceManager.set(getCurrentNameSpace());
			business.getSystemService().getCache().putBlob(DEFAULT_SITE, file);
			messageQueue.publish(new ImportMessage.Builder().setStart(1).setFilename(DEFAULT_SITE).setCurrentNamespace(getCurrentNameSpace()).setChannelKey(getChannelKey()).create());
		}catch(ThemeException themeException){
			log.log(Priority.DEBUG,"Error to get blobkey from imageurl",themeException);
		}/*catch (IOException e) {
			log.error("Can't load default site: " + e.getMessage());
			log.log(Priority.DEBUG,"Error please",e);
		}*/
	}
	private String getFileName(BlobKey blobKey) {
		String result = null;
		try {
			FileService fileService = FileServiceFactory.getFileService();
			AppEngineFile file = fileService.getBlobFile(blobKey);
			result = file.getNamePart();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	private byte[] getThemeResource(BlobKey blobKey) {
		byte[] result = null;
		try {
			ChainedBlobstoreInputStream inputStream = new ChainedBlobstoreInputStream(blobKey);
			byte[] data = IOUtils.toByteArray(inputStream);
			result = data;
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return result;
	}
	//-----------end loadShopTheme-----------
	
	/**
	 * 
	 * This method is used for login to Vosao cms during the initialisation of shop configurations because
	 * vosao always checks the authentication deatails before every operations.
	 * In this method,it sets the USER_SESSION_ATTR as the admin user email and add the {@link UserEntity}
	 * to {@link VosaoContext}. 
	 * 
	 * @since 01/10/2011
	 * @author Girish.G.H
	 * @return {@link Boolean}
	 */
	private boolean cmsLogin(String userEmail) {	
		boolean result = false;
	//	NamespaceManager.set(getCurrentNameSpace());
		
		try	{
			log.debug("*******Inside cmsLogin");
			if(frontService==null || business==null)
				init();
			
			if(NamespaceManager.get()!=SocConstant.SOC_NAMESPACE)
			   NamespaceManager.set(SocConstant.SOC_NAMESPACE);
			UserEntity user = business.getDao().getUserDao().getByEmail(userEmail);
			log.debug("..create userEntity user="+user);
			if (user == null || user.isDisabled()) {
				result = false;
			}else {
				HttpSession session = VosaoContext.getInstance().getRequest().getSession(true);
				session.setAttribute(AuthenticationFilter.USER_SESSION_ATTR, user.getEmail());
				VosaoContext.getInstance().setUser(user);
	    	    result = true;
			}
	    	log.debug("..result="+result);
		}
		
		catch(Exception exception)	{
			log.debug("*******Inside cmsLogin: catch exception");
			log.log(Priority.DEBUG,"Error please",exception);
		}
		
		return result;
	}
	
	private boolean cmsLogout() {
		if(frontService==null)
			init();
		frontService.getLoginService().logout();
		return true;
	}

	/**
	 * 
	 * This method is used for creating shop websites by using vosao cms.
	 * 
	 * @since 28/09/2011
	 * @author Girish.G.H
	 * @param {@code {@link ShopVO},List of {@link AccountVO}},adminuser_email
	 * @return boolean
	 * @throws VosaoException
	 */
	public boolean createCmsPage(ShopVO shopVO,List<AccountVO> accounts,String userEmail) throws VosaoException {
       
		boolean result = false;
		try {
			result = cmsLogin(userEmail);
			if(result) {
		        NamespaceManager.set(getCurrentNameSpace());	
		        initShop(accounts, userEmail,shopVO);
			}
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: create CMS page",exception);
			throw new VosaoException(VosaoException.PAGE_CREATE_ERROR, exception);
		}
		
	    return result;
	}
	
	/**
	 * 
	 * This method counts the number of child pages of the main root page with URL "/".
	 * It is used for checking whether the task queue for creating shop pages is completed or  not
	 * before proceeding to the next step of shop creation. 
	 * 
	 * @since 09/10/2011
	 * @author Girish.G.H
	 * @return Integer count
	 */
	public int countAllPages() {
		int result = 0;
		NamespaceManager.set(getCurrentNameSpace());
		List<PageVO> pages = null;
		if(backService == null)
			init();
		try {
			pages = backService.getPageService().getChildren("/");
		}catch(Exception exception) {
			log.debug("failed to count pages");
			log.log(Priority.DEBUG,"Error please",exception);
		}
		result = pages.size();
		return result;	
	}
	
	/**
	 * 
	 * This method is called only after loading the basic site configurations of the new shop website.
	 * It sets the content of the pages and rename the pages as required.
	 * 
	 * @since 09/10/2011
	 * @author Girish.G.H
	 * @param {@code {@link ShopVO}},adminuser_email
	 * @return boolean
	 * @throws VosaoException
	 */
	public boolean customiseShopSite(ShopVO shopVO,String userEmail) throws VosaoException {
		boolean result = false;
		try {
			if(cmsLogin(userEmail)) {
				NamespaceManager.set(getCurrentNameSpace());
				
				if(backService == null)
					init();
				
				/**
				 * 
				 * Rename the root page in the vosao cms to shop name
				 */
				PageEntity pageEntity = backService.getPageService().getPageByUrl("/");
				if(pageEntity==null)
					return false;
				Map<String, String> pageMap = new HashMap<String, String>();
				pageMap.put("id", Long.toString(pageEntity.getId()));
				pageMap.put("friendlyUrl", pageEntity.getFriendlyURL());
				pageMap.put("title", shopVO.getName());
				pageMap.put("approve", "true");
				pageMap.put("languageCode", "en");
				pageMap.put("titles", "{en:\""+shopVO.getName()+"\"}");// ####check titles
				backService.getPageService().savePage(pageMap);
				
				/**
				 * 
				 * Here the required contents are added to the default pages
				 */
				PageEntity pageEntity1 = backService.getPageService().getPageByUrl("/about");
				if(pageEntity1==null)
					return false;
				Map<String, String> pageMap1 = new HashMap<String, String>();
				pageMap1.put("id", Long.toString(pageEntity1.getId()));
				pageMap1.put("friendlyUrl", pageEntity1.getFriendlyURL());
				pageMap1.put("title", pageEntity1.getTitle()); // ####check titles
				//pageMap1.put("titles", "{en:\""+pageEntity1.getTitle()+"\"}");
				pageMap1.put("approve", "true");
				pageMap1.put("languageCode", "en");
				String content = shopVO.getDescription();
				pageMap1.put("content", content);
				backService.getPageService().savePage(pageMap1);
			    pageEntity1 = null;
				pageMap1 = null;
				content = null;
				pageEntity1 = backService.getPageService().getPageByUrl("/contact");
				if(pageEntity1==null)
					return false;
				pageMap1 = new HashMap<String, String>();
				pageMap1.put("id", Long.toString(pageEntity1.getId()));
				pageMap1.put("friendlyUrl", pageEntity1.getFriendlyURL());
				pageMap1.put("title", pageEntity1.getTitle()); // ####check titles
				pageMap1.put("approve", "true");
				pageMap1.put("languageCode", "en");
				content = "<table><tr><td>Location: </td><td>"+shopVO.getLocation()+"</td></tr>";
				content = content+"<tr><td>Address: </td><td>"+shopVO.getAddress()+"</td></tr>";
				if(shopVO.getPhone()!=null)
					content = content+"<tr><td>Phone: </td><td>"+shopVO.getPhone()+"</td></tr>";
				if(shopVO.getMobile()!=null)
					content = content+"<tr><td>Mobile: </td><td>"+shopVO.getMobile()+"</td></tr>";
				if(shopVO.getEmail()!=null)
					content = content+"<tr><td>Email: </td><td>"+shopVO.getEmail()+"</td></tr>";
				content = content+"</table>";
				pageMap1.put("content", content);
				backService.getPageService().savePage(pageMap1);
				pageEntity1 = null;
				pageMap1 = null;
				content = null;
				pageEntity1 = backService.getPageService().getPageByUrl("/configdata");
				if(pageEntity1==null)
					return false;
				pageMap1 = new HashMap<String, String>();
				pageMap1.put("id", Long.toString(pageEntity1.getId()));
				pageMap1.put("friendlyUrl", pageEntity1.getFriendlyURL());
				pageMap1.put("title", pageEntity1.getTitle()); // ####check titles
				pageMap1.put("approve", "true");
				pageMap1.put("languageCode", "en");
				
				String contentStart = "<?xml version=\"1.0\" encoding=\"utf-8\"?><content>";
				String shopId = "<shopid>"+shopVO.getShopId()+"</shopid>";
				String contentEnd = "</content>";
				content = contentStart + shopId + contentEnd;
				pageMap1.put("content", content);
				backService.getPageService().savePage(pageMap1);
				pageEntity1 = null;
				pageMap1 = null;
				content = null;
				contentStart = null;
				contentEnd = null;
				pageEntity1 = backService.getPageService().getPageByUrl("/location_map");
				if(pageEntity1==null)
					return false;
				pageMap1 = new HashMap<String, String>();
				pageMap1.put("id", Long.toString(pageEntity1.getId()));
				pageMap1.put("friendlyUrl", pageEntity1.getFriendlyURL());
				pageMap1.put("title", pageEntity1.getTitle()); // ####check titles
				pageMap1.put("approve", "true");
				pageMap1.put("languageCode", "en");

				contentStart = "<?xml version=\"1.0\" encoding=\"utf-8\"?><content>";
				String location = "<location>"+shopVO.getLocation()+"</location>";
				String latitude = "<latitude>"+shopVO.getLatitude()+"</latitude>";
				String longitude = "<longitude>"+shopVO.getLongitude()+"</longitude>";
				contentEnd = "</content>";
				content = contentStart + location + latitude + longitude + contentEnd;
				pageMap1.put("content", content);
				backService.getPageService().savePage(pageMap1);
				
				/**
				 * 
				 * The default user admin@test.com which created during cms initialization is removed
				 * here to prevent unauthorized access. 
				 */
				List<UserVO> siteAdmins = backService.getUserService().select();
				result = false;
				ServiceResponse serviceResponse = null;
				List<String> userIds = new ArrayList<String>();
				if(siteAdmins != null) {
					for(UserVO userVO : siteAdmins) {
						if(userVO.getEmail().equals("admin@test.com")) {
							userIds.add(userVO.getId());
						}
					}
					serviceResponse = backService.getUserService().remove(userIds);
					result = true;
				}
			}
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: failed to customise vosao page",exception);
			throw new VosaoException(VosaoException.PAGE_CREATE_ERROR, exception);
		}
		cmsLogout();
		return result;
	}
	
	/**
	 * 
	 * function for configuring social network connectionsIfacebook,twitter,googleplus etc) by
	 * setting their pageid/userid required to access the page/account
	 * 
	 * @since 20/12/2011
	 * @author Girish.G.H
	 * @param {@code {@link ShopVO}}
	 * @param {@link String}
	 * @return {@literal Boolean}
	 * @throws VosaoException
	 */
	public boolean configureSocialConnection(ShopVO shopVO,String userEmail) throws VosaoException{
		
		boolean result = false;
		try {
			if(cmsLogin(userEmail)) {
				NamespaceManager.set(getCurrentNameSpace());
				
				if(backService == null)
					init();
				
				PageEntity pageEntity1 = backService.getPageService().getPageByUrl("/socialnetwork");
				if(pageEntity1==null)
					return true; //## temp code
				Map<String, String> pageMap1 = new HashMap<String, String>();
				pageMap1.put("id", Long.toString(pageEntity1.getId()));
				pageMap1.put("friendlyUrl", pageEntity1.getFriendlyURL());
				pageMap1.put("title", pageEntity1.getTitle()); // ####check titles
				pageMap1.put("approve", "true");
				pageMap1.put("languageCode", "en");
				String contentStart = "<?xml version=\"1.0\" encoding=\"utf-8\"?><content>";
				String facebookId = "<facebook_page_id>"+shopVO.getFacebookId()+"</facebook_page_id>";
				String twitterId = "<twitter_id>"+shopVO.getTwitterId()+"</twitter_id>";
				String gPlusId = "<google_plus_id>"+shopVO.getgPlusId()+"</google_plus_id>";
				String flickrId = "<flickr_id>"+shopVO.getFlickrId()+"</flickr_id>";
				String flickrPhotosetId = "<flickr_photoset_id>"+shopVO.getFlickrPhotosetId()+"</flickr_photoset_id>";
				String contentEnd = "</content>";
				String content = contentStart + facebookId + twitterId + gPlusId + flickrId + flickrPhotosetId + contentEnd;
				pageMap1.put("content", content);
				backService.getPageService().savePage(pageMap1);
				result = true;
			}
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: failed to customise vosao page",exception);
			throw new VosaoException(VosaoException.PAGE_CREATE_ERROR, exception);
		}
		cmsLogout();
		return result;
	}
	
    //-----------getters and setters-----------
	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getCurrentNameSpace() {
		return currentNameSpace;
	}

	public void setCurrentNameSpace(String currentNameSpace) {
		this.currentNameSpace = currentNameSpace;
	}

	public void setChannelKey(String channelKey) {
		this.channelKey = channelKey;
	}

	public String getChannelKey() {
		return channelKey;
	}

	public String getSITE_BACKUP() {
		return SITE_BACKUP;
	}

	public void setSITE_BACKUP(String sITE_BACKUP) {
		SITE_BACKUP = sITE_BACKUP;
	}
	
}