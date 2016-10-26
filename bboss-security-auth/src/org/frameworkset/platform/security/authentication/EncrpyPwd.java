package org.frameworkset.platform.security.authentication;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.vps.crypt.Crypt;

import org.frameworkset.platform.config.ConfigManager;

import org.frameworkset.platform.security.util.Base64;
import org.frameworkset.platform.security.util.KeyUtil;

public class EncrpyPwd implements Serializable{
	
	public static final char[] saltChars =
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./"
			.toCharArray();
	
	public static final String TYPE_CRYPT = "CRYPT";

	public static final String TYPE_MD2 = "MD2";

	public static final String TYPE_MD5 = "MD5";

	public static final String TYPE_NONE = "NONE";

	public static final String TYPE_SHA = "SHA";

	public static final String TYPE_SHA_256 = "SHA-256";

	public static final String TYPE_SHA_384 = "SHA-384";

	public static final String TYPE_SSHA = "SSHA";
	/**
	 * 加密算法
	 * @param password
	 * @return
	 */
	public static String encodePassword(String password) {
		ConfigManager manager = ConfigManager.getInstance();
		//加密算法名称
		String algorithm = manager.getConfigValue("passwordsEncryptionAlgorithm","NONE");
		String encrypeType = ConfigManager.getInstance().getConfigValue("encrpytype", "NONE");
		if(encrypeType.equals("NONE"))
			return password;
		return _encodePassword(password,algorithm, encrypeType);
		
		
		
	}
	
