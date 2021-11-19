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

import com.frameworkset.listener.BSServletRequestListener;
import org.frameworkset.util.AntPathMatcher;
import org.frameworkset.util.PathMatcher;
import org.frameworkset.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 * <p>Title: SessionFilter.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年4月15日
 * @author biaoping.yin
 * @version 3.8.0
 */
public class SessionFilter implements Filter {
	protected boolean dosessionfilter = true;
	protected ServletContext servletContext;
	protected List<String> excludePatterns;
	protected List<String> includePatterns;
	
	protected UrlPathHelper urlPathHelper = new UrlPathHelper();
	protected PathMatcher pathMatcher = new AntPathMatcher();
	@Override
	public void destroy() {
		
		servletContext = null;
		dosessionfilter = true;
		excludePatterns = null;
		includePatterns = null;
	}
	
	protected SessionHttpServletRequestWrapper buildSessionHttpServletRequestWrapper(ServletRequest request, ServletResponse response)
	{
		SessionHttpServletRequestWrapper mrequestw = new SessionHttpServletRequestWrapper((HttpServletRequest)request,(HttpServletResponse)response,this.servletContext);
		return mrequestw;
	}

	private boolean excludeRequest(ServletRequest request){
		if(this.excludePatterns == null || this.excludePatterns.size() == 0)
			return false;
		String uri = urlPathHelper.getRequestUri((HttpServletRequest)request);
		if(includePatterns != null && includePatterns.size() > 0){
			boolean include = false;
			for(int i = 0;  i < this.includePatterns.size(); i ++){
				String pattern = this.includePatterns.get(i);
				include = this.pathMatcher.match(pattern, uri);
				if(include )
					break;
			}
			if(include)
				return false;
		}
		boolean exclude = false;
		for(int i = 0;  i < this.excludePatterns.size(); i ++){
			String pattern = this.excludePatterns.get(i);
			exclude = this.pathMatcher.match(pattern, uri);
			if(exclude )
				break;
		}
		return exclude;
		
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain fc) throws IOException, ServletException {
		boolean exclude = excludeRequest(request);
		if(exclude){
			fc.doFilter(request, response);
			return;
		}
		if(dosessionfilter)
		{
			SessionHttpServletRequestWrapper mrequestw = buildSessionHttpServletRequestWrapper(  request,   response);
			try
			{
				mrequestw.touch();
				BSServletRequestListener.requestReset(mrequestw);
			    fc.doFilter(mrequestw, response);
			}
			finally
			{
				mrequestw.submit();
			}
		}
		else
		{
			fc.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {
	
		servletContext = fc.getServletContext();
		String excludePatterns_ =  fc.getInitParameter("excludePatterns");
		if(excludePatterns_ != null && !excludePatterns_.equals("")){
			excludePatterns = new ArrayList<String>();
			String[] patterns = excludePatterns_.split(",");
			for(int i = 0;  i < patterns.length; i ++){
				String p = patterns[i].trim() ;
				if(!p.equals(""))
					excludePatterns.add(p);
			}
		}
		
		
		String includePatterns_ =  fc.getInitParameter("includePatterns");
		if(includePatterns_ != null && !includePatterns_.equals("")){
			includePatterns = new ArrayList<String>();
			String[] patterns = includePatterns_.split(",");
			for(int i = 0;  i < patterns.length; i ++){
				String p = patterns[i].trim() ;
				if(!p.equals(""))
					includePatterns.add(p);
			}
		}
		
	}

}
