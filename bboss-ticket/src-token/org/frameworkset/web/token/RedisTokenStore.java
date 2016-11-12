package org.frameworkset.web.token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.frameworkset.nosql.redis.RedisFactory;
import org.frameworkset.nosql.redis.RedisHelper;
import org.frameworkset.security.ecc.SimpleKeyPair;


public class RedisTokenStore extends BaseTokenStore{
	private static Logger log = Logger.getLogger(RedisTokenStore.class);
//	private  Map<String,MemToken> temptokens = new HashMap<String,MemToken>();
//	private  Map<String,MemToken> dualtokens = new HashMap<String,MemToken>();
//	private final Object checkLock = new Object();
//	private final Object dualcheckLock = new Object();
	 
	
	  public static final String token_authtemptokens_prefix = "bboss:token:authtemptokens:";
	public static final String token_temptokens_prefix = "bboss:token:temptokens:";
	public static final String token_dualtokens_prefix = "bboss:token:dualtokens:";
	public static final String token_eckeypair_prefix = "bboss:token:eckeypair:";
	public static final String token_tickets_prefix = "bboss:token:tickets:";
	public RedisTokenStore()
	{
		
	}
	public void destory()
	{
		
		 
		
	}
	public void livecheck()
	{
		
		 
	}
 
	
//	private MemToken todualToken(Map<String,String> object)
//	{
//		if(object == null)
//			return null;
//		MemToken token = new MemToken(object.get("token"),Long.parseLong(object.get("createTime")), Boolean.valueOf(object.get("validate")),
//				Long.parseLong(object.get("lastVistTime")), Long.parseLong(object.get("livetime")));
//		token.setAppid( object.get("appid"));
//		token.setSecret( object.get("secret"));
//		token.setSigntoken( object.get("signtoken"));
//		return token;
//	}
	
	private MemToken totempToken(Map<String,String>  object)
	{
		MemToken token = new MemToken((String)object.get("token"),Long.parseLong(object.get("createTime")));
		token.setLivetime(Long.parseLong(object.get("livetime")));
		return token;
	}
	
	private MemToken toauthtempToken(Map<String,String> object)
	{
		MemToken token = new MemToken(object.get("token"),Long.parseLong(object.get("createTime")));
		token.setLivetime(Long.parseLong(object.get("livetime")));
		token.setSigntoken(object.get("signtoken"));
		return token;
	}
	
//	@Override
//	protected MemToken getDualMemToken(String token, String appid,
//			long lastVistTime) {
//		
//		
//		MemToken tt = queryDualToken( appid, null,lastVistTime);
//		return tt;
//	}
	private String buildAuthTempMemTokenKey(String token, String appid)
	{
		return new StringBuilder().append(token_authtemptokens_prefix).append(appid).append(":").append(token).toString();
	}
	@Override
	protected MemToken getAuthTempMemToken(String token, String appid) {
		MemToken token_m  = null;
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String key = buildAuthTempMemTokenKey(token, appid);
			Map<String,String> authTempToken = redisHelper.hgetAll(key);
			if(authTempToken == null|| authTempToken.size() == 0)
				return null;
			redisHelper.del(key);
			  token_m = toauthtempToken(authTempToken);		
			 
		}
		finally
		{
			RedisFactory.releaseTX();
		}
//		BasicDBObject dbobject = new BasicDBObject("token", token).append("appid", appid);
//		DBObject tt = authtemptokens.findOne(dbobject);
//		
//		if(tt == null)
//			return null;
//		MongoDB.remove(this.authtemptokens,dbobject,WriteConcern.JOURNAL_SAFE);
//		MemToken token_m = toauthtempToken(tt);		
		return token_m;
	}
	private String buildTempMemTokenKey(String token, String appid)
	{
		return this.token_temptokens_prefix+token;
	}
	@Override
	protected MemToken getTempMemToken(String token, String appid) {
		MemToken token_m = null;
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String tk = buildTempMemTokenKey(token, appid);
			Map<String,String> temptoken = redisHelper.hgetAll(tk);
			if(temptoken != null&& temptoken.size() > 0)
			{
				redisHelper.del(tk);
				token_m = totempToken(temptoken);	
			}
		}
		finally
		{
			RedisFactory.releaseTX();
		}
		return token_m;	
		 
		
	}	
	
	
	
