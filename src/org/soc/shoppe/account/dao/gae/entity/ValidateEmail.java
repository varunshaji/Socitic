package org.soc.shoppe.account.dao.gae.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.soc.shoppe.account.vo.ValidateEmailVO;

import com.google.appengine.api.datastore.Key;

@Entity
public class ValidateEmail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key confirmKey;
	
	private String email;
	
	private String sms;
	
	private String validationKey;
	
	private String location;
	
	private int no_of_attempt;

	public ValidateEmail(ValidateEmailVO validateEmailVO){
		this.email = validateEmailVO.getEmail();
		this.validationKey = validateEmailVO.getValidationKey();
		this.location = validateEmailVO.getLocation();
		this.sms = validateEmailVO.getSms();
		this.no_of_attempt = validateEmailVO.getNo_of_attempt();
	}
	
	public ValidateEmailVO getValidateEmailVO(){
		ValidateEmailVO validateEmailVO = new ValidateEmailVO();
		validateEmailVO.setEmail(this.email);
		validateEmailVO.setLocation(this.location);
		validateEmailVO.setValidationKey(this.validationKey);
		validateEmailVO.setSms(this.sms);
		validateEmailVO.setNo_of_attempt(this.no_of_attempt);
		return validateEmailVO;
	}
	
	public void update(ValidateEmailVO validateEmailVO){
		if(validateEmailVO!=null){
			if(validateEmailVO.getEmail()!=null)
				this.email = validateEmailVO.getEmail();
			if(validateEmailVO.getLocation() != null)
				this.location = validateEmailVO.getLocation();
			if(validateEmailVO.getSms() != null)
				this.sms = validateEmailVO.getSms();
			this.no_of_attempt = validateEmailVO.getNo_of_attempt();
		}
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public void setValidationKey(String validationKey) {
		this.validationKey = validationKey;
	}

	public String getEmail() {
		return email;
	}

	public String getValidationKey() {
		return validationKey;
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

	public void setConfirmKey(Key confirmKey) {
		this.confirmKey = confirmKey;
	}

	public Key getConfirmKey() {
		return confirmKey;
	}

	public void setNo_of_attempt(int no_of_attempt) {
		this.no_of_attempt = no_of_attempt;
	}

	public int getNo_of_attempt() {
		return no_of_attempt;
	}
}
