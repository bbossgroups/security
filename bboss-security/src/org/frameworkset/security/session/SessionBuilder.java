/**
 * 
 */
package org.frameworkset.security.session;

import org.frameworkset.security.session.impl.HttpSessionImpl;

/**
 * @author yinbp
 *
 * @Date:2016-11-21 15:45:11
 */
public interface SessionBuilder extends InvalidateCallback {

	public HttpSessionImpl buildHttpSessionImpl(Session session);
}
