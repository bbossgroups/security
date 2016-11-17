package org.frameworkset.web.token;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.frameworkset.security.ecc.SimpleKeyPair;
import org.frameworkset.web.auth.AuthenticatePlugin;

public class DummyTokenService implements TokenServiceInf {

	public DummyTokenService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String genToken(ServletRequest request, String fid, boolean cache) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildDToken(String elementType, HttpServletRequest request) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildDToken(String elementType, String jsonsplit, HttpServletRequest request, String fid)
			throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTokenfailpath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TokenResult checkTicket(String appid, String secret, String ticket) throws TokenException {
		TokenResult result = new TokenResult();
		return result;
	}

	@Override
	public TokenResult checkToken(String appid, String secret, String token) throws TokenException {
		TokenResult result = new TokenResult();
		return result;
	}

	@Override
	public String buildHiddenDToken(HttpServletRequest request) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String appendDTokenToURL(HttpServletRequest request, String url) throws TokenException {
		// TODO Auto-generated method stub
		return url;
	}

	@Override
	public String buildJsonDToken(String jsonsplit, HttpServletRequest request) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildParameterDToken(HttpServletRequest request) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildDToken(HttpServletRequest request) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildDToken(String elementType, String jsonsplit, HttpServletRequest request, String fid,
			boolean cache) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String genTempToken() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public String genDualToken(String appid, String secret, String ticket, long dualtime) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String genDualTokenWithDefaultLiveTime(String appid, String secret, String ticket) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public String genAuthTempToken(String appid, String secret, String ticket) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ticket genTicket(String account, String worknumber, String appid, String secret) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableToken() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSecret() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAppid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean destroyTicket(String ticket, String appid, String secret) throws TokenException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean refreshTicket(String ticket, String appid, String secret) throws TokenException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int checkTempToken(String token) throws TokenException {
		// TODO Auto-generated method stub
		return TokenStore.token_request_validateresult_notenabletoken;
	}

	@Override
	public Ticket genTempTicket(String account, String worknumber, String appid, String secret) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.frameworkset.web.token.TokenServiceInf#getPublicKey(java.lang.String)
	 */
	@Override
	public PublicKey getPublicKey(String appid) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.frameworkset.web.token.TokenServiceInf#getPrivateKey(java.lang.String)
	 */
	@Override
	public PrivateKey getPrivateKey(String appid) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.frameworkset.web.token.TokenServiceInf#getSimpleKeyPair(java.lang.String)
	 */
	@Override
	public SimpleKeyPair getSimpleKeyPair(String appid) {
		// TODO Auto-generated method stub
		return null;
	}

	/** (non-Javadoc)
	 * @see org.frameworkset.web.token.TokenServiceInf#getAuthenticatePlugin()
	 */
	@Override
	public AuthenticatePlugin getAuthenticatePlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	/** (non-Javadoc)
	 * @see org.frameworkset.web.token.TokenServiceInf#assertApplication(java.lang.String, java.lang.String)
	 */
	@Override
	public Application assertApplication(String appid, String secret) throws TokenException {
		// TODO Auto-generated method stub
		return null;
	}

}
