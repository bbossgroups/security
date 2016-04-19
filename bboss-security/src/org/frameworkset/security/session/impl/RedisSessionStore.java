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

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.frameworkset.nosql.redis.RedisFactory;
import org.frameworkset.nosql.redis.RedisHelper;
import org.frameworkset.security.session.AttributeNamesEnumeration;
import org.frameworkset.security.session.InvalidateCallback;
import org.frameworkset.security.session.Session;
import org.frameworkset.security.session.SessionBasicInfo;
import org.frameworkset.security.session.SimpleHttpSession;
import org.frameworkset.security.session.statics.SessionConfig;
import org.frameworkset.soa.ObjectSerializable;

import com.frameworkset.util.SimpleStringUtil;
import com.frameworkset.util.StringUtil;

public class RedisSessionStore extends BaseSessionStore{
	
	private static Logger log = Logger.getLogger(RedisSessionStore.class);
	public RedisSessionStore()
	{
		
	}
	public void destory()
	{
		 
		
	}
	@Override
	public void livecheck() {
//		Set<String> apps = MongoDBHelper.getSessionDBCollectionNames();
//		if(apps == null || apps.size() == 0)
//			return;
//		long curtime = System.currentTimeMillis();
//		StringBuffer wherefun = new StringBuffer();
//		wherefun.append("function() ")
//				.append("{")	
//				 .append(" if(!this._validate) return true;")
//				 .append(" if(this.maxInactiveInterval <= 0) return false;")
//			    .append(" if(this.lastAccessedTime + this.maxInactiveInterval < ").append(curtime).append(")")
//			    .append("{")
//				.append("return true;")				
//				.append("}")
//				.append("else")
//				.append(" {")
//				.append(" return false;")		
//				.append("}")
//				.append("}");
//		String temp = wherefun.toString();
//		Iterator<String> itr = apps.iterator();
//		while(itr.hasNext())
//		{
//			String app = itr.next();
//			if(app.endsWith("_sessions"))
//			{
//				DBCollection appsessions = MongoDBHelper.getSessionCollection(app);
//				MongoDB.remove(appsessions,new BasicDBObject("$where",temp),WriteConcern.UNACKNOWLEDGED);
//			}
//		}
		
	}
	public static final String config_prefix = "bboss:session:config:";
	public static final String table_prefix = "bboss:session:table:";
	public static final String sessionapps = "bboss:session:apps";
	private String getAPPConfigKey(String appcode)
	{
		return config_prefix+appcode;  
	}
	
	private String getAPPTable(String appcode)
	{
		return table_prefix+appcode;  
	}
	
	public static String getAPPSessionKey(String appcode,String sessionid)
	{
		return new StringBuilder().append(table_prefix).append(appcode).append(":").append(sessionid).toString();  
	}
	
	public void saveSessionConfig(SessionConfig config)
	{
		
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			redisHelper.sadd(sessionapps, config.getAppcode());
			String appkey = getAPPConfigKey(config.getAppcode());
			String createTime = redisHelper.hget(appkey,"createTime");
 		 
			if(createTime == null)
			{
				Date date = new Date();
				config.setCreateTime(date);
				config.setUpdateTime(date);		
				createTime = date.getTime()+"";
			}
			else
			{
				
				
				Date date = new Date();
				config.setCreateTime(new Date(Long.parseLong(createTime)));
				config.setUpdateTime(date);

					
			}
			String configxml = ObjectSerializable.toXML(config);
			Map<String,String> data = new HashMap<String,String>();
			data.put("config", configxml);
			data.put("createTime", createTime);
			redisHelper.hmset(appkey, data);
			
		} catch (Exception e) {
			log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
		 
		
		
		
		
		
	}
	
