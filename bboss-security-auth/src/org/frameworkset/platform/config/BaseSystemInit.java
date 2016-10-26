package org.frameworkset.platform.config;

import java.util.Map;

import javax.servlet.ServletContext;

public abstract class BaseSystemInit implements SystemInit {
	protected ServletContext context ;
	protected Map params;

	
	public void setContext(ServletContext context) {
		this.context = context;
		
	}


}
