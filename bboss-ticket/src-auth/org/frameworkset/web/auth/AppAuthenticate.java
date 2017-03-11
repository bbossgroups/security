/**
 * 
 */
package org.frameworkset.web.auth;

import java.util.Map;

import org.frameworkset.web.token.TokenHelper;
import org.frameworkset.web.token.ws.v2.AuthorService;

/**
 * 应用后端登陆系统
 * @author yinbp
 *
 * @Date:2016-11-18 17:56:43
 */
public class AppAuthenticate {

	/**
	 * 
	 */
	public AppAuthenticate() {
		// TODO Auto-generated constructor stub
	}
	protected String sessionid = null;
	protected String account;
	protected String password;
	protected Map<String,Object> extendAttributes;
	protected AuthenticatedToken token; 
	protected String authenticatecode;
	
	/**
	 * 
	 */
	public AppAuthenticate(String sessionid , String account,String password,Map<String,Object> extendAttributes) {
		this.account = account;
		this.password = password;
		this.extendAttributes = extendAttributes;
		this.sessionid = sessionid;
	}
	protected void afterSuccessLogin()
	{
		
	}
	public AuthenticatedToken login() throws AuthenticateException
	{
		 AuthorService authorService = TokenHelper.getTokenService().getAuthorService();

         //构建一个待验证的token
         String authtoken = null;
        authtoken = AuthorHelper.encodeAuthenticateRequest(sessionid,account,password, extendAttributes);
        
         
         AuthenticateResponse authorResponse = authorService.auth(authtoken);
          if(authorResponse.isValidateResult())
         {
        	 try {
        		 authenticatecode = authorResponse.getAuthorization();
        		 token = AuthorHelper.decodeMessageResponse(authorResponse.getAuthorization());
        		
        		 afterSuccessLogin();
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
	
	public AuthenticateResponse ssologin() throws AuthenticateException
	{
		 AuthorService authorService = TokenHelper.getTokenService().getAuthorService();

         //构建一个待验证的token
         AuthorHelper authorHelper = TokenHelper.getTokenService().getAuthorHelper();
         
         String authtoken = AuthorHelper.encodeAuthenticateRequest(sessionid,account,password, extendAttributes);

         
         AuthenticateResponse authorResponse = authorService.auth(authtoken);
         return authorResponse;
	}
	public String getAuthenticatecode() {
		return authenticatecode;
	}

}
