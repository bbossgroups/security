//Source file: D:\\environment\\eclipse\\workspace\\cjxerpsecurity\\src\\com\\westerasoft\\common\\security\\websphere\\authorization\\impl\\SecurityCollaborator.java

package org.frameworkset.platform.security.authorization.impl;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.security.authorization.AccessException;
import org.frameworkset.platform.security.authorization.AccessManager;
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
public abstract class SecurityCollaborator{
    private static Logger log  = Logger.getLogger(SecurityCollaborator.class);
    protected static boolean securityEnabled;
    protected static AccessManager appAccessManager;
    protected PermissionTokenMap permissionTokenMap;
    protected Map<String,PermissionRoleMap> permissionMapIndex = new HashMap<String,PermissionRoleMap>();
    public void _destory()
    {
    	if(permissionTokenMap != null)
    	{
	    	permissionTokenMap.destory();
	    	permissionTokenMap = null;
    	}
    	if(appAccessManager != null)
    	{
    		appAccessManager.destory();
    		appAccessManager = null;
    	}
    	if(permissionMapIndex != null)
    	{
    		
    		
    			Iterator<Map.Entry<String,PermissionRoleMap>> it = this.permissionMapIndex.entrySet().iterator();
    			while(it.hasNext())
    			{
    				Map.Entry<String,PermissionRoleMap> entry = it.next();
    				entry.getValue().destroy();
    				
    			}
    			
    		permissionMapIndex.clear();
    		permissionMapIndex = null;
    	}
    	
    }

    /**
     * 定义未受保护的资源
     * 以下定义的时未受保护的ejb的资源，必要时换成未受保护的应用系统资源
     */
    protected static final String UNPROTECTED[][] = new String[][]{
                                                  {"RemoteSRP","orgunit"},
                                                  {"RemoteSRPHome","orgunit"},
                                                  {"SrdSrvltCtxHome","orgunit"},
                                                  {"SessionBMP","orgunit"},
                                                  {"UPManager","orgunit"},
                                                  {"UP_ReadOnly","orgunit"},
                                                  {"UP_ReadWrite","orgunit"}};







    /**
     * @throws java.lang.Exception
     */
    public  void initialize() throws java.lang.Exception {
        log.debug("Appsecuritycollaborator initializ");

        securityEnabled = ConfigManager.getInstance().securityEnabled();
        log.debug("securityEnabled: " + securityEnabled);
        try
        {
            appAccessManager = new AppAccessManager();
            permissionTokenMap = new PermissionTokenMap();
        }
        catch(Exception e)
        {
            log.error(e.getMessage(),e);
        }

        log.debug("appsecuritycollaborator initialized");
    }

    /**
     *
     * Description:
     * @param resource
     * @param action
     * @param serverSubject
     * @param callerSubject
     * @param securityappcookie
     * @return
     * @throws com.ibm.websphere.csi.CSIException
     * javax.security.auth.Subject[]
     */
    protected void performAuthorization(AccessContext context,
                                        Principal principal,
                                        String resource,
                                        String action,
                                        String resourceType) throws
            AccessException {
        boolean authorized = false;
        AccessException accessexception = null;
        try {
                //如果资源是未受保护的资源，则直接返回
            if (isUnprotected(context,resource,resourceType)) {
                return ;
            }

            checkAuthorization(context,principal,resource, action, resourceType);
            authorized = true;
        } catch (AccessException accessexception1) {

            accessexception = accessexception1;

        }
        if (!authorized) {
            String userName = principal.getName();
            throw new AccessException(
                "Security authz failed for userName[" + userName +"]:" +
                accessexception.getMessage());


//            throw new AccessException(
//                    "Security authz failed for userName[" + userName +"],Resource["+
//                     resource + "],Action[" + action + "]:" +
//                    accessexception.getMessage());
        }

    }

    /**
     * @param arg0
     * @return boolean
     */
    protected boolean isUnprotected(AccessContext context,String resourceName,String resourceType) {
        for (int i = 0; i < UNPROTECTED.length; i++) {
            if (UNPROTECTED[i][0].equals(resourceName) && UNPROTECTED[i][1].equals(resourceType)) {
                return true;
            }
        }
        return false;
    }



//    /**
//     * @param arg0
//     * @return boolean
//     */
//    public boolean isCallerInRole(java.lang.String roleName, Principal caller) {
//        throw new RuntimeException("not implemented");
//    }

