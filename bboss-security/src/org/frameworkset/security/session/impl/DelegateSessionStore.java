/*
 *  Copyright 2008 bbossgroups
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.frameworkset.security.session.impl;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.frameworkset.security.session.InvalidateCallback;
import org.frameworkset.security.session.Session;
import org.frameworkset.security.session.SessionBasicInfo;
import org.frameworkset.security.session.SessionEvent;
import org.frameworkset.security.session.SessionStore;
import org.frameworkset.security.session.SimpleHttpSession;
import org.frameworkset.security.session.statics.SessionConfig;

/**
 * <p>Title: DelegateSessionStrore.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年5月6日
 * @author biaoping.yin
 * @version 3.8.0
 */
public class DelegateSessionStore implements SessionStore {
	private SessionStore sessionStore;
	public DelegateSessionStore(SessionStore sessionStore)
	{
		this.sessionStore = sessionStore;
	}
	@Override
	public void destory() {
		sessionStore.destory();

	}

	@Override
	public void livecheck() {
		sessionStore.livecheck();

	}
	public SimpleHttpSession createHttpSession(ServletContext servletContext,SessionBasicInfo sessionBasicInfo,String contextpath,InvalidateCallback invalidateCallback)
	{
		Session session = createSession(sessionBasicInfo);
		SimpleHttpSession httpsession = new HttpSessionImpl(session,servletContext,contextpath,invalidateCallback);
		if(SessionHelper.haveSessionListener())
		{
			SessionHelper.dispatchEvent(new SessionEventImpl(httpsession,SessionEvent.EventType_create));
		}
		return httpsession;
	}
	@Override
	public Session createSession(SessionBasicInfo sessionBasicInfo) {
		// TODO Auto-generated method stub
		Session session = sessionStore.createSession( sessionBasicInfo);
		if(session == null)
			return null;
		session._setSessionStore(this);
		session.putNewStatus();
		
		return session;
	}

	@Override
	public Object getAttribute(String appKey,String contextpath, String sessionID, String attribute) {
		// TODO Auto-generated method stub
		String _attribute = SessionHelper.wraperAttributeName(appKey,contextpath,  attribute);
		return sessionStore.getAttribute(appKey, contextpath, sessionID, _attribute);
	}

	@Override
	public Enumeration getAttributeNames(String appKey,String contextpath, String sessionID) {
		// TODO Auto-generated method stub
		return this.sessionStore.getAttributeNames(appKey, contextpath, sessionID);
	}

	@Override
	public void updateLastAccessedTime(String appKey, String sessionID,
			long lastAccessedTime,String lastAccessedUrl,int MaxInactiveInterval) {
		this.sessionStore.updateLastAccessedTime(appKey, sessionID, lastAccessedTime, lastAccessedUrl,  MaxInactiveInterval);

	}

	@Override
	public long getLastAccessedTime(String appKey, String sessionID) {
		// TODO Auto-generated method stub
		return this.sessionStore.getLastAccessedTime(appKey, sessionID);
	}

	@Override
	public String[] getValueNames(String appKey,String contextpath, String sessionID) {
		// TODO Auto-generated method stub
		return this.sessionStore.getValueNames(appKey, contextpath, sessionID);
	}

	@Override
	public void invalidate(SimpleHttpSession session,String appKey,String contextpath, String sessionID) {
		if(SessionHelper.haveSessionListener())
		{
			SessionHelper.dispatchEvent(new SessionEventImpl(session,SessionEvent.EventType_destroy));
		}
		this.sessionStore.invalidate(session,appKey, contextpath, sessionID);
		
		
		
		
	}

	@Override
	public boolean isNew(String appKey, String sessionID) {
		// TODO Auto-generated method stub
		return this.sessionStore.isNew(appKey, sessionID);
	}

	@Override
	public void removeAttribute(SimpleHttpSession session,String appKey,String contextpath, String sessionID,
			String attribute) {
		if(SessionHelper.haveSessionListener())
		{
			SessionHelper.dispatchEvent(new SessionEventImpl(session,SessionEvent.EventType_removeAttibute)
										.setAttributeName(attribute));
		}
		String _attribute = SessionHelper.wraperAttributeName(appKey,contextpath,  attribute);
		if(!session.islazy())
			this.sessionStore.removeAttribute(  session,appKey, contextpath, sessionID, _attribute);
		else
		{
			session.modifyAttribute(_attribute, null,  ModifyValue.type_data,ModifyValue.type_remove);
		}
		
		
		
	}

	@Override
	public void addAttribute(SimpleHttpSession session,String appKey,String contextpath, String sessionID, String attribute,
			Object value) {
		
		value = SessionHelper.serial(value);
		String _attribute = SessionHelper.wraperAttributeName(appKey,contextpath,  attribute);
		if(!session.islazy())
		{
			this.sessionStore.addAttribute(  session,appKey, contextpath, sessionID, _attribute, value);
		}
		else
		{
			session.modifyAttribute(_attribute, value, ModifyValue.type_data, ModifyValue.type_add);
		}
		
		if(SessionHelper.haveSessionListener())
		{
			SessionHelper.dispatchEvent(new SessionEventImpl(session,SessionEvent.EventType_addAttibute)
										.setAttributeName(attribute));
		}
		
	}

	@Override
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionStore.setSessionManager(sessionManager);
		

	}

	@Override
	public Session getSession(String appKey,String contextpath, String sessionid) {
		// TODO Auto-generated method stub
		Session session = this.sessionStore.getSession(appKey, contextpath, sessionid);
		if(session != null)
			session._setSessionStore(this);
		return session;
	}
	@Override
	public void setMaxInactiveInterval(SimpleHttpSession session, String appKey, String id, long maxInactiveInterval,
			String contextpath) {
		sessionStore.setMaxInactiveInterval(session, appKey, id, maxInactiveInterval, contextpath);
		
	}
	@Override
	public void saveSessionConfig(SessionConfig config) {
		sessionStore.saveSessionConfig(config);
		
	}
	@Override
	public SessionConfig getSessionConfig(String appkey) {
		// TODO Auto-generated method stub
		return sessionStore.getSessionConfig(appkey);
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.sessionStore.getName();
	}
	@Override
	public void submit(Session session, String appkey) {
		sessionStore.submit(session, appkey);
	}
	@Override
	public boolean uselazystore() {
		return sessionStore.uselazystore();
	}
	@Override
	public Long expired(String appkey, String sessionid, int timeout) {
		// TODO Auto-generated method stub
		return sessionStore.expired(appkey, sessionid, timeout);
	}
	
	

}
