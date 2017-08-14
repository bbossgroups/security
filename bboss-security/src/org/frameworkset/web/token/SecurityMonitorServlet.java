package org.frameworkset.web.token;

import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.frameworkset.security.session.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 独立的token生命周期管理服务，在启用统一的mongodb存储token和ticket情况下有用
 * @author yinbp
 *
 */
public class SecurityMonitorServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(SecurityMonitorServlet.class);
	private static Method getTokenService;
	static 
	{
		try {
			Class clazz = Class.forName("org.frameworkset.web.token.TokenHelper");
			getTokenService = clazz.getMethod("getTokenService");
		} catch (ClassNotFoundException e) {
			 
		} catch (NoSuchMethodException e) {
			 
		} catch (SecurityException e) {
			 
		}
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		try {
			
			getTokenService.invoke(null);
			
		} catch (Throwable e) {
			log.warn("",e);
		}
		
		try {
			
			SessionUtil.getSessionManager();
			
		} catch (Throwable e) {
			log.warn("",e);
		}
	}

}
