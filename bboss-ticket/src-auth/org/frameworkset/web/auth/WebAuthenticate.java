/**
 * 
 */
package org.frameworkset.web.auth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author yinbp
 *
 * @Date:2016-11-18 17:19:26
 */
public class WebAuthenticate extends AppAuthenticate {
	private HttpServletRequest request;
	 
	
	/**
	 * 
	 */
	public WebAuthenticate(HttpServletRequest request,String account,String password,Map<String,Object> extendAttributes) {
		super(null,account,  password, extendAttributes);
		HttpSession session = request.getSession(false);	
		sessionid = session != null ?session.getId():null;
	}
	
	

}
