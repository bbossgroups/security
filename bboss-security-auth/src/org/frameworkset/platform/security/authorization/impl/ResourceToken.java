package org.frameworkset.platform.security.authorization.impl;

import java.util.List;
import java.util.Map;

public class ResourceToken {
	private String url;
	private String origineUrl;
	private String resourceAuthCode;
	private boolean requiredAuthCode;
	private boolean useResourceAuthCode;
	/**
	 * 权限码，也就是url后面跟随的参数值，如果资源操作都匹配，但是参数不匹配，同样不允许访问
	 */
	private List<P> paramConditions;
	private boolean hasParamCondition;
	
	private Map<String,Object> conditions;
	
	public void setConditions(Map<String, Object> conditions) {
		this.conditions = conditions;
		if(conditions != null)
		{
			this.resourceAuthCode = (String)this.conditions.get(PermissionTokenMap.RESOURCE_PARAMNAME);
			if(this.resourceAuthCode != null && !this.resourceAuthCode.equals(""))
				useResourceAuthCode = true;
			Boolean temp = (Boolean)this.conditions.get("required");
			if(temp != null)
			{
				this.requiredAuthCode = temp.booleanValue();
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getResourceAuthCode() {
		return resourceAuthCode;
	}
	public void setResourceAuthCode(String resourceAuthCode) {
		this.resourceAuthCode = resourceAuthCode;
	}
	public boolean isRequiredAuthCode() {
		return requiredAuthCode;
	}
	public void setRequiredAuthCode(boolean requiredAuthCode) {
		this.requiredAuthCode = requiredAuthCode;
	}
	public List<P> getParamConditions() {
		return paramConditions;
	}
	public void setParamConditions(List<P> paramConditions) {
		this.paramConditions = paramConditions;
		if(this.paramConditions!= null && this.paramConditions.size() > 0)
			this.hasParamCondition = true;
	}
	public boolean hasParamCondition()
	{
		return this.hasParamCondition;
	}

	public String getOrigineUrl() {
		return origineUrl;
	}

	public void setOrigineUrl(String origineUrl) {
		this.origineUrl = origineUrl;
	}
	
	public String toString()
	{
		return this.origineUrl;
	}
}
