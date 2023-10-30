package org.frameworkset.web.token;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.frameworkset.nosql.mongodb.MongoDB;
import org.frameworkset.nosql.mongodb.MongoDBHelper;
import org.frameworkset.security.KeyCacheUtil;
import org.frameworkset.security.ecc.SimpleKeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;

import com.mongodb.WriteConcern;


public class MongodbTokenStore extends BaseTokenStore{
	private static Logger log = LoggerFactory.getLogger(MongodbTokenStore.class);
//	private  Map<String,MemToken> temptokens = new HashMap<String,MemToken>();
//	private  Map<String,MemToken> dualtokens = new HashMap<String,MemToken>();
//	private final Object checkLock = new Object();
//	private final Object dualcheckLock = new Object();
	 
	private MongoDatabase db = null;

	private MongoCollection temptokens = null;
	private MongoCollection authtemptokens = null;
//	private DBCollection dualtokens = null;
	private MongoCollection eckeypairs = null;
	private MongoCollection tickets = null;

	private MongoDatabase dbJouner = null;
	private MongoCollection temptokensJouner = null;
	private MongoCollection authtemptokensJouner = null;
	//	private DBCollection dualtokens = null;
	private MongoCollection eckeypairsJouner = null;
	private MongoCollection ticketsJouner = null;
//	public void requestStart()
//	{
//		if(db != null)
//		{
//			db.requestStart();
//		}
//	}
//	public void requestDone()
//	{
//		if(db != null)
//		{
//			db.requestDone();
//		}
//	}
	
//	public CommandResult getLastError()
//	{
//		if(db != null)
//		{
//			return db.getLastError();
//		}
//		return null;
//	}
	
