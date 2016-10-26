<%@page import="org.frameworkset.platform.security.AccessControl"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<script type="text/javascript">
	/**
	var wHeight = window.screen.height-1;
	var wWidth = window.screen.width-1;
	//获取屏幕分辨率
	var param = 'height=' + wHeight + ',' + 'width=' + wWidth
			+ ',top=0, left=0,status=no,toolbar=no,menubar=no,location=no,resizable=yes';
	var milliseconds = new Date();
	
	window.moveTo(0,0)
	window.resizeTo(wWidth, wHeight)
	*/
	//window.location.href = 'login.jsp';
	<%
		String rpage = "sanydesktop/index.page";
		AccessControl control = AccessControl.getAccessControl();
		if(control != null)
		{
			rpage = control.getIndexPage(request);
			if(rpage == null || rpage.equals(""))
				rpage = "sanydesktop/index.page";
			if(rpage != null && 
					(rpage.equals(request.getContextPath()) || rpage.equals(request.getContextPath() + "/")) )
				rpage = "sanydesktop/index.page";
		}
	%>
	window.location.href = '<%=rpage%>';
	
</script>
</body>
</html>
