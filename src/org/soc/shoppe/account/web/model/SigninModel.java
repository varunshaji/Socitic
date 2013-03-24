package org.soc.shoppe.account.web.model;

import java.util.List;

import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.PrivilegeVO;

public class SigninModel {

	private String role;
	
	private List<String> roles;
	
	private AccountVO accountVO;

	public AccountVO getAccountVO() {
		return accountVO;
	}

	public void setAccountVO(AccountVO accountVO) {
		this.accountVO = accountVO;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
