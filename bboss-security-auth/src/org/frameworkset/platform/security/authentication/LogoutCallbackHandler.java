package org.frameworkset.platform.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutCallbackHandler  extends ACLCallbackHandler {
	private String userName;
    private HttpServletRequest request;
    private HttpServletResponse response;
	  public LogoutCallbackHandler(String userName  ,HttpServletRequest request,HttpServletResponse response)
	  {
		  this.userName = userName;
	        this.request = request;
	        this.response = response;
	  }
	@Override
	public HttpServletRequest getRequest() {
		// TODO Auto-generated method stub
		return request;
	}

	@Override
	public HttpServletResponse getResponse() {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return userName;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getUserTypes() {
		// TODO Auto-generated method stub
		return null;
	}
 

}
