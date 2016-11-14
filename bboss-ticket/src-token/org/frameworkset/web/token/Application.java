package org.frameworkset.web.token;

/**
 * 
 * @author yinbp
 *
 */
public class Application {
	private String appid;
	private String secret;
	/**
	 * ticket有效期
	 */
	private long ticketlivetime;
	private long dualtokenlivetime;
	private long temptokenlivetime;
	/**
	 * 签名key
	 */
	private String signkey ;
	/**
	 * 数据加密key
	 */
	private String encryptKey;
	private Boolean sign = true;
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
	public long getTicketlivetime() {
		return ticketlivetime;
	}
	public void setTicketlivetime(long ticketlivetime) {
		this.ticketlivetime = ticketlivetime;
	}
	public long getDualtokenlivetime() {
		return dualtokenlivetime;
	}
	public void setDualtokenlivetime(long dualtokenlivetime) {
		this.dualtokenlivetime = dualtokenlivetime;
	}
	public long getTemptokenlivetime() {
		return temptokenlivetime;
	}
	public void setTemptokenlivetime(long temptokenlivetime) {
		this.temptokenlivetime = temptokenlivetime;
	}
	public Boolean isSign() {
		return sign;
	}
	public void setSign(Boolean sign) {
		this.sign = sign;
	}
	public String getSignkey() {
		return signkey;
	}
	public void setSignkey(String signkey) {
		this.signkey = signkey;
	}
	public String getEncryptKey() {
		return encryptKey;
	}
	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}
	 
}
