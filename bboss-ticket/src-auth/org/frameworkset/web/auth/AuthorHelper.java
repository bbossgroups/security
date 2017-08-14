/**
 * 
 */
package org.frameworkset.web.auth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Map;

import org.frameworkset.security.KeyCacheUtil;
import org.frameworkset.security.ecc.SimpleKeyPair;
import org.frameworkset.util.encoder.Base64Commons;
import org.frameworkset.web.token.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frameworkset.util.FileUtil;
import com.frameworkset.util.StringUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * @author yinbp
 *
 * @Date:2016-11-16 09:58:30
 */
public class AuthorHelper {
	
	private static Logger log = LoggerFactory.getLogger(AuthorHelper.class);
	private String appid;
	private String secret;
	/**
	 * 服务端证书算法
	 */
	private String privateCertAlgorithm;
	public String getPrivateCertAlgorithm() {
		return privateCertAlgorithm;
	}

	public void setPrivateCertAlgorithm(String privateCertAlgorithm) {
		this.privateCertAlgorithm = privateCertAlgorithm;
	}

	public String getPublicCertAlgorithm() {
		return publicCertAlgorithm;
	}

	public void setPublicCertAlgorithm(String publicCertAlgorithm) {
		this.publicCertAlgorithm = publicCertAlgorithm;
	}
	/**
	 * 客户端证书算法
	 */
	private String publicCertAlgorithm;
	
	/**
	 * 客户端私钥证书内容
	 */
	private String privateKey;
	/**
	 * 客户端公钥证书内容
	 */
	private String publicKey;

