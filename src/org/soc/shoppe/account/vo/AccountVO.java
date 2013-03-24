package org.soc.shoppe.account.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class AccountVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -128308995450132528L;
	
	@NotEmpty
	private  String username;

	@NotEmpty
	private  String password;

	@NotEmpty
	private  String firstName;

	@NotEmpty
	private  String lastName;
	
	@NotEmpty
	private  String email;
	
    private  String phone;
	
	private  String mobile;
	
	@NotEmpty
	private  String address;
	
	private boolean enabled;
	
	private  List<String> shops;
	
	private  List<RoleVO> roles;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<RoleVO> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleVO> roles) {
		this.roles = roles;
	}

	public List<String> getShops() {
		return shops;
	}

	public void setShops(List<String> shops) {
		this.shops = shops;
	}

	public AccountVO() {
		this.roles = new ArrayList<RoleVO>();
	}
	
	public String getPhone() {
		return phone;
	}

	public String getMobile() {
		return mobile;
	}

	public String getAddress() {
		return address;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
