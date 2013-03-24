package org.soc.shoppe.account.vo;

import java.io.Serializable;

public class PrivilegeVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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


	public void setEditShop(boolean editShop) {
		this.editShop = editShop;
	}



	public void setAddNewShop(boolean addNewShop) {
		this.addNewShop = addNewShop;
	}



	public void setAddUser(boolean addUser) {
		this.addUser = addUser;
	}



	public void setEditPrivilage(boolean editPrivilage) {
		this.editPrivilage = editPrivilage;
	}

	public PrivilegeVO(boolean editShop,boolean addNewShop,boolean addUser,boolean editPrivilege) {
		
		this.addNewShop = addNewShop;
		this.addUser = addUser;
		this.editShop = editShop;
		this.editPrivilage = editPrivilege;
	}
}
