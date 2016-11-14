/**
 * 
 */
package org.frameworkset.web.token;

/**
 * @author yinbp
 *
 * @Date:2016-11-13 12:24:57
 */
public abstract class BaseValidateApplication implements ValidateApplication {

	/**
	 * 
	 */
	public BaseValidateApplication() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.frameworkset.web.token.ValidateApplication#checkApp(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean checkApp(String appid, String secret) throws TokenException {
		AppValidateResult result = validateApp(appid, secret);
		if(result == null || !result.getResult())
			return false;
		else
		{
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see org.frameworkset.web.token.ValidateApplication#validateApp(java.lang.String, java.lang.String)
	 */
	@Override
	public abstract AppValidateResult validateApp(String appid, String secret) throws TokenException ;

}
