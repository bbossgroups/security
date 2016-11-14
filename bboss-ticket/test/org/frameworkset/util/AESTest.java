/**
 * 
 */
package org.frameworkset.util;

import org.frameworkset.security.AesException;
import org.frameworkset.security.Uuid16;
import org.frameworkset.security.aes.AESCoder;
import org.frameworkset.web.token.TokenMessage;
import org.junit.Test;

import com.frameworkset.util.SimpleStringUtil;

/**
 * @author yinbp
 *
 * @Date:2016-11-13 22:36:59
 */
public class AESTest {

	/**
	 * 
	 */
	public AESTest() {
		// TODO Auto-generated constructor stub
	}
	@Test
	public void test() throws AesException
	{
		String aesKey = Uuid16.create().toString();
		String appid = "test";
		String token = Uuid16.create().toString();
		AESCoder AESCoder = new AESCoder(aesKey,appid, token);
		String data = "as;dkfjal;skdjf";
		String timestamp = Long.toString(System.currentTimeMillis());
		String nonce = SimpleStringUtil.getUUID();
		TokenMessage mwen = AESCoder.encryptMsg(data, timestamp, nonce);
		System.out.println("encryptKey:"+aesKey);
		System.out.println("signKey:"+token);
		System.out.println(AESCoder.decrypt(mwen.getData()));
		String sing = AESCoder.getSHA1( mwen.getData(), timestamp, nonce);
		System.out.println(sing.equals(mwen.getSignature()));
		
	}

}
