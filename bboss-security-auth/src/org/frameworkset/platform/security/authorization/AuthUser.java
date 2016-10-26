package org.frameworkset.platform.security.authorization;

import java.io.Serializable;

/**
 * 权限用户信息
 * @author Administrator
 *
 */
public class AuthUser implements Serializable{
	private String userName;
	private String userAccount;
	private String userID;
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
