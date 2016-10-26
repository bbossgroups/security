package org.frameworkset.platform.security;

import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements javax.servlet.http.HttpSessionAttributeListener,HttpSessionListener{

	
	public void sessionCreated(HttpSessionEvent event) {

	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session =  (HttpSession)event.getSource();

		Principal principals = (Principal) session
        .getAttribute(AccessControl.PRINCIPAL_INDEXS);
		if(principals != null)
		{
			AccessControl.logoutdirect(session);
		}
	}

	

	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
			}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	

}
