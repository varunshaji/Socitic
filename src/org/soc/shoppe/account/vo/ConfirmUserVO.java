package org.soc.shoppe.account.vo;

public class ConfirmUserVO {
	
	private String confirmId;
	
	private String username;

	public ConfirmUserVO(String username) {
		this.username = username;
	}
	
	public ConfirmUserVO(String username,String confirmId) {
		this.username = username;
		this.confirmId = confirmId;
	}
	
	public String getConfirmId() {
		return confirmId;
	}

	public String getUsername() {
		return username;
	}

	public void setConfirmId(String confirmId) {
		this.confirmId = confirmId;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
