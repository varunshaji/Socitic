package org.soc.shoppe.account.dao.gae;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.exception.DaoException;
import org.soc.shoppe.account.dao.AccountDao;
import org.soc.shoppe.account.dao.gae.entity.Account;
import org.soc.shoppe.account.dao.gae.entity.ConfirmUser;
import org.soc.shoppe.account.dao.gae.entity.Privilege;
import org.soc.shoppe.account.dao.gae.entity.Role;
import org.soc.shoppe.account.dao.gae.entity.ValidateEmail;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.ConfirmUserVO;
import org.soc.shoppe.account.vo.PrivilegeVO;
import org.soc.shoppe.account.vo.RoleVO;
import org.soc.shoppe.account.vo.ValidateEmailVO;
import org.soc.shoppe.site.entity.Shop;
import org.soc.shoppe.site.vo.ShopVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AccountDaoImpl implements AccountDao,UserDetailsService {
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	Logger log = Logger.getLogger(AccountDaoImpl.class);
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public AccountDaoImpl() {
		
	}

	@Override
	public boolean addAccount(AccountVO accountVO) throws DaoException {
		boolean result = false;
		Account account = new Account(accountVO);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {			
			entityManager.getTransaction().begin();
			entityManager.persist(account);
			entityManager.getTransaction().commit();
			result = true;
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: add account",exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}finally {
			entityManager.close();
		}
		
		return result;
	}

	@Override
	public ConfirmUserVO setUserConfirmation(ConfirmUserVO confirmUserVO) throws DaoException {
		ConfirmUserVO result = null;
		ConfirmUser confirmUser = new ConfirmUser(confirmUserVO);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(confirmUser);
			entityManager.getTransaction().commit();
			result = confirmUser.getConfirmUserVO();
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error: setConfirmation",exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return result;
	}
	@Override
	public AccountVO loadAccount(String username) throws DaoException {
		AccountVO result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Account account = entityManager.find(Account.class, username);
			result =  account.retrieveAccount();
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error:load account:"+username,exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		
		return result;
	}


	/*@Override
	public boolean addPrivilege(AccountVO accountVO, PrivilegeVO privilegeVO) {
		// TODO Auto-generated method stub
		return false;
	}*/

	@Override
	public List<AccountVO> retrieveAccount() throws DaoException {
		List<AccountVO> result = new ArrayList<AccountVO>();
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query q = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a");
			List<Account> accounts = (List<Account>) q.getResultList();
			result =  convertToAccountVOList(accounts);
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: retrieve Accounts",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return result;
	}

    private List<AccountVO> convertToAccountVOList(List<Account> accounts)
    {
    	List<AccountVO> accountVOList = new ArrayList<AccountVO>();
    	for(Account account:accounts)
    	{
    		AccountVO accountVO = account.getAccountVO();
    		accountVO.setPassword(null);
    		accountVOList.add(accountVO);
    	}
    	return accountVOList;
    }

	@Override
	public List<AccountVO> setPrivilege(PrivilegeVO privilegeVO,List<AccountVO> accounts) throws DaoException {
		
		List<AccountVO> result = new ArrayList<AccountVO>();
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			for(AccountVO user : accounts)
			{
				Account account = (Account) entityManager.find(Account.class, user.getUsername());
				Privilege privilege = new Privilege(privilegeVO);
		//##########		account.getPrivileges().add(privilege);
				entityManager.persist(account);
				result.add(account.getAccountVO());
			}
			entityManager.getTransaction().commit();
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: set Privilege",exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return result;
	}

	@Override
	public AccountVO getUser(String username) throws DaoException {
		AccountVO result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try{
			
	        Query q = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a WHERE username=:username");
	        q.setParameter("username", username);
	        Account account = (Account) q.getSingleResult();
			result =  account.getAccountVO();
			log.debug("### account: "+account+" role="+account.getRoles());
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get user:"+username,exception);
        	throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
        }finally {
        	entityManager.close();
        }
		return result;
	}
	
	@Override
	public boolean validateUsername(String username) {
		
		boolean result = false;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query query = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a WHERE username = :username" );
			query.setParameter("username", username);
			Account account = (Account) query.getSingleResult();
			if(account == null)
				result = true;
		}catch (NoResultException nre) {
			
			result = true;
		}
		catch (Exception exception) {
			log.log(Priority.DEBUG,"Error: validate user:"+username,exception);
			result = false;
		}
		
		return result;
	}
	
	@Override
	public boolean validateEmail(String email) {
		boolean result = false;
		try {
			
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			Query query = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a WHERE email = :email" );
			query.setParameter("email", email);
			Account account = (Account) query.getSingleResult();
			if(account == null)
				result = true;
		} 
		catch (NoResultException nre) {
			
			result = true;
		}
		catch (Exception exception) {
			log.log(Priority.DEBUG,"Error: validate email:"+email,exception);
			result = false;
		}
		
		return result;
		
	}
	
	@Override
	public List<ShopVO> getAllShopByUser(String user) throws DaoException {
		List<ShopVO> result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query query = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a WHERE username = :username");   //#### error
			query.setParameter("username",user);
			Account account = (Account) query.getSingleResult();
			List<Shop> shops = (List<Shop>) query.getResultList();
//########################################################################incomplete		   
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error please",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR,exception);
		}finally {
			entityManager.close();
		}
		
		//		return convertToShopVOList(shopss);
		return null;
	}

	@Override
	public List<AccountVO> addSitePrivilege(String shopId, List<String> users) {
		
		List<AccountVO> accounts = new ArrayList<AccountVO>();
		try {
			
			   EntityManager entityManager = entityManagerFactory.createEntityManager();
			   entityManager.getTransaction().begin();
			   
			   for(String user : users) {
				   
				   Query query = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a WHERE username = :username");
				   query.setParameter("username",user);
				   Account account = (Account) query.getSingleResult();
				   account.getShops().add(shopId);
				   boolean siteAdmin = false;
				   for(Role role:account.getRoles()) {
					   if(role.getRole().equals("SITE_ADMIN"))
						   siteAdmin = true;
				   }
				   if(!siteAdmin) {
					   Role role = new Role(new RoleVO("SITE_ADMIN"));
					   account.getRoles().add(role);
				   }
					   
				   entityManager.persist(account);
				   accounts.add(account.getAccountVO());
			   }
			   entityManager.getTransaction().commit();
		}
		catch(Exception exception) {
			accounts = null;
			log.log(Priority.DEBUG,"Error please",exception);
		}
		
		return accounts;
	}
	
	@Override
	public List<AccountVO> getAllAcccounts() throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<AccountVO> result = new ArrayList<AccountVO>();
		try {
			Query q = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a");
			List<Account> accounts = (List<Account>) q.getResultList();
			result = convertToAccountVOList(accounts);
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get all accounts",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}
		return result;
	}
	
	@Override
	public AccountVO updateAccount(AccountVO accountVO) throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		AccountVO result = null;
		try {
			entityManager.getTransaction().begin();
			Query q = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a WHERE username = :username");
			q.setParameter("username", accountVO.getUsername());
			Account account = (Account) q.getSingleResult();
			account.update(accountVO);
			result = account.getAccountVO();     //####note this
			entityManager.persist(account);
			entityManager.getTransaction().commit();
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: update account",exception);
			throw new DaoException(DaoException.UPDATE_ERROR, exception);
		}
		return result;
	}
    
	@Override
	public Boolean deleteAccount(String username) throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Boolean result = false;
		try {
			entityManager.getTransaction().begin();
			Query q = entityManager.createQuery("DELETE FROM a "+Account.class.getSimpleName()+" a WHERE username = :username");
			q.setParameter("username", username);
			q.executeUpdate();
			entityManager.getTransaction().commit();
			result = true;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: delete account:"+username,exception);
			throw new DaoException(DaoException.DELETE_ERROR, exception);
		}
		return result;
	}
	
	@Override
	public boolean saveEmailValidation(ValidateEmailVO validateEmailVO) throws DaoException {
		boolean result = false;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			ValidateEmail validateEmail = new ValidateEmail(validateEmailVO);
			entityManager.persist(validateEmail);
			entityManager.getTransaction().commit();
			result = true;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: setting email validation:"+ validateEmailVO==null ? "" : validateEmailVO.getEmail(),exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}finally {
			entityManager.close();
		}
		
		return result;
	}
	
	@Override
	public boolean updateEmailValidation(ValidateEmailVO validateEmailVO) throws DaoException{
		boolean result = false;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query q = entityManager.createQuery("SELECT v FROM "+ValidateEmail.class.getSimpleName()+" v WHERE validationKey = :validationKey");
			q.setParameter("validationKey", validateEmailVO.getValidationKey());
			ValidateEmail validateEmail = (ValidateEmail) q.getSingleResult();
			validateEmail.update(validateEmailVO);
			entityManager.getTransaction().begin();
			entityManager.persist(validateEmail);
			entityManager.getTransaction().commit();
			result = true;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: setting email validation:"+ validateEmailVO==null ? "" : validateEmailVO.getEmail(),exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return result;
	}
	
	@Override
	public String getEmailValidationKey(String email) throws DaoException {
		String result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query q = entityManager.createQuery("SELECT validationKey FROM "+ValidateEmail.class.getSimpleName()+" v WHERE email = :email");
			q.setParameter("email", email);
			result = (String) q.getSingleResult();
		}catch(NoResultException noResultException) {
			result = null;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: getting email validationKey:"+email,exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return result;
	}
	
	@Override
	public String getSmsValidationKey(String sms) throws DaoException{
		String result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query q = entityManager.createQuery("SELECT validationKey FROM "+ValidateEmail.class.getSimpleName()+" v WHERE sms = :sms");
			q.setParameter("sms", sms);
			result = (String) q.getSingleResult();
		}catch(NoResultException noResultException) {
			result = null;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: getting sms validationKey:"+sms,exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		
		return result;
	}
	
	@Override
	public  ValidateEmailVO getEmailValidationData(String email) throws DaoException{
		ValidateEmailVO result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query q = entityManager.createQuery("SELECT v FROM "+ValidateEmail.class.getSimpleName()+" v WHERE email = :email");
			q.setParameter("email", email);
			ValidateEmail validateEmail = (ValidateEmail) q.getSingleResult();
			if(validateEmail!=null)
				result = validateEmail.getValidateEmailVO();
		}catch(NoResultException noResultException) {
			result = null;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: getting email validation Data:"+email,exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		
		return result;
	}
	
	@Override
	public  ValidateEmailVO getSmsValidationData(String sms) throws DaoException{
		ValidateEmailVO result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query q = entityManager.createQuery("SELECT v FROM "+ValidateEmail.class.getSimpleName()+" v WHERE sms = :sms");
			q.setParameter("sms", sms);
			ValidateEmail validateEmail = (ValidateEmail) q.getSingleResult();
			if(validateEmail!=null)
				result = validateEmail.getValidateEmailVO();
		}catch(NoResultException noResultException) {
			result = null;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: getting sms validation Data:"+sms,exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		
		return result;
	}
	
	@Override
	public AccountVO getAccountByEmail(String email) throws DaoException {
		AccountVO result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try{
	        Query q = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a WHERE email=:email");
	        q.setParameter("email", email);
	        Account account = (Account) q.getSingleResult();
			result =  account.getAccountVO();
			log.debug("### account: "+account+" role="+account.getRoles());
		}catch(NoResultException noResultException) {
			result = null;
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get account:"+email,exception);
        	throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
        }finally {
        	entityManager.close();
        }
		return result;
	}
	
	@Override
	public AccountVO getAccounByMobile(String number) throws DaoException{
		AccountVO result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try{
	        Query q = entityManager.createQuery("SELECT a FROM "+Account.class.getSimpleName()+" a WHERE mobile=:mobile");
	        q.setParameter("mobile", number);
	        Account account = (Account) q.getSingleResult();
			result =  account.getAccountVO();
			log.debug("### account: "+account+" role="+account.getRoles());
		}catch(NoResultException noResultException) {
			result = null;
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get account by mobile:"+number,exception);
        	throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
        }finally {
        	entityManager.close();
        }
		return result;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		UserDetails details = null;
		try{
				EntityManager entityManager = entityManagerFactory.createEntityManager();
				Account account = entityManager.find(Account.class, username);
				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				
				for(Role role : account.getRoles()) {
					if(role.getRole().equals("ROLE_ADMIN"))
						authorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
					if(role.getRole().equals("ROLE_SITE-ADMIN"))
						authorities.add(new GrantedAuthorityImpl("ROLE_SITE-ADMIN"));
				}
				
				
				if(!(account==null)){
					details = new User(account.getUsername(), account.getPassword(),true , true, true, true, authorities);
				}
				else{
					throw new Exception("User cannot be found");
				}
		}catch(Exception exception){
			log.log(Priority.DEBUG,"Error loadUser:"+username,exception);
			throw new UsernameNotFoundException("Name not found",exception);
		}
		return details;
	}
}
