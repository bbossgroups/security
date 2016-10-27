package org.frameworkset.platform.security;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.security.authorization.AccessException;

public class AppPermissionModule extends DefaultPermissionModule {

	public AppPermissionModule() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 图形校验码校验
	 * HttpServletRequest request
	 * String rand = request.getParameter("rand");
	 *  String session_rand = (String) session
                                    .getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
                            session.removeAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
                            if (session_rand == null || (!session_rand.equalsIgnoreCase(rand))) {
                                throw new AccessException("验证码错误!");
                            }
	 * @param code
	 * @param session
	 * @return
	 */
	public boolean validatecode(HttpServletRequest request) throws AccessException
	{
		String rand = request.getParameter("rand");
		
		String session_rand = (String) request.getSession()
	                                    .getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		request.getSession().removeAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
        if (session_rand == null || (!session_rand.equalsIgnoreCase(rand))) {
            throw new AccessException("验证码错误!");
        }
        return true;
	}

}
