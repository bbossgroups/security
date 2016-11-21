/**
 * 
 */
package org.frameworkset.web.session;

import javax.servlet.ServletContext;

import org.frameworkset.security.session.InvalidateCallback;
import org.frameworkset.security.session.Session;
import org.frameworkset.security.session.impl.HttpSessionImpl;
import org.frameworkset.web.auth.AuthenticatedToken;
import org.frameworkset.web.auth.TicketConsts;

/**
 * @author yinbp
 *
 * @Date:2016-11-21 01:15:29
 */
public class TicketHttpSessionImpl extends HttpSessionImpl {
	protected AuthenticatedToken token;
	protected String authenticatecode;

	/**
	 * @param session
	 * @param servletContext
	 * @param contextpath
	 * @param invalidateCallback
	 */
	public TicketHttpSessionImpl(AuthenticatedToken token,String authenticatecode, Session session, ServletContext servletContext,
			String contextpath, InvalidateCallback invalidateCallback) {
		super(session, servletContext, contextpath, invalidateCallback);
		this.token = token;
		this.authenticatecode =  authenticatecode;
		// TODO Auto-generated constructor stub
	}

	protected void assertSession(String attribute) {
		if (session != null)
			super.session.assertSession(this, attribute);
	}

	@Override
	public Object getAttribute(String attribute) {
		if (attribute.equals(TicketConsts.ticket_session_token_key)) {
			assertSession(attribute);
			return this.token;
		} 
		else if (attribute.equals(TicketConsts.ticket_session_authenticatecode_key)) {
			assertSession(attribute);
			return this.authenticatecode;
		}
		else
			return super.getAttribute(attribute);
	}

	@Override
	public Object getValue(String attribute) {
		if (attribute.equals(TicketConsts.ticket_session_token_key)) {
			assertSession(attribute);
			return this.token;
		} 
		else if (attribute.equals(TicketConsts.ticket_session_authenticatecode_key)) {
			assertSession(attribute);
			return this.authenticatecode;
		}
		 else
			return super.getValue(attribute);
	}

	@Override
	public void putValue(String attribute, Object value) {
		if (attribute.equals(TicketConsts.ticket_session_token_key)) {
			assertSession(attribute);
			this.token = (AuthenticatedToken) value;
		} 
		else if (attribute.equals(TicketConsts.ticket_session_authenticatecode_key)) {
			assertSession(attribute);
			 this.authenticatecode = (String) value;
		}
		 else
			super.putValue(attribute, value);
	}

	@Override
	public void removeAttribute(String attribute) {
		if (attribute.equals(TicketConsts.ticket_session_token_key)){
			assertSession(attribute);
			this.token = null;
		}
		else if (attribute.equals(TicketConsts.ticket_session_authenticatecode_key)) {
			assertSession(attribute);
			 this.authenticatecode = null;
		}
		 else
			super.removeAttribute(attribute);
	}

	@Override
	public void removeValue(String attribute) {
		if (attribute.equals(TicketConsts.ticket_session_token_key)){
			assertSession(attribute);
			this.token = null;
		}
		 
		else if (attribute.equals(TicketConsts.ticket_session_authenticatecode_key)) {
			assertSession(attribute);
			 this.authenticatecode = null;
		}
		 else
			super.removeValue(attribute);
	}

	@Override
	public void setAttribute(String attribute, Object value) {
		if (attribute.equals(TicketConsts.ticket_session_token_key)){
			assertSession(attribute);
			this.token = (AuthenticatedToken) value;
		}		 
		else if (attribute.equals(TicketConsts.ticket_session_authenticatecode_key)) {
			assertSession(attribute);
			 this.authenticatecode = (String) value;
		}
		 else
			super.setAttribute(attribute, value);
	}

}
