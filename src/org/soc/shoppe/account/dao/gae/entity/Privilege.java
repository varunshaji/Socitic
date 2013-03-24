package org.soc.shoppe.account.dao.gae.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.soc.shoppe.account.vo.PrivilegeVO;

import com.google.appengine.api.datastore.Key;

@Entity
public class Privilege implements Serializable {
	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key privilageId;
	
	private boolean editShop;
	
	private boolean addNewShop;
	
	private boolean addUser;
	
	private boolean editPrivilage;
	
	public boolean isEditShop() {
		return editShop;
	}

	public boolean isAddNewShop() {
		return addNewShop;
	}

	public boolean isAddUser() {
		return addUser;
	}

	public boolean isEditPrivilage() {
		return editPrivilage;
	}

	public Key getPrivilageId() {
		return privilageId;
	}

	public void setPrivilageId(Key privilageId) {
		this.privilageId = privilageId;
	}
    
	public Privilege(){
	}
	
	public Privilege(PrivilegeVO privilegeVO){
		
		this.addNewShop = privilegeVO.isAddNewShop();
		this.addUser = privilegeVO.isAddUser();
		this.editPrivilage = privilegeVO.isEditPrivilage();
		this.editShop = privilegeVO.isEditShop();
	}
	
    public Privilege(boolean editShop,boolean addNewShop,boolean addUser,boolean editPrivilege) {
		
		this.addNewShop = addNewShop;
		this.addUser = addUser;
		this.editShop = editShop;
		this.editPrivilage = editPrivilege;
	}

    public PrivilegeVO getPrivilegeVO() {
    	
    	PrivilegeVO privilegeVO = new PrivilegeVO(this.editShop, this.addNewShop, this.addUser, this.editPrivilage);
    	return privilegeVO;
    }
}
