package org.frameworkset.web.auth;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Hex;
import org.frameworkset.util.encoder.Base64Commons;

public class ApplicationSecretEncrpy {

	public ApplicationSecretEncrpy() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 加密算法
	 * @param password
	 * @return
	 */
	public static String encodePassword(String password) {
		
		String encrypeType = "MD5";
//		if(encrypeType.equals("NONE"))
//			return password;
		return _encodePassword(password,"NONE", encrypeType);
		
		
		
	}
	
	public static String _encodePassword(String password,String algorithm,String encrypeType) {
//		ConfigManager manager = ConfigManager.getInstance();
		//加密算法名称
//		String algorithm = manager.getConfigValue("passwordsEncryptionAlgorithm","NONE");
		//没有设置加密算法或者将加密算法设置为NONE，就不进行这一层的加密，使用原有加密模式。根据encrpytype的配置进行加密
		
//			String encrypeType = ConfigManager.getInstance().getConfigValue("encrpytype", "NONE");
			if (encrypeType.equals("MD5")) {
				KeyUtil key = new KeyUtil();
				return key.getkeyBeanofStr(password);
			} else if (encrypeType.equals("BASE64")){
				Base64Commons base = new Base64Commons();
				try {
					return base.encodeAsString(password.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(encrypeType.equals("HEX")){
				try {
					return new String(Hex.encodeHex(password.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return password;
		
		
		
	}

}
