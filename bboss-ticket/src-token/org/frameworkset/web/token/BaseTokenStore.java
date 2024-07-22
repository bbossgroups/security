package org.frameworkset.web.token;

import org.frameworkset.security.KeyCacheUtil;
import org.frameworkset.security.ecc.ECCCoderInf;
import org.frameworkset.security.ecc.SimpleKeyPair;
import org.frameworkset.util.encoder.Hex;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


//import com.mongodb.DBObject;


public abstract class BaseTokenStore implements TokenStore {
	protected long tempTokendualtime;
	protected long ticketdualtime;
	protected long dualtokenlivetime;
	protected ECCCoderInf ECCCoder = null;
	protected ValidateApplication validateApplication;
	/**
	 * 是否需要进行ticket和token签名
	 */
//	protected boolean sign = false;
	protected abstract boolean destroyTicket(String token,String appid);
	protected abstract boolean refreshTicket(String token,String appid);
	
	
	public boolean destroyTicket(String token,String appid,String secret) throws TokenException
	{
		try {
			boolean result = false;
			this.assertApplication(appid, secret);
			result = this.destroyTicket(token, appid);
			return result;
		} catch (Exception e) {
			throw new TokenException("destroy ticket["+token+"] of app["+appid+"] failed:",e);
		}
	}
	public boolean refreshTicket(String token,String appid,String secret) throws TokenException
	{
		try {
			boolean result = false;
			this.assertApplication(appid, secret);
			result = this.refreshTicket(token, appid);
			return result;
		} catch (TokenException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TokenException("refresh ticket["+token+"] of app["+appid+"] failed:",e);
		}
		
		
	}
	
	protected void assertExpiredTicket(Ticket ticket,String appid,long lastVistTime)
	{
		if(isoldticket(ticket, lastVistTime))
		{
			destroyTicket(ticket.getToken(), appid);
			throw new TokenException(TokenStore.ERROR_CODE_TICKETEXPIRED);
		}
	}
	protected String randomToken()
	{
		String token = UUID.randomUUID().toString();
		return token;
	}
	public void setECCCoder(ECCCoderInf eCCCoder)
	{
		this.ECCCoder =  eCCCoder;
	}
	
	public ECCCoderInf getECCCoder()
	{
		return this.ECCCoder;
	}
	
	protected boolean isold(MemToken token,long currentTime)
	{
		long age = currentTime - token.getCreateTime();		
		return age > this.tempTokendualtime;
		
	}
	
	protected boolean isoldticket(Ticket ticket,long currentTime)
	{
		if(ticket.getLivetime() <= 0)
			return false;
		long age = currentTime - ticket.getLastVistTime();	
		return age > ticket.getLivetime();
	}
//	protected String encodeToken(MemToken token,String tokentype,String ticket) throws TokenException
//	{
//		signToken( token, tokentype, ticket);
//		return token.getSigntoken();
//			
//	}
//	public MemToken genDualTokenWithDefaultLiveTime(String appid,String ticket,String secret)throws TokenException
//	{
//		return genDualToken(appid,ticket, secret, TokenStore.DEFAULT_DUALTOKENLIVETIME) ;
//	}
	
	public Application assertApplication(String appid,String secret) throws TokenException
	{
		try {
			AppValidateResult result = validateApplication.validateApp(appid, secret);
			if(result == null || !result.getResult())
			{
				throw new TokenException(TokenStore.ERROR_CODE_APPVALIDATEFAILED);
			}
			return result.getApplication();
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenException(TokenStore.ERROR_CODE_APPVALIDATERROR,e);
		}
			
	}
	
