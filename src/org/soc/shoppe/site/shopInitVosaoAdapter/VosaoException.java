package org.soc.shoppe.site.shopInitVosaoAdapter;

public class VosaoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8650805080596984006L;
	
	public static final String THEME_RETRIEVE_ERROR = "org.soc.shoppee.vosao.theme.retrieval";
	public static final String PAGE_CREATE_ERROR ="org.soc.shoppee.vosao.pagecreate";
	public static final String CMS_INIT_ERROR ="org.soc.shoppee.vosao.init";
	public static final String CMS_LOGIN_ERROR ="org.soc.shoppee.vosao.login";
	public static final String CMS_SIGNUP_ERROR ="org.soc.shoppee.vosao.signup";
	public static final String ERROR_UNKNOWN="Something went wrong!!";
	
	public VosaoException(String message,Exception exception) {
		super(message, exception);
	}
}
