package org.frameworkset.nosql;

import org.frameworkset.nosql.redis.RedisFactory;
import org.frameworkset.nosql.redis.RedisHelper;
import org.frameworkset.security.session.statics.SessionConfig;
import org.frameworkset.soa.ObjectSerializable;
import org.junit.Test;

public class RedisTest {

	public RedisTest() {
		// TODO Auto-generated constructor stub
	}
	@Test
	public void get()
	{
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getRedisHelper();
			redisHelper.set("test", "value1");
			String value = redisHelper.get("test");
			redisHelper.set("foo", "fasdfasf");
			value = redisHelper.get("foo");
			value = redisHelper.get("fowwero");
			System.out.println(value);
		}
		finally
		{
			if(redisHelper != null)
				redisHelper.release();
		}
	}
	@Test
	public void serial()
	{
		String config = "<ps><p n=\"_dflt_\" cs=\"org.frameworkset.security.session.statics.SessionConfig\"><p n=\"appcode\" s:t=\"String\"><![CDATA[sessiondemo]]></p><p n=\"cookieLiveTime\" s:t=\"long\" v=\"0\"/><p n=\"cookiename\" s:t=\"String\"><![CDATA[demo_sessionid]]></p><p n=\"createTime\" s:t=\"java.util.Date\" v=\"1459127144777\"/><p n=\"crossDomain\" s:t=\"String\"><![CDATA[<ps><p n=\"_dflt_\" cs=\"org.frameworkset.security.session.domain.CrossDomain\"><p n=\"domain\" s:t=\"String\"><![CDATA[127.0.0.1]]></p><p n=\"domainApps\" s:t=\"ArrayList\"><l cmt=\"bean\"><p cs=\"org.frameworkset.security.session.domain.App\"><p n=\"attributeNamespace\" s:t=\"String\"><![CDATA[session_bboss_com_cn#]]></p><p n=\"currentApp\" s:t=\"boolean\" v=\"false\"/><p n=\"path\" s:t=\"String\"><![CDATA[/session]]></p><p n=\"uuid\" s:t=\"String\"><![CDATA[session_bboss_com_cn#]]></p></p><p cs=\"org.frameworkset.security.session.domain.App\"><p n=\"attributeNamespace\" s:t=\"String\"><![CDATA[sessionmonitor_bboss_com_cn#]]></p><p n=\"currentApp\" s:t=\"boolean\" v=\"true\"/><p n=\"path\" s:t=\"String\"><![CDATA[/sessionmonitor]]></p><p n=\"uuid\" s:t=\"String\"><![CDATA[sessionmonitor_bboss_com_cn#]]></p></p></l></p><p n=\"rootDomain\" s:t=\"String\"><![CDATA[127.0.0.1]]></p><p n=\"shareSessionAttrs\" s:t=\"String\"><![CDATA[userAccount]]></p><p n=\"currentApp\" refid=\"attr:_dflt_->domainApps[1]\"/><p n=\"appsIdxs\" s:t=\"HashMap\"><m cmt=\"bean\"><p n=\"sessionmonitor_bboss_com_cn#\" refid=\"attr:_dflt_->domainApps[1]\"/><p n=\"session_bboss_com_cn#\" refid=\"attr:_dflt_->domainApps[0]\"/></m></p></p></ps>]]></p><p n=\"httpOnly\" s:t=\"boolean\" v=\"true\"/><p n=\"lazystore\" s:t=\"boolean\" v=\"true\"/><p n=\"monitorAttributes\" s:t=\"String\"><![CDATA[[{\"name\":\"userAccount\",\"cname\":\"账号\",\"type\":\"String\",\"like\":true,\"enableEmptyValue\":true}]]]></p><p n=\"monitorScope\" s:t=\"String\"><![CDATA[all]]></p><p n=\"secure\" s:t=\"boolean\" v=\"false\"/><p n=\"sessionListeners\" s:t=\"String\"><![CDATA[org.frameworkset.security.session.impl.NullSessionListener]]></p><p n=\"sessionStore\" s:t=\"String\"><![CDATA[org.frameworkset.security.session.impl.RedisSessionStore]]></p><p n=\"sessionTimeout\" s:t=\"long\" v=\"3600000\"/><p n=\"sessionscaninterval\" s:t=\"long\" v=\"3600000\"/><p n=\"startLifeScan\" s:t=\"boolean\" v=\"false\"/><p n=\"updateTime\" s:t=\"java.util.Date\" v=\"1459127177221\"/><p n=\"usewebsession\" s:t=\"boolean\" v=\"false\"/></p></ps>";
		SessionConfig configbean = ObjectSerializable.toBean(config, SessionConfig.class);
	}

}