	public AppValidateResult validateApplication(String appid,String secret) throws TokenException
	{
		try {
			AppValidateResult result = validateApplication.validateApp(appid, secret);
			 
			return result;
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenException(TokenStore.ERROR_CODE_APPVALIDATERROR,e);
		}
			
	}
	/**
	 * 零时ticket一次性有效，在有效期了只能用一次，用过即删除掉，超过指定时间即为无效ticket，要删除
	 * @param account
	 * @param worknumber
	 * @param appid
	 * @param secret
	 * @return
	 * @throws TokenException
	 */
	public Ticket genTempTicket(String account, String worknumber,
			String appid, String secret) throws TokenException
	{
		return _genTicket(account, worknumber,
				appid, secret,true) ;
	}
	public Ticket genTicket(String account, String worknumber,
			String appid, String secret) throws TokenException
	{
		return _genTicket(account, worknumber,
				appid, secret,false) ;
	}
	protected boolean istempticket(String ticket)
	{
		if(ticket.startsWith(TokenStore.type_tempticket))
		{
			return true;
		}
		else
			return false;
	}
	public Ticket _genTicket(String account, String worknumber,
			String appid, String secret,boolean istemp) throws TokenException
	{
		
		Application application = this.assertApplication(appid, secret);
		long createTime = System.currentTimeMillis();
		if(worknumber == null)
		{
			worknumber = "";
		}
		if(account == null)
		{
			account = "";
		}
		try {
			String token = this.randomToken();
			if(istemp)
				token = TokenStore.type_tempticket+token;
			String ticket = account+"|"+worknumber +"|"+createTime;
			boolean comment = true;
			if(application.isSign() && !comment)
			{
				SimpleKeyPair keyPairs = _getSimpleKey(appid,secret,false, KeyCacheUtil.ALGORITHM_RSA);
				byte[] data =  null;
				if(keyPairs.getPubKey() != null)
				{
					
					data = ECCCoder.encrypt(ticket.getBytes(), keyPairs.getPubKey());
					ticket= Hex.toHexString(data);
					
				}
				else
				{
					data = ECCCoder.encrypt(ticket.getBytes(), keyPairs.getPublicKey());
					ticket= Hex.toHexString(data);
				}
			}
			Ticket _ticket = new Ticket();
			_ticket.setAppid(appid);
			_ticket.setCreatetime(createTime);	
			if(application.getTicketlivetime() == -2l)
				_ticket.setLivetime(this.ticketdualtime);
			else
				_ticket.setLivetime(application.getTicketlivetime());
			_ticket.setTicket(ticket);
			_ticket.setToken(token);
			_ticket.setLastVistTime(createTime);
			this.persisteTicket(_ticket);
			return _ticket;
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenException(TokenStore.ERROR_CODE_GENTICKETFAILED,e);
		}
		
		
	}
	
	protected abstract void persisteTicket(Ticket ticket);
	protected abstract Ticket getTicket(String token,String appid);
	
//	protected abstract MemToken getDualMemToken(String token,String appid,long lastVistTime);
	protected abstract MemToken getAuthTempMemToken(String token,String appid );
	protected abstract MemToken getTempMemToken(String token,String appid);
	protected Integer checkTempToken(TokenInfo tokeninfo) throws TokenException
	{
		
		if(tokeninfo != null)
		{
			
			try {
			
				MemToken token_m = tokeninfo.getTokenInfo();
				if(token_m != null)
				{
					if(!this.isold(token_m,System.currentTimeMillis()))
					{
						return TokenStore.token_request_validateresult_ok;
					}
					else
					{
						return TokenStore.token_request_validateresult_expired;
					}
				}
				else
				{
					if(tokeninfo.getDecoderesult().getResult() != null)
						return tokeninfo.getDecoderesult().getResult();
					return TokenStore.token_request_validateresult_fail;
				}
					
				
			} catch (Exception e) {
				throw new TokenException(TokenStore.ERROR_CODE_CHECKTEMPTOKENFAILED,e);
			}
						
		}
		else 
		{
			return TokenStore.token_request_validateresult_nodtoken;
		}
	}
	
