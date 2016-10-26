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

import org.frameworkset.util.AntPathMatcher;
import org.frameworkset.util.PathMatcher;

/**
 * <p>Title: RID.java</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>bboss workgroup</p>
 * @Date 2013-8-13
 * @author biaoping.yin
 * @version 1.0
 */
public class RID {
	private boolean key = false;
	private static PathMatcher pathMatcher = new AntPathMatcher();
	public RID(String url) {
		super();
		this.url = url;
		
	}
	public RID(String url,boolean isKey) {
		super();
		this.url = url;
		this.key = isKey;
		
	}

	private String url;
	
	public boolean isKey()
	{
		return this.key;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return url.hashCode();
	}

//	@Override
//	public boolean equals(Object obj) {
//		if(obj == null )
//			return false;
//		RID other = (RID)obj;
//		if((this.isKey && other.isKey) || (!this.isKey && !other.isKey) )
//			return url.equals(other.url);
//		if(this.isKey)
//		
//			return pathMatcher.match(this.url, other.url);
//		else
//			return pathMatcher.match(other.url, this.url);
////		return url.equals(other.url);
//	}
	
	public boolean match(RID url)
	{
		return pathMatcher.match(this.url, url.url);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null )
			return false;
		RID other = (RID)obj;
//		if((this.isKey && other.isKey) || (!this.isKey && !other.isKey) )
		return url.equals(other.url);
//		if(this.isKey)
//		
//			return pathMatcher.match(this.url, other.url);
//		else
//			return pathMatcher.match(other.url, this.url);
//		return url.equals(other.url);
	}
	

}