//
//	private MemToken queryDualToken(String appid, String secret)
//	{
//		return queryDualToken(appid, secret,-1);
//	}
//	private String buildAppDualTokenKey(String appid)
//	{
//		return token_dualtokens_prefix+appid;
//	}
//	private MemToken queryDualToken(String appid, String secret,long lastVistTime)
//	{
//		String dualtokenKey = this.buildAppDualTokenKey(appid);
//		MemToken tt = null;
//		RedisHelper redisHelper = null;
//		try
//		{
//			redisHelper = RedisFactory.getTXRedisHelper();
//			Map<String,String> tokens = redisHelper.hgetAll(dualtokenKey);
//			if(tokens != null&& tokens.size() > 0)
//			{
//				if(lastVistTime > 0)
//				{
//					
//					
//				 	redisHelper.hset(dualtokenKey, "lastVistTime", String.valueOf(lastVistTime));
//					tt = todualToken(tokens); 
//					if(tt.getLivetime() > 0)
//						redisHelper.expire(dualtokenKey, (int)tt.getLivetime());
//					 
//				}
//				else
//				{
//					tt = todualToken(tokens); 
//				}
//			}
//		}
//		finally
//		{
//			RedisFactory.releaseTX();
//		}
//		 
//		
//		return tt;
//	}
//	
//
	private void insertDualToken(MemToken dualToken)
	{
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String dualKey = this.buildAuthTempMemTokenKey(dualToken.getToken(), dualToken.getAppid());
			Map<String,String> tokens = new HashMap<String,String>();
			tokens.put("token",dualToken.getToken());
			tokens.put("createTime", String.valueOf(dualToken.getCreateTime()));
			tokens.put("lastVistTime", String.valueOf(dualToken.getLastVistTime()));
			tokens.put("livetime", String.valueOf(dualToken.getLivetime()));
			tokens.put("appid", dualToken.getAppid());
			tokens.put("secret", dualToken.getSecret());
			tokens.put("signtoken", dualToken.getSigntoken());
			tokens.put("validate", String.valueOf(dualToken.isValidate()));
			redisHelper.hmset(dualKey, tokens);
			if(dualToken.getLivetime() > 0)
				redisHelper.expire(dualKey, (int)dualToken.getLivetime());
		}
		finally
		{
			RedisFactory.releaseTX();
		}
		
	}
//	
//	private void updateDualToken(MemToken dualToken)
//	{
//		RedisHelper redisHelper = null;
//		try
//		{
//			redisHelper = RedisFactory.getTXRedisHelper();
//			String dualKey = this.buildAppDualTokenKey(dualToken.getAppid());
//			Map<String,String> tokens = new HashMap<String,String>();
//			tokens.put("token",dualToken.getToken());
//			tokens.put("createTime", String.valueOf(dualToken.getCreateTime()));
//			tokens.put("lastVistTime", String.valueOf(dualToken.getLastVistTime()));
//			tokens.put("livetime", String.valueOf(dualToken.getLivetime()));
//			tokens.put("appid", dualToken.getAppid());
//			tokens.put("secret", dualToken.getSecret());
//			tokens.put("signtoken", dualToken.getSigntoken());
//			tokens.put("validate", String.valueOf(dualToken.isValidate()));
//			redisHelper.hmset(dualKey, tokens);
//			if(dualToken.getLivetime() > 0)
//				redisHelper.expire(dualKey, (int)dualToken.getLivetime());
//		}
//		finally
//		{
//			RedisFactory.releaseTX();
//		}
// 
//	}

	@Override
	public MemToken _genTempToken( ) throws TokenException {
		String token = this.randomToken();
		MemToken token_m = new MemToken(token,System.currentTimeMillis());
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String dualKey = this.buildTempMemTokenKey(token,null);
			Map<String,String> tokens = new HashMap<String,String>();
			tokens.put("token",token_m.getToken());
			tokens.put("createTime", String.valueOf(token_m.getCreateTime()));
			tokens.put("livetime", String.valueOf(this.tempTokendualtime));
			tokens.put("validate", "true");
			redisHelper.hmset(dualKey, tokens);
			if(this.tempTokendualtime > 0)
				redisHelper.expire(dualKey, (int)this.tempTokendualtime);
			this.signToken(token_m, type_temptoken, null,null,false);
			return token_m;
		}
		finally
		{
			RedisFactory.releaseTX();
		}
		 
		
	}
	
