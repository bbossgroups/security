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
package org.frameworkset.security.session;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.frameworkset.security.session.impl.SessionManager;
import org.frameworkset.security.session.statics.SessionConfig;

/**
 * <p>Title: SessionStore.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年4月15日
 * @author biaoping.yin
 * @version 3.8.0
 */
public interface SessionStore{

	void destory();
	String getName();
	Long expired(String appkey,String sessionid,int timeout);

	void livecheck();
	public SimpleHttpSession createHttpSession(ServletContext servletContext,SessionBasicInfo sessionBasicInfo,String contextpath,InvalidateCallback invalidateCallback);
//	Session createSession(String appKey,String referip,String reqesturi);
	Session createSession(SessionBasicInfo sessionBasicInfo);

	Object getAttribute(String appKey,String contextpath,String sessionID, String attribute);

	Enumeration getAttributeNames(String appKey,String contextpath,String sessionID);

	void updateLastAccessedTime(String appKey,String sessionID, long lastAccessedTime,String lastAccessedUrl,int MaxInactiveInterval);

	long getLastAccessedTime(String appKey,String sessionID);

	String[] getValueNames(String appKey,String contextpath,String sessionID);

	void invalidate(SimpleHttpSession session,String appKey,String contextpath,String sessionID);

	boolean isNew(String appKey,String sessionID);

	void removeAttribute(SimpleHttpSession session,String appKey,String contextpath,String sessionID, String attribute);

	void addAttribute(SimpleHttpSession session,String appKey,String contextpath,String sessionID, String attribute, Object value);

	void setSessionManager(SessionManager sessionManager);
	
	Session getSession(String appKey,String contextpath,String sessionid);

	void setMaxInactiveInterval(SimpleHttpSession session, String appKey, String id, long maxInactiveInterval,String contextpath);
	public void saveSessionConfig(SessionConfig config);
	public SessionConfig getSessionConfig(String appkey);
	void submit(Session session,String appkey);
	public boolean uselazystore();

}
