package org.frameworkset.platform.security.authorization;

import java.security.Principal;

import org.frameworkset.platform.security.authorization.impl.SecurityException;
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
public interface AuthorizationTable {
    /**
     * isEveryoneGranted
     *
     * @param hashmap HashMap
     * @param requiredRoleNames AuthRole[]
     * @return boolean
     */
    public boolean isEveryoneGranted(AuthRole[] requiredRoles) throws
    SecurityException;

    /**
     * isGrantedAnyRole
     *
     * @param hashmap HashMap
     * @param requiredRoleNames String[]
     * @param wsprincipal AuthPrincipal
     * @return boolean
     */
    public boolean isGrantedAnyRole(AuthRole[] requiredRoles,
                                    Principal wsprincipal) throws
                                    SecurityException;

    /**
     * isGrantedRole
     *
     * @param context HashMap
     * @param roleName String
     * @param wsprincipal AuthPrincipal
     * @return boolean
     */
    public boolean isGrantedRole(AuthRole roleName,
                                 Principal wsprincipal) throws
                                 SecurityException;

    /**
     * isAdmin
     *
     * @param principal Principal
     * @return boolean
     */
    public boolean isAdmin(Principal principal);
    
    public void setAccessContext(AccessContext context); 

    //    /**
//     * 判断用户是否为系统管理员
//     * @param wsprincipal Principal
//     * @return boolean
//     */
//    public boolean isAdmin(Principal wsprincipal);
    public AuthRole[] getAllRoleOfPrincipal(String userName)
	throws SecurityException;
    
    
    /**
     * 获取资源操作许可的用户列表的抽象方法，
     * 并且根据系统配置对获取的结果进行缓冲
     * @param resourceid
     * @param operation
     * @param resourceType
     * @return
     * @throws SecurityException
     */
    public AuthUser[] getAllPermissionPrincipalsOfResource(String resourceid,
    													   String operation,
    													   String resourceType) 
    throws SecurityException;
    public void reset();
    public void destroy();
}