//	@Override
//	protected MemToken _genDualToken(String appid,String ticket, String secret, long livetime,boolean sign) throws TokenException {
//		
//		String accountinfo[] = this.decodeTicket(ticket, appid, secret,  sign);
//		MemToken token_m = null;
////		synchronized(this.dualcheckLock)
//		
//		token_m = queryDualToken( appid, secret);
//		if(token_m != null)
//		{
//			long lastVistTime = System.currentTimeMillis();
//			if(isold(token_m, livetime,lastVistTime))//如果令牌已经过期，重新申请新的令牌
//			{
//				//刷新过期的有效期令牌
//				String token = this.randomToken();
//				token_m.setLastVistTime(lastVistTime);
////					this.dualtokens.remove(key);
//				long createTime = System.currentTimeMillis();
//				token_m = new MemToken(token, createTime, true,
//						createTime, livetime);
//				token_m.setAppid(appid);
//				token_m.setSecret(secret);
//				this.signToken(token_m,TokenStore.type_dualtoken,accountinfo,ticket,  sign);
//				updateDualToken(token_m);
//				
//			}
//		}
//		else
//		{
//			String token = this.randomToken();
//			long createTime = System.currentTimeMillis();
//			token_m = new MemToken(token, createTime, true,
//					createTime, livetime);
//			token_m.setAppid(appid);
//			token_m.setSecret(secret);
//			this.signToken(token_m,TokenStore.type_dualtoken,accountinfo,ticket,  sign);
//			this.insertDualToken(token_m);
//		}
//		
//		
//		return token_m ;
//		
//	}
	/**
	 * 创建带认证的临时令牌
	 * @param string
	 * @param string2
	 * @return
	 * @throws TokenException 
	 */
	protected MemToken _genAuthTempToken(String appid,String ticket, String secret,boolean sign) throws TokenException {
		String accountinfo[] = this.decodeTicket(ticket, appid, secret,  sign);
		String token = this.randomToken();//需要将appid,secret,token进行混合加密，生成最终的token进行存储，校验时，只对令牌进行拆分校验
		
		MemToken token_m = null;
//		synchronized(this.dualcheckLock)
		
		long createTime = System.currentTimeMillis();
		token_m = new MemToken(token, createTime, true,
				createTime, this.tempTokendualtime);
		token_m.setAppid(appid);
		token_m.setSecret(secret);
		this.signToken(token_m,TokenStore.type_authtemptoken,accountinfo,ticket,  sign);
		this.insertDualToken(token_m);
		
		
		return token_m ;
	}
	private String buildKeyPaireKey(String appid)
	{
		return this.token_eckeypair_prefix+appid;
	}
	protected SimpleKeyPair _getKeyPair(String appid,String secret) throws TokenException
	{
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String KeyPaireKey = buildKeyPaireKey(  appid);
			Map<String,String> keypairInfo = redisHelper.hgetAll(KeyPaireKey);
			if(keypairInfo != null && keypairInfo.size() > 0)
			{
				return toECKeyPair(keypairInfo);
			}
			else
			{
				try {
					SimpleKeyPair keypair = ECCCoder.genECKeyPair();
					insertECKeyPair(   KeyPaireKey, redisHelper, appid, secret, keypair);
					return keypair;
				} catch (Exception e) {
					throw new TokenException(TokenStore.ERROR_CODE_GETKEYPAIRFAILED,e);
				}
			}
			 
		}
		finally
		{
			RedisFactory.releaseTX();
		}
		 
	}
	private void insertECKeyPair(String KeyPaireKey,RedisHelper redisHelper,String appid,String secret,SimpleKeyPair keypair)
	{
		Map<String,String> datas = new HashMap<String,String>();
		 
		datas.put("appid",appid);		
		datas.put("privateKey", keypair.getPrivateKey());		
		datas.put("createTime", String.valueOf(System.currentTimeMillis()));		
		datas.put("publicKey", keypair.getPublicKey()) ;
		redisHelper.hmset(KeyPaireKey, datas);
	}
	
	protected SimpleKeyPair toECKeyPair(Map<String,String> value)
	{
		SimpleKeyPair ECKeyPair = new SimpleKeyPair( value.get("privateKey"), value.get("publicKey"),null,null);
		return ECKeyPair;
	}
	@Override
	protected void persisteTicket(Ticket ticket) {
		/**
		 *  token;
	private String ticket;
	private long createtime;
	private long livetime;
	private String appid
		 */
		
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String ticketKey = buildTicketkey(ticket.getAppid(),ticket.getToken());
			 Map<String,String> data = new HashMap<String,String>();
			 data.put("appid",ticket.getAppid())		;
				data.put("token", ticket.getToken());
				data.put("ticket", ticket.getTicket());
				data.put("livetime", String.valueOf(ticket.getLivetime()));
				data.put("createtime", String.valueOf(ticket.getCreatetime())) ;
				data.put("lastVistTime", String.valueOf(ticket.getLastVistTime())) ;
				redisHelper.hmset(ticketKey, data);
				if(ticket.getLivetime() > 0)
					redisHelper.expire(ticketKey, (int)ticket.getLivetime());
		}
		finally
		{
			RedisFactory.releaseTX();
		}
		 
		
	}
	private String buildTicketkey(String appid,String token)
	{
		return new StringBuilder().append(this.token_tickets_prefix).append(appid).append(":").append(token).toString();
	}
	protected boolean refreshTicket(String token,String appid) 
	{
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String ticketKey = buildTicketkey(appid,token);
			List<String> ticketTimeInfo = redisHelper.hmget(ticketKey,"lastVistTime","livetime");
			if(ticketTimeInfo != null && ticketTimeInfo.get(0) != null)
			{
				Ticket ticket = new Ticket();
				long lastVistTime =  System.currentTimeMillis();
				ticket.setLivetime(Long.parseLong(ticketTimeInfo.get(1)));
				ticket.setLastVistTime(Long.parseLong(ticketTimeInfo.get(0)));
				assertExpiredTicket(ticket,appid,lastVistTime);
				redisHelper.hset(ticketKey, "lastVistTime", String.valueOf(lastVistTime));
				if(ticket.getLivetime() > 0)
				{
					redisHelper.expire(ticketKey, (int)ticket.getLivetime());
				}
				return true;
			}
			return false;
		}
		finally
		{
			RedisFactory.releaseTX();
		}
		
	
		
	}
	protected boolean destroyTicket(String token,String appid)
	{
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String ticketKey = buildTicketkey(appid,token);
			redisHelper.del(ticketKey);
			return true;
		}
		finally
		{
			RedisFactory.releaseTX();
		}
		 
	}
	@Override
	protected Ticket getTicket(String token, String appid) {
		
		RedisHelper redisHelper = null;
		try
		{
			redisHelper = RedisFactory.getTXRedisHelper();
			String ticketKey = buildTicketkey(appid,token);
			Map<String,String> ticketInfo = redisHelper.hgetAll(ticketKey);
			if(ticketInfo != null&& ticketInfo.size() > 0)
			{
				long lastVistTime =  System.currentTimeMillis();
				Ticket ticket = new Ticket();
				ticket.setAppid( ticketInfo.get("appid"));
				ticket.setCreatetime(Long.parseLong(ticketInfo.get("createtime")));
				ticket.setLivetime(Long.parseLong(ticketInfo.get("livetime")));
				ticket.setTicket(ticketInfo.get("ticket"));
				ticket.setLastVistTime( Long.parseLong(ticketInfo.get("lastVistTime")));
				ticket.setToken(token);
				
				assertExpiredTicket(ticket,appid,lastVistTime);
				if(!this.istempticket(token))
				{
					redisHelper.hset(ticketKey, "lastVistTime", String.valueOf(lastVistTime));
				}
				else//一次性票据，获取后立马销毁
				{
					this.destroyTicket(  token, appid);
				}
				return ticket;
			}
			return null;
		}
		finally
		{
			RedisFactory.releaseTX();
		}
		 
	}
	

}
