<%@page import="java.net.URLEncoder"%>
<%@page import="org.frameworkset.web.interceptor.AuthenticateFilter"%>
<%@ page  session="false" language="java" pageEncoding="UTF-8"%>
<%@page import="org.frameworkset.platform.security.AccessControl"%>

<%@ taglib uri="/WEB-INF/pager-taglib.tld" prefix="pg"%>
<%@page import="com.frameworkset.util.StringUtil"%><%
	/**
	* 用户推出系统时，可以退出到用户指定的页面地址，如果没有指定就退出到缺省的登录页面
	* 即login.jsp
	* 同时用户还可指定退出的目的窗口，如果没有指定就退出到top窗口
	*/	
		String referer = request.getParameter(AuthenticateFilter.referpath_parametername); // REFRESH
		String failedbackpath = request.getParameter(AuthenticateFilter.failedbackpath_parametername); // REFRESH
	String redirect = request.getParameter("_redirectPath");//"http://172.16.17.26:9080";
	if(redirect == null || redirect.trim().equals(""))
	{
		redirect = AccessControl.getSubSystemLogoutRedirect(request);
		if(redirect == null || redirect.trim().equals(""))
			redirect = request.getContextPath() + AccessControl.pathloginPage;
	}
	/**
	MemTokenManager memTokenManager = MemTokenManagerFactory.getMemTokenManagerNoexception();
	if(memTokenManager != null)//如果开启令牌机制就会存在memTokenManager对象，否则不存在
	{
		redirect = memTokenManager.appendDTokenToURL(request,redirect);
	}*/
	String target = request.getParameter("_redirecttarget");
	if(target == null || target.trim().equals(""))
		target = "top";
	if(StringUtil.isNotEmpty(referer) && StringUtil.isNotEmpty(redirect))
	{
		StringBuffer temp = new StringBuffer();
		temp.append(redirect);
		if(redirect.indexOf("?") >= 0)
		{
			 temp.append("&").append(AuthenticateFilter.referpath_parametername).append("=").append(URLEncoder.encode(referer, "UTF-8"));
		}
		else
		{
			temp.append("?").append(AuthenticateFilter.referpath_parametername).append("=").append(URLEncoder.encode(referer, "UTF-8"));
		}
		redirect = temp.toString();
		
	}
%>

<head>	
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
	<META  HTTP-EQUIV="Cache-Control" CONTENT="no-cache"/>
	<META HTTP-EQUIV="Expires" CONTENT="0"/>	
	
	
	<script language="javascript">		
				
					
		
		
	
				<%if(target.equals("location")){%>
					window.location = "<%=redirect%>";
					<%}
					else
					{%>
						//alert(top);
						window.<%=target%>.location = "<%=redirect%>";				
					<%}%>		
	
		
	</script>
</head>





