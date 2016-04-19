package org.frameworkset.security.session;

import javax.servlet.http.HttpSession;

public interface SimpleHttpSession extends HttpSession {
	public Session getInnerSession();
	public boolean islazy();
	public void modifyAttribute(String name, Object value, int valuetype,int optype);
}
