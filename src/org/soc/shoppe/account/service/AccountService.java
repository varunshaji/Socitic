package org.soc.shoppe.account.service;

import java.util.List;

import org.soc.shoppe.account.exception.AccountsException;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.PrivilegeVO;
import org.soc.shoppe.account.vo.ValidateEmailVO;
import org.soc.shoppe.site.vo.ShopVO;

public interface AccountService {
	
	public boolean createAccount(AccountVO accountVO) throws AccountsException; 
	
	public AccountVO loadAccountVO(String username) throws AccountsException;
	
	public List<String> getAllSiteAdmins() throws AccountsException;
	
	public List<AccountVO> addPrivilege(PrivilegeVO privilegeVO,List<AccountVO> accounts) throws AccountsException;
	
	public boolean checkUsername(String username) throws AccountsException;
	
	public boolean checkEmail(String email) throws AccountsException;
	
	public List<ShopVO> loadAllShopsByUser(String username);
	
	public List<AccountVO> setSitePrivilege(String shopId, List<String> users);
	
	public List<AccountVO> loadAllAccounts() throws AccountsException;
	
	public AccountVO modifyAccount(AccountVO accountVO) throws AccountsException;
	
	public Boolean removeAccount(String username) throws AccountsException;
	
	public AccountVO validateUser(String username) throws AccountsException;

	public boolean addUser(AccountVO accountVO) throws AccountsException;

	public Boolean setEmailValidation(ValidateEmailVO validateEmailVO) throws AccountsException;

	public Boolean checkEmailValidationKey(String key,String value,String validationKey) throws AccountsException;

	public AccountVO loadAccountByEmail(String email) throws AccountsException;

	public String getEmailValidationKey(String email) throws AccountsException;

	public ValidateEmailVO loadValidateEmailDataAfterValidation(String key,String value,String validationKey) throws AccountsException;

	public AccountVO loadAccountByMobile(String number) throws AccountsException;

	public String loadSmsValidationKey(String sms) throws AccountsException;

	public ValidateEmailVO loadValidationEmailVOByValue(String key, String value) throws AccountsException;

	public Boolean updateValitationDetails(ValidateEmailVO validateEmailVO)
			throws AccountsException;
}
