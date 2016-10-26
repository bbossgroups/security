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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.frameworkset.util.AntPathMatcher;
import org.frameworkset.util.PathMatcher;

/**
 * <p>Title: PermissionTokenRegion.java</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>bboss workgroup</p>
 * @Date 2013-8-14
 * @author biaoping.yin
 * @version 1.0
 */
public class PermissionTokenRegion {
	/**
	 * Map<String,        Map<String,         List<PermissionToken>>>
	 *     region(资源区域划分)       url（或其他）              资源类型/资源标识/资源操作(一个url可能会对应多个资源操作)
	 */
	private Map<String,Map<String,List<PermissionToken>>> regionResourcTokenMap;
	
//	private Map<RID,List<PermissionToken>> resourcTokenMap;
	/**
	 *
	 *@Deprecated 
	 *@see unprotectedResourcTokenMap 
	 */	
	
	private Map<String,Map<RID,List<P>>> regionUnprotectedResourcTokenMap;//冗余对象，
	private PermissionTokenMap permissionTokenMap;
	
	public PermissionTokenRegion (PermissionTokenMap permissionTokenMap)
	{
		regionResourcTokenMap = new HashMap<String,Map<String,List<PermissionToken>>>();
//		resourcTokenMap = new HashMap<RID,List<PermissionToken>>();
		
		regionUnprotectedResourcTokenMap = new HashMap<String,Map<RID,List<P>>>();		
		this.permissionTokenMap = permissionTokenMap;
	}
	
	
	
	public void addPermissionToken(ResourceToken url,String region,PermissionToken token)
	{
		if(url == null || url.equals(""))
			return;
		token.setConditions(url.getConditions());
		
		//解析url资源中对应的参数，作为权限判断的依据，除了识别url外，有权限外，还需要识别参数的值是否匹配，这样才能决定是否有访问权限
		
		
		token.setHasParamCondition(url.hasParamCondition());
		token.setParamConditions(url.getParamConditions());
		Map<String,List<PermissionToken>> resourceTokens = this.regionResourcTokenMap.get(region);
		if(resourceTokens == null)
		{
			resourceTokens = new HashMap<String,List<PermissionToken>>();
			this.regionResourcTokenMap.put(region, resourceTokens);
		}
		
//		RID rid = new RID(url.getUrl(),true);
		List<PermissionToken> tokens = resourceTokens.get(url.getUrl());
		if(tokens == null)
		{
			tokens = new ArrayList<PermissionToken>();
			resourceTokens.put(url.getUrl(), tokens);
					
		}
		if(!token.hasParamCondition())
			tokens.add(token);
		else //带参数的url资源优先做权限检测，如果url中带了相应的参数，但是参数值不匹配，则直接阻止对url的访问
			tokens.add(0,token);
	}
	
	public void resetPermission()
	{
//		if(resourcTokenMap != null)
//		{
//			resourcTokenMap.clear();
//		}
		
		if(regionResourcTokenMap != null)
		{
			Iterator<Entry<String, Map<String, List<PermissionToken>>>> entries = this.regionResourcTokenMap.entrySet().iterator();
			while(entries.hasNext())
			{
				Entry<String, Map<String, List<PermissionToken>>> entry = entries.next();
				Map<String, List<PermissionToken>> resourceTokens = entry.getValue();
				resourceTokens.clear();
			}
			regionResourcTokenMap.clear();
		}
		
		Iterator<Entry<String, Map<RID, List<P>>>> uentries = this.regionUnprotectedResourcTokenMap.entrySet().iterator();
		while(uentries.hasNext())
		{
			Entry<String, Map<RID, List<P>>> entry = uentries.next();
			entry.getValue().clear();
		}
		this.regionUnprotectedResourcTokenMap.clear();
	}
	
