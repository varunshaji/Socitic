/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * limitations under the License.
 */
package org.soc.shoppe.account.dao.gae.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.RoleVO;

@Entity
public class Account implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private  String username;

	private  String password;

	private  String firstName;

	private  String lastName;
	
	private  String email;
	
	private  String phone;
	
	private  String mobile;
	
	private  String address;
	
	private boolean enabled;

	private  List<String> shops;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Role> roles;

	public Account(String username, String password, String firstName, String lastName,String email,String role) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public Account(AccountVO accountVO) {
		this.firstName = accountVO.getFirstName();
		this.lastName = accountVO.getLastName();
		this.password = accountVO.getPassword();
		this.username = accountVO.getUsername();
		this.email = accountVO.getEmail();
		this.phone = accountVO.getPhone();
		this.mobile = accountVO.getMobile();
		this.address = accountVO.getAddress();
		this.enabled = accountVO.isEnabled();
		this.shops = new ArrayList<String>();
		List<String> shopIds = accountVO.getShops();
		if (shopIds != null) {
			for (String shop : shopIds) {
				this.shops.add(shop);
			}
		}
		this.roles = new ArrayList<Role>();
		for(RoleVO roleVO : accountVO.getRoles()) {
			this.roles.add(new Role(roleVO));
		}
	}
	
	public void update(AccountVO accountVO) {
		this.firstName = accountVO.getFirstName();
		this.lastName = accountVO.getLastName();
		this.password = accountVO.getPassword();
		this.phone = accountVO.getPhone();
		this.mobile = accountVO.getMobile();
		this.address = accountVO.getAddress();
	}
	
	public AccountVO  retrieveAccount()
	{
		AccountVO accountVO = new AccountVO();
		accountVO.setFirstName(firstName);
		accountVO.setLastName(lastName);
		accountVO.setUsername(username);
		accountVO.setEmail(email);
		accountVO.setPhone(phone);
		accountVO.setMobile(mobile);
		accountVO.setAddress(address);
		accountVO.setEnabled(enabled);
		return accountVO;
	}
	
	public AccountVO getAccountVO()
	{
		AccountVO accountVO = new AccountVO();
		accountVO.setFirstName(firstName);
		accountVO.setLastName(lastName);
		accountVO.setUsername(username);
		accountVO.setEmail(email);
		accountVO.setPassword(password);
		accountVO.setPhone(phone);
		accountVO.setMobile(mobile);
		accountVO.setAddress(address);
		accountVO.setEnabled(enabled);
		List<String> shopIds = new ArrayList<String>();
		for(String shop : shops) {
			shopIds.add(shop);
		}
		accountVO.setShops(shopIds);
		List<RoleVO> roleVOs = new ArrayList<RoleVO>();
		for(Role role : roles) {
			
			roleVOs.add(role.getRoleVO());
		}
		accountVO.setRoles(roleVOs);
		return accountVO;
	}

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

	public String getEmail() {
		return email;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public List<String> getShops() {
		return shops;
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
}
