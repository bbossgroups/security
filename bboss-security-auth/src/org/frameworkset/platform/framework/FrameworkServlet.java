package org.frameworkset.platform.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.security.AccessControl;
import com.frameworkset.util.VelocityUtil;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description: 确定系统的主体框架,自由生成系统的
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author biaoping.yin
 * @version 1.0
 */
public class FrameworkServlet extends HttpServlet implements java.io.Serializable
{
	private static Logger log = Logger.getLogger(FrameworkServlet.class);

	public static final String CONTENT_TYPE = "text/html; charset=UTF-8";

//	private String configFile = "module.xml";

	private static boolean inited = false;

//	private static boolean refresh = false;

//	private String userAccount = "biaoping.yin";

//	private String currentPath = "";

	public static final String CURRENTPATH_KEY = "current_path";

//	private boolean logined;

//	AccessControl control = null;
	public static HttpSession getHttpSession(HttpServletRequest request,int line)
	{
		HttpSession session = request.getSession(false);
		if(session == null)
		{
			String remoteip = com.frameworkset.util.StringUtil.getClientIP(request);
			String remoteurl = request.getRequestURI();
			log_("line[" +line+"] [remoteip=" + remoteip + ",remoteurl=" + remoteurl + "] framework servlet session is null." );
			return null;
		}
		if(session.isNew())
		{	
			String remoteip = com.frameworkset.util.StringUtil.getClientIP(request);
			String remoteurl = request.getRequestURI();
			log_("line[" +line+"] [remoteip=" + remoteip + ",remoteurl=" + remoteurl + "] framework servlet new session id=" + session.getId() );
		}
		return session;
	}
	
	/**
	 * 返回当前子系统的菜单路径
	 * @param request
	 * @param response
	 * @return
	 */
	public static String getCurrentMenuPath(HttpServletRequest request,
			HttpServletResponse response)
	{
		AccessControl control = AccessControl.getAccessControl();
		if(control.isGuest())
			return null;
		String path = request.getParameter(Framework.MENU_PATH);
		
//		String subsystem = getSubSystem(request, response,control.getUserAccount());
		String subsystem = control.getCurrentSystemID();//getSubSystem(request, response,control.getUserAccount());
		Framework framework = Framework.getInstance(subsystem);
		
		if (path == null)
		{			
			path = getCurrentPath(subsystem, getHttpSession(request,93));
		}

		if (path == null)
		{
			MenuItem publicitem = framework.getPublicItem();
			if (!publicitem.isMain())
			{
				path = getDefaultPathFromCookie(request, response,subsystem, control
						.getUserAccount());

			}
			else
			{
				path = publicitem.getPath();
			}

		}

		if (path == null)
		{
			MenuItem item = framework.getDefaultItem();
			path = item.getPath();
		}

		if (path == null)
		{
			MenuItem item = framework.getPublicItem();
			path = item.getPath();
		}

		return path;
	}

	/**
	 * 从session中获取当前的菜单路径
	 * @param subsystem
	 * @param session
	 * @return
	 */
	public static String getCurrentPath(String subsystem, HttpSession session)
	{
		
		String path = null;
		if(session != null)
		{
			path = (String) session.getAttribute(CURRENTPATH_KEY + "_"
				+ subsystem);
		}
		Framework framework = Framework.getInstance(subsystem);
		if (path != null && path.equals(framework.getPublicItem().getPath()))
			return path;
		if (path != null && Framework.getMenu(path) == null)
		{
			path = null;
		}
		return path;
	}

	/**
	 * 返回session中但前菜单对应的对象
	 * @param subsystem
	 * @param session
	 * @return
	 */
	public static MenuItem getCurrent(String subsystem, HttpSession session)
	{
		String path = getCurrentPath(subsystem, session);

		Framework framework = Framework.getInstance(subsystem);
		if (path == null)
			return null;
		if (framework.getPublicItem().equals(path))
			return framework.getPublicItem();

		return Framework.getMenu(path);
	}

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		// String approot = config.getInitParameter("approot");
		

