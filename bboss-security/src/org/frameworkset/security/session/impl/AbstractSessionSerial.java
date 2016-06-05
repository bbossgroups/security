package org.frameworkset.security.session.impl;

import org.frameworkset.security.session.SessionSerial;

public abstract class AbstractSessionSerial implements SessionSerial {

	public AbstractSessionSerial() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String handleLikeCondition(Object condition) {
		// TODO Auto-generated method stub
		return new StringBuilder().append("^" ).append( condition ).append( ".*$").toString();
	}

	

}
