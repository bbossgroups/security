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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.frameworkset.security.session.Session;
import org.frameworkset.security.session.SessionBasicInfo;
import org.frameworkset.security.session.SessionBuilder;
import org.frameworkset.security.session.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frameworkset.util.StringUtil;

/**
 * <p>Title: SessionHttpServletRequestWrapper.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年4月30日
 * @author biaoping.yin
 * @version 3.8.0
 */
public class SessionHttpServletRequestWrapper extends HttpServletRequestWrapper implements SessionBuilder {
	private static Logger log = LoggerFactory.getLogger(SessionHttpServletRequestWrapper.class);
	protected SessionID sessionid;
	protected HttpSessionImpl session;
	protected HttpServletResponse response;
	protected ServletContext servletContext;	
	protected String appkey ;
	protected boolean usewebsession = true;
	protected boolean sessionidcookiewrited = false;
	protected boolean requestedSessionIdFromURL;
	protected String signParameterSessionID;
	public SessionHttpServletRequestWrapper(HttpServletRequest request,HttpServletResponse response,ServletContext servletContext) {
		super(request);
		try
		{
			SessionUtil.init(SessionUtil.getAppKeyFromRequest(this));
			if(SessionUtil.getSessionManager() != null)
			{
				
				usewebsession = SessionUtil.getSessionManager().usewebsession();
				if( !usewebsession)
				{
					boolean enableSessionIDFromParameter = SessionUtil.getSessionManager().enableSessionIDFromParameter();
					String cookieName = SessionUtil.getSessionManager().getCookiename();
					String sessionid = null;
					if( enableSessionIDFromParameter){							
						sessionid = request.getParameter(cookieName);
						if(sessionid != null ){
							initSessionIDFromParameter(sessionid,  cookieName,  request);
						}
						else
						{
							initSessionIDFromCookie( cookieName, request);
						}
					}
					else
					{
						initSessionIDFromCookie( cookieName, request);
					}
//					if(sessionid == null )
//					{
//						if( enableSessionIDFromParameter){							
//							sessionid = request.getParameter(cookieName);
//							if(sessionid != null ){
//								String signSessionid = sessionid;
//								sessionid = SessionUtil.getSessionManager().getSignSessionIDGenerator().design(signSessionid,true);
//								this.sessionid = new SessionID();
//								if(SessionUtil.getSessionManager().isSignSessionID())//只有在启用签名的情况下，才需要往cookie中存放加密的sessionid
//									this.sessionid.setSignSessionId(signSessionid);
//								else
//									this.sessionid.setSignSessionId(sessionid);
//								this.sessionid.setSessionId(sessionid);
//								if(SessionUtil.getSessionManager().rewriteSessionCookie())
//									SessionUtil.writeSessionIDCookies(this, response,cookieName,this.sessionid);
//							}
//						}
//							
//					}
//					else
//					{
//						String signSessionid = sessionid;
//						sessionid = SessionUtil.getSessionManager().getSignSessionIDGenerator().design(signSessionid,false);
//						this.sessionid = new SessionID();
//						this.sessionid.setSignSessionId(signSessionid);
//						this.sessionid.setSessionId(sessionid);
//					}
					appkey = SessionUtil.getAppKey(this);
				}
			}
		}
		catch(SessionException e){
			throw e;
		}
		catch(Throwable e)
		{
			//log.debug("Init bboss SessionHttpServletRequestWrapper failed:",e);
			throw new SessionException("Init bboss SessionHttpServletRequestWrapper failed:",e);
		}
		this.servletContext = servletContext;
		this.response = response;
		
	}
	
	protected void initSessionIDFromParameter(String sessionid,String cookieName,HttpServletRequest request){
		
			requestedSessionIdFromURL = true;
			String signSessionid = sessionid;
			sessionid = SessionUtil.getSessionManager().getSignSessionIDGenerator().design(signSessionid,true);
			this.sessionid = new SessionID();
			this.signParameterSessionID = signSessionid;
			if(SessionUtil.getSessionManager().isSignSessionID())//只有在启用签名的情况下，才需要往cookie中存放加密的sessionid
				this.sessionid.setSignSessionId(signSessionid);
			else
				this.sessionid.setSignSessionId(sessionid);
			this.sessionid.setSessionId(sessionid);
			if(SessionUtil.getSessionManager().rewriteSessionCookie())
				SessionUtil.writeSessionIDCookies(this, response,cookieName,this.sessionid);
		
	}
	
	protected void initSessionIDFromCookie(String cookieName,HttpServletRequest request){
		String sessionid = StringUtil.getCookieValue((HttpServletRequest)request, cookieName);
		if(sessionid != null){
			String signSessionid = sessionid;
			sessionid = SessionUtil.getSessionManager().getSignSessionIDGenerator().design(signSessionid,false);
			this.sessionid = new SessionID();
			this.sessionid.setSignSessionId(signSessionid);
			this.sessionid.setSessionId(sessionid);
		}
	}
	
	public String signParameterSessionID(SessionManager sessionManager,boolean createSessionIfNotExist){
		if(this.signParameterSessionID != null){
			return signParameterSessionID;
		}
		
		HttpSession session = getSession(createSessionIfNotExist);
		if(session != null)
			return signParameterSessionID = sessionManager.signParameterSessionID(session.getId());		
		return null;
	}

