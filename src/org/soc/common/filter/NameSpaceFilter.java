package org.soc.common.filter;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.SocConstant;
import org.soc.common.UrlProvider;
import org.soc.shoppe.site.vo.ShopVO;
import org.vosao.filter.AuthenticationFilter;
import org.vosao.global.PageCacheItem;

import com.google.appengine.api.NamespaceManager;

public class NameSpaceFilter implements Filter {

	Logger log = Logger.getLogger(NameSpaceFilter.class);
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        String url = httpRequest.getServletPath();
        Enumeration parameters = httpRequest.getParameterNames();
        boolean shop_viewer = false;
        while(parameters.hasMoreElements()){
    		String parameterName = (String)parameters.nextElement();
    		if(parameterName.equals("shop"))
    			shop_viewer = true;
    	}
        log.debug("#####Inside NameSpaceFilter url="+url);
        String sub_domain = UrlProvider.getSubDomainName(httpRequest);
 		log.log(Priority.DEBUG,"result domain="+sub_domain);
 		log.log(Priority.DEBUG,"url="+url);
        if(!url.contains(SocConstant.SOC_PATH)){
        	if(url.contains("/setup")) {
        		log.log(Priority.DEBUG,"setup url");
            	httpResponse.sendRedirect("soc/main.htm");
            	return;
            }
        	HttpSession session = ((HttpServletRequest)request).getSession();
            if(sub_domain!=null){
     	        try {
     	        	log.log(Priority.DEBUG,"validating domain");
     				NamespaceManager.validateNamespace(sub_domain);
     				session.setAttribute("current", sub_domain);
     				shop_viewer = true;
     			}catch(IllegalArgumentException illegalArgumentException) {
     				log.log(Priority.DEBUG,"domain="+sub_domain,illegalArgumentException);
     				httpResponse.sendRedirect("soc/security_login.htm?argument=illegal");
     				return;
     			}catch(NullPointerException nullPointerException) {
     				log.log(Priority.DEBUG,"Nullpointer in domain namespace validation: domain="+sub_domain,nullPointerException);
     			}catch(Exception exception) {
     				log.log(Priority.DEBUG,"Error domain namespace: domain="+sub_domain,exception);
     			}
             }
             log.log(Priority.DEBUG,"current url="+httpRequest.getRequestURL());
        	 if(url.equals("/") && !shop_viewer) {
        		 if (!isLoggedIn(httpRequest)) {
        			httpResponse.sendRedirect("soc/main.htm");
     	        	return;
     	        }
        	 }
        	log.debug("#####Inside NameSpaceFilter starting..Namespace: "+NamespaceManager.get());
    		
    		String shop = (String) request.getParameter("shop");
    		if(shop!=null)
    		   session.setAttribute("current", shop);
    		String org_view = (String) session.getAttribute(AuthenticationFilter.ORIGINAL_VIEW_KEY);
    		if(org_view!=null&&org_view.contains("shop=")){
    			shop = org_view.substring(org_view.indexOf("shop=")+5, org_view.length()); 
    		}	
    		if(shop==null)
    			shop = (String)(session.getAttribute("current"));
    		else {
    			try {
        			NamespaceManager.validateNamespace(shop);
        		}catch(IllegalArgumentException illegalArgumentException) {
        			log.log(Priority.DEBUG,"shop="+shop,illegalArgumentException);
        			httpResponse.sendRedirect("soc/security_login.htm?argument=illegal");
        			return;
        		}catch(NullPointerException nullPointerException) {
        			log.log(Priority.DEBUG,"Nullpointer in namespace validation: shop="+shop,nullPointerException);
        		}catch(Exception exception) {
        			log.log(Priority.DEBUG,"Error namespace: shop="+shop,exception);
        		}
    		}
    		NamespaceManager.set(shop); 
    		 log.debug("#####Inside NameSpaceFilter ending..Namespace: "+NamespaceManager.get());
        }else if(sub_domain!=null && url.contains(SocConstant.SOC_PATH)) {
        	httpResponse.sendRedirect("/");
        	return;
        }
        
		chain.doFilter(request, response);

	}
	protected boolean isLoggedIn(final HttpServletRequest request) {
		return request.getSession(true).getAttribute(
				AuthenticationFilter.USER_SESSION_ATTR) != null;
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
