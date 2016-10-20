<%@ page contentType="text/html; charset=UTF-8" session="false"%>

<%
 
out.println("<div>ID request.getSession(true).getId():"+request.getSession(true).getId()+"</div>");
request.getSession().invalidate();
out.println("<div>after invalidate session request.getSession(false):"+request.getSession(false)+"</div>");


out.println("<div>after invalidate session request.getSession().getId():"+request.getSession().getId()+"</div>");


out.println("<div>ID request.getSession(true).getId():"+request.getSession(true).getId()+"</div>");
 %>
 
 <a href="http://sessionmonitor.bbossgroups.com" target="demo">session跨域测试</a>
 <br>
 <a href="http://sessionmonitor.bbossgroups.com/session/sessionManager/sessionManager.page" target="demomonitor">session监控</a>