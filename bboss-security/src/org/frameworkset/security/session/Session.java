package org.frameworkset.security.session;

import java.util.Enumeration;
import java.util.Map;

import org.frameworkset.security.session.impl.ModifyValue;

public interface Session {

	public void initInvalidateCallback(InvalidateCallback invalidateCallback);
	public Object getAttribute(SimpleHttpSession session,String attribute,String contextpath) ;

	public Object getCacheAttribute(String attribute);
	public Enumeration getAttributeNames(SimpleHttpSession session,String contextpath) ;

	
	public long getCreationTime() ;
	
	public String getId() ;
	/**
	 * 更新最后访问时间
	 */
	public void touch(SimpleHttpSession session,String lastAccessedUrl,String contextpath,int MaxInactiveInterval);
	public long getLastAccessedTime() ;
	public void setLastAccessedTime(long lastAccessedTime) ;
	public long getMaxInactiveInterval();

//	@Override
//	public ServletContext getServletContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public HttpSessionContext getSessionContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	
	public Object getValue(SimpleHttpSession session,String attribute,String contextpath) ;
	public String[] getValueNames(SimpleHttpSession session,String contextpath) ;
	public void invalidate(SimpleHttpSession session,String contextpath) ;
	public boolean isNew() ;
	public void putValue(SimpleHttpSession session,String attribute, Object value,String contextpath) ;

	
	public void removeAttribute(SimpleHttpSession session,String attribute,String contextpath) ;
	public void removeValue(SimpleHttpSession session,String attribute,String contextpath) ;
	public void setAttribute(SimpleHttpSession session,String attribute, Object value,String contextpath) ;
	public void setMaxInactiveInterval(SimpleHttpSession session,long maxInactiveInterval,String contextpath) ;
	public void setMaxInactiveInterval(SimpleHttpSession session,long maxInactiveInterval,boolean refreshstore,String contextpath) ;
	public String getReferip();
	public boolean isValidate();
	public void _setSessionStore(SessionStore sessionStore);
//	public String getSessionID();
//	public Object getAttribute(String attribute);
//	public void setAttribute(String attribute,Object value);


	public void putNewStatus();
	
	public String getRequesturi() ;
	public void setRequesturi(String requesturi);
	public void submit();
	public boolean islazy() ;
	public void modifyAttribute(String name, Object value, int valuetype, int optype);
	public Map<String, ModifyValue> getModifyattributes() ;
}