	protected Integer checkAuthTempToken(TokenInfo tokeninfo) throws TokenException
	{
		
		if(tokeninfo != null)
		{
//			MemToken token = new MemToken((String)object.get("token"),(Long)object.get("createTime"));
//			token.setLivetime((Long)object.get("livetime"));
			
//			synchronized(checkLock)
//			DBCursor cursor = null;
//			BasicDBObject dbobject = new BasicDBObject("token", tokeninfo.getToken()).append("appid", tokeninfo.getAppid()).append("secret", tokeninfo.getSecret());
			try {
				MemToken token_m = tokeninfo.getTokenInfo();
				if(token_m != null)
				{
//					DBObject tt = cursor.next();
	//				authtemptokens.remove(dbobject);
//					MemToken token_m = totempToken(tt);					
					if(!this.isold(token_m,System.currentTimeMillis()))
					{
						return TokenStore.token_request_validateresult_ok;
					}
					else
					{
						return TokenStore.token_request_validateresult_expired;
					}
					
				}
				else
				{
					if(tokeninfo.getDecoderesult().getResult() != null)
						return tokeninfo.getDecoderesult().getResult();
					return TokenStore.token_request_validateresult_fail;
				}			
			} catch (Exception e) {
				throw new TokenException(TokenStore.ERROR_CODE_CHECKAUTHTEMPTOKENFAILED,e);
			}
			
		}
		else 
		{
			return TokenStore.token_request_validateresult_nodtoken;
		}
	}
	
//	@Override
//	public Integer checkDualToken(TokenInfo tokeninfo) throws TokenException {
//		
//		
//		if(tokeninfo != null)
//		{	
//			MemToken tt = tokeninfo.getTokenInfo();
////			String appid=tokeninfo.getAppid();String secret=tokeninfo.getSecret();
//			String dynamictoken=tokeninfo.getDynamictoken();
//			
//			if(tt != null )//is first request,and clear temp token to against Cross Site Request Forgery
//			{
//				if(tt.getToken().equals(dynamictoken))
//				{
//					long vtime = System.currentTimeMillis();
//					if(!this.isold(tt,tt.getLivetime(),vtime))
//					{
//						tt.setLastVistTime(vtime);
//						if(tt.isValidate())
//							return TokenStore.token_request_validateresult_ok;
//						else
//							return TokenStore.token_request_validateresult_invalidated;
//					}
//					else
//					{
//						return TokenStore.token_request_validateresult_expired;
//					}
//					
//				}
//				else
//				{
//					if(tokeninfo.getDecoderesult().getResult() != null)
//						return tokeninfo.getDecoderesult().getResult();
//					return TokenStore.token_request_validateresult_fail;
//				}
//				
//			}
//			else
//			{
//				return TokenStore.token_request_validateresult_fail;
//			}
//		}
//		else 
//		{
//			return TokenStore.token_request_validateresult_nodtoken;
//		}
//		
//		
//	}
	/**
	 * 0 account 1 worknumber
	 */
	protected String[] decodeTicket(String ticket,
			String appid, String secret,boolean sign) throws TokenException
	{
		
		try {
			Ticket _ticket  = this.getTicket(ticket, appid);
			if(_ticket == null)
			{
				throw new TokenException(TokenStore.ERROR_CODE_TICKETNOTEXIST);
			}
			String accountinfo = null;
			boolean comment = true;			 
			if(sign  && !comment)
			{
				SimpleKeyPair keyPairs = _getSimpleKey(appid,secret,false, KeyCacheUtil.ALGORITHM_RSA);
				byte[] data =  null;
				
				if(keyPairs.getPriKey() != null)
				{
					
					data = ECCCoder.decrypt(Hex.decode(_ticket.getTicket()), keyPairs.getPriKey());
					accountinfo = new String(data);
					
	//			return signtoken;
				}
				else
				{
					data = ECCCoder.decrypt(Hex.decode(_ticket.getTicket()), keyPairs.getPrivateKey());
					accountinfo = new String(data);
	//			return signtoken;
				}
			}
			else
			{
				accountinfo = _ticket.getTicket();
			}
			String infs[] = accountinfo.split("\\|");
//			long createTime = Long.parseLong(infs[2]);
//			if(createTime + this.ticketdualtime < System.currentTimeMillis())
//			{
//				throw new TokenException(TokenStore.ERROR_CODE_TICKETEXPIRED);
//			}
			/**
			//持久性的ticket有效期以最后访问的时间为基准进行计算及
			if (isoldticket(_ticket, System.currentTimeMillis())) {
				throw new TokenException(TokenStore.ERROR_CODE_TICKETEXPIRED);
			}*/

			return infs;
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw new TokenException(TokenStore.ERROR_CODE_DECODETICKETFAILED,e);
		}
		
		
	}
	