		if (!inited)
		{
			synchronized(this.getClass())
			{
				ConfigManager.getInstance().init();
				String approot = ConfigManager.getInstance().getConfigValue("approot");
				String realpath = config.getServletContext().getRealPath("/");
				if (realpath == null)
					realpath = approot;
				if (realpath == null)
					realpath = "";
				VelocityUtil.init(realpath);
				
//				this.configFile = config.getInitParameter("configFile");
//				if (configFile == null || configFile.trim().equals(""))
//					configFile = "module.xml";
				Framework.getInstanceWithContext(config.getServletContext()).init((String)null);
				inited = true;
	
				
			}
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		
		//System.out.println("request uri:" + req.getRequestURL());
		// if(!control.checkAccess(req,resp))
		// return ;
//		System.out.println("Frameworkservlet:" + this);
//		if(control == null)
//		{
		AccessControl control = AccessControl.getAccessControl();
//		JspFactory fac=JspFactory.getDefaultFactory();		
//		PageContext pageContext=fac.getPageContext(this, req,resp, null, false, JspWriter.DEFAULT_BUFFER <= 0?8192:JspWriter.DEFAULT_BUFFER, true); 
//		control.setPageContext(pageContext);
		
//		String OUT_USER_ACCOUNT_KEY = req.getParameter("OUT_USER_ACCOUNT_KEY");
		boolean logined = false;
//		if(OUT_USER_ACCOUNT_KEY == null)
//		{
//			logined = control.checkAccess(req, resp);
//		}
//		else
		{
			logined = control.checkAccess(req, resp);
		}
		if (!logined)
			return;
//		}
//		userAccount = control.getUserAccount();
		this.process(req, resp,control);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		doGet(req, resp);
	}

	/**
	 * 从cookie中获取缺省路径
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param userAccount
	 *            String 登陆用户
	 * @return String
	 */
	public static String getDefaultPathFromCookie(HttpServletRequest req,HttpServletResponse resp,
			String subsystem, String userAccount)
	{

		String path = null;
		Cookie[] cookies = req.getCookies();
		Cookie cookie = null;
		if (subsystem == null)
			subsystem = getSubSystem(req, resp,userAccount);
		String cookiekey = userAccount + "_" + Framework.COOKIE_NAME + "_"
				+ subsystem;
		try
		{
			for (int i = 0; cookies != null && i < cookies.length; i++)
			{

				cookie = cookies[i];
				if (cookie.getName().equals(cookiekey))
				{
					path = cookie.getValue();
					break;
				}
			}
		}
		catch (Exception e)
		{

		}
		if (path != null
				&& Framework.getMenu(path) == null)
		{
			cookie.setValue(null);
			cookie.setMaxAge(1);
			path = null;
		}
		return path;
	}

	// public static String getDefaultPathFromCookie(HttpServletRequest
	// req,String userAccount)
	// {
	// return getDefaultPathFromCookie( req,null, userAccount);
	//        
	// }

	/**
	 * 从cookie中获取缺省的菜单项目
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param userAccount
	 *            String 登陆用户
	 * @return String
	 */
	public static MenuItem getDefaultMenuFromCookie(HttpServletRequest req,HttpServletResponse resp,
			String userAccount)
	{
		String subsystem = getSubSystem(req, resp,userAccount);
		String path = getDefaultPathFromCookie(req, resp,subsystem, userAccount);
		if (path != null)
			return Framework.getMenu(path);
		return null;
	}

	public static String getSubSystemFromeCookie(HttpServletRequest req,
			String userName)
	{
		Cookie cookies[] = req.getCookies();
		/**
		 * 获取子系统的cookie键值
		 */
		String name = AccessControl.SUBSYSTEM_COOKIE + "_" + userName;
		String temp = null;
		for (int i = 0; cookies != null && i < cookies.length; i++)
		{
			Cookie co = cookies[i];
			if (co.getName().equals(name))
			{
				return temp = co.getValue();
			}

		}
		return null;
	}

