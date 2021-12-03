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

import org.frameworkset.util.AttackContext;
import org.frameworkset.util.AttackException;
import org.frameworkset.util.AttackFielterPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2021/12/1 9:02
 * @author biaoping.yin
 * @version 1.0
 */
public class WrapperAttackFielterPolicy extends BaseAttackFielterPolicy{
	private Logger logger = LoggerFactory.getLogger(WrapperAttackFielterPolicy.class);
	private long webXMLattackRuleCacheRefreshInterval = 60 * 60 * 1000l;
	private AttackFielterPolicy attackFielterPolicy;
	private Thread refresh = null;
	private boolean inited ;

	public WrapperAttackFielterPolicy(long webXMLattackRuleCacheRefreshInterval,AttackFielterPolicy attackFielterPolicy){
		this.attackFielterPolicy = attackFielterPolicy;
		this.webXMLattackRuleCacheRefreshInterval = webXMLattackRuleCacheRefreshInterval;
	}

	@Override
	public String[] getWhiteUrls() {
		return attackFielterPolicy.getWhiteUrls();
	}

	@Override
	public boolean isDisable() {
		return attackFielterPolicy.isDisable();
	}

	@Override
	public Long getAttackRuleCacheRefreshInterval() {
		return attackFielterPolicy.getAttackRuleCacheRefreshInterval();
	}

	@Override
	public void init() {
		if(inited)
			return;
		synchronized (this) {
			if(inited)
				return;
			attackFielterPolicy.init();
			load();
//			long temp = webXMLattackRuleCacheRefreshInterval;
//			if(attackFielterPolicy.getAttackRuleCacheRefreshInterval() != null && attackFielterPolicy.getAttackRuleCacheRefreshInterval() > 0){
//				temp = attackFielterPolicy.getAttackRuleCacheRefreshInterval() * 1000l;
//			}
			if (webXMLattackRuleCacheRefreshInterval > 0) {
//				final long t = temp;
				refresh = new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
//							long temp_i = t;
//							if(attackFielterPolicy.getAttackRuleCacheRefreshInterval() != null && attackFielterPolicy.getAttackRuleCacheRefreshInterval() > 0){
//								temp_i = attackFielterPolicy.getAttackRuleCacheRefreshInterval() * 1000l;
//							}
							synchronized (this) {
								try {
									wait(webXMLattackRuleCacheRefreshInterval);
								} catch (InterruptedException e) {
									logger.warn("", e);
									break;
								}
							}
							load();
						}

					}
				});
				refresh.setDaemon(true);
				refresh.start();
			}
			inited = true;
		}
	}

	@Override
	public void load(){
//		String[] xssWallwhilelist = attackFielterPolicy.getXSSWallwhilelist();
//		String[] xssWallfilterrules = attackFielterPolicy.getXSSWallfilterrules();
//		String[] sensitiveWallwhilelist = attackFielterPolicy.getSensitiveWallwhilelist();
//		String[] sensitiveWallfilterrules = attackFielterPolicy.getSensitiveWallfilterrules();
//
//		this.xssWallwhilelist = xssWallwhilelist;
//		this.xssWallfilterrules = xssWallfilterrules;
//		this.sensitiveWallwhilelist = sensitiveWallwhilelist;
//		this.sensitiveWallfilterrules = sensitiveWallfilterrules;
		try {
			attackFielterPolicy.load();
		}
		catch (Exception e){
			logger.warn("Load attackFielterPolicy failed:",e);
		}

	}

	@Override
	public String[] getXSSWallwhilelist() {
		return attackFielterPolicy.getXSSWallwhilelist();
	}

	@Override
	public String[] getXSSWallfilterrules() {
		return attackFielterPolicy.getXSSWallfilterrules();
	}

	@Override
	public String[] getSensitiveWallwhilelist() {
		return attackFielterPolicy.getSensitiveWallwhilelist();
	}

	@Override
	public String[] getSensitiveWallfilterrules() {
		return attackFielterPolicy.getSensitiveWallfilterrules();
	}

	@Override
	public void attackHandle(AttackContext attackContext) throws AttackException {
		attackFielterPolicy.attackHandle(attackContext);
	}

	/**
	 * xss检测
	 * @return
	 */
	@Override
	public boolean xssCheck(String paramValue,String xssWallRule){
		return attackFielterPolicy.xssCheck( paramValue, xssWallRule);
	}

	/**
	 * 敏感词检测
	 * @return
	 */
	@Override
	public boolean sensitiveCheck(String paramValue,String sensitiveWallRule){
		return attackFielterPolicy.sensitiveCheck( paramValue, sensitiveWallRule);
	}

}
