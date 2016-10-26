package org.frameworkset.platform.config;

import javax.servlet.ServletContext;

/**
 * 系统初始化接口
 * 系统中所有需要在启动时加载的程序，可实现一个Systeminit接口，然后配置到
 * config-manager.xml文件的systems元素中，应用在启动时将自动加载并运行所有的初始化
 * 程序，例如：
 * <systems>
 *		<system class="com.frameworkset.platform.sysmgrcore.manager.SysmanagerInit"/>
 *		<system class="com.frameworkset.platform.cms.CMSInit"/>
 * </systems>
 * <p>Title: SystemInit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 * @Date 2007-5-17 19:00:03
 * @author biaoping.yin
 * @version 1.0
 */
public interface SystemInit  {
	/**
	 * 初始化上下文
	 * @param context
	 */
	public void setContext(ServletContext context);
	/**
	 * 系统初始化接口
	 *
	 */
	public void init()  throws InitException;
	/**
	 * 系统注销接口
	 *
	 */
	public void destroy() throws DestroyException;

}