	public static String _encodePassword(String password,String algorithm,String encrypeType) {
//		ConfigManager manager = ConfigManager.getInstance();
		//加密算法名称
//		String algorithm = manager.getConfigValue("passwordsEncryptionAlgorithm","NONE");
		//没有设置加密算法或者将加密算法设置为NONE，就不进行这一层的加密，使用原有加密模式。根据encrpytype的配置进行加密
		if(algorithm.equals("NONE")){
//			String encrypeType = ConfigManager.getInstance().getConfigValue("encrpytype", "NONE");
			if (encrypeType.equals("MD5")) {
				KeyUtil key = new KeyUtil();
				return key.getkeyBeanofStr(password);
			} else if (encrypeType.equals("BASE64")){
				Base64 base = new Base64();
				return base.getEncodeBase64(password);
			} else if(encrypeType.equals("HEX")){
				try {
					return new String(Hex.encodeHex(password.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return password;
		}else{
			return encrypt(algorithm, password,null,encrypeType);
		}
		
		
	}
	
	
	
	
	
	/**
	 * portal加密算法
	 * @param password
	 * @return
	 */
	public static String encodePortalPassword(String password) {
		ConfigManager manager = ConfigManager.getInstance();
		//加密算法名称
		String algorithm = manager.getConfigValue("portalAlgorithm","NONE");
		String encrypeType = ConfigManager.getInstance().getConfigValue("portalencrpytype", "NONE");
		return _encodePassword( password,algorithm,encrypeType);
//		ConfigManager manager = ConfigManager.getInstance();
//		//加密算法名称
//		String algorithm = manager.getConfigValue("portalAlgorithm","NONE");
//		//没有设置加密算法或者将加密算法设置为NONE，就不进行这一层的加密，使用原有加密模式。根据encrpytype的配置进行加密
//		if(algorithm.equals("NONE")){
//			String encrypeType = ConfigManager.getInstance().getConfigValue("portalencrpytype", "NONE");
//			if (encrypeType.equals("MD5")) {
//				KeyUtil key = new KeyUtil();
//				return key.getkeyBeanofStr(password);
//			} else if (encrypeType.equals("BASE64")){
//				Base64 base = new Base64();
//				return base.getEncodeBase64(password);
//			} else if (encrypeType.equals("BASE63")){
//				EnDeCode base = new EnDeCode();
//				return base.enCode(password);
//			} else if(encrypeType.equals("HEX")){
//				try {
//					return new String(Hex.encodeHex(password.getBytes("UTF-8")));
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//			return password;
//		}else{
//			return encrypt(algorithm, password,null);
//		}
		
		
	}
	
	/**
	 * 返回加密后的密码串
	 * @param algorithm		加密机制
	 * @param clearTextPwd	输入的密码
	 * @param dbPassword	加密后的密码
	 * @return
	 */
	public static String encrypt(String algorithm, String clearTextPwd,String dbPassword,String encrypeType){
		algorithm = algorithm.toUpperCase();
		if(clearTextPwd != null && !"".equals(clearTextPwd)){
			if(algorithm.equals(TYPE_SHA)){
				return digest(algorithm, clearTextPwd,encrypeType);
			}
			if(algorithm.equals(TYPE_CRYPT)){
				try {
					byte[] saltBytes = _getSaltFromCrypt(dbPassword);
					return Crypt.crypt(
							clearTextPwd.getBytes("UTF-8"), saltBytes);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(algorithm.equals(TYPE_MD2)){
				return digest(algorithm, clearTextPwd,encrypeType);
			}
			if(algorithm.equals(TYPE_MD5)){
				return digest(algorithm, clearTextPwd,encrypeType);
			}
			if(algorithm.equals(TYPE_NONE)){
				return dbPassword;
			}
			if(algorithm.equals(TYPE_SHA_256)){
				return digest(algorithm, clearTextPwd,encrypeType);
			}
			if(algorithm.equals(TYPE_SHA_384)){
				return digest(algorithm, clearTextPwd,encrypeType);
			}
			if(algorithm.equals(TYPE_SSHA)){
				try {
					byte[] saltBytes = _getSaltFromSSHA(dbPassword);
					byte[] clearTextPwdBytes =
						clearTextPwd.getBytes("UTF-8");

					// Create a byte array of salt bytes appeneded to password bytes

					byte[] pwdPlusSalt =
						new byte[clearTextPwdBytes.length + saltBytes.length];

					System.arraycopy(
						clearTextPwdBytes, 0, pwdPlusSalt, 0,
						clearTextPwdBytes.length);

					System.arraycopy(
						saltBytes, 0, pwdPlusSalt, clearTextPwdBytes.length,
						saltBytes.length);

					// Digest byte array

					MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");

					byte[] pwdPlusSaltHash = sha1Digest.digest(pwdPlusSalt);

					// Appends salt bytes to the SHA-1 digest.

					byte[] digestPlusSalt =
						new byte[pwdPlusSaltHash.length + saltBytes.length];

					System.arraycopy(
						pwdPlusSaltHash, 0, digestPlusSalt, 0,
						pwdPlusSaltHash.length);

					System.arraycopy(
						saltBytes, 0, digestPlusSalt, pwdPlusSaltHash.length,
						saltBytes.length);

					// Base64 encode and format string
//					return digestPlusSalt;
					return new Base64().encode(digestPlusSalt);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		return null;
	}

	private static String digest(String algorithm, String text,String encrypeType){
		MessageDigest digester = null;

		try{
			digester = MessageDigest.getInstance(algorithm);

			digester.update(text.getBytes("UTF-8"));
		}
		catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}

		byte[] bytes = digester.digest();
//		String encrypeType = ConfigManager.getInstance().getConfigValue("encrpytype", "NONE");
		if (encrypeType.equals("MD5")) {
			KeyUtil key = new KeyUtil();
			return key.getkeyBeanofStr(bytes.toString());
		} else if (encrypeType.equals("BASE64")){
			Base64 base = new Base64();
			return base.encode(bytes);
		} else if(encrypeType.equals("HEX")){
			return new String(Hex.encodeHex(bytes));
		}else{
			return null;
		}
//		if("base64".equals("")){
//			return new Base64().encode(bytes);
//		}else{
//			return new String(Hex.encodeHex(bytes));
//		}
	}
	
	private static byte[] _getSaltFromSSHA(String sshaString)
		throws Exception {
	
		byte[] saltBytes = new byte[8];
	
		if (isNull(sshaString)) {
	
			// Generate random salt
	
			Random random = new SecureRandom();
	
			random.nextBytes(saltBytes);
		}
		else {
	
			// Extract salt from encrypted password
	
			try {
				byte[] digestPlusSalt = new Base64().decodeBase64(sshaString);
				byte[] digestBytes = new byte[digestPlusSalt.length - 8];
	
				System.arraycopy(
					digestPlusSalt, 0, digestBytes, 0, digestBytes.length);
	
				System.arraycopy(
					digestPlusSalt, digestBytes.length, saltBytes, 0,
					saltBytes.length);
			}
			catch (Exception e) {
				throw new Exception(
					"Unable to extract salt from encrypted password: " +
						e.getMessage());
			}
		}
	
		return saltBytes;
	}
	
	private static byte[] _getSaltFromCrypt(String cryptString)
		throws Exception {
	
		byte[] saltBytes = new byte[2];
	
		try {
			if (isNull(cryptString)) {
	
				// Generate random salt
	
				Random randomGenerator = new Random();
	
				int numSaltChars = saltChars.length;
	
				StringBuilder sb = new StringBuilder();
	
				int x = Math.abs(randomGenerator.nextInt()) % numSaltChars;
				int y = Math.abs(randomGenerator.nextInt()) % numSaltChars;
	
				sb.append(saltChars[x]);
				sb.append(saltChars[y]);
	
				String salt = sb.toString();
	
				saltBytes = salt.getBytes("UTF-8");
			}
			else {
	
				// Extract salt from encrypted password
	
				String salt = cryptString.substring(0, 3);
	
				saltBytes = salt.getBytes("UTF-8");
			}
		}
		catch (UnsupportedEncodingException uee) {
			throw new Exception(
				"Unable to extract salt from encrypted password: " +
					uee.getMessage());
		}
	
		return saltBytes;
	}
	
	private static boolean isNull(String s){
		if(s == null){
			return true;
		}
		s = s.trim();

		if ((s.length() == 0) || (s.equals(""))) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param password
	 * @return
	 * @throws EncrpyException
	 */
	public static String decodePassword(String password)throws EncrpyException{
		ConfigManager manager = ConfigManager.getInstance();
		String encrypeType = manager.getConfigValue("encrpytype", "NONE");
		if (encrypeType.equals("MD5")) {
			throw new EncrpyException("MD5算法不能解密");
			 
		} else if (encrypeType.equals("BASE64")){
			Base64 base = new Base64();
			return base.getDecodeBase64(password);
		}else if(encrypeType.equals("HEX")){
			try {
				return Hex.decodeHex(password.toCharArray()).toString();
			} catch (DecoderException e) {
				e.printStackTrace();
			}
		}
		return password;
	}
}
