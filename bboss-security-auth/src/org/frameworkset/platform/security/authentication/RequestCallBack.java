package org.frameworkset.platform.security.authentication;

import javax.servlet.http.HttpServletRequest;

public class RequestCallBack  implements Callback{
	
	private HttpServletRequest request;
	private String promt ;
	
	public RequestCallBack(String promt) {
		this.promt = promt;
	}
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}
	
	public HttpServletRequest getRequest()
	{
		return request; 
	}
	public String getPromt() {
		return promt;
	}

	
}
