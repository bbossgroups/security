package org.frameworkset.security.session.statics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.frameworkset.nosql.mongodb.MongoDB;
import org.frameworkset.security.session.MongoDBUtil;
import org.frameworkset.security.session.SessionSerial;
import org.frameworkset.security.session.SessionUtil;
import org.frameworkset.security.session.impl.SessionHelper;
import org.frameworkset.spi.InitializingBean;

import com.frameworkset.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import com.mongodb.DBObject;

public class MongoSessionStaticManagerImpl extends BaseSessionStaticManagerImpl implements InitializingBean {
	
	 
	 
	

	public MongoSessionStaticManagerImpl() {
		MongoDBUtil.initSessionDB();
	}


	@Override
	public List<SessionAPP> getSessionAPP(HttpServletRequest request) {
		List<SessionAPP> appList = new ArrayList<SessionAPP>();
		
		List<String> list = getAPPName(request);

		for (String appkey : list) {
			SessionAPP sessionApp = new SessionAPP();

			MongoCollection<Document> coll = MongoDBUtil.getSessionCollection(appkey);

			sessionApp.setAppkey(appkey.substring(0,
					appkey.indexOf("_sessions")));
			boolean hasDeletePermission = this.hasDeleteAppPermission(sessionApp.getAppkey(),request);
			sessionApp.setHasDeletePermission(hasDeletePermission);
			sessionApp.setSessions(coll.estimatedDocumentCount());

			appList.add(sessionApp);

		}

		return appList;
	}
	@Override
	public SessionAPP getSingleSessionAPP(HttpServletRequest request)
	{
		String currentAPP = SessionUtil.getAppKey(request);
		return getSingleSessionAPP(currentAPP);
	}
	@Override
	public SessionAPP getSingleSessionAPP(String appName) {
		

		 
		SessionAPP sessionApp = new SessionAPP();
		MongoCollection<Document> coll = MongoDBUtil.getSessionCollection(appName +"_sessions");
		sessionApp.setAppkey(appName);
		sessionApp.setHasDeletePermission(false);
		sessionApp.setSessions(coll.estimatedDocumentCount());
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
		
		if(request == null)
		{
			
			// 获取所有当前db所有信息集合
			Set<String> apps = MongoDBUtil.getSessionDBCollectionNames();
	
			if (apps == null || apps.size() == 0) {
				return null;
			}
	
			Iterator<String> itr = apps.iterator();
	
			while (itr.hasNext()) {
	
				String app = itr.next();
	
				if (app.endsWith("_sessions")) {
					appList.add(app);
				}
	
			}
			return appList;
		}
		else
		{
			String currentAPP = SessionUtil.getAppKey(request);
			String currentAPPTableName = currentAPP + "_sessions";
			// 获取所有当前db所有信息集合
			Set<String> apps = MongoDBUtil.getSessionDBCollectionNames();
	
			if (apps == null || apps.size() == 0) {
				return null;
			}
	
			Iterator<String> itr = apps.iterator();
	
			while (itr.hasNext()) {
	
				String app = itr.next();
				
				if (app.endsWith("_sessions")) {
					if(hasMonitorPermission( app,currentAPPTableName))
						appList.add(app);
				}
	
			}
			return appList;
		}
	}
	
	
	
