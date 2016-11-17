/**
 * 
 */
package org.frameworkset.web.auth;

import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * @author yinbp
 *catch (ExpiredJwtException e) {
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
 * @Date:2016-11-17 09:46:15
 */
public abstract class AuthenticateMessages {
	private static Map<String,String> messages = new HashMap<String,String>();
	static
	{
		messages.put("50001", "服务端报错：账号和口令不能为空");
		messages.put("50002", "服务端报错：应用标识和应用口令不能为空");
		messages.put("50003", "服务端报错：应用凭证已经过期失效");
		messages.put("50004", "服务端报错：不支持的应用凭证");
		messages.put("50005", "服务端报错：错误的凭证格式");
		messages.put("50006", "服务端报错：信息被篡改或者签名错误");
		messages.put("50007", "服务端报错：非法参数");
		messages.put("50008", "服务端报错：系统异常");
		
		
		messages.put("50009", "服务端报错：应用加密privateKey为空");
		//认证服务器公钥加密publicKey为空
		messages.put("50010", "服务端报错：认证服务器公钥加密publicKey为空");
		
		messages.put("40003", "应用端报错：应用凭证已经过期失效 ");
		messages.put("40004", "应用端报错：不支持的应用凭证");
		messages.put("40005", "应用端报错：错误的凭证格式");
		messages.put("40006", "应用端报错：信息被篡改或者签名错误");
		messages.put("40007", "应用端报错：非法参数");
		messages.put("40008", "应用端报错：系统异常");
		
		
	}
	
	public static String getMessage(String code)
	{
		String message = messages.get(code);
		if(message != null)
			return message;
		return code;
	}

}
