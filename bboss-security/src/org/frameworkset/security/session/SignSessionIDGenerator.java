/**
 * 
 */
package org.frameworkset.security.session;

/**
 * @author yinbp
 *
 * @Date:2016-12-22 14:54:31
 */
public interface SignSessionIDGenerator extends SessionIDGenerator{
	/**
	 * 对sessionid进行签名
	 * paramenterSessionID 标识sessionID是否从参数传递过来，如果从参数传递过来则必须调用本方法，并且通过指定paramenterSessionID=true对sessionid进行加密签名，否则sessionid不起作用
	 * 如果显示指定sessionID必须加密签名或者paramenterSessionID为true，则会对sessionid进行加密签名
	 * @return
	 */
	String sign(String sessionid,boolean paramenterSessionID)  throws SignSessionIDException;
	/**
	 * 校验并还原签名的sessionid
	 * 如果显示指定sessionID必须加密签名或者paramenterSessionID为true，则会对sessionid进行解密
	 * @param signedSessionid
	 * @param paramenterSessionID 如果sessionid是从url请求参数传递过来时，paramenterSessionID为true，否则为false
	 * @return
	 */
	String design(String signedSessionid,boolean paramenterSessionID) throws SignSessionIDException;
}
