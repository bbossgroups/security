/*
 * @(#)AppBomControler.java
 * 
 * Copyright @ 2001-2011 SANY Group Co.,Ltd.
 * All right reserved.
 * 
 * 这个软件是属于三一集团有限公司机密的和私有信息，不得泄露。
 * 并且只能由三一集团有限公司内部员工在得到许可的情况下才允许使用。
 * This software is the confidential and proprietary information
 * of SANY Group Co, Ltd. You shall not disclose such
 * Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * SANY Group Co, Ltd.
 */
package org.frameworkset.platform.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.frameworkset.web.interceptor.AuthenticateFilter;
import org.frameworkset.web.servlet.handler.HandlerMeta;

import org.frameworkset.platform.config.ConfigManager;


/**
 * 公共开发平台页面保护拦截器
 * @author yinbp
 *
 */
public class SYSAuthenticateFilter extends AuthenticateFilter
{
	private static Logger log = Logger.getLogger(SYSAuthenticateFilter.class);
	private boolean iswebsealserver = false;
	public SYSAuthenticateFilter()
	{
		iswebsealserver = ConfigManager.getInstance()
				.getConfigBooleanValue("isWebSealServer", false);
	}
	/**
	 * 用户身份验证
	 */
	protected boolean check(HttpServletRequest request,
			HttpServletResponse response, HandlerMeta handlerMeta)
	{
		AccessControl control = AccessControl.getInstance();
		boolean result = control.checkAccess(request, response, false);
		
		if(result)
		{
			
//			System.out.println("iswebsealserver filter:" + iswebsealserver);
			if(iswebsealserver )
			{
//				System.out.println("iswebsealserver filter:" + iswebsealserver);
				String uimusername = request.getHeader("iv-user");
				String useraccount = control.getUserAccount();
				
				if(uimusername != null && !uimusername.equals(useraccount))
				{
					log.warn("Filter uim user is not same with session user:[uim username=" + uimusername + ",session username=" + useraccount);
					HttpSession session = request.getSession(false);
					if(session != null)
						AccessControl.resetSession(session);
					result = false;
				}
				else
				{
					request.setAttribute(AccessControl.accesscontrol_request_attribute_key,control);
				}
			}
			else
			{
				request.setAttribute(AccessControl.accesscontrol_request_attribute_key,control);
			}
		}
		return result;
		
		
	}

	/**
	 * url权限判断
	 */
	@Override
	protected boolean checkPermission(HttpServletRequest request,
			HttpServletResponse response, HandlerMeta handlerMeta, String uri) {
		AccessControl control = AccessControl.getAccessControl();
		return control.checkURLPermission(uri);
	}

}
