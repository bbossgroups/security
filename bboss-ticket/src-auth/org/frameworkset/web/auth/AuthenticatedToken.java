/**
 * 
 */
package org.frameworkset.web.auth;

import java.util.Date;
import java.util.Map;

/**
 * @author yinbp
 *
 * @Date:2016-11-15 17:00:28
 */
public class AuthenticatedToken implements java.io.Serializable{
	private String subject;
	private String cnname;
	private String issuer;
	private String audience;
	private Date expiration;
	private Date notBefore;
	private String sessionid;
	private Date issuedAt;
	private String appcode;
	private String authenticatecode;
	/**
	 * 是否从记住口令登陆
	 */
	private boolean fromremember;
	/**
	 * 是否从手机登陆
	 */
	private boolean frommobile;
	 
	
	private Map<String,Object> extendAttributes;
	/**
	 * 
	 */
	public AuthenticatedToken() {
		
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getAudience() {
		return audience;
	}
	public void setAudience(String audience) {
		this.audience = audience;
	}
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public Date getNotBefore() {
		return notBefore;
	}
	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}
	public Date getIssuedAt() {
		return issuedAt;
	}
	public void setIssuedAt(Date issuedAt) {
		this.issuedAt = issuedAt;
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
	 
	public String getAppcode() {
		return appcode;
	}
	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}
	public Map<String, Object> getExtendAttributes() {
		return extendAttributes;
	}
	public void setExtendAttributes(Map<String, Object> extendAttributes) {
		this.extendAttributes = extendAttributes;
	}
	public boolean isFromremember() {
		return fromremember;
	}
	public void setFromremember(boolean fromremember) {
		this.fromremember = fromremember;
	}
	public boolean isFrommobile() {
		return frommobile;
	}
	public void setFrommobile(boolean frommobile) {
		this.frommobile = frommobile;
	}
	public String getCnname() {
		return cnname;
	}
	public void setCnname(String cnname) {
		this.cnname = cnname;
	}
	public String getAuthenticatecode() {
		return authenticatecode;
	}
	public void setAuthenticatecode(String authenticatecode) {
		this.authenticatecode = authenticatecode;
	}
	 
}
