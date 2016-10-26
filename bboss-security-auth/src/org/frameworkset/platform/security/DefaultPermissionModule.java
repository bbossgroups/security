package org.frameworkset.platform.security;

import java.security.Principal;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.security.authorization.AccessException;

public class DefaultPermissionModule implements PermissionModule {

	public DefaultPermissionModule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkPermission(String userAccount, String resourceID, String action, String resourceType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkPermission(Principal principal, String resourceID, String action, String resourceType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOrgManager(String userAccount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserIDByUserAccount(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOrganizationManager(String userAccount, String orgId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubOrgManager(String userAccount, String orgId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getChargeOrgId(String userAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOrgLeader(String org) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUserPasswordDualTimeByUserAccount(String userAccount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Date getPasswordExpiredTimeByUserAccount(String userAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String isSpesialUser(String ip) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserPassword(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validatecode(HttpServletRequest request) throws AccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map getUserNameAndPasswordByWorknumber(String worknumber) {
		// TODO Auto-generated method stub
		return null;
	}

}
