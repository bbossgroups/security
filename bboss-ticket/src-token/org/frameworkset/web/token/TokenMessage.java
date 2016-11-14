/**
 * 
 */
package org.frameworkset.web.token;

/**
 * @author yinbp
 *
 * @Date:2016-11-13 22:18:11
 */
public class TokenMessage {
	private String data;
	private String timestamp;
	private String nonce;
	private String signature;
	
	/**
	 * 
	 */
	public TokenMessage() {
		// TODO Auto-generated constructor stub
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
