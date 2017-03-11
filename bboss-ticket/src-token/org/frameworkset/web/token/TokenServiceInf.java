package org.frameworkset.web.token;

import java.security.Key;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.frameworkset.security.ecc.SimpleKeyPair;
import org.frameworkset.web.auth.AuthenticatePlugin;
import org.frameworkset.web.auth.AuthorHelper;
import org.frameworkset.web.token.ws.v2.AuthorService;

public interface TokenServiceInf {
	public String getTokenServerAppName();
	public abstract String genToken(ServletRequest request, String fid,
			boolean cache ) throws TokenException;

	public abstract String buildDToken(String elementType,
			HttpServletRequest request ) throws TokenException;

	public abstract String buildDToken(String elementType, String jsonsplit,
			HttpServletRequest request, String fid) throws TokenException;
	public String getTokenfailpath();
	public TokenResult checkTicket(String appid,String secret,String ticket) throws TokenException;
	public TokenResult checkToken(String appid,String secret,String token) throws TokenException;
	public int checkTempToken(String token) throws TokenException;
	/**
	 * 生成隐藏域令牌,输出值为：
	 * <input type="hidden" name="_dt_token_" value="-1518435257">
	 * @param request
	 * @return
	 * @throws TokenException 
	 */
	public abstract String buildHiddenDToken(HttpServletRequest request)
			throws TokenException;
	public String appendDTokenToURL(HttpServletRequest request,String url) throws TokenException;
	/**
	 * 生成json串令牌
	 * 如果jsonsplit为'，则输出值为：
	 * _dt_token_:'1518435257'
	 * 如果如果jsonsplit为",则输出值为：
	 * _dt_token_:"1518435257"
	 * @param jsonsplit
	 * @param request
	 * @return
	 * @throws TokenException 
	 */
	public abstract String buildJsonDToken(String jsonsplit,
			HttpServletRequest request) throws TokenException;

	/**
	 * 生成url参数串令牌
	 * 输出值为：
	 * _dt_token_=1518435257
	 * @param request
	 * @return
	 * @throws TokenException 
	 */
	public abstract String buildParameterDToken(HttpServletRequest request)
			throws TokenException;

	/**
	 * 只生成令牌，对于这种方式，客户端必须将该token以参数名_dt_token_传回服务端，否则不起作用
	 * 输出值为：
	 * 1518435257
	 * @param request
	 * @return
	 * @throws TokenException 
	 */
	public abstract String buildDToken(HttpServletRequest request)
			throws TokenException;

	public abstract String buildDToken(String elementType, String jsonsplit,
			HttpServletRequest request, String fid, boolean cache)
			throws TokenException;

	public abstract String genTempToken( ) throws Exception;

//	public abstract String genDualToken(String appid, String secret,
//			String ticket, long dualtime) throws Exception;
//
//	public abstract String genDualTokenWithDefaultLiveTime(String appid,
//			String secret, String ticket) throws Exception;

	public abstract String genAuthTempToken(String appid, String secret,
			String ticket) throws Exception;
	public Ticket genTempTicket(String account,String worknumber,String appid,String secret) throws TokenException;
	public abstract Ticket genTicket(String account, String worknumber,
			String appid, String secret) throws TokenException;
	public long getTicketdualtime() ;
	public abstract boolean isEnableToken();
	public String getSecret();
	public String getAppid();
	/**
	 * 销毁令牌票据ticket
	 * @param token
	 * @param appid
	 * @param secret
	 */
	public boolean destroyTicket(String ticket,String appid,String secret) throws TokenException;
	/**
	 * 刷新令牌票据ticket有效时间，如果ticket已经失效则抛出异常
	 * @param token
	 * @param appid
	 * @param secret
	 */
	public boolean refreshTicket(String ticket,String appid,String secret) throws TokenException;
	
	public Key getPublicKey( String appid);
	
	public Key getPrivateKey(String appid);
	
//	public SimpleKeyPair getSimpleKeyPair(String appid); 
	
	public SimpleKeyPair getSimpleKey(String appid,String certAlgorithm);
	public SimpleKeyPair getServerSimpleKey(String certAlgorithm);
	public SimpleKeyPair getServerSimpleKey(String appid,String certAlgorithm);
//	public SimpleKeyPair getServerSimpleKeyPair();
//	public SimpleKeyPair getServerSimpleKey();
	public AuthenticatePlugin getAuthenticatePlugin();
	public Application assertApplication(String appid,String secret) throws TokenException;
	public AppValidateResult validateApplication(String appid,String secret) throws TokenException;
	
	public AuthorHelper getAuthorHelper();
	public AuthorService getAuthorService();
}