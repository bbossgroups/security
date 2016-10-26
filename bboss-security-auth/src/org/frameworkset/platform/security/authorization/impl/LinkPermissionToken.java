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

import org.frameworkset.platform.security.AccessControl;

/**
 * <p>Title: LinkPermissionToken.java</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>bboss workgroup</p>
 * @Date 2013-10-21
 * @author biaoping.yin
 * @version 1.0
 */
public class LinkPermissionToken {
	private String url;
	private boolean unprotected;
	private List<PermissionToken> permissionTokensWithParams;
	private List<PermissionToken> permissionTokensWithNoParams;
	
	public LinkPermissionToken(String url, boolean unprotected,
			List<PermissionToken> permissionTokens) {
		super();
		this.url = url;
		this.unprotected = unprotected;
//		this.permissionTokens = permissionTokens;
		splitToken( permissionTokens);
	}
	
	
	public LinkPermissionToken(String url, boolean unprotected) {
		super();
		this.url = url;
		this.unprotected = unprotected;
//		this.permissionTokens = permissionTokens;
		permissionTokensWithParams = new ArrayList<PermissionToken>();
		permissionTokensWithNoParams = new ArrayList<PermissionToken>();
	}
	private void splitToken(List<PermissionToken> permissionTokens)
	{
		if(permissionTokens != null && permissionTokens.size() > 0)
		{
			permissionTokensWithParams = new ArrayList<PermissionToken>();
			permissionTokensWithNoParams = new ArrayList<PermissionToken>();
			for(PermissionToken token:permissionTokens)
			{
				if(token.hasParamCondition())
					permissionTokensWithParams.add(token);
				else
					permissionTokensWithNoParams.add(token);
			}
		}
	}
	public void addPermissionToken(PermissionToken token)
	{
		if(token.hasParamCondition())
			permissionTokensWithParams.add(token);
		else
			permissionTokensWithNoParams.add(token);
	}
	public LinkPermissionToken() {
		// TODO Auto-generated constructor stub
	}
	public String getUrl() {
		return url;
	}
	public boolean isUnprotected() {
		return unprotected;
	}
	public List<PermissionToken> getPermissionTokensWithParams() {
		return permissionTokensWithParams;
	}
	
	public List<PermissionToken> getPermissionTokensWithNoParams() {
		return permissionTokensWithNoParams;
	}
//	public List<List<P>> getParamConditions() {
//		return paramConditions;
//	}
//	public void setParamConditions(List<List<P>> paramConditions) {
//		if(paramConditions == null || paramConditions.size() == 0)
//			return;
//		this.paramConditions = paramConditions;
//		for(List<P> ps :paramConditions)
//		{
//			if(ps == PermissionTokenRegion.dual)
//			{
//				this.paramConditions.clear();
//				break;
//			}
//		}
//		
//	}
	
	

}
