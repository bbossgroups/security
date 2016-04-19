package org.frameworkset.security.session.statics;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.security.session.impl.SessionHelper;

public abstract class BaseSessionStaticManagerImpl implements SessionStaticManager{

	/**
	 * monitorScope="self|all" 指定监控管理的session数据的应用系统范围:
	self:表示只能监控管理本应用的会话数据
	all:表示监控管理所有应用的会话数据
	 */
	protected String monitorScope;
	
	public BaseSessionStaticManagerImpl() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 判断应用是否有查询会话权限，除了总控应用可以看所有会话外，其他的应用只能看当前应用的会话数据
	 * @param app 
	 * @param currentapp
	 * @return
	 */
	public boolean hasMonitorPermission(String app,String currentapp)
	{
		if(this.monitorScope == null || this.monitorScope.equals(MONITOR_SCOPE_SELF))
		{
			return app.equals(currentapp);
		}
		else if(this.monitorScope.equals(MONITOR_SCOPE_ALL))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断用户是有使用app的session管理权限
	 * @param app 
	 * @param currentapp
	 * @return
	 */
	public boolean hasMonitorPermission(String app,HttpServletRequest request)
	{
		String currentAPP = SessionHelper.getAppKey(request);
		if(this.monitorScope == null || this.monitorScope.equals(MONITOR_SCOPE_SELF))
		{
			return app.equals(currentAPP);
		}
		else if(this.monitorScope.equals(MONITOR_SCOPE_ALL))
		{
			return true;
		}
		return false;
	}
	
	public String getMonitorScope() {
		return monitorScope;
	}

	public void setMonitorScope(String monitorScope) {
		this.monitorScope = monitorScope;
	}
	@Override
	public boolean hasDeleteAppPermission(String app, HttpServletRequest request) {
		
		return this.monitorScope != null && this.monitorScope.equals(MONITOR_SCOPE_ALL);
	}
	@Override
	public List<SessionAPP> getSessionAPP() {
//		List<SessionAPP> appList = new ArrayList<SessionAPP>();
//
//		List<String> list = getAPPName();
//
//		for (String appkey : list) {
//			SessionAPP sessionApp = new SessionAPP();
//
//			DBCollection coll = db.getCollection(appkey);
//
//			sessionApp.setAppkey(appkey.substring(0,
//					appkey.indexOf("_sessions")));
//			sessionApp.setSessions(coll.getCount());
//
//			appList.add(sessionApp);
//
//		}
//
//		return appList;
		return getSessionAPP((HttpServletRequest )null);
	}
	/**
	 * 获取当前db中以_sessions结尾的表名
	 * 
	 * @return 2014年6月5日
	 */
	public List<String> getAPPName() {

//		List<String> appList = new ArrayList<String>();
//
//		// 获取所有当前db所有信息集合
//		Set<String> apps = db.getCollectionNames();
//
//		if (apps == null || apps.size() == 0) {
//			return null;
//		}
//
//		Iterator<String> itr = apps.iterator();
//
//		while (itr.hasNext()) {
//
//			String app = itr.next();
//
//			if (app.endsWith("_sessions")) {
//				appList.add(app);
//			}
//
//		}
//		return appList;
		return getAPPName((HttpServletRequest)null);
	}
	@Override
	public SessionAPP getSingleSessionAPP(HttpServletRequest request)
	{
		String currentAPP = SessionHelper.getAppKey(request);
		return getSingleSessionAPP(currentAPP);
	}
	@Override
	public boolean isMonitorAll() {
		
		return this.monitorScope != null && this.monitorScope.equals(MONITOR_SCOPE_ALL);
	}

	@Override
	public AttributeInfo[] getExtendAttributeArray(String appkey) {
		SessionConfig sessionConfig = SessionHelper.getSessionConfig(appkey);
		return sessionConfig == null?null:sessionConfig.getExtendAttributeInfos();
	}
	
	public SessionConfig getSessionConfig(String appkey)
	{
		return SessionHelper.getSessionConfig(appkey);
	}

}
