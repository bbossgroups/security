package org.frameworkset.security.session.statics;

import java.io.Serializable;
import java.util.Date;

import org.frameworkset.security.session.domain.CrossDomain;

public class SessionConfig implements Serializable {
	private long sessionTimeout;
	private String cookiename;
	private String serialType;
	private String sessionidGeneratorPlugin;
	private CrossDomain crossDomain;
	private boolean startLifeScan = false;
	private Date scanStartTime;
	private Date createTime;
	private Date updateTime;
	/**
	 * enableSessionIDFromParameter:支持以参数方式 传递sessionid控制开关
			true 启用,使用cookiename属性对应的值作为传递sessionid的参数名称
			false 关闭 默认值
			优先从cookie中获取sessionid，如果从cookie中没有获取sessionid到才需要从参数中获取sessionid	
			从参数传递的sessionid，必须采用以下方式对sessionid进行加密，才能传递：
			String sid = SessionUtil.getSessionManager().getSignSessionIDGenerator().sign("d4d6d67bb1e64bb39ee81434add36b59", true);
	 */
	private boolean enableSessionIDFromParameter = false;
	/**
	 * 将从请求参数中获取sessionid写回cookie控制开关，当enableSessionIDFromParameter为true时起作用<br>
			true 启用,使用cookiename属性将对应的值作为sessionid写回cookie<br>
			false 关闭 默认值<br>
		bboss采用增强的sessionid签名校验机制，避免客户端篡改sessionid，为了避免bboss内置的sessionid的签名算法被暴露，请修改默认的signKey
	 */
	private boolean rewriteSessionCookie;
	/**
	 * 是否对sessionid进行加密存入cookie
	 * true 加密
	 * false 不加密，默认值
	 */
	private boolean signSessionID = false;
	/**
	 * sessionid 签名key
	 */
	private String signKey = null;
	private boolean httpOnly;
	private boolean secure;
	private String domain;
	private String monitorAttributes;
	AttributeInfo[] extendAttributeInfos;
	/**
	 * 应用编码，如果没有指定appcode值默认为应用上下文
	 * appcode的作用：当所有的应用上下文为/时，用来区分后台统计的会话信息
	 */
	private String appcode;
	private long cookieLiveTime;
	private String sessionStore;
	 
	private String sessionListeners;
	
	private String monitorScope ; 
	
	/**
	 * session超时检测时间间隔，默认为-1，不检测
	 * 如果需要检测，那么只要令牌持续时间超过sessionscaninterval
	 * 对应的时间将会被清除
	 */
	private long sessionscaninterval = 60*60000;
	private boolean usewebsession = false; 
	private boolean lazystore;
	private boolean storeReadAttributes;
	public boolean isStoreReadAttributes() {
		return storeReadAttributes;
	}
	public void setStoreReadAttributes(boolean storeReadAttributes) {
		this.storeReadAttributes = storeReadAttributes;
	}
	public SessionConfig() {
		// TODO Auto-generated constructor stub
	}
	public long getSessionTimeout() {
		return sessionTimeout;
	}
	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	public String getCookiename() {
		return cookiename;
	}
	public void setCookiename(String cookiename) {
		this.cookiename = cookiename;
	}
 
	 
	public boolean isStartLifeScan() {
		return startLifeScan;
	}
	public void setStartLifeScan(boolean startLifeScan) {
		this.startLifeScan = startLifeScan;
	}
 
	public boolean isHttpOnly() {
		return httpOnly;
	}
	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}
	public boolean isSecure() {
		return secure;
	}
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getAppcode() {
		return appcode;
	}
	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}
	public long getCookieLiveTime() {
		return cookieLiveTime;
	}
	public void setCookieLiveTime(long cookieLiveTime) {
		this.cookieLiveTime = cookieLiveTime;
	}
	public String getSessionStore() {
		return sessionStore;
	}
	public void setSessionStore(String sessionStore) {
		this.sessionStore = sessionStore;
	}
	public String getSessionListeners() {
		return sessionListeners;
	}
	public void setSessionListeners(String sessionListeners) {
		this.sessionListeners = sessionListeners;
	}
	public long getSessionscaninterval() {
		return sessionscaninterval;
	}
	public void setSessionscaninterval(long sessionscaninterval) {
		this.sessionscaninterval = sessionscaninterval;
	}
	public boolean isUsewebsession() {
		return usewebsession;
	}
	public void setUsewebsession(boolean usewebsession) {
		this.usewebsession = usewebsession;
	}
	 
	public CrossDomain getCrossDomain() {
		return crossDomain;
	}
	public void setCrossDomain(CrossDomain crossDomain) {
		this.crossDomain = crossDomain;
	}
	public Date getScanStartTime() {
		return scanStartTime;
	}
	public void setScanStartTime(Date scanStartTime) {
		this.scanStartTime = scanStartTime;
	}
	public String getMonitorAttributes() {
		return monitorAttributes;
	}
	public void setMonitorAttributes(String monitorAttributes) {
		this.monitorAttributes = monitorAttributes;
	}
	
	public AttributeInfo[] getExtendAttributeInfos() {
		return extendAttributeInfos;
	}
	public void setExtendAttributeInfos(AttributeInfo[] extendAttributeInfos) {
		this.extendAttributeInfos = extendAttributeInfos;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getMonitorScope() {
		return monitorScope;
	}
	public void setMonitorScope(String monitorScope) {
		this.monitorScope = monitorScope;
	}
	public boolean isLazystore() {
		return lazystore;
	}
	public void setLazystore(boolean lazystore) {
		this.lazystore = lazystore;
	}
	public String getSerialType() {
		return serialType;
	}
	public void setSerialType(String serialType) {
		this.serialType = serialType;
	}
	public String getSessionidGeneratorPlugin() {
		return sessionidGeneratorPlugin;
	}
	public void setSessionidGeneratorPlugin(String sessionidGeneratorPlugin) {
		this.sessionidGeneratorPlugin = sessionidGeneratorPlugin;
	}
	public boolean isEnableSessionIDFromParameter() {
		return enableSessionIDFromParameter;
	}
	public void setEnableSessionIDFromParameter(boolean enableSessionIDFromParameter) {
		this.enableSessionIDFromParameter = enableSessionIDFromParameter;
	}
	public boolean isRewriteSessionCookie() {
		return rewriteSessionCookie;
	}
	public void setRewriteSessionCookie(boolean rewriteSessionCookie) {
		this.rewriteSessionCookie = rewriteSessionCookie;
	}
	public boolean isSignSessionID() {
		return signSessionID;
	}
	public void setSignSessionID(boolean signSessionID) {
		this.signSessionID = signSessionID;
	}
	public String getSignKey() {
		return signKey;
	}
	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

}
