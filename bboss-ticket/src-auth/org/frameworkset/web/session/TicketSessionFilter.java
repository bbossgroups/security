/**
 * 
 */
package org.frameworkset.web.session;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.frameworkset.security.session.impl.SessionFilter;
import org.frameworkset.security.session.impl.SessionHttpServletRequestWrapper;

/**
 * @author yinbp
 *
 * @Date:2016-11-20 22:23:08
 */
public class TicketSessionFilter extends SessionFilter{

	@Override
	protected SessionHttpServletRequestWrapper buildSessionHttpServletRequestWrapper(ServletRequest request,
			ServletResponse response) {
		SessionHttpServletRequestWrapper mrequestw = new TicketSessionHttpServletRequestWrapper((HttpServletRequest)request,(HttpServletResponse)response,this.servletContext);
		return mrequestw;
	}

 

}
