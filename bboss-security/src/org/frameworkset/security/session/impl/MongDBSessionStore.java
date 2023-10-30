package org.frameworkset.security.session.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.frameworkset.nosql.mongodb.MongoDB;
import org.frameworkset.nosql.mongodb.MongoDBHelper;
import org.frameworkset.security.session.AttributeNamesEnumeration;
import org.frameworkset.security.session.MongoDBUtil;
import org.frameworkset.security.session.Session;
import org.frameworkset.security.session.SessionBasicInfo;
import org.frameworkset.security.session.SessionBuilder;
import org.frameworkset.security.session.SessionUtil;
import org.frameworkset.security.session.SimpleHttpSession;
import org.frameworkset.security.session.domain.CrossDomain;
import org.frameworkset.security.session.statics.SessionConfig;
import org.frameworkset.soa.ObjectSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frameworkset.util.SimpleStringUtil;
import com.frameworkset.util.StringUtil;
import com.mongodb.BasicDBObject;


public class MongDBSessionStore extends BaseSessionStore{
	
	private static Logger log = LoggerFactory.getLogger(MongDBSessionStore.class);
	public MongDBSessionStore()
	{
		MongoDBUtil.initSessionDB();
	}
	public void destory()
	{
		
//		MongoDBUtil.destory();
		
	}
	@Override
	public void livecheck() {
		Set<String> apps = MongoDBUtil.getSessionDBCollectionNames();
		if(apps == null || apps.size() == 0)
			return;
		long curtime = System.currentTimeMillis();
		StringBuffer wherefun = new StringBuffer();
		wherefun.append("function() ")
				.append("{")	
				 .append(" if(!this._validate) return true;")
				 .append(" if(this.maxInactiveInterval <= 0) return false;")
			    .append(" if(this.lastAccessedTime + this.maxInactiveInterval < ").append(curtime).append(")")
			    .append("{")
				.append("return true;")				
				.append("}")
				.append("else")
				.append(" {")
				.append(" return false;")		
				.append("}")
				.append("}");
		String temp = wherefun.toString();
		Iterator<String> itr = apps.iterator();
		while(itr.hasNext())
		{
			String app = itr.next();
			if(app.endsWith("_sessions"))
			{
				MongoCollection<Document> appsessions = MongoDBUtil.getSessionCollection(app);
				MongoDB.remove(appsessions,new BasicDBObject("$where",temp));
			}
		}
		
	}

	
	private MongoCollection<Document> getAppSessionDBCollection(String appKey)
	{
		
		 
		 return MongoDBUtil.getAppSessionDBCollection(appKey);
	}

	private MongoCollection<Document> getJOURNALEDAppSessionDBCollection(String appKey)
	{

		return MongoDBUtil.getJOURNALEDAppSessionDBCollection(appKey);
	}
	private MongoCollection<Document> getConfigSessionDBCollection()
	{
		
		return MongoDBUtil.getConfigSessionDBCollection();
	}
	public void saveSessionConfig(SessionConfig config)
	{
		MongoCollection<Document> sessionconf = getConfigSessionDBCollection();
		try {	
		
//			BasicDBObject keys = new BasicDBObject();
//			keys.put("appcode", 1);
			Bson projectionFields = Projections.fields(
					Projections.include("appcode"),
					Projections.excludeId());
			String cd = "";
			if(config.getCrossDomain() != null)
			{
				cd = ObjectSerializable.toXML(config.getCrossDomain());
					// TODO Auto-generated catch block
				
			}
			Bson bson = Filters.eq("appcode",config.getAppcode());
			Document object = sessionconf.find(bson ).projection(projectionFields).first();
			Document record = new Document("appcode",config.getAppcode())
			 
			.append("cookiename",config.getCookiename())
			
			.append("crossDomain", cd)
			.append("domain", config.getDomain());
			if(config.getScanStartTime() != null)
				record.append("scanStartTime", config.getScanStartTime().getTime());
			 record.append("sessionListeners", config.getSessionListeners())
			.append("sessionscaninterval", config.getSessionscaninterval())
			.append("sessionStore", config.getSessionStore())
			.append("sessionTimeout",config.getSessionTimeout())
			.append("httpOnly", config.isHttpOnly())
			.append("secure", config.isSecure())
			.append("serialType", config.getSerialType())
			.append("sessionidGeneratorPlugin", config.getSessionidGeneratorPlugin())
			.append("monitorAttributes", config.getMonitorAttributes())
			.append("storeReadAttributes", config.isStoreReadAttributes())			
			.append("startLifeScan", config.isStartLifeScan()).append("monitorScope", config.getMonitorScope()).append("lazystore", config.isLazystore());
			 
			if(object == null)
			{
				Date date = new Date();
				record.append("createTime", date.getTime());
				record.append("updateTime", date.getTime());
				 
				MongoDB.insert(sessionconf,record);
			}
			else
			{
				Date date = new Date();
				 
				record.append("updateTime", date.getTime());
				MongoDB.updateOne(sessionconf, new BasicDBObject("appcode",config.getAppcode()) ,
						new BasicDBObject("$set",record));
			}
		} catch (Exception e) {
			log.error("",e);
		}
		
	}
	
