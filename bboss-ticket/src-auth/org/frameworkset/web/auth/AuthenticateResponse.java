/**
 * 
 */
package org.frameworkset.web.auth;

/**
 * @author yinbp
 *
 * @Date:2016-11-15 17:21:04
 */
public class AuthenticateResponse implements java.io.Serializable{
	private String authorization;
	private String error;
	private String resultcode;
	private boolean validateResult;
	/**
	 * 
	 */
	public AuthenticateResponse() {
		// TODO Auto-generated constructor stub
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getResultcode() {
		return resultcode;
	}
	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}
	public boolean isValidateResult() {
		return validateResult;
	}
	public void setValidateResult(boolean validateResult) {
		this.validateResult = validateResult;
	}
	

}