	@Override
	public HttpSession getSession() {
		 return getSession(true);
	}
	/**
	 * 清除session数据
	 * @param sessionid
	 */
	public void removeSession(String sessionid)
	{
		if( usewebsession)
			return;
		if(this.sessionid != null && this.sessionid.equals(sessionid))
		{
			 
			HttpSession session  = getSession(false);
			signParameterSessionID = null;
			if(session != null)
				session.invalidate();
		}
		else
		{
			HttpSession session = _getSession( sessionid);
			if(session != null)
				session.invalidate();
		}
	}

	
	protected String getRequestUrl()
	{
		StringBuilder basePath = new StringBuilder().append(getScheme()).append("://").append(getServerName());
		if(getServerPort() != 80)
			basePath.append(":").append(getServerPort() ) ;
		 
		basePath
			.append( this.getRequestURI());
		if(this.getQueryString() != null)
			basePath.append("?").append(this.getQueryString());
		return basePath.toString();
	}
	protected void createSession(SessionID sessionid)
	{
		SessionBasicInfo sessionBasicInfo = new SessionBasicInfo();
		sessionBasicInfo.setAppKey(appkey);
		sessionBasicInfo.setSessionid(sessionid.getSessionId());
		sessionBasicInfo.setReferip(StringUtil.getClientIP(this));
		sessionBasicInfo.setRequesturi(this.getRequestUrl());				
		this.session = (HttpSessionImpl) SessionUtil.createSession(sessionBasicInfo,this);				
		this.sessionid = sessionid;
		if(!sessionidcookiewrited)
		{
			SessionUtil.writeSessionIDCookies(this, response,SessionUtil.getSessionManager().getCookiename(),sessionid);
			sessionidcookiewrited = true;
		}
		
	}
	protected SessionID randomToken()
	{
//		String token = UUID.randomUUID().toString();
//		return token;
		return SessionUtil.getSessionManager().getSignSessionIDGenerator().generateID();
	}
	@Override
	public HttpSession getSession(boolean create) {
		if( usewebsession)
		{
			// TODO Auto-generated method stub
			return super.getSession(create);
		}
		if(sessionid == null)
		{
			if(create)
			{

//				String appkey = SessionUtil.getAppKey(this);
				createSession(this.randomToken());
				return this.session;
			}
			else
			{
				return null;
			}
		}
		else if(session != null)
		{
			return session;
		}
		else
		{
//			String appkey =  SessionUtil.getAppKey(this);

			Session session = SessionUtil.getSession(appkey,this.getContextPath(),sessionid.getSessionId());
			if(session == null)//session不存在，创建新的session
			{				
				if(create)
				{
					createSession(this.randomToken());
				}
			}
			else
			{
				this.session =  buildHttpSessionImpl(  session);
			}
			return this.session;
		}
		
		
	}
	
	public HttpSessionImpl buildHttpSessionImpl(Session session)
	{
		return new HttpSessionImpl(session,servletContext,this.getContextPath(),this);
	}
	
	
	protected HttpSession _getSession(String sessionid) {
		if( usewebsession)
		{
			return null;
		}
		HttpSessionImpl session = null;
		if(sessionid == null)
		{
			
			return session;
		}
		
		else
		{
//			String appkey =  SessionUtil.getAppKey(this);

			Session session_ = SessionUtil.getSession(appkey,this.getContextPath(),sessionid);
			if(session_ == null)//session不存在，创建新的session
			{				
				return null;
			}
			else
			{
				session =  new HttpSessionImpl(session_,servletContext,this.getContextPath(),null);
			}
			return session;
		}
		
		
	}

	public void touch() {
		if( usewebsession)
			return;
		if(this.sessionid != null )
		{
			if(session == null)
			{
//				String appkey =  SessionUtil.getAppKey(this);
				Session session_ = SessionUtil.getSession(appkey,this.getContextPath(), sessionid.getSessionId());
				if(session_ == null || !session_.isValidate())
				{
					this.sessionid = null;
					this.signParameterSessionID = null;
					return;
				}
				this.session =  buildHttpSessionImpl(  session_);
			}
			if(session != null && !session.isNew() )
			{
				session.touch(this.getRequestUrl());
			}
		}
		
	}
	

	@Override
	public String getRequestedSessionId() {
		if( usewebsession)
		{
			return super.getRequestedSessionId();
		}
		if(this.sessionid != null)
			return sessionid.getSessionId();
		HttpSession session = this.getSession(false);
		if(session == null)
			return null;
		else
			return session.getId();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		if( usewebsession)
		{
			return super.isRequestedSessionIdFromCookie();
		}
		return !requestedSessionIdFromURL ;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		if( usewebsession)
		{
			return super.isRequestedSessionIdFromURL();
		}
		return requestedSessionIdFromURL;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		if( usewebsession)
		{
			return super.isRequestedSessionIdFromUrl();
		}
		return requestedSessionIdFromURL;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		if( usewebsession)
		{
			return super.isRequestedSessionIdValid();
		}
		HttpSessionImpl session = (HttpSessionImpl)this.getSession(false);
		if(session == null)
			return false;
		else
			return session.getInnerSession().isValidate();
	}

	@Override
	public void invalidateCallback() {
		this.session = null;
		this.sessionid = null;
		sessionidcookiewrited = false;
		this.signParameterSessionID = null;
		requestedSessionIdFromURL = false;
	}
	
	public void submit()
	{
		if(session != null && session.islazy())
		{			
			session.submit();			
		}
	}

}
