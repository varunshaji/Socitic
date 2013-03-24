package org.soc.setup.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class SetupSoc implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key setupkey;
	
	private boolean initStatus;
	
	private java.util.Date initDate;

	public boolean isInitStatus() {
		return initStatus;
	}

	public java.util.Date getInitDate() {
		return initDate;
	}
	
	public SetupSoc(boolean initStatus,java.util.Date initDate) {
	
		this.initStatus = initStatus;
		this.initDate = initDate;
	}

	public Key getSetupkey() {
		return setupkey;
	}
}
