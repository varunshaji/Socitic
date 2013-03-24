package org.soc.shoppe.busdata.exception;

public class BusinessException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9044433411923352760L;

	public static final String ERROR_RETRIEVE="org.soc.shoppee.business.retrieval";
	public static final String ERROR_ADD="org.soc.shoppee.business.add";
	public static final String ERROR_UNKNOWN="Something went wrong!!";
	
	
	public BusinessException(String message,Exception exception) {
		super(message,exception);
	}

}
