/**
 * @author Girish.G.H
 * @since 6/9/2011
 */
package org.soc.common;

import javax.servlet.http.HttpServletRequest;

import org.soc.common.SocConstant;

public class UrlProvider {
	/**
	 * 
	 * This method create the complete URL prefix including service context.
	 * @since 6/9/2011
	 * @author Girish.G.H
	 * @param {@link HttpServletRequest}
	 * @return {@literal String}
	 * 
	 */	
	public static String getServicePath(HttpServletRequest request) {
		String requestUrl = request.getRequestURL().toString();
		String pathInfo   = request.getPathInfo();
		String ServletPath  = request.getServletPath();
		String requestUri   = request.getRequestURI();
		String servicePath = requestUrl.replace(requestUri, "");
		String result = servicePath+SocConstant.SOC_PATH;
		return result.trim();
	}
	/**
	 * 
	 * This method create the original URL prefix excluding service context.
	 * @since 6/9/2011
	 * @author Girish.G.H
	 * @param {@link HttpServletRequest}
	 * @return {@literal String}
	 * 
	 */	
	public static String getServerPath(HttpServletRequest request) {
		String requestUrl = request.getRequestURL().toString();
		String requestUri   = request.getRequestURI();
		String serverPath = requestUrl.replace(requestUri, "");
		return serverPath.trim();
	}
	
	public static String getSubDomainName(HttpServletRequest request) {
		String requestUrl = request.getRequestURL().toString();
 		String requestUri = request.getRequestURI();
 		String filteredUrl = requestUrl.replace(requestUri, "").trim();
 //		log.log(Priority.DEBUG,"requestUrl="+requestUrl+",requestUri="+requestUri+",filteredUrl="+filteredUrl);
 		String result = null;
 		if(filteredUrl.contains("socitic.com")) {
 			filteredUrl = filteredUrl.replace(".socitic.com", "").trim();
 			if(filteredUrl.contains("/"))
 				filteredUrl= filteredUrl.replace("/", "").trim();
 			filteredUrl = filteredUrl.replace("http:", "").trim();
 	//		log.log(Priority.DEBUG,"after http clipping filteredUrl:"+filteredUrl);
 			if(filteredUrl.contains("www"))
 				filteredUrl = filteredUrl.replace("www", "").trim();
 			if(filteredUrl.contains("."))
 				filteredUrl = filteredUrl.replace(".", "").trim();
 			result = filteredUrl.trim();
 			if(result.equals(""))
 			  result = null;
 		}else
 			result = null;
		
		return result;
	}
	
 /*
    ####################<<For reference>>#######################
    
      examples of HttpServletRequest properties
      -----------------------------------------
         ServerName        : LocalHost
	     serverPort        : 8888
	     ServletPath       : /test
	     getPathinfo       : /tohome
	     requestURI        : /test/tohome
	     requestURL        : http://localhost:8888/test/tohome
	   
	#############################################################
*/
}
