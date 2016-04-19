package org.frameworkset.security.session.impl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.frameworkset.security.session.InvalidateCallback;
import org.frameworkset.security.session.Session;
import org.frameworkset.security.session.SessionStore;
import org.frameworkset.security.session.SimpleHttpSession;

import com.frameworkset.util.SimpleStringUtil;

public class SimpleSessionImpl implements Session{
	private String appKey;
	private String id;
	private long creationTime;
	private long lastAccessedTime;
	private long maxInactiveInterval;
	private String referip;
	private boolean validate;
	private boolean dovalidate;
	private Boolean assertValidate = null;
	private transient SessionStore sessionStore;
	private transient Map<String,Object> attributes;
	protected transient Map<String,ModifyValue> modifyattributes;
	protected transient boolean islazy = false;
	private static final Object NULL = new Object();
	private String host ;
	private String requesturi;
	private String lastAccessedUrl;
	private boolean httpOnly;
	private boolean secure;
	private String lastAccessedHostIP;
	private transient InvalidateCallback invalidateCallback;
	public SimpleSessionImpl()
	{
		attributes = new HashMap<String,Object>();
		
	}
	private void assertSession(SimpleHttpSession session,String contextpath) 
	{
		
		if(assertValidate != null)
		{
			
		}
		else
		{
			synchronized(this)
			{
				if(assertValidate == null)
				{
					assertValidate = new Boolean(this.isValidate());
				}
			}
			
		}
		if(!assertValidate.booleanValue())
		{
			this.invalidate(session,contextpath);
//			throw new SessionInvalidateException("Session " +this.getId() + "已经失效!");
			throw new IllegalStateException("Session " +this.getId() + "已经失效!"); 
		}
			
	}
	public Object getCacheAttribute(String attribute)
	{
		Object value = this.attributes.get(attribute);
		if(value == NULL)
			return null;
		else
			return value;
	}
	@Override
	public Object getAttribute(SimpleHttpSession session,String attribute,String contextpath) {
		assertSession(session,contextpath) ;
		
		Object value = this.attributes.get(attribute);
		if(value == null)
		{
			value = sessionStore.getAttribute(appKey, contextpath,id,attribute);
			if(value != null)
			{
				this.attributes.put(attribute, value);
			}
			else
			{
				this.attributes.put(attribute, NULL);
			}
			return value;
		}
		else
		{
			if(value == NULL)
				return null;
			else
				return value;
		}
	}

	@Override
	public Enumeration getAttributeNames(SimpleHttpSession session,String contextpath) {
		assertSession(  session,contextpath) ;
		
		return sessionStore.getAttributeNames(appKey, contextpath,id);
	}

	@Override
	public long getCreationTime() {
//		assertSession() ;
		return creationTime;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void touch(SimpleHttpSession session,String lastAccessedUrl,String contextpath,int MaxInactiveInterval) {
		assertSession(  session,contextpath) ;
		lastAccessedTime = System.currentTimeMillis();
		
		if(!this.islazy)
		{
			sessionStore.updateLastAccessedTime(appKey,id,lastAccessedTime, lastAccessedUrl,MaxInactiveInterval);
		}
		else
		{
			sessionStore.expired(appKey,id,MaxInactiveInterval);
			modifyAttribute("lastAccessedTime",lastAccessedTime,ModifyValue.type_base,ModifyValue.type_add);
			modifyAttribute("lastAccessedUrl",lastAccessedUrl,ModifyValue.type_base,ModifyValue.type_add);
			modifyAttribute("lastAccessedHostIP", SimpleStringUtil.getHostIP(),ModifyValue.type_base,ModifyValue.type_add);
			
			
		}
//		assertSession() ;
		
	}
	
	public void modifyAttribute(String name, Object value, int valuetype,int optype)
	{
		modifyattributes.put(name, new ModifyValue(name,   value,   valuetype,optype));
	}

	@Override
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return lastAccessedTime = sessionStore.getLastAccessedTime(appKey,id);
	}