	protected void signToken(MemToken token,String tokentype,String accountinfo[],String ticket,boolean sign) throws TokenException
	{
		try {
			if(tokentype == null || tokentype.equals(TokenStore.type_temptoken))
			{
				token.setSigntoken( token.getToken());
			}
			else if(tokentype.equals(TokenStore.type_authtemptoken))
			{	
				String input = accountinfo[0] + "|" + accountinfo[1] + "|" + token.getToken();
				boolean comment = true;				
				if(sign  && !comment)
				{
					//_getSimpleKey(appid,secret,false, KeyCacheUtil.ALGORITHM_RSA);
					SimpleKeyPair keyPairs = _getSimpleKey(token.getAppid(),token.getSecret(),false, KeyCacheUtil.ALGORITHM_RSA);
					
					byte[] data =  null;
					if(keyPairs.getPubKey() != null)
					{
						
						data = ECCCoder.encrypt(input.getBytes(), keyPairs.getPubKey());
						String signtoken = Hex.toHexString(data);
						token.setSigntoken(signtoken);
	//					return signtoken;
					}
					else
					{
						data = ECCCoder.encrypt(input.getBytes(), keyPairs.getPublicKey());
						String signtoken = Hex.toHexString(data);
						token.setSigntoken(signtoken);
	//					return signtoken;
					}
				}
				else
				{
					token.setSigntoken(input);
				}
				
				
			}
			else if(tokentype.equals(TokenStore.type_dualtoken))
			{	
				String input = accountinfo[0] + "|" + accountinfo[1] +  "|" + token.getToken();
				boolean comment = true;				
				if(sign  && !comment)
				{
					//_getSimpleKey(appid,secret,false, KeyCacheUtil.ALGORITHM_RSA);
					SimpleKeyPair keyPairs = _getSimpleKey(token.getAppid(),token.getSecret(),false, KeyCacheUtil.ALGORITHM_RSA);
					
					byte[] data =  null;
					if(keyPairs.getPubKey() != null)
					{				
						data = ECCCoder.encrypt(input.getBytes(), keyPairs.getPubKey());
						String signtoken = Hex.toHexString(data);
						token.setSigntoken(signtoken);
	//					return signtoken;
					}
					else
					{
						data = ECCCoder.encrypt(input.getBytes(), keyPairs.getPublicKey());
						String signtoken = Hex.toHexString(data);
						token.setSigntoken(signtoken);
	//					return signtoken;
					}
				}
				else
				{
					token.setSigntoken(input);
				}
				
				
			}
			else
			{
				throw new TokenException(TokenStore.ERROR_CODE_UNKNOWNTOKENTYPE,new Exception("unknowntokentype："+tokentype+",token="+token.getToken()+",appid="+token.getAppid()+",ticket="+ticket));
			}
		} catch (TokenException e) {
			throw e;
		} catch (Exception e) {
			throw  new TokenException(TokenStore.ERROR_CODE_SIGNTOKENFAILED,e);
		}
		
			
	}
	public static class TokenInfo
	{
		private TokenResult decoderesult;
		private MemToken tokenInfo;
		private String dynamictoken;
		public TokenResult getDecoderesult() {
			return decoderesult;
		}
		public void setDecoderesult(TokenResult decoderesult) {
			this.decoderesult = decoderesult;
		}
		public MemToken getTokenInfo() {
			return tokenInfo;
		}
		public void setTokenInfo(MemToken tokenInfo) {
			this.tokenInfo = tokenInfo;
		}
		public String getDynamictoken() {
			return dynamictoken;
		}
		public void setDynamictoken(String dynamictoken) {
			this.dynamictoken = dynamictoken;
		}
	}
	protected TokenInfo decodeToken(String appid,String secret,String token) throws TokenException
	{
		TokenResult decodetokenResult = new TokenResult();
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setDecoderesult(decodetokenResult);
		char line = token.charAt(2);
		String signtoken = null;
		if(line =='_')
		{
			String tokentype = token.substring(0,2);
			 
			if(tokentype.equals(TokenStore.type_temptoken))//无需认证的临时令牌
			{
				tokenInfo.setDynamictoken(token.substring(3));
				 MemToken memtoken = getTempMemToken(tokenInfo.getDynamictoken(),appid);
				 if(memtoken == null)
					{
//						throw new TokenException(TokenStore.ERROR_CODE_AUTHTEMPTOKENNOTEXIST);
					    decodetokenResult.setTokentype(tokentype);
						decodetokenResult.setAppid(appid);
						decodetokenResult.setResult(token_request_validateresult_notexist);
						return tokenInfo;
					}
				 tokenInfo.setTokenInfo(memtoken);
				decodetokenResult.setTokentype(tokentype);
				
				decodetokenResult.setToken(memtoken.getToken());
				
			}
			else if(tokentype.equals(TokenStore.type_authtemptoken))//需要认证的临时令牌
			{
				try {
					Application appliction = assertApplication( appid, secret);
					tokenInfo.setDynamictoken(token.substring(3));
					
					MemToken memtoken = this.getAuthTempMemToken(tokenInfo.getDynamictoken(), appid);
					if(memtoken == null)
					{
//						throw new TokenException(TokenStore.ERROR_CODE_AUTHTEMPTOKENNOTEXIST);
						decodetokenResult.setResult(token_request_validateresult_notexist);
						decodetokenResult.setTokentype(tokentype);
						decodetokenResult.setAppid(appid);
						return tokenInfo;
					}
					tokenInfo.setTokenInfo(memtoken);
					decodetokenResult.setTokentype(tokentype);
					signtoken = memtoken.getSigntoken();
					String mw = signtoken;
					boolean comment = true;
					if(appliction.isSign()  && !comment)
					{
						//_getSimpleKey(appid,secret,false, KeyCacheUtil.ALGORITHM_RSA);
						SimpleKeyPair keyPairs = _getSimpleKey(appid,secret,false, KeyCacheUtil.ALGORITHM_RSA);
						
						decodetokenResult.setAppid(appid);
						mw = new String(ECCCoder.decrypt(Hex.decode(signtoken), keyPairs.getPrivateKey()));
					}
//					String mw = new String(ECCCoder.decrypt(Hex.decode(signtoken), keyPairs.getPrivateKey()));
					String[] t = mw.split("\\|");
					decodetokenResult.setToken(t[2]);
					decodetokenResult.setWorknumber(t[1]);
					decodetokenResult.setAccount(t[0]);
					decodetokenResult.setSecret(secret);
				}catch (TokenException e) {
					throw  (e);
				} 
				catch (Exception e) {
					throw new TokenException(TokenStore.ERROR_CODE_DECODETOKENFAILED,e);
				}
			}
//			else if(tokentype.equals(TokenStore.type_dualtoken))//有效期令牌校验
//			{
//				try {
//					Application appliction = assertApplication( appid, secret);
//					tokenInfo.setDynamictoken(token.substring(3));
//					long lastVistTime = System.currentTimeMillis(); 
//					MemToken memtoken = this.getDualMemToken(tokenInfo.getDynamictoken(), appid, lastVistTime);
//					if(memtoken == null)
//					{
//						decodetokenResult.setResult(token_request_validateresult_notexist);
//						decodetokenResult.setTokentype(tokentype);
//						decodetokenResult.setAppid(appid);
//						return tokenInfo;
//					}
//					tokenInfo.setTokenInfo(memtoken);
//					decodetokenResult.setTokentype(tokentype);
//					signtoken = memtoken.getSigntoken();
//					String mw = signtoken;
//					if(appliction.isSign())
//					{
//						SimpleKeyPair keyPairs = _getKeyPair(appid,secret,false);
//						
//						decodetokenResult.setAppid(appid);
//						mw = new String(ECCCoder.decrypt(Hex.decode(signtoken), keyPairs.getPrivateKey()));
//					}
//					String[] t = mw.split("\\|");
//					decodetokenResult.setToken(t[2]);
//					decodetokenResult.setWorknumber(t[1]);
//					decodetokenResult.setAccount(t[0]);
//					decodetokenResult.setSecret(secret);
//				}catch (TokenException e) {
//					throw  (e);
//				} 
//				catch (Exception e) {
//					throw new TokenException(TokenStore.ERROR_CODE_DECODETOKENFAILED,e);
//				}
//			}
			else
			{
				throw new TokenException(TokenStore.ERROR_CODE_UNKNOWNTOKENTYPE,new Exception("不正确的令牌类型："+token));
			}
			
		}
		else
		{
			decodetokenResult.setTokentype(TokenStore.type_temptoken);
			decodetokenResult.setAppid(appid);
			decodetokenResult.setToken(token);
			 MemToken memtoken = getTempMemToken(decodetokenResult.getToken(),appid);
			 if(memtoken == null)
				{
	//					throw new TokenException(TokenStore.ERROR_CODE_AUTHTEMPTOKENNOTEXIST);
					decodetokenResult.setResult(token_request_validateresult_notexist);
					return tokenInfo;
				}
			 tokenInfo.setTokenInfo(memtoken);
		}
		
		return tokenInfo;
	}
	protected boolean isold(MemToken token,long livetime,long currentTime)
	{
		long age = currentTime - token.getCreateTime();		
		return age > livetime;
		
	}
	
