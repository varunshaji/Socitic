package org.soc.common;

import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.RoleVO;

public class SecurityValidator {

	public static String validateType(AccountVO accountVO) {
		
		String result = "NORMAL";
		for(RoleVO roleVO : accountVO.getRoles()) {
			
			if(roleVO.getRole().equals("ADMIN"))
				result = "ADMIN";
			if(roleVO.getRole().equals("SITE_ADMIN") && !result.equals("ADMIN"))
				result = "SITE_ADMIN";
			if(roleVO.getRole().equals("SITE_ADMIN") && result.equals("ADMIN"))
				result = "SITE+ADMIN";
		}
		return result;
	}
}
