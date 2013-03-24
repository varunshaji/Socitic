package org.soc.shoppe.account.vo;

import java.io.Serializable;

public class RoleVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String role;
	
	private PrivilegeVO privilege;

	public RoleVO(String role) {
		this.role = role;
	}

	public RoleVO() {
		// TODO Auto-generated constructor stub
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public PrivilegeVO getPrivilege() {
		return privilege;
	}

	public void setPrivilege(PrivilegeVO privilege) {
		this.privilege = privilege;
	}
}
