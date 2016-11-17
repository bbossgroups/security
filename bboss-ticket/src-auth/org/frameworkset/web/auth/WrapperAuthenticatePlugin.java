/**
 * 
 */
package org.frameworkset.web.auth;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.frameworkset.web.auth.AuthenticateException;
import org.frameworkset.web.auth.AuthenticateMessages;
import org.frameworkset.web.auth.AuthenticatePlugin;
import org.frameworkset.web.auth.AuthenticateResponse;
import org.frameworkset.web.auth.AuthenticateToken;
import org.frameworkset.web.auth.AuthenticatedToken;
import org.frameworkset.web.auth.AuthorHelper;
import org.frameworkset.web.auth.BaseAuthenticatePlugin;
import org.frameworkset.web.token.Application;
import org.frameworkset.web.token.TokenException;
import org.frameworkset.web.token.TokenHelper;

/**
 * @author yinbp
 *
 * @Date:2016-11-16 16:30:05
 */
public class WrapperAuthenticatePlugin extends BaseAuthenticatePlugin {
	private AuthenticatePlugin authenticatePlugin;
	private PublicKey publicKey;
	
	public WrapperAuthenticatePlugin(AuthenticatePlugin authenticatePlugin,PublicKey publicKey) {
		this.authenticatePlugin = authenticatePlugin;
		this.publicKey = publicKey;
	}
	@Override
	public AuthenticateResponse login(String authenticateToken) 
	{
		AuthorHelper authorHelper = new AuthorHelper();
		AuthenticateResponse authenticateResponse = new AuthenticateResponse();
		try {
			if(publicKey == null)
			{
				authenticateResponse.setResultcode("failed");
				authenticateResponse.setError(AuthenticateMessages.getMessage("50010"));//认证服务器公钥加密publicKey为空
				authenticateResponse.setValidateResult(false);
			}
			AuthenticateToken authenticateToken_ = authorHelper.decodeMessageRequest(authenticateToken,this.publicKey);
			
			Application application = TokenHelper.getTokenService().assertApplication(authenticateToken_.getAppcode(), authenticateToken_.getAppsecret());
			PrivateKey privateKey = TokenHelper.getTokenService().getPrivateKey(authenticateToken_.getAppcode());
			if(privateKey == null)
			{
				authenticateResponse.setResultcode("failed");
				authenticateResponse.setError(AuthenticateMessages.getMessage("50009"));//应用加密privateKey为空
				authenticateResponse.setValidateResult(false);
				
			}
			else
			{
				AuthenticatedToken authenticatedToken = login(authenticateToken_) ;
				
				String auhorcode = authorHelper.encodeAuthenticateResponse(authenticatedToken,privateKey);
				authenticateResponse.setAuthorization(auhorcode);
				authenticateResponse.setResultcode("success");
				authenticateResponse.setValidateResult(true);
			}
		} 
		catch(TokenException tokenException)
		{
			authenticateResponse.setResultcode("failed");
			authenticateResponse.setError(AuthenticateMessages.getMessage(tokenException.getMessage()));
			authenticateResponse.setValidateResult(false);
		}
		catch (AuthenticateException e) {
			authenticateResponse.setResultcode("failed");
			authenticateResponse.setError(AuthenticateMessages.getMessage(e.getMessage()));
			authenticateResponse.setValidateResult(false);
			
		}
		catch (Exception e) {
			authenticateResponse.setResultcode("failed");
			authenticateResponse.setError(AuthenticateMessages.getMessage(e.getMessage()));
			authenticateResponse.setValidateResult(false);
			
		}
		catch (Throwable e) {
			authenticateResponse.setResultcode("failed");
			authenticateResponse.setError(AuthenticateMessages.getMessage(e.getMessage()));
			authenticateResponse.setValidateResult(false);
			
		}
		
		return authenticateResponse;
	}

	/** (non-Javadoc)
	 * @see org.frameworkset.web.auth.AuthenticatePlugin#login(org.frameworkset.web.auth.AuthenticateToken)
	 */
	@Override
	public AuthenticatedToken login(AuthenticateToken authenticateToken) throws AuthenticateException {
		// TODO Auto-generated method stub
		return authenticatePlugin.login(authenticateToken);
	}

	public AuthenticatePlugin getAuthenticatePlugin() {
		return authenticatePlugin;
	}

	public void setAuthenticatePlugin(AuthenticatePlugin authenticatePlugin) {
		this.authenticatePlugin = authenticatePlugin;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

}
