package org.frameworkset.security.session.impl;



import org.frameworkset.security.session.SessionIDGenerator;

import com.frameworkset.util.SimpleStringUtil;

public class UUIDSessionIDGenerator implements SessionIDGenerator {

	@Override
	public String generateID() {
		 return SimpleStringUtil.getUUID();
	}

}
