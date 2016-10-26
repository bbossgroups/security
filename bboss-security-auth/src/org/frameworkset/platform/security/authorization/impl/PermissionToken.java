/**
 *  Copyright 2008 biaoping.yin
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
package org.frameworkset.platform.security.authorization.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * <p>Title: PermissionToken.java</p>
 *
 * <p>Description: 标识权限资源
 * 资源类型，资源id，资源操作
 * 主要用于URL和资源权限的反向索引
 * </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>bboss workgroup</p>
 * @Date 2013-7-15
 * @author biaoping.yin
 * @version 1.0
 */
public class PermissionToken {
	private String resourceType;
	private String resourcedID;
	private Map<String,Object> conditions;
	private String resouceAuthCodeParamName;
	private String operation;
	private boolean hasParamCondition;
	private boolean useResourceAuthCode;
	private boolean resouceAuthCodeRequired = true;
	private String region;
	
	private boolean validate = true;
	
	/**
	 * 权限码，也就是url后面跟随的参数值，如果资源操作都匹配，但是参数不匹配，同样不允许访问
	 */
	private List<P> paramConditions;
	public PermissionToken addParamCondition(String name,String value)
	{
		if(paramConditions == null)
			paramConditions = new ArrayList<P>();
		P p = new P();
		p.setName(name);
		p.setValue(value);
		this.paramConditions.add(p);
		return this;
	}
	public String getResouceAuthCodeParamName() {
		return resouceAuthCodeParamName;
	}


	public boolean hasParamCondition()
	{
		return hasParamCondition;
	}
	public boolean isResouceAuthCodeRequired() {
		return resouceAuthCodeRequired;
	}
	
	public void setConditions(Map<String, Object> conditions) {
		this.conditions = conditions;
		if(conditions != null)
		{
			this.resouceAuthCodeParamName = (String)this.conditions.get(PermissionTokenMap.RESOURCE_PARAMNAME);
			if(this.resouceAuthCodeParamName != null && !this.resouceAuthCodeParamName.equals(""))
				useResourceAuthCode = true;
			Boolean temp = (Boolean)this.conditions.get("required");
			if(temp != null)
			{
				this.resouceAuthCodeRequired = temp.booleanValue();
			}
		}
	}

	public boolean useResourceAuthCode()
	{
		return useResourceAuthCode;
	}
	
	public Map<String, Object> getConditions() {
		return conditions;
	}
	public String getCondition(String name) {
		return (String)conditions.get(name);
	}
	public Boolean getBooleanCondition(String name) {
		return (Boolean)conditions.get(name);
	}
	public boolean containCondition()
	{
		return conditions != null;
	}
	public PermissionToken(String resourceType, String resourcedID,
			String operation) {
		super();
		this.resourceType = resourceType;
		this.resourcedID = resourcedID;
		this.operation = operation;
	}
	public PermissionToken(String resourceType, 
			String operation) {
		super();
		this.resourceType = resourceType;
		
		this.operation = operation;
	}
	
	public String getResourceType() {
		return resourceType;
	}
	public String getResourcedID() {
		return resourcedID;
	}
	public String getOperation() {
		return operation;
	}
	public List<P> getParamConditions() {
		return paramConditions;
	}
	public void setHasParamCondition(boolean hasParamCondition) {
		this.hasParamCondition = hasParamCondition;
	}
	public void setParamConditions(List<P> paramConditions) {
		this.paramConditions = paramConditions;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public boolean isValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
