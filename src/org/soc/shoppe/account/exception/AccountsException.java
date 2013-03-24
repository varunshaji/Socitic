package org.soc.shoppe.account.exception;

public class AccountsException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8424530571698623723L;
	
	public static final String ERROR_RETRIEVE="org.soc.shoppee.account.retrieval";
	public static final String ERROR_UPDATE="org.soc.shoppee.account.update";
	public static final String ERROR_ADD="org.soc.shoppee.account.add";
	public static final String ERROR_DELETE="org.soc.account.site.delete";
	public static final String ERROR_UNKNOWN="Something went wrong!!";

	public AccountsException(String message,Exception exception) {
		super(message, exception);
	}
}