	@Override
	public long getTempTokendualtime() {
		// TODO Auto-generated method stub
		return tempTokendualtime;
	}

	@Override
	public void setTempTokendualtime(long tempTokendualtime) {
		this.tempTokendualtime = tempTokendualtime;
		
	}
	protected abstract MemToken _genTempToken( ) throws TokenException;
	public MemToken genTempToken( ) throws TokenException
	{
		MemToken tt = _genTempToken(  );
		tt.setToken(TokenStore.type_temptoken+"_" +tt.getToken());
		return tt;
	}
	public MemToken genAuthTempToken(String appid, String ticket,String secret)  throws TokenException 
	{
		Application appliction = this.assertApplication(appid, secret);
		
		MemToken tt = _genAuthTempToken(appid, ticket,secret,  appliction.isSign());
		tt.setToken(TokenStore.type_authtemptoken+"_" +tt.getToken());
		return tt;
	}
	
	protected abstract MemToken _genAuthTempToken(String appid, String ticket,String secret,boolean sign)  throws TokenException;
	
	
	
	public TokenResult checkToken(String appid,String secret,String token) throws TokenException
	{
		
		if(token == null)
		{
			TokenResult result = new TokenResult();
			result.setResult(TokenStore.token_request_validateresult_nodtoken);
			return result; 
		}
		
		TokenInfo tokeninfo = this.decodeToken(appid,secret,token);
		Integer result = null;
		
		if(tokeninfo.getDecoderesult().getTokentype().equals(TokenStore.type_temptoken))//无需认证的临时令牌
		{
			result = this.checkTempToken(tokeninfo);			
		}
		else if(tokeninfo.getDecoderesult().getTokentype().equals(TokenStore.type_authtemptoken))//需要认证的临时令牌
		{
			result = this.checkAuthTempToken(tokeninfo);
		}
//		else if(tokeninfo.getDecoderesult().getTokentype().equals(TokenStore.type_dualtoken))//有效期令牌校验
//		{
//			result = this.checkDualToken(tokeninfo);
//		}
		if(result != null)
		{
			tokeninfo.getDecoderesult().setResult(result);
			return tokeninfo.getDecoderesult();
		}
		
		throw new TokenException(TokenStore.ERROR_CODE_UNKNOWNTOKEN,new Exception("unknowntoken:appid="+appid+",secret="+ secret+",token="+token));
			
		
		
			
			
	}
	
