package org.soc.common.exception;

public class GeneralException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3296099373265025730L;
	
	public static final String SESSION_OUT = "org.soc.common.error";

	public GeneralException(String msg,Exception exception) {
		super(msg,exception);
	}
}
