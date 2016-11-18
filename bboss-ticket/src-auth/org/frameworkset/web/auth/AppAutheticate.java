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
public class AppAutheticate {

	/**
	 * 
	 */
	public AppAutheticate() {
		// TODO Auto-generated constructor stub
	}
	
	private String account;
	private String password;
	private Map<String,Object> extendAttributes;
	
	/**
	 * 
	 */
	public AppAutheticate( String account,String password,Map<String,Object> extendAttributes) {
		this.account = account;
		this.password = password;
		this.extendAttributes = extendAttributes;
	}
	
	public AuthenticatedToken login() throws AuthenticateException
	{
		 AuthorService authorService = TokenHelper.getTokenService().getAuthorService();

         //构建一个待验证的token
         AuthorHelper authorHelper = TokenHelper.getTokenService().getAuthorHelper();
         
         String sessionid = null;
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
