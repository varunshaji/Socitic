package org.soc.shoppe.account.dao.gae.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.soc.shoppe.account.vo.ConfirmUserVO;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Entity
public class ConfirmUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key confirmKey;
	
	private String username;

	public String getUsername() {
		return username;
	}

	public Key getConfirmKey() {
		return confirmKey;
	}

	public void setConfirmKey(Key confirmKey) {
		this.confirmKey = confirmKey;
	}
	
	public ConfirmUserVO getConfirmUserVO() {
		ConfirmUserVO confirmUserVO = new ConfirmUserVO(username,KeyFactory.keyToString(confirmKey));
		return confirmUserVO;
	}
	
	public ConfirmUser(ConfirmUserVO confirmUserVO) {
		this.username = confirmUserVO.getUsername();
	}
}