	@Override
	public Session createSession(SessionBasicInfo sessionBasicInfo) {
		String sessionid = this.randomToken();
		long creationTime = System.currentTimeMillis();
		long maxInactiveInterval = this.getSessionTimeout();
		long lastAccessedTime = creationTime;
	
		boolean isHttpOnly = StringUtil.hasHttpOnlyMethod()?SessionHelper.getSessionManager().isHttpOnly():false;
		boolean secure = SessionHelper.getSessionManager().isSecure();
		RedisHelper redisHelper = redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			String sessionkey = getAPPSessionKey(sessionBasicInfo.getAppKey(),sessionid);
			Map<String,String> record = new HashMap<String,String>(); 
			record.put("sessionid",sessionid);
			record.put("creationTime", creationTime+"");
			record.put("maxInactiveInterval",maxInactiveInterval+"");
			record.put("lastAccessedTime", lastAccessedTime+"");
			record.put("_validate", true+"");
			record.put("appKey", sessionBasicInfo.getAppKey());
			record.put("referip", sessionBasicInfo.getReferip());
			record.put("host", SimpleStringUtil.getHostIP());
			record.put("requesturi", sessionBasicInfo.getRequesturi());
			record.put("lastAccessedUrl", sessionBasicInfo.getRequesturi());
			record.put("httpOnly",isHttpOnly+"");
			record.put("secure", secure+"");
			record.put("lastAccessedHostIP", SimpleStringUtil.getHostIP());
			redisHelper.hmset(sessionkey, record);
			if(maxInactiveInterval > 0)
			{
				redisHelper.expire(sessionkey, (int)(maxInactiveInterval /1000));
			}
			 
			
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
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
		 
		return null;
		
	}
	
	
	

	@Override
	public Object getAttribute(String appKey,String contextpath,String sessionID, String attribute) {
		String sessionkey = getAPPSessionKey(appKey,sessionID);
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			String value = redisHelper.hget(sessionkey, attribute);
			if(value == null || value.equals(""))
				return null;
			return SessionHelper.unserial(value);
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
		 
			return null;
		
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
		String sessionKey = this.getAPPSessionKey(appKey, sessionID);
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			Map<String,String> values = new HashMap<String,String>();
			values.put("lastAccessedTime", lastAccessedTime+"");
			values.put("lastAccessedUrl", lastAccessedUrl);
			values.put("lastAccessedHostIP", SimpleStringUtil.getHostIP());			
			redisHelper.hmset(sessionKey, values);
			redisHelper.expire(sessionKey, MaxInactiveInterval);
			 
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
		 
//		DBCollection sessions =getAppSessionDBCollection( appKey);
//		
//		MongoDB.update(sessions, new BasicDBObject("sessionid",sessionID).append("_validate", true), new BasicDBObject("$set",new BasicDBObject("lastAccessedTime", lastAccessedTime).append("lastAccessedUrl", lastAccessedUrl).append("lastAccessedHostIP", SimpleStringUtil.getHostIP())),WriteConcern.JOURNAL_SAFE);
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
		String sessionKey = this.getAPPSessionKey(appKey, sessionID);
		RedisHelper  redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			
			String lastAccessedTime = redisHelper.hget(sessionKey, "lastAccessedTime");
			if(lastAccessedTime != null)
				return Long.parseLong(lastAccessedTime);
			 
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
		return 0;
		 
	}

