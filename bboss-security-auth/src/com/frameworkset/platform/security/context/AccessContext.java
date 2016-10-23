package com.frameworkset.platform.security.context;

import java.io.Serializable;

import com.frameworkset.platform.security.authorization.impl.PermissionRoleMap;

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
public interface AccessContext extends Serializable{
    public String getAppName();
    public String getModuleName();
    public String getConfigParam(String name);
    public void setPermissionRoleMap(PermissionRoleMap permissionRoleMap);  
    public PermissionRoleMap getPermissionRoleMap();  
    
}
