package org.soc.shoppe.account.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.SocEmailManager;
import org.soc.common.exception.DaoException;
import org.soc.shoppe.account.dao.AccountDao;
import org.soc.shoppe.account.exception.AccountsException;
import org.soc.shoppe.account.service.AccountService;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.ConfirmUserVO;
import org.soc.shoppe.account.vo.PrivilegeVO;
import org.soc.shoppe.account.vo.ValidateEmailVO;
import org.soc.shoppe.site.vo.ShopVO;

public class AccountServiceImpl implements AccountService {
	
	Logger logAcSer = Logger.getLogger(AccountServiceImpl.class);

	public AccountDao accountDao;

	@Override
	public boolean createAccount(AccountVO accountVO) throws AccountsException {
		boolean result = false;
		try {
			result = accountDao.addAccount(accountVO);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: create Account",daoException);
			throw new AccountsException(AccountsException.ERROR_ADD,daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: craete Account",exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public boolean addUser(AccountVO accountVO) throws AccountsException {
		boolean result = false;
		try {
			result = accountDao.addAccount(accountVO);
			/*if(result) {
				ConfirmUserVO confirmUserVO = new ConfirmUserVO(accountVO.getUsername());
				SocEmailManager.sendUserConfirmationMail(confirmUserVO.getConfirmId(), accountVO);
			}*/
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: adding user",daoException);
			throw new AccountsException(AccountsException.ERROR_ADD,daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: adding user",exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public AccountVO loadAccountVO(String username) throws AccountsException
	{
		logAcSer.debug("Inside loadAccountVO......");
		AccountVO accountVO = null;
		try {
			accountVO = accountDao.loadAccount(username);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: loading account: "+username,daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: loading account: "+username,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return accountVO;
	}

	@Override
	public List<String> getAllSiteAdmins() throws AccountsException {
		
		 List<String> result = new ArrayList<String>();
        try {
        	 List<AccountVO> accounts = accountDao.retrieveAccount();
        	
             for(AccountVO accountVO:accounts){
             	
             	result.add(accountVO.getUsername());
             }
        }catch(DaoException daoException) {
        	logAcSer.log(Priority.DEBUG,"Error: getting all accounts",daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: getting all accounts",exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
       
		return result;
	}

	@Override
	public List<AccountVO> addPrivilege(PrivilegeVO privilegeVO,List<AccountVO> accounts) throws AccountsException {
		
		List<AccountVO> accountVOs = null;
		try {
			accountVOs = accountDao.setPrivilege(privilegeVO, accounts);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error please",daoException);
			throw new AccountsException(AccountsException.ERROR_ADD, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error please",exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return accountVOs;
	}

	@Override
	public AccountVO validateUser(String username) throws AccountsException {
		
		 AccountVO newAccountVO = null;
		 try {
			 newAccountVO = accountDao.getUser(username);
		 }catch(DaoException daoException) {
			    logAcSer.log(Priority.DEBUG,"Error: validating user:"+username,daoException);
				throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		 }catch(Exception exception) {
			    logAcSer.log(Priority.DEBUG,"Error: validating user:"+username,exception);
				throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		 }
		if (newAccountVO != null) {

			newAccountVO.setPassword(null);
	/*		String selectedRole = accountVO.getRoles().get(0).getRole();

			for (Iterator<RoleVO> it = newAccountVO.getRoles().iterator(); it
					.hasNext();) {

				if (!(it.next().getRole().equals(selectedRole)))
					it.remove();

			}*/

			if (newAccountVO.getRoles().size() == 0)
				newAccountVO = null;
		}

		return newAccountVO;
	}

	@Override
	public boolean checkUsername(String username) throws AccountsException {
		boolean result = false;
		try {
			 result = accountDao.validateUsername(username);
		 }catch(DaoException daoException) {
			    logAcSer.log(Priority.DEBUG,"Error : check user:"+username,daoException);
				throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		 }catch(Exception exception) {
			    logAcSer.log(Priority.DEBUG,"Error : check user:"+username,exception);
				throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		 }
		return result;
	}
	
	@Override
	public boolean checkEmail(String email) throws AccountsException {
		boolean result = false;
		try {
			 result = accountDao.validateEmail(email);
		 }catch(DaoException daoException) {
			    logAcSer.log(Priority.DEBUG,"Error: check email:"+email,daoException);
				throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			    logAcSer.log(Priority.DEBUG,"Error: check email:"+email,exception);
				throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}

	@Override
	public List<ShopVO> loadAllShopsByUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountVO> setSitePrivilege(String shopId, List<String> users) {
		List<AccountVO> result = null;
		try {
			 result = accountDao.addSitePrivilege(shopId,users);
		 }catch(DaoException daoException) {
			 logAcSer.log(Priority.DEBUG,"Error please",daoException);
		 }catch(Exception exception) {
			 logAcSer.log(Priority.DEBUG,"Error please",exception);
		 }
		return result;
	}
	
	@Override
	public List<AccountVO> loadAllAccounts() throws AccountsException {
		List<AccountVO> result = new ArrayList<AccountVO>();
		try {
			result = accountDao.getAllAcccounts();
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: load all accounts",daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: load all accounts",exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public AccountVO modifyAccount(AccountVO accountVO) throws AccountsException {
		AccountVO result = accountVO;
		try {
			result = accountDao.updateAccount(accountVO);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: modifying account",daoException);
			throw new AccountsException(AccountsException.ERROR_UPDATE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: modifying account",exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public Boolean removeAccount(String username) throws AccountsException {
		Boolean result = false;
		try {
			result = accountDao.deleteAccount(username);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: removing account:"+username,daoException);
			throw new AccountsException(AccountsException.ERROR_DELETE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: removing account:"+username,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public Boolean setEmailValidation(ValidateEmailVO validateEmailVO) throws AccountsException {
		boolean result = false;
		try {
			result = accountDao.saveEmailValidation(validateEmailVO);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: setting email validation:"+validateEmailVO.getEmail(),daoException);
			throw new AccountsException(AccountsException.ERROR_ADD, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: setting email validation:"+validateEmailVO.getEmail(),exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		
		return result;
	}
	
	@Override
	public Boolean updateValitationDetails(ValidateEmailVO validateEmailVO) throws AccountsException{
		boolean result = false;
		try{
			result = accountDao.updateEmailValidation(validateEmailVO);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: updating email validation:"+validateEmailVO.getEmail(),daoException);
			throw new AccountsException(AccountsException.ERROR_ADD, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: updating email validation:"+validateEmailVO.getEmail(),exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public Boolean checkEmailValidationKey(String key,String value,String validationKey) throws AccountsException {
		boolean result = false;
		String v_key = null;
		try {
			v_key = "sms".equals(key) ? accountDao.getSmsValidationKey(value) : accountDao.getEmailValidationKey(value);
			if(v_key != null) {
				if(v_key.equals(validationKey))
					result = true;
			}
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: checking alidateKey:"+key+" "+value,daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: checking validateKey:"+key+" "+value,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public ValidateEmailVO loadValidateEmailDataAfterValidation(String key,String value,String validationKey) throws AccountsException{
		ValidateEmailVO result = null;
		boolean validation = false;
		try{
			ValidateEmailVO validateEmailVO = "sms".equals(key) ? accountDao.getSmsValidationData(value) : accountDao.getEmailValidationData(value);
			String v_key = validateEmailVO==null ? null : validateEmailVO.getValidationKey();
			if(v_key != null) {
				if(v_key.equals(validationKey))
					validation = true;
			}
			if(validation)
				result = validateEmailVO;
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: checking validateKey:"+key+" : "+value,daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: checking validateKey:"+key+" : "+value,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public String getEmailValidationKey(String email) throws AccountsException {
		String result = null;
		try {
			result = accountDao.getEmailValidationKey(email);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error:getting email validationKey:"+email,daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error:getting email validationKey:"+email,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public String loadSmsValidationKey(String sms) throws AccountsException{
		String result = null;
		try {
			result = accountDao.getSmsValidationKey(sms);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error:getting sms validationKey:"+sms,daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error:getting sms validationKey:"+sms,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public ValidateEmailVO loadValidationEmailVOByValue(String key,String value) throws AccountsException{
		ValidateEmailVO result = null;
		boolean validation = false;
		try{
			result = "sms".equals(key) ? accountDao.getSmsValidationData(value) : accountDao.getEmailValidationData(value);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: load validationVO by value:"+key+" : "+value,daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: load validationVO by value:"+key+" : "+value,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public AccountVO loadAccountByEmail(String email) throws AccountsException {
		AccountVO result = null;
		try {
			result = accountDao.getAccountByEmail(email);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: loading account:"+email,daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: loading account:"+email,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	@Override
	public AccountVO loadAccountByMobile(String number) throws AccountsException{
		AccountVO result = null;
		try {
			result = accountDao.getAccounByMobile(number);
		}catch(DaoException daoException) {
			logAcSer.log(Priority.DEBUG,"Error: loading account:"+number,daoException);
			throw new AccountsException(AccountsException.ERROR_RETRIEVE, daoException);
		}catch(Exception exception) {
			logAcSer.log(Priority.DEBUG,"Error: loading account:"+number,exception);
			throw new AccountsException(AccountsException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
}
