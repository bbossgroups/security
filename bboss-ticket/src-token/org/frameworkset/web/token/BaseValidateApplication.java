/**
 * 
 */
package org.frameworkset.web.token;

import org.frameworkset.web.auth.ApplicationSecretEncrpy;

import com.frameworkset.util.SimpleStringUtil;


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
	public  AppValidateResult validateApp(String appid, String secret) throws TokenException {
		
		AppValidateResult result = new AppValidateResult();
		try
		{
			Application _app = getApplication( appid);
			if(_app == null )
			{
				result.setResult(false);
				result.setError("应用["+appid+"]不存在");
				
			}
			else if(!ApplicationSecretEncrpy.encodePassword(secret).equals(_app.getSecret()))
			{
				result.setResult(false);
				result.setError("应用["+appid+"]口令错误"); 
			}
			else
			{
//				Application app = new Application();
//				app.setAppid(appid);
//				app.setSecret(secret);
//				app.setSign(true);
//				
//				app.setTicketlivetime(_app.getTicketlivetimes());
//				app.setCertAlgorithm(_app.getCertAlgorithm());
				result.setApplication(_app);
				result.setResult(true);
			}
				 
			 
		}
		 
		catch(Exception e)
		{
			result.setResult(false);
			result.setError(new StringBuilder().append("获取应用[appid=").append(appid).append("]失败:").append(SimpleStringUtil.exceptionToString(e)).toString());
//			throw new ValidateApplicationException("获取应用[appid="+appid+"]失败",e);
		}
		
		return result;
//		AppValidateResult result = new AppValidateResult();
//		Application app = new Application();
//		app.setAppid(appid);
//		app.setSecret(secret);
//		app.setSign(true);
//		app.setCertAlgorithm("RSA");
//		app.setTicketlivetime(-2);
//		result.setApplication(app);
//		result.setResult(true);
//		 
//		return result;
	}
	
	/**
	 * 获取应用信息,应用信息中必须包含以下信息：
	 * Application app = new Application();
		app.setAppid(appid);
		app.setSecret(secret);
		app.setSign(true);
		app.setCertAlgorithm("RSA");
		app.setTicketlivetime(-2);
	 * @param appid
	 * @return
	 */
	public abstract Application getApplication(String appid) throws ValidateApplicationException;

}
