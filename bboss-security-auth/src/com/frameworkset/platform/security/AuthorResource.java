package com.frameworkset.platform.security;

import java.util.ArrayList;
import java.util.List;

import com.frameworkset.platform.security.authorization.impl.PermissionTokenMap;
import com.frameworkset.platform.security.authorization.impl.ResourceToken;

public class AuthorResource {
	protected List<ResourceToken> authorResources;
	
	public void addAuthorResource(String authorResource)
	{
		if(authorResources == null)
			authorResources = new ArrayList<ResourceToken>();
		if(authorResource == null || authorResource.equals(""))
			return ;
		this.authorResources.add(PermissionTokenMap.buildResourceToken(authorResource));
	}
	
	
	public List<ResourceToken> getAuthorResources() {
		return authorResources;
	}
	
	
}
