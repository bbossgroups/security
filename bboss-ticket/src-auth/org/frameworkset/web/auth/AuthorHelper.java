/**
 * 
 */
package org.frameworkset.web.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.frameworkset.security.KeyCacheUtil;
import org.frameworkset.util.io.ClassPathResource;

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
	private   Properties ssoproperties ;
	private static Logger log = Logger.getLogger(AuthorHelper.class);
	public void init(String configPropertiesFile)
	{
		
		if(ssoproperties != null)
			return;
		synchronized(this)
		{
			if(ssoproperties != null)
				return;
			ssoproperties = new Properties();
			Properties properties = new java.util.Properties();
			if(configPropertiesFile == null || configPropertiesFile.equals(""))
				configPropertiesFile = "conf/sso.properties";	
			
	    	InputStream input = null;
	    	try
	    	{
	    		
	    		if(!configPropertiesFile.startsWith("file:"))
	    		{
			    	ClassPathResource  resource = new ClassPathResource(configPropertiesFile);
			    	input = resource.getInputStream();
			    	log.debug("load config Properties File :"+resource.getFile().getAbsolutePath());
	    		}
	    		else
	    		{
	    			String _configPropertiesFile = configPropertiesFile.substring("file:".length());
	    			input = new FileInputStream(new File(_configPropertiesFile));
	    			log.debug("load config Properties File :"+_configPropertiesFile);
	    		}
		    	properties.load(input);
		    	ssoproperties.putAll(properties);
		    
	    	}
	    	catch(Exception e)
	    	{
	    		log.error("load config Properties File failed:",e);
	    	}
	    	finally
	    	{
	    		if(input != null)
					try {
						input.close();
					} catch (IOException e) {
						 
					}
	    	}
		}
	}

	/**
	 * 
	 */
	public AuthorHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public AuthenticateToken decodeMessageRequest(String authorization,PublicKey publicKey) throws AuthenticateException
	{
		
//		PublicKey publicKey_ = KeyCacheUtil.getPublicKey( publicKey);
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(authorization);
		} catch (ExpiredJwtException e) {
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
	
	/**
	 * @param authorization
	 * @param secretPublicKey
	 * @throws AuthenticateException 
	 */
	public AuthenticatedToken decodeMessageResponse(String authorization, String secretPublicKey) throws AuthenticateException {
		PublicKey publicKey = KeyCacheUtil.getPublicKey( secretPublicKey);
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(authorization);
		} catch (ExpiredJwtException e) {
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
		AuthenticatedToken authenticatedToken = new AuthenticatedToken();
	
		String subject = claims.getBody().getSubject();
		String issuer = (String)claims.getHeader().get("issuer");	 
		String audience = (String)claims.getHeader().get("audience");	 
		Date expiration = (Date)claims.getHeader().get("expiration");	 
		 
		String sessionid = (String)claims.getHeader().get("sessionid");
		String appcode = (String)claims.getHeader().get("appcode");	 
		
		Map<String,Object> body = claims.getBody();		
		authenticatedToken.setSubject(subject);
		authenticatedToken.setAppcode(appcode);
		authenticatedToken.setExtendAttributes(body);
		authenticatedToken.setSessionid(sessionid);
		
		authenticatedToken.setIssuer(issuer);
		authenticatedToken.setAudience(audience);
		authenticatedToken.setExpiration(expiration);
		return authenticatedToken;
	}
	
	public String getAppcode()
	{
		return this.ssoproperties.getProperty("appcode");
	}
	
	public String getAppsecret()
	{
		return this.ssoproperties.getProperty("appsecret");
	}
	
	public String getSecretPrivateKey()
	{
		return this.ssoproperties.getProperty("secret.privateKey");
	}
	
	public String getSecretPublicKey ()
	{
		return this.ssoproperties.getProperty("secret.publicKey");
	}
	
	
	
	public String encodeAuthenticateRequest(String sessionid,String account,
			String password,
			String appcode,
			String appsecret,
			String privateKey,Map<String,Object> extendAttributes)
	{
		 
		PrivateKey privateKey_ = KeyCacheUtil.getPrivateKey(privateKey);
		
		String compactJws =  Jwts.builder()
				.setHeaderParam("sessionid", sessionid)
				.setHeaderParam("appcode", appcode)
				.setHeaderParam("appsecret", appsecret)
				.setHeaderParam("password", password)
				.setClaims(extendAttributes)
			    .setSubject(account)
			    .compressWith(CompressionCodecs.GZIP)
			    .signWith(SignatureAlgorithm.RS512, privateKey_)
			    .compact();
		return compactJws;
	}
	
	
	public String encodeAuthenticateResponse(AuthenticatedToken authenticatedToken,PrivateKey privateKey)
	{
		 
//		PrivateKey privateKey_ = KeyCacheUtil.getPrivateKey(appPrivateKey);
		String account = authenticatedToken.getSubject();
		String sessionid = authenticatedToken.getSessionid();
		String appcode = authenticatedToken.getAppcode();
		String issuer = authenticatedToken.getIssuer();
		String audience = authenticatedToken.getAudience();
		Date expiration = authenticatedToken.getExpiration();
		 
		Map<String,Object> body = authenticatedToken.getExtendAttributes();
		String compactJws =  Jwts.builder()
				.setHeaderParam("appcode", appcode)
				.setHeaderParam("sessionid", sessionid)
				.setHeaderParam("issuer", issuer)
				.setHeaderParam("audience", audience)
				.setHeaderParam("expiration", expiration)
				.setClaims(body)
			    .setSubject(account)
			    .compressWith(CompressionCodecs.GZIP)
			    .signWith(SignatureAlgorithm.RS512, privateKey)
			    .compact();
		return compactJws;
	}

	

}
