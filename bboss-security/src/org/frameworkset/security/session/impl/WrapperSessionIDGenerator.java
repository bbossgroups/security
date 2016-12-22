/**
 * 
 */
package org.frameworkset.security.session.impl;

import org.frameworkset.security.DESCipher;
import org.frameworkset.security.session.SessionIDGenerator;
import org.frameworkset.security.session.SignSessionIDException;
import org.frameworkset.security.session.SignSessionIDGenerator;

/**
 * @author yinbp
 *
 * @Date:2016-12-22 10:48:20
 */
public class WrapperSessionIDGenerator implements SignSessionIDGenerator{
	
	private SessionIDGenerator sessionIDGenerator;
	private boolean signSessionID;
	private String signKey;
	/**
	 * 
	 */
	public WrapperSessionIDGenerator(SessionIDGenerator sessionIDGenerator,boolean signSessionID,String signKey) {
		this.sessionIDGenerator = sessionIDGenerator;
		this.signSessionID = signSessionID;
		this.signKey = signKey;
	}

	/** (non-Javadoc)
	 * @see org.frameworkset.security.session.SessionIDGenerator#generateID()
	 */
	@Override
	public SessionID generateID() {
		// TODO Auto-generated method stub
		
		SessionID sessionID = sessionIDGenerator.generateID();
		sessionID.setSignSessionId(this.sign(sessionID.getSessionId(),false));
		return sessionID;
	}

	/** (non-Javadoc)
	 * @see org.frameworkset.security.session.SessionIDGenerator#sign(java.lang.String)
	 */
	 
	public String sign(String sessionid,boolean paramenterSessionID) {
		if(signSessionID || paramenterSessionID)
			return _sign(sessionid);
		else
			return sessionid;
	}

	/** (non-Javadoc)
	 * @see org.frameworkset.security.session.SessionIDGenerator#design(java.lang.String)
	 */
	 
	public String design(String signedSessionid,boolean paramenterSessionID) {
		if(signSessionID || paramenterSessionID)
			return _design(signedSessionid);
		else
			return signedSessionid;
	}
	
	
	 
	public String _sign(String sessionid) {
		try {
			DESCipher desCipher = new DESCipher(signKey,DESCipher.type_encode);
			return desCipher.encrypt(sessionid);
		} catch (Exception e) {
			throw new SignSessionIDException(e);
		}
	}

	 
	public String _design(String signedSessionid) {
		try {
			DESCipher desCipher = new DESCipher(signKey,DESCipher.type_decode);
			return desCipher.decrypt(signedSessionid);
		} catch (Exception e) {
			throw new SignSessionIDException(e);
		}

	}

}
