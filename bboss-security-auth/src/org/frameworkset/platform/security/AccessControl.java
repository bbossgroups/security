/*****************************************************************************
 *                                                                           *
 *  This file is part of the frameworkset distribution.                      *
 *  Documentation and updates may be get from  biaoping.yin the author of    *
 *  this framework							     							 *
 *                                                                           *
 *  Sun Public License Notice:                                               *
 *                                                                           *
 *  The contents of this file are subject to the Sun Public License Version  *
 *  1.0 (the "License"); you may not use this file except in compliance with *
 *  the License. A copy of the License is available at http://www.sun.com    *
 *                                                                           *
 *  The Original Code is tag. The Initial Developer of the Original          *
 *  Code is biaoping.yin. Portions created by biaoping.yin are Copyright     *
 *  (C) 2004.  All Rights Reserved.                                          *
 *                                                                           *
 *  GNU Public License Notice:                                               *
 *                                                                           *
 *  Alternatively, the contents of this file may be used under the terms of  *
 *  the GNU Lesser General Public License (the "LGPL"), in which case the    *
 *  provisions of LGPL are applicable instead of those above. If you wish to *
 *  allow use of your version of this file only under the  terms of the LGPL *
 *  and not to allow others to use your version of this file under the SPL,  *
 *  indicate your decision by deleting the provisions above and replace      *
 *  them with the notice and other provisions required by the LGPL.  If you  *
 *  do not delete the provisions above, a recipient may use your version of  *
 *  this file under either the SPL or the LGPL.                              *
 *                                                                           *
 *  biaoping.yin (yin-bp@163.com)                                            *
 *                                                                           *
 *****************************************************************************/
package org.frameworkset.platform.security;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.frameworkset.security.AccessControlInf;
import org.frameworkset.security.DESCipher;
import org.frameworkset.spi.SPIException;
import org.frameworkset.web.token.TokenHelper;
import org.frameworkset.web.token.TokenStore;

import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.config.model.Operation;
import org.frameworkset.platform.config.model.OperationQueue;
import org.frameworkset.platform.config.model.ResourceInfo;
import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.FrameworkServlet;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.MenuHelper;
import org.frameworkset.platform.framework.MenuItem;
import org.frameworkset.platform.framework.MenuQueue;
import org.frameworkset.platform.framework.Module;
import org.frameworkset.platform.framework.ModuleQueue;
import org.frameworkset.platform.framework.SubSystem;
import org.frameworkset.platform.resource.Resource;
import org.frameworkset.platform.resource.ResourceManager;
import org.frameworkset.platform.security.authentication.Credential;
import org.frameworkset.platform.security.authentication.LoginContext;
import org.frameworkset.platform.security.authentication.LoginException;
import org.frameworkset.platform.security.authentication.LogoutCallbackHandler;
import org.frameworkset.platform.security.authentication.SimpleLoginContext;
import org.frameworkset.platform.security.authentication.Subject;
import org.frameworkset.platform.security.authentication.UsernamePasswordCallbackHandler;
import org.frameworkset.platform.security.authorization.AccessException;
import org.frameworkset.platform.security.authorization.AuthPrincipal;
import org.frameworkset.platform.security.authorization.AuthRole;
import org.frameworkset.platform.security.authorization.AuthUser;
import org.frameworkset.platform.security.authorization.impl.AppSecurityCollaborator;
import org.frameworkset.platform.security.authorization.impl.BaseAuthorizationTable;
import org.frameworkset.platform.security.authorization.impl.P;
import org.frameworkset.platform.security.authorization.impl.PermissionToken;
import org.frameworkset.platform.security.context.AppAccessContext;
import org.frameworkset.platform.security.menu.MenuItemU;
import org.frameworkset.platform.security.util.CookieUtil;
import org.frameworkset.platform.util.LogManagerInf;
import com.frameworkset.util.StringUtil;

/**
 * @author biaoping.yin created on 2005-9-29 version 1.0
 */
public class AccessControl implements AccessControlInf{
	private static final Logger log = Logger.getLogger(AccessControl.class);

	// public static final String LOGINCONTEXT_CACHE_KEY = "LOGIN_CONTEXT";
	public static final String accesscontrol_request_attribute_key = "com.frameworkset.platform.security.AccessControl";
	public static final String REMOTEADDR_CACHE_KEY = "REMOTEADDR_CACHE_KEY";
	
	public static final String MACADDR_CACHE_KEY = "MACADDR_CACHE_KEY";
	
	public static final String MACHINENAME_CACHE_KEY = "MACHINENAME_CACHE_KEY";
	public static final String LOGINSTYLE_CACHE_KEY = "LOGINSTYLE_CACHE_KEY";
	
	public static final String SERVER_IP_KEY = "SERVER_IP_KEY";
	public static final String SERVER_PORT_KEY = "SERVER_PORT_KEY";

	public static final String PRINCIPAL_INDEXS = "PRINCIPAL_INDEXS";

	public static final String PRINCIPALS_COOKIE = "PRINCIPALS_COOKIE";

	public static final String CREDENTIAL_INDEXS = "CREDENTIAL_INDEXS";

	public static final String SUBSYSTEM_ID = "subsystem_id";

	public static final String SUBSYSTEM_COOKIE = "SUBSYSTEM_COOKIE_ID";

	public static final String HEAD_SPLIT = "^@^";

	public static final String PRINCIPAL_SPLIT = "^_^";

	public static final String IDENTITY_SPLIT = "#$#";

	public static final String PRINCIPAL_CREDENTIAL_SPLIT = "^|^";

	public static final String CREDENTIAL_SPLIT = "@|@";

	public static final String CREDENTIAL_ATTRIBUTE_SPLIT = "@^@";

	public static final String ATTRIBUTE_SPLIT = "@~@";

	public static final String VISIBLE_PERMISSION = "visible";

	public static final String UNVISIBLE_PERMISSION = "unvisible";

	/**
	 * 写权限
	 */
	public static final String WRITE_PERMISSION = "write";

	/**
	 * 读权限
	 */
	public static final String READ_PERMISSION = "read";

	/**
	 * 读写权限
	 */
	public static final String READ_WRITE_PERMISSION = "read_write";

	/**
	 * 批量角色授予
	 */
	public static final String BAT_ROLE_ADD_PERMISSION = "batroleadd";

	/**
	 * 批量岗位授予
	 */
	public static final String BAT_JOB_ADD_PERMISSION = "batjobadd";

	/**
	 * 批量加入机构
	 */
	public static final String BAT_ORG_ADD_PERMISSION = "batorgadd";

	/**
	 * 批量资源操作授予
	 */
	public static final String BAT_RES_ADD_PERMISSION = "batresadd";

	/**
	 * 用户排序
	 */
	public static final String SORT_USER_PERMISSION = "sortuser";

	/**
	 * 机构排序
	 */
	public static final String SORT_ORG_PERMISSION = "sortorg";

	/**
	 * 用户调入调出
	 */
	public static final String USER_MOVE_PERMISSION = "usermove";

	/**
	 * 新增权限
	 */
	public static final String ADD_PERMISSION = "create";

	/**
	 * 修改权限
	 */
	public static final String UPDATE_PERMISSION = "edit";

	/**
	 * 删除权限
	 */
	public static final String DELETE_PERMISSION = "delete";

	/**
	 * 送审权限
	 */
	public static final String DELIVER_PERMISSION = "deliver";

	/**
	 * 撤销送审权限
	 */
	public static final String WITHDRAW_DELIVER_PERMISSION = "withdrawdeliver";
	/**
	 * 提交发布权限
	 */
	public static final String SUBPUBLISH_PERMISSION = "subpublish";
	/**
	 * 文档发布
	 */
	public static final String DOCPUBLISH_PERMISSION = "docpublish";
	/**
	 * 归档权限
	 */
	public static final String ARCHIVE_PERMISSION = "archive";
	/**
	 * 转发权限
	 */
	public static final String TRANSMIT_PERMISSION = "transmit";
	/**
	 * 保存版本
	 */
	public static final String ADD_DOCVER_PERMISSION = "addDocVer";
	/**
	 * 版本管理
	 */
	public static final String MANAGE_DOCVER_PERMISSION = "manageDocVer";
	/**
	 * 评论管理
	 */
	public static final String MANAGE_DOCCOMMENT_PERMISSION = "manageDocComment";
	/**
	 * 设置置顶权限
	 */
	public static final String UPARRANGE_PERMISSION = "addArrangeDoc";
	/**
	 * 文档导入权限
	 */
	public static final String IMPORTDOC_PERMISSION = "importDoc";
	/**
	 * 文档导入权限
	 */
	public static final String EXPORTDOC_PERMISSION = "exportDoc";

	/**
	 * 执行权限
	 */
	public static final String EXECUTE_PERMISSION = "execute";

	/**
	 * 回收权限
	 */
	public static final String TRASH_PERMISSION = "trash";

	public static final String AUDIT_PERMISSION = "audit";

	public static final String PUBLISH_CHNLBYINC = "chnlbyinc";

	/***************************************************************************
	 * 站点操作组 /** 站点设置站点流程
	 */
	public static final String SITE_WORKFLOW_PERMISSION = "workflow";
	/**
	 * 站点预览
	 */
	public static final String SITE_VIEW = "siteview";

	/**
	 * 站点上发布右键
	 */
	public static final String SITEPUBLISH_PERMISSION = "sitepublish";

	/**
	 * 站点上完全发布右键
	 */
	public static final String SITEBYALL_PERMISSION = "sitebyall";

	/**
	 * 站点上增量发布右键
	 */
	public static final String SITEBYINC_PERMISSION = "sitebyinc";

	/**
	 * 站点上的站内文档查询
	 */
	public static final String SITE_DOCSEARCH_PERMISSION = "docsearch";

	/**
	 * 站点上的内容管理
	 */
	public static final String SITE_CONTENTMANAGEITEM_PERMISSION = "contentManageItem";

	/**
	 * 站点节点新建频道
	 */
	public static final String CHANNELROOT_ADDCHANNEL_PERMISSION = "create_channel";

	/***************************************************************************
	 * 站点模板操作组
	 * 
	 * /** 站点上的模板管理
	 */
	public static final String SITE_TEMPMANAGER_PERMISSION = "templetmanager";
	/**
	 * 模板管理中的模板视图
	 */
	public static final String SITE_TEMPLATEVIEW_PERMISSION = "templateview";
	/**
	 * 模板视图中的导入按钮
	 */
	public static final String SITE_TEMPLATEVIEWIMP_PERMISSION = "templateviewimp";

	/**
	 * 模板视图中的新增按钮
	 */
	public static final String SITE_TEMPLATEVIEWADD_PERMISSION = "templateviewadd";

	/**
	 * 模板视图中的导出按钮
	 */
	public static final String SITE_TEMPLATEVIEWEXP_PERMISSION = "templateviewexp";

	/**
	 * 模板视图中的删除按钮
	 */
	public static final String SITE_TEMPLATEVIEWDEL_PERMISSION = "templateviewdel";

	/***************************************************************************
	 * 站点文件视图操作组
	 * 
	 * /** 模板管理中的文件视图
	 */
	public static final String SITE_FILEVIEW_PERMISSION = "fileview";
	/**
	 * 文件视图新建文件权限
	 */
	public static final String SITE_TPLCREATORFILE_PERMISSION = "tplcreatorFile";

	/**
	 * 文件视图新建目录权限
	 */
	public static final String SITE_TPLCREATORDIRECTORY_PERMISSION = "tplcreatorDirectory";

	/**
	 * 文件视图删除文件权限
	 */
	public static final String SITE_TPLDELFILE_PERMISSION = "tpldelFile";

	/**
	 * 文件视图上传文件权限
	 */
	public static final String SITE_TPLSENDFILE_PERMISSION = "tplsendFile";

	/**
	 * 文件视图上传压缩包权限
	 */
	public static final String SITE_TPLSENDBAG_PERMISSION = "tplsendBag";

	/***************************************************************************
	 * 频道操作组 频道节点新建频道
	 */
	public static final String CHANNEL_ADDCHANNEL_PERMISSION = "createchannel";

	/**
	 * 改变频道流程
	 */
	public static final String CHANNEL_WORKFLOW_PERMISSION = "chnlworkflow";

	/**
	 * 频道概览图片设置
	 */
	public static final String CHANNEL_INDEXPIC_PERMISSION = "chnlindexpic";

	/**
	 * 频道新增文档
	 */
	public static final String CHANNEL_ADDDOC_PERMISSION = "adddoc";

	/**
	 * 频道修改文档
	 */
	public static final String CHANNEL_UPDATEDOC_PERMISSION = "updatedoc";

	/**
	 * 频道查看文档
	 */
	public static final String CHANNEL_VIEWDOC_PERMISSION = "viewdoc";

	/**
	 * 频道上频道预览右键
	 */
	public static final String CHNL_VIEW = "chnlview";

	/**
	 * 频道上发布右键
	 */
	public static final String CHNLPUBLISH_PERMISSION = "chnlpublish";

	/**
	 * 频道上完全发布右键
	 */
	public static final String CHNLBYALL_PERMISSION = "chnlbyall";

	/**
	 * 频道上增量发布右键
	 */
	public static final String CHNLBYINC_PERMISSION = "chnlbyinc";

	/**
	 * 频道上复制文档右键
	 */
	public static final String COPYDOC_PERMISSION = "copydoc";

	/**
	 * 频道上置顶管理右键
	 */
	public static final String ARRANGE_DOCM = "arrangedocm";

	/**
	 * 频道上引用文档管理右键
	 */
	public static final String CITEDOC_MANAGER = "citedocmanager";

	/**
	 * 频道上扩展字段右键
	 */
	public static final String EXT_FIELD = "extfield";

	/**
	 * 频道上撤消发布右键
	 */
	public static final String WITHDRAWPUBLISH_MANAGER = "withdrawPublish";
	/***************************************************************************
	 * 模板操作组*
	 * 
	 * /** 模板列表导出右键
	 */
	public static final String TEMPLATE_TPLEXP_PERMISSION = "tplexp";

	/**
	 * 模板列表删除右键
	 */
	public static final String TEMPLATE_TPLDEL_PERMISSION = "tpldel";

	/**
	 * 模板列表编辑右键
	 */
	public static final String TEMPLATE_TPLEDIT_PERMISSION = "tpledit";

	/**
	 * 模板列表重新导入右键
	 */
	public static final String TEMPLATE_TPLREIMP_PERMISSION = "tplreimp";

	/**
	 * 完全控制权限
	 */
	public static final String FULL_PERMISSION = "full";

	public static final String COLUMN_RESOURCE = "column";

	public static final String ORGUNIT_RESOURCE = "orgunit";

	public static final String RES_RESOURCE = "resmanager";

	public static final String GROUP_RESOURCE = "group";

	public static final String ROLE_RESOURCE = "role";

