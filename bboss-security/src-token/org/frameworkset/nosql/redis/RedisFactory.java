package org.frameworkset.nosql.redis;

import java.util.HashMap;
import java.util.Map;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;

public class RedisFactory {

	private static Map<String,RedisDB> dbs = new HashMap<String,RedisDB>();;
	public RedisFactory() {
		
	}
	private static RedisDB init(String dbname)
	{
		RedisDB db = dbs.get(dbname);
		if(db == null)
		{
			synchronized(RedisFactory.class)
			{
				db = dbs.get(dbname);
				if(db == null)
				{
					BaseApplicationContext context = DefaultApplicationContext.getApplicationContext("redis.xml");
					db = context.getTBeanObject(dbname, RedisDB.class);
					dbs.put(dbname, db);
				}
				
			}
		}
		return db;
	}
	
	public static RedisHelper getRedisHelper()
	{
		RedisDB db = init("default");
		return new RedisHelper(db);
	}
	
	public static RedisHelper getRedisHelper(String dbname)
	{
		if(dbname == null)
			dbname = "default";
		RedisDB db = init(dbname);
		return new RedisHelper(db);
	}

}
