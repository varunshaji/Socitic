package org.soc.shoppe.account.dao.gae.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.soc.shoppe.account.vo.RoleVO;

import com.google.appengine.api.datastore.Key;

@Entity
public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key roleId;
	
	private String role;
	
	@OneToOne(cascade=CascadeType.ALL)
	private Privilege privilege;

	public Key getRoleId() {
		return roleId;
	}

	public void setRoleId(Key roleId) {
		this.roleId = roleId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}


	public Privilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}

	public Role(RoleVO roleVO) {
		
		if (roleVO.getRole().equals("ROLE_ADMIN")) {
            this.role = roleVO.getRole();
			this.privilege = new Privilege(true, true, true, true);
		}
		if (roleVO.getRole().equals("ROLE_SITE-ADMIN")) {

			this.role = roleVO.getRole();
			this.privilege = new Privilege(true, false, false,false);
		}
		if (roleVO.getRole().equals("ROLE_USER")) {

			this.role = roleVO.getRole();
			this.privilege = new Privilege(false, true, false,false);
		}

	}
	
	public RoleVO getRoleVO() {
		
		RoleVO roleVO = new RoleVO();
		roleVO.setRole(role);
		roleVO.setPrivilege(privilege.getPrivilegeVO());
		return roleVO;
	}
}
