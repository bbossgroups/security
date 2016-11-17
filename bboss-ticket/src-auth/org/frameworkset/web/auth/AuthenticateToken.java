/**
 * 
 */
package org.frameworkset.web.auth;

import java.util.Map;

/**
 * @author yinbp
 *
 * @Date:2016-11-15 17:00:28
 */
public class AuthenticateToken implements java.io.Serializable{
	private String account;
	private String password;
	private String appcode;
	private String appsecret;
	private String sessionid;
	private String signature;
	
	private Map<String,Object> extendAttributes;
	/**
	 * 
	 */
	public AuthenticateToken() {
		
	}
	
	public Object getAttribute(String attributeName)
	{
		if(extendAttributes == null)
			return null;
		return this.extendAttributes.get(attributeName);
	}
	public String getSessionid() {
		return sessionid;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	 
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAppcode() {
		return appcode;
	}
	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public Map<String, Object> getExtendAttributes() {
		return extendAttributes;
	}

	public void setExtendAttributes(Map<String, Object> extendAttributes) {
		this.extendAttributes = extendAttributes;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
