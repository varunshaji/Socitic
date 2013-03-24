package org.soc.shoppe.site.exception;

public class ThemeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8441007964611919839L;
	
	public static final String ERROR_RETRIEVE="org.soc.shoppee.theme.retrieval";
	public static final String ERROR_UPDATE="org.soc.shoppee.theme.update";
	public static final String ERROR_ADD="org.soc.shoppee.theme.add";
	public static final String ERROR_DELETE="org.soc.shoppee.theme.delete";
	public static final String ERROR_UNKNOWN="Something went wrong!!";
	
	public ThemeException(String message,Exception exception) {
		super(message,exception);
	}
}