	// 数据字典资源
	public static final String DICT_RESOURCE = "dict";

	public static final String JOB_RESOURCE = "job";

	public static final String USER_RESOURCE = "user";

	public static final String SITE_RESOURCE = "site";

	/**
	 * 2007-08-29 ge.tao 站点应用设置
	 */
	public static final String SITE_APP_SET = "siteappset";

	public static final String SITECHANNEL_RESOURCE = "site.channel";

	public static final String SITEDOC_RESOURCE = "site.doc";

	public static final String SITETPL_RESOURCE = "sitetpl";

	public static final String SITEFILE_RESOURCE = "sitefile";

	public static final String CHANNEL_RESOURCE = "channel";

	public static final String CHANNELDOC_RESOURCE = "channeldoc";

	public static final String TEMPLATE_RESOURCE = "template";

	public static final String ADMINISTRATOR_ROLE = "administrator";

	/**
	 * 2007-05-30 添加于郴州 biaoping.yin
	 */
	public static final String MEMBERCHANNEL_RESOURCE = "memberchannel";

	public static final String LOGOUT_REDIRECT = "jsp/logoutredirect.jsp";

	/**
	 * 2007-08-29 ge.tao 系统管理 机构管理 编辑机构
	 */
	public static final String EDIT_ORG = "editsuborg";
	/**
	 * 2007-08-29 ge.tao 系统管理 机构管理 删除机构
	 */
	public static final String DELETE_ORG = "deletesuborg";

	/**
	 * 2007-12-03 ge.tao 字典数据项资源 过滤 "可见"和"常用"两种权限
	 */
	public static final String DICTDATA_RESOURCE = "orgTaxcode";

	private Subject subject;



	private String moduleName;

	protected Credential credential;

	protected Principal principal;

	protected String roles[];

	private static final ThreadLocal current = new ThreadLocal();

	public static String loginPage = "login.page";
	public static String pathloginPage = "login.page";
	public static String redirectpathloginPage = "redirect:/login.page";
	public static String authorfailedPage = "login.page";
	
	

	HttpServletRequest request;

	// JspWriter out;

	HttpSession session;
	PageContext pageContext;
	
	/**
	 * 未知身份的用户对象
	 */
	private static  AccessControl guest ;
	static
	{
		try {
			loginPage = ConfigManager.getInstance().getDefaultLoginpage();
			
			if (loginPage == null || loginPage.trim().equals("")) {
				loginPage = "login.jsp";
			}
			pathloginPage = loginPage.startsWith("/") ?loginPage:"/"+loginPage;
			redirectpathloginPage = "redirect:"+pathloginPage;
			authorfailedPage = ConfigManager.getInstance().getAuthorfailedDirect();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			guest = new AccessControl();
			guest.guestlogin();
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		
		
		
	}
	
	public static AccessControl getGuest()
	{
		return guest;
	}

	/**
	 * CS和BS单点登录参数
	 */
	public static final String OUTER_USER_ACCOUNT_KEY = "OUT_USER_ACCOUNT_KEY";

	/**
	 * 请对密码采用MD5加密
	 */
	public static final String OUTER_USER_PASSWORD_KEY = "OUTER_USER_PASSWORD_KEY";

//	static final OnLineUser onlineUser = new OnLineUser();

	public  static final String SESSIONID_FROMCLIENT_KEY = "SESSIONID_FROMCLIENT_KEY";

	HttpServletResponse response;

	protected AccessControl() {

		try
		{
			
			moduleName = ConfigManager.getInstance().getModuleName();
		}
		catch(Exception e)
		{
			log.error("", e);
		}
	}
	
	

	public static void init(AccessControl instance) {
		current.set(instance);
	}

	public static AccessControl getInstance() {
		return new AccessControl();
	}
	public static final String current_indexpage = "current_indexpage";
	public static final String current_logoutredirect_cookie = "current.logoutredirect.cookie";
	public static void recordIndexPage(HttpServletRequest request,String page)
	{
		HttpSession session = request.getSession();
		session.setAttribute(current_indexpage, page);
	}
	
	public static void recordeSystemLoginPage(HttpServletRequest request,HttpServletResponse response)
	{
		String subsystem_id = request.getParameter(SUBSYSTEM_ID);
		if(subsystem_id == null)
			return ;
		String logoutredirect = AccessControl.getSubSystemLogoutRedirect(request, subsystem_id,false);
		if(logoutredirect == null )
			logoutredirect = StringUtil.getRealPath(request.getContextPath(),loginPage,true);
		
		AccessControl.addCookieValue(request, response, current_logoutredirect_cookie, logoutredirect);
	}
	public static String getIndexPage(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		return (String)session.getAttribute(current_indexpage);
	}
	

	// public static AccessControl getInstance(AccessControl instance) {
	// return new AccessControl(instance);
	// }

	/**
	 * 系统用户登录接口 目前的ssoCookie未加密,将来需实现ssoCookie的加密处理,用户的信息cookie格式：
	 * loginMudleName + #$% + userName 不同身份信息之间以^_^分隔，例如
	 * loginMudleName#$%userName^_^loginMudleName1#$#userName1^|^loginModuleName@|@name1=value1@~@name=value
	 * 属性列表： ssoCookie在登录时创建，将作为令牌在门户中的各应用系统之间传递的，从而实现单点登录。
	 * 当用户关闭浏览器或者退出登录时，ssoCookie自动失效
	 * 
	 * @param pageContext
	 * @param userName
	 * @param password
	 * @param decode
	 *            是否对密码加密
	 * @return
	 */
	public boolean login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password)
			throws AccessException

	{
		return login(request, response, userName, password, true);
	}

	public String getCurrentSystemID() {
		if(this.session == null)
			return "";
		String id = (String) this.session.getAttribute(Framework.SUBSYSTEM);

		return id == null ? "" : id;
	}
	
	/**
	 * 获取当前登录系统的注销跳转页面
	 * @return
	 */
	public String getSubSystemLogoutRedirect() {
		String systemid = this.getCurrentSystemID();
		
			return getSubSystemLogoutRedirect(request,systemid,false) ;
		
	}
	
	public static String getCookieValue(HttpServletRequest request,String name,String defaultvalue)
	{
//		Cookie[] cookies = request.getCookies();
//	
//	
//			String temp_ = null;
//			for (Cookie temp : cookies) {
//				if(name.equals(temp.getName()))
//				{
//					temp_ = temp.getValue();
//					break;
//				}
//			}
//			if(temp_==null){
//				temp_ = defaultvalue;
//			}
//		return temp_;
		return StringUtil.getCookieValue( request, name, defaultvalue);
	}
	
	public static String getCookieValue(HttpServletRequest request,String name)
	{
		return getCookieValue(request,name,null);
	}
	
	public static void  addCookieValue(HttpServletRequest request,HttpServletResponse response ,String name,String value,int maxage)
	{
//		Cookie[] cookies = request.getCookies();
//		Cookie loginPathCookie = null;
//		for(Cookie cookie:cookies)
//		{
//			if(name.equals(cookie.getName()))
//			{
//				loginPathCookie = cookie;
//				break;
//			}
//		}
//		if(loginPathCookie == null)
//		{
//			 loginPathCookie = new Cookie(name, value);			 
//			loginPathCookie.setMaxAge(maxage);
//			loginPathCookie.setPath(request.getContextPath());			
//			response.addCookie(loginPathCookie);
//		}
//		else
//		{
//			loginPathCookie.setMaxAge(maxage);
//			loginPathCookie.setValue(value);
//			loginPathCookie.setPath(request.getContextPath());	
//			response.addCookie(loginPathCookie);
////			loginPathCookie.setPath(request.getContextPath());
//		}
		StringUtil.addCookieValue(request, response, name, value,maxage);
	}
	
	public static void  addCookieValue(HttpServletRequest request,HttpServletResponse response ,String name,String value)
	{
		addCookieValue( request, response , name, value,3600 * 24);
	}
	
	
	/**
	 * 获取指定系统的注销跳转页面
	 * @return
	 */
	public static String getSubSystemLogoutRedirect(HttpServletRequest request,String systemid,boolean appendToken) {
		String ret = null;
		if(systemid == null)
		{
			String defaultvalue = StringUtil.getRealPath(request.getContextPath(),pathloginPage,true);
			ret = AccessControl.getCookieValue(request, current_logoutredirect_cookie);
			if(!StringUtil.isEmpty(ret))
			{
				 if(ret.indexOf(TokenStore.temptoken_param_name_word) > 0)
					 ret = defaultvalue;				 
			}
			else
			{
				ret = defaultvalue;	
			}
			if(ret != null && appendToken)
			{
				
				if(TokenHelper.isEnableToken())//如果开启令牌机制就会存在memTokenManager对象，否则不存在
				{
					ret = TokenHelper.getTokenService().appendDTokenToURL(request,ret);
				}
			}
				
		}
		else
		{
			SubSystem subsystem = Framework.getInstance().getSubsystem(systemid);
			if(subsystem != null)
			{
				systemid = subsystem.getLogoutredirect();
				if(systemid != null )
				{
					ret = StringUtil.getRealPath(request.getContextPath(), systemid,true);
				}
			}
			else
			{
				String defaultvalue = StringUtil.getRealPath(request.getContextPath(),pathloginPage,true);
				ret = AccessControl.getCookieValue(request, current_logoutredirect_cookie);
				if(!StringUtil.isEmpty(ret))
				{
					 if(ret.indexOf(TokenStore.temptoken_param_name_word) > 0)
						 ret = defaultvalue;				 
				}
				else
				{
					ret = defaultvalue;	
				}
			}
			if(ret != null && appendToken)
			{
				
				if(TokenHelper.getTokenService() != null)//如果开启令牌机制就会存在memTokenManager对象，否则不存在
				{
					ret = TokenHelper.getTokenService().appendDTokenToURL(request,ret);
				}
			}
			
		}
		return ret;
	}
	
	/**
	 * 获取指定系统的注销跳转页面
	 * @return
	 */
	public static String getSubSystemLogoutRedirect(HttpServletRequest request) {
		return getSubSystemLogoutRedirect(request,null,false);
		
	}

	public String getCurrentSystemName() {
		String id = getCurrentSystemID();
		if (id == null)
			return "";
		try {
			return Framework.getInstance(id).getDescription(request);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 系统用户登录接口 目前的ssoCookie未加密,将来需实现ssoCookie的加密处理,用户的信息cookie格式：
	 * loginMudleName + #$% + userName 不同身份信息之间以^_^分隔，例如
	 * loginMudleName#$%userName^_^loginMudleName1#$#userName1^|^loginModuleName@|@name1=value1@~@name=value
	 * 属性列表： ssoCookie在登录时创建，将作为令牌在门户中的各应用系统之间传递的，从而实现单点登录。
	 * 当用户关闭浏览器或者退出登录时，ssoCookie自动失效
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param enablelog
	 * @return
	 * @throws AccessException
	 */

	public boolean login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			boolean enablelog) throws AccessException

	{
		return login( request,
				 response,  userName,  password,
				 enablelog,null);
		
	}

	/**
	 * 系统用户登录接口 目前的ssoCookie未加密,将来需实现ssoCookie的加密处理,用户的信息cookie格式：
	 * loginMudleName + #$% + userName 不同身份信息之间以^_^分隔，例如
	 * loginMudleName#$%userName^_^loginMudleName1#$#userName1^|^loginModuleName@|@name1=value1@~@name=value
	 * 属性列表： ssoCookie在登录时创建，将作为令牌在门户中的各应用系统之间传递的，从而实现单点登录。
	 * 当用户关闭浏览器或者退出登录时，ssoCookie自动失效
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param enablelog
	 * @return
	 * @throws AccessException
	 */

	public boolean login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String userType) throws AccessException

	{

		return login(request, response, userName, password,
				new String[] { userType });
	}
	
	

	/**
	 * 系统用户登录接口 目前的ssoCookie未加密,将来需实现ssoCookie的加密处理,用户的信息cookie格式：
	 * loginMudleName + #$% + userName 不同身份信息之间以^_^分隔，例如
	 * loginMudleName#$%userName^_^loginMudleName1#$#userName1^|^loginModuleName@|@name1=value1@~@name=value
	 * 属性列表： ssoCookie在登录时创建，将作为令牌在门户中的各应用系统之间传递的，从而实现单点登录。
	 * 当用户关闭浏览器或者退出登录时，ssoCookie自动失效
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param enablelog
	 * @return
	 * @throws AccessException
	 */

