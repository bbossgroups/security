package org.frameworkset.security.session.statics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.nosql.mongodb.MongoDB;
import org.frameworkset.nosql.mongodb.MongoDBHelper;
import org.frameworkset.nosql.redis.RedisFactory;
import org.frameworkset.nosql.redis.RedisHelper;
import org.frameworkset.security.session.impl.RedisSessionStore;
import org.frameworkset.security.session.impl.SessionHelper;
import org.frameworkset.spi.InitializingBean;

import com.frameworkset.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class RedisSessionStaticManagerImpl extends BaseSessionStaticManagerImpl implements InitializingBean {
	
	 
	

	public RedisSessionStaticManagerImpl() {
		 
	}

	
	
	@Override
	public List<SessionAPP> getSessionAPP(HttpServletRequest request) {
		List<SessionAPP> appList = new ArrayList<SessionAPP>();
		
		List<String> list = getAPPName(request);

		for (String appkey : list) {
			SessionAPP sessionApp = new SessionAPP();


			sessionApp.setAppkey(appkey);
			boolean hasDeletePermission = this.hasDeleteAppPermission(sessionApp.getAppkey(),request);
			sessionApp.setHasDeletePermission(hasDeletePermission);
			sessionApp.setSessions(-1);

			appList.add(sessionApp);

		}

		return appList;
	}
	
	@Override
	public SessionAPP getSingleSessionAPP(String appName) {
		

		 
		SessionAPP sessionApp = new SessionAPP();
		 
		sessionApp.setAppkey(appName);
		sessionApp.setHasDeletePermission(false);
		sessionApp.setSessions(-1);		
		return sessionApp;
	}
	
	/**
	 * 获取当前db中以_sessions结尾的表名
	 * 如果request不为空就是需要获取带权限的会话表数据
	 * 
	 * @return 2014年6月5日
	 */
	public List<String> getAPPName(HttpServletRequest request) {

		List<String> appList = new ArrayList<String>();
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			if(request == null)
			{
				
				// 获取所有当前db所有信息集合
				Set<String> apps = redisHelper.smembers(RedisSessionStore.sessionapps);
		
				if (apps == null || apps.size() == 0) {
					return null;
				}
				appList.addAll(apps);
				
				return appList;
			}
			else
			{
				String currentAPP = SessionHelper.getAppKey(request);
				 
				// 获取所有当前db所有信息集合
				Set<String> apps = redisHelper.smembers(RedisSessionStore.sessionapps);
		
				if (apps == null || apps.size() == 0) {
					return null;
				}
		
				Iterator<String> itr = apps.iterator();
		
				while (itr.hasNext()) {
		
					String app = itr.next();					
					 
					if(hasMonitorPermission( app,currentAPP))
						appList.add(app);
				 
				}
				return appList;
			}
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
	}
	 
	
	@Override
	/**
	 * 只支持根据sessionid进行查询，其他不支持
	 */
	public List<SessionInfo> getAllSessionInfos(SessionConfig sessionConfig,Map queryParams, int row,
			int page) throws Exception {
		String appKey = (String)queryParams.get("appKey");
		if (StringUtil.isEmpty(appKey)) {
			return null;
		}
		List<SessionInfo> sessions = null;
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			String sessionid = (String) queryParams.get("sessionid");
			if(sessionid == null || sessionid.equals(""))
				return null;
			List<String> fields = new ArrayList<String>();
			fields.add("appKey");
			fields.add("sessionid");
			fields.add("creationTime");
			fields.add("lastAccessedTime");
			fields.add("maxInactiveInterval");
			fields.add("referip");
			fields.add("_validate");
			fields.add("host");
			fields.add("requesturi");
			fields.add("lastAccessedUrl");
			fields.add("secure");
			fields.add("httpOnly");
			fields.add("lastAccessedHostIP");
			AttributeInfo[] attributeInfos = sessionConfig == null?null:sessionConfig.getExtendAttributeInfos();
			if(attributeInfos != null && attributeInfos.length > 0)
			{
				for(AttributeInfo attr:attributeInfos)
				{
					fields.add(attr.getName());				 
					
				}
				
				
			}
			String sessionKey = RedisSessionStore.getAPPSessionKey(appKey, sessionid);
			String[] fs = new String[fields.size()];
			List<String> data = redisHelper.hmget(sessionKey, fields.toArray(fs));
			if(data != null)
			{
				if(data.get(1) == null)
					return null;
				sessions = new ArrayList<SessionInfo>();
				SessionInfo info = new SessionInfo();

				info.setAppKey(data.get(0));

				info.setSessionid(data.get(1));

				String creationTime = data.get(2);
				if (!StringUtil.isEmpty(creationTime)) {
					Date creationTimeDate = new Date(
							Long.parseLong(creationTime));
					info.setCreationTime(creationTimeDate);
				}

				String lastAccessedTime = data.get(3);
				if (!StringUtil.isEmpty(lastAccessedTime)) {
					Date lastAccessedTimeDate = new Date(
							Long.parseLong(lastAccessedTime));
					info.setLastAccessedTime(lastAccessedTimeDate);
				}

				String maxInactiveInterval = data.get(4);
				if (!StringUtil.isEmpty(maxInactiveInterval)) {
					info.setMaxInactiveInterval(Long
							.parseLong(maxInactiveInterval));
				}

				info.setReferip(data.get(5));
				info.setValidate(Boolean.parseBoolean(data.get(6)));
				info.setHost(data.get(7));
				info.setRequesturi(data.get(8));
				info.setLastAccessedUrl(data.get(9));
				String secure_ = data.get(10);
				if(secure_ != null)
				{
					info.setSecure(Boolean.parseBoolean(secure_));
				}
				String httpOnly = data.get(11);
				if(httpOnly != null)
				{
					info.setHttpOnly(Boolean.parseBoolean(httpOnly));
				}
				else
				{
					info.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionHelper.getSessionManager().isHttpOnly():false);
				}
				info.setLastAccessedHostIP(data.get(12));
					
				 
					List<AttributeInfo> extendAttrs = SessionHelper.evalqueryfiledsValue(attributeInfos,data,13);
					
					info.setExtendAttributes(extendAttrs);
				 
				 
					sessions.add(info);
			}
			
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
		return sessions;
	}

	@Override
	public SessionInfo getSessionInfo(String appKey, String sessionid) {
		SessionInfo info = null;
		if (!StringUtil.isEmpty(appKey) && !StringUtil.isEmpty(sessionid)) {
			
			
			RedisHelper redisHelper = null;
			try
			{
				redisHelper = RedisFactory.getRedisHelper();
				 
				 
				String sessionKey = RedisSessionStore.getAPPSessionKey(appKey, sessionid);
				 
				Map<String,String> data = redisHelper.hgetAll(sessionKey);
				 
				if(data != null&& data.size() > 0)
				{
					 
					  info = new SessionInfo();

					info.setAppKey(data.get("appKey"));

					info.setSessionid(data.get("sessionid"));

					String creationTime = data.get("creationTime");
					if (!StringUtil.isEmpty(creationTime)) {
						Date creationTimeDate = new Date(
								Long.parseLong(creationTime));
						info.setCreationTime(creationTimeDate);
					}

					String lastAccessedTime = data.get("lastAccessedTime");
					if (!StringUtil.isEmpty(lastAccessedTime)) {
						Date lastAccessedTimeDate = new Date(
								Long.parseLong(lastAccessedTime));
						info.setLastAccessedTime(lastAccessedTimeDate);
					}

					String maxInactiveInterval = data.get("maxInactiveInterval");
					if (!StringUtil.isEmpty(maxInactiveInterval)) {
						info.setMaxInactiveInterval(Long
								.parseLong(maxInactiveInterval));
					}

					info.setReferip(data.get("referip"));
					info.setValidate(Boolean.parseBoolean(data.get("_validate")));
					info.setHost(data.get("host"));
					info.setRequesturi(data.get("requesturi"));
					info.setLastAccessedUrl(data.get("lastAccessedUrl"));
					String secure_ = data.get("secure");
					if(secure_ != null)
					{
						info.setSecure(Boolean.parseBoolean(secure_));
					}
					String httpOnly = data.get("httpOnly");
					if(httpOnly != null)
					{
						info.setHttpOnly(Boolean.parseBoolean(httpOnly));
					}
					else
					{
						info.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionHelper.getSessionManager().isHttpOnly():false);
					}
					info.setLastAccessedHostIP(data.get("lastAccessedHostIP"));
						
					Map<String, Object> attributes = toMap(data, false);
					info.setAttributes(attributes); 
 
				}
				
			}
			finally
			{
				if(redisHelper != null)
					redisHelper.release();
			}
			return info;
			
			
			 
			 
		} else {

			return null;
		}

	}
	
	public static Map<String,Object> toMap(Map<String,String> object,boolean deserial) {

		Set<Entry<String, String>> set = object.entrySet();
		if (set != null && set.size() > 0) {
			Map<String,Object> attrs = new HashMap<String,Object>();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> entry =   it.next();
				if (!SessionHelper.filter(entry.getKey())) {
					String value = entry.getValue();
					try {
						attrs.put(entry.getKey(),
								deserial?SessionHelper.unserial((String) value):value);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return attrs;
		}
		return null;
	}

	@Override
	public void removeSessionInfo(String appKey, String sessionid) {
		

		if (!StringUtil.isEmpty(appKey) && !StringUtil.isEmpty(sessionid)) {
			
			
			RedisHelper redisHelper = null;
			try
			{
				redisHelper = RedisFactory.getRedisHelper();			 
				 
				String sessionKey = RedisSessionStore.getAPPSessionKey(appKey, sessionid);
				redisHelper.del(sessionKey);
			}
			finally
			{
				
			}
		}

	}

	@Override
	public void removeSessionInfos(String appKey, String[] sessionids) {
 
		throw new java.lang.UnsupportedOperationException("Redis Session Store do not support batch remove Sessions:removeSessionInfos.");
	}

	@Override
	public void removeAllSession(String appKey,String currentappkey,String currentsessionid) {
		throw new java.lang.UnsupportedOperationException("Redis Session Store do not support batch remove Sessions:removeAllSession.");

	}

	 

	



	@Override
	public boolean deleteApp(String appKey) throws Exception {
		throw new java.lang.UnsupportedOperationException("Redis Session Store do not support batch remove Sessions:removeAllSession.");
	}

	 

	@Override
	public void afterPropertiesSet() throws Exception {
		 
		
	}

	

}
