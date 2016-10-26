<%@page session="false" contentType="text/html;charset=UTF-8"%>
<%@page import="org.frameworkset.platform.security.AccessControl"%>

<%
  HttpSession session = request.getSession(false);
  String _redirectPath = request.getParameter("_redirectPath");
  
  	   
  AccessControl accesscontroler = AccessControl.getInstance();
  try
  {
	 
	
  	   
  			
  		boolean success = accesscontroler.checkAccess(request, response,false);
  		String logoutpage = null;
  		if(success)
  		{	  	  			
  			logoutpage = accesscontroler.getSubSystemLogoutRedirect();
  		}
  		else
  		{
  			if(_redirectPath != null && !_redirectPath.equals(""))
  			{
  				logoutpage = _redirectPath;
  			}
  			else
  				logoutpage = accesscontroler.getSubSystemLogoutRedirect(request,null,false);
  		}
  		
  		accesscontroler.logout(logoutpage);
  	   
 	  	
	  
  }
  catch(Exception e)
  {
  	
  }
	 
  

%>


