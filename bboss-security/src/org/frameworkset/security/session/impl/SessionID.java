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
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return sessionId.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj instanceof SessionID)
		{
			return this.sessionId.equals(((SessionID)obj).getSessionId());
		}
		else
		{
			return this.sessionId.equals((String)obj);
		}
		 
	}

}
