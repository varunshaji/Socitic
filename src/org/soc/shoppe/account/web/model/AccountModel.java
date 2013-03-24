package org.soc.shoppe.account.web.model;

import java.util.List;

import org.soc.shoppe.account.vo.AccountVO;

public class AccountModel {

	private AccountVO accountVO;
	
	private List<AccountVO> accounts;
	
	private List<String> roles;

	public AccountVO getAccountVO() {
		return accountVO;
	}

	public void setAccountVO(AccountVO accountVO) {
		this.accountVO = accountVO;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<AccountVO> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountVO> accounts) {
		this.accounts = accounts;
	}
}
