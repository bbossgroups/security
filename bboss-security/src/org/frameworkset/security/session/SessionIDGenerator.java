package org.frameworkset.security.session;

import org.frameworkset.security.session.impl.SessionID;

public interface SessionIDGenerator {
	/**
	 * 生成sessionid
	 * @return
	 */
	SessionID generateID();
	

}
