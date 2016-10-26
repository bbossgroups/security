//Source file: D:\\environment\\eclipse\\workspace\\cjxerpsecurity\\src\\com\\westerasoft\\common\\security\\websphere\\authorization\\impl\\BaseAccessManager.java

package org.frameworkset.platform.security.authorization.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.config.model.ResourceInfo;
import org.frameworkset.platform.resource.ResourceManager;
import org.frameworkset.platform.security.authorization.AccessException;
import org.frameworkset.platform.security.authorization.AccessManager;
import org.frameworkset.platform.security.authorization.AuthRole;
import org.frameworkset.platform.security.authorization.AuthUser;
import org.frameworkset.platform.security.authorization.AuthorizationTable;
import org.frameworkset.platform.security.context.AccessContext;


/**
 *
 * To change for your class or interface
 *
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class BaseAccessManager implements AccessManager {

    /**
     * 变量pluggableAuthTable和pluggableAuthzTableName为了与websphere4.0兼容而定义
     */
    
	public void destory()
	{
		if(pluggableAuthTableIdx != null)
		{
			Iterator<Map.Entry<String,AuthorizationTable>> it = this.pluggableAuthTableIdx.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<String,AuthorizationTable> entry = it.next();
				entry.getValue().destroy();
				
			}
			pluggableAuthTableIdx.clear();
			pluggableAuthTableIdx = null;
		}
	}
    private Map<String,AuthorizationTable> pluggableAuthTableIdx;
   
    private static final Logger log = Logger.getLogger(BaseAccessManager.class);

    /**
     * @since 2004.12.15
     */
    public BaseAccessManager() {
        pluggableAuthTableIdx = new HashMap<String,AuthorizationTable>();
//        pluggableAuthTable = ConfigManager.getInstance().getAuthorTable();
//        
//        pluggableAuthTableIdx.put(ConfigManager.getInstance().getAppName()
//                                  + ":"
//                                  + ConfigManager.getInstance().getModuleName(),pluggableAuthTable);

    }

    /**
     *
     *  Description:判断pricipal是否具有对资源resource的访问权限，如果没有则抛出
     *  com.ibm.ws.security.core.AccessException
     * @param accesscontext
     * @param resource
     * @param method：参数暂不使用
     * @param principal
     * @throws com.ibm.ws.security.core.AccessException
     * @see com.ibm.ws.security.core.AccessManager#checkAccess(com.ibm.ws.security.core.AccessContext, java.lang.Object, java.lang.Object, java.security.Principal)
     */

    public void checkAccess(AccessContext accesscontext,
            Principal principal,
            java.lang.Object resource,
            java.lang.Object method,
            String resourceType) throws
            AccessException {
        String sresource = (String) resource;
        String action = (String) method;

        AuthRole requiredRoles[] =
                getRequiredRoles(accesscontext, sresource, action,resourceType);
        if (requiredRoles == null) {
            throw new AccessException("Null required roles");
        }
        //判断资源是否是受排斥的资源，如果是则拒绝访问
        if (isExcluded(accesscontext, sresource,method.toString(),resourceType)) {
            String s2 = "资源[resource[" + sresource + "],action["+ method + "],resourceType[" + resourceType +"] is excluded";
            throw new AccessException(s2);
        }
        //判断资源的相应操作所需要的角色是否为空角色，如果是空角色则拒绝访问
        if (requiredRoles == PermissionRoleMap.EMPTY_REQUIRED_ROLES) {
            throw new AccessException(
                    "资源[resource[" + sresource + "],action["+ method + "],resourceType[" + resourceType +"]，Empty required roles list defined in Authorization Constraint");
        }

        //如果所特定类型的资源的所有操作都没有赋给任何角色，则判断没有角色的资源是否可以访问
        if (requiredRoles == PermissionRoleMap.NO_REQUIRED_ROLES) {
            if (allowIfNoRequiredRoles(accesscontext)) {
                return;
            } else {
                throw new AccessException("No required roles defined for 资源[resource[" + sresource + "],action["+ method + "],resourceType[" + resourceType +"]");
            }
        }

        if (isEveryoneGranted(accesscontext, requiredRoles)) {
            return;
        }
        if (isGrantedAnyRole(accesscontext, requiredRoles, principal)) {
            return;
        }
        StringBuffer stringbuffer = new StringBuffer(128);
        stringbuffer.append(principal.toString());
        stringbuffer.append(" is not granted any of the required roles: ");
        int k = requiredRoles.length;
        int l;
        for (l = 0; l < k; l++) {
            stringbuffer.append(requiredRoles[l]).append(" ");
        }

        String roleNameStr = stringbuffer.toString();
        log.debug(roleNameStr);
        throw new AccessException(roleNameStr);

    }
    
 


    /**
     * @param arg0 访问上下文
     * @param arg1
     * @return boolean
     */
    public boolean isEveryoneGranted(
            AccessContext accesscontext,
            AuthRole[] asecurityroles) {
        HashMap hashmap;
        String requiredRoleNames[] = null;

        if (asecurityroles == PermissionRoleMap.EMPTY_REQUIRED_ROLES) {
            return false;
        }
        AuthorizationTable pluggableAuthTable =  getAuthorizationTable(accesscontext);
        if (pluggableAuthTable == null) {
            return false;
        }
        try {
            return pluggableAuthTable.isEveryoneGranted(asecurityroles);
        } catch (SecurityException e) {
           log.debug(e.getMessage());
           return false; 
        }
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @return boolean
     */
    public boolean isGrantedAnyRole(AccessContext accesscontext,
    		AuthRole requiredRoles[],
                                    Principal principal) {

//        String requiredRoleNames[] = null;



        if (requiredRoles == PermissionRoleMap.EMPTY_REQUIRED_ROLES) {

            return false;
        }
        AuthorizationTable pluggableAuthTable =  getAuthorizationTable(accesscontext);
        if (pluggableAuthTable == null) {

            return false;
        }


        try {
            //        if (requiredRoleNames != null && requiredRoles != null) {
//            requiredRoleNames = new String[requiredRoles.length];
//            for (int i1 = 0; i1 < requiredRoles.length; i1++) {
//                requiredRoleNames[i1] = requiredRoles[i1].getRoleName();
//            }
//
//        }
            return pluggableAuthTable.isGrantedAnyRole(requiredRoles,
                    principal);
        } catch (SecurityException e) {
            log.debug(e.getMessage());
            return false;
        }

    }
    /**
	 * 判定给定的资源没有授予任何的角色时， 是否可以访问， 缺省为false， 具体的应用可以根据不同的要求决定是否可以访问
	 * 
	 * @return boolean
	 */
	public boolean allowIfNoRequiredRoles(String resourceType)
	{
		return _allowIfNoRequiredRoles( resourceType);
	}
	/**
	 * 判定给定的资源没有授予任何的角色时， 是否可以访问， 缺省为false， 具体的应用可以根据不同的要求决定是否可以访问
	 * 
	 * @return boolean
	 */
	public static boolean _allowIfNoRequiredRoles(String resourceType)
	{
		// 判断资源类型本身是否允许
		ResourceManager s = new ResourceManager();

		ResourceInfo resourceInfo = s.getResourceInfoByType(resourceType);
		if (resourceInfo == null || resourceInfo.defaultAllowIfNoRequiredRole())
		{
			// 判断系统是否允许

			PermissionRoleMap permissionrolemap = ConfigManager.getInstance()
					.getPermissionRoleMap();// ((AppAccessContext)
											// accesscontext).
			// getPermissionRoleMap();
			return permissionrolemap.getPermissionRoleMapInfo()
					.isAllowIfNoRequiredRole();
		}
		else
		{
			return resourceInfo.allowIfNoRequiredRole();
		}
	}
	
	
    /**
     * @return boolean
     */
    public abstract boolean allowIfNoRequiredRoles(AccessContext accesscontext);

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @return com.ibm.etools.j2ee.common.SecurityRole[]
     */
    public abstract AuthRole[] getRequiredRoles(
            AccessContext accessContext,
            String resource,
            String action,String resourceType);
    
    
    /**
     * 判断角色是否包含在资源许可中
     * @param role
     * @param accessContext
     * @param resource
     * @param resourceType
     * @return
     */
    public boolean hasRolePermission(AuthRole role,
    		AccessContext accessContext,
            String resource,
            String resourceType)
    {
    	//update 20080721 gao.tang 如果抛出SecurityException异常返回false
    	//to fixed:20141217 yinbp
    	try {
			return accessContext.getPermissionRoleMap().hasGrantRole(role,resource,resourceType);
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		}
    } 



    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @return boolean
     */
    public boolean isGrantedRole(AccessContext accesscontext,
    		AuthRole securityrole,
                                    Principal principal) {
        try {
            AuthorizationTable pluggableAuthTable =  getAuthorizationTable(accesscontext);
            return pluggableAuthTable.isGrantedRole(securityrole,
                    principal);
        } catch (SecurityException securityproviderex) {
            log.error(securityproviderex);
            return false;
        }
    }


    public AuthorizationTable getAuthorizationTable(AccessContext accesscontext)
    {
        String key = accesscontext.getAppName() + ":" + accesscontext.getModuleName();
        AuthorizationTable pluggableAuthTable = (AuthorizationTable)pluggableAuthTableIdx.get(key);
        if(pluggableAuthTable == null)
        {
            pluggableAuthTable = ConfigManager.getInstance().getAuthorTable(accesscontext.getAppName(),accesscontext.getModuleName());            
            pluggableAuthTable.setAccessContext(accesscontext);
            
            pluggableAuthTableIdx.put(key,pluggableAuthTable);
        }
        return pluggableAuthTable;
    }

    public boolean isAdmin(AccessContext accesscontext,
                           Principal principal)
    {
        if(principal == null)
            return false;
        AuthorizationTable pluggableAuthTable = getAuthorizationTable(accesscontext);
        boolean isadmin = pluggableAuthTable.isAdmin(principal);
//        if(isadmin)
//        log.info(principal + " is administrator:" + isadmin);
        return isadmin;
    }
    /**
     * 判断给定的资源操作是否是除超级管理员其他人都不能访问的资源操作
     * @param accesscontext
     * @param resource
     * @param operation
     * @param resourceType
     * @return
     */
    public boolean isExcluded(AccessContext accesscontext,
    						 String resource,
    						 String operation,
    						 String resourceType) 
    {
    	return false;
    }
    
    /**
     * 判断给定的资源是否是除超级管理员其他人都不能访问的资源
     * @param accesscontext
     * @param resource
     
     * @param resourceType
     * @return
     */
    public boolean isExcluded(AccessContext accesscontext,
    						 String resource,
    						 
    						 String resourceType) 
    {
    	return false;
    }
    
    
    /**
     * 获取用户的所有角色数组
     * @param userAccount
     * @return
     */

    
    public AuthRole[] getAllRoleofUser(AccessContext accesscontext,String userAccount) 
    {
    	try
		{
			return getAuthorizationTable(accesscontext).getAllRoleOfPrincipal(userAccount);
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			return null;
		}
    }
    
    public AuthUser[] getAllPermissionUsersOfResource(AccessContext accesscontext,
    												  String resourceid,
    												  String operation,
    												  String resourceType) 
    {
    	try
		{
			return getAuthorizationTable(accesscontext).getAllPermissionPrincipalsOfResource(resourceid,
					  operation,
					  resourceType);
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public boolean hasGrantedAnyRole(AccessContext context,String resource,String resourceType)
    {
    	return context.getPermissionRoleMap().hasGrantedAnyRole(resource,resourceType);
    }
}