    /**
     * 判断用户是否拥有给定的角色
     * @param accessContext AccessContext
     * @param roleName String
     * @param caller Principal
     * @return boolean
     */
    public boolean isCallerInRole(
            AccessContext accessContext,
            AuthRole roleName,
            Principal caller) {
        boolean flag = false;

        

        if (roleName == null) {
        } else {
        	String appName = accessContext.getAppName();
            String moduleName = accessContext.getModuleName();
            if(accessContext.getPermissionRoleMap() == null)
            {
	            PermissionRoleMap appPermissionrolemap = this.getPermissionRoleMap(accessContext);
	            AppAccessContext appAccesscontext =
	                    new AppAccessContext(
	                            appName,
	                            moduleName,
	                            appPermissionrolemap
	                    );
	
	            try {
	
	                flag =
	                    appAccessManager.isGrantedRole(
	                        appAccesscontext,
	                        roleName,
	                        caller);
	            } catch (AccessException ex) {
	                ex.printStackTrace();
	            }
            }
            else
            {
            	 try {
            			
 	                flag =
 	                    appAccessManager.isGrantedRole(
 	                    		accessContext,
 	                        roleName,
 	                        caller);
 	            } catch (AccessException ex) {
 	                ex.printStackTrace();
 	            }
            }
        }
        return flag;

    }

    protected PermissionRoleMap getPermissionRoleMap(AccessContext accessContext)
    {
        PermissionRoleMap permissionRoleMap = (PermissionRoleMap)this.permissionMapIndex.get(accessContext.getAppName() + ":" + accessContext.getModuleName());
        if(permissionRoleMap == null)
        {
            permissionRoleMap = ConfigManager.getInstance().
                                getPermissionRoleMap(
                                        accessContext.getAppName(),
                                        accessContext.getModuleName());
            permissionRoleMap.setAccessContext(accessContext);
            permissionRoleMap.init();
            permissionMapIndex.put(accessContext.getAppName() + ":" +
                                        accessContext.getModuleName() ,permissionRoleMap);
        }
        return permissionRoleMap;

    }



    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws com.ibm.ws.security.core.AccessException
     */
    protected void checkAuthorization(
            AccessContext appAccesscontext,
            Principal principal,
            String resource,
            String action,
            String resourceType) throws AccessException {

//        String appName = appAccesscontext.getAppName();
//        String module = context.getModuleName();
    	if(appAccesscontext.getPermissionRoleMap() == null)
    	{
	        PermissionRoleMap appPermissionrolemap = getPermissionRoleMap(appAccesscontext);
	        appAccesscontext.setPermissionRoleMap(appPermissionrolemap);
    	}
    	
//        AppAccessContext appAccesscontext = new AppAccessContext(appName,
//                module, appPermissionrolemap);
        try {
            appAccessManager.checkAccess(
                    appAccesscontext,
                    principal,
                    resource,
                    action,
                    resourceType);

        } catch (AccessException accessexception) {
            throw accessexception;
        }
    }

    /**
     * 判断用户是否是系统管理员
     * @param principal Principal
     * @return boolean
     */
    public boolean isAdmin(AccessContext context,Principal principal)
    {
    	if(appAccessManager == null)
    	{
    		return false;
//    		return appAccessManager.isAdmin(context,principal);
    	}
    	else
    	{
    		return appAccessManager.isAdmin(context,principal);
    	}
    }
    
    /**
     * 获取系统管理员角色名称
     * @param context
     * @return
     */
    protected String getAdministratorRoleName(AccessContext context)
    {
        return ConfigManager.getInstance().getAuthorTableInfo(context.getAppName(),context.getModuleName()).getAdminRole().getRoleName();
    }
    
    public boolean hasGrantedAnyRole(AccessContext context,String resource,String resourceType)
    {
    	if(context.getPermissionRoleMap() == null)
    	{
	    	PermissionRoleMap appPermissionrolemap = this.getPermissionRoleMap(context);
	    	context.setPermissionRoleMap(appPermissionrolemap);
	        
    	}
    	return appAccessManager.hasGrantedAnyRole(context,resource,resourceType);
    }
    
    public AuthUser[] getAllPermissionUsersOfResource(AccessContext context,String resourceid,String operation,String resourceType)
    {	
    	return appAccessManager.getAllPermissionUsersOfResource(context,resourceid,operation,resourceType);
    }

	public String getEveryonegrantedRoleName(AppAccessContext context)
	{	
		AuthRole role = ConfigManager.getInstance().getAuthorTableInfo(context.getAppName(),context.getModuleName()).getEveryonegrantedrole();
		if(role == null)
			return "";
		else
			return role.getRoleName();
		
	}
	
	public boolean grantedRole(AuthRole role,
    		AccessContext accessContext,
            String resource,
            String resourceType)
    {
		if(accessContext.getPermissionRoleMap() == null)
    	{
	    	PermissionRoleMap appPermissionrolemap = this.getPermissionRoleMap(accessContext);
	    	accessContext.setPermissionRoleMap(appPermissionrolemap);
	        
    	}
    	return this.appAccessManager.hasRolePermission(role,accessContext,resource,resourceType);
    }

	public PermissionTokenMap getPermissionTokenMap() {
		return permissionTokenMap;
	}
}
