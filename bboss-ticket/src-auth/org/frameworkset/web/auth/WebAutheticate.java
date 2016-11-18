/**
 * 
 */
package org.frameworkset.web.auth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.frameworkset.web.token.TokenHelper;
import org.frameworkset.web.token.ws.v2.AuthorService;

/**
 * @author yinbp
 *
 * @Date:2016-11-18 17:19:26
 */
public class WebAutheticate {
	private HttpServletRequest request;
	private String account;
	private String password;
	private Map<String,Object> extendAttributes;
	
	/**
	 * 
	 */
	public WebAutheticate(HttpServletRequest request,String account,String password,Map<String,Object> extendAttributes) {
		this.request = request;
		this.account = account;
		this.password = password;
		this.extendAttributes = extendAttributes;
	}
	
	public AuthenticatedToken login() throws AuthenticateException
	{
		 AuthorService authorService = TokenHelper.getTokenService().getAuthorService();

         //构建一个待验证的token
         AuthorHelper authorHelper = TokenHelper.getTokenService().getAuthorHelper();
         HttpSession session = request.getSession(false);	
         String sessionid = session != null ?session.getId():null;
         String authtoken = AuthorHelper.encodeAuthenticateRequest(sessionid,account,password, authorHelper.getAppcode(), authorHelper.getAppsecret(), authorHelper.getSecretPrivateKey(), extendAttributes);

         
         AuthenticateResponse authorResponse = authorService.auth(authtoken);
          if(authorResponse.isValidateResult())
         {
        	 try {
        		 AuthenticatedToken token = AuthorHelper.decodeMessageResponse(authorResponse.getAuthorization(), authorHelper.getSecretPublicKey());
        		 return token;
			} catch (AuthenticateException e) {
				throw e;
			}
         }
         else
         {        	 
        	 throw new AuthenticateException(authorResponse.getError());
         }
	}

}