	public TokenResult checkTicket(String appid,String secret,String ticket) throws TokenException
	{
		Application appliction = assertApplication( appid, secret);
		if(ticket == null)
		{
			TokenResult result = new TokenResult();
			result.setResult(TokenStore.token_request_validateresult_nodtoken);
			return result; 
		}
		String[] accountinfos = this.decodeTicket(ticket, appid, secret,appliction.isSign());
		TokenResult result = new TokenResult();
		result.setAccount(accountinfos[0]);
		result.setWorknumber(accountinfos[1]);
		result.setResult(token_request_validateresult_ok);
		return result;
		
//		throw new TokenException(TokenStore.ERROR_CODE_UNKNOWNTOKEN,new Exception("unknowntoken:appid="+appid+",secret="+ secret+",token="+ticket));
			
		
		
			
			
	}
	
	public Integer checkAuthTempToken(TokenResult token)  throws TokenException 
	{
		return TokenStore.token_request_validateresult_fail;
	}
	
	public SimpleKeyPair getKeyPair(String appid,String secret) throws TokenException
	{
		//_getSimpleKey(appid,secret,false, KeyCacheUtil.ALGORITHM_RSA);
		return _getSimpleKey(appid,secret,true, KeyCacheUtil.ALGORITHM_RSA);
	}
	private Map<String,SimpleKeyPair> simpleKeyPairCache = new ConcurrentHashMap<String,SimpleKeyPair>();
	public  SimpleKeyPair getKeyPair(String appid,String secret,boolean validateapp) throws TokenException
	{
		//_getSimpleKey(appid,secret,false, KeyCacheUtil.ALGORITHM_RSA);
		return _getSimpleKey(  appid,  secret,  validateapp, KeyCacheUtil.ALGORITHM_RSA);
	}
	public SimpleKeyPair getSimpleKey(String appid, String secret,String certAlgorithm) {
		// TODO Auto-generated method stub
		return _getSimpleKey(  appid,   secret, true,  certAlgorithm);
	}
	@Override
	public SimpleKeyPair getSimpleKey(String appid, String secret, boolean validateapp,String certAlgorithm) {
		// TODO Auto-generated method stub
		return _getSimpleKey(  appid,   secret, validateapp,  certAlgorithm);
	}
	protected SimpleKeyPair _getSimpleKey(String appid, String secret, boolean validateapp,String certAlgorithm) {
		if(validateapp)
			this.assertApplication(appid, secret);	
		String cacheKey = appid+":"+certAlgorithm;
		SimpleKeyPair kp = simpleKeyPairCache.get(cacheKey);
		 
		if(kp != null)
			return kp;
		synchronized(simpleKeyPairCache)
		{
			kp = simpleKeyPairCache.get(cacheKey);
			if(kp != null)
				return kp;
			kp = _getSimpleKey( appid, secret,certAlgorithm);
			if(kp.getPriKey() == null){
				Key prikey = this.ECCCoder.evalPrivateKey(kp.getPrivateKey(), certAlgorithm);
				
				kp.setPriKey(prikey);
				Key pubkey = this.ECCCoder.evalPublicKey(kp.getPublicKey(), certAlgorithm);
				 
				kp.setPubKey(pubkey);
			}
			simpleKeyPairCache.put(cacheKey, kp);
		}
		return kp;
	}
//	protected SimpleKeyPair _getKeyPair(String appid,String secret,boolean validateapp) throws TokenException
//	{
//		if(validateapp)
//			this.assertApplication(appid, secret);	
//		SimpleKeyPair kp = simpleKeyPairCache.get(appid);
//		if(kp != null)
//			return kp;
//		synchronized(simpleKeyPairCache)
//		{
//			kp = simpleKeyPairCache.get(appid);
//			if(kp != null)
//				return kp;
//			kp = _getSimpleKey( appid, secret, KeyCacheUtil.ALGORITHM_RSA);
//			if(kp.getPriKey() == null)
//			{
//				Key priKey = this.ECCCoder.evalECPrivateKey( kp.getPrivateKey());
//				kp.setPriKey(priKey);
//				
//			}
//			
//			if(kp.getPubKey() == null) {
//				Key pubKey = this.ECCCoder.evalECPublicKey( kp.getPublicKey());
//				kp.setPubKey(pubKey);
//			}
//			simpleKeyPairCache.put(appid, kp);
//		}
//		return kp;
//	}
	
//	protected abstract SimpleKeyPair _getKeyPair(String appid,String secret) throws TokenException;
//	
	protected abstract SimpleKeyPair _getSimpleKey(String appid,String secret,String certAlgorithm) throws TokenException;
	 
	public void setTicketdualtime(long ticketdualtime) {
		this.ticketdualtime = ticketdualtime;
	}

	public long getTicketdualtime() {
		return ticketdualtime;
	}

	public long getDualtokenlivetime() {
		return dualtokenlivetime;
	}

	public void setDualtokenlivetime(long dualtokenlivetime) {
		this.dualtokenlivetime = dualtokenlivetime;
	}
	
	
//	@Override
//	public MemToken genDualToken(String appid, String ticket, String secret,
//			long livetime) throws TokenException {
//		Application application = assertApplication( appid, secret);
//		MemToken tt = _genDualToken( appid,  ticket,  secret,
//				 livetime,  application.isSign());
//		tt.setToken(TokenStore.type_dualtoken + "_"+tt.getToken());
//		return tt;
//		
//	}
	
//	protected abstract MemToken _genDualToken(String appid, String ticket, String secret,
//			long livetime,boolean sign) throws TokenException ;
	public ValidateApplication getValidateApplication() {
		return validateApplication;
	}
	public void setValidateApplication(ValidateApplication validateApplication) {
		this.validateApplication = validateApplication;
	}
	 
	
	

}