	/**
	 * 
	 */
	public AuthorHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public static  AuthenticateToken decodeMessageRequest(String authorization,Key publicKey) throws AuthenticateException
	{
		
		return _decodeMessageRequest( authorization, publicKey);
		
	}
	public static InputStream generateCAStream(String appcode){
		return generateCAStream( appcode,KeyCacheUtil.ALGORITHM_RSA,KeyCacheUtil.ALGORITHM_RSA);
	}
	private static  AuthenticateToken _decodeMessageRequest(String authorization,Key publicKey) throws AuthenticateException
	{
		
//		PublicKey publicKey_ = KeyCacheUtil.getPublicKey( publicKey);
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(authorization);
			AuthenticateToken authenticateToken = new AuthenticateToken();
			String subject = claims.getBody().getSubject();
			String password = (String)claims.getHeader().get("password");
			String appcode = (String)claims.getHeader().get("appcode");
			String appsecret = (String)claims.getHeader().get("appsecret");
			String sessionid = (String)claims.getHeader().get("sessionid");
			if(StringUtil.isEmpty(subject) || StringUtil.isEmpty(password))
				throw new AuthenticateException("50001");//账号和口令不能为空
			if(StringUtil.isEmpty(appcode) || StringUtil.isEmpty(appsecret))
				throw new AuthenticateException("50002");//应用标识和应用口令不能为空
			authenticateToken.setAccount(subject);
			authenticateToken.setPassword(password);
			authenticateToken.setAppcode(appcode);
			authenticateToken.setAppsecret(appsecret);		
			authenticateToken.setExtendAttributes(claims.getBody());
			authenticateToken.setSignature(claims.getSignature());
			authenticateToken.setSessionid(sessionid);
			return authenticateToken;
		}
		catch(AuthenticateException e)
		{
			throw e;
		}
		catch (ExpiredJwtException e) {
			throw new AuthenticateException("50003");
		} catch (UnsupportedJwtException e) {
			throw new AuthenticateException("50004");
		} catch (MalformedJwtException e) {
			throw new AuthenticateException("50005");
		} catch (SignatureException e) {
			throw new AuthenticateException("50006");
		} catch (IllegalArgumentException e) {
			throw new AuthenticateException("50007");
		}
		catch (Exception e) {
			throw new AuthenticateException("50008");
		}	
		
	}
	

	

	
	private static  AuthenticatedToken _decodeMessageResponse(String authorization,Key publicKey) throws AuthenticateException {
	 	Jws<Claims> claims = null;
		try {
			claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(authorization);
			AuthenticatedToken authenticatedToken = new AuthenticatedToken();
			
			String subject = claims.getBody().getSubject();
			String issuer = claims.getBody().getIssuer();	 
			String audience = claims.getBody().getAudience();	 
//			Date expiration = claims.getBody().getExpiration() ;
			
			 
			String sessionid = (String)claims.getHeader().get("sessionid");
			String appcode = (String)claims.getHeader().get("appcode");	 
			Integer ticketlivetimes = (Integer)claims.getHeader().get("ticketlivetimes");	 
			if(ticketlivetimes != null)
			{
				authenticatedToken.setTicketdualtime(ticketlivetimes.intValue());
			}
			String localappcode = TokenHelper.getTokenService().getAppid();
			if(appcode == null || !appcode.equals(localappcode))
				throw new AuthenticateException("40010");
			
			Map<String,Object> body = claims.getBody();		
			authenticatedToken.setSubject(subject);
			authenticatedToken.setAppcode(appcode);
			authenticatedToken.setExtendAttributes(body);
			authenticatedToken.setSessionid(sessionid);
			authenticatedToken.setCnname(audience);
			authenticatedToken.setIssuer(issuer);
			authenticatedToken.setAudience(audience);
			 
			Boolean frommobile = (Boolean)body.get("frommobile");
			if(frommobile != null)
				authenticatedToken.setFrommobile(frommobile);
			Boolean fromremember = (Boolean)body.get("fromremember");
			if(fromremember != null)
				authenticatedToken.setFromremember(fromremember);
			 
			return authenticatedToken;
		} 
		catch(AuthenticateException e)
		{
			throw e;
		}
		catch (ExpiredJwtException e) {
			throw new AuthenticateException("40003");
		} catch (UnsupportedJwtException e) {
			throw new AuthenticateException("40004");
		} catch (MalformedJwtException e) {
			throw new AuthenticateException("40005");
		} catch (SignatureException e) {
			throw new AuthenticateException("40006");
		} catch (IllegalArgumentException e) {
			throw new AuthenticateException("40007");
		}
		catch (Exception e) {
			throw new AuthenticateException("40008");
		}	
		
	}
	/**
	 * @param authorization
	 * @param secretPublicKey
	 * @throws AuthenticateException 
	 */
	public static  AuthenticatedToken decodeMessageResponse(String authorization, String secretPublicKey) throws AuthenticateException {
		 AuthorHelper authorHelper = TokenHelper.getTokenService().getAuthorHelper();
		 if(authorHelper == null)
		 {
			 throw new AuthenticateException("40009");
		 }
		 if(authorHelper.getPublicCertAlgorithm() == null || authorHelper.getPublicCertAlgorithm().equals(KeyCacheUtil.ALGORITHM_RSA)){
			 Key publicKey = KeyCacheUtil.getPublicKey( secretPublicKey);
			return  _decodeMessageResponse(authorization,publicKey);
		 }
		else
		{
			Key publicKey = KeyCacheUtil.getKey(secretPublicKey, authorHelper.getPublicCertAlgorithm());
			return  _decodeMessageResponse(authorization,publicKey);
		}
		
		
		
	}
	
	/**
	 * @param authorization
	 * @param secretPublicKey
	 * @throws AuthenticateException 
	 */
	public static  AuthenticatedToken decodeMessageResponse(String authorization) throws AuthenticateException {
		 AuthorHelper authorHelper = TokenHelper.getTokenService().getAuthorHelper();
		 if(authorHelper == null)
		 {
			 throw new AuthenticateException("40009");
		 }
		 
		String secretPublicKey = authorHelper.getSecretPublicKey();
		return decodeMessageResponse( authorization,secretPublicKey);
//		if(authorHelper.getCertAlgorithm() == null)
//			return decodeMessageResponse(  authorization,   secretPublicKey);
//		else
//			return decodeMessageResponseWithCertAlgorithm(  authorization,authorHelper.getCertAlgorithm(),   secretPublicKey);
//		
	}
	
	
	
	public String getAppcode()
	{
		return this.appid;
	}
	
	public String getAppsecret()
	{
		return this.secret;
	}
	
	public String getSecretPrivateKey()
	{
		return this.privateKey;
	}
	
	public String getSecretPublicKey ()
	{
		return this.publicKey;
	}
	
	
	
	public static  String encodeAuthenticateRequest(String sessionid,String account,
			String password,
			Map<String,Object> extendAttributes)
	{
		AuthorHelper authorHelper = TokenHelper.getTokenService().getAuthorHelper(); 
		String appcode = authorHelper.getAppcode(); 
		String appsecret = authorHelper.getAppsecret(); 
		Key privateKey_ = null;
		SignatureAlgorithm signatureAlgorithm = null;
		if(authorHelper.getPrivateCertAlgorithm() == null  || authorHelper.getPrivateCertAlgorithm().equals(KeyCacheUtil.ALGORITHM_RSA)){
			String privateKey = authorHelper.getSecretPrivateKey(); 
			privateKey_ = KeyCacheUtil.getPrivateKey(privateKey);
			signatureAlgorithm = SignatureAlgorithm.RS512;
			
		}
		else
		{			
			 privateKey_ = KeyCacheUtil.getKey(authorHelper.getSecretPrivateKey(),  authorHelper.getPrivateCertAlgorithm());
			 signatureAlgorithm = SignatureAlgorithm.forName(authorHelper.getPrivateCertAlgorithm());
			
		}
		String compactJws =  Jwts.builder()
				.setHeaderParam("sessionid", sessionid)
				.setHeaderParam("appcode", appcode)
				.setHeaderParam("appsecret", appsecret)
				.setHeaderParam("password", password)
				.setClaims(extendAttributes)
			    .setSubject(account)
			    .setIssuedAt(new Date())
			    .compressWith(CompressionCodecs.GZIP)
			    .signWith(signatureAlgorithm, privateKey_)
			    .compact();
		return compactJws;
	}
	
	
	
	/**
	 * 
	 * @param authenticatedToken
	 * @param privateKey
	 * @param expiration
	 * @param ticketlivetimes 毫秒，凭证最大空闲时间，从凭证的最近访问时间开始计算
	 * @return
	 */
	public static String encodeAuthenticateResponse(AuthenticatedToken authenticatedToken,PrivateKey privateKey,Date expiration,int ticketlivetimes)
	{
		return encodeAuthenticateResponse(  authenticatedToken,SignatureAlgorithm.RS512,  privateKey,  expiration,  ticketlivetimes);

	}
	
	public static String encodeAuthenticateResponse(AuthenticatedToken authenticatedToken,SignatureAlgorithm signatureAlgorithm,Key privateKey,Date expiration,int ticketlivetimes)
	{
		 
//		PrivateKey privateKey_ = KeyCacheUtil.getPrivateKey(appPrivateKey);
		String account = authenticatedToken.getSubject();
		String sessionid = authenticatedToken.getSessionid();
		String appcode = authenticatedToken.getAppcode();
		String issuer = authenticatedToken.getIssuer();
		String audience = authenticatedToken.getCnname();
		 
		 
		Map<String,Object> body = authenticatedToken.getExtendAttributes();
//		if(body == null)
//			body = new HashMap<String,Object>();
		
		
		String compactJws =  Jwts.builder()
				.setHeaderParam("appcode", appcode)
				.setHeaderParam("sessionid", sessionid)
				.setHeaderParam("ticketlivetimes", ticketlivetimes)
//				.setHeaderParam("issuer", issuer)
//				.setHeaderParam("audience", audience)
//				.setHeaderParam("expiration", expiration)
				.setClaims(body)
				.setIssuer(issuer)
				.setAudience(audience)
				.setExpiration(expiration)
				.setIssuedAt(new Date())
			    .setSubject(account)
			    .compressWith(CompressionCodecs.GZIP)
			    .signWith(signatureAlgorithm, privateKey)
			    .compact();
		return compactJws;
	}
	
