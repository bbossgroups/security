package org.frameworkset.platform.security.authentication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class CheckCallBack implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String loginModule;
    private Map<String,Object> callBacks = new HashMap<String,Object>();
//    private AttributeQueue  list = new AttributeQueue();
    /**
     * userName
userID
password
orgId
logincount
userAccount
remark1
remark2
remark3
remark4
remark5
userAddress
userEmail
userFax
userHometel
userIdcard
userMobiletel1
userMobiletel2
userOicq
userPinyin
userPostalcode
userSex
userType
userWorknumber
userWorktel
userBirthday
userRegdate
userSn
userIsvalid
passwordExpiredTime
passwordUpdateTime
     * @param userAttribute
     * @return
     */
    public Object getUserAttribute(String userAttribute)
    {
    	Object attr = (Object)callBacks.get(userAttribute);
//    	if(attr == null)
//    		return null;
        return attr;
    }
    public Map<String,Object> getCallBacks()
    {
    	return this.callBacks;
    }
    public void setUserAttribute(String userAttribute,Object value)
    {
//        Attribute attribute = new Attribute(userAttribute,value);
//        this.list.add(attribute);
        callBacks.put(userAttribute,value);
    }

//    public AttributeQueue getAttributeQueue()
//    {
//        return this.list;
//    }

    public String getLoginModule() {
        return loginModule;
    }

    public static class Attribute implements Serializable
    {
    	public Attribute()
    	{
    		
    	}
        public Attribute(String name,Object value)
        {
            this.name = name;
            this.value = value;
        }
        String name;
        Object value;
        public String getName()
        {
            return name;
        }
        public Object getValue()
        {
            return value;
        }
    }

    public static class AttributeQueue implements Serializable
    {
        List list = new ArrayList();
        public void add(Attribute attribute)
        {
            this.list.add(attribute);
        }

        public Attribute get(int i)
        {
            return (Attribute)this.list.get(i);
        }

        public int size()
        {
            return list.size();
        }
    }

}
