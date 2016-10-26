package org.eclipse.jetty.webapp;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.xml.sax.SAXException;

public class AuthwebJettyStart {
	 public AuthwebJettyStart() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		 
			try {
				BaseApplicationContext jettyconfig = DefaultApplicationContext.getApplicationContext("org/eclipse/jetty/webapp/jetty.xml");
				// 服务器的监听端口
				String port = jettyconfig.getProperty("port", "8080");
				
				String contextPath = jettyconfig.getProperty("context",
						"/");
				
				int p = Integer.parseInt(port);
				Server server = new Server(p);
				// 关联一个已经存在的上下文
				WebAppContext context = new WebAppContext();
				// 设置描述符位置
				String descriptor = jettyconfig.getProperty("webxml",
						"./WebRoot/WEB-INF/web.xml");
				context.setDescriptor(descriptor);
				
				// 设置Web内容上下文路径
				String resourceBase = jettyconfig.getProperty("resourceBase",
						"./WebRoot");
				context.setResourceBase(resourceBase);
				// 设置上下文路径
				context.setContextPath(contextPath.startsWith("/")?contextPath:"/" + contextPath);
				context.setParentLoaderPriority(true);
				ContextHandlerCollection contexts = new ContextHandlerCollection();
				contexts.setHandlers(new Handler[] { context });
	
				server.setHandler(contexts);
	
				// server.setHandler(context);
				// 启动
				server.start();
				System.out.println("http://localhost:"+port+(contextPath.startsWith("/")?contextPath:"/" + contextPath));
				
				server.join();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		 

	}
	
	 
}