	@Override
	public String[] getValueNames(String appKey,String contextpath,String sessionID) {
		String sessionKey = this.getAPPSessionKey(appKey, sessionID);
		String[] valueNames = null;
		RedisHelper redisHelper = null;
			try
			{
				redisHelper = RedisFactory.getRedisHelper();
			Set<String> keys = redisHelper.hkeys(sessionKey);
			 if(keys != null && keys.size() > 0)
			 {
				 List<String> temp = _getAttributeNames(keys.iterator(),  appKey,  contextpath);
				 valueNames = new String[temp.size()];
					valueNames = temp.toArray(valueNames);
			 }
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
		 
		
		return valueNames ;
	}
	
	
	@Override
	public Enumeration getAttributeNames(String appKey,String contextpath,String sessionID) {
		String sessionKey = this.getAPPSessionKey(appKey, sessionID);
		
		Enumeration<String> valueNames = null;
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			Set<String> keys = redisHelper.hkeys(sessionKey);
			 if(keys != null && keys.size() > 0)
			 {
				 List<String> temp = _getAttributeNames(keys.iterator(),  appKey,  contextpath);
				 valueNames = new AttributeNamesEnumeration<String>(temp.iterator());
			 }
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
		 
		
		return valueNames ;
		
	}

	@Override
	public void invalidate(SimpleHttpSession session,String appKey,String contextpath,String sessionID) {
		String sessionKey = this.getAPPSessionKey(appKey, sessionID);
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			redisHelper.del(sessionKey);
			 
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
//		DBCollection sessions = getAppSessionDBCollection( appKey);		
////		sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject("_validate", false)));
//		MongoDB.remove(sessions,new BasicDBObject("sessionid",sessionID));
//		return session;
		
	}

	@Override
	public boolean isNew(String appKey,String sessionID) {
		String sessionKey = this.getAPPSessionKey(appKey, sessionID);
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			List<String> values = redisHelper.hmget(sessionKey, "lastAccessedTime","creationTime");
			if(values == null || values.get(1) == null )
				throw new SessionException("SessionID["+sessionID+"],appKey["+appKey+"] do not exist or is invalidated!" );
			return values.get(0).equals(values.get(1));
			
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
		return false;
//		DBCollection sessions =getAppSessionDBCollection( appKey);
//		BasicDBObject keys = new BasicDBObject();
//		keys.put("lastAccessedTime", 1);
//		keys.put("creationTime", 1);
//		DBObject obj = sessions.findOne(new BasicDBObject("sessionid",sessionID),keys);
//		
//		if(obj == null)
//			throw new SessionException("SessionID["+sessionID+"],appKey["+appKey+"] do not exist or is invalidated!" );
//		 long lastAccessedTime =(Long)obj.get("lastAccessedTime");
//		 long creationTime =(Long)obj.get("creationTime");
//		 return creationTime == lastAccessedTime;
	}

	@Override
	public void removeAttribute(SimpleHttpSession session,String appKey,String contextpath,String sessionID, String attribute) {
		String sessionKey = this.getAPPSessionKey(appKey, sessionID);
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			redisHelper.hdel(sessionKey, attribute);
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
//		DBCollection sessions = getAppSessionDBCollection( appKey);
////		if(SessionHelper.haveSessionListener())
////		{
////			List<String> list = new ArrayList<String>();
////	//		attribute = converterSpecialChar( attribute);
////			list.add(attribute);
//////			Session value = getSession(appKey, contextpath, sessionID,list);
////			MongoDB.update(sessions, new BasicDBObject("sessionid",sessionID), new BasicDBObject("$unset",new BasicDBObject(list.get(0), 1)));
//////			sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$unset",new BasicDBObject(list.get(0), 1)));
////			
////		}
////		else
//		{
//			attribute = MongoDBHelper.converterSpecialChar(attribute);
////			sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$unset",new BasicDBObject(attribute, 1)));
//			MongoDB.update(sessions, new BasicDBObject("sessionid",sessionID), new BasicDBObject("$unset",new BasicDBObject(attribute, 1)));
//			//sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject(attribute, null)));
//			
//		}
		
	}
	@Override
	public void submit(Session session,String appkey) {
		Map<String, ModifyValue> modifyattributes = session.getModifyattributes();
		
		if(modifyattributes != null && modifyattributes.size() > 0)
		{
			
			 
			Iterator<Entry<String, ModifyValue>> it = modifyattributes.entrySet().iterator();
			Map<String,String> record = null;//new BasicDBObject("lastAccessedTime", lastAccessedTime).append("lastAccessedUrl", lastAccessedUrl).append("lastAccessedHostIP", SimpleStringUtil.getHostIP())),WriteConcern.JOURNAL_SAFE);
//			String attribute = null;
			ModifyValue  value = null;
			List<String> removes = null;
			while(it.hasNext())
			{
				Entry<String, ModifyValue> entry = it.next();
				
				value = entry.getValue();
				if(value.getValuetype() == ModifyValue.type_base)//session 基本信息
				{
					if(record == null)
					{
						record = new HashMap<String,String>();//new BasicDBObject(entry.getKey(), value.getValue()); 
					}
					record.put(entry.getKey(), String.valueOf(value.getValue()));
					
					 
				}
				else//session数据
				{
//					attribute = MongoDBHelper.converterSpecialChar(entry.getKey());
					if(value.getOptype() == ModifyValue.type_add)
					{
						if(record == null)
						{
							record = new HashMap<String,String>();//new BasicDBObject(entry.getKey(), value.getValue()); 
						}
						if(value.getValue() != null)
							record.put(entry.getKey(), String.valueOf(value.getValue()));
						else
							record.put(entry.getKey(), null);
//						if(record == null)
//						{
//							record = new BasicDBObject(attribute, value.getValue()); 
//						}
//						else
//						{
//							record.append(attribute, value.getValue());
//						}
					}
					else
					{
						if(removes == null)
						{
							removes = new ArrayList<String>(); 
						}
						 
						removes.add(entry.getKey());
						 
					}
				}
				
				
			}
			RedisHelper redisHelper = null;
			try
			{
				redisHelper = RedisFactory.getRedisHelper();
				String sessionKey = getAPPSessionKey(appkey, session.getId());
				if(record != null && record.size() > 0)
				{
					redisHelper.hmset(sessionKey, record);
				}
				if(removes != null && removes.size() > 0)
				{
					String[] as = new String[removes.size()];
					redisHelper.hdel(sessionKey, removes.toArray(as));
				}
			}
			 catch (Exception e) {
					log.error("",e);
			}
			finally
			{
				if(redisHelper != null)
					redisHelper.release();
			} 
//			MongoDB.update(sessions, new BasicDBObject("sessionid",session.getId()), new BasicDBObject("$set",record));
			
		}
		
	}
	
	@Override
	public void addAttribute(SimpleHttpSession session,String appKey,String contextpath,String sessionID, String attribute, Object value) {
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			String sessionKey = this.getAPPSessionKey(appKey, session.getId());
			if(value != null)
				redisHelper.hset(sessionKey, attribute, String.valueOf(value));
			else
				redisHelper.hset(sessionKey, attribute, null);
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
//		attribute = MongoDBHelper.converterSpecialChar( attribute);
//		DBCollection sessions = getAppSessionDBCollection( appKey);	
////		Session session = getSession(appKey,contextpath, sessionID);
////		sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject(attribute, value)));
//		MongoDB.update(sessions,new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject(attribute, value)));
//		return session;
		
	}
	
	public void setMaxInactiveInterval(SimpleHttpSession session, String appKey, String sessionID, long maxInactiveInterval,String contextpath)
	{
		
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			String sessionKey = this.getAPPSessionKey(appKey, session.getId());
			
				redisHelper.hset(sessionKey, "maxInactiveInterval", String.valueOf(maxInactiveInterval));			
				redisHelper.expire(sessionKey, (int)maxInactiveInterval);
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
//		DBCollection sessions = getAppSessionDBCollection( appKey);	
////		Session session = getSession(appKey,contextpath, sessionID);
////		sessions.update(new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject(attribute, value)));
//		MongoDB.update(sessions,new BasicDBObject("sessionid",sessionID), new BasicDBObject("$set",new BasicDBObject("maxInactiveInterval", maxInactiveInterval)));
	}
	private Session getSession(String appKey,String contextpath, String sessionid,List<String> attributeNames) {
		RedisHelper  redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			String sessionKey = this.getAPPSessionKey(appKey, sessionid);
			List<String> fields = new ArrayList<String>();
			fields.add("creationTime");
			fields.add("maxInactiveInterval");
			fields.add("lastAccessedTime");
			fields.add("_validate");
//			fields.add("appKey");
			fields.add("referip");
			fields.add("host");
			fields.add("requesturi" );
			fields.add("lastAccessedUrl");
			fields.add("secure" );
			fields.add("httpOnly");
			fields.add("lastAccessedHostIP");
			fields.addAll(attributeNames);
			String[] keys = new String[fields.size()];
			List<String> values = redisHelper.hmget(sessionKey, fields.toArray(keys));
			if(values != null && values.get(0) != null)
			{
				SimpleSessionImpl session = createSimpleSessionImpl();
				session.setId(sessionid);
				session.setAppKey(appKey);
//				session.setCreationTime((Long)object.get("creationTime"));
				session.setCreationTime(Long.parseLong(values.get(0)));
//				session.setMaxInactiveInterval(null,(Long)object.get("maxInactiveInterval"),contextpath);
				session.setMaxInactiveInterval(null,Long.parseLong(values.get(1)),contextpath);
//				session.setLastAccessedTime((Long)object.get("lastAccessedTime"));
				session.setLastAccessedTime(Long.parseLong(values.get(2)));
				session.setValidate(Boolean.parseBoolean(values.get(3)));
				session.setReferip(values.get(4));
				
				session.setHost(values.get(5));
//				session._setSessionStore(this);
				session.setRequesturi(values.get(6));
				session.setLastAccessedUrl(values.get(7));
				
				String secure_ =values.get(8);
				if(secure_ != null)
				{
					session.setSecure(Boolean.parseBoolean(secure_));
				}
//				Object httpOnly_ = object.get("httpOnly");
				String httpOnly_ =values.get(9);
				if(httpOnly_ != null)
				{
					session.setHttpOnly(Boolean.parseBoolean(httpOnly_));
				}
				else
				{
					session.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionHelper.getSessionManager().isHttpOnly():false);
				}
				session.setLastAccessedHostIP(values.get(10));
				Map<String,Object> attributes = new HashMap<String,Object>();
				for(int i = 0; attributeNames != null && i < attributeNames.size(); i ++)
				{
					String name = attributeNames.get(i);
					String value = values.get(i+11);
					try {
						String temp = SessionHelper.dewraperAttributeName(appKey, contextpath, name);		
						if(temp != null)
							attributes.put(temp, SessionHelper.unserial((String)value));
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
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
		return null;
//		DBCollection sessions =getAppSessionDBCollection( appKey);
//		BasicDBObject keys = new BasicDBObject();
//		keys.put("creationTime", 1);
//		keys.put("maxInactiveInterval", 1);
//		keys.put("lastAccessedTime", 1);
//		keys.put("_validate", 1);
//		keys.put("appKey", 1);
//		keys.put("referip", 1);
//		keys.put("host", 1);
//		keys.put("requesturi",1);
//		keys.put("lastAccessedUrl", 1);
//		keys.put("secure",1);
//		keys.put("httpOnly", 1);
//		keys.put("lastAccessedHostIP", 1);
////		.append("lastAccessedHostIP", SimpleStringUtil.getHostIP())
//		List<String> copy = new ArrayList<String>(attributeNames);
//		for(int i = 0; attributeNames != null && i < attributeNames.size(); i ++)
//		{
//			String r = MongoDBHelper.converterSpecialChar(attributeNames.get(i));
//			attributeNames.set(i, r);
//			keys.put(r, 1);
//		}
//		
//		
//		
//		DBObject object = sessions.findOne(new BasicDBObject("sessionid",sessionid).append("_validate", true),keys);
//		if(object != null)
//		{
//			SimpleSessionImpl session = createSimpleSessionImpl();
//			session.setMaxInactiveInterval(null,(Long)object.get("maxInactiveInterval"),contextpath);
//			session.setAppKey(appKey);
//			session.setCreationTime((Long)object.get("creationTime"));
//			session.setLastAccessedTime((Long)object.get("lastAccessedTime"));
//			session.setId(sessionid);
//			session.setReferip((String)object.get("referip"));
//			session.setValidate((Boolean)object.get("_validate"));
//			session.setHost((String)object.get("host"));
////			session._setSessionStore(this);
//			session.setRequesturi((String)object.get("requesturi"));
//			session.setLastAccessedUrl((String)object.get("lastAccessedUrl"));
//			session.setLastAccessedHostIP((String)object.get("lastAccessedHostIP"));
//			Object secure_ = object.get("secure");
//			if(secure_ != null)
//			{
//				session.setSecure((Boolean)secure_);
//			}
//			Object httpOnly_ = object.get("httpOnly");
//			if(httpOnly_ != null)
//			{
//				session.setHttpOnly((Boolean)httpOnly_);
//			}
//			else
//			{
//				session.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionHelper.getSessionManager().isHttpOnly():false);
//			}
//			Map<String,Object> attributes = new HashMap<String,Object>();
//			for(int i = 0; attributeNames != null && i < attributeNames.size(); i ++)
//			{
//				String name = attributeNames.get(i);
//				Object value = object.get(name);
//				try {
//					String temp = SessionHelper.dewraperAttributeName(appKey, contextpath, copy.get(i));		
//					if(temp != null)
//						attributes.put(temp, SessionHelper.unserial((String)value));
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			session.setAttributes(attributes);
//			return session;
//		}
//		else
//		{
//			return null;
//		}
	}
	@Override
	public SessionConfig getSessionConfig(String appkey) {
		if(appkey == null || appkey.equals(""))
			return null;
		
		RedisHelper  redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			appkey = this.getAPPConfigKey(appkey);
		
			String configxml =redisHelper.hget(appkey, "config");
			 if(configxml == null || configxml.equals(""))
				 return null;
			 SessionConfig config = ObjectSerializable.toBean(configxml, SessionConfig.class);
			return config;
		} catch (Exception e) {
			log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
		 return null;
		 
	}
	@Override
	public Session getSession(String appKey,String contextpath, String sessionid) {
		RedisHelper  redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			String sessionKey = this.getAPPSessionKey(appKey, sessionid);
			List<String> fields = new ArrayList<String>();
			fields.add("creationTime");
			fields.add("maxInactiveInterval");
			fields.add("lastAccessedTime");
			fields.add("_validate");
//			fields.add("appKey");
			fields.add("referip");
			fields.add("host");
			fields.add("requesturi" );
			fields.add("lastAccessedUrl");
			fields.add("secure" );
			fields.add("httpOnly");
			fields.add("lastAccessedHostIP");
			 
			String[] keys = new String[fields.size()];
			List<String> values = redisHelper.hmget(sessionKey, fields.toArray(keys));
			if(values != null && values.get(0) != null)
			{
				SimpleSessionImpl session = createSimpleSessionImpl();
				session.setId(sessionid);
				session.setAppKey(appKey);
//				session.setCreationTime((Long)object.get("creationTime"));
				session.setCreationTime(Long.parseLong(values.get(0)));
//				session.setMaxInactiveInterval(null,(Long)object.get("maxInactiveInterval"),contextpath);
				session.setMaxInactiveInterval(null,Long.parseLong(values.get(1)),contextpath);
//				session.setLastAccessedTime((Long)object.get("lastAccessedTime"));
				session.setLastAccessedTime(Long.parseLong(values.get(2)));
				session.setValidate(Boolean.parseBoolean(values.get(3)));
				session.setReferip(values.get(4));
				
				session.setHost(values.get(5));
//				session._setSessionStore(this);
				session.setRequesturi(values.get(6));
				session.setLastAccessedUrl(values.get(7));
				
				String secure_ =values.get(8);
				if(secure_ != null)
				{
					session.setSecure(Boolean.parseBoolean(secure_));
				}
//				Object httpOnly_ = object.get("httpOnly");
				String httpOnly_ =values.get(9);
				if(httpOnly_ != null)
				{
					session.setHttpOnly(Boolean.parseBoolean(httpOnly_));
				}
				else
				{
					session.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionHelper.getSessionManager().isHttpOnly():false);
				}
				session.setLastAccessedHostIP(values.get(10));
				 
				return session;
			}
			else
			{
				return null;
			}
		}
		 catch (Exception e) {
				log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		} 
		return null;
//		DBCollection sessions =getAppSessionDBCollection( appKey);
//		BasicDBObject keys = new BasicDBObject();
//		keys.put("creationTime", 1);
//		keys.put("maxInactiveInterval", 1);
//		keys.put("lastAccessedTime", 1);
//		keys.put("_validate", 1);
//		keys.put("appKey", 1);
//		keys.put("referip", 1);
//		keys.put("host", 1);
//		keys.put("requesturi", 1);
//		keys.put("lastAccessedUrl", 1);
//		keys.put("secure",1);
//		keys.put("httpOnly", 1);
//		keys.put("lastAccessedHostIP", 1);
//		DBObject object = sessions.findOne(new BasicDBObject("sessionid",sessionid).append("_validate", true),keys);
//		if(object != null)
//		{
//			SimpleSessionImpl session = createSimpleSessionImpl();
//			session.setMaxInactiveInterval(null,(Long)object.get("maxInactiveInterval"),contextpath);
//			session.setAppKey(appKey);
//			session.setCreationTime((Long)object.get("creationTime"));
//			session.setLastAccessedTime((Long)object.get("lastAccessedTime"));
//			session.setId(sessionid);
//			session.setReferip((String)object.get("referip"));
//			session.setValidate((Boolean)object.get("_validate"));
//			session.setHost((String)object.get("host"));
////			session._setSessionStore(this);
//			session.setRequesturi((String)object.get("requesturi"));
//			session.setLastAccessedUrl((String)object.get("lastAccessedUrl"));
//			Object secure_ = object.get("secure");
//			if(secure_ != null)
//			{
//				session.setSecure((Boolean)secure_);
//			}
//			Object httpOnly_ = object.get("httpOnly");
//			if(httpOnly_ != null)
//			{
//				session.setHttpOnly((Boolean)httpOnly_);
//			}
//			else
//			{
//				session.setHttpOnly(StringUtil.hasHttpOnlyMethod()?SessionHelper.getSessionManager().isHttpOnly():false);
//			}
//			session.setLastAccessedHostIP((String)object.get("lastAccessedHostIP"));
//			return session;
//		}
//		else
//		{
//			return null;
//		}
	}
	@Override
	public SimpleHttpSession createHttpSession(ServletContext servletContext,
			SessionBasicInfo sessionBasicInfo, String contextpath,InvalidateCallback invalidateCallback) {
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
		if(appkey == null || appkey.equals(""))
			return null;
		
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			String sessionkey = this.getAPPSessionKey(appkey,sessionid);
		
			return redisHelper.expire(sessionkey, timeout);
			 
			
		} catch (Exception e) {
			log.error("",e);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
		 return null;
	}

	
 


}