//	/**
//	 * 
//	 * @param authenticatedToken
//	 * @param privateKey
//	 * @param expiration
//	 * @param ticketlivetimes 毫秒，凭证最大空闲时间，从凭证的最近访问时间开始计算
//	 * @return
//	 */
//	public static String encodeAuthenticateResponseWithCertAlgorithm(AuthenticatedToken authenticatedToken,String certAlgorithm,Key privateKey,Date expiration,int ticketlivetimes)
//	{
//		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forName(certAlgorithm);
//		return encodeAuthenticateResponse(  authenticatedToken,signatureAlgorithm,  privateKey,  expiration,  ticketlivetimes);
//	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
//	/**
//	 * 
//	 * @param appcode
//	 * @return
//	 */
//	public static InputStream generateCAStream(String appcode)
//    {
//    	if(appcode == null || appcode.equals(""))
//    		throw new java.lang.NullPointerException("生成证书出错:必须指定应用编码");
//    	SimpleKeyPair appkeyPair = TokenHelper.getTokenService().getSimpleKeyPair(appcode);
//	SimpleKeyPair serverkeyPair = TokenHelper.getTokenService().getServerSimpleKeyPair();
//    	StringBuilder content = new StringBuilder();
//    	content.append("[privateKey]\r\n");
//    	content.append(serverkeyPair.getPrivateKey()).append("\r\n");
//    	content.append("[publicKey]\r\n");
//    	content.append(appkeyPair.getPublicKey());
//    	Base64Commons b = new Base64Commons();
//    	
//		try {
//			byte[] data = b.encode(content.toString().getBytes("UTF-8"));
//			java.io.ByteArrayInputStream input = new ByteArrayInputStream(data);
//	        
//	        return input;
//		} catch (UnsupportedEncodingException e) {
//			throw new java.lang.RuntimeException(e);
//		}
//    	
//    }
	
	/**
	 * 
	 * @param appcode
	 * @return
	 */
	public static InputStream generateCAStream(String appcode,String certAlgorithm,String serverCertAlgorithm)
    {
    	if(appcode == null || appcode.equals(""))
    		throw new java.lang.NullPointerException("生成证书出错:必须指定应用编码");
    	
    	SimpleKeyPair appkeyPair = TokenHelper.getTokenService().getSimpleKey(appcode,certAlgorithm);
    	SimpleKeyPair serverkeyPair = TokenHelper.getTokenService().getServerSimpleKey(serverCertAlgorithm);
    	StringBuilder content = new StringBuilder();
    	
    	
    	content.append("[privateKey]\r\n");
    	content.append(serverkeyPair.getPrivateKey()).append("\r\n");
    	content.append("[publicKey]\r\n");    	
    	content.append(appkeyPair.getPublicKey()).append("\r\n");
    	content.append("[privateCertAlgorithm]\r\n");
    	content.append(serverCertAlgorithm).append("\r\n");
    	content.append("[publicCertAlgorithm]\r\n");
    	content.append(certAlgorithm);
    	Base64Commons b = new Base64Commons();
    	
		try {
			byte[] data = b.encode(content.toString().getBytes("UTF-8"));
			java.io.ByteArrayInputStream input = new ByteArrayInputStream(data);
	        
	        return input;
		} catch (UnsupportedEncodingException e) {
			throw new java.lang.RuntimeException(e);
		}
    	
    }
	/**
	 * 
	 * @param appcode
	 * @return
	 */
	public static void generateCAFile(String appcode,String filepath,String certAlgorithm,String serverCertAlgorithm)
    {
    	if(appcode == null || appcode.equals(""))
    		throw new java.lang.NullPointerException("生成证书出错:必须指定应用编码");
    	SimpleKeyPair appkeyPair = TokenHelper.getTokenService().getSimpleKey(appcode,certAlgorithm);
    	SimpleKeyPair serverkeyPair = TokenHelper.getTokenService().getServerSimpleKey( serverCertAlgorithm);
    	StringBuilder content = new StringBuilder();
    	content.append("[privateKey]\r\n");
    	content.append(serverkeyPair.getPrivateKey()).append("\r\n");
    	content.append("[publicKey]\r\n");    	
    	content.append(appkeyPair.getPublicKey()).append("\r\n");
    	content.append("[privateCertAlgorithm]\r\n");
    	content.append(serverCertAlgorithm).append("\r\n");
    	content.append("[publicCertAlgorithm]\r\n");
    	content.append(certAlgorithm);
    	Base64Commons b = new Base64Commons();
    	
		try {
			String data = b.encodeAsString(content.toString().getBytes("UTF-8"));
	        
	       	FileUtil.writeFile(filepath, data, "UTF-8");
		} catch (Exception e) {
			throw new java.lang.RuntimeException(e);
		}
    	
    }
//	/**
//	 * 
//	 * @param appcode
//	 * @return
//	 */
//	public static void generateCAFile(String appcode,String filepath)
//    {
//    	if(appcode == null || appcode.equals(""))
//    		throw new java.lang.NullPointerException("生成证书出错:必须指定应用编码");
//    	SimpleKeyPair appkeyPair = TokenHelper.getTokenService().getSimpleKeyPair(appcode);
//    	SimpleKeyPair serverkeyPair = TokenHelper.getTokenService().getServerSimpleKeyPair();
//    	StringBuilder content = new StringBuilder();
//    	content.append("[privateKey]\r\n");
//    	content.append(serverkeyPair.getPrivateKey()).append("\r\n");
//    	content.append("[publicKey]\r\n");
//    	content.append(appkeyPair.getPublicKey());
//    	Base64Commons b = new Base64Commons();
//    	
//		try {
//			String data = b.encodeAsString(content.toString().getBytes("UTF-8"));
//	        
//	       	FileUtil.writeFile(filepath, data, "UTF-8");
//		} catch (Exception e) {
//			throw new java.lang.RuntimeException(e);
//		}
//    	
//    }

	

}
