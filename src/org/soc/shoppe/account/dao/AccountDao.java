package org.soc.shoppe.account.dao;

import java.util.List;

import org.soc.common.exception.DaoException;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.ConfirmUserVO;
import org.soc.shoppe.account.vo.PrivilegeVO;
import org.soc.shoppe.account.vo.ValidateEmailVO;
import org.soc.shoppe.site.vo.ShopVO;

public interface AccountDao {
	
	public boolean addAccount(AccountVO accountVO) throws DaoException;
	
	public AccountVO loadAccount(String username) throws DaoException;
	
//	public boolean addPrivilege(AccountVO accountVO,PrivilegeVO privilegeVO);//ld acnt,add prev

	public List<AccountVO> retrieveAccount()throws DaoException;
	
	public List<AccountVO> setPrivilege(PrivilegeVO privilegeVO,List<AccountVO> accounts) throws DaoException;
	
	public boolean validateUsername(String username) throws DaoException;

	public boolean validateEmail(String email) throws DaoException;

	public List<ShopVO> getAllShopByUser(String user) throws DaoException;

	public List<AccountVO> addSitePrivilege(String shopId, List<String> users) throws DaoException;

	public List<AccountVO> getAllAcccounts() throws DaoException;

	public AccountVO updateAccount(AccountVO accountVO) throws DaoException;

	public Boolean deleteAccount(String username) throws DaoException;

	public AccountVO getUser(String username) throws DaoException;

	public ConfirmUserVO setUserConfirmation(ConfirmUserVO confirmUserVO) throws DaoException;

	public boolean saveEmailValidation(ValidateEmailVO validateEmailVO) throws DaoException;

	public String getEmailValidationKey(String email) throws DaoException;

	public AccountVO getAccountByEmail(String email) throws DaoException;

	public ValidateEmailVO getEmailValidationData(String email) throws DaoException;

	public AccountVO getAccounByMobile(String number) throws DaoException;

	public String getSmsValidationKey(String sms) throws DaoException;

	public ValidateEmailVO getSmsValidationData(String sms) throws DaoException;

	public boolean updateEmailValidation(ValidateEmailVO validateEmailVO) throws DaoException;
}
