/**
 * 
 */
package org.frameworkset.web.auth;

/**
 * @author yinbp
 *
 * @Date:2016-11-16 15:44:29
 */
public interface AuthenticatePlugin {
	public AuthenticatedToken login(AuthenticateToken authenticateToken) throws AuthenticateException;
	public AuthenticateResponse login(String authenticateToken) ;
}
