//Source file: D:\\environment\\eclipse\\workspace\\cjxerpsecurity\\src\\com\\westerasoft\\common\\security\\websphere\\authorization\\impl\\AppSecurityCollaborator.java

package org.frameworkset.platform.security.authorization.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.frameworkset.spi.BaseApplicationContext;

import org.frameworkset.platform.config.ConfigException;
import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.config.model.ResourceInfo;
import org.frameworkset.platform.resource.UNProtectedResource;
import org.frameworkset.platform.resource.UNProtectedResourceQueue;
import org.frameworkset.platform.security.authorization.AccessException;
import org.frameworkset.platform.security.authorization.AuthPrincipal;
import org.frameworkset.platform.security.authorization.AuthRole;
import org.frameworkset.platform.security.authorization.AuthUser;
import org.frameworkset.platform.security.authorization.AuthorizationTable;
import org.frameworkset.platform.security.context.AccessContext;
import org.frameworkset.platform.security.context.AppAccessContext;

/**
 * 
 * To change for your class or interface
 * 
 * @author biaoping.yin
 * @version 1.0
 */
public class AppSecurityCollaborator extends SecurityCollaborator
{
	private static Logger log = Logger.getLogger(AppSecurityCollaborator.class);

	private static AppSecurityCollaborator securityCollaboratorInstance;
	
	static 
	{
		AppSecurityCollaborator.getInstance();
		BaseApplicationContext.addShutdownHook(new Runnable(){

			@Override
			public void run() {
				destroy();
				
			}
			
		});
	}
	
	public void _destory()
	{
		super._destory();
	}
	static void destroy()
	{
		if(securityCollaboratorInstance != null)
		{
			securityCollaboratorInstance._destory();
			securityCollaboratorInstance = null;
		}
	}
	/**
	 * @since 2004.12.15
	 */
	private AppSecurityCollaborator()
	{
		super();

	}

	/**
	 * @return com.ibm.ws.security.core.EJSSecurityCollaborator
	 */
	public static AppSecurityCollaborator getInstance()
	{
		if (securityCollaboratorInstance == null)
		{
			synchronized(AppSecurityCollaborator.class)
			{
				securityCollaboratorInstance = new AppSecurityCollaborator();
				try
				{
					securityCollaboratorInstance.initialize();
				}
				catch (Exception ex)
				{
					log.error(ex.getMessage(), ex);
					// ex.printStackTrace();
				}
			}
		}
		return securityCollaboratorInstance;
	}
	
	

