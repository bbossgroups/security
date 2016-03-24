package org.frameworkset.nosql;

import org.frameworkset.nosql.redis.RedisFactory;
import org.frameworkset.nosql.redis.RedisHelper;
import org.junit.Test;

public class RedisTest {

	public RedisTest() {
		// TODO Auto-generated constructor stub
	}
	@Test
	public void get()
	{
		RedisHelper redisHelper = RedisFactory.getRedisHelper();
		try
		{
			redisHelper.set("test", "value1");
			String value = redisHelper.get("test");
			redisHelper.set("foo", "fasdfasf");
			value = redisHelper.get("foo");
			System.out.println(value);
		}
		finally
		{
			redisHelper.release();
		}
	}

}
