package org.frameworkset.platform.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface CallbackHandler {
//	 void handle(Callback[] callbacks)
//			    throws java.io.IOException, UnsupportedCallbackException;
		public HttpServletRequest getRequest();

		public HttpServletResponse getResponse();
		public String getUserName() ;

		public String getPassword() ;

		public String[] getUserTypes();
}