	@Override
	public Session createSession(SessionBasicInfo sessionBasicInfo) {
		String sessionid = sessionBasicInfo.getSessionid();
		long creationTime = System.currentTimeMillis();
		long maxInactiveInterval = this.getSessionTimeout();
		long lastAccessedTime = creationTime;
	
		boolean isHttpOnly = StringUtil.hasHttpOnlyMethod()?SessionUtil.getSessionManager().isHttpOnly():false;
		boolean secure = SessionUtil.getSessionManager().isSecure();
		MongoCollection<Document> sessions =getAppSessionDBCollection( sessionBasicInfo.getAppKey());
		MongoDB.insert(sessions,new Document("sessionid",sessionid)
		.append("creationTime", creationTime)
		.append("maxInactiveInterval",maxInactiveInterval)
		.append("lastAccessedTime", lastAccessedTime)
		.append("_validate", true)
		.append("appKey", sessionBasicInfo.getAppKey()).append("referip", sessionBasicInfo.getReferip())
		.append("host", SimpleStringUtil.getHostIP())
		.append("requesturi", sessionBasicInfo.getRequesturi())
		.append("lastAccessedUrl", sessionBasicInfo.getRequesturi())
		.append("httpOnly",isHttpOnly)
		.append("secure", secure)
		.append("lastAccessedHostIP", SimpleStringUtil.getHostIP()));
		SimpleSessionImpl session = createSimpleSessionImpl();
		session.setMaxInactiveInterval(null,maxInactiveInterval,null);
		session.setAppKey(sessionBasicInfo.getAppKey());
		session.setCreationTime(creationTime);
		session.setLastAccessedTime(lastAccessedTime);
		session.setId(sessionid);
		session.setHost(SimpleStringUtil.getHostIP());
		session.setValidate(true);
		session.setRequesturi(sessionBasicInfo.getRequesturi());
		session.setLastAccessedUrl(sessionBasicInfo.getRequesturi());
		session.setSecure(secure);
		session.setHttpOnly(isHttpOnly);
		session.setLastAccessedHostIP(SimpleStringUtil.getHostIP());
		return session;
	}
	
	
	

