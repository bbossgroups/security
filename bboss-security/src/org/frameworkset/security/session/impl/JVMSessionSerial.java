package org.frameworkset.security.session.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.frameworkset.util.ValueObjectUtil;


public class JVMSessionSerial extends BBossSessionSerial {

	@Override
	public String serialize(Object object) {
		if(object == null)
			return null;
		else
		{   
			if(object instanceof java.io.Serializable)
			{
				java.io.ObjectOutputStream out =null;
				ByteArrayOutputStream byteout = null;
				try {
					 byteout = new ByteArrayOutputStream();
					out = new ObjectOutputStream(byteout);
					out.writeObject(object);
					return ValueObjectUtil.byteArrayEncoder(byteout.toByteArray());
				} catch (IOException e) {
					throw new SessionException(e);
				}
				finally
				{
					try {
						if(byteout != null)
							byteout.close();
						if(out != null)
							out.close();
					} catch (IOException e) {
						 
					}
				}
			}
			else
			{
				StringBuilder ret = new StringBuilder();
				ret.append("b:");
				return ret.append(super.serialize(object)).toString();
			}
		}
	}

	@Override
	public Object deserialize(String object) {
		if(object == null)
			return null;
		if(!object.startsWith("b:"))
		{
			
			
			ByteArrayInputStream in = null;
			ObjectInputStream ois = null;
			try {
				byte[] v = ValueObjectUtil.byteArrayDecoder(object);
				  in = new ByteArrayInputStream(v);
				  ois = new ObjectInputStream(in);
				return ois.readObject();
			} catch (Exception e) {
				throw new SessionException(e);
			}
			finally
			{
				try {
					if(in != null)
						in.close();
					if(ois != null)
						ois.close();
				} catch (IOException e) {
					 
				}
			}
		}
		else
		{
			object = object.substring(2);
			return super.deserialize(object);
		}
		 
	}
	
	public static void main(String[] args)
	{
		System.out.println("b:sss".substring(2));
	}

}
