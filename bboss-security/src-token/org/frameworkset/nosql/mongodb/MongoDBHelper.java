package org.frameworkset.nosql.mongodb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.frameworkset.security.session.impl.SessionHelper;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;

import com.frameworkset.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDBHelper {
	public static final String defaultMongoDB = "default";
	private static Mongo mongoClient;
	 
	 private static final Object ovalue = new Object();
	 private static final Object sessionlock = new Object();
	 private static boolean configdbindexed;
	 private static boolean initSessionDB;
	private static DB configdb = null;
	 private static  Map<String, Object> dbcollectionCache = null;
	 private static DB sessiondb = null;
	 private static  boolean closeDB;
	private static Logger log = Logger.getLogger(MongoDBHelper.class);
	private static BaseApplicationContext context = DefaultApplicationContext.getApplicationContext("mongodb.xml");
	public static  void initSessionDB()
	{
		if(initSessionDB)
			return ;
		synchronized(sessionlock)
		{
			if(initSessionDB)
				return ;
			try
			{
				mongoClient = MongoDBHelper.getMongoClient(MongoDBHelper.defaultMongoDB);
				dbcollectionCache = new HashMap<String,Object>();
				sessiondb = mongoClient.getDB( "sessiondb" );
				configdb = mongoClient.getDB( "sessionconfdb" );
			}
			finally
			{
				initSessionDB = true;
			}
		}
	}
	public static Set<String> getSessionDBCollectionNames()
	{
		initSessionDB();
		return sessiondb.getCollectionNames();
	}
	
	public static DBCollection getSessionCollection(String app)
	{
		initSessionDB();
		return sessiondb.getCollection(app);
	}
	public static Mongo getMongoClient(String name)
	{
		if(StringUtil.isEmpty(name))
		{
			return context.getTBeanObject(defaultMongoDB, Mongo.class);
		}
		else
			return context.getTBeanObject(name, Mongo.class);
	}
	
	public static Mongo getMongoClient()
	{
		return getMongoClient(null);
	}
	
	public static void destory()
	{
		if(closeDB)
			return;
		try
		{
			if(mongoClient != null)
			{
				try {
					mongoClient.close();
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		finally
		{
			closeDB = true;
		}
		
	}
	private static final String dianhaochar = "____";
	private static final String moneychar = "_____";
	private static final int msize = moneychar.length();

	public static String recoverSpecialChar(String attribute) {
		if (attribute.startsWith(moneychar)) {
			attribute = "$" + attribute.substring(msize);
		}

		attribute = attribute.replace(dianhaochar, ".");
		return attribute;
	}
	public static String converterSpecialChar(String attribute)
	{
		attribute = attribute.replace(".", dianhaochar);
		if(attribute.startsWith("$"))
		{
			if(attribute.length() == 1)
			{
				attribute = moneychar;
			}
			else
			{
				attribute = moneychar + attribute.substring(1);
			}
		}
		return attribute;
	}
	public static boolean filter(String key) {
		return SessionHelper.filter(key);
			
	}
	
	public static String getAppSessionTableName(String appKey)
	{
		return appKey+"_sessions";
	}
	
	public static DBCollection getAppSessionDBCollection(String appKey)
	{
		initSessionDB();
		String tablename = MongoDBHelper.getAppSessionTableName( appKey);
		 DBCollection sessions = sessiondb.getCollection(tablename);
//		 sessions.ensureIndex("sessionid");
		 String idxname = tablename+":sessionid";
		 if(!dbcollectionCache.containsKey(idxname))
		 {
			 dbcollectionCache.put(idxname, ovalue);
			 sessions.createIndex(new BasicDBObject( "sessionid" , 1 ));
			
		 }
		 
		 return sessions;
	}
	
	public static DB getDB(String poolname,String dbname)
	{
		return MongoDBHelper.getMongoClient(poolname).getDB( dbname );
	}
	
	public static DBCollection getConfigSessionDBCollection()
	{
		initSessionDB();
		DBCollection sessionconf = configdb.getCollection("sessionconf");
//		 sessions.ensureIndex("sessionid");
		if(!configdbindexed)
		{
			sessionconf.createIndex(new BasicDBObject( "appcode" , 1 ));
			configdbindexed = true;
		}
		return sessionconf;
	}
	
	public static Map<String,Object> toMap(DBObject object,boolean deserial) {

		Set set = object.keySet();
		if (set != null && set.size() > 0) {
			Map<String,Object> attrs = new HashMap<String,Object>();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (!MongoDBHelper.filter(key)) {
					Object value = object.get(key);
					try {
						attrs.put(MongoDBHelper.recoverSpecialChar(key),
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
	
	public static Map<String,Object> toMap(String appkey,String contextpath,DBObject object,boolean deserial) {

		Set set = object.keySet();
		if (set != null && set.size() > 0) {
			Map<String,Object> attrs = new HashMap<String,Object>();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (!MongoDBHelper.filter(key)) {
					Object value = object.get(key);
					try {
						String temp = MongoDBHelper.recoverSpecialChar(key);
						temp = SessionHelper.dewraperAttributeName(appkey, contextpath, temp);
						if(temp != null)
							attrs.put(temp,
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
	
}
