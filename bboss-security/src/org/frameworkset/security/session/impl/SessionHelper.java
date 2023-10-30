/*
 *  Copyright 2008 bbossgroups
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.frameworkset.security.session.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.Document;
import org.frameworkset.nosql.mongodb.MongoDBHelper;
import org.frameworkset.security.session.MongoDBUtil;
import org.frameworkset.security.session.SessionUtil;
import org.frameworkset.security.session.statics.AttributeInfo;

import com.frameworkset.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: SessionHelper.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年4月30日
 * @author biaoping.yin
 * @version 3.8.0
 */
public class SessionHelper {
	private static final Logger logger = LoggerFactory.getLogger(SessionHelper.class);
	
	public static void buildExtendFieldQueryCondition(Map<String, AttributeInfo> monitorAttributeArray,  BasicDBObject query,String serialType)
	{
		 
		if(monitorAttributeArray != null && monitorAttributeArray.size() > 0)
		{
			
			for(Entry<String, AttributeInfo> Entry:monitorAttributeArray.entrySet())
			{
				AttributeInfo  attr = Entry.getValue();
				if(attr.getType().equals("String"))
				{
					
					if(!attr.isLike())
					{
						if (!StringUtil.isEmpty((String)attr.getValue())) {
							Object value = SessionUtil.serial(attr.getValue(),serialType);
							query.append(attr.getName(), value);
						}
						else if(attr.isEnableEmptyValue())
						{
							BasicDBList values = new BasicDBList();
							values.add(new BasicDBObject(attr.getName(), SessionUtil.serial("",serialType)));
							values.add(new BasicDBObject(attr.getName(), null));
							query.append("$or", values);
						}
						

						
					}
					else
					{
						if (!StringUtil.isEmpty((String)attr.getValue())) {
							Object value = attr.getValue();
							//getLikeCondition(String condition,String serialType)
//							Pattern hosts = Pattern.compile("^<ps><p n=\"_dflt_\" s:t=\"String\"><\\!\\[CDATA\\[" + value + ".*$",
//									Pattern.CASE_INSENSITIVE);
							Pattern hosts = Pattern.compile(SessionUtil.getLikeCondition(value,  serialType),
									Pattern.CASE_INSENSITIVE);
							query.append(attr.getName(), new BasicDBObject("$regex",hosts));
						}
						else if(attr.isEnableEmptyValue())
						{
							 
							//values.add(null);
						
							BasicDBList values = new BasicDBList();
							values.add(new BasicDBObject(attr.getName(), SessionUtil.serial("",serialType)));
							values.add(new BasicDBObject(attr.getName(), null));
							query.append("$or", values);
							
							
						}
					}
				}
				else 
				{
					Object value = SessionUtil.serial(attr.getValue(),serialType);
					query.append(attr.getName(), value);
				}
				
			}
			
			
		}
		
		
		
	}
	public static List<AttributeInfo> evalqueryfiledsValue(AttributeInfo[] monitorAttributeArray, Document dbobject, String serialType)
	{
		List<AttributeInfo> extendAttrs = null;
		 
		if(monitorAttributeArray != null && monitorAttributeArray.length > 0)				
		{
			extendAttrs = new ArrayList<AttributeInfo>();
			AttributeInfo attrvalue = null;
			for(AttributeInfo attributeInfo:monitorAttributeArray)
			{
				try {
					attrvalue = attributeInfo.clone();
					String value = (String)dbobject.get(attrvalue.getName());
//					attrvalue.setValue(SessionUtil.unserial(  value,serialType));
					attrvalue.setValue(value);
					extendAttrs.add(attrvalue);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					logger.error("",e);
				}
				
			}
		}
		return extendAttrs;
		
	}

	
	public static Map<String,Object> toMap(DBObject object,boolean deserial) {

		Set set = object.keySet();
		if (set != null && set.size() > 0) {
			Map<String,Object> attrs = new HashMap<String,Object>();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (!MongoDBUtil.filter(key)) {
					Object value = object.get(key);
					try {
						attrs.put(MongoDBHelper.recoverSpecialChar(key),
								deserial?SessionUtil.unserial((String) value):value);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("",e);
					}
				}
			}
			return attrs;
		}
		return null;
	}
	
	public static Map<String,Object> toMap(String appkey,String contextpath,DBObject object,boolean deserial) {

		Set set = object.keySet();
		if (set != null && set.size() > 0) {
			Map<String,Object> attrs = new HashMap<String,Object>();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (!MongoDBUtil.filter(key)) {
					Object value = object.get(key);
					try {
						String temp = MongoDBHelper.recoverSpecialChar(key);
						temp = SessionUtil.dewraperAttributeName(appkey, contextpath, temp);
						if(temp != null)
							attrs.put(temp,
									deserial?SessionUtil.unserial((String) value):value);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("",e);
					}
				}
			}
			return attrs;
		}
		return null;
	}
}