	public void resetPermissionByRegion(String region)
	{		
		if(regionResourcTokenMap != null)
		{
			Map<String,List<PermissionToken>> resourceTokens = regionResourcTokenMap.get(region);
			
			if(resourceTokens != null)
				resourceTokens.clear();
			
		}
		Map<RID,List<P>> resourceTokens = this.regionUnprotectedResourcTokenMap.get(region);
		if(resourceTokens != null)
			resourceTokens.clear();
	}
	
	public List<List<P>> isUnprotectedURL(RID rid)
	{
	
		List<List<P>> ps = new ArrayList<List<P>>();
		Iterator<Entry<String, Map<RID, List<P>>>> uentries = this.regionUnprotectedResourcTokenMap.entrySet().iterator();
		while(uentries.hasNext())
		{
			Entry<String, Map<RID, List<P>>> entry = uentries.next();
			
			Map<RID, List<P>> tokens = entry.getValue();
			Iterator<Entry<RID, List<P>>> its = tokens.entrySet().iterator();
			while(its.hasNext())
			{
				Entry<RID, List<P>> rentry = its.next();
				if(rentry.getKey().match(rid))
				{
					List<P> tps =  rentry.getValue();
					if(tps !=null && tps == dual)
					{
						ps.add(tps);
					}
					else if(tps != null && tps.size() > 0)
					{
						ps.add(0,tps);
					}
				}
				
			}
			
				
				
			
				
		}
		
		if(ps.size() == 0)
			return null;
		else 
			return ps;
	}

	
	
	
	
	public static final List<P> dual = new ArrayList<P>();
	public void addUnprotectedPermissionToken(ResourceToken url, String region,
			PermissionToken token) {
		
		List<P> paramConditions = url.getParamConditions();
		
		
		Map<RID,List<P>> regionTokenMap = this.regionUnprotectedResourcTokenMap.get(region);
		if(regionTokenMap == null)
		{
			regionTokenMap = new HashMap<RID,List<P>>();
			this.regionUnprotectedResourcTokenMap.put(region,regionTokenMap);
		}
		RID key = new RID(url.getUrl(),true);
		if(paramConditions == null || paramConditions.size() == 0)
			regionTokenMap.put(key, dual);
		else
		{
			regionTokenMap.put(key, paramConditions);
		}
		
	}
	
	private static PathMatcher pathMatcher = new AntPathMatcher();
	
	public List<PermissionToken> getAllURLToken(String rid) {
		List<PermissionToken> ptokens = new ArrayList<PermissionToken>();
//		if(resourcTokenMap.size() > 0)
//		{
//			List<PermissionToken> tokens = new ArrayList<PermissionToken>();
//			Iterator<Entry<RID, List<PermissionToken>>> resources = resourcTokenMap.entrySet().iterator();
//			while(resources.hasNext())
//			{
//				Entry<RID, List<PermissionToken>> entry = resources.next();
//				if(entry.getKey().match(rid))
//					tokens.addAll(entry.getValue());
//			}
//			if(tokens.size() > 0)
//			{
//				ptokens.addAll(tokens);
//				
//			}
//		}
		if((this.regionResourcTokenMap == null || this.regionResourcTokenMap.size() == 0))
			return ptokens;
		Iterator<Entry<String, Map<String, List<PermissionToken>>>> entries = this.regionResourcTokenMap.entrySet().iterator();		
		while(entries.hasNext())
		{
			Entry<String, Map<String, List<PermissionToken>>> entry = entries.next();
			Map<String, List<PermissionToken>> resourceTokens_ = entry.getValue();
			List<PermissionToken> tokens = new ArrayList<PermissionToken>();
			Iterator<Entry<String, List<PermissionToken>>> resources = resourceTokens_.entrySet().iterator();
			while(resources.hasNext())
			{
				Entry<String, List<PermissionToken>> entry_= resources.next();
				if(pathMatcher.match(entry_.getKey(),rid))
					tokens.addAll(entry_.getValue());
			}
			if(tokens.size() > 0)
			{
				ptokens.addAll(tokens);				
			}
		}
		return ptokens;
	}


}
