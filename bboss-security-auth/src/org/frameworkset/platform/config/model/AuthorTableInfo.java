package org.frameworkset.platform.config.model;

import org.apache.log4j.Logger;
import org.frameworkset.platform.security.PermissionModule;
import org.frameworkset.platform.security.authorization.AuthRole;
import org.frameworkset.platform.security.authorization.impl.BaseAuthorizationTable;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.web.servlet.support.WebApplicationContextUtils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class AuthorTableInfo implements java.io.Serializable {
    private static Logger log = Logger.getLogger(AuthorTableInfo.class);
    private String moduleName;
    private String authorizetableClass ;
    private boolean cachable ;
    private ApplicationInfo applicationInfo;
    private BaseAuthorizationTable authorizationTable;
    private String cacheType;
    private boolean defaultable = false;
    private String providerType;
    private AuthRole adminRole;
    /**
     * 定义每个人都授予的角色
     */
    private AuthRole everyonegrantedrole; 
    public static void main(String[] args) {
        AuthorTableInfo authortableinfo = new AuthorTableInfo();
    }

    public String getAuthorizetableClass() {
        return authorizetableClass;
    }

    public boolean isCachable() {
        return cachable;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setAuthorizetableClass(String authorizetableClass) {
        this.authorizetableClass = authorizetableClass;
    }

    public void setCachable(boolean cachable) {
        this.cachable = cachable;
    }

    /**
     * setApplicationInfo
     *
     * @param ApplicationInfo ApplicationInfo
     */
    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public BaseAuthorizationTable getAuthorizationTable()
    {
        if(authorizationTable == null)
        {

            try {
            	if(!authorizetableClass.startsWith("mvc:"))
	        	{
            		 authorizationTable = (BaseAuthorizationTable) Class.forName(this.
                             authorizetableClass).newInstance();
					
	        	}
	        	else
	        	{
	        		BaseApplicationContext ioc = WebApplicationContextUtils.getWebApplicationContext();
	        		authorizationTable = ioc.getTBeanObject(authorizetableClass.substring(4), BaseAuthorizationTable.class);
	        	}
//                authorizationTable = (BaseAuthorizationTable) Class.forName(this.
//                        authorizetableClass).newInstance();
                authorizationTable.setAuthorTableInfo(this);
                
            } catch (ClassNotFoundException ex) {
                log.error("getAuthorizationTable",ex);
            } catch (IllegalAccessException ex) {
                log.error("getAuthorizationTable",ex);
            } catch (InstantiationException ex) {
            	 log.error("getAuthorizationTable",ex);
            }
        }
        return authorizationTable;
    }

    public String getCacheType() {
        return cacheType;
    }

    public String getModuleName() {
        return moduleName;
    }

    public boolean isDefaultable() {
        return defaultable;
    }

    public String getProviderType() {
        return providerType;
    }

    public AuthRole getAdminRole() {
        return adminRole;
    }

    /**
     * setCacheType
     *
     * @param cacheType String
     */
    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * setDefaultable
     *
     * @param defaultable boolean
     */
    public void setDefaultable(boolean defaultable) {
        this.defaultable = defaultable;
    }

    /**
     * setProviderType
     *
     * @param providerType String
     */
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    /**
     * setAdminrole
     *
     * @param adminRole String
     */
    public void setAdminRole(String adminRole) {
    	AuthRole role = new AuthRole();
    	role.setRoleName(adminRole);
    	role.setRoleType(AuthRole.TYPE_ROLE);
        this.adminRole = role;
    }
    /**
     * @return Returns the everyonegrantedrole.
     */
    public AuthRole getEveryonegrantedrole() {
        return everyonegrantedrole;
    }
    /**
     * @param everyonegrantedrole The everyonegrantedrole to set.
     */
    public void setEveryonegrantedrole(String everyonegrantedrole) {
    	AuthRole role = new AuthRole();
    	role.setRoleName(everyonegrantedrole);
    	role.setRoleType(AuthRole.TYPE_ROLE);
        this.everyonegrantedrole = role;
    }
}
