//Source file: D:\\environment\\eclipse\\workspace\\cjxerpsecurity\\src\\com\\westerasoft\\common\\security\\websphere\\authorization\\impl\\AppAuthorizationTable.java

package org.frameworkset.platform.security.authorization;

import org.apache.log4j.Logger;
import org.frameworkset.platform.security.authorization.impl.BaseAuthorizationTable;
import org.frameworkset.spi.SPIException;

/**
 * 
 * 应用角色/用户/用户组关系表
 * 
 * @author biaoping.yin
 * @version 1.0
 */
public class AppAuthorizationTable extends BaseAuthorizationTable{
	private static final Logger log = Logger
			.getLogger(AppAuthorizationTable.class);


	public AuthRole[] getAllRoleOfPrincipal(String userName)
			throws SecurityException {
		try {
		 
			AuthRole[] roles = new AuthRole[2];
			if(userName.equals("zhangsan"))
        	{
	        	AuthRole r = new AuthRole();
	        	r.setRoleId("3");
	        	r.setRoleName("manager");
	        	r.setRoleType(AuthRole.TYPE_ROLE);
	        	roles[0] = r;
	        	r = new AuthRole();
	        	r.setRoleId("4");
	        	r.setRoleName("zhangsan");
	        	r.setRoleType(AuthRole.TYPE_USER);
	        	roles[1] = r;
        	}
			else if(userName.equals("admin"))
        	{
	        	AuthRole r = new AuthRole();
	        	r.setRoleId("1");
	        	r.setRoleName("administrator");
	        	r.setRoleType(AuthRole.TYPE_ROLE);
	        	roles[0] = r;
	        	r = new AuthRole();
	        	r.setRoleId("2");
	        	r.setRoleName("admin");
	        	r.setRoleType(AuthRole.TYPE_USER);
	        	roles[1] = r;
        	}
        	else
        	{
        		AuthRole r = new AuthRole();
	        	r.setRoleId("5");
	        	r.setRoleName("leader");
	        	r.setRoleType(AuthRole.TYPE_ROLE);
	        	roles[0] = r;
	        	r = new AuthRole();
	        	r.setRoleId("6");
	        	r.setRoleName("lisi");
	        	r.setRoleType(AuthRole.TYPE_USER);
	        	roles[1] = r;
        	}
			
			 
			return roles;

		} catch (SecurityException e) {
			 
			log.error("", e);
			 
			throw e;
		} catch (SPIException e) {
			throw new SecurityException("Get AllRoleOfPrincipal error:"
					+ e.getMessage());
		} 
	 
		catch(Exception e){
			log.error("", e);
			throw new SecurityException(e);
		}
		finally
		{
//			tm.releasenolog();
		}
	}

	public AuthUser[] getAllPermissionUsersOfResource(String resourceid, String operation, String resourceType) throws SecurityException {		
//		try {
//
//			RoleManager roleMgr = SecurityDatabase.getRoleManager(super
//					.getProviderType());
//
//			List list = roleMgr.getAllUserOfHasPermission(resourceid, operation, resourceType);
//			if(list == null || list.size() == 0)
//				return null;
//			AuthUser[] authUsers = new AuthUser[list.size()];
//			for(int i = 0; i < authUsers.length; i ++)
//			{
//				AuthUser authUser = new AuthUser();
//				User user = (User)list.get(i);
//				authUser.setUserAccount(user.getUserName());
//				authUser.setUserID(user.getUserId() + "");
//				authUser.setUserName(user.getUserRealname());
//				authUsers[i] = authUser;
//				
//				
//			}
//			return authUsers;
//			
//
//		} catch (Exception e) {
//			//add by 20080721 gao.tang 添加异常输出信息e.printStackTrace();
//			e.printStackTrace();
//			throw new SecurityException(e.getMessage());
//		}
		throw new java.lang.UnsupportedOperationException();


	}

}