	@Override
	public long getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return maxInactiveInterval;
	}

	@Override
	public Object getValue(SimpleHttpSession session,String attribute,String contextpath) {
		// TODO Auto-generated method stub
		return getAttribute( session, attribute, contextpath);
	}

	@Override
	public String[] getValueNames(SimpleHttpSession session,String contextpath) {
		assertSession(  session,contextpath) ;
		if(!this.isValidate())
		{
			return null;
		}
		return sessionStore.getValueNames(appKey, contextpath,id);
	}

	@Override
	public void invalidate(SimpleHttpSession session,String contextpath) {
		
		if(!dovalidate)
		{
			this.dovalidate = true;
			if(validate)
			{
				try
				{
					sessionStore.invalidate(session,appKey, contextpath,id);
					this.validate =false;
					this.attributes.clear();
					if(this.modifyattributes != null)
						modifyattributes.clear();
				}
				finally
				{
					invalidateCallback.invalidateCallback();
				}
			}
		}
		
		
	}

	@Override
	public boolean isNew() {
		
//		return this.creationTime == this.lastAccessedTime;
		return isNew;
	}
	protected boolean isNew = false;
	public void putNewStatus()
	{
		this.isNew = true;
	}
	@Override
	public void putValue(SimpleHttpSession session,String attribute, Object value,String contextpath) {
		setAttribute(  session, attribute,  value, contextpath) ;
		
	}

	@Override
	public void removeAttribute(SimpleHttpSession session,String attribute,String contextpath) {
		assertSession(  session, contextpath) ;
		if(!this.isValidate())
		{
			return ;
		}
		
		sessionStore.removeAttribute(session,appKey, contextpath,id,attribute);
		
//		this.attributes.remove(attribute);
		//将属性设置为空避免重复从mongodb获取数据
		this.attributes.put(attribute, NULL);
		
	}

	@Override
	public void removeValue(SimpleHttpSession session,String attribute,String contextpath) {
		removeAttribute(  session, attribute, contextpath);
		
	}

	@Override
	public void setAttribute(SimpleHttpSession session,String attribute, Object value,String contextpath) {
		assertSession(  session, contextpath) ;
		this.attributes.put(attribute, value);
		sessionStore.addAttribute(session,appKey, contextpath,id,attribute,value);
		
	}

	@Override
	public void setMaxInactiveInterval(SimpleHttpSession session,long maxInactiveInterval,String contextpath) {
		this.maxInactiveInterval = maxInactiveInterval;
		
	}
	
	@Override
	public void setMaxInactiveInterval(SimpleHttpSession session,long maxInactiveInterval,boolean refreshstore,String contextpath) {
		this.maxInactiveInterval = maxInactiveInterval;
		if(refreshstore)
		{
			assertSession(  session, contextpath) ;
			sessionStore.setMaxInactiveInterval(session,appKey, id,maxInactiveInterval,  contextpath);
		}
			
		
			
		
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	

	public SessionStore _getSessionStore() {
		return sessionStore;
	}

	public void _setSessionStore(SessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferip() {
		return referip;
	}

	public void setReferip(String referip) {
		this.referip = referip;
	}
	public boolean isValidate()
	{
		if(!validate)
			return false;
		return this.maxInactiveInterval <= 0 || (this.lastAccessedTime + this.maxInactiveInterval) > System.currentTimeMillis(); 
//		return validate;
	}
	
	public void setValidate(boolean validate)
	{
		this.validate = validate;
	}
	public Map<String, Object> getAttributes() {
//		assertSession() ;
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
//		assertSession() ;
		this.attributes = attributes;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getRequesturi() {
		return requesturi;
	}
	public void setRequesturi(String requesturi) {
		this.requesturi = requesturi;
	}
	public String getLastAccessedUrl() {
		return lastAccessedUrl;
	}
	public void setLastAccessedUrl(String lastAccessedUrl) {
		this.lastAccessedUrl = lastAccessedUrl;
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
	public String getLastAccessedHostIP() {
		return lastAccessedHostIP;
	}
	public void setLastAccessedHostIP(String lastAccessedHostIP) {
		this.lastAccessedHostIP = lastAccessedHostIP;
	}
	@Override
	public void initInvalidateCallback(InvalidateCallback invalidateCallback) {
		this.invalidateCallback = invalidateCallback;
		
	}
	
	@Override
	public void submit() {
		if(!this.isValidate())
			return;
		this.sessionStore.submit(this,this.appKey);
	}
	@Override
	public boolean islazy() {
		// TODO Auto-generated method stub
		return this.islazy;
	}
	public Map<String, ModifyValue> getModifyattributes() {
		return modifyattributes;
	}
	

}
