package org.frameworkset.security.session.impl;



import com.frameworkset.util.SimpleStringUtil;

public class UUIDSessionIDGenerator   extends AbstractSessionIDGenerator {

	@Override
	public SessionID generateID() {
		SessionID sessionid = new SessionID();
		sessionid.setSessionId(SimpleStringUtil.getUUID());
		 return sessionid;
	}
	

}
