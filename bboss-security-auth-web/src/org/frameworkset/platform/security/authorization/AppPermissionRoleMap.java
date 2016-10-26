//Source file: D:\\environment\\eclipse\\workspace\\cjxerpsecurity\\src\\com\\westerasoft\\common\\security\\websphere\\authorization\\impl\\AppPermissionRoleMap.java

package org.frameworkset.platform.security.authorization;

import org.frameworkset.platform.security.authorization.impl.PermissionRoleMap;

/**
 *
 * 获取相应资源许可角色；
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class AppPermissionRoleMap extends PermissionRoleMap {
//    private static TraceComponent tc;

 
//    static {
//        tc = Tr.register(AppPermissionRoleMap.class, null,
//                         "com.ibm.ejs.resources.security");
//    }
 
    /**
     * @since 2004.12.15
     */
    public AppPermissionRoleMap() {

    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @return Role[]
     */
    protected AuthRole[] getRequiredRoles(
                                      java.lang.String resource,
                                      java.lang.String action,
                                      String resourceType) throws SecurityException{

    	AuthRole[] asecurityroles = null;
        try {

        	asecurityroles = new AuthRole[2];
        	if(resourceType.equals("column"))
        	{
	        	AuthRole r = new AuthRole();
	        	r.setRoleId("1");
	        	r.setRoleName("manager");
	        	r.setRoleType(AuthRole.TYPE_ROLE);
	        	asecurityroles[0] = r;
	        	r = new AuthRole();
	        	r.setRoleId("2");
	        	r.setRoleName("zhangsan");
	        	r.setRoleType(AuthRole.TYPE_USER);
	        	asecurityroles[1] = r;
        	}
        	else
        	{
        		AuthRole r = new AuthRole();
	        	r.setRoleId("3");
	        	r.setRoleName("leader");
	        	r.setRoleType(AuthRole.TYPE_ROLE);
	        	asecurityroles[0] = r;
	        	r = new AuthRole();
	        	r.setRoleId("4");
	        	r.setRoleName("lisi");
	        	r.setRoleType(AuthRole.TYPE_USER);
	        	asecurityroles[1] = r;
        	}
        	if(asecurityroles != null && asecurityroles.length == 0)
        		asecurityroles = null;
        }
         
         catch (Exception e) {
        	 
        
        	throw new SecurityException(e);
        }
        return asecurityroles;
    }
    
    /**
     * 判断特定类型的资源是否授过任何权权限
     * true:标识已授过权限
     * false:标识没有受过权限
     * @param resourceType
     * @param resource
     * @return
     * @throws SecurityException
     * add by 20080721 gao.tang 方法抛出SecurityException异常
     */
    public boolean hasGrantedRoles(String resource,String resourceType) throws SecurityException
    {
    	boolean hasGranted = true;
		
    	return hasGranted;
    }

    /**
     * 判断资源有没有操作被授予过role角色（要匹配角色名称和角色类型）
     * add by 20080721 gao.tang 方法抛出SecurityException异常
     */
	public boolean hasGrantRole(AuthRole role, String resource, String resourceType) throws SecurityException
	{
		boolean hasGranted = true;
		
    	return hasGranted;
	}

}
