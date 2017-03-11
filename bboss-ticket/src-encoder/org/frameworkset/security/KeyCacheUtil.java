/**
 * 
 */
package org.frameworkset.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.frameworkset.security.ecc.BaseECCCoder;
import org.frameworkset.security.ecc.SimpleKeyPair;
import org.frameworkset.util.encoder.Base64Commons;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * @author yinbp
 *
 * @Date:2016-11-14 16:27:26
 */
public abstract class KeyCacheUtil {
	protected static Map<String,Key> PrivateKeyIndex = new HashMap<String,Key>();
	protected static Map<String,Key> keyIndex = new HashMap<String,Key>();
	protected static Map<String,Key> ECPublicKeyIndex = new HashMap<String,Key>();
	protected static Map<String,SimpleKeyPair> PrivateKeyPairIndex = new HashMap<String,SimpleKeyPair>();
	protected static Map<String,SimpleKeyPair> ECPublicKeyPairIndex = new HashMap<String,SimpleKeyPair>();
	public static final String ALGORITHM_RSA = "RSA";
	/** 指定key的大小 */
	private static int KEYSIZE = 1024;
	/**
	 * 
	 */
	public KeyCacheUtil() {
		// TODO Auto-generated constructor stub
	}
	public static Key getPublicKey(String publicKey)
	{
		return getPublicKey(publicKey,null);
	}
	public static Key getPublicKey(String publicKey,BaseECCCoder BaseECCCoder)
	{
		
		Key pubKey = ECPublicKeyIndex.get(publicKey);
		if(pubKey != null)
			return pubKey;
		synchronized(ECPublicKeyIndex)
		{
			pubKey = ECPublicKeyIndex.get(publicKey);
			if(pubKey != null)
				return pubKey;
			try {
				// 对公钥解密
				byte[] keyBytes = Base64Commons.decodeBase64(publicKey);
				if(BaseECCCoder == null)
					pubKey = _evalECPublicKey(keyBytes);
				else
					pubKey = BaseECCCoder._evalECPublicKey(keyBytes);
				ECPublicKeyIndex.put(publicKey, pubKey);
				return pubKey;
			} catch (Exception e) {
				throw new java.lang.RuntimeException(e);
			}
		}
		
	}
	public static Key getPrivateKey(String privateKey)
	{
		return  getPrivateKey(  privateKey,null);
	}
	
//	public static Key getKey(String key,String certAlgorithm)
//	{
//		return  getPrivateKey(  privateKey,null);
//	}
	
	public static Key getKey(String key,String certAlgorithm)
	{
		Key priKey = keyIndex.get(key);
		if(priKey != null)
			return priKey;
		synchronized(keyIndex)
		{
			priKey = keyIndex.get(key);
			if(priKey != null)
				return priKey;
			try {
				
				// 对密钥解密
				byte[] keyBytes = Base64Commons.decodeBase64(key);
				ByteArrayInputStream binput = new ByteArrayInputStream(keyBytes);
				ObjectInputStream input = new ObjectInputStream(binput);
				priKey = (Key) input.readObject();
				keyIndex.put(key, priKey);
				return priKey;
			} catch (Exception e) {
				throw new java.lang.RuntimeException(e);
			}
		}
	}
	public static Key getPrivateKey(String privateKey,BaseECCCoder BaseECCCoder)
	{
		Key priKey = PrivateKeyIndex.get(privateKey);
		if(priKey != null)
			return priKey;
		synchronized(PrivateKeyIndex)
		{
			priKey = PrivateKeyIndex.get(privateKey);
			if(priKey != null)
				return priKey;
			try {
				
				// 对密钥解密
				byte[] keyBytes = Base64Commons.decodeBase64(privateKey);
				if(BaseECCCoder == null)
					priKey = _evalECPrivateKey( keyBytes);
				else
					priKey = BaseECCCoder._evalECPrivateKey( keyBytes);
				 PrivateKeyIndex.put(privateKey, priKey);
				return priKey;
			} catch (Exception e) {
				throw new java.lang.RuntimeException(e);
			}
		}
	}
	
