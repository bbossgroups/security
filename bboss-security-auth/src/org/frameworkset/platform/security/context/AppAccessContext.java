//Source file: D:\\environment\\eclipse\\workspace\\cjxerpsecurity\\src\\com\\westerasoft\\common\\security\\websphere\\authorization\\impl\\AppAccessContext.java

package org.frameworkset.platform.security.context;

import java.util.Map;

import org.frameworkset.platform.security.authorization.impl.PermissionRoleMap;

//import com.ibm.ejs.ras.Tr;
//import com.ibm.ejs.ras.TraceComponent;

/**
 *
 * To change for your class or interface
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class AppAccessContext extends BaseAccessContext {
    PermissionRoleMap permissionRoleMap;
     
 
    public AppAccessContext(
            String appName,
            String module,
            PermissionRoleMap permissionrolemap,Map configParams) {

        super(appName, module,configParams);
        permissionRoleMap = permissionrolemap;
    }
    
    public AppAccessContext(
            String appName,
            String module,
            PermissionRoleMap permissionrolemap) {

        super(appName, module,null);
        permissionRoleMap = permissionrolemap;
    }
    
    public AppAccessContext(
            String appName,
            String module
            ) {
        super(appName, module,null);
    }
    public AppAccessContext(
            String appName,
            String module,Map configParams
            ) {
        super(appName, module,configParams);
    }


    public PermissionRoleMap getPermissionRoleMap() {
        return permissionRoleMap;
    }

	public void setPermissionRoleMap(PermissionRoleMap permissionRoleMap) {
		this.permissionRoleMap = permissionRoleMap;
		
	}

	
}
