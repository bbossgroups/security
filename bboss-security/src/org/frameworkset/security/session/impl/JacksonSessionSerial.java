package org.frameworkset.security.session.impl;

import com.frameworkset.util.SimpleStringUtil;
import com.frameworkset.util.ValueObjectUtil;

public class JacksonSessionSerial extends AbstractSessionSerial{

	public JacksonSessionSerial() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String serialize(Object object) {
		 
		
		String result =  SimpleStringUtil.object2json(object);
		if(result != null)
		{
			
			result = new StringBuilder().append(ValueObjectUtil.getSimpleTypeName(object.getClass())).append(":").append(result).toString();
		}
		return result;
	}

	@Override
	public Object deserialize(String object) {
		
		if(object == null )
		{
			return null;
		}
		int idx = object.indexOf(':');
		String classInfo = object.substring(0, idx);
		String data = object.substring(idx+1);
		try
		{
			Class clazz = ValueObjectUtil.getClass(classInfo);
			return SimpleStringUtil.json2Object(data, clazz);
		}
		catch(ClassNotFoundException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		
	}

	@Override
	public String handleLikeCondition(Object condition) {
		if(condition instanceof String)
			return new StringBuilder().append("^String:\"").append( condition ).append( ".*$").toString();
		else
			return new StringBuilder().append("^" ).append(ValueObjectUtil.getSimpleTypeName(condition.getClass())).append(":").append( condition ).append( ".*$").toString();
			
	}

}
