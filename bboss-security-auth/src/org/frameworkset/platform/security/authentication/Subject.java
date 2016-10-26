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
package org.frameworkset.platform.security.authentication;

import java.security.Principal;

/**
 * <p>Title: Subject.java</p> 
 * <p>Description: </p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2008</p>
 * @Date 2014年5月8日
 * @author biaoping.yin
 * @version 3.8.0
 */
public class Subject {
	private Credential  credential;
	private  Principal   principal;
	public Subject()
	{
//		credential = new ArrayList<Credential>();
//		principal = new ArrayList<Principal>();
	}
	public Credential getCredential() {
		return credential;
	}
	public void setCredential(Credential credential) {
		this.credential = credential;
	}
	public Principal getPrincipal() {
		return principal;
	}
	public void setPrincipal( Principal principal) {
		this.principal = principal;
	}
	public void addAuthPrincipal(Principal authPrincipal)
	{
		this.principal =authPrincipal;
	}
	public void addCredential(Credential credential)
	{
		this.credential =credential;
	}
}