	@Override
	public Object getAttribute(String appKey,String contextpath,String sessionID, String attribute,Session session) {
		MongoCollection<Document> sessions =getAppSessionDBCollection( appKey);
//		BasicDBObject keys = new BasicDBObject();
//		attribute = MongoDBHelper.converterSpecialChar( attribute);
//		keys.put(attribute, 1);

		// Creates instructions to project two document fields
		attribute = MongoDBHelper.converterSpecialChar( attribute);
		Bson projectionFields = Projections.fields(
				Projections.include(attribute),
				Projections.excludeId());

		Document obj = sessions.find(new BasicDBObject("sessionid",sessionID).append("_validate", true)).projection(projectionFields).first();
		if(obj == null)
			return null;		
		return SessionUtil.unserial((String)obj.get(attribute));
//		return null;
	}

//	@Override
//	public Enumeration getAttributeNames(String appKey,String contextpath,String sessionID) {
////		DBCollection sessions =getAppSessionDBCollection( appKey);
////		
////		DBObject obj = sessions.findOne(new BasicDBObject("sessionid",sessionID));
////		
////		if(obj == null)
////			throw new SessionException("SessionID["+sessionID+"],appKey["+appKey+"] do not exist or is invalidated!" );
////		String[] valueNames = null;
////		if(obj.keySet() != null)
////		{
////			return obj.keySet().iterator();
////		}
////		throw new java.lang.UnsupportedOperationException();
//		String[] names = getValueNames(appKey,contextpath,sessionID);
//		
//		return SimpleStringUtil.arryToenum(names);
//		
//	}

	
	@Override
	public void updateLastAccessedTime(String appKey,String sessionID, long lastAccessedTime,String lastAccessedUrl,int MaxInactiveInterval) {
		MongoCollection<Document> sessions =getJOURNALEDAppSessionDBCollection( appKey);
		MongoDB.updateOne(sessions, new BasicDBObject("sessionid",sessionID).append("_validate", true), new BasicDBObject("$set",new BasicDBObject("lastAccessedTime", lastAccessedTime).append("lastAccessedUrl", lastAccessedUrl).append("lastAccessedHostIP", SimpleStringUtil.getHostIP())));
//		try
//		{
//			WriteResult wr = sessions.update(new BasicDBObject("sessionid",sessionID).append("_validate", true), new BasicDBObject("$set",new BasicDBObject("lastAccessedTime", lastAccessedTime).append("lastAccessedUrl", lastAccessedUrl).append("lastAccessedHostIP", SimpleStringUtil.getHostIP())));
//			System.out.println("wr.getN():"+wr.getN());
//			System.out.println("wr:"+wr);
//			System.out.println("wr.getLastConcern():"+wr.getLastConcern());
//		}
//		catch(WriteConcernException e)
//		{
//			log.debug("updateLastAccessedTime",e);
//		}
	}

	@Override
	public long getLastAccessedTime(String appKey,String sessionID) {
		MongoCollection<Document> sessions =getAppSessionDBCollection( appKey);
//		BasicDBObject keys = new BasicDBObject();
//		keys.put("lastAccessedTime", 1);
		Bson projectionFields = Projections.fields(
				Projections.include("lastAccessedTime"),
				Projections.excludeId());

		Document obj = sessions.find(new BasicDBObject("sessionid",sessionID)).projection(projectionFields).first();
		if(obj == null)
			throw new SessionException("SessionID["+sessionID+"],appKey["+appKey+"] do not exist or is invalidated!" );
		return (Long)obj.get("lastAccessedTime");
	}

	@Override
	public String[] getValueNames(String appKey,String contextpath,String sessionID,Map<String,Object> localAttributes) {

		MongoCollection<Document> sessions =getAppSessionDBCollection( appKey);

		Document obj = sessions.find(new BasicDBObject("sessionid",sessionID)).first();
		
		if(obj == null)
			throw new SessionException("SessionID["+sessionID+"],appKey["+appKey+"] do not exist or is invalidated!" );
		String[] valueNames = null;
		if(obj.keySet() != null)
		{
//			valueNames = new String[obj.keySet().size()];
			List<String> temp = localAttributes != null?
					_getAttributeNamesRecoverSpecialChars(obj.keySet().iterator(),  appKey,  contextpath ,localAttributes):
						_getAttributeNamesRecoverSpecialChars(obj.keySet().iterator(),  appKey,  contextpath );
//			List<String> temp = new ArrayList<String>();
//			Iterator<String> keys = obj.keySet().iterator();
//			while(keys.hasNext())
//			{
//				String tempstr = keys.next();
//				if(!MongoDBHelper.filter(tempstr))
//				{
//					tempstr = SessionHelper.dewraperAttributeName(appKey, contextpath, tempstr);
//					if(tempstr != null)
//					{
//						temp.add(tempstr);
//					}
//				}
//			}
			valueNames = new String[temp.size()];
			valueNames = temp.toArray(valueNames);
			
		}
		return valueNames ;
	}
	
	
	@Override
	public Enumeration getAttributeNames(String appKey,String contextpath,String sessionID,Map<String,Object> localAttributes) {

		MongoCollection<Document> sessions =getAppSessionDBCollection( appKey);

		Document obj = sessions.find(new BasicDBObject("sessionid",sessionID)).first();
		
		if(obj == null)
			throw new SessionException("SessionID["+sessionID+"],appKey["+appKey+"] do not exist or is invalidated!" );
		Enumeration<String> valueNames = null;
		if(obj.keySet() != null)
		{
//			valueNames = new String[obj.keySet().size()];
			List<String> temp = localAttributes != null?
					_getAttributeNamesRecoverSpecialChars(obj.keySet().iterator(),  appKey,  contextpath ,localAttributes):
						_getAttributeNamesRecoverSpecialChars(obj.keySet().iterator(),  appKey,  contextpath );
//			Iterator<String> keys = obj.keySet().iterator();
//			while(keys.hasNext())
//			{
//				String tempstr = keys.next();
//				if(!MongoDBHelper.filter(tempstr))
//				{
//					tempstr = SessionHelper.dewraperAttributeName(appKey, contextpath, tempstr);
//					if(tempstr != null)
//					{
//						temp.add(tempstr);
//					}
//				}
//			}
//			valueNames = new String[temp.size()];
//			valueNames = temp.toArray(valueNames);
			valueNames = new AttributeNamesEnumeration<String>(temp.iterator());
		}
		return valueNames ;
	}

