package org.frameworkset.security.session;

import java.util.*;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.frameworkset.nosql.mongodb.MongoDB;
import org.frameworkset.nosql.mongodb.MongoDBHelper;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frameworkset.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoDBUtil {
	public static final String defaultMongoDB = "default";

//	 private static MongoDB mongoClient;
	 private static final Object ovalue = new Object();
	 private static final Object sessionlock = new Object();
	 private static boolean configdbindexed;
	 private static boolean initSessionDB;
	private static MongoDatabase configdb = null;
	 private static  Map<String, Object> dbcollectionCache = null;
	 private static MongoDatabase sessiondb = null;
	private static MongoDatabase jonersafesessiondb = null;
	 private static  boolean closeDB;
	private static Logger log = LoggerFactory.getLogger(MongoDBUtil.class);
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
				MongoDB mongoClient = MongoDBHelper.getMongoClient(MongoDBHelper.defaultMongoDB);
				dbcollectionCache = new HashMap<String,Object>();
				sessiondb = mongoClient.getDB( "sessiondb" );
				jonersafesessiondb = sessiondb.withWriteConcern(WriteConcern.JOURNALED);
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
		MongoIterable<String> names = sessiondb.listCollectionNames();

		Set<String> set = new HashSet<>();
		names.into(set);
//		Iterator<String> iterator = names.iterator();
//		while (iterator.hasNext()){
//			set.add(iterator.next());
//		}
		return  set;
	}
	
	public static MongoCollection<Document> getSessionCollection(String app)
	{
		initSessionDB();
		return sessiondb.getCollection(app);
	}
	public static MongoClient getMongoClient(String name)
	{
		MongoDB mongoDB = null;
		if(StringUtil.isEmpty(name))
		{
			mongoDB = context.getTBeanObject(defaultMongoDB, MongoDB.class);
		}
		else
			mongoDB = context.getTBeanObject(name, MongoDB.class);
		return mongoDB == null?null:mongoDB.getMongo();
	}

	public static MongoClient getMongoClient()
	{
		return getMongoClient(null);
	}
	
//	public static void destory()
//	{
//		if(closeDB)
//			return;
//		try
//		{
//			if(mongoClient != null)
//			{
//				try {
//					mongoClient.close();
//				} catch (Exception e) {
//					log.error("", e);
//				}
//			}
//		}
//		finally
//		{
//			closeDB = true;
//		}
//		
//	}


	
	public static boolean filter(String key) {
		return SessionUtil.filter(key);
			
	}
	
	public static String getAppSessionTableName(String appKey)
	{
		return appKey+"_sessions";
	}
	
	public static MongoCollection<Document> getAppSessionDBCollection(String appKey)
	{
		initSessionDB();
		String tablename = getAppSessionTableName( appKey);
		MongoCollection<Document> sessions = sessiondb.getCollection(tablename);
//		 sessions.ensureIndex("sessionid");
		 String idxname = tablename+":sessionid";
		 if(!dbcollectionCache.containsKey(idxname))
		 {
			 dbcollectionCache.put(idxname, ovalue);
			 sessions.createIndex(new BasicDBObject( "sessionid" , 1 ));
			
		 }
		 
		 return sessions;
	}

	public static MongoCollection<Document> getJOURNALEDAppSessionDBCollection(String appKey)
	{
		initSessionDB();
		String tablename = getAppSessionTableName( appKey);
		MongoCollection<Document> sessions = jonersafesessiondb.withWriteConcern( WriteConcern.JOURNALED).getCollection(tablename);
//		 sessions.ensureIndex("sessionid");
		String idxname = tablename+":sessionid";
		if(!dbcollectionCache.containsKey(idxname))
		{
			dbcollectionCache.put(idxname, ovalue);
			sessions.createIndex(new BasicDBObject( "sessionid" , 1 ));

		}

		return sessions;
	}


	public static MongoDatabase getDB(String poolname, String dbname)
	{
		return MongoDBHelper.getMongoClient(poolname).getDB( dbname );
	}
	
	public static MongoCollection<Document>  getConfigSessionDBCollection()
	{
		initSessionDB();
		MongoCollection<Document>  sessionconf = configdb.getCollection("sessionconf");
//		 sessions.ensureIndex("sessionid");
		if(!configdbindexed)
		{
			sessionconf.createIndex(new BasicDBObject( "appcode" , 1 ));
			configdbindexed = true;
		}
		return sessionconf;
	}
	
	public static Map<String,Object> toMap(Document object,boolean deserial) {

		Set set = object.keySet();
		if (set != null && set.size() > 0) {
			Map<String,Object> attrs = new HashMap<String,Object>();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (!filter(key)) {
					Object value = object.get(key);
					try {
						attrs.put(MongoDBHelper.recoverSpecialChar(key),
								deserial?SessionUtil.unserial((String) value):value);
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
				if (!filter(key)) {
					Object value = object.get(key);
					try {
						String temp = MongoDBHelper.recoverSpecialChar(key);
						temp = SessionUtil.dewraperAttributeName(appkey, contextpath, temp);
						if(temp != null)
							attrs.put(temp,
									deserial?SessionUtil.unserial((String) value):value);
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
