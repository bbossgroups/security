package org.frameworkset.security.session;

public interface SessionSerial {
	/**
	 * bboss is a xml serializable framework.
	 * json is a json serializable framework base on jackson 2.x or 1.x
	 */
	public static final String SERIAL_TYPE_BBOSS="bboss";
	public static final String SERIAL_TYPE_JSON="json";
	public String serialize(Object object); 
	public Object deserialize(String object);
	public String handleLikeCondition(Object condition);	
	
}