	@Override
	public List<SessionInfo> getAllSessionInfos(SessionConfig sessionConfig,Map queryParams, int row,
			int page) throws Exception {
		List<SessionInfo> sessionList = new ArrayList<SessionInfo>();
	 
		
		String appKey = (String)queryParams.get("appKey");
		if (StringUtil.isEmpty(appKey)) {
			return null;
		}

		// 获取当前表
		MongoCollection<Document> sessions = MongoDBUtil.getAppSessionDBCollection(appKey);
//		sessions.createIndex(new BasicDBObject("sessionid",1));

		// 查询条件
		BasicDBObject query = new BasicDBObject();

		String sessionid = (String) queryParams.get("sessionid");
		if (!StringUtil.isEmpty(sessionid)) {
			query.append("sessionid", sessionid);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		String createtime_start = (String) queryParams.get("createtime_start");
		String createtime_end = (String) queryParams.get("createtime_end");

		if (!StringUtil.isEmpty(createtime_start)
				&& !StringUtil.isEmpty(createtime_end)) {

			Date start_date = sdf.parse(createtime_start);
			Date end_date = sdf.parse(createtime_end);

			query.append("creationTime",
					new BasicDBObject("$gte", start_date.getTime()).append(
							"$lte", end_date.getTime()));
		} else if (!StringUtil.isEmpty(createtime_start)) {
			Date date = sdf.parse(createtime_start);
			query.append("creationTime",
					new BasicDBObject("$gte", date.getTime()));
		} else if (!StringUtil.isEmpty(createtime_end)) {
			Date date = sdf.parse(createtime_end);

			query.append("creationTime",
					new BasicDBObject("$lte", date.getTime()));
		}

		String host = (String) queryParams.get("host");
		if (!StringUtil.isEmpty(host)) {
			Pattern hosts = Pattern.compile("^" + host + ".*$",
					Pattern.CASE_INSENSITIVE);
			query.append("host", new BasicDBObject("$regex",hosts));
		}

		String referip = (String) queryParams.get("referip");
		if (!StringUtil.isEmpty(referip)) {
			Pattern referips = Pattern.compile("^" + referip + ".*$",
					Pattern.CASE_INSENSITIVE);
			query.append("referip", new BasicDBObject("$regex",referips));
		}

		String validate = (String) queryParams.get("validate");
		if (!StringUtil.isEmpty(validate)) {
			boolean _validate = Boolean.parseBoolean(validate);
			
			
			if(_validate)
			{
				query.append("_validate", _validate);
				BasicDBList values = new BasicDBList();
				values.add(new BasicDBObject("maxInactiveInterval", new BasicDBObject("$lte", 0)));
				values.add(new BasicDBObject("$where","return this.lastAccessedTime + this.maxInactiveInterval >="+ System.currentTimeMillis()));
				query.append("$or", values);
//				query.append("maxInactiveInterval", new BasicDBObject("$lte", 0));
//				query.append("lastAccessedTime + maxInactiveInterval", new BasicDBObject("$gte", System.currentTimeMillis()));
			}
			else
			{
				query.append("maxInactiveInterval", new BasicDBObject("$gt", 0));
				BasicDBList values = new BasicDBList();
				values.add(new BasicDBObject("_validate", false));
				 
				values.add(new BasicDBObject("$where","return this.lastAccessedTime + this.maxInactiveInterval <"+ System.currentTimeMillis()));
				query.append("$or", values);
			}
			
		}

		AttributeInfo[] attributeInfos = sessionConfig == null?null:sessionConfig.getExtendAttributeInfos();
		String serialType = sessionConfig == null?SessionSerial.SERIAL_TYPE_BBOSS:sessionConfig.getSerialType();
		// 显示字段
		List<String> keys = new ArrayList();
		keys.add("appKey");
		keys.add("sessionid");
		keys.add("creationTime");
		keys.add("lastAccessedTime");
		keys.add("maxInactiveInterval");
		keys.add("referip");
		keys.add("_validate");
		keys.add("host");
		keys.add("requesturi");
		keys.add("lastAccessedUrl");
		keys.add("secure");
		keys.add("httpOnly");
		keys.add("lastAccessedHostIP");
		SessionUtil.evalqueryfields(attributeInfos,keys );
		Bson projectionFields = Projections.fields(
				Projections.include(keys),
				Projections.excludeId());
		@SuppressWarnings("unchecked")
		Map<String, AttributeInfo> extendAttributes = (Map<String, AttributeInfo>)queryParams.get("extendAttributes");
		SessionHelper.buildExtendFieldQueryCondition(extendAttributes,    query,serialType);

		FindIterable<Document> findIterable = sessions.find(query).projection(projectionFields).skip(page).limit(row)
				.sort(new BasicDBObject("creationTime", -1));// 1升序，-1降序
		MongoCursor<Document> cursor = null;
		try {
			cursor = findIterable.cursor();
			while (cursor.hasNext()) {
				Document dbobject = cursor.next();

				SessionInfo info = new SessionInfo();

				info.setAppKey(appKey);

				info.setSessionid(dbobject.get("sessionid") + "");

				String creationTime = dbobject.get("creationTime") + "";
				if (!StringUtil.isEmpty(creationTime)) {
					Date creationTimeDate = new Date(
							Long.parseLong(creationTime));
					info.setCreationTime(creationTimeDate);
				}

				String lastAccessedTime = dbobject.get("lastAccessedTime") + "";
				if (!StringUtil.isEmpty(lastAccessedTime)) {
					Date lastAccessedTimeDate = new Date(
							Long.parseLong(lastAccessedTime));
					info.setLastAccessedTime(lastAccessedTimeDate);
				}

				String maxInactiveInterval = dbobject
						.get("maxInactiveInterval") + "";
				if (!StringUtil.isEmpty(maxInactiveInterval)) {
					info.setMaxInactiveInterval(Long
							.parseLong(maxInactiveInterval));
				}

				info.setReferip(dbobject.get("referip") + "");
				info.setValidate((Boolean) dbobject.get("_validate"));
				info.setHost(dbobject.get("host") + "");
				info.setRequesturi((String)dbobject.get("requesturi"));
				info.setLastAccessedUrl((String)dbobject.get("lastAccessedUrl"));
				Object secure_ = dbobject.get("secure");
				if(secure_ != null)
				{
					info.setSecure((Boolean)secure_);
				}
				Object httpOnly = dbobject.get("httpOnly");
				if(httpOnly != null)
				{
					info.setHttpOnly((Boolean)httpOnly);
				}
				else
				{
					info.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionUtil.getSessionManager().isHttpOnly():false);
				}
				info.setLastAccessedHostIP((String)dbobject.get("lastAccessedHostIP"));
					
				 
					List<AttributeInfo> extendAttrs = SessionHelper.evalqueryfiledsValue(attributeInfos,dbobject,serialType);
					
					info.setExtendAttributes(extendAttrs);
				 
				 
				sessionList.add(info);
			}
			 
			
		} finally {
			if(cursor != null)
				cursor.close();
		}

		return sessionList;
	}