	@Override
	public void invalidate(SimpleHttpSession session,String appKey,String contextpath,String sessionID) {
		MongoCollection<Document> sessions = getAppSessionDBCollection( appKey);
//		sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject("_validate", false)));
		MongoDB.remove(sessions,new BasicDBObject("sessionid",sessionID));
//		return session;
		
	}

	@Override
	public boolean isNew(String appKey,String sessionID) {
		MongoCollection<Document> sessions =getAppSessionDBCollection( appKey);
//		BasicDBObject keys = new BasicDBObject();
//		keys.put("lastAccessedTime", 1);
//		keys.put("creationTime", 1);
		Bson projectionFields = Projections.fields(
				Projections.include("lastAccessedTime","creationTime"),
				Projections.excludeId());
		Document obj = sessions.find(new BasicDBObject("sessionid",sessionID)).projection(projectionFields).first();
		
		if(obj == null)
			throw new SessionException("SessionID["+sessionID+"],appKey["+appKey+"] do not exist or is invalidated!" );
		 long lastAccessedTime =(Long)obj.get("lastAccessedTime");
		 long creationTime =(Long)obj.get("creationTime");
		 return creationTime == lastAccessedTime;
	}

	@Override
	public void removeAttribute(SimpleHttpSession session,String appKey,String contextpath,String sessionID, String attribute) {
		MongoCollection<Document> sessions = getJOURNALEDAppSessionDBCollection( appKey);
//		if(SessionHelper.haveSessionListener())
//		{
//			List<String> list = new ArrayList<String>();
//	//		attribute = converterSpecialChar( attribute);
//			list.add(attribute);
////			Session value = getSession(appKey, contextpath, sessionID,list);
//			MongoDB.update(sessions, new BasicDBObject("sessionid",sessionID), new BasicDBObject("$unset",new BasicDBObject(list.get(0), 1)));
////			sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$unset",new BasicDBObject(list.get(0), 1)));
//			
//		}
//		else
		{
			attribute = MongoDBHelper.converterSpecialChar(attribute);
//			sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$unset",new BasicDBObject(attribute, 1)));
			MongoDB.updateOne(sessions, new BasicDBObject("sessionid",sessionID), new BasicDBObject("$unset",new BasicDBObject(attribute, 1)));
			//sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject(attribute, null)));
			
		}
		
	}
	@Override
	public void submit(Session session,String appkey) {
		Map<String, ModifyValue> modifyattributes = session.getModifyattributes();
		
		if(modifyattributes != null && modifyattributes.size() > 0)
		{
			MongoCollection<Document> sessions = getJOURNALEDAppSessionDBCollection(appkey );
			Iterator<Entry<String, ModifyValue>> it = modifyattributes.entrySet().iterator();
			BasicDBObject record = null;//new BasicDBObject("lastAccessedTime", lastAccessedTime).append("lastAccessedUrl", lastAccessedUrl).append("lastAccessedHostIP", SimpleStringUtil.getHostIP())),WriteConcern.JOURNAL_SAFE);
			String attribute = null;
			BasicDBObject removerecord = null;
			ModifyValue  value = null;
			while(it.hasNext())
			{
				Entry<String, ModifyValue> entry = it.next();
				
				value = entry.getValue();
				if(value.getValuetype() == ModifyValue.type_base)//session 基本信息
				{
					if(record == null)
					{
						record = new BasicDBObject(entry.getKey(), value.getValue()); 
					}
					else
					{
						record.append(entry.getKey(), value.getValue());
					}
				}
				else//session数据
				{
					attribute = MongoDBHelper.converterSpecialChar(entry.getKey());
					if(value.getOptype() == ModifyValue.type_add  )
					{
						if(record == null)
						{
							record = new BasicDBObject(attribute, value.getValue()); 
						}
						else
						{
							record.append(attribute, value.getValue());
						}
					}
					else if(value.getOptype() == ModifyValue.type_read)
					{
						if(record == null)
						{
							record = new BasicDBObject(attribute, SessionUtil.serial(value.getValue())); 
						}
						else
						{
							record.append(attribute, SessionUtil.serial(value.getValue()));
						}
					}
					else
					{
						if(removerecord == null)
						{
							removerecord = new BasicDBObject(attribute, 1); 
						}
						else
						{
							removerecord.append(attribute, 1);
						}
					}
				}
				
				
			}
			BasicDBObject obj = new BasicDBObject();
			if(record != null)
				obj.append("$set",record);
			if(removerecord != null)
			{
				obj.append("$unset", removerecord);
			}
			MongoDB.updateOne(sessions, new BasicDBObject("sessionid",session.getId()), obj);
			
		}
		
	}
	
