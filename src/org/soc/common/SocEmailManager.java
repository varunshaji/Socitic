package org.soc.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocEmailManager {
	
    /** isEmailValid: Validate email address using Java reg ex. 
    * This method checks if the input string is a valid email address. 
    *
    * @author Girish.G.H
    * @param email String. Email address to validate 
    * @return boolean: true if email address is valid, false otherwise. 
    */  
    public static boolean isEmailValid(String email) {  
    
    	boolean isValid = false; 
    	//Initialize reg ex for email.  
	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";  
	    CharSequence inputStr = email;  
	    
	    //Make the comparison case-insensitive.  
	    Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);  
	    Matcher matcher = pattern.matcher(inputStr);  
	    if(matcher.matches()){  
	    isValid = true;  
	    }  
	    return isValid;  
    }  
}
