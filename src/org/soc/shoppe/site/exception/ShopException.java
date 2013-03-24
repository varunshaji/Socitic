package org.soc.shoppe.site.exception;

public class ShopException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1299331406596777840L;
	
	public static final String ERROR_RETRIEVE="org.soc.shoppee.site.retrieval";
	public static final String ERROR_UPDATE="org.soc.shoppee.site.update";
	public static final String ERROR_ADD="org.soc.shoppee.site.add";
	public static final String ERROR_DELETE="org.soc.shoppee.site.delete";
	public static final String ERROR_UNKNOWN="Something went wrong!!";
	
	
	public ShopException(String message,Exception exception) {
		super(message, exception);
	}

	
}
