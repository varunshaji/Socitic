package org.soc.setup.service;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.SocConstant;
import org.soc.common.exception.DaoException;
import org.soc.setup.dao.SetupDao;
import org.soc.shoppe.account.dao.AccountDao;
import org.soc.shoppe.account.vo.AccountVO;
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
import org.vosao.entity.TemplateEntity;
import org.vosao.entity.UserEntity;
import org.vosao.enums.FolderPermissionType;
import org.vosao.enums.UserRole;
import org.vosao.service.BackService;
import org.vosao.service.FrontService;
import org.vosao.utils.StreamUtil;

public class SocVosaoSetupService {
	
	Logger log = Logger.getLogger(SocVosaoSetupService.class);
	
	@Inject
	private SetupDao setupDao;
	
	@Inject
	private AccountDao accountDao;

	FrontService frontService;
	
	BackService backService;
	
	Business business;
	
	MessageQueue messageQueue;
	
	private GroupEntity guests;
	
	public void init() {
    	try {
    		this.frontService = VosaoContext.getInstance().getFrontService();
    		
    		this.backService = VosaoContext.getInstance().getBackService();
    		
    		this.business = VosaoContext.getInstance().getBusiness();
    		
    		this.messageQueue = VosaoContext.getInstance().getMessageQueue();
    	}catch(Exception exception) {
    		log.info("failed to get VosaoContext Instance");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
	}
	
	public boolean initStatus() {
		return setupDao.validateInitStatus();
	}
	public Boolean setupService(AccountVO accountVO) {
		boolean result = initStatus();
		if(result) {
			result = setupSoc(accountVO);
			if(result)
				setupVosao(accountVO);
		}
		if(result) {
			Calendar currentDate = Calendar.getInstance();
			java.util.Date date = currentDate.getTime();
			setupDao.saveInit(true, date);
		}
			
		return result;
	}
	
	private Boolean setupSoc(AccountVO accountVO) {
		boolean result = false;
		try {
			result = addSocAdmin(accountVO);
		}catch(Exception exception) {
			log.debug("failed to add account");
			log.log(Priority.DEBUG,"Error please",exception);
		}
		return result;
	}
	
	private boolean addSocAdmin(AccountVO accountVO) {
		boolean result = false;
		try {
			accountDao.addAccount(accountVO);
			result = true;
		}catch(DaoException daoException) {
			log.debug("failed to add account");
			log.log(Priority.DEBUG,"Error please",daoException);
		}
		return result;
	}
	
	private void setupVosao(AccountVO accountVO) {
		setupCms(accountVO);
		loadSocThemes();
	}
	
	private boolean setupCms(AccountVO account) {
    	try {
	    	initGroups();
			initUsers(account);
			initTemplates();
			initFolders();
			initConfigs();
			initForms();
			initLanguages();
    	}catch(Exception exception) {
    		log.info("problem occured during setup shop");
    		log.log(Priority.DEBUG,"Error please",exception);
    	}
    	return true;
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
	private void initUsers(AccountVO account) {
		try {
			if (business == null)
				init();
			UserEntity admin = new UserEntity(account.getUsername(),BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()),account.getEmail(),UserRole.ADMIN);
			business.getDao().getUserDao().save(admin);
			log.debug("Adding admin user: "+account.getUsername());
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
	
	private void loadSocThemes() {
		try {
			if (business == null || messageQueue == null)
				init();
			byte[] file = StreamUtil.getBytesResource(SocConstant.SOC_DEEFAULT_SITE);
			business.getSystemService().getCache().putBlob(SocConstant.SOC_DEEFAULT_SITE, file);
			messageQueue.publish(new ImportMessage.Builder().setStart(1).setFilename(SocConstant.SOC_DEEFAULT_SITE).setCurrentNamespace(SocConstant.SOC_NAMESPACE).create());
		}
		catch (IOException e) {
			log.error("Can't load default site: " + e.getMessage());
			log.log(Priority.DEBUG,"Error please",e);
		}
	}
	
	public AccountDao getAccountDao() {
		return accountDao;
	}
	
	public SetupDao getSetupDao() {
		return setupDao;
	}
	public void setSetupDao(SetupDao setupDao) {
		this.setupDao = setupDao;
	}
	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}
}
