package org.soc.shoppe.account.vo;

public class ValidateEmailVO {
	
	private String email;
	
	private String sms;
	
	private String validationKey;
	
	private String location;
	
	private int no_of_attempt;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getValidationKey() {
		return validationKey;
	}

	public void setValidationKey(String validationKey) {
		this.validationKey = validationKey;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getSms() {
		return sms;
	}

	public void setNo_of_attempt(int no_of_attempt) {
		this.no_of_attempt = no_of_attempt;
	}

	public int getNo_of_attempt() {
		return no_of_attempt;
	}
}
