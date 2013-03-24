package org.soc.shoppe.site.dao.test;

import java.util.List;

import org.soc.shoppe.account.vo.PrivilegeVO;

public interface TestPrivilege {
	
	public boolean addPrivilege();
	
	public List<PrivilegeVO> loadPrivileges();

}
