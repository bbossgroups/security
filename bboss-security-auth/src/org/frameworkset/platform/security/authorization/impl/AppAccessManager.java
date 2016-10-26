//Source file: D:\\environment\\eclipse\\workspace\\cjxerpsecurity\\src\\com\\westerasoft\\common\\security\\websphere\\authorization\\impl\\AppAccessManager.java

package org.frameworkset.platform.security.authorization.impl;

import org.apache.log4j.Logger;

import org.frameworkset.platform.config.ConfigException;
import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.config.model.ResourceInfo;
import org.frameworkset.platform.resource.ExcludeResource;
import org.frameworkset.platform.resource.ExcludeResourceQueue;
import org.frameworkset.platform.resource.ResourceManager;
import org.frameworkset.platform.security.authorization.AuthRole;
import org.frameworkset.platform.security.context.AccessContext;
import org.frameworkset.platform.security.context.AppAccessContext;

//import com.ibm.ejs.ras.Tr;
//import com.ibm.ejs.ras.TraceComponent;
//import com.ibm.etools.ejb.ExcludeList;
//import com.ibm.ws.security.core.AccessContext;

/**
 * 
 * To change for your class or interface
 * 
 * @author biaoping.yin
 * @version 1.0
 */
public class AppAccessManager extends BaseAccessManager
{
	 private static Logger log = Logger.getLogger(ResourceManager.class);

	/** 
	 * @since 2004.12.15
	 */
	public AppAccessManager()
	{
		super();
	}

	/**
	 * 判定给定的资源没有授予任何的角色时， 是否可以访问， 缺省为false， 具体的应用可以根据不同的要求决定是否可以访问
	 * 
	 * @return boolean
	 */
	public boolean allowIfNoRequiredRoles(AccessContext accesscontext)
	{
		// 判断资源类型本身是否允许
		ResourceManager s = new ResourceManager();
		String resourceType = accesscontext.getConfigParam("resourceType");
		ResourceInfo resourceInfo = s.getResourceInfoByType(resourceType);
		if (resourceInfo == null || resourceInfo.defaultAllowIfNoRequiredRole())
		{
			// 判断系统是否允许
			PermissionRoleMap permissionrolemap = ((AppAccessContext) accesscontext)
					.getPermissionRoleMap();
			return permissionrolemap.getPermissionRoleMapInfo()
					.isAllowIfNoRequiredRole();
		}
		else
		{
			return resourceInfo.allowIfNoRequiredRole();
		}
	}

//	/**
//	 * 判定给定的资源没有授予任何的角色时， 是否可以访问， 缺省为false， 具体的应用可以根据不同的要求决定是否可以访问
//	 * 
//	 * @return boolean
//	 */
//	public boolean allowIfNoRequiredRoles(String resourceType)
//	{
//		// 判断资源类型本身是否允许
//		ResourceManager s = new ResourceManager();
//
//		ResourceInfo resourceInfo = s.getResourceInfoByType(resourceType);
//		if (resourceInfo == null || resourceInfo.defaultAllowIfNoRequiredRole())
//		{
//			// 判断系统是否允许
//
//			PermissionRoleMap permissionrolemap = ConfigManager.getInstance()
//					.getPermissionRoleMap();// ((AppAccessContext)
//											// accesscontext).
//			// getPermissionRoleMap();
//			return permissionrolemap.getPermissionRoleMapInfo()
//					.isAllowIfNoRequiredRole();
//		}
//		else
//		{
//			return resourceInfo.allowIfNoRequiredRole();
//		}
//	}
//	
//	

	/**
	 * Description:获取相应的应用的中访问资源resource所需要的权限
	 * 
	 * @param accesscontext
	 * @param resource
	 * @return
	 * @see com.frameworkset.platform.security.authorization.impl.BaseAccessManager#getRequiredRoles(com.ibm.ws.security.core.AccessContext,
	 *      com.frameworkset.platform.config.model.Resource)
	 */
	public AuthRole[] getRequiredRoles(AccessContext accesscontext,
			String resource, String action, String resourceType)
	{

		PermissionRoleMap permissionrolemap = ((AppAccessContext) accesscontext)
				.getPermissionRoleMap();
		AccessPermission appAccesspermission = new AccessPermission(resource,
				action, resourceType);
		// 获取资源所分派的角色
		// add by 20080721 添加SecurityException异常
		AuthRole asecurityrole[] = null;
		try {
			asecurityrole = permissionrolemap.getRequiredRoles(
					accesscontext, appAccesspermission);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return asecurityrole;

	}

	/**
	 * 判断给定的资源是否是排斥资源， 如果是返回true,否则返回false
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return boolean
	 */
	public boolean isExcluded(AccessContext accesscontext, String resource,
			String resourceType)
	{
		ResourceInfo resInfo = null;
		try
		{
			if(accesscontext != null)
				resInfo = ConfigManager.getInstance()
										        .getResourceInfo(accesscontext.getAppName(),
										        		accesscontext.getModuleName(),
										                         resourceType);
			else
				resInfo = ConfigManager.getInstance()
		        						.getResourceInfoByType(resourceType);
				
		}
		catch (ConfigException e)
		{
			log.error(e);
			e.printStackTrace();
		}
    	if(resInfo == null)
    	{
    		return false;
    	}
		ExcludeResourceQueue excludeResourceQueue = resInfo.getExcludeResources();

		for (int i = 0; i < excludeResourceQueue.size(); i++)
		{
			ExcludeResource excludeResource = excludeResourceQueue
					.getExcludeResource(i);
			if (excludeResource.getResourceID().equals(resource) && excludeResource.getExcludeoperations().size() == 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断给定的资源是否是排斥资源， 如果是返回true,否则返回false
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return boolean
	 */
	public boolean isExcluded(AccessContext accesscontext, String resource,String operation,
			String resourceType)
	{
		ResourceInfo resInfo = null;
		try
		{
			if(accesscontext != null)
				resInfo = ConfigManager.getInstance()
										        .getResourceInfo(accesscontext.getAppName(),
										        		accesscontext.getModuleName(),
										                         resourceType);
			else
				resInfo = ConfigManager.getInstance()
		        						.getResourceInfoByType(resourceType);
		}
		catch (ConfigException e)
		{
			log.error(e);
			e.printStackTrace();
		}
    	if(resInfo == null)
    	{
    		return false;
    	}
		ExcludeResourceQueue excludeResourceQueue = resInfo.getExcludeResources();

		for (int i = 0; i < excludeResourceQueue.size(); i++)
		{
			ExcludeResource excludeResource = excludeResourceQueue
					.getExcludeResource(i);
			if (excludeResource.getResourceID().equals(resource) && excludeResource.getExcludeOperation(operation) != null)
			{
				return true;
			}
			else if (excludeResource.getResourceID().equals(resource) && excludeResource.getExcludeoperations().size() == 0)
			{
				return true;
			}
			
		}
		return false;
	}


	
	
}
