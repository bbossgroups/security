<%@ page language="java" pageEncoding="UTF-8"  session="false"%>
<%@page import="com.frameworkset.util.StringUtil" %>

<%@ taglib uri="/WEB-INF/pager-taglib.tld" prefix="pg"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Session详情</title>
<script type='text/javascript' src='<%=request.getContextPath() %>/include/jquery-1.4.2.min.js' language='JavaScript'></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/include/css/common.css">
 
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/include/syntaxhighlighter/styles/SyntaxHighlighter.css"></link>
<script language="javascript" src="${pageContext.request.contextPath}/include/syntaxhighlighter/shCore.js"></script>
<script language="javascript" src="${pageContext.request.contextPath}/include/syntaxhighlighter/shBrushJava.js"></script>
<script language="javascript" src="${pageContext.request.contextPath}/include/syntaxhighlighter/shBrushXml.js"></script>
<script language="javascript" src="${pageContext.request.contextPath}/include/syntaxhighlighter/shBrushJScript.js"></script>

<body>
<div class="form">
	<div class="mcontent">
		<div id="searchblock">
			<form id="viewForm" name="viewForm">
				<pg:beaninfo requestKey="sessionInfo">
				
					<fieldset >
						<legend><strong>基本信息</strong></legend>
						
						<table border="0" cellpadding="0" cellspacing="0" class="table4" >
							<tr>
								<th width="60"><strong>SessionID:</strong></th>
								<td width="300"><pg:cell colName="sessionid" /></td>
								<th width="100"><strong>创建时间:</strong></th>
								<td width="150"><pg:cell colName="creationTime" dateformat="yyyy-MM-dd HH:mm:ss"/></td>
								<th width="100"><strong>最近访问时间:</strong></th>
								<td width="150"><pg:cell colName="lastAccessedTime" dateformat="yyyy-MM-dd HH:mm:ss"/></td>
								<th width="100"><strong>客户端IP:</strong></th>
								<td width="200"><pg:cell colName="referip"/></td>
							</tr>
							<tr>
								<th width="60"><strong>创建会话服务器:</strong></th>
								<td width="300"><pg:cell colName="host" /></td>
								<th width="100"><strong>有效期:</strong></th>
								<td width="150"> <strong><font color="red"><pg:cell colName="maxInactiveInterval" /></font></strong> </td>
								<th width="100"><strong>预计失效时间:</strong></th>
								<td width="150"><pg:notnull colName="loseTime" evalbody="true">
				       		<pg:yes><strong><font color="red"><pg:cell colName="loseTime" dateformat="yyyy-MM-dd HH:mm:ss"/></font></strong></pg:yes>
				       		<pg:no><strong><font color="red"><pg:cell colName="maxInactiveInterval"  /></font></strong></pg:no>
				       		</pg:notnull ></td>
								<th width="100"><strong>状态:</strong></th>
								<td width="150">
									<pg:true colName="validate" evalbody="true">
						       		<pg:yes><strong><font color="green">有效</font></strong></pg:yes>
						       		<pg:no><strong><font color="red">无效</font></strong></pg:no>
					       		</pg:true>
					       		</td>
							</tr>
							<tr>
								<th width="60"><strong>Cookie HttpOnly:</strong></th>
								<td width="300" ><pg:true colName="httpOnly"  evalbody="true">
					       			<pg:yes><strong><font color="green">启用</font></strong></pg:yes>
					       			<pg:no><strong><font color="red">关闭</font></strong></pg:no>
					       		</pg:true>
	       						</td>	
								<th width="60"><strong>Cookie Secure:</strong></th>
								<td width="300" colspan="10"><pg:true colName="secure" evalbody="true">
						       		<pg:yes><strong><font color="green">启用</font></strong></pg:yes>
						       		<pg:no><strong><font color="red">关闭</font></strong></pg:no>
					       		</pg:true>
	       						</td>									
							</tr>
							<tr>
								<th width="60"><strong>创建会话url:</strong></th>
								<td width="300" ><pg:cell colName="requesturi" /></td>	
								<th width="60"><strong>最近访问会话url:</strong></th>
								<td width="300" colspan="10"><pg:cell colName="lastAccessedUrl" /></td>									
							</tr>
							<tr>
							
								<th width="60"><strong>最近访问服务器:</strong></th>
								<td width="300" colspan="10"><pg:cell colName="lastAccessedHostIP"/></td>									
							</tr>
							
						</table>
					</fieldset>
				
					<fieldset >
						<legend><strong>属性列表</strong></legend>
						<div class="shadow">
						<%if(!StringUtil.isIE(request)){ %>
						<div class='info'><p><div id='detail'>
							<table border='1' cellspacing='0' width='100%' id='resultsTable'><tbody>
								<pg:map actual='${sessionInfo.attributes}'>
									<tr><td ><pg:mapkey/></td><td><pre name='code' class='xml'><pg:cell htmlEncode='true'/></pre></td></tr>
								</pg:map>
						</tbody></table></div></p></div>
						<%}else{ %>
							<table border='1' cellpadding='0' cellspacing='0' class='table2' width='100%'>
							<pg:map actual='${sessionInfo.attributes}'>
								<tr height='60px'><td ><pg:mapkey/></td> 
									<td><textarea rows='8' cols='50'  class='w120'  
								           style='width: 800px;font-size: 12px;height:60px;' ><pg:cell htmlEncode='true'/></textarea>
								</td></tr>
							</pg:map>
							</table>
						<%} %>
						</div>
					</fieldset>
				</pg:beaninfo>
			</form>
		</div>
  	</div>	
  		
	
</div>
</body>
<script type="text/javascript">

var trHtml ="";

if(!$.browser.msie) {
		
	dp.SyntaxHighlighter.ClipboardSwf = '${pageContext.request.contextPath}/include/syntaxhighlighter/clipboard.swf';
	dp.SyntaxHighlighter.HighlightAll('code');
}else {
	
	
}	

</script>


</head>
</html>
