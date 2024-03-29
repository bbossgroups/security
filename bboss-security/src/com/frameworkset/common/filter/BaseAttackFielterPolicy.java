package com.frameworkset.common.filter;
/**
 * Copyright 2020 bboss
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.frameworkset.util.AttackFielterPolicy;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2021/12/1 9:02
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class BaseAttackFielterPolicy implements AttackFielterPolicy {
//	protected WebApplicationContext context;
	protected volatile String[] xssWallwhilelist;
	protected volatile String[] xssWallfilterrules;
	protected volatile String[] sensitiveWallwhilelist;
	protected volatile String[] sensitiveWallfilterrules;
	protected volatile Long attackRuleCacheRefreshInterval;
	protected volatile String[] whiteUrls;
	protected volatile boolean disable;
	@Override
	public void  load(){

	}
	/**
	 * xss检测
	 * @return
	 */
	@Override
	public boolean xssCheck(String paramValue,String xssWallRule){
		return paramValue.indexOf(xssWallRule) >= 0;
	}

	/**
	 * 敏感词检测
	 * @return
	 */
	@Override
	public boolean sensitiveCheck(String paramValue,String sensitiveWallRule){
		return paramValue.indexOf(sensitiveWallRule) >= 0;
	}


	@Override
	public String[] getWhiteUrls() {
		return whiteUrls;
	}

	public boolean isDisable() {
		return disable;
	}

	/**
	 * 单位：秒
	 * @return
	 */
	public Long getAttackRuleCacheRefreshInterval(){
		return attackRuleCacheRefreshInterval;
	}
	@Override
	public void init() {
//		if(context == null)
//			context = org.frameworkset.web.servlet.support.WebApplicationContextUtils.getWebApplicationContext();//获取mvc容器实例
	}

	@Override
	public String[] getXSSWallwhilelist() {
		return xssWallwhilelist;
	}

	@Override
	public String[] getXSSWallfilterrules() {
		return xssWallfilterrules;
	}

	@Override
	public String[] getSensitiveWallwhilelist() {
		return sensitiveWallwhilelist;
	}

	@Override
	public String[] getSensitiveWallfilterrules() {
		return sensitiveWallfilterrules;
	}



	public void setXssWallwhilelist(String[] xssWallwhilelist) {
		this.xssWallwhilelist = xssWallwhilelist;
	}

	public void setXssWallfilterrules(String[] xssWallfilterrules) {
		this.xssWallfilterrules = xssWallfilterrules;
	}

	public void setSensitiveWallwhilelist(String[] sensitiveWallwhilelist) {
		this.sensitiveWallwhilelist = sensitiveWallwhilelist;
	}

	public void setSensitiveWallfilterrules(String[] sensitiveWallfilterrules) {
		this.sensitiveWallfilterrules = sensitiveWallfilterrules;
	}
}