	@Override
	public SessionInfo getSessionInfo(String appKey, String sessionid) {

		if (!StringUtil.isEmpty(appKey) && !StringUtil.isEmpty(sessionid)) {
			// 获取当前表
			MongoCollection<Document> sessions = MongoDBUtil.getAppSessionDBCollection(appKey);
//			sessions.createIndex(new BasicDBObject("sessionid", 1));

			// 查询条件
			BasicDBObject query = new BasicDBObject();

			if (!StringUtil.isEmpty(sessionid)) {
				query.append("sessionid", sessionid);
			}

//			// 显示字段
//			BasicDBObject keys = new BasicDBObject();

			Document obj = sessions.find(query).first();

			if (obj == null) {
				return null;
			} else {
				SessionInfo info = new SessionInfo();
				info.setMaxInactiveInterval((Long) obj
						.get("maxInactiveInterval"));
				info.setAppKey(appKey);

				String creationTime = obj.get("creationTime") + "";
				if (!StringUtil.isEmpty(creationTime)) {
					Date creationTimeDate = new Date(
							Long.parseLong(creationTime));
					info.setCreationTime(creationTimeDate);
				}

				String lastAccessedTime = obj.get("lastAccessedTime") + "";
				if (!StringUtil.isEmpty(lastAccessedTime)) {
					Date lastAccessedTimeDate = new Date(
							Long.parseLong(lastAccessedTime));
					info.setLastAccessedTime(lastAccessedTimeDate);
				}

				info.setSessionid(sessionid);
				info.setReferip((String) obj.get("referip"));
				info.setValidate((Boolean) obj.get("_validate"));
				info.setHost((String) obj.get("host"));
				info.setRequesturi((String)obj.get("requesturi"));
				info.setLastAccessedUrl((String)obj.get("lastAccessedUrl"));
				info.setLastAccessedHostIP((String)obj.get("lastAccessedHostIP"));
				Object secure_ = obj.get("secure");
				if(secure_ != null)
				{
					info.setSecure((Boolean)secure_);
				}
				Object httpOnly = obj.get("httpOnly");
				if(httpOnly != null)
				{
					info.setHttpOnly((Boolean)httpOnly);
				}
				else
				{
					info.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionUtil.getSessionManager().isHttpOnly():false);
				}
				Map<String, Object> attributes = MongoDBUtil
						.toMap(obj, false);
				info.setAttributes(attributes);

				return info;
			}
		} else {

			return null;
		}

	}

	@Override
	public void removeSessionInfo(String appKey, String sessionid) {
		if (!StringUtil.isEmpty(appKey) && !StringUtil.isEmpty(sessionid)) {

			MongoCollection<Document> sessions = MongoDBUtil.getAppSessionDBCollection(appKey);
//			sessions.createIndex(new BasicDBObject("sessionid",1));

			// 条件
			BasicDBObject wheresql = new BasicDBObject();
			wheresql.append("sessionid", sessionid);

			MongoDB.remove(sessions,wheresql);

		}
	}

	@Override
	public void removeSessionInfos(String appKey, String[] sessionids) {
		if (!StringUtil.isEmpty(appKey)) {

			for (String sessionid : sessionids) {
				if (!StringUtil.isEmpty(sessionid)) {

					removeSessionInfo(appKey, sessionid);
				}

			}
		}
	}

	@Override
	public void removeAllSession(String appKey,String currentappkey,String currentsessionid) {
		if (!StringUtil.isEmpty(appKey)) {

			MongoCollection<Document> sessions = MongoDBUtil.getAppSessionDBCollection(appKey);

			// 条件
			BasicDBObject wheresql = null;
			
			if(StringUtil.isEmpty((String)currentsessionid))
			{
				wheresql = new BasicDBObject();
			}
				
			else
			{
				if(appKey.equals(currentappkey))
				{
					wheresql = new BasicDBObject("sessionid", new BasicDBObject("$ne", currentsessionid));
				}
				else
				{
					wheresql = new BasicDBObject();
				}
			}
//			sessions.remove(wheresql);
			MongoDB.remove(sessions,wheresql);
		}

	}

	public static void main(String[] args) {
		MongoSessionStaticManagerImpl smsi = new MongoSessionStaticManagerImpl();

		// 应用列表
		List<SessionAPP> list = smsi.getSessionAPP();
		for (SessionAPP app : list) {
			System.out.println(app.getAppkey() + "==" + app.getSessions());
		}

		// 应用对应的session列表
		Map queryParams = new HashMap();
		queryParams.put("appKey", "SanyPDP");
		// queryParams.put("host", "10.8.198.108-BPITGW-TANX");
		// queryParams.put("createtime_start", "2014/06/05 16:15:10");
		// queryParams.put("createtime_end", "2014/06/05 19:05:00");

		// queryParams.put("validate", "true");

		queryParams.put("sessionid", "0c11f3c8");
		try {
			List<SessionInfo> infolist = smsi.getAllSessionInfos(null,queryParams,
					6, 1);
			for (int i = 0; i < infolist .size(); i ++) {
				SessionInfo info = (SessionInfo)infolist .get(i);
				System.out.println("appkey=" + info.getAppKey() + ",host="
						+ info.getHost() + ",MaxInactiveInterval="
						+ info.getMaxInactiveInterval() + ",Referip="
						+ info.getReferip() + ",Sessionid="
						+ info.getSessionid() + ",CreationTime="
						+ info.getCreationTime() + ",LastAccessedTime="
						+ info.getLastAccessedTime() + "validate="
						+ info.isValidate());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// smsi.removeAllSession("SanyPDP");

		// 删除session
		// smsi.removeSessionInfo("SanyPDP",
		// "7f860398-c7b4-461b-ab63-281145dabbf4");

		// SessionInfo info = smsi.getSessionInfo("SanyPDP",
		// "751f3a63-fba2-4372-89ed-ea8957a8eb11");
		// System.out.println("appkey==" + info.getAppKey() + ",host=="
		// + info.getHost() + ",MaxInactiveInterval=="
		// + info.getMaxInactiveInterval() + ",Referip=="
		// + info.getReferip() + ",Sessionid==" + info.getSessionid()
		// + ",CreationTime==" + info.getCreationTime()
		// + ",LastAccessedTime==" + info.getLastAccessedTime()
		// + "validate==" + info.isValidate());
		// Map<String, Object> attributes = info.getAttributes();
		//
		// Set set = attributes.keySet();
		// if (set != null && set.size() > 0) {
		// Iterator it = set.iterator();
		// while (it.hasNext()) {
		// String key = (String) it.next();
		// String value = (String) attributes.get(key);
		// System.out.println("key=" + key + "  value=" + value);
		// }
		// }

	}

	 
 
	@Override
	public boolean deleteApp(String appKey) throws Exception {
		MongoCollection<Document> table = MongoDBUtil.getAppSessionDBCollection(appKey);
		table.drop();
		return true;
	}
 

	@Override
	public void afterPropertiesSet() throws Exception {
		 
		
	}

	 

	 

}
