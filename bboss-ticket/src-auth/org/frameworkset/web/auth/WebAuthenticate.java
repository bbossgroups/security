/**
 * 
 */
package org.frameworkset.web.auth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.frameworkset.security.session.SessionUtil;

/**
 * @author yinbp
 *
 * @Date:2016-11-18 17:19:26
 */
public class WebAuthenticate extends AppAuthenticate {
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	
	/**
	 * 
	 */
	public WebAuthenticate(HttpServletRequest request,HttpServletResponse response,String account,String password,Map<String,Object> extendAttributes) {
		super(null,account,  password, extendAttributes);
		
		this.request = request;
		this.response = response;
		session = request.getSession(true);	
		sessionid = session != null ?session.getId():null;
		
	}
	
	protected void afterSuccessLogin()
	{
		session.setAttribute(TicketConsts.ticket_session_token_key, token);
		session.setAttribute(TicketConsts.ticket_session_authenticatecode_key, authenticatecode);
		
		SessionUtil.writeCookies_(request, response, sessionid,authenticatecode);
	}
	
	

}
