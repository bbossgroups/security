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

/**
 * <p>Title: P.java</p>
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
public class P {
	private String name;
	private String value;
	/**
	 * a = b
	 * true:a==b if(haspermission) ok if(!haspermission) no a!=b no
	 * false:a==b if(haspermission) ok if(!haspermission) no a!=b ok
	 */
	private boolean mustmatchvaluetoallow = true;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isMustmatchvaluetoallow() {
		return mustmatchvaluetoallow;
	}
	public void setMustmatchvaluetoallow(boolean mustmatchvaluetoallow) {
		this.mustmatchvaluetoallow = mustmatchvaluetoallow;
	}

}
