package com.frameworkset.platform.security;

import java.security.Principal;

/**
 * 应用自定义的权限扩展
 * @author biaoping.yin
 *
 */
public interface PermissionModule {
	public boolean checkPermission(String userAccount,String resourceID, String action,
			String resourceType) ;
	public boolean checkPermission(Principal principal,String resourceID, String action,
			String resourceType) ;
	
	public boolean isOrgManager(String userAccount) ;
	
	public  String getUserIDByUserAccount(String name) ;
	public boolean isOrganizationManager(String orgId) ;
	public boolean isSubOrgManager(String orgId);
	public String getChargeOrgId();
	public String getOrgLeader(String org);
}