	public MongodbTokenStore()
	{
		db = MongoDBHelper.getDB(MongoDBHelper.defaultMongoDB, "tokendb" );
		dbJouner = db.withWriteConcern(WriteConcern.JOURNALED);
		authtemptokens = db.getCollection("authtemptokens");
		authtemptokensJouner = dbJouner.getCollection("authtemptokens");
		authtemptokens.createIndex(new BasicDBObject("appid", 1).append("token", 1));
		temptokens = db.getCollection("temptokens");
		temptokensJouner = dbJouner.getCollection("temptokens");
		temptokens.createIndex(new BasicDBObject("token", 1));
//		dualtokens = db.getCollection("dualtokens");
//		dualtokens.createIndex(new BasicDBObject("appid", 1));
		eckeypairs = db.getCollection("eckeypair");
		eckeypairsJouner = dbJouner.getCollection("eckeypair");
		eckeypairs.createIndex(new BasicDBObject("appid", 1));
		tickets = db.getCollection("tickets");
		ticketsJouner = dbJouner.getCollection("tickets");
		tickets.createIndex(new BasicDBObject("token", 1));
	}
	public void destory()
	{
		
//		MongoDBHelper.destory();
		
	}
	public void livecheck()
	{
		
		long curtime = System.currentTimeMillis();
//		synchronized(this.checkLock)
		
		StringBuffer wherefun = new StringBuffer();
		wherefun.append("function() ")
				.append("{")			
			    .append(" if(this.createTime + this.livetime < ").append(curtime).append(")")
			    .append("{")
				.append("return true;")				
				.append("}")
				.append("else")
				.append(" {")
				.append(" return false;")		
				.append("}")
				.append("}");
		String temp = wherefun.toString();

		try
		{
//			temptokens.remove(new BasicDBObject("$where",temp));
			MongoDB.remove(temptokens,new BasicDBObject("$where",temp));
			
		}
		finally
		{
			
		}		
//		

//		try
//		{
////			dualtokens.remove(new BasicDBObject("$where",temp));
//			MongoDB.remove(dualtokens,new BasicDBObject("$where",temp),WriteConcern.UNACKNOWLEDGED);
//			
//		}
//		finally
//		{
//			
//		}		

		try
		{
//			authtemptokens.remove(new BasicDBObject("$where",temp));
			MongoDB.remove(authtemptokens,new BasicDBObject("$where",temp));
		}
		finally
		{
			
		}
		
		
		wherefun = new StringBuffer();
		wherefun.append("function() ")
				.append("{")			
				 .append(" if(this.livetime <= 0)")
			    .append("{")
				.append("return false;")				
				.append("}")
			    .append(" if(this.lastVistTime + this.livetime < ").append(curtime).append(")")
			    .append("{")
				.append("return true;")				
				.append("}")
				.append("else")
				.append(" {")
				.append(" return false;")		
				.append("}")
				.append("}");
		try
		{
//			this.tickets.remove(new BasicDBObject("$where",wherefun));
			MongoDB.remove(tickets,new BasicDBObject("$where",wherefun.toString()));
		}
		finally
		{
			
		}
		
	}
//	public void livecheck()
//	{
//		List<BasicDBObject> dolds = new ArrayList<BasicDBObject>();
//		DBCursor cursor = null;
////		synchronized(this.checkLock)
//		{
//			try
//			{
//				cursor = this.temptokens.find();				
//				while(cursor.hasNext())
//				{	
//					DBObject tt = cursor.next();
//					MemToken token_m = totempToken(tt);				
//					if(isold(token_m))
//					{
//						dolds.add(new BasicDBObject("token", token_m.getToken()));
//					}
//				}
//			}
//			finally
//			{
//				if(cursor != null)
//				{
//					cursor.close();
//					cursor = null;
//				}
//			}
//		}
//		
//		for(int i = 0; i < dolds.size(); i ++)
//		{
////			if(tokenMonitor.isKilldown())
////				break;
//			temptokens.remove(dolds.get(i));
//		}
//		
//		dolds = new ArrayList<BasicDBObject>();
//		try
//		{
//			cursor = this.dualtokens.find();
//			while(cursor.hasNext())
//			{	
//				DBObject tt = cursor.next();
////				MemToken token_m = new MemToken((String)tt.get("token"),(Long)tt.get("createTime"));		
//				MemToken token_m = todualToken(tt);
//				if(isold(token_m,token_m.getLivetime(),System.currentTimeMillis()))
//				{
//					dolds.add(new BasicDBObject("appid", (String)tt.get("appid")).append("secret", (String)tt.get("secret")));
//				}
//			}
//		}
//		finally
//		{
//			if(cursor != null)
//			{
//				cursor.close();
//				cursor = null;
//			}
//		}
//		
//		for(int i = 0; i < dolds.size(); i ++)
//		{
//			dualtokens.remove(dolds.get(i));
//		}
//		
//		dolds = new ArrayList<BasicDBObject>();
//		try
//		{
//			cursor = this.authtemptokens.find();
//			while(cursor.hasNext())
//			{	
//				DBObject tt = cursor.next();
////				MemToken token_m = new MemToken((String)tt.get("token"),(Long)tt.get("createTime"));		
//				MemToken token_m = todualToken(tt);
//				if(isold(token_m,token_m.getLivetime(),System.currentTimeMillis()))
//				{
//					dolds.add(new BasicDBObject("appid", (String)tt.get("appid")).append("secret", (String)tt.get("secret")).append("token", (String)tt.get("token")));
//				}
//			}
//		}
//		finally
//		{
//			if(cursor != null)
//			{
//				cursor.close();
//				cursor = null;
//			}
//		}
//		for(int i = 0; i < dolds.size(); i ++)
//		{
//			authtemptokens.remove(dolds.get(i));
//		}
//		
//		dolds = null;
//		
//	}
	
//	private MemToken todualToken(DBObject object)
//	{
//		if(object == null)
//			return null;
//		MemToken token = new MemToken((String)object.get("token"),(Long)object.get("createTime"), (Boolean)object.get("validate"),
//					(Long)object.get("lastVistTime"), (Long)object.get("livetime"));
//		token.setAppid((String)object.get("appid"));
//		token.setSecret((String)object.get("secret"));
//		token.setSigntoken((String)object.get("signtoken"));
//		return token;
//	}
	
	private MemToken totempToken(Document object)
	{
		MemToken token = new MemToken((String)object.get("token"),(Long)object.get("createTime"));
		token.setLivetime((Long)object.get("livetime"));
		return token;
	}
	
