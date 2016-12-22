/**
 * 
 */
package org.frameworkset.security.session.impl;

/**
 * @author yinbp
 *
 * @Date:2016-12-22 12:49:29
 */
public class SessionID {
	private String sessionId;
	private String signSessionId;
	/**
	 * 
	 */
	public SessionID() {
		// TODO Auto-generated constructor stub
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSignSessionId() {
		return signSessionId == null?this.sessionId:this.signSessionId;
	}
	public void setSignSessionId(String signSessionId) {
		this.signSessionId = signSessionId;
	}

}
