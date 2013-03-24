package org.soc.shoppe.account.helper;

import java.io.Serializable;

import org.soc.shoppe.account.vo.AccountVO;


public class UserContextHolder implements Serializable {
	
	/**
	 * 
	 */
	//Logger log = Logger.getLogger(UserContextHolder.class);


	private static final long serialVersionUID = 2122816069416453401L;
	
	
	private AccountVO currentAccount;


	public AccountVO getCurrentAccount() {
		return currentAccount;
	}


	public void setCurrentAccount(AccountVO currentAccount) {		
		//log.debug("Inside setCurrentAccount....."+currentAccount);
		this.currentAccount = currentAccount;
	}



}
