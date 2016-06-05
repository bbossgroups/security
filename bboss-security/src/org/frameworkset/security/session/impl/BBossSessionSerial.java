package org.frameworkset.security.session.impl;

import org.frameworkset.soa.ObjectSerializable;

public class BBossSessionSerial extends AbstractSessionSerial {

	public BBossSessionSerial() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String serialize(Object value) {
		if(value != null)
		{
			try {
				value = ObjectSerializable.toXML(value);
 			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public Object deserialize(String value) {
		if(value == null)
			return null;
		return ObjectSerializable.toBean((String)value, Object.class);
	}
	
	@Override
	public String handleLikeCondition(Object condition) {
		// TODO Auto-generated method stub
		return new StringBuilder().append("^<ps><p n=\"_dflt_\" s:t=\"String\"><\\!\\[CDATA\\[" ).append( condition ).append( ".*$").toString();
	}
	
	


}
