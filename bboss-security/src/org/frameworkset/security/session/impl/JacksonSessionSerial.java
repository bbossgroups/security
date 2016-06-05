package org.frameworkset.security.session.impl;

import com.frameworkset.util.SimpleStringUtil;

public class JacksonSessionSerial extends AbstractSessionSerial{

	public JacksonSessionSerial() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object serialize(Object object) {
		// TODO Auto-generated method stub
		return SimpleStringUtil.object2json(object);
	}

	@Override
	public Object deserialize(String object) {
		// TODO Auto-generated method stub
		return SimpleStringUtil.json2Object(object, Object.class);
	}

}