	@Override
	public void addAttribute(SimpleHttpSession session,String appKey,String contextpath,String sessionID, String attribute, Object value) {
		attribute = MongoDBHelper.converterSpecialChar( attribute);
		MongoCollection<Document> sessions = getJOURNALEDAppSessionDBCollection( appKey);
//		Session session = getSession(appKey,contextpath, sessionID);
//		sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject(attribute, value)));
		MongoDB.updateOne(sessions,new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject(attribute, value)));
//		return session;
		
	}
	
	public void setMaxInactiveInterval(SimpleHttpSession session, String appKey, String sessionID, long maxInactiveInterval,String contextpath)
	{
		MongoCollection<Document> sessions = getJOURNALEDAppSessionDBCollection( appKey);
//		Session session = getSession(appKey,contextpath, sessionID);
//		sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject(attribute, value)));
		MongoDB.updateOne(sessions,new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject("maxInactiveInterval", maxInactiveInterval)));
	}
	private Session getSession(String appKey,String contextpath, String sessionid,List<String> attributeNames) {
		MongoCollection<Document> sessions =getAppSessionDBCollection( appKey);
		List<String> keys = new ArrayList();
		keys.add("creationTime");
		keys.add("maxInactiveInterval");
		keys.add("lastAccessedTime");
		keys.add("_validate");
		keys.add("appKey");
		keys.add("referip");
		keys.add("host");
		keys.add("requesturi");
		keys.add("lastAccessedUrl");
		keys.add("secure");
		keys.add("httpOnly");
		keys.add("lastAccessedHostIP");


//		.append("lastAccessedHostIP", SimpleStringUtil.getHostIP())
		List<String> copy = new ArrayList<String>(attributeNames);
		for(int i = 0; attributeNames != null && i < attributeNames.size(); i ++)
		{
			String r = MongoDBHelper.converterSpecialChar(attributeNames.get(i));
			attributeNames.set(i, r);
			keys.add(r);
		}

		Bson projectionFields = Projections.fields(
				Projections.include(keys),
				Projections.excludeId());
		
		Document object = sessions.find(new BasicDBObject("sessionid",sessionid).append("_validate", true)).projection(projectionFields).first();
		if(object != null)
		{
			SimpleSessionImpl session = createSimpleSessionImpl();
			session.setMaxInactiveInterval(null,(Long)object.get("maxInactiveInterval"),contextpath);
			session.setAppKey(appKey);
			session.setCreationTime((Long)object.get("creationTime"));
			session.setLastAccessedTime((Long)object.get("lastAccessedTime"));
			session.setId(sessionid);
			session.setReferip((String)object.get("referip"));
			session.setValidate((Boolean)object.get("_validate"));
			session.setHost((String)object.get("host"));
//			session._setSessionStore(this);
			session.setRequesturi((String)object.get("requesturi"));
			session.setLastAccessedUrl((String)object.get("lastAccessedUrl"));
			session.setLastAccessedHostIP((String)object.get("lastAccessedHostIP"));
			Object secure_ = object.get("secure");
			if(secure_ != null)
			{
				session.setSecure((Boolean)secure_);
			}
			Object httpOnly_ = object.get("httpOnly");
			if(httpOnly_ != null)
			{
				session.setHttpOnly((Boolean)httpOnly_);
			}
			else
			{
				session.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionUtil.getSessionManager().isHttpOnly():false);
			}
			Map<String,Object> attributes = new HashMap<String,Object>();
			for(int i = 0; attributeNames != null && i < attributeNames.size(); i ++)
			{
				String name = attributeNames.get(i);
				Object value = object.get(name);
				try {
					String temp = SessionUtil.dewraperAttributeName(appKey, contextpath, copy.get(i));		
					if(temp != null)
						attributes.put(temp, SessionUtil.unserial((String)value));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			session.setAttributes(attributes);
			return session;
		}
		else
		{
			return null;
		}
	}
	@Override
	public SessionConfig getSessionConfig(String appkey) {
		if(appkey == null || appkey.equals(""))
			return null;
		MongoCollection<Document>  sessionconf = getConfigSessionDBCollection();
		List<String> keys = new ArrayList();
		keys.add("appcode");
		keys.add("cookiename");
		keys.add("crossDomain");
		keys.add("domain");
		keys.add("scanStartTime");
		keys.add("sessionListeners");
		keys.add("sessionscaninterval");
		keys.add("sessionStore");
		keys.add("sessionTimeout");
		keys.add("httpOnly");
		keys.add("startLifeScan");
		keys.add("secure");
		keys.add("monitorAttributes");
		keys.add("createTime");
		keys.add("updateTime");
		keys.add("monitorScope");
		keys.add("lazystore");
		keys.add("serialType");
		keys.add("sessionidGeneratorPlugin");
		keys.add("storeReadAttributes");

		Bson projectionFields = Projections.fields(
				Projections.include(keys),
				Projections.excludeId());


		Document object = sessionconf.find(new BasicDBObject("appcode",appkey) ).projection(projectionFields).first();
		 
		 
		 
		if(object != null)
		{
			SessionConfig sessionConfig = new SessionConfig();
			sessionConfig.setAppcode(appkey);
			sessionConfig.setCookiename((String)object.get("cookiename"));
			String cd_ = (String)object.get("crossDomain");
			if(cd_ != null && !cd_.equals(""))
			{
				CrossDomain cd = ObjectSerializable.toBean((String)object.get("crossDomain"), CrossDomain.class);
				sessionConfig.setCrossDomain(cd);
			}
			sessionConfig.setDomain((String)object.get("domain"));
			Long st = (Long)object.get("scanStartTime");
			if(st!= null)
				sessionConfig.setScanStartTime(new Date(st.longValue()));
			sessionConfig.setSessionListeners((String)object.get("sessionListeners")); 
			sessionConfig.setMonitorScope((String)object.get("monitorScope"));
			sessionConfig.setSessionscaninterval((Long)object.get("sessionscaninterval")); 
			sessionConfig.setSessionStore((String)object.get("sessionStore"));
			sessionConfig.setSessionTimeout((Long)object.get("sessionTimeout")); 
			sessionConfig.setHttpOnly((Boolean)object.get("httpOnly")); 
			sessionConfig.setStartLifeScan((Boolean)object.get("startLifeScan")); 
			sessionConfig.setSecure((Boolean)object.get("secure")); 
			sessionConfig.setMonitorAttributes((String)object.get("monitorAttributes"));
			sessionConfig.setCreateTime(new Date((Long)object.get("createTime")));
			sessionConfig.setUpdateTime(new Date((Long)object.get("updateTime")));
			sessionConfig.setSessionidGeneratorPlugin((String)object.get("sessionidGeneratorPlugin"));
			Object storeReadAttributes = object.get("storeReadAttributes");
			if(storeReadAttributes != null)
				sessionConfig.setStoreReadAttributes((Boolean)storeReadAttributes);
			sessionConfig.setSerialType((String)object.get("serialType"));
			Boolean lazystore = (Boolean)object.get("lazystore");
			if(lazystore != null)
				sessionConfig.setLazystore(lazystore); 
			 
			return sessionConfig;
		}
		else
		{
			return null;
		}
		 
	}
	@Override
	public Session getSession(String appKey,String contextpath, String sessionid) {
		MongoCollection<Document>  sessions =getAppSessionDBCollection( appKey);
		List<String> keys = new ArrayList();
		keys.add("creationTime");
		keys.add("maxInactiveInterval");
		keys.add("lastAccessedTime");
		keys.add("_validate");
		keys.add("appKey");
		keys.add("referip");
		keys.add("host");
		keys.add("requesturi");
		keys.add("lastAccessedUrl");
		keys.add("secure");
		keys.add("httpOnly");
		keys.add("lastAccessedHostIP");
		Bson projectionFields = Projections.fields(
				Projections.include(keys),
				Projections.excludeId());
		Document object = sessions.find(new BasicDBObject("sessionid",sessionid).append("_validate", true)).projection(projectionFields).first();
		if(object != null)
		{
			SimpleSessionImpl session = createSimpleSessionImpl();
			session.setMaxInactiveInterval(null,(Long)object.get("maxInactiveInterval"),contextpath);
			session.setAppKey(appKey);
			session.setCreationTime((Long)object.get("creationTime"));
			session.setLastAccessedTime((Long)object.get("lastAccessedTime"));
			session.setId(sessionid);
			session.setReferip((String)object.get("referip"));
			session.setValidate((Boolean)object.get("_validate"));
			session.setHost((String)object.get("host"));
//			session._setSessionStore(this);
			session.setRequesturi((String)object.get("requesturi"));
			session.setLastAccessedUrl((String)object.get("lastAccessedUrl"));
			Object secure_ = object.get("secure");
			if(secure_ != null)
			{
				session.setSecure((Boolean)secure_);
			}
			Object httpOnly_ = object.get("httpOnly");
			if(httpOnly_ != null)
			{
				session.setHttpOnly((Boolean)httpOnly_);
			}
			else
			{
				session.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionUtil.getSessionManager().isHttpOnly():false);
			}
			session.setLastAccessedHostIP((String)object.get("lastAccessedHostIP"));
			return session;
		}
		else
		{
			return null;
		}
	}
	@Override
	public SimpleHttpSession createHttpSession(
			SessionBasicInfo sessionBasicInfo, SessionBuilder sessionBuilder) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}
	@Override
	public Long expired(String appkey,String sessionid,int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	
//	private Session getSessionAndRemove(String appKey,String contextpath, String sessionid) {
//		DBCollection sessions =getAppSessionDBCollection( appKey);
//	
//		
//		if(SessionHelper.haveSessionListener())
//		{
//			DBObject object = MongoDB.findAndRemove(sessions,new BasicDBObject("sessionid",sessionid));
//			if(object != null)
//			{
//				SimpleSessionImpl session = new SimpleSessionImpl();
//				session.setMaxInactiveInterval((Long)object.get("maxInactiveInterval"));
//				session.setAppKey(appKey);
//				session.setCreationTime((Long)object.get("creationTime"));
//				session.setLastAccessedTime((Long)object.get("lastAccessedTime"));
//				session.setId(sessionid);
//				session.setReferip((String)object.get("referip"));
//				session.setValidate((Boolean)object.get("_validate"));
//				session.setHost((String)object.get("host"));
//				session.setRequesturi((String)object.get("requesturi"));
//				session.setLastAccessedUrl((String)object.get("lastAccessedUrl"));
//				session.setLastAccessedHostIP((String)object.get("lastAccessedHostIP"));
//				Object secure_ = object.get("secure");
//				if(secure_ != null)
//				{
//					session.setSecure((Boolean)secure_);
//				}
//				Object httpOnly_ = object.get("httpOnly");
//				if(httpOnly_ != null)
//				{
//					session.setHttpOnly((Boolean)httpOnly_);
//				}	
//				else
//				{
//					session.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionHelper.getSessionManager().isHttpOnly():false);
//				}
//	//			session._setSessionStore(this);
//				Map<String,Object> attributes = MongoDBHelper.toMap(appKey, contextpath,object,true);
//				session.setAttributes(attributes);
//				return session;
//			}
//			else
//			{
////				sessions.remove(new BasicDBObject("sessionid",sessionid));
//				return null;
//			}
//		}
//		else
//		{
//			MongoDB.remove(sessions,new BasicDBObject("sessionid",sessionid));
//			return null;
//		}
//	}


}


