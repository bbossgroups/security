package org.frameworkset.security.session;

public class SessionBasicInfo {
    private String appKey;
    private String referip;
    private String requesturi;
    private String lastAccessedHostIP;
    private String sessionid;
	public SessionBasicInfo() {
		// TODO Auto-generated constructor stub
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getReferip() {
		return referip;
	}
	public void setReferip(String referip) {
		this.referip = referip;
	}
	public String getRequesturi() {
		return requesturi;
	}
	public void setRequesturi(String requesturi) {
		this.requesturi = requesturi;
	}
	public String getLastAccessedHostIP() {
		return lastAccessedHostIP;
	}
	public void setLastAccessedHostIP(String lastAccessedHostIP) {
		this.lastAccessedHostIP = lastAccessedHostIP;
	}
	public String getSessionid() {
		return sessionid;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String toString(){
//		super.toString();
		StringBuilder ret = new StringBuilder();
		ret.append("[appKey:").append(appKey).append(",referip:").append(referip)
				.append(",requesturi:").append(requesturi)
				.append(",lastAccessedHostIP:").append(lastAccessedHostIP)
				.append(",sessionid:").append(sessionid);
		return ret.toString();
	}

}