	public static void setSubSystemToCookie(HttpServletResponse resp,
			String userName, String subsystem)
	{
		if (resp == null)
			return;
		String name = AccessControl.SUBSYSTEM_COOKIE + "_" + userName;

		Cookie co = new Cookie(name, subsystem);
		co.setMaxAge(86400);
		resp.addCookie(co);

	}
	
	public static void log_(String msg)
	{
		
		Date date = new java.util.Date() ; 
//		System.err.println("FrameworkServlet--" + date + "===============================================");
		System.err.println("FrameworkServlet--[" + date + "]"+msg);
//		System.err.println("FrameworkServlet--" + date +"================================================");
	}

	/**
	 * 获取子系统模块标识
	 * 
	 */
	public static String getSubSystem(HttpServletRequest req, HttpServletResponse resp, String userName)
	{
		String t_path = req.getParameter(Framework.MENU_PATH);
		if(t_path != null)
			return Framework.getSubsystemFromPath(t_path);
//		String __directitem = req.getParameter("__directitem");
//		log_("__directitem = " + __directitem);
		String subsystem_q = req.getParameter(Framework.SUBSYSTEM);
		HttpSession session = FrameworkServlet.getHttpSession(req,341);
//		if(__directitem != null)
		{
			if((subsystem_q == null || subsystem_q.equals("")) && session != null)
			{
				subsystem_q = (String)session.getAttribute(
					Framework.SUBSYSTEM);
			}
			return subsystem_q != null ? subsystem_q : "module";
		}
		
		
//		String subsystem_s = null;
//		if(session != null)
//		{
//			subsystem_s = (String)session.getAttribute(
//				Framework.SUBSYSTEM);
//		}
//
//		if (subsystem_q == null || subsystem_q.equals(""))
//		{
//
//			String temp = getSubSystemFromeCookie(req, userName);
//
//			if (temp != null && !temp.equals(""))
//			{
//				if (subsystem_s != null && !subsystem_s.equals(""))
//				{
//					if (!temp.equals(subsystem_s))
//					{
//						setSubSystemToCookie(resp, userName, subsystem_s);
//						
//					}
//					subsystem_q = subsystem_s;
//				}
//				else
//				{
//					subsystem_q = temp;
//					if(session != null)
//					{
//						session.setAttribute(Framework.SUBSYSTEM,subsystem_q);
//					}
//				}
//				
//
//			}
//			else
//			{
//				if (subsystem_s != null && !subsystem_s.equals(""))
//					subsystem_q = subsystem_s;
//				else
//				{
//					subsystem_q = "module";					
//				}
//				if(session != null)
//				{					
//					session.setAttribute(Framework.SUBSYSTEM, subsystem_q);
//				}
//				setSubSystemToCookie(resp, userName, subsystem_q);
//			}
//		}
//		else
//		{
//			if(session != null)
//			{					
//				session.setAttribute(Framework.SUBSYSTEM, subsystem_q);
//			}
//			setSubSystemToCookie(resp, userName, subsystem_q);
//		}

//		return subsystem_q;
	}

