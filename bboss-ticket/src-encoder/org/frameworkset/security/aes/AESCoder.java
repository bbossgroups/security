/**
 * 
 */
package org.frameworkset.security.aes;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.frameworkset.security.AesException;
import org.frameworkset.web.token.TokenMessage;


/**
 * @author yinbp
 *
 * @Date:2016-11-13 21:53:19
 */
public class AESCoder {
	public static Charset CHARSET = Charset.forName("utf-8");
	private String appid;
	private Base64 base64 = new Base64();
	private byte[] aesKey;
	String token;
	/**
	 * 
	 */
	public AESCoder(String aesKey,String appid,String token ) {
		this.appid = appid;
		this.aesKey =  aesKey.getBytes();
		this.token = token;
	}
	
	// 生成4个字节的网络字节序
		byte[] getNetworkBytesOrder(int sourceNumber) {
			byte[] orderBytes = new byte[4];
			orderBytes[3] = (byte) (sourceNumber & 0xFF);
			orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
			orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
			orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
			return orderBytes;
		}

		// 还原4个字节的网络字节序
		int recoverNetworkBytesOrder(byte[] orderBytes) {
			int sourceNumber = 0;
			for (int i = 0; i < 4; i++) {
				sourceNumber <<= 8;
				sourceNumber |= orderBytes[i] & 0xff;
			}
			return sourceNumber;
		}

		// 随机生成16位字符串
		String getRandomStr() {
			String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
			Random random = new Random();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 16; i++) {
				int number = random.nextInt(base.length());
				sb.append(base.charAt(number));
			}
			return sb.toString();
		}

		/**
		 * 对明文进行加密.
		 * 
		 * @param text
		 *            需要加密的明文
		 * @return 加密后base64编码的字符串
		 * @throws AesException
		 *             aes加密失败
		 */
		private String encrypt(String randomStr, String text) throws AesException {
			ByteGroup byteCollector = new ByteGroup();
			byte[] randomStrBytes = randomStr.getBytes(CHARSET);
			byte[] textBytes = text.getBytes(CHARSET);
			byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
			byte[] appidBytes = appid.getBytes(CHARSET);

			// randomStr + networkBytesOrder + text + corpid
			byteCollector.addBytes(randomStrBytes);
			byteCollector.addBytes(networkBytesOrder);
			byteCollector.addBytes(textBytes);
			byteCollector.addBytes(appidBytes);

			// ... + pad: 使用自定义的填充方式对明文进行补位填充
			byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
			byteCollector.addBytes(padBytes);

			// 获得最终的字节流, 未加密
			byte[] unencrypted = byteCollector.toBytes();

			try {
				// 设置加密模式为AES的CBC模式
				Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
				SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
				IvParameterSpec iv = new IvParameterSpec(aesKey);
				cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

				// 加密
				byte[] encrypted = cipher.doFinal(unencrypted);

				// 使用BASE64对加密后的字符串进行编码
				String base64Encrypted = new String(base64.encode(encrypted));

				return base64Encrypted;
			} catch (Exception e) {
				e.printStackTrace();
				throw new AesException(AesException.EncryptAESError);
			}
		}

		/**
		 * 对密文进行解密.
		 * 
		 * @param text
		 *            需要解密的密文
		 * @return 解密得到的明文
		 * @throws AesException
		 *             aes解密失败
		 */
		public String decrypt(String text) throws AesException {
			byte[] original;
			try {
				// 设置解密模式为AES的CBC模式
				Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
				SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
				IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
				cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

				// 使用BASE64对密文进行解码
				byte[] encrypted = Base64.decodeBase64(text.getBytes());

				// 解密
				original = cipher.doFinal(encrypted);
			} catch (Exception e) {
				e.printStackTrace();
				throw new AesException(AesException.DecryptAESError);
			}

			String xmlContent, from_appid;
			try {
				// 去除补位字符
				byte[] bytes = PKCS7Encoder.decode(original);

				// 分离16位随机字符串,网络字节序和corpId
				byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

				int xmlLength = recoverNetworkBytesOrder(networkOrder);

				xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
				from_appid = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
			} catch (Exception e) {
				e.printStackTrace();
				throw new AesException(AesException.IllegalBuffer);
			}

			// corpid不相同的情况
			if (!from_appid.equals(appid)) {
				throw new AesException(AesException.ValidateCorpidError);
			}
			return xmlContent;

		}

		/**
		 * 将公众平台回复用户的消息加密打包.
		 * <ol>
		 * <li>对要发送的消息进行AES-CBC加密</li>
		 * <li>生成安全签名</li>
		 * <li>将消息密文和安全签名打包成xml格式</li>
		 * </ol>
		 * 
		 * @param replyMsg
		 *            公众平台待回复用户的消息，xml格式的字符串
		 * @param timeStamp
		 *            时间戳，可以自己生成，也可以用URL参数的timestamp
		 * @param nonce
		 *            随机串，可以自己生成，也可以用URL参数的nonce
		 * 
		 * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce,
		 *         encrypt的xml格式的字符串
		 * @throws AesException
		 *             执行失败，请查看该异常的错误码和具体的错误信息
		 */
		public TokenMessage encryptMsg(String data, String timestamp, String nonce) throws AesException {
			// 加密
			String encrypt = encrypt(getRandomStr(), data);

			// 生成安全签名
			if (timestamp == null || timestamp.equals("") ) {
				timestamp = Long.toString(System.currentTimeMillis());
			}

			String signature = SHA1.getSHA1(token,encrypt, timestamp, nonce);

			// System.out.println("发送给平台的签名是: " + signature[1].toString());
			// 生成发送的xml
			
			TokenMessage tm = new TokenMessage();
			tm.setData(encrypt);
			tm.setTimestamp(timestamp);
			tm.setNonce(nonce);
			tm.setSignature(signature);
			return tm;
		} 
		
		public  String getSHA1(String data, String timestamp, String nonce) throws AesException {
			return  SHA1.getSHA1(token,data, timestamp, nonce);
		}
			

}
