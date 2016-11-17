/**
 * 
 */
package org.frameworkset.web.auth;

/**
 * @author yinbp
 *
 * @Date:2016-11-17 11:50:31
 */
public abstract class BaseAuthenticatePlugin implements AuthenticatePlugin {

	/**
	 * 
	 */
	public BaseAuthenticatePlugin() {
		// TODO Auto-generated constructor stub
	}

	/** (non-Javadoc)
	 * @see org.frameworkset.web.auth.AuthenticatePlugin#login(org.frameworkset.web.auth.AuthenticateToken)
	 */
	@Override
	public abstract AuthenticatedToken login(AuthenticateToken authenticateToken) throws AuthenticateException ;

	/** (non-Javadoc)
	 * @see org.frameworkset.web.auth.AuthenticatePlugin#login(java.lang.String)
	 */
	@Override
	public  AuthenticateResponse login(String authenticateToken) 
	{
		return null;
	}

}
