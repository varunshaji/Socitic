package org.soc.shoppe.site.dao.test.impl;

import java.util.List;

import javax.inject.Inject;

import org.soc.shoppe.account.service.AccountService;
import org.soc.shoppe.account.vo.PrivilegeVO;
import org.soc.shoppe.site.dao.test.TestPrivilege;
import org.soc.shoppe.site.service.ShopService;

public class TestAccountPrivilege implements TestPrivilege {
	
	@Inject
	private AccountService accountService;
	
	@Inject
	private ShopService shopService;
	
	public boolean addPrivilege()
	{
		return false;
	}
	
	public List<PrivilegeVO> loadPrivileges(){
		return null;
	}
}