	public static PrivateKey _evalECPrivateKey(byte[] privateKey)
	{
			try {
				
				// 对密钥解密
				PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
				KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
	
				PrivateKey priKey = (PrivateKey) keyFactory
						.generatePrivate(pkcs8KeySpec);
				return priKey;
			} catch (Exception e) {
				throw new java.lang.RuntimeException(e);
			}
	}
	
	public static PublicKey _evalECPublicKey(byte[] publicKey)
	{
		
			try {
				// 对公钥解密

				// 取得公钥
				X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
				KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);

				PublicKey pubKey = (PublicKey) keyFactory
						.generatePublic(x509KeySpec);
				return pubKey;
			} catch (Exception e) {
				throw new java.lang.RuntimeException(e);
			}
		
	}
	
	public static KeyPair genECKeyPair() throws Exception {
				java.security.KeyPairGenerator keygen = java.security.KeyPairGenerator
			     .getInstance(ALGORITHM_RSA);
			   SecureRandom secrand = new SecureRandom();
			   secrand.setSeed(randomToken().getBytes()); // 初始化随机产生器
			   keygen.initialize(KEYSIZE, secrand);
			   KeyPair keys = keygen.genKeyPair();
			   return keys;
	}
	
	public static SimpleKeyPair genECKeyPair(String certAlgorithm) throws Exception {
		if(certAlgorithm == null || certAlgorithm.equals(ALGORITHM_RSA))
			return getECKeyPair(null);
		SecretKey key = MacProvider.generateKey(SignatureAlgorithm.forName(certAlgorithm));
		ByteArrayOutputStream bout =null;
		ObjectOutputStream out = null;
		try{
			 bout =new ByteArrayOutputStream();
			 out = new ObjectOutputStream(bout);
			out.writeObject(key);
			out.flush();
			String spublicKey = Base64Commons.encodeBase64String(bout.toByteArray());
			
//			SimpleKeyPair ECKeyPair = new SimpleKeyPair( spublicKey, 
//					key);
			SimpleKeyPair ECKeyPair = new SimpleKeyPair(spublicKey, spublicKey, 
					key, key,certAlgorithm);
//			ECKeyPair.setCertAlgorithm(certAlgorithm);
		   return ECKeyPair;
		}
		finally
		{
			bout.close();
			out.close();
		}
}
	
	protected static String randomToken()
	{
		String token = Uuid16.create().toString();
		return token;
	}
	
	private static SimpleKeyPair getECKeyPair(BaseECCCoder BaseECCCoder ) throws Exception {
//		KeyPair pair = _genECKeyPair(  );
		KeyPair pair = null;
		if(BaseECCCoder == null)
		    pair = KeyCacheUtil.genECKeyPair();
		else
			pair = BaseECCCoder._genECKeyPair();
        PublicKey              pubKey = pair.getPublic();
        PrivateKey              privKey = pair.getPrivate();
        String sprivateKey = Base64Commons.encodeBase64String(privKey.getEncoded());
  		String spublicKey = Base64Commons.encodeBase64String(pubKey.getEncoded());
  		SimpleKeyPair ECKeyPair = new SimpleKeyPair(sprivateKey, spublicKey, 
  				pubKey, privKey,ALGORITHM_RSA);
  		PrivateKeyPairIndex.put(sprivateKey, ECKeyPair);
  		 PrivateKeyIndex.put(sprivateKey, privKey);
  		
  		ECPublicKeyPairIndex.put(spublicKey, ECKeyPair);
  		ECPublicKeyIndex.put(spublicKey, pubKey);
  		return ECKeyPair;
       
	}
	
	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, Key pubKey)
			throws Exception {
		

		Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		/** 执行加密操作 */
		byte[] b1 = cipher.doFinal(data);
		return b1;
	}
	
	
	/**
	 * 解密<br>
	 * 用私钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, Key priKey) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		
		/** 执行解密操作 */
		byte[] b = cipher.doFinal(data);
		return b;
	}

}
