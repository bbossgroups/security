package org.frameworkset.platform.security.authorization;

import java.io.Serializable;
import java.security.Principal;

import org.frameworkset.platform.security.context.AccessContext;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface AccessManager {
	public void destory();
    public void checkAccess(
            AccessContext accesscontext,
            java.security.Principal principal,
            java.lang.Object resource,
            java.lang.Object method,
            String resourceType) throws AccessException;

    /**
     * 判断accesscontext中用户principal是否被授予角色securityrole
     * @param accesscontext AccessContext
     * @param securityrole Role
     * @param principal Principal
     * @return boolean
     * @throws AccessException
     */
    public boolean isGrantedRole(AccessContext accesscontext, AuthRole securityrole,
                                 Principal principal) throws AccessException;

    ;
    /**
     * 判断上下文accesscontext中asecurityrole是否包含所有用户都拥有的角色
     * @param accesscontext AccessContext
     * @param asecurityrole Role[]
     * @return boolean
     * @throws AccessException
     */
    public boolean isEveryoneGranted(AccessContext accesscontext,
    		AuthRole[] asecurityroles) throws
            AccessException;

    /**
     * 判断用户是否是系统管理员
     * isAdmin
     *
     * @param principal Principal
     * @return boolean
     */
    public boolean isAdmin(AccessContext accesscontext,Principal principal);
    
    
    /**
     *  判定给定的资源没有授予任何的角色时，
     *  是否可以访问，
     *  缺省为false，
     *  具体的应用可以根据不同的要求决定是否可以访问
     * @return boolean
     */
    public boolean allowIfNoRequiredRoles(String resourceType) ;
    
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
    						 String resourceType) ;
    
    /**
     * 判断给定的资源是否是除超级管理员其他人都不能访问的资源
     * @param accesscontext
     * @param resource
     
     * @param resourceType
     * @return
     */
    public boolean isExcluded(AccessContext accesscontext,
    						 String resource,
    						 
    						 String resourceType) ;
    
    /**
     * 获取用户的所有角色数组
     * @param userAccount
     * @return
     */

    
    public AuthRole[] getAllRoleofUser(AccessContext accesscontext,String userAccount) ;
    
    public boolean hasGrantedAnyRole(AccessContext context,String resource,String resourceType);
    
    public boolean hasRolePermission(AuthRole role,
    		AccessContext accessContext,
            String resource,
            String resourceType);
    
    public AuthUser[] getAllPermissionUsersOfResource(AccessContext accesscontext,
			  String resourceid,
			  String operation,
			  String resourceType);
    public AuthorizationTable getAuthorizationTable(AccessContext accesscontext);
}
