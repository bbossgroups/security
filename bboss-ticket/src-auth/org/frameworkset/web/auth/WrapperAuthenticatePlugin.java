/**
 * 
 */
package org.frameworkset.web.auth;

import java.security.Key;
import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Date;

import org.frameworkset.security.KeyCacheUtil;
import org.frameworkset.web.token.AppValidateResult;
import org.frameworkset.web.token.Application;
import org.frameworkset.web.token.TokenException;
import org.frameworkset.web.token.TokenHelper;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author yinbp
 *
 * @Date:2016-11-16 16:30:05
 */
public class WrapperAuthenticatePlugin extends BaseAuthenticatePlugin {
	private AuthenticatePlugin authenticatePlugin;
	private Key publicKey;
	
	public WrapperAuthenticatePlugin(AuthenticatePlugin authenticatePlugin,Key publicKey) {
		this.authenticatePlugin = authenticatePlugin;
		this.publicKey = publicKey;
	}
	@Override
	public AuthenticateResponse login(String authenticateToken) 
	{
		AuthenticateResponse authenticateResponse = new AuthenticateResponse();
		try {
			if(publicKey == null)
			{
				authenticateResponse.setResultcode("failed");
				authenticateResponse.setError(AuthenticateMessages.getMessage("50010"));//认证服务器公钥加密publicKey为空
				authenticateResponse.setValidateResult(false);
				return authenticateResponse;
			}
			AuthenticateToken authenticateToken_ = AuthorHelper.decodeMessageRequest(authenticateToken,this.publicKey);
			
			AppValidateResult validateResult = TokenHelper.getTokenService().validateApplication(authenticateToken_.getAppcode(), authenticateToken_.getAppsecret());
			if(!validateResult.getResult())
			{
				authenticateResponse.setResultcode("failed");
				authenticateResponse.setError(validateResult.getError());//认证服务器公钥加密publicKey为空
				authenticateResponse.setValidateResult(false);
				return authenticateResponse;
			}
			Application application = validateResult.getApplication();
			authenticateToken_.setLivetimes(application.getTicketlivetime());
			Key privateKey = null;
//			SignatureAlgorithm.RS512
			SignatureAlgorithm signatureAlgorithm = null;
			if(application.getCertAlgorithm() == null  || application.getCertAlgorithm().equals(KeyCacheUtil.ALGORITHM_RSA)){
//				privateKey = TokenHelper.getTokenService().getPrivateKey(authenticateToken_.getAppcode());
				signatureAlgorithm = SignatureAlgorithm.RS512;
			}
			else
			{
				
				signatureAlgorithm = SignatureAlgorithm.forName(application.getCertAlgorithm());
			}
			privateKey = TokenHelper.getTokenService().getSimpleKey(authenticateToken_.getAppcode(),application.getCertAlgorithm()).getPriKey();
			
			if(privateKey == null)
			{
				authenticateResponse.setResultcode("failed");
				authenticateResponse.setError(AuthenticateMessages.getMessage("50009"));//应用加密privateKey为空
				authenticateResponse.setValidateResult(false);
				
			}
			else
			{
				AuthenticatedToken authenticatedToken = login(authenticateToken_) ;
				Date expiration = null;
				int ticketlivetimes = 0;
				if(authenticateToken_.getLivetimes() > 0 )
				{
					ticketlivetimes = (int)authenticateToken_.getLivetimes();
				}
				else if(authenticateToken_.getLivetimes()  == -2)
				{
					ticketlivetimes =  (int)TokenHelper.getTokenService().getTicketdualtime() ;
				}
				if(authenticateToken_.getSessionid() == null)
				{
					if(ticketlivetimes > 0 )
					{
						expiration = addDateSeconds(new Date(), (int)(ticketlivetimes /1000));
					}
					
				}		
			 
					
				String auhorcode = AuthorHelper.encodeAuthenticateResponse(authenticatedToken,signatureAlgorithm,privateKey,expiration,ticketlivetimes);
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
	
	public static Date addDateSeconds(Date date,int seconds)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, seconds);
		return c.getTime();
		
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

	public Key getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(Key publicKey) {
		this.publicKey = publicKey;
	}

}
