package org.soc.common.exception;

public class DaoException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5800409883787853152L;
	
	public static final String UPDATE_ERROR="Failed to update data";
	public static final String ADD_ERROR="Failed to add data";
	public static final String RETRIEVE_ERROR="Failed to retrieve data";
	public static final String DELETE_ERROR="Failed to delete data";

	public DaoException(String msg,Exception exception) {
		super(msg,exception);
	}
	
}
