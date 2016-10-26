<%@page import="com.frameworkset.util.StringUtil"%>
<%@ page language="java" session="false"  isErrorPage="true" contentType="text/html; charset=UTF-8"%>

 
<%
	String approot = request.getContextPath();
%>
<script language="javascript">
function showT(){
	var content = document.getElementById("exceptions");
	if(content.style.display == "none"){
		content.style.display = "";
	}else{
		content.style.display = "none";
	}
}
</script>

<div Style="width: 100%; display: none" id="exceptionDiv">
	<img src="<%=approot %>/include/themes/default/images/messager_error.gif" />	
	<%
		String message = "后台处理异常！";
		boolean sessionTimeOut = false;
		if (exception != null) {
			if (exception.getCause() instanceof org.frameworkset.platform.security.SessionTimeoutExcetpion){
				//response.sendRedirect(request.getContextPath());
				//return;
				sessionTimeOut = true;
			}
			int remoteExceptionCount = 0;
			Throwable e =  exception.getCause();
			while (e != null){
				
				e = e.getCause();
			}
			
			if (remoteExceptionCount > 1){
				message = "";
			}
			else if (remoteExceptionCount == 1){
				message = "";
			}
			//message = exception.getMessage();

	%>	
	<%=message %>
	<a onClick="javascript:showT();" href="javascript:void(0)">查看详细异常</a>
	<div id='exceptions'
		style="display: none; OVERFLOW: auto; width: 100%; height: 460">
		
		<pre>
			<%
				String error = StringUtil.HTMLEncode(StringUtil.exceptionToString(exception));
				//String error = "出错了!";
				//exception.printStackTrace(new java.io.PrintWriter(out));
				out.print(error);
			%>
		</pre>		
	</div>
	<%
		}
	%>
</div>

<script type="text/javascript">
<!--
	var isTopOtherSystem = false;//最上层窗口是不是本系统
	//返回层级窗口
		function findOpenerWin(win){
				var openerWin = win.top.dialogArguments;				
				if (!openerWin){
						openerWin = win.top.opener;					
				}
				if (openerWin){
					try
					{
							openerWin.top.document.location;
					}
					catch(e)
					{					
							isTopOtherSystem = true;										  		
					}			
					if (isTopOtherSystem){
						return openerWin;
					}
					return findOpenerWin(openerWin)
				}
				else{
					try
					{
							win.top.document.location;
					}
					catch(e)
					{					
							isTopOtherSystem = true;										  		
					}			
				}
				return win;
		}	
		
		if (<%=sessionTimeOut%>){
			var openerWin = findOpenerWin(this);	
					
			this.top.location.href = "<%=request.getContextPath()%>";
			
			/**
			//如果本窗口就是原始窗口
			if (openerWin == this){
				this.top.location.href = "<%=request.getContextPath()%>";
			}
			else{
				alert("会话超时，请重新登录！");
				//关闭当前窗口
				this.close();
				//如果没有跨域	,即原始窗口不是其他系统			
				if (!isTopOtherSystem){
					openerWin.top.location.href = "<%=request.getContextPath()%>";
				}				
			}	
			*/		
		}
		else{
			document.getElementById("exceptionDiv").style.display = "block";
		}
//-->
</script>
		