	/**
	 * 判断资源是否是未受保护的资源
	 * 
	 * @param arg0
	 * @return boolean
	 */
	public boolean isUnprotected(AccessContext accesscontext,
			String resourceName, String resourceType)
	{
		ResourceInfo resInfo = null;
		try
		{
			if (accesscontext != null)
				resInfo = ConfigManager.getInstance().getResourceInfo(
						accesscontext.getAppName(),
						accesscontext.getModuleName(), resourceType);
			else
				resInfo = ConfigManager.getInstance().getResourceInfoByType(
						resourceType);
		}
		catch (ConfigException e)
		{
			log.error(e);
			e.printStackTrace();
		}
		if (resInfo == null)
		{
			return false;
		}
		UNProtectedResourceQueue unProtectedResourceQueue = resInfo
				.getUNProtectedResourceQueue();

		for (int i = 0; i < unProtectedResourceQueue.size(); i++)
		{
			UNProtectedResource unProtectedResource = unProtectedResourceQueue
					.getUNProtectedResource(i);
			if ((unProtectedResource.getResourceID().equals("*") 
					|| unProtectedResource.getResourceID().equals(resourceName))
					&& unProtectedResource.getUnprotectedoperations().size() == 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断资源是否是未受保护的资源
	 * 
	 * @param arg0
	 * @return boolean
	 */
	public boolean isUnprotected(AccessContext accesscontext,
			String resourceName, String operation, String resourceType)
	{
		ResourceInfo resInfo = null;
		try
		{
			if (accesscontext != null)
				resInfo = ConfigManager.getInstance().getResourceInfo(
						accesscontext.getAppName(),
						accesscontext.getModuleName(), resourceType);
			else
				resInfo = ConfigManager.getInstance().getResourceInfoByType(
						resourceType);
		}
		catch (ConfigException e)
		{
			log.error(e);
			e.printStackTrace();
		}
		if (resInfo == null)
		{
			return false;
		}
		UNProtectedResourceQueue unProtectedResourceQueue = resInfo
				.getUNProtectedResourceQueue();

		for (int i = 0; i < unProtectedResourceQueue.size(); i++)
		{
			UNProtectedResource unProtectedResource = unProtectedResourceQueue
					.getUNProtectedResource(i);
			if ((unProtectedResource.getResourceID().equals("*") 
					|| unProtectedResource.getResourceID().equals(resourceName))
					&& unProtectedResource.getUNprotectedOperation(operation) != null)
			{
				return true;
			}
			else if ((unProtectedResource.getResourceID().equals("*") 
					|| unProtectedResource.getResourceID().equals(resourceName))
					&& unProtectedResource.getUnprotectedoperations().size() == 0)
				return true;
		}
		return false;
	}

	/**
	 * 判断资源是否是未受保护的资源
	 * 
	 * @param arg0
	 * @return boolean
	 */
	public boolean isUnprotected(String resourceName, String resourceType)
	{
		return isUnprotected((AccessContext) null, resourceName, resourceType);
	}

	/**
	 * 判断资源是否是未受保护的资源
	 * 
	 * @param arg0
	 * @return boolean
	 */
	public boolean isUnprotected(String resourceName, String operation,
			String resourceType)
	{
		return this.isUnprotected(null, resourceName, operation, resourceType);
	}

	/**
	 * Description:权限检测入口
	 * 
	 * @param resourceName
	 * @param actionName
	 * @return
	 * @see com.frameworkset.platform.security.authorization.impl.SecurityCollaborator#checkAccess(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean checkAccess(Principal principal, String resource,
			String action, String resourceType)
	{
		try
		{
			Map configParams = new HashMap();
			configParams.put("resourceType", resourceType);
			AccessContext context = new AppAccessContext(ConfigManager
					.getInstance().getAppName(), ConfigManager.getInstance()
					.getModuleName(), configParams);
			return checkAccess(context, principal, resource, action,
					resourceType);
		}
		catch (Exception e)
		{
			log.error("Check permission error:\r\n" + e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Description:权限检测入口
	 * 
	 * @param resourceName
	 * @param actionName
	 * @return
	 * @see com.frameworkset.platform.security.authorization.impl.SecurityCollaborator#checkAccess(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean checkAccess(AccessContext context, Principal principal,
			String resource, String action, String resourceType)
	{

		if (!SecurityCollaborator.securityEnabled
				|| isAdmin(context, principal))
		{
			return true;
		}
		if (principal == null)
			return false;

		try
		{
			performAuthorization(context, principal, resource, action,
					resourceType);
		}
		catch (AccessException e1)
		{
			// log.error(e1);
			return false;
		}

		return true;
	}

	/**
	 * isAdmin
	 * 
	 * @return boolean
	 */
	public boolean isAdmin(Principal principal)
	{
		return isAdmin(new AppAccessContext(ConfigManager.getInstance()
				.getAppName(), ConfigManager.getInstance().getModuleName()),
				principal);

	}

	public static void main(String[] args)
	{

		AppSecurityCollaborator.getInstance().isAdmin(
				new AuthPrincipal("admin", null, "console"));
	}

	public String getAdministratorRoleName()
	{
		return super.getAdministratorRoleName(new AppAccessContext(
				ConfigManager.getInstance().getAppName(), ConfigManager
						.getInstance().getModuleName()));
	}
	
	public String getEveryonegrantedRoleName() {
		return super.getEveryonegrantedRoleName(new AppAccessContext(
				ConfigManager.getInstance().getAppName(), ConfigManager
						.getInstance().getModuleName()));
	}

	/**
	 * 判定给定的资源没有授予任何的角色时， 是否可以访问， 缺省为false， 具体的应用可以根据不同的要求决定是否可以访问
	 * 
	 * @return boolean
	 */
	public boolean allowIfNoRequiredRoles(String resourceType)
	{
		return appAccessManager.allowIfNoRequiredRoles(resourceType);
	}
	
	

	/**
	 * 判断给定的资源操作是否是除超级管理员其他人都不能访问的资源操作
	 * 
	 * @param resource
	 * @param operation
	 * @param resourceType
	 * @return
	 */
	public boolean isExcluded(String resourceId, String operation,
			String resourceType)
	{

		return appAccessManager.isExcluded(null, resourceId, operation,
				resourceType);
	}

	/**
	 * 判断给定的资源是否是除超级管理员其他人都不能访问的资源
	 * 
	 * @param resource
	 * 
	 * @param resourceType
	 * @return
	 */
	public boolean isExcluded(String resourceId, String resourceType)
	{

		return appAccessManager.isExcluded(new AppAccessContext(ConfigManager
				.getInstance().getAppName(), ConfigManager.getInstance()
				.getModuleName()), resourceId, resourceType);
	}

	public AuthRole[] getAllRoleofUser(String userAccount)
	{
		// TODO Auto-generated method stub

		return appAccessManager.getAllRoleofUser(new AppAccessContext(
				ConfigManager.getInstance().getAppName(), ConfigManager
						.getInstance().getModuleName()), userAccount);
	}

	public boolean hasGrantedAnyRole(String resource, String resourceType)
	{
		return hasGrantedAnyRole(new AppAccessContext(ConfigManager
						.getInstance().getAppName(), ConfigManager
						.getInstance().getModuleName()), resource, resourceType);
	}
	
	public boolean hasGrantedRole(String role,String roleType,String resource, String resourceType)
	{
		AuthRole role_a = new AuthRole();
//		role_a.setRoleName(role);
		role_a.setRoleId(role);
		role_a.setRoleType(roleType);
		return hasGrantedRole( role_a, resource,  resourceType);
	}
	
	public boolean hasGrantedRole(AuthRole role_a,String resource, String resourceType)
	{
		
		return super.grantedRole(role_a,new AppAccessContext(
				ConfigManager.getInstance().getAppName(), ConfigManager
						.getInstance().getModuleName()),resource,resourceType);
	}
	

    public AuthUser[] getAllPermissionUsersOfResource(String resourceid,String operation,String resourceType)
    {	
    	return super.getAllPermissionUsersOfResource(new AppAccessContext(ConfigManager
				.getInstance().getAppName(), ConfigManager
				.getInstance().getModuleName()), resourceid,operation,resourceType);
    }
    

    /**
     * 清除用户和角色的缓冲关系
     */
	public void resetAuthCache()
	{
		AuthorizationTable pluggableAuthTable = appAccessManager.getAuthorizationTable(new AppAccessContext(ConfigManager
				.getInstance().getAppName(), ConfigManager
				.getInstance().getModuleName()));
		if(pluggableAuthTable != null)
			pluggableAuthTable.reset();
	}
	
	/**
     * 清除角色和资源操作的缓冲关系
     */
	public void resetPermissionCache()
	{
		PermissionRoleMap permissionRoleMap = getPermissionRoleMap(new AppAccessContext(ConfigManager
				.getInstance().getAppName(), ConfigManager
				.getInstance().getModuleName()));
		if(permissionRoleMap != null)
			permissionRoleMap.reset();
	}
	
	

}
