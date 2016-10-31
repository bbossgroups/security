package org.frameworkset.platform.config.model;

import org.frameworkset.platform.security.authorization.impl.BaseAuthorizationTable;
import org.frameworkset.platform.security.authorization.impl.PermissionRoleMap;
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
public class PermissionRoleMapInfo implements java.io.Serializable {
    private String moduleName;
    private String permissionRoleMapClass;
    private boolean cachable = true;
    private ApplicationInfo applicationInfo;
    private PermissionRoleMap permissionRoleMap;
    private String cacheType;
    private boolean allowIfNoRequiredRole = false;
    private boolean defaultable = false;
    private String providerType;
    public static void main(String[] args) {
        PermissionRoleMapInfo permissionrolemapinfo = new PermissionRoleMapInfo();
    }

    public boolean isCachable() {
        return cachable;
    }

    public String getPermissionRoleMapClass() {
        return permissionRoleMapClass;
    }


    public void setCachable(boolean cachable) {
        this.cachable = cachable;
    }

    public void setPermissionRoleMapClass(String permissionRoleMapClass) {
        this.permissionRoleMapClass = permissionRoleMapClass;
    }

    /**
     * setApplicationInfo
     *
     * @param ApplicationInfo ApplicationInfo
     */
    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public PermissionRoleMap getPermissionRoleMap()
    {
        if(permissionRoleMap == null)
        {
            try {
            	if(!permissionRoleMapClass.startsWith("mvc:"))
	        	{
            		 permissionRoleMap = (PermissionRoleMap) Class.forName(this.
                             permissionRoleMapClass).newInstance();
	        	}
	        	else
	        	{
	        		BaseApplicationContext ioc = WebApplicationContextUtils.getWebApplicationContext();
	        		permissionRoleMap = ioc.getTBeanObject(permissionRoleMapClass.substring(4), PermissionRoleMap.class);
	        	}
               
                permissionRoleMap.setPermissionRoleMapInfo(this);
                //permissionRoleMap.init();
            } catch (ClassNotFoundException ex) {
            } catch (IllegalAccessException ex) {
            } catch (InstantiationException ex) {
            }
        }
        return permissionRoleMap;
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

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public boolean isAllowIfNoRequiredRole() {
        return allowIfNoRequiredRole;
    }

    public String getProviderType() {
        return providerType;
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

    public void setAllowIfNoRequiredRole(boolean allowIfNoRequiredRole) {
        this.allowIfNoRequiredRole = allowIfNoRequiredRole;
    }

    /**
     * setProviderType
     *
     * @param providerType String
     */
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }
}