	public boolean login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String[] userTypes) throws AccessException

	{

		return login( request,
				 response,  userName,  password, true,
				 userTypes);
	}
	
	public static String kickmode = ConfigManager.getInstance().getConfigValue("kickmode", "refuse");
	
	public static boolean enablemutilogin = ConfigManager.getInstance().getConfigBooleanValue("enablemutilogin", true) ;
	public static boolean cluster_session_synchronize = ConfigManager.getInstance().getConfigBooleanValue("cluster.session.synchronize", false);
	public static void resetSession(HttpSession session)
	{
		if(session == null )
		{
			return;
		}
//		session.removeAttribute(PRINCIPAL_INDEXS);
//		session.removeAttribute(Framework.SUBSYSTEM);
//		session.removeAttribute(CREDENTIAL_INDEXS);
//		session.removeAttribute(AccessControl.SESSIONID_FROMCLIENT_KEY);
//		session.removeAttribute(SERVER_IP_KEY);
//		session.removeAttribute(SERVER_PORT_KEY);
//		session.removeAttribute(REMOTEADDR_CACHE_KEY);
//		
//		 
//		session.removeAttribute(MACADDR_CACHE_KEY);
//		session.removeAttribute(AccessControl.MACHINENAME_CACHE_KEY);
//		session.removeAttribute(SESSIONID_CACHE_KEY);
		try {
			Enumeration anames = session.getAttributeNames();
			if(anames != null )
			{
				while(anames.hasMoreElements())
				{
					String name = String.valueOf(anames.nextElement());
					session.removeAttribute(name);
				}
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	/**
	 * 系统用户登录接口 目前的ssoCookie未加密,将来需实现ssoCookie的加密处理,用户的信息cookie格式：
	 * loginMudleName + #$% + userName 不同身份信息之间以^_^分隔，例如
	 * loginMudleName#$%userName^_^loginMudleName1#$#userName1^|^loginModuleName@|@name1=value1@~@name=value
	 * 属性列表： ssoCookie在登录时创建，将作为令牌在门户中的各应用系统之间传递的，从而实现单点登录。
	 * 当用户关闭浏览器或者退出登录时，ssoCookie自动失效
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param enablelog
	 * @return
	 * @throws AccessException
	 */

	public boolean login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,boolean enablelog,
			String[] userTypes) throws AccessException

	{

		
		
		/**
		 * 如果ie session相互干扰时需要给出提示,不允许用户登录
		 */
		session = request.getSession(false);
		
		Principal temp =  (session != null ?(Principal)session.getAttribute(PRINCIPAL_INDEXS):null);
		
		 this.log("userAccount.login",request);
		if(temp != null )
		{
		        boolean sameuserenable = ConfigManager.getInstance().getConfigBooleanValue("session.sameuserenable",true);
		        this.unprotectedCheck(null, null);
		        String userAccount = this.getUserAccount();
//			Principal principal_ = (Principal) temp.get(moduleName);
		        if(userAccount.equals(userName))
		        {
		             this.log("userAccount.equals(userName)",request);
		            String subsystem_id = request.getParameter(SUBSYSTEM_ID);
		            if (subsystem_id == null || subsystem_id.equals(""))
		            {
		                        ;
		                // 将用户登录的子系统模块名称添加到session中
		            }
		            else
		            {
		                String old = (String)session.getAttribute(Framework.SUBSYSTEM);
		                if(old != null && !old.equals(subsystem_id))
		                    session.setAttribute(Framework.SUBSYSTEM, subsystem_id);
		            }
		            return true;
		        }
		        else
		        {
		            this.log("!userAccount.equals(userName)",request);
		            if(!sameuserenable)
		                throw new AccessException("用户[" + userAccount + "]正在使用系统，等待退出后再登录！");
		        }
			
		}
		
//		if ((!enablemutilogin 
//				|| cluster_session_synchronize) 
//				&& onlineUser.existUser(userName))
//		{
//			if(kickmode.equalsIgnoreCase("refuse"))
//			{
//				if(!isAdmin(userName))
//					throw new AccessException("用户[" + userName + "]已登陆,登陆情况为：\\n"
//							+ onlineUser.getUserLoginInfo(userName)
//							+ "，不允许用户同时多次登陆系统。");
//			}
//		}
		
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				userName, password,userTypes,request,response);

	 
		
		this.request = request;
		this.response = response;
		try {
			innerlogon(callbackHandler,
					userName,
					 enablelog,true);			
			return true;
		} catch (LoginException ex) {
			
			throw new AccessException(ex.getMessage(),ex);
		}
		
		catch (Exception ex) {
			
			throw new AccessException(ex.getMessage(),ex);
		}
	}
	
	public String getSessionID()
	{
		if(session == null)
			return null;
		return this.session.getId();
	}
	
	private void guestlogin()
	{
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				BaseAuthorizationTable.guest, BaseAuthorizationTable.password,null,request,response);
		
		try {
			
			SimpleLoginContext loginContext = new SimpleLoginContext("base",
					callbackHandler);
			loginContext.login();
			subject = loginContext.getSubject();
			 
				Credential credential = subject.getCredential();
				
//					CheckCallBack.AttributeQueue attributeQueue = credential
//							.getCheckCallBack().getAttributeQueue();				
				 				
				this.credential = credential;
				

			 
				AuthPrincipal principal = (AuthPrincipal)subject.getPrincipal();
				this.principal = principal;
		
			 
		} catch (LoginException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public String getMachineName()
	{
		String machineName = null;
		if(this.credential != null)
		{
			machineName = (String)this.credential.getCheckCallBack().getUserAttribute(AccessControl.MACHINENAME_CACHE_KEY);
			
		}
		if(machineName == null)
		{
			return "";
		}
		return machineName;
	}
	public static String getDefaultSUBSystemID()
	{
		String default_module = ConfigManager.getInstance().getConfigValue("default_module", "module");
		return default_module;
	}
	public static String getLoginStyle(HttpServletRequest request)
	{
//		HttpSession session = request.getSession(false);
		AccessControl  control = AccessControl.getAccessControl();
		/**
		 * 1 other
		 * 2 other
		 * 3 ISany
		 * 4 WEBIsany
		 * 5 common
		 */
		String loginstyle = !control.isGuest() ?(String)control.getUserAttribute(AccessControl.LOGINSTYLE_CACHE_KEY):null;
		if(loginstyle == null)
		{
			
			return ConfigManager.getInstance().getConfigValue("destop.defaultstyle", "3");
		}
		return loginstyle;
	}
	private void innerlogon(UsernamePasswordCallbackHandler callbackHandler,
			String userName,
			boolean enablelog,boolean recordonlineuser) throws LoginException
	{
		SimpleLoginContext loginContext = new SimpleLoginContext("base",
				callbackHandler);
		loginContext.login();
//		/**
//		 * 通过验证以后，判断用户是否已经登录系统，如果已经登录了，按以下情况进行相应处理：
//		 * 1.不允许用户多次登录系统时,将先前登录的用户从系统中剔除
//		 * 2.应用存在多实例或者应用部署在集群环境中时,将先前登录的用户从系统中剔除
//		 */
//		if ((!enablemutilogin 
//				|| cluster_session_synchronize) 
//				&& recordonlineuser && onlineUser.existUser(userName) )
//		{
//			onlineUser.removeUser(userName);
////			throw new AccessException("用户[" + userName + "]已登陆,登陆情况为：\\n"
////					+ onlineUser.getUserLoginInfo(userName)
////					+ "，系统不允许用户在同时多次登陆。");
//		}
		subject = loginContext.getSubject();
		// 将loginContext缓冲到session中
		// session.setAttribute(LOGINCONTEXT_CACHE_KEY, loginContext);
		// 缓冲远程地址，以便session实效时清除未清除的用户信息
		String machineIP = request.getHeader("iv-remote-address");//获取webseal反向代理过来的ip地址
		if(machineIP == null || machineIP.equals(""))
		{

			machineIP = request.getParameter("machineIp_");
			if(machineIP == null || machineIP.trim().equals(""))
				machineIP = StringUtil.getClientIP(request);
		}
		if(session == null)
			session = request.getSession();
		
		String sessionTimeout = request.getParameter("sessionTimeout");
		if(sessionTimeout != null && !sessionTimeout.equals(""))
		{
			int timeout = Integer.parseInt(sessionTimeout);
			session.setMaxInactiveInterval(timeout);
		}
	
		StringBuffer ssoCookie = new StringBuffer();
		StringBuffer credentialCookie = new StringBuffer();
		boolean flag = false;
		boolean enablecookie = this.enablecookie();
		
			  credential = (Credential) subject.getCredential();
			  principal = (AuthPrincipal)subject.getPrincipal();
			  /**
				 * 1 other
				 * 2 other
				 * 3 ISany
				 * 4 WEBIsany
				 * 5 common
				 */
				String loginPath = request.getParameter("loginPath");
				if(StringUtil.isEmpty(loginPath))
				{
					loginPath = "5";
				}
//				session.setAttribute(LOGINSTYLE_CACHE_KEY,loginPath);
				credential.getCheckCallBack().setUserAttribute(LOGINSTYLE_CACHE_KEY, loginPath);
//				session.setAttribute(REMOTEADDR_CACHE_KEY, machineIP);
				credential.getCheckCallBack().setUserAttribute(REMOTEADDR_CACHE_KEY, machineIP);
				/**
				 * 获取客服端网卡的mac地址
				 */
				String macaddr = request.getParameter("macaddr_");
				String machineName = request.getParameter("machineName_");
				 
//				session.setAttribute(MACADDR_CACHE_KEY, macaddr);
				credential.getCheckCallBack().setUserAttribute(MACADDR_CACHE_KEY, macaddr);
//				session.setAttribute(AccessControl.MACHINENAME_CACHE_KEY, machineName);
				credential.getCheckCallBack().setUserAttribute(MACHINENAME_CACHE_KEY, machineName);
				String subsystem_id = request.getParameter(SUBSYSTEM_ID);
				if (subsystem_id == null || subsystem_id.equals(""))
					subsystem_id = getDefaultSUBSystemID();
				// 将用户登录的子系统模块名称添加到session中
				session.setAttribute(Framework.SUBSYSTEM, subsystem_id);
				
				
				// 添加用户的所有身份索引到session中
						session.setAttribute(PRINCIPAL_INDEXS, this.principal);
						// 添加用户的所有属性到session中
						session.setAttribute(CREDENTIAL_INDEXS, this.credential);
//				CheckCallBack.AttributeQueue attributeQueue = credential
//						.getCheckCallBack().getAttributeQueue();
				Map<String,Object> callBacks = credential.getCheckCallBack().getCallBacks();
				if(enablecookie)
				{
					if (callBacks.size() > 0) {
						if (!flag) {
							flag = true;
						} else {
							credentialCookie.append(CREDENTIAL_SPLIT);
						}
						credentialCookie.append(credential.getLoginModule())
								.append(CREDENTIAL_ATTRIBUTE_SPLIT);
						boolean _flag = false;
						Iterator<Entry<String, Object>>  it = callBacks.entrySet().iterator();
						
						while (it.hasNext()) {
							if (!_flag) {
								_flag = true;
							} else {
								credentialCookie.append(ATTRIBUTE_SPLIT);
							}
							Entry<String, Object> entry = it.next();
							Object attr =entry.getValue();
							credentialCookie.append(entry.getKey()).append("=")
									.append(attr);
						}
					}
				}
				
			 

			 

		 
		flag = false;
		
			
			
			
//			log("enablecookie:" + enablecookie);
			if (enablecookie) {
				if (!flag) {
					ssoCookie.append(((AuthPrincipal)principal).getLoginModuleName()).append(
							IDENTITY_SPLIT).append(principal.getName());
					flag = true;
				} else {
					ssoCookie.append(PRINCIPAL_SPLIT).append(
							((AuthPrincipal)principal).getLoginModuleName()).append(
							IDENTITY_SPLIT).append(principal.getName());
				}
			}
		
		if (enablecookie) {
			if (ssoCookie.length() > 0) {
				ssoCookie.insert(0, "encrypt=false" + HEAD_SPLIT);
				ssoCookie.append(PRINCIPAL_CREDENTIAL_SPLIT).append(
						credentialCookie);
			}
		}
		
		
		if (enablecookie) {

			try
			{
				DESCipher dd = new DESCipher();
				String sss = dd.encrypt(ssoCookie.toString());
				// 添加用户登录信息到客服端cookie中，以便实现单点登录
				log.debug("生成sso Cookie[" + PRINCIPALS_COOKIE + ","
						+ sss + "]");
				Cookie newCookie = new Cookie(PRINCIPALS_COOKIE,
						sss);
				
				// 记录cookie
				newCookie.setMaxAge(86400);
				
				response.addCookie(newCookie);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			

			
		}
		//记录登录子系统的subsystemid到cookie中
		FrameworkServlet.setSubSystemToCookie(response, userName, subsystem_id);
//		Cookie subsystemCookie = new Cookie(SUBSYSTEM_COOKIE + "_"
//				+ userName, subsystem_id);
//		subsystemCookie.setMaxAge(86400);
//		response.addCookie(subsystemCookie);
		// 记录cookie

		String operSource = this.getMachinedID();
		log_info("User[" + this.getUserAccount() + ","+ getUserName() + "] login from [" +operSource +"].session id is " + session.getId(),request);
		//log.info("User[" + this.getUserAccount() + "," + getUserName() + "] login from [" +operSource +"].");
//		String serverIp = request.getServerName();
//		String serverport = request.getServerPort() + "";
//		session.setAttribute(SERVER_IP_KEY,serverIp);
//		session.setAttribute(SERVER_PORT_KEY,serverport);
//		if(recordonlineuser)
//		{
//			onlineUser.valueBound(session.getId(), getUserAccount(), machineIP,
//	//				request.getRemoteAddr(),
//					macaddr,machineName,serverIp,serverport);
//		}
		session.setAttribute("userAccount", this.getUserAccount());
		session.setAttribute("worknumber", this.getUserAttribute("userWorknumber"));
		current.set(this);
		
		// ------------登陆时保存用户日志信息
		if(enablelog && recordonlineuser)
		{
			try {
				LogManagerInf logMgr = ConfigManager.getInstance().getLogManager();
				String userrelName = getUserName();
				String operContent = userName + "(" +userrelName + ")" + " 登陆["
						+ getCurrentSystemName() + "]";
				
				String openModle = "认证管理";

				logMgr.log(userName, operContent,
						openModle, operSource);
			} catch (SPIException e1) {
				
				e1.printStackTrace();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}

	/**
	 * 系统用户从Dreamweaver客服端登录接口
	 * 目前的ssoCookie未加密,将来需实现ssoCookie的加密处理,用户的信息cookie格式： loginMudleName + #$% +
	 * userName 不同身份信息之间以^_^分隔，例如
	 * loginMudleName#$%userName^_^loginMudleName1#$#userName1^|^loginModuleName@|@name1=value1@~@name=value
	 * 属性列表： ssoCookie在登录时创建，将作为令牌在门户中的各应用系统之间传递的，从而实现单点登录。
	 * 当用户关闭浏览器或者退出登录时，ssoCookie自动失效 不记录用户的在线信息
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param userType
	 *            用户类型 0-标识外部用户，1-标识内部用户
	 * @return
	 * @throws AccessException
	 */

	public boolean logindw(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String userType) throws AccessException

	{

		if(userType != null && !userType.equals(""))
		{
			return logindw(request, response, userName, password,
					new String[] { userType });
		}
		else
		{
			return logindw(request, response, userName, password,
					(String[])null);
		}
	}
	
	public String getRemoteAddr()
	{
		String ip = null;
		if(this.credential != null)
		{
			ip = (String)this.credential.getCheckCallBack().getUserAttribute(REMOTEADDR_CACHE_KEY);
		}
		if(ip == null)
			return "";
		return ip;
		
	}
	
	public String getMachinedID()
	{
		if(this.isGuest())
			return "";
		String machineID = this.getRemoteAddr() + "||" + this.getMacAddr() + "||" + this.getMachineName()
		;
		return machineID;
	}

	/**
	 * 系统用户从Dreamweaver客服端登录接口
	 * 目前的ssoCookie未加密,将来需实现ssoCookie的加密处理,用户的信息cookie格式： loginMudleName + #$% +
	 * userName 不同身份信息之间以^_^分隔，例如
	 * loginMudleName#$%userName^_^loginMudleName1#$#userName1^|^loginModuleName@|@name1=value1@~@name=value
	 * 属性列表： ssoCookie在登录时创建，将作为令牌在门户中的各应用系统之间传递的，从而实现单点登录。
	 * 当用户关闭浏览器或者退出登录时，ssoCookie自动失效 不记录用户的在线信息
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param userType
	 *            用户类型 0-标识外部用户，1-标识内部用户
	 * @return
	 * @throws AccessException
	 */

	public boolean logindw(HttpServletRequest request,
			HttpServletResponse response, String userName, String password)
			throws AccessException

	{

		return logindw(request, response, userName, password,
				(String[] )null);
	}

	/**
	 * 系统用户从Dreamweaver客服端登录接口
	 * 目前的ssoCookie未加密,将来需实现ssoCookie的加密处理,用户的信息cookie格式： loginMudleName + #$% +
	 * userName 不同身份信息之间以^_^分隔，例如
	 * loginMudleName#$%userName^_^loginMudleName1#$#userName1^|^loginModuleName@|@name1=value1@~@name=value
	 * 属性列表： ssoCookie在登录时创建，将作为令牌在门户中的各应用系统之间传递的，从而实现单点登录。
	 * 当用户关闭浏览器或者退出登录时，ssoCookie自动失效 不记录用户的在线信息
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param userType
	 *            用户类型 0-标识外部用户，1-标识内部用户
	 * @return
	 * @throws AccessException
	 */

	public boolean logindw(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String[] userTypes) throws AccessException

	{

		
		UsernamePasswordCallbackHandler callbackHandler = new UsernamePasswordCallbackHandler(
				userName, password, userTypes,request,response);

	 
		this.request = request;
		this.response = response;
		session = request.getSession(false);
		
		try {
			innerlogon(callbackHandler,
					userName,
					 true,false);	
			if(session != null)
				session.setAttribute(AccessControl.SESSIONID_FROMCLIENT_KEY, new Boolean(true));
			return true;
		} catch (LoginException ex) {
//			principalIndexs = null;
//			credentialIndexs = null;
			throw new AccessException(ex.getMessage());
		}
		catch (Exception ex) {
//			principalIndexs = null;
//			credentialIndexs = null;
			throw new AccessException(ex.getMessage());
		}
		
	}
	
	

	/**
	 * 用户修改密码时，同步session中保存的用户密码
	 * 
	 * @param newPassword
	 *            String
	 */
	public void refreshPassword(String newPassword) {
		if (this.getUserAttribute("password") != null) {
			this.credential.getCheckCallBack().setUserAttribute(
					"password",
					org.frameworkset.platform.security.authentication.EncrpyPwd
							.encodePassword(newPassword));
			this.credential.getCheckCallBack().setUserAttribute("password_i",
					newPassword);
		}
	}



	/**
	 * 检查用户是否登录，如果没有登录，系统将跳转的登录页面 检查用户是否有权限，如果没有权限访问本页面将跳转的权限检测失败页面
	 */
	public boolean checkAccess(HttpServletRequest request,
			HttpServletResponse response) {
		AccessControl c = AccessControl.getAccessControl();
		if(c.isGuest())
			return false;
		else
		{
			this.request = c.getRequest();
			this.response = c.getResponse();		
			this.session = c.getSession();			
			 
			// 添加用户的所有属性到session中
			 
			this.principal = c.principal;
			this.credential = c.credential;
			if(this.principal != null)
			{
				this.subject = new Subject();
				
				this.subject.setCredential(credential);
				this.subject.setPrincipal(principal);
			}
			return true;
		}
//		boolean isLogin = checkAccess(request, response, null, true);
//		//modify by jianfeng 20090909
////		if (!isLogin){			
////			throw new SessionTimeoutExcetpion();
////		}
////		return true;
//		return isLogin;
	}
	
	/**
	 * 检测当前登陆用户是否是管理员或者拥有超级管理员角色
	 */
	public boolean checkManagerAccess(HttpServletRequest request,
			HttpServletResponse response) {
		boolean success = checkAccess(request, response);
		if(!success)
			return false;
		if(isAdmin() || isOrgManager(getUserAccount())){
			return true;
		}else{
			redirectManager(request,response,"/purviewmanager/nopermission.jsp");
			return false;
		}
		
	}
	
	protected boolean isOrgManager(String userAccount) {
		if(ConfigManager.getInstance().getPermissionModule() != null)
			return ConfigManager.getInstance().getPermissionModule().isOrgManager(userAccount);
		return false;
	}



	/**
	 * 检测当前登陆用户是否是管理员
	 */
	public boolean checkAdminAccess(HttpServletRequest request,
			HttpServletResponse response) {
		boolean success = checkAccess(request, response);
		if(!success)
			return false;
		if(isAdmin()){
			return true;
		}else{
			redirectManager(request,response,"/purviewmanager/onlyAdminPermission.jsp");
			return false;
		}
		
	}
	
	/**
	 * 检查用户是否登录，如果没有登录，系统将跳转的登录页面 检查用户是否有权限，如果没有权限访问本页面将跳转的权限检测失败页面
	 */
	public boolean checkAccess(String[] needUserTypes,HttpServletRequest request,
			HttpServletResponse response) {
		return checkAccess( needUserTypes,  request,
				 response, null,true,null);
	}

	/**
	 * 检查用户是否登录，如果没有登录系统将跳转到redirectPath指定的页面，如果redirectPath为null或者为""
	 * 将缺省地跳转到配置文件config-manager.xml中指定的登录页面。 重定向规则如下：
	 * 重定项之前首先检查response的状态，如果状态为commited则不跳转，直接返回。
	 * 如果commited为false再对参数标识的页面地址进行处理，处理方法如下：
	 * 如果redirectPath的第一个字符为"/"或者"\\"，并且路径中没有包含应用的contextPath时，
	 * 本方法通过处理将contextPath追加在redirectPath之前，然后跳转到处理过的页面地址。
	 * 否则不进行处理，直接跳转到redirectPath对应的页面。
	 * 
	 * @param request
	 * @param response
	 * @param redirectPath
	 * @return
	 */
	public boolean checkAccess(HttpServletRequest request,
			HttpServletResponse response, String redirectPath) {
		return checkAccess(request, response, null, true, redirectPath);
	}

	/**
	 * 检查用户是否登录
	 * 
	 */
	public boolean checkAccess(HttpServletRequest request,
			HttpServletResponse response, boolean protect) {
		return checkAccess(request, response, null, protect);
	}

	public boolean checkAccess(HttpServletRequest request,
			HttpServletResponse response, JspWriter out, boolean protect) {


		return checkAccess(request,
				response, out, protect,
				null);

	}

	/**
	 * 判断当前登录的用户类型是否与要求的用户类型一致，一致返回true，否则返回false
	 * 
	 * @param userType
	 * @return
	 */
	private boolean checkUserType(String[] userTypes) {
		if(userTypes == null || userTypes.length == 0)
			return true;
		
		Credential dd = this.credential;
		String _userType = (String) dd.getCheckCallBack().getUserAttribute(
				"LOGINCONTEXT.USERTYPE");
		if (_userType != null && !_userType.equals("")) {
			StringBuffer b = new StringBuffer();
			for(int i = 0; i < userTypes.length; i ++)
			{
				String userType = userTypes[i];
				if (userType != null && _userType.equals(userType)) {
					b.setLength(0);
					return true;
				}
				b.append(userType).append(",");
			}
			log.debug("用户类型不一致，访问当前模块需要的类型为[needType=" + b.toString()
					+ "],但是用户的类型为[userType=" + _userType + "]");
			return false;
		}

		return true;
	}

	/**
	 * 检测当前访问系统的用户是否登录过系统，并且该用户的类型必须是给定的userType类型， 否则将不允许用户访问请求的资源，并跳转到登录页面
	 * 
	 * @param userType
	 *            用户类型
	 * @param request
	 * @param response
	 * @param out
	 * @param protect
	 * @deprecated 请参考方法 checkAccess(String userType,HttpServletRequest request,
	 *             HttpServletResponse response)
	 * @return
	 */
	public boolean checkAccess(String userType, HttpServletRequest request,
			HttpServletResponse response, JspWriter out) {
		return checkAccess(new String[] {userType}, request,response, out,null);


	}

	/**
	 * 检测当前访问系统的用户是否登录过系统，并且该用户的类型必须是给定的userType类型， 否则将不允许用户访问请求的资源，并跳转到登录页面
	 * 
	 * @param userType
	 *            用户类型
	 * @param request
	 * @param response
	 * @param protect
	 * @return
	 */
	public boolean checkAccess(String userType, HttpServletRequest request,
			HttpServletResponse response) {

		return checkAccess(userType, request, response, (JspWriter) null);
	}

	public boolean checkAccess(HttpServletRequest request,
			HttpServletResponse response, JspWriter out, boolean protect,
			boolean abc) {
		return this.checkAccess( request,response,out,  protect);
	}

	public boolean checkAccess(HttpServletRequest request,
			HttpServletResponse response, JspWriter out, boolean protect,
			String redirectPath) {


		
		return checkAccess(null,  request,
				 response,  out, protect, redirectPath);

	}

	/**
	 * 当前访问系统的用户是否登录过系统，并且该用户的类型必须是给定的userType类型，
	 * 否则将不允许用户访问请求的资源，并跳转到redirectPath对应的页面
	 * 
	 * @param userType
	 * @param request
	 * @param response
	 * @param redirectPath
	 * @return
	 */
	public boolean checkAccess(String userType, HttpServletRequest request,
			HttpServletResponse response, String redirectPath) {
		return this.checkAccess(new String[] {userType}, request, response, null,redirectPath);

	}
	
	public boolean checkAccess(String[] userType, HttpServletRequest request,
			HttpServletResponse response, JspWriter out,String redirectPath) {
		
		return checkAccess(userType,  request,
				 response,  out,true, redirectPath);
	}
	
	private  boolean unprotectedCheck(String redirectPath,String[] userTypes)
	{
		try
		{
			this.principal = (Principal) session.getAttribute(PRINCIPAL_INDEXS);
			// 添加用户的所有属性到session中
			this.credential = (Credential) session.getAttribute(CREDENTIAL_INDEXS);
	
			if (this.principal != null) {
				current.set(this);
				 
			}
			 
//			String sessionid = (String)session.getAttribute(SESSIONID_CACHE_KEY);
//			Boolean fromclient =  (Boolean)session.getAttribute(SESSIONID_FROMCLIENT_KEY);
	//		if (principal != null
	//				&& (fromclient == null && !onlineUser.existUser(principal.getName(), sessionid))) {
	//			if(redirectPath == null || redirectPath.equals(""))
	//			{					
	//				this.logoutwithalt_(null, null,true, true,"kickout") ;
	//			}
	//			else
	//			{
	////				this.logout(redirectPath);
	//				this.logoutwithalt_(redirectPath, null,true, true,"kickout") ;
	//			}
	//			return false;
	//		}
			
			
	//		/**
	//		 * 检查当前用户的类型是否在被允许的范围内，不允许则跳转到相应的页面
	//		 */
	//		if (!checkUserType(userTypes))
	//		{
	//			if(redirectPath == null || redirectPath.equals(""))
	//			{
	//				this.redirect();
	//			}
	//			else
	//			{
	//				redirect(request, response, redirectPath);
	//			}
	//			return false;
	//		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			if(redirectPath == null || redirectPath.equals(""))
//			{
//				this.redirect();
//			}
//			else
//			{
//				redirect(request, response, redirectPath);
//			}
			return false;
		}
		if(this.principal != null)
			return true;
		else
			return false;
	}
	
	private boolean innerRedirect(String redirectPath)
	{
//		if (ConfigManager.getInstance().securityEnabled()) 
		{
			if(redirectPath == null || redirectPath.equals(""))
			{
				this.redirect();
			}
			else
			{
				redirect(request, response, redirectPath);
			}
		}
		return false;
		
		
	}
	
	private boolean innerLogout(String redirectPath,String kickmode)
	{
		if(redirectPath == null || redirectPath.equals(""))
		{
			//"kickout"
			this.logoutwithalt_(null, null,true, true,kickmode) ;
		}
		else
		{
//			this.logout(redirectPath);
			this.logoutwithalt_(redirectPath, null,true, true,kickmode) ;
		}

		return false;
	}
	private static void log(String msg,HttpServletRequest request)
	{
		Date date = new java.util.Date() ; 
		if(request != null)
		{
			String remoteip = StringUtil.getClientIP(request);
			
//			System.err.println(date + "===============================================");
//			Exception e = new Exception("date["+ date +"] [remoteip=" + remoteip + "] " +msg);
//			e.printStackTrace();
//			log.debug("date["+ date +"] [remoteip=" + remoteip + "] " +msg);
//			System.err.println(date +"================================================");
		}
		else
		{
//			System.err.println(date + "===============================================");
//			Exception e = new Exception("date["+ date +"] [remoteip=null] " +msg);
//			log.error("",e);
//			System.err.println("date["+ date +"] [remoteip=null] " +msg);
//			System.err.println(date +"================================================");
		}
	}
	
	private static void log_info(String msg,HttpServletRequest request)
	{
		Date date = new java.util.Date() ; 
		if(request != null)
		{
			String remoteip = StringUtil.getClientIP(request);
			
//			System.err.println(date + "===============================================");
//			Exception e = new Exception("date["+ date +"] [remoteip=" + remoteip + "] " +msg);
//			e.printStackTrace();
//			log.debug("date["+ date +"] [remoteip=" + remoteip + "] " +msg);
//			System.err.println(date +"================================================");
		}
		else
		{
//			System.err.println(date + "===============================================");
//			Exception e = new Exception("date["+ date +"] [remoteip=null] " +msg);
//			e.printStackTrace();
//			log.debug("date["+ date +"] [remoteip=null] " +msg);
//			System.err.println(date +"================================================");
		}
	}
	/**
	 * 检测到用户未登陆时进行相应的处理
	 * 返回值说明ret:
	 * 0: 继续执行，无意义
	 * 1：返回false
	 * 2: 返回true，客服端登陆成功
	 * @return
	 */
	private int unlogincheck()
	{
		String OUT_useraccount = request.getParameter(OUTER_USER_ACCOUNT_KEY);
		String OUT_userpassord = request.getParameter(OUTER_USER_PASSWORD_KEY);
		
		int ret = 0;
		/**
		 * 判断用户是否从外部系统访问bs系统，如果不是则从cookie中获取用户登录信息，
		 * 如果是则获取外部用户信息进行登录操作，如果用户登录成功则允许访问系统，否则不允许
		 */
		if (OUT_useraccount == null || OUT_useraccount.equals("")) {
			// 启用了系统安全性、或者启用了页面保护、或者启用了sso机制，则尝试从cookie中恢复用户信息
			boolean enablecookie = enablecookie();
//			log("enablecookie:" + enablecookie,request);
			if (enablecookie) {
				Cookie[] cookies = request.getCookies();
				int idx = 0;
				boolean flag = false;
				for (int i = 0; cookies != null && i < cookies.length; i++) {

					if (cookies[i].getName().equals(PRINCIPALS_COOKIE)) {

						flag = true;
						idx = i;
						break;
					}
				}
				if (!flag) {
//					return innerRedirect(redirectPath);
					ret = 1;
					return ret;
				}
				

				String ssoCookie = cookies[idx]
						.getValue();
				
				try
				{
					DESCipher dd = new DESCipher();
					ssoCookie = dd.decrypt(ssoCookie);
				}
				catch(Exception e)
				{
					
				}
				CookieUtil cookieUtil = new CookieUtil();
				Object[] messages = cookieUtil.refactorPricipal(ssoCookie);

				if (messages != null) {
					this.principal = (Principal) messages[0];
					this.credential = (Credential) messages[1];
					subject = new Subject();
					subject.setCredential(credential);
					subject.setPrincipal(principal);
					
					// 添加用户的所有身份索引到session中
					session.setAttribute(PRINCIPAL_INDEXS, principal);
					// 添加用户的所有属性到session中
					session.setAttribute(CREDENTIAL_INDEXS, credential);
					ret = 3;
					return ret;

				} else {
//					return innerRedirect(redirectPath);
					ret = 1;
					return ret;
				}
			} else {
//				return innerRedirect(redirectPath);
				ret = 1;
				return ret;
			}
		} 
		else // 客服端登陆系统
		{
			try {
				boolean su = this.logindw(request, response, OUT_useraccount,
						OUT_userpassord);
				if (!su) {
//					return innerRedirect(redirectPath);
					ret = 4;
					return ret;
				}
				else
				{
//				return su;
					ret = 2;
					return ret;
				}

			} catch (AccessException e) {

//				return innerRedirect(redirectPath);
				ret = 1;
				return ret;
			}
		}
		
	}
//	public static String serverid;
//	public static String getServerID(String filePath)
//	{
//		if(serverid == null)
//		{
//		
//			String localpath = filePath;
//			try {
//				BufferedReader reader = new BufferedReader(new FileReader(localpath));
//				serverid = reader.readLine().trim();
//				return serverid;
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//request.getRealPath("/ServerID.txt");
//			catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		}
//		return serverid;
//	}
//	
	public String getParameters(HttpServletRequest request)
	{
		java.util.Enumeration enums =  request.getParameterNames();
		StringBuffer sb = new StringBuffer(100);
		boolean flag = false;
		while(enums.hasMoreElements())
		{
			String key = (String)enums.nextElement();
			String value = (String)request.getParameter(key);
			if(!flag)
			{
				sb.append("?").append(key).append("=").append(value);
				flag = true;
			}
			else
			{
				sb.append("&").append(key).append("=").append(value);
				
			}
		}
		return sb.toString();
	}
	public boolean checkAccess(String[] userTypes, HttpServletRequest request,
			HttpServletResponse response, JspWriter out,boolean protect,String redirectPath) {		
		this.request = request;
		this.response = response;		
		// this.out = out;

//		System.out.println("session is " + session.isNew());
		this.session = this.request.getSession(false);
		
		
		
		String sessionid = null;
		if(session != null)
		{
			sessionid = session.getId();
		}
//		log("checkAccess ,puid["+ puid+"] sessionid["+ sessionid +"] ["+ request.getRequestURI() + this.getParameters(request) +"] ." ,request);
		
//		log("checkAccess puid["+ puid+"],Localid["+ LocalServerPort+"] " ,request);
		if(session == null)
		{
			
//			String puid = request.getParameter("uid");
//			String LocalServerPort = request.getServerPort() + "";
			
			
			if(protect)
			{
				log("Session is null, Check page["+ request.getRequestURI() + this.getParameters(request) +"] will go to "+pathloginPage+"." ,request);
				return innerRedirect(redirectPath);
			}
			else
				return false;
		}
		
		
//		if(this.session.isNew()){	
//			log("session is new, Check page["+ request.getRequestURI() +
//					"] session id is:"+this.session.getId(),request );
//			
//		}
		
		// onlineUser =
		// (OnLineUser)session.getServletContext().getAttribute("onlineUser");
		if (!protect) {
			return unprotectedCheck(redirectPath,userTypes);
			
		}
		principal = (Principal) session.getAttribute(PRINCIPAL_INDEXS);
		// 添加用户的所有属性到session中
		credential = (Credential) session.getAttribute(CREDENTIAL_INDEXS);
		if (principal == null ) // 如果没有当前用户的会话信息，则判断cookie中是否有当前用户的登录信息
		{
			int ret = unlogincheck();
			if(ret == 1) //无cookie，或者客服端登陆失败
			{
				boolean isNew = false;
				try
				{
					isNew = session.isNew();
					log(" Check page["+ request.getRequestURI() + this.getParameters(request) + "]:  No user logon  will go to "+pathloginPage+".session id is " + sessionid + " and new is " + isNew,request);
				}
				catch(Exception e)
				{
					//e.printStackTrace();
					log(" Check page["+ request.getRequestURI() + this.getParameters(request) +"]:  No user logon  will go to "+pathloginPage+".session id is " + sessionid + " and get session status failed: " + e.getMessage(),request);
				}
				
				return innerRedirect(redirectPath);
			}
			else if(ret == 4)
			{
				log("Check page["+ request.getRequestURI() + this.getParameters(request) +"]: CS client user logon failed, will go to "+pathloginPage+".session id is " + sessionid,request);
				
				String redirecttarget = "_self";
				redirect(request,
						response, 
						redirectPath,
						redirecttarget,false); 
				return false;
//				return innerRedirect(redirectPath);
			}
			else if(ret == 2) //CS客服端登录成功
			{
				return true;
			}
			else if(ret == 3) //从cookie中读取用户会话信息成功
			{
				log(" Check page["+ request.getRequestURI() + this.getParameters(request) +"]: \r\n\tUser not logon ,but Get user info from cookie successed continue login.session id is " + sessionid,request);
			}
			else
			{
				log(" Check page["+ request.getRequestURI() + this.getParameters(request) +"]: \r\n\tUser not logon ,Will go to "+pathloginPage+".session id is " + session.getId()+ " and new is " + session.isNew(),request);
				return innerRedirect(redirectPath);
			}
		}
		else
		{
			this.subject = new Subject();
			
			this.subject.setCredential(credential);
			this.subject.setPrincipal(principal);
		}
		{
			
		}
//		else // 如果有已经登陆会话信息，直接获取用户的会话信息
		{
			
//			// mmmm
//			String sessionid_ = (String)session.getAttribute(SESSIONID_CACHE_KEY);
//			Boolean fromclient =  (Boolean)session.getAttribute(SESSIONID_FROMCLIENT_KEY);
			if (this.credential != null) {
				 
			} 
			else 
			{
				log("Check page["+ request.getRequestURI() +"]: \r\n\tUser[" + principal + "]'s credentialIndexs not exist,Will go to login.page.session id is " + sessionid,request);
				return this.innerRedirect(redirectPath);
			}
//			if (principal != null
//					&& (fromclient == null && !onlineUser.existUser(principal.getName(), sessionid_))) {
//
//				return innerLogout(redirectPath,"kickout");
//			}
//			if (this.principalIndexs != null) {
			
			
//			// mmmm
//			String sessionid = (String)session.getAttribute(SESSIONID_CACHE_KEY);
//			Boolean fromclient =  (Boolean)session.getAttribute(SESSIONID_FROMCLIENT_KEY);
//			if (principal != null
//					&& (fromclient == null && !onlineUser.existUser(principal.getName(), sessionid))){
//				return innerLogout(redirectPath,"kickout");
//			}
			/**
			 * 检查当前用户的类型是否在被允许的范围内，不允许则跳转到相应的页面
			 */
//			if (!checkUserType(userTypes))
//			{
//				return this.innerRedirect(redirectPath);
//			}
//			} else {
//				return this.innerRedirect(redirectPath);
//			}
			
			current.set(this);
			
			
			return true;

		}
		

	}

	/**
	 * 当前访问系统的用户是否登录过系统，并且该用户的类型必须是给定的userType类型，
	 * 否则将不允许用户访问请求的资源，并跳转到redirectPath对应的页面
	 * 
	 * @param userType
	 * @param request
	 * @param response
	 * @param out
	 * @param redirectPath
	 * @deprecated 请参考方法 checkAccess(String userType,HttpServletRequest request,
	 *             HttpServletResponse response, String redirectPath)
	 * @return
	 */
	public boolean checkAccess(String userType, HttpServletRequest request,
			HttpServletResponse response, JspWriter out, String redirectPath) {
		return checkAccess(new String[] {userType}, request,
				response, out, redirectPath);


	}

	/**
	 * 检查用户是否有访问系统的权限
	 * 
	 */
	public boolean checkAccess(PageContext pageContext, boolean protect) {
		return checkAccess((HttpServletRequest) pageContext.getRequest(),
				(HttpServletResponse) pageContext.getResponse(), null, protect);
	}

	/**
	 * 重定向到相关的页面 重定项之前首先检查response的状态，如果状态为commited则不跳转，直接返回。
	 * 如果commited为false再对参数标识的页面地址进行处理，处理方法如下：
	 * 如果redirectPath的第一个字符为"/"或者"\\"，并且路径中没有包含应用的contextPath时，
	 * 本方法通过处理将contextPath追加在redirectPath之前，然后跳转到处理过的页面地址。
	 * 否则不进行处理，直接跳转到redirectPath对应的页面。
	 * 
	 * @param request
	 * @param response
	 * @param redirectPath
	 */

	public static void redirect(HttpServletRequest request,
			HttpServletResponse response, String redirectPath) {
		if (redirectPath == null || redirectPath.trim().equals("")) {
			redirectPath = ConfigManager.getInstance().getLoginPage();
		}
		if (redirectPath == null || redirectPath.trim().equals(""))
			redirectPath = "/login.page";
		try {

			// 如果response已经提交过，则不执行重定向操作，否则执行
			if (!response.isCommitted()) {
				String t_redirectPath = StringUtil.getRealPath(request,
						redirectPath);
				response.sendRedirect(t_redirectPath);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重定向到相关的页面 重定项之前首先检查response的状态，如果状态为commited则不跳转，直接返回。
	 * 如果commited为false再对参数标识的页面地址进行处理，处理方法如下：
	 * 如果redirectPath的第一个字符为"/"或者"\\"，并且路径中没有包含应用的contextPath时，
	 * 本方法通过处理将contextPath追加在redirectPath之前，然后跳转到处理过的页面地址。
	 * 否则不进行处理，直接跳转到redirectPath对应的页面。
	 * 
	 * @param request
	 * @param response
	 * @param redirectPath
	 * @param redirecttarget
	 */

	public static void redirect(HttpServletRequest request,
			HttpServletResponse response, String redirectPath,
			String redirecttarget) {
		redirect(request,
				 response,  redirectPath,
				 redirecttarget,false);

	}
	
	public static void redirect(HttpServletRequest request,
			HttpServletResponse response, String redirectPath,
			String redirecttarget,boolean _alertMsg) {
		if (redirectPath == null || redirectPath.trim().equals("")) {
			redirectPath = ConfigManager.getInstance().getLoginPage();
		}
		if (redirectPath == null || redirectPath.trim().equals(""))
			redirectPath = "/login.page";

		if (redirecttarget == null || redirecttarget.trim().equals(""))
			redirecttarget = "top";

		try {
			// 如果response已经提交过，则不执行重定向操作，否则执行
			if (!response.isCommitted()) {
				StringBuffer url = new StringBuffer(request.getContextPath());
				url.append("/" )
				.append( LOGOUT_REDIRECT)
						.append("?_redirectPath=" )
						.append( StringUtil.encode(redirectPath))
						.append( "&_redirecttarget=" )
						.append( redirecttarget)						
						;
				if(_alertMsg)
				{
					url.append( "&_alertMsg=true");
				}
				response.sendRedirect(url.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void redirect(boolean _alertMsg) {

		try {
			// 如果response已经提交过，则不执行重定向操作，否则执行
			//从分页列表树数据加载器执行页面保护失败时，不需要跳转，所以response为null，
			//此处需要判断reponse是否为null
			if (response != null && !response.isCommitted()) {
				StringBuffer url = new StringBuffer(request.getContextPath());
				url.append("/")
				.append(LOGOUT_REDIRECT)
				.append("?_redirectPath=")
				.append(StringUtil.encode(request.getContextPath() + "/"
						+ loginPage) );
				if(_alertMsg)
				{
					url.append( "&_alertMsg=true");
				}
				
				
//				{
//					MemTokenManager memTokenManager = org.frameworkset.web.token.MemTokenManagerFactory.getMemTokenManagerNoexception();
//					if(memTokenManager != null)//如果开启令牌机制就会存在memTokenManager对象，否则不存在
//					{
//						url.append("&").append(MemTokenManager.temptoken_param_name).append("=").append(memTokenManager.buildDToken(request));
//					}
//				}
				response.sendRedirect(url.toString());

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void redirect() {

		redirect(false);

	}
	
	private void redirectManager(HttpServletRequest request,HttpServletResponse response,String redirectPath) {
		try {
			if (response != null && !response.isCommitted()) {
				StringBuffer url = new StringBuffer(request.getContextPath());
				url.append(redirectPath);
				response.sendRedirect(url.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 检查用户是否有访问系统的权限
	 * 
	 */
	public boolean checkAccess(HttpSession session) {
		this.session = session;
		// onlineUser =
		// (OnLineUser)session.getServletContext().getAttribute("onlineUser");
		principal  = (Principal) session.getAttribute(PRINCIPAL_INDEXS);
		// 添加用户的所有属性到session中
		credential  = (Credential) session.getAttribute(CREDENTIAL_INDEXS);

		return true;
	}

	/**
	 * 检查用户是否有访问系统的权限
	 * 
	 */
	public boolean checkAccess(PageContext pageContext) {
		return this.checkAccess((HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse());
//		return this.checkAccess(pageContext, true);
	}

	/**
	 * 检查权限接口
	 * 
	 * @param pageContext
	 *            PageContext
	 * @param resourceID
	 *            String
	 * @return boolean
	 */
	public boolean checkPermission(PageContext pageContext, String resourceID,
			String action) {
		// onlineUser = (OnLineUser)pageContext.getAttribute("onlineUser");
		if (!ConfigManager.getInstance().securityEnabled())
			return true;
		else {

			this.request = (HttpServletRequest) pageContext.getRequest();
			this.response = (HttpServletResponse) pageContext.getResponse();
			// this.out = pageContext.getOut();
			this.session = request.getSession(false);
			if(session == null)
			{
				log("Check Permission failed: session is null.",request);
				return false;
			}
			this.principal  = (Principal) session.getAttribute(PRINCIPAL_INDEXS);
			this.credential  = (Credential) session
					.getAttribute(CREDENTIAL_INDEXS);
			String resourceType = ConfigManager.getInstance().getResourceInfo()
			.getId();

			return checkPermission( resourceID, action,resourceType);
		}
	}

	/**
	 * 检查用户userAccount是否拥有资源resourceId的操作action的权限
	 * 
	 * @param pageContext
	 *            PageContext
	 * @param resourceID
	 *            String
	 * @return boolean
	 */
	public static boolean checkPermission(Principal principal,
			String resourceID, String action) {
		return checkPermission(principal, resourceID, action, null);
	}

	/**
	 * 检查用户userAccount是否拥有资源resourceId的操作action的权限
	 * 
	 * @param pageContext
	 *            PageContext
	 * @param resourceID
	 *            String
	 * @return boolean
	 */
	public static boolean checkPermission(Principal principal,
			String resourceID, String action, String resourceType) {

		if (resourceType == null)
			resourceType = ConfigManager.getInstance().getResourceInfo()
					.getId();
		
		boolean ret = false;
		if(ConfigManager.getInstance().getPermissionModule() != null)
		{
			ret = ConfigManager.getInstance().getPermissionModule().checkPermission(principal,resourceID, action, resourceType);
		}
		String userID = ((AuthPrincipal)principal).getUserID();
//		if(userID == null)
//			userID = AccessControl.getUserIDByUserAccount(principal.getName());
//		
//		if (ROLE_RESOURCE.equalsIgnoreCase(resourceType)) // 判断角色是否当前用户创建，如果是当前用户创建则拥有该角色的全部操作权限
//		{
//			if (isAdmin(principal.getName()))
//				return true;
//			
//			Role role = RoleCacheManager.getInstance().getRoleByID(resourceID);
//			if (role != null) {
//				if (userID.equals(role.getOwner_id() + ""))
//					return true;
//			}
//
//		}
//
//		else if (JOB_RESOURCE.equalsIgnoreCase(resourceType)) {// 判断岗位是否当前用户创建，如果是当前用户创建则拥有该岗位的全部操作权限
//			if (isAdmin(principal.getName()))
//				return true;
//			try {
//				boolean state = JobManagerImpl.isJobCreatorByUserId(userID, resourceID);
//				if (state) {
//					return true;
//				}
//			} catch (ManagerException e) {
//				e.printStackTrace();
//			}
//		}
//
//		else if (GROUP_RESOURCE.equalsIgnoreCase(resourceType)) // 判断用户组是否当前用户创建，如果是当前用户创建则拥有该用户组的全部操作权限
//		{
//			if (isAdmin(principal.getName()))
//				return true;
//			Group group = GroupCacheManager.getInstance().getGroupByID(
//					resourceID);
//			if (group != null) {
//				if (userID.equals(group.getOwner_id() + ""))
//					return true;
//			}
//
//		}
		if(!ret)
			ret = AppSecurityCollaborator.getInstance().checkAccess(principal,
				resourceID, action, resourceType);
		return ret;
	}

	  

	private static String getUserIDByUserAccount(String name) {
		if(ConfigManager.getInstance().getPermissionModule() != null)
			return ConfigManager.getInstance().getPermissionModule().getUserIDByUserAccount(name);
		return null;
		
	}



	/**
	 * 检查用户userAccount是否拥有资源resourceId的操作action的权限
	 * 
	 * @param pageContext
	 *            PageContext
	 * @param resourceID
	 *            String
	 * @return boolean
	 */
	public static boolean checkPermission(String useraccount,
			String resourceID, String action, String resourceType) {
		Principal principal = new AuthPrincipal(useraccount, null, null);
		return checkPermission(principal,
				 resourceID,  action,  resourceType);
		
	}

//	/**
//	 * 检查用户userAccount是否拥有资源resourceId的操作action的权限
//	 * 
//	 * @param pageContext
//	 *            PageContext
//	 * @param resourceID
//	 *            String
//	 * @return boolean
//	 */
//	public static boolean checkPermissionByUserID(String userID,
//			String resourceID, String action, String resourceType) {
//		String useraccount = AccessControl.getUserAccountByUserID(userID);
//
//		Principal principal = new AuthPrincipal(useraccount,  null,userID);
//		return checkPermission(principal,
//				 resourceID,  action,  resourceType);
//	}

	/**
	 * 检测当前系统用户是否拥有访问资源的权限
	 * 
	 * @param resourceID
	 * @param action
	 * @param resourceType
	 * @return
	 */
	public boolean checkPermission(String resourceID, String action,
			String resourceType) {
		
		return checkPermission( resourceID,  action,
				 resourceType,  false,  null);
	}
	
	

	
	/**
	 * 检测当前系统用户是否拥有访问资源的权限
	 * 
	 * @param resourceID
	 * @param action
	 * @param resourceType
	 * @return
	 */
	public boolean checkURLPermission(String uri) {

		return AppSecurityCollaborator.getInstance().getPermissionTokenMap().checkUrlPermission(uri);
		
	}

	/**
	 * 检测当前系统用户是否拥有访问资源的权限，如果没有则跳转到权限提示页面 否则允许用户访问当前资源
	 * 根据条件redirect决定是否跳转，true表示跳转，false表示不跳转
	 * 
	 * @param resourceID
	 * @param action
	 * @param resourceType
	 * @param redirect
	 * @return
	 */
	public boolean checkPermission(String resourceID, String action,
			String resourceType, boolean redirect) {
		return checkPermission( resourceID,  action,
				 resourceType,  redirect,  null);
	}

	/**
	 * 检测当前系统用户是否拥有访问资源的权限，如果没有则跳转到权限提示页面 否则允许用户访问当前资源
	 * 根据条件redirect决定是否跳转，true表示跳转，false表示不跳转
	 * 
	 * @param resourceID
	 * @param action
	 * @param resourceType
	 * @param redirect
	 * @return
	 */
	public boolean checkPermission(String resourceID, String action,
			String resourceType, boolean redirect, String redirectPath) {
		//如果使用新版系统管理注释掉机构资源的判断    org_org
		//如果是机构资源且用户拥有该机构的管理员身份，则返回true
		boolean ret = false;
		if(ConfigManager.getInstance().getPermissionModule() != null)
		{
			ret = ConfigManager.getInstance().getPermissionModule().checkPermission(principal,resourceID, action, resourceType);
					
		}
//		if(resourceType.equals(ORGUNIT_RESOURCE)){
//			if(this.isAdmin())
//				return true;
//			//判断当前用户是否是resourceID对应的机构何上级机构的管理员，如果是的话，不管是什么操作都是可以做的
//			if(this.isOrganizationManager(resourceID))
//			{
//				return true;
//			}
//			else if(action.equals(READ_PERMISSION))//如果当前用户是resourceID对应的机构的下级机构的管理员，可以进行读操作
//			{
//				if(this.isSubOrgManager(resourceID)){
//					return true;
//				}
//			}
//		}
//
//		if (ROLE_RESOURCE.equalsIgnoreCase(resourceType)) // 判断角色是否当前用户创建，如果是当前用户创建则拥有该角色的全部操作权限
//		{
//			if (this.isAdmin())
//				return true;
//			Role role = RoleCacheManager.getInstance().getRoleByID(resourceID);
//			if (role != null) {
//				if (this.getUserID().equals(role.getOwner_id() + ""))
//					return true;
//			}
//
//		}
//
//		else if (JOB_RESOURCE.equalsIgnoreCase(resourceType)) {// 判断岗位是否当前用户创建，如果是当前用户创建则拥有该岗位的全部操作权限
//			if (this.isAdmin())
//				return true;
//			try {
//				boolean state = JobManagerImpl.isJobCreatorByUserId(this
//						.getUserID(), resourceID);
//				if (state) {
//					return true;
//				}
//			} catch (ManagerException e) {
//				e.printStackTrace();
//			}
//		}
//
//		else if (GROUP_RESOURCE.equalsIgnoreCase(resourceType)) // 判断用户组是否当前用户创建，如果是当前用户创建则拥有该用户组的全部操作权限
//		{
//			if (this.isAdmin())
//				return true;
//			Group group = GroupCacheManager.getInstance().getGroupByID(
//					resourceID);
//			if (group != null) {
//				if (this.getUserID().equals(group.getOwner_id() + ""))
//					return true;
//			}
//
//		}

		
		if(!ret)
			ret =  AppSecurityCollaborator.getInstance().checkAccess(principal,
				resourceID, action, resourceType);
		if (!ret && redirect) {
			log("permission check failed,will go to " + redirectPath + "/" + authorfailedPage,request);
			// try {
			if (redirectPath == null || redirectPath.trim().equals(""))
				redirectPath = "/" + authorfailedPage;
			redirect(request, response, redirectPath);
			
		}
		return ret;
	}

	/**
	 * 登出系统，通过redirected参数控制是否跳转
	 * 
	 * @param redirected
	 */
	public void logout(boolean redirected) {
		logout(redirected, true);
	}

	/**
	 * 释放所有的session中的系统资源
	 */
	private static void releaseSession(HttpSession session) {
//		try
//		{
//			session.removeAttribute(AccessControl.PRINCIPAL_INDEXS);
//			session.removeAttribute(REMOTEADDR_CACHE_KEY);
//			session.removeAttribute(MACADDR_CACHE_KEY);
//			session.removeAttribute(SESSIONID_CACHE_KEY);
//			
//			// 添加用户的所有属性到session中
//			session.removeAttribute(CREDENTIAL_INDEXS);
//			session.removeAttribute(SERVER_IP_KEY);
//			session.removeAttribute(SERVER_PORT_KEY);
//			
//			session.removeAttribute(Framework.SUBSYSTEM);
//			session.removeAttribute(AccessControl.SESSIONID_FROMCLIENT_KEY);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
		

	}
	
	public boolean enablecookie()
	{
		return ConfigManager.getInstance().isSecuritycookieenabled()
		&& (ConfigManager.getInstance().securityEnabled() || ConfigManager
				.getInstance().isSSO());
		
	}

	
	/**
	 * 用户从dreamweaver客户端退出系统，不重定向系统
	 */

	public void logoutdw() {

		// LoginContext loginContext = (LoginContext) session
		// .getAttribute(LOGINCONTEXT_CACHE_KEY);
		

		try {
			if(session == null)
				return;
			Principal principals = (Principal) session.getAttribute(PRINCIPAL_INDEXS);

			if (principals == null) {

				return;
			}

			log("Check page["+ request.getRequestURI() +"]: User[" + this.getUserAccount() + "] logout from cs client。session id is " + session.getId(),request);
		
			String userAccount = this.getUserAccount();
			// 清除cookie;
//			if (this.enablecookie()) {
//
//				
////				String subsystemcookieid = SUBSYSTEM_COOKIE + "_" + userAccount;
//				Cookie[] cookies = request.getCookies();
//				for (int i = 0; cookies != null && i < cookies.length; i++) {
//					if (cookies[i].getName().equals(PRINCIPALS_COOKIE)) {
//						// 设置cookie一秒后失效
//						cookies[i].setMaxAge(1);
//						response.addCookie(cookies[i]);
//
//					} 
////					else if (cookies[i].getName()
////							.equals(subsystemcookieid)) {
////						cookies[i].setMaxAge(1);
////
////						response.addCookie(cookies[i]);
////					}
//				}
//				
//
//			}
			
			
			current.set(null);
			// 使session失效
			// session.removeAttribute(LOGINCONTEXT_CACHE_KEY);
		
			String machineID = getMachinedID();
			
			releaseSession(session);
			// onlineUser.valueUnbound(session.getId(),getUserAccount(),
			// request.getRemoteAddr());
			String userName = this.getUserName();
			
			String userId = this.getUserID();
			
			session.invalidate();
			// System.out.println("session:" + session);
			// ------------退出时保存用户日志信息
			try {
				LogManagerInf logMgr =  ConfigManager.getInstance().getLogManager();
				String operContent = userName + "[" + userId + "]"
						+ " 从客户端退出系统";
				String operSource = machineID;
				
				
				
				
				String openModle = "认证管理";

				logMgr.log(userAccount, operContent,
						openModle, operSource);
			} catch (SPIException e1) {
				e1.printStackTrace();
			} 

			// // -------------------
			// // 跳转到登录页面
			// if (redirected)
			// redirect();
		} 
		 catch (Exception ex) {
			log.debug("Logout from dreamweaver client failed：" + ex.getMessage());
		}
		finally
		{
			current.set(null);
		}
	}

	/**
	 * 用户登录退出
	 * 
	 * @param redirected
	 *            重定向控制开关
	 * @param enablelog
	 *            日志控制开关
	 */

	public void logout(boolean redirected, boolean enablelog) {

		logout(null, null,redirected, enablelog);

	}

	/**
	 * 如果系统没有正常退出系统，强制用户退出,当session实效之前， 如果用户还没有退出，调用该方法强制当前用户退出系统
	 * 
	 * @param pageContext
	 *            PageContext
	 */
	public static void logoutdirect(HttpSession session) {

		try
		{
//			log("Enter logoutdirect from session destroyed event.   session id is:"+session.getId(),null);
			Principal principal_ = (Principal) session.getAttribute(PRINCIPAL_INDEXS);
	
			if (principal_ == null) {	
				log("Unknowken user logoutdirect from session destroyed event.",null);
				return;
			}
//			String address = (String) session
//			.getAttribute(AccessControl.REMOTEADDR_CACHE_KEY);
			log_info("User[" + principal_ + "] logoutdirect from session destroyed event.session id is "+session.getId(),null);
			try {
				
				String userName = principal_.getName();
				
//				LoginContext loginContext = new LoginContext(subject);
//				loginContext.logout();
				// session.removeAttribute(LOGINCONTEXT_CACHE_KEY);
				if (userName == null) {
	
					return;
				}
//				
//				onlineUser.valueUnbound(session.getId(), userName, address,(String)session
//						.getAttribute(AccessControl.MACADDR_CACHE_KEY));
//				releaseSession(session);
////				current.set(null);
//	
//				
//	
			} catch (Exception ex) {
				log.debug("退出登录失败：" + ex.getMessage());
			}
		}
		finally
		{
//			current.set(null);
		}
	}

	private String _alt;
	/**
	 * 用户登录退出
	 * 
	 * @param pageContext
	 *            PageContext
	 */
	public void logout() {

		logout(true, true);
	}

	/**
	 * 用户登录退出
	 * 
	 * @param redirect
	 *            String 用户退出系统重定向页面
	 */
	public void logout(String redirect) {
		logout(redirect, true);
	}

	/**
	 * 获取本地线程中的访问控制类
	 * 
	 * @return
	 */
	public static Subject getLocalSubject() {
		return getAccessControl().subject;
	}
	public static boolean fromWebseal(HttpServletRequest request)
	{
		boolean isWebSealServer = ConfigManager.getInstance()
				.getConfigBooleanValue("isWebSealServer", false);
		String user_name = request.getHeader("iv-user");
		 if(isWebSealServer && user_name!= null && !user_name.equals(""))
			 return true;
		 return false;
	}
	public static AccessControl getAccessControl() {
		AccessControl context = (AccessControl) current.get();
		if(context == null)
		{
			context = AccessControl.guest;
		}
		return context;
	}
	
	public static AccessControl getAccessControl(HttpServletRequest request,HttpServletResponse response) {
		AccessControl context = (AccessControl) current.get();
		if(context == null)
		{
			context = AccessControl.getAccessControl(request);
			if(context == null)
			{
				context = AccessControl.getInstance();
				boolean success = context.checkAccess(request, response,false);
				if(success)
				{
					request.setAttribute(AccessControl.accesscontrol_request_attribute_key,context);
					return context;
				}
				else
					return AccessControl.guest;
			}
		}
		return context;
	}

	
	/**
	 * 用户登录退出
	 * 
	 * @param redirect
	 *            String 用户退出系统重定向页面
	 */
	public void logout(String redirect, boolean enablelog) {
		logout( redirect,  null, true,  enablelog);
//		Map principalsIndexs = (Map) session.getAttribute(PRINCIPAL_INDEXS);
//
//		if (principalsIndexs == null) {
//			redirect();
//			return;
//		}
//		try {
//			String userName = getUserName();
//			log.debug("用户[" + getUserName() + "]退出系统。");
//
//			LoginContext loginContext = new LoginContext(this.getSubject());
//			loginContext.logout();
//			// 清除cookie;
//			Cookie[] cookies = request.getCookies();
//			for (int i = 0; i < cookies.length; i++) {
//				if (cookies[i].getName().equals(PRINCIPALS_COOKIE)) {
//					// 设置cookie一秒后失效
//					cookies[i].setMaxAge(1);
//
//					response.addCookie(cookies[i]);
//
//				} else if (cookies[i].getName().equals(SUBSYSTEM_COOKIE)) {
//					// 设置cookie一秒后失效
//					cookies[i].setMaxAge(1);
//
//					response.addCookie(cookies[i]);
//
//				}
//			}
//			// 使session失效
//			String subsystem = getCurrentSystemName();
//
//			String userAccount = this.getUserAccount();
//			String userId = this.getUserID();
//			onlineUser.valueUnbound(session.getId(), userAccount, request
//					.getRemoteAddr());
//			releaseSession(session);
//			session.invalidate();
//			// ------------退出时保存用户日志信息
//			if(enablelog)
//			{
//				try {
//					LogManager logMgr = SecurityDatabase.getLogManager();
//	
//					String operContent = userName + "[" + userId + "] 退出["
//							+ subsystem + "]";
//					String operSource = request.getRemoteAddr();
//					String openModle = "认证管理";
//	
//					logMgr.log(userAccount + ":" + userName, operContent,
//							openModle, operSource);
//				} catch (SPIException e1) {
//					e1.printStackTrace();
//				} catch (ManagerException e) {
//					e.printStackTrace();
//				}
//			}
//
//			
//			// 跳转到登录页面
//			redirect(request, response, redirect);
//		} catch (LoginException ex) {
//			log.debug("退出登录失败：" + ex.getMessage());
//		}
	}

	/**
	 * 用户登录退出
	 * 
	 * @param redirect
	 *            String 用户退出系统重定向地址
	 * @param redirecttarget
	 *            String 用户退出系统重定向窗口
	 * @param enablelog
	 *            用户退出系统是否记录日志
	 */
	public void logout(String redirect, String redirecttarget,boolean redirected, boolean enablelog) {

		
		logoutwithalt_( redirect,  redirecttarget, redirected,  enablelog,null);
	}
	
	/**
	 * 用户登录退出
	 * 
	 * @param redirect
	 *            String 用户退出系统重定向地址
	 * @param redirecttarget
	 *            String 用户退出系统重定向窗口
	 * @param enablelog
	 *            用户退出系统是否记录日志
	 */
	public void logoutwithalt_(String redirect, String redirecttarget,boolean redirected, boolean enablelog,String _alt) {

		
		

		try {
			if(session == null)
			{
				if(redirected )
				{
					log_info("Unknown user Logout to "+redirect+" from system on " + new java.util.Date() + ". session is null.",request);
					if(redirect == null && redirecttarget == null)
					{
						redirect();
					}
					else
					{
						redirect(request,response,redirect,redirecttarget);
					}
				}
				
				return;
			}
//			Principal principalsIndexs = (Principal) session.getAttribute(PRINCIPAL_INDEXS);

			if (this.principal == null) {
				if(redirected )
				{
					log_info("Unknown user Logout from system on " + new java.util.Date() + ". session id is " + session.getId(),request);
					if(redirect == null && redirecttarget == null)
					{
						redirect();
					}
					else
					{
						redirect(request,response,redirect,redirecttarget);
					}
				}
				
				return;
			}
			log_info("Logout from page["+ request.getRequestURI() +"]: User["+ this.getUserAccount() + "," + getUserName() + "] logout.session id is " + session.getId(),request);
			//log.debug("Logout from page["+ request.getRequestURI() +"]: \r\n\tUser["+ this.getUserAccount() + "," + getUserName() + "] logout.");
			
			
			
			
				
				
			String userAccount = this.getUserAccount();
			if(subject == null)
			{
				subject = new Subject();
				subject.setCredential(credential);
				subject.setPrincipal(principal);
			}
			
			LogoutCallbackHandler callbackHandler = new LogoutCallbackHandler(
					userAccount,  request,response);
			SimpleLoginContext loginContext = new SimpleLoginContext("base",
					callbackHandler,this.subject);
			
			loginContext.logout();
			String subsystem = getCurrentSystemName();
//			String subsystemcookieid = SUBSYSTEM_COOKIE + "_" + userAccount;
			
			// 清除cookie;
//			if(this.enablecookie())
//			{
//				Cookie[] cookies = request.getCookies();
//				for (int i = 0; cookies != null && i < cookies.length; i++) {
//					if (cookies[i].getName().equals(PRINCIPALS_COOKIE)) {
//						// 设置cookie一秒后失效
//						cookies[i].setMaxAge(1);	
//						response.addCookie(cookies[i]);
//	
//					} 
////					else if (cookies[i].getName().equals(subsystemcookieid)) {
////						// 设置cookie一秒后失效
////						cookies[i].setMaxAge(1);	
////						response.addCookie(cookies[i]);
////	
////					} 
//					
//				}
//			}
//			Cookie subsystemCookie = new Cookie(subsystemcookieid, subsystem);
//			subsystemCookie.setMaxAge(1);
//			response.addCookie(subsystemCookie);
//			else if (cookies[i].getName().equals(subsystemcookieid )) {
//				// 设置cookie一秒后失效
//				cookies[i].setMaxAge(1);
//
//				response.addCookie(cookies[i]);
//
//			}

			
			// 使session失效
			
			
			String userId = this.getUserID();

			String userName = this.getUserName();
			
			String machineIP = (String)this.credential.getCheckCallBack().getUserAttribute(REMOTEADDR_CACHE_KEY);
			String orgID = this.getChargeOrgId();
//			onlineUser.valueUnbound(session.getId(), userAccount, machineIP,(String)session.getAttribute(MACADDR_CACHE_KEY));

			String machineID = this.getMachinedID();
			
			current.set(null);
			releaseSession(session);
			session.invalidate();
			// ------------退出时保存用户日志信息
			if(enablelog)
			{
				try {
					LogManagerInf logMgr = ConfigManager.getInstance().getLogManager();
					
//					String operContent = userName + "[" + userId + "] 退出["
//							+ subsystem + "]";
					// modified by hilary on 20101105,for fixing bug 13979,for logout's log  and login's log has same manner 
					String operContent = userAccount + "(" + userName + ") 退出[" + subsystem + "]";
					if(_alt != null)
						operContent = operContent + ",退出原因为：该用户在其他地方登录或者用户会话信息被管理员清除。";
					String operSource = machineID;
					String operModle = "认证管理";
					logMgr.log(userAccount,orgID,operModle,  operSource,
							operContent ,"", LogManagerInf.INSERT_OPER_TYPE);		
					
				} catch (SPIException e1) {
					//e1.printStackTrace();
				} 

			}
			// -------------------


			if(redirected )
			{
				if(redirect == null && redirecttarget == null)
				{
					log_info("User["+ this.getUserAccount() + "," + getUserName() + "] logout." + new java.util.Date() + ". " + _alt,request);
					if(_alt == null)
					{
						redirect();
					}
					else
					{
						redirect(true);
					}
				}
				else
				{
					log_info("User["+ this.getUserAccount() + "," + getUserName() + "] logout." + new java.util.Date() + ". " + _alt + ".redirect=" + redirect + ",redirecttarget=" +redirecttarget,request);
					if(_alt == null)
					{
						redirect(request,response,redirect,redirecttarget);
					}
					else
					{
						redirect(request,response,redirect,redirecttarget,true);
					}
					
				}
			}
		}  catch (Exception ex) {
			ex.printStackTrace();
			//log("Unknown user Logout failed from system on " + new java.util.Date() + ". ",request);
			log.debug("Logout failed：" + ex.getMessage());
		}
		finally
		{
			current.set(null);
		}
	}

	/**
	 * 返回用户id
	 * 
	 * @return long
	 */
	public String getUserID() {
		if (credential == null)
			return "";
		Object userID = credential.getCheckCallBack()
				.getUserAttribute("userID");

		return userID == null ? "" : userID.toString();
	}

	/**
	 * 返回用户帐号
	 * 
	 * @return long
	 */
	public String getUserAccount() {
		if (principal != null) {
			String userAccount = this.principal.getName();
			return userAccount == null ? "" : userAccount;
		}
		return "";
	}

	/**
	 * 返回用户真实名称
	 * 
	 * @return long
	 */
	public String getUserName() {
		if (credential == null)
			return "";
		Object userName = credential.getCheckCallBack().getUserAttribute(
				"userName");

		return userName == null ? "" : userName.toString();
	}


	
	/**
	 * 判断用户是否是系统管理员
	 * 
	 * @return boolean
	 */

	// public boolean isAdmin()
	// {
	// if(roles == null)
	// {
	// String t_roles = credential.getCheckCallBack().getUserAttribute(
	// "userRoles");
	// if (t_roles != null)
	// this.roles = StringUtil.split("\\,");
	// }
	// for(int i = 0; i < roles.length;i ++)
	// {
	// if(roles[i].equals(ADMINISTRATOR_ROLE))
	// return true;
	// }
	// return false;
	// }
	public Principal getPrincipal() {
		return principal;
	}

	public Credential getCredential() {
		return credential;
	}

	/**
	 * 获取用户当前登录的子系统标识
	 * 
	 * @return
	 * @deprecated 
	 * @see use getCurrentSystemID()
	 */
	public String getCurrentSystem() {
		return FrameworkServlet.getSubSystem(this.request, this.response, this
				.getUserAccount());
	}

	/**
	 * 判断当前用户是否是系统管理员
	 * 
	 * @return boolean
	 */
	public boolean isAdmin() {
		return AppSecurityCollaborator.getInstance().isAdmin(this.principal);
	}

	public static boolean isAdmin(String userAccount) {
		if (userAccount == null)
			return false;
		AuthPrincipal principal = new AuthPrincipal(userAccount, null, null);
		return AppSecurityCollaborator.getInstance().isAdmin(principal);
	}

	

	/**
	 * 判断给定的角色是否是超级管理员角色
	 * 
	 * @param role
	 * @return
	 */
	public static boolean isAdministratorRole(String role) {
		if (role == null)
			return false;
		String administratorRole = AppSecurityCollaborator.getInstance()
				.getAdministratorRoleName();

		return administratorRole.equals(role);
	}

	/**
	 * 判断给定的角色是否是每个人都拥有的角色
	 * 
	 * @param role
	 * @return
	 */
	public static boolean isRoleOfEveryOne(String role) {
		if (role == null)
			return false;
		String roleOfEveryOne = AppSecurityCollaborator.getInstance()
				.getEveryonegrantedRoleName();

		return roleOfEveryOne.equals(role);
	}

	/**
	 * 判断当前用户是否授予给定的角色
	 * 
	 * @param roleName
	 *            String
	 * @return boolean
	 */
	public boolean isGrantedRole(AuthRole role) {
		return AppSecurityCollaborator.getInstance().isCallerInRole(
				new AppAccessContext(ConfigManager.getInstance().getAppName(),
						ConfigManager.getInstance().getModuleName()), role,
				principal);
	}

	/**
	 * 判断当前用户是否授予给定的角色
	 * 
	 * @param roleName
	 *            String
	 * @return boolean
	 */
	public boolean isGrantedRole(String role) {
		AuthRole arole = new AuthRole();
		arole.setRoleName(role);
		return isGrantedRole(arole);
	}
	public boolean isOrgmanager()
	{
		return this.isGrantedRole(AuthRole.ORGMANAGER);
	}

	/**
	 * 获取超级管理员角色名称
	 * 
	 * @return
	 */
	public static String getAdministratorRoleName() {
		return AppSecurityCollaborator.getInstance().getAdministratorRoleName();
	}

	/**
	 * 获取每个人都有的角色名称
	 * 
	 * @return
	 */
	public static String getEveryonegrantedRoleName() {
		return AppSecurityCollaborator.getInstance()
				.getEveryonegrantedRoleName();
	}

	/**
	 *  
     * userName
userID
password
orgId
logincount
userAccount
remark1
remark2
remark3
remark4
remark5
userAddress
userEmail
userFax
userHometel
userIdcard
userMobiletel1
userMobiletel2
userOicq
userPinyin
userPostalcode
userSex
userType
userWorknumber
userWorktel
userBirthday
userRegdate
userSn
userIsvalid
passwordExpiredTime
passwordUpdateTime
     * @param userAttribute
     * @return
     */
	 
	public String getUserAttribute(String userAttribute) {
		try {
			Object value = credential.getCheckCallBack().getUserAttribute(
					userAttribute);
			return value == null ? "" : value.toString();
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 * 重置指定的用户属性值
	 * @param userAttribute
	 */
	public void resetUserAttribute(String userAttribute) {
		try {
			
			LoginContext.resetUserAttribute(request,credential.getCheckCallBack(), userAttribute);
//			LoginModuleInfoQueue moduleQueue = ConfigManager.getInstance().getDefaultApplicationInfo().getLoginModuleInfos();
//			credential.getCheckCallBack().setUserAttribute(userAttribute, value);
			session.setAttribute(CREDENTIAL_INDEXS, credential);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 重置指定的用户属性值
	 * @param userAttribute
	 */
	public void setUserAttribute(String userAttribute,Object value) {
		try {
			
			
			session.setAttribute(userAttribute, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 重置用户属性值
	 */
	public void resetUserAttributes() {
		try {
			
			LoginContext.resetUserAttribute(request,credential.getCheckCallBack());
//			LoginModuleInfoQueue moduleQueue = ConfigManager.getInstance().getDefaultApplicationInfo().getLoginModuleInfos();
//			credential.getCheckCallBack().setUserAttribute(userAttribute, value);
			
			session.setAttribute(CREDENTIAL_INDEXS, credential);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getUserObjectAttribute(String userAttribute) {
		try {
			Object value = credential.getCheckCallBack().getUserAttribute(
					userAttribute);
			return value;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判断用户是否登录过
	 * 
	 * @return
	 */
	public boolean isLogin(String userAccount) {
		if (userAccount == null)
			return false;
		if (this.principal == null)
			return false;
		else {
			String login_userAccount = principal.getName();
			return login_userAccount != null
					&& login_userAccount.equals(userAccount);
		}
	}

	public int getLoginUserCount() {
//		return onlineUser.getCount();
		return -1;
	}

	

	/**
	 * 判断给定类型的资源在没有授权的情况下是否允许用户访问
	 * 
	 * @param resourceType
	 * @return
	 */
	public boolean allowIfNoRequiredRoles(String resourceType) {
		return AppSecurityCollaborator.getInstance().allowIfNoRequiredRoles(
				resourceType);
	}

	/**
	 * 判断资源操作是否是未受保护的资源操作
	 * 
	 * @param resourceType
	 * @param resourceId
	 * @param operation
	 * @return
	 */
	public static boolean isUnprotected(String resourceId, String operation,
			String resourceType) {
		return AppSecurityCollaborator.getInstance().isUnprotected(resourceId,
				operation, resourceType);
	}

	/**
	 * 判断资源是否是未受保护的资源 true-表示无需授权所有人都能访问该资源的所有操作
	 * false-就要根据方法isUnprotected(String resourceId,String operation,String
	 * resourceType)的返回值来 判断每个操作是否无需授权所有人都能访问该资源操作
	 * 
	 * @param resourceType
	 * @param resourceId
	 * @return
	 */
	public static boolean isUnprotected(String resourceId, String resourceType) {
		return AppSecurityCollaborator.getInstance().isUnprotected(resourceId,
				resourceType);
	}

	/**
	 * 判断资源操作是否是系统管理员独占的资源操作
	 * 
	 * @param resourceType
	 * @param resourceId
	 * @param operation
	 * @return
	 */
	public static boolean isExcluded(String resourceId, String operation,
			String resourceType) {
		return AppSecurityCollaborator.getInstance().isExcluded(resourceId,
				operation, resourceType);
	}

	/**
	 * 判断资源所有操作是否只有超级管理员才能访问，返回： true-表示只有超级管理员才能访问,其他人不能访问
	 * false-就要根据方法isExcluded(String resourceId,String operation,String
	 * resourceType)的返回值来 判断每个操作是否是超级管理员独占的操作
	 * 
	 * @param resourceType
	 * @param resourceId
	 * @param operation
	 * @return
	 */
	public static boolean isExcluded(String resourceId, String resourceType) {
		return AppSecurityCollaborator.getInstance().isExcluded(resourceId,
				resourceType);
	}

	public static AuthRole[] getAllRoleofUser(String userAccount) {
		return AppSecurityCollaborator.getInstance().getAllRoleofUser(
				userAccount);
	}

	/**
	 * 判断资源是否已经授过权限
	 * 
	 * @param resource
	 * @param resourceType
	 * @return
	 */
	public static boolean hasGrantedAnyRole(String resource, String resourceType) {
		return AppSecurityCollaborator.getInstance().hasGrantedAnyRole(
				resource, resourceType);
	}

	/**
	 * 判断资源是否已经授过权限
	 * 
	 * @param resource
	 * @param resourceType
	 * @return
	 */
	public static boolean hasGrantedRole(String role, String roleType,
			String resource, String resourceType) {
		return AppSecurityCollaborator.getInstance().hasGrantedRole(role,
				roleType, resource, resourceType);
	}

	/**
	 * 获取拥有特定资源许可操作的用户列表
	 * 
	 * @param resourceid
	 * @param operation
	 * @param resourceType
	 * @return
	 */
	public static AuthUser[] getAllPermissionUsersOfResource(String resourceid,
			String operation, String resourceType) {
		return AppSecurityCollaborator.getInstance()
				.getAllPermissionUsersOfResource(resourceid, operation,
						resourceType);
	}

	
	public String getMacAddr() {
		String macaddr =null;
		if(credential != null)
//			macaddr = (String)this.session.getAttribute(AccessControl.MACADDR_CACHE_KEY);
			macaddr = (String)this.credential.getCheckCallBack().getUserAttribute(AccessControl.MACADDR_CACHE_KEY);
		if(macaddr == null)
			return "";
		return macaddr;
	}
	
	 
	
	//转换异常信息中的 \\n,\\r 
	public static String formatErrorMsg(String errorMessage){
		if(errorMessage != null)
        {
        	errorMessage = errorMessage.replaceAll("\\n","\\\\n");
        	errorMessage = errorMessage.replaceAll("\\r","\\\\r");
        	errorMessage = errorMessage.replaceAll("\"","\'");
        }
		return errorMessage;
	}
	
	/**
     * 清除角色和资源操作的缓冲关系
     */
	public static void resetPermissionCache(){
		AppSecurityCollaborator.getInstance().resetPermissionCache();
	}
	
	/**
     * 清除用户和角色的缓冲关系
     */
	public static void resetAuthCache(){
		AppSecurityCollaborator.getInstance().resetAuthCache();
	}
	
	/**
	 * 得到应用端口
	 * @return
	 */
	public String getPort(){
		return String.valueOf(request.getLocalPort());
	}
	
	/**
	 * 得到应用上下文
	 * @return
	 */
	public String getContextPath(){
		return request.getContextPath();
	}
	
	public static AccessControl getAccessControl(HttpServletRequest request)
	{
		return (AccessControl)request.getAttribute(AccessControl.accesscontrol_request_attribute_key);
	}



	public PageContext getPageContext() {
		return pageContext;
	}



	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}



	public HttpServletRequest getRequest() {
		return request;
	}



	public HttpSession getSession() {
		return session;
	}


	
	public void refreshCurrentSystemID(HttpServletRequest request) {
		String subsystem_id = request.getParameter(SUBSYSTEM_ID);
		if(!StringUtil.isEmpty(subsystem_id))
		{
			if (subsystem_id == null || subsystem_id.equals(""))
            {
                        ;
                // 将用户登录的子系统模块名称添加到session中
            }
            else
            {
                String old = (String)session.getAttribute(Framework.SUBSYSTEM);
                if(old != null && !old.equals(subsystem_id))
                    session.setAttribute(Framework.SUBSYSTEM, subsystem_id);
            }
			
			FrameworkServlet.setSubSystemToCookie(response, getUserAccount(), subsystem_id);
		}
		
	}


	public boolean evalResource(   PermissionToken token) {
		return _evalResource( token,request);
	}
	public int compareParams(List<P> paramConditions)
	{
		
		int nullcount = 0;
		
		
		boolean success = true;
	
		for(int i = 0; i < paramConditions.size(); i ++)
		{
			P p = paramConditions.get(i);
			String value = request.getParameter(p.getName());
			if(value != null)
			{
			
				if(!value.equals(p.getValue()))
				{
					
					success = false;
					
				}
			}
			else
			{
				nullcount ++;
			}
			
		}
		if(nullcount > 0)
			return 2;	
		return success?1:0;
	}
	
	/**
	 * 
	 * @param token
	 * @return 0:参数匹配不成功，1:参数匹配成功 2:request没有设置部分相关的参数 ,3:request完全没有设置对应的参数,4:没有配置参数
	 */
	public int compareParams(PermissionToken token)
	{
		if(!token.hasParamCondition())//带参数的token排在最前面，都已经比较过了，没有匹配的值，没有参数时，自然返回true
			return 4;
		List<P> params = token.getParamConditions();
//		int nullcount = 0;
		
		
		boolean success = true;
	
		for(int i = 0; i < params.size(); i ++)
		{
			P p = params.get(i);
			String value = request.getParameter(p.getName());
			if(value != null)
			{
			
				
				if(!value.equals(p.getValue()))
				{
					
					success = false;
					break;
					
				}
			}
			else
			{
				success = false;
				break;
			}
			
		}
//		if(nullcount > 0)
//			return 2;	
//		else if(nullcount == params.size() - 1)
//		{
//			return 3;
//		}
		return success?1:0;
		
		
		
	}
	public boolean _evalResource(  PermissionToken token,HttpServletRequest request) {
								
		{
			String resourceParamName = token.getResouceAuthCodeParamName();
			if(resourceParamName == null || resourceParamName.equals(""))
			{
				if(token.isResouceAuthCodeRequired())
				{
					return false;
				}
				else
					return true;
			}
			else
			{
				if(request != null)
				{
					String rid = request.getParameter(resourceParamName);
					if(rid == null)
					{
						return false;
					}
//					return token.getResourcedID().equals(rid);
					return checkPermission(rid, token.getOperation(),token.getResourceType());
				}
				else
				{
					
				}
				return false;
			}
		}
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean isGuest() {
		
		return this.getUserAccount() == null || this.getUserAccount().equals("") 
				|| this.getUserAccount().equals(BaseAuthorizationTable.guest);
	}



	public HttpServletResponse getResponse() {
		return response;
	}
	
	public static Map<String,MenuItemU> geMenus(HttpServletRequest request,AccessControl accesscontroler)
	{
		Map<String,MenuItemU> permissionMenus = new HashMap<String,MenuItemU>();
		MenuHelper menuHelper = MenuHelper.getMenuHelper(request);
		ModuleQueue moduleQueue = menuHelper.getModules();
		for (int i = 0; moduleQueue != null && i < moduleQueue.size(); i++) {
			Module module = moduleQueue.getModule(i);
			if (!module.isUsed()) {
				continue;
			}
			MenuItemU menuItemU = new MenuItemU();
			menuItemU.setId(module.getId());
			menuItemU.setName(module.getName(request));
			menuItemU.setImageUrl(module.getMouseclickimg(request));
			menuItemU.setType("module");
			permissionMenus.put(module.getId(), menuItemU);
			geSubMenus(  permissionMenus,  module,  request,  accesscontroler);
		}
		return permissionMenus;
	}
	
	public static void geSubMenus(Map<String,MenuItemU> permissionMenus,Module module,HttpServletRequest request,AccessControl accesscontroler)
	{
		
		MenuQueue menus = module.getMenus();
		String contextpath = request.getContextPath();
		for(int i = 0 ; menus != null && i < menus.size() ; i ++)
		{
			MenuItem menu = menus.getMenuItem(i);
			if (!menu.isUsed()) {
				continue;
			}
			if(menu instanceof Module)
			{
				MenuItemU menuItemU = new MenuItemU();
				menuItemU.setId(menu.getId());
				menuItemU.setName(menu.getName(request));
				menuItemU.setImageUrl(menu.getMouseclickimg(request));
				menuItemU.setType("module");
				permissionMenus.put(menu.getId(), menuItemU);
			}
			else
			{
				Item item = (Item)menu;
				
				String url = null;
				String area = item.getArea();
				if(area != null && area.equals("main"))
				{
					url = MenuHelper.getMainUrl(contextpath, item,
							(java.util.Map) null);
				}
				else
				{
					url = MenuHelper.getRealUrl(contextpath, Framework.getWorkspaceContent(item,accesscontroler),MenuHelper.menupath_menuid,item.getId());
				}
				MenuItemU menuItemU = new MenuItemU();
				menuItemU.setId(item.getId());
				menuItemU.setName(item.getName(request));
				menuItemU.setImageUrl(item.getMouseclickimg(request));
				menuItemU.setPathU(url);
				menuItemU.setType("item");
				menuItemU.setDesktop_height(item.getDesktop_height());
				menuItemU.setDesktop_width(item.getDesktop_width());
				permissionMenus.put(item.getId(), menuItemU);
			}
		}
	 
		 
	}
	
	public static Map<String,List<String>> getResourcePermissions(AccessControl accesscontroler,String resourceType) throws Exception
	{
		Map<String,List<String>> cmPermissions = new HashMap<String,List<String>>();
		List<Resource> cmresources =ConfigManager.getInstance().getResourceManager() !=null? ConfigManager.getInstance().getResourceManager().getResourcesByType(resourceType):null;//SQLExecutor.queryList(String.class, "select title from td_sm_res where restype_id=?", resourceType);
		if(cmresources == null)
		{
			cmresources = new ArrayList<Resource>();
		}
		
		ResourceManager resourceManager = new ResourceManager();
		ResourceInfo resourceInfo = resourceManager.getResourceInfoByType(resourceType);
		if(resourceInfo == null)
			return cmPermissions;
		OperationQueue operationQueue = resourceInfo.getOperationQueue();
		
		for(int i = 0; operationQueue != null && operationQueue.size() > 0 && i < cmresources.size(); i ++)
		{
			String resid = cmresources.get(i).getTitle();
			List<String> ops = new ArrayList<String>();
			for(int j = 0; j < operationQueue.size(); j ++)
			{
				Operation op = operationQueue.getOperation(j);
				if(accesscontroler.checkPermission(resid, op.getId(), resourceType))
				{
					ops.add(op.getId());
				}
			}
			if(ops.size()> 0)
				cmPermissions.put(resid, ops);
		}
		
		String globalid = resourceInfo.getGlobalresourceid();
		if(StringUtil.isNotEmpty(globalid))
		{
			operationQueue = resourceInfo.getGlobalOperationQueue();
			List<String> ops = new ArrayList<String>();
			for(int j = 0; operationQueue != null && operationQueue.size() > 0 &&j < operationQueue.size(); j ++)
			{
				Operation op = operationQueue.getOperation(j);
				if(accesscontroler.checkPermission(globalid, op.getId(), resourceType))
				{
					ops.add(op.getId());
				}
			}
			if(ops.size()> 0)
				cmPermissions.put(globalid, ops);
		}		
		
		return cmPermissions;
	}
	
	public static String getRequestParameter(String name)
	{
		return AccessControl.getAccessControl()._getRequestParameter(name);
	}
	
	private String _getRequestParameter(String name)
	{
		return request !=null?request.getParameter(name):null;
	}
	
	public static String[] getRequestParameters(String name)
	{
		return AccessControl.getAccessControl()._getRequestParameters(name);
	}
	
	private String[] _getRequestParameters(String name)
	{
		return request !=null?request.getParameterValues(name):null;
	}



	@Override
	public boolean isOrganizationManager(String orgId) {
		if(ConfigManager.getInstance().getPermissionModule() != null)
			return ConfigManager.getInstance().getPermissionModule().isOrganizationManager(this.getUserAccount(),orgId);
		return false;
	}



	@Override
	public boolean isSubOrgManager(String orgId) {
		if(ConfigManager.getInstance().getPermissionModule() != null)
			return ConfigManager.getInstance().getPermissionModule().isSubOrgManager(this.getUserAccount(),orgId);
		return false;
	}



	@Override
	public String getChargeOrgId() {
		if(ConfigManager.getInstance().getPermissionModule() != null)
			return ConfigManager.getInstance().getPermissionModule().getChargeOrgId(this.getUserAccount());
		return null;
	}
	
	public String getOrgLeader(String org) {
		if(ConfigManager.getInstance().getPermissionModule() != null)
			return ConfigManager.getInstance().getPermissionModule().getOrgLeader(org);
		return null;
	}
	
}