	private Item getCurrentItem(HttpServletRequest req, HttpServletResponse resp,AccessControl control,String path)
	{
		
		boolean securityEnabled = ConfigManager.getInstance().securityEnabled();
		/**
		 * 获取子系统模块标识
		 */
		String subsystem = getSubSystem(req,resp,control.getUserAccount()); //有取session
		HttpSession session = FrameworkServlet.getHttpSession(req,417);
		if (path != null)
		{
			String temp_subsystem = Framework.getSubsystemFromPath(path);
			// 当前的模块和从缓冲中获取的模块不一致则更新缓冲中的模块
			if (!subsystem.equals(temp_subsystem))
			{
				subsystem = temp_subsystem;
				if(session != null)
				{
					session.setAttribute(Framework.SUBSYSTEM, subsystem);
				}
				setSubSystemToCookie(resp,control.getUserAccount(), subsystem);
			}
		}

		Framework framework = Framework.getInstance(subsystem);
		Item item = null;
		Item publicitem = framework.getPublicItem();
		String temp_path = null;

		

		// 定义是否需要进行权限检测的控制变量，一般情况不要再次进行权限检测，以下情况需要重新进行权限检测：
		// 1.当栏目路径从session中获取时
		// 2.当栏目路径从cookie中获取时
		// 3.当栏目是缺省的栏目时
		boolean needcheck = false;
		// 如果是publicitem时无需设置path cookie
		boolean setcookie = true;

		// 如果页面path为空，从session中获取path

		if (path == null)
		{
			//注释by biaoping.yin 开始
			if(!publicitem.isMain())
			{
				path = getCurrentPath(subsystem, session);
			}
			//注释by biaoping.yin 结束
			// 如果path不为空设置权限检测标记为true
//			if (path != null)
//				temp_path = path;
		}

		// 如果页面path为空并且没有设置系统主页，从cookie中获取当前path，并且设置权限检测标记为true
		if (path == null)
		{
			//注释by biaoping.yin 开始
			if(!publicitem.isMain())
				path = getDefaultPathFromCookie(req, resp,subsystem, control.getUserAccount());
			//注释by biaoping.yin 结束
			if (path != null)
			{
				
				temp_path = path;
			}
			else
			{
				// 系统必须指定缺省的栏目item
				item = framework.getDefaultItem();
				if (item != null)
				{
					path = item.getPath();
					
					temp_path = path;
				}
			}
			if (publicitem.isMain())
			{
				item = publicitem;
				path = publicitem.getPath();
				setcookie = false;
				
			}
			// path可能为空
		}

		if (path != null)
		{
			if (path.equals(framework.getPublicItem().getPath()))
			{
				item = framework.getPublicItem();
				setcookie = false;
				
			}
			else
			{
				item = framework.getItem(path);

				if (item != null)
				{
					temp_path = path;
				}
				else
				{
					item = framework.getPublicItem();
					setcookie = false;
					
				}
			}
		}

		// 如果启用了安全机制，则检测当前用户拥有item的访问权限
		// 如果没有访问权，则获取用户拥有权限的第一个资源作为缺省的栏目
		if (securityEnabled && item != null)
		{
			if(!item.isMain())
			{
				if (!control.checkPermission(item.getId(),
						AccessControl.VISIBLE_PERMISSION,
						AccessControl.COLUMN_RESOURCE))
				{
					item = null;
					// 本处性能需要优化
					MenuHelper temp = new MenuHelper(subsystem, control);
					temp.getItems();
					if (temp.getFirstAuthorItem() != null)
					{
						item = temp.getFirstAuthorItem();
					}
					else
					{
						ModuleQueue modules = temp.getModules();
						if (temp.getFirstAuthorItem() != null)
						{
							item = temp.getFirstAuthorItem();
						}
						else
						{
							item = getPermissionItem(temp, modules);
						}
					}
				}
				// 如果item不为空，
				if (item != null)
				{
					temp_path = item.getPath();
					path = temp_path;
				}
				else
				{
					temp_path = null;
					item = framework.getPublicItem();
					path = item.getPath();
					temp_path = path;
					setcookie = false;
				}
			}
			else if(temp_path != null)
			{
				Item temp_item = framework.getItem(temp_path);
				if (!control.checkPermission(temp_item.getId(),
						AccessControl.VISIBLE_PERMISSION,
						AccessControl.COLUMN_RESOURCE))
				{
					temp_item = null;
					// 本处性能需要优化
					MenuHelper temp = new MenuHelper(subsystem, control);
					temp.getItems();
					if (temp.getFirstAuthorItem() != null)
					{
						temp_item = temp.getFirstAuthorItem();
					}
					else
					{
						ModuleQueue modules = temp.getModules();
						if (temp.getFirstAuthorItem() != null)
						{
							temp_item = temp.getFirstAuthorItem();
						}
						else
						{
							temp_item = getPermissionItem(temp, modules);
						}
					}
				}
				// 如果temp_item不为空，
				if (temp_item != null)
				{
					temp_path = temp_item.getPath();
					path = temp_path;
				}
				else
				{
					temp_path = null;	
					path = null;
				}
				
			}
		}
		// 设置页面主题框架

		if (setcookie)
		{
			Cookie newCookie = new Cookie(control.getUserAccount() + "_"
					+ Framework.COOKIE_NAME + "_" + subsystem, temp_path);
			newCookie.setMaxAge(86400);
			resp.addCookie(newCookie);
			if(session != null)
			{
				session.setAttribute(CURRENTPATH_KEY + "_" + subsystem,
						temp_path);
			}
		}
		else
		{
			if(session != null)
			{
				session.removeAttribute(CURRENTPATH_KEY + "_" + subsystem);
			}
			Cookie newCookie = new Cookie(control.getUserAccount() + "_"
					+ Framework.COOKIE_NAME + "_" + subsystem, null);
			newCookie.setMaxAge(1);
			
			resp.addCookie(newCookie);
		}

//		this.currentPath = path;
		if (item != null && item.isMain() && temp_path != null)
		{
			if(session != null)
			{
				session.setAttribute("menu_temp_path_" + subsystem,
					temp_path);
			}
		}
		if(path == null || item == null)
		{
			return framework.getPublicItem();
		}
		return item;
	}
	protected void process(HttpServletRequest req, HttpServletResponse resp,AccessControl control)
			throws ServletException, IOException
	{
		

		resp.setContentType(CONTENT_TYPE);
		PrintWriter out = resp.getWriter();
		
		// 根据路径获取系统所属的模块

//		 获取框架容器类型参数
		String path = req.getParameter(Framework.MENU_PATH);
		/**
		 * 是否是第一次进入菜单，第一次进入时需要修改session，cookie中的值，并且进行权限检测，
		 * 第二次进入时无需设置session和cookie
		 */
		String __directitem = req.getParameter("__directitem");

		Framework framework = null;
		Item item = null;
		if(__directitem != null)
		{
			framework = Framework.getInstance(Framework.getSubsystemFromPath(path));
			item = (Item)Framework.getMenu(path);
			if(item == null)
				item = framework.getPublicItem();
				
		}
		else
		{
			
			item = getCurrentItem(req, resp, control,path);
			//log_("First into menu[" + item.getPath() + "]");
			framework = Framework.getInstance(Framework.getSubsystemFromPath(item.getPath()));
		}
		String stype = req.getParameter(Framework.MENU_TYPE);
		// 定义框架容器类型，缺省为根容器
		int type = Framework.ROOT_CONTAINER;
		// 设置容器类型
		if (stype == null || stype.trim().equals(""))
			type = Framework.ROOT_CONTAINER;
		else
			type = Integer.parseInt(stype);
		
		
//		Framework framework = Framework.getInstance(temp_subsystem);
//		if (path == null || item == null)
//		{
//			framework.evaluateFrameWork(req,framework.getPublicItem(), type, out);
//		}
//		else
//		{
//			framework.evaluateFrameWork(req,item, type, out);
//		}
		framework.evaluateFrameWork(req,item, type, out,control);
		out.flush();

	}

	private Item getPermissionItem(MenuHelper temp, ModuleQueue modules)
	{

		Item item = null;
		for (int i = 0; i < modules.size(); i++)
		{
			Module module = modules.getModule(i);
			module.getItems();
			if (temp.getFirstAuthorItem() != null)
				return temp.getFirstAuthorItem();
			else
			{
				item = getPermissionItem(temp, module.getSubModules());
				if (item != null)
					return item;
			}
		}
		return item;
	}

	/**
	 * removeDefaultPathFromCookie
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param string
	 *            String
	 */
	private static void removeDefaultPathFromCookie(HttpServletRequest req,
			String string)
	{

	}
	
	public static void main(String[] a)
	{
		Framework.getInstance();
	}

	@Override
	public void destroy() {
		ConfigManager.destroy();
		
		super.destroy();
	}

}
