/**
 * 
 */
package org.frameworkset.web.token.ws.v2;

import com.caucho.hessian.client.HessianProxyFactory;
import com.frameworkset.util.SimpleStringUtil;
import org.frameworkset.security.aes.AESCoder;
import org.frameworkset.web.token.TokenMessage;

import java.net.MalformedURLException;

/**
 * @author yinbp
 *
 * @Date:2016-11-14 00:23:02
 */
public class TicketClient {
	private String appid = "test";
	private String secret = "76252a20-b171-4796-a4a4-c89149b725a2";
//	 String server = "http://10.0.15.223/sToken";
	private String server = "http://localhost:90/ticketserver";
	private String encryptKey = "5e74d37d000000c0";
	private String  signKey = "5e74d37d000001c0";
	private String protocol = "hessian";//hessian,http,cxf
	private TokenService tokenService;
	/**
	 * 
	 */
	public TicketClient() {
		// TODO Auto-generated constructor stub
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getEncryptKey() {
		return encryptKey;
	}
	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}
	public String getSignKey() {
		return signKey;
	}
	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	private  void init()
	{
		if(tokenService == null)
		{
			synchronized(this)
			{
				if(tokenService == null)
				{
			        HessianProxyFactory factory = new HessianProxyFactory();
			
			        //String url = "http://localhost:8080/context/hessian?service=tokenService";
			
			        String url = server + "/hessian/v2tokenService";
			
			        try {
			        	TokenService tokenService;				
						tokenService = (org.frameworkset.web.token.ws.v2.TokenService) factory.create(org.frameworkset.web.token.ws.v2.TokenService.class, url);
						 this.tokenService = tokenService;
					} catch (MalformedURLException e) {
						throw new TicketClientException("初始化令牌服务v2tokenService异常",e);
					}
				}
			}
	       
		}
	}
	
	public org.frameworkset.web.token.ws.v2.TicketGetResponse getTempTicket(String account, String worknumber)
	{
		
        //通过hessian根据账号或者工号获取ticket



        try {
        	//加密、签名
        	String timestamp = Long.toString(System.currentTimeMillis());
    		String nonce = SimpleStringUtil.getUUID();
    		AESCoder AESCoder = new AESCoder(this.encryptKey,appid, this.signKey);
    		String data = account+"|"+ worknumber;
    		TokenMessage tokenMessage = AESCoder.encryptMsg(data, timestamp, nonce);
			org.frameworkset.web.token.ws.v2.TicketGetResponse ticket = tokenService.getTempTicket(account, worknumber, appid, secret);
			return ticket;
		} catch (Exception e) {
			throw new TicketClientException("getTempTicket异常:account="+account+", worknumber="+worknumber,e);
		}
	}

}