	private MemToken toauthtempToken(Document object)
	{
		MemToken token = new MemToken((String)object.get("token"),(Long)object.get("createTime"));
		token.setLivetime((Long)object.get("livetime"));
		token.setSigntoken((String)object.get("signtoken"));
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
	@Override
	protected MemToken getAuthTempMemToken(String token, String appid) {
		BasicDBObject dbobject = new BasicDBObject("token", token).append("appid", appid);
		Document tt = (Document) authtemptokens.find(dbobject).first();
		
		if(tt == null)
			return null;
		MongoDB.remove(this.authtemptokensJouner,dbobject);
		MemToken token_m = toauthtempToken(tt);		
		return token_m;
	}
	@Override
	protected MemToken getTempMemToken(String token, String appid) {
		BasicDBObject dbobject = new BasicDBObject("token", token);
		Document tt = (Document)temptokens.find(dbobject).first();
		if(tt != null)
		{
//			DBObject tt = cursor.next();
			MongoDB.remove(temptokensJouner,dbobject);
			MemToken token_m = totempToken(tt);	
			
			return token_m;
		}
		
		
		return null;
		
	}	
	
	
	

//	private MemToken queryDualToken(String appid, String secret)
//	{
//		return queryDualToken(appid, secret,-1);
//	}
//	private MemToken queryDualToken(String appid, String secret,long lastVistTime)
//	{
//		BasicDBObject dbobject = new BasicDBObject("appid", appid);
//		MemToken tt = null;
//		DBCursor cursor = null;
//		DBObject dt = null;
//		if(lastVistTime > 0)
//		{
//			dt = dualtokens.findOne(dbobject);
//			MongoDB.update(dualtokens,dbobject, new BasicDBObject("$set",new BasicDBObject("lastVistTime", lastVistTime)));
////			dt = dualtokens.findAndModify(dbobject, new BasicDBObject("$set",new BasicDBObject("lastVistTime", lastVistTime)));
//			tt = todualToken(dt); 
//		}
//		else
//		{
//			try
//			{
//				cursor = dualtokens.find(dbobject);
//				if(cursor.hasNext())
//				{
//					dt = cursor.next();
//					tt = todualToken(dt); 
//				}
//			}
//			finally
//			{
//				if(cursor != null)
//				{
//					cursor.close();
//				}
//			}
//		}
//		
//		
//		return tt;
//	}
	

	private void insertDualToken(MongoCollection dualtokens,MemToken dualToken)
	{
		Document basicDBObject = new Document("token",dualToken.getToken())
				.append("createTime", dualToken.getCreateTime())
				.append("lastVistTime", dualToken.getLastVistTime())
				.append("livetime", dualToken.getLivetime())
				.append("appid", dualToken.getAppid())
				.append("secret", dualToken.getSecret())
				.append("signtoken", dualToken.getSigntoken())
				.append("validate", dualToken.isValidate());
		MongoDB.insert(dualtokens,basicDBObject);
	}
	
//	private void updateDualToken(MemToken dualToken)
//	{
//		MongoDB.update(this.dualtokens,new BasicDBObject(
//		"appid", dualToken.getAppid()),
//		new BasicDBObject("token",dualToken.getToken())
//		.append("createTime", dualToken.getCreateTime())
//		.append("lastVistTime", dualToken.getLastVistTime())
//		.append("livetime", dualToken.getLivetime())
//		.append("appid", dualToken.getAppid())
//		.append("secret", dualToken.getSecret())
//		.append("signtoken", dualToken.getSigntoken())
//		.append("validate", dualToken.isValidate()));
//	}

	@Override
	public MemToken _genTempToken( ) throws TokenException {
		String token = this.randomToken();
		MemToken token_m = new MemToken(token,System.currentTimeMillis());
		MongoDB.insert(temptokens,new Document("token",token_m.getToken()).append("createTime", token_m.getCreateTime()).append("livetime", this.tempTokendualtime).append("validate", true));
		this.signToken(token_m, type_temptoken, null,null,false);
		return token_m;
		
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
//			this.insertDualToken(this.dualtokens,token_m);
//		}
//		
//		
//		return token_m ;
//		
//	}
	/**
	 * 创建带认证的临时令牌
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
		this.insertDualToken(this.authtemptokens,token_m);
		
		
		return token_m ;
	}
	@Override
	protected SimpleKeyPair _getSimpleKey(String appid, String secret, String certAlgorithm) throws TokenException {
			String id = appid+":"+certAlgorithm;
			Document value = (Document)eckeypairs.find(new BasicDBObject("appid", id)).first();
			if(value != null)
			{
				return toECKeyPair(value,certAlgorithm);
				
			}
			else
			{
				try {
					SimpleKeyPair keypair = ECCCoder.genECKeyPair( certAlgorithm);
					insertECKeyPair( id, secret, keypair);
					return keypair;
				} catch (Exception e) {
					throw new TokenException(TokenStore.ERROR_CODE_GETKEYPAIRFAILED,e);
				}
			}

	}
//	protected SimpleKeyPair _getKeyPair(String appid,String secret ) throws TokenException
//	{
//		DBCursor cursor = null;
//		try
//		{
//			cursor = eckeypairs.find(new BasicDBObject("appid", appid));
//			if(cursor.hasNext())
//			{
//				DBObject value = cursor.next();
//				return toECKeyPair(value);
//				
//			}
//			else
//			{
//				try {
//					SimpleKeyPair keypair = ECCCoder.genECKeyPair( );
//					insertECKeyPair( appid, secret, keypair);
//					return keypair;
//				} catch (Exception e) {
//					throw new TokenException(TokenStore.ERROR_CODE_GETKEYPAIRFAILED,e);
//				}
//			}
//		}
//		finally
//		{
//			if(cursor != null)
//			{
//				cursor.close();
//			}
//		}
//	}
	private void insertECKeyPair(String appid,String secret,SimpleKeyPair keypair)
	{
		if(keypair.getPrivateKey()  != null){
			MongoDB.insert(this.eckeypairs,new Document("appid",appid)
			.append("privateKey", keypair.getPrivateKey())
			.append("createTime", System.currentTimeMillis())
			.append("publicKey", keypair.getPublicKey()) );
		}
		else
		{
			MongoDB.insert(this.eckeypairs,new Document("appid",appid)
					
					.append("createTime", System.currentTimeMillis())
					.append("publicKey", keypair.getPublicKey()) );
		}
	}
	protected SimpleKeyPair toECKeyPair(Document value,String certAlgorithm)
	{
		SimpleKeyPair ECKeyPair = new SimpleKeyPair((String)value.get("privateKey"),(String)value.get("publicKey"),null,null,certAlgorithm == null?KeyCacheUtil.ALGORITHM_RSA:certAlgorithm);
		return ECKeyPair;
	}
//	protected SimpleKeyPair toECKeyPair(DBObject value)
//	{
//		SimpleKeyPair ECKeyPair = new SimpleKeyPair((String)value.get("privateKey"),(String)value.get("publicKey"),null,null,KeyCacheUtil.ALGORITHM_RSA);
//		return ECKeyPair;
//	}
	@Override
	protected void persisteTicket(Ticket ticket) {
		/**
		 *  token;
	private String ticket;
	private long createtime;
	private long livetime;
	private String appid
		 */
		MongoDB.insert(this.tickets,new Document("appid",ticket.getAppid())
		.append("token", ticket.getToken())
		.append("ticket", ticket.getTicket())
		.append("livetime", ticket.getLivetime())
		.append("createtime", ticket.getCreatetime()) 
		.append("lastVistTime", ticket.getLastVistTime()) );
		
	}
	
	protected boolean refreshTicket(String token,String appid) 
	{
		try
		{
//			BasicDBObject keys = new BasicDBObject();
//			keys.put("lastVistTime", 1);
//			keys.put("livetime", 1);
			Bson projectionFields = Projections.fields(
					Projections.include("lastVistTime","livetime"),
					Projections.excludeId());
			Document value = (Document)tickets.find(new BasicDBObject("token", token)).projection(projectionFields).first();
			if(value != null)
			{
				Ticket ticket = new Ticket();
				long lastVistTime =  System.currentTimeMillis();
				ticket.setLivetime((Long)value.get("livetime"));
				ticket.setLastVistTime( (Long)value.get("lastVistTime"));
				assertExpiredTicket(ticket,appid,lastVistTime);
				MongoDB.updateOne(this.ticketsJouner,new BasicDBObject("token", token),
													   new BasicDBObject("$set",
															  			new BasicDBObject("lastVistTime", lastVistTime)
															  		    ));
				return true;
			}
			return false;
		}
		catch (TokenException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TokenException("refresh ticket["+token+"] of app["+appid+"] failed:",e);
		}
		
	}
	protected boolean destroyTicket(String token,String appid)
	{
		try {
			MongoDB.remove(this.ticketsJouner,new BasicDBObject("token", token));
			return true;
		} catch (Exception e) {
			throw new TokenException("destroy ticket["+token+"] of app["+appid+"] failed:",e);
		}
	}
	@Override
	protected Ticket getTicket(String token, String appid) {
		try
		{
			long lastVistTime =  System.currentTimeMillis();
			Document value = (Document)tickets.find(new BasicDBObject("token", token)).first();
		
			if(value != null)
			{
				Ticket ticket = new Ticket();
				ticket.setAppid((String)value.get("appid"));
				ticket.setCreatetime((Long)value.get("createtime"));
				ticket.setLivetime((Long)value.get("livetime"));
				ticket.setTicket((String)value.get("ticket"));
				ticket.setLastVistTime( (Long)value.get("lastVistTime"));
				ticket.setToken(token);
				
				assertExpiredTicket(ticket,appid,lastVistTime);
				if(!this.istempticket(token))
				{
					MongoDB.updateOne(ticketsJouner,new BasicDBObject("token", token),
							   new BasicDBObject("$set",
									  			new BasicDBObject("lastVistTime", lastVistTime)
									  		    ));
				}
				else//一次性票据，获取后立马销毁
				{
					this.destroyTicket(token, appid);
				}
				return ticket;
				
			}
			return null;
		}
		catch (TokenException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TokenException(TokenStore.ERROR_CODE_GETTICKETFAILED,e);
		}

	}
	
	

}
