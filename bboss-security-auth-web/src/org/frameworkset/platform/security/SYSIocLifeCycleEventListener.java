package org.frameworkset.platform.security;

import java.util.Map;

import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.framework.Framework;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.event.IocLifeCycleEventListener;
import org.frameworkset.web.servlet.context.WebApplicationContext;

public class SYSIocLifeCycleEventListener implements IocLifeCycleEventListener {

	public SYSIocLifeCycleEventListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void afterstart(BaseApplicationContext arg0) {
	/*	// 初始化任务管理服务
		TaskService service = TaskService.getTaskService();
		service.startService();
		CacheManager.registRefreshEventListener();
		//启动分布式事件服务
		EventUtils.init();  */
		System.out.println("public void afterstart(BaseApplicationContext arg0) ");
		Framework.getInstanceWithContext(((WebApplicationContext)arg0).getServletContext()).init();
	}

	@Override
	public void beforestart() {
		System.out.println("public void beforestart() {");


	}

	@Override
	public void init(Map<String, String> params) {
		// TODO Auto-generated method stub
		
	}

}
