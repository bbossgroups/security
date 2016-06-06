<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="test.*"%>
<%
String value = (String)session.getAttribute("$a.b.c");
if(value == null)
{
	session.setAttribute("$a.b.c", "abc");
}
value = (String)session.getAttribute("$a.b.c");
out.println("$a.b.c:"+value);
out.println("<br>");
  
out.println("<br>");
out.println("local:"+session.getAttribute("local"));

//下面的功能演示存储一个复杂对象（包含引用关系）到session中，然后读取出来验证对象关系是否正确还原
 

  
out.println("<br>");
String privateAttr = (String)session.getAttribute("privateAttr");//session应用设置的私有会话属性
out.println("sessionmonitor's private attribute:"+privateAttr+"<br>");  
String userAccount = (String)session.getAttribute("userAccount");
out.println("shared attribute userAccount:"+userAccount+"<br>");
TestVO testVO = (TestVO)session.getAttribute("testVO");
if(testVO != null)
{
	out.println("attribute testVO.id:"+testVO.getId()+"<br>");
	testVO.setId("test modify");
}
 %>
 
