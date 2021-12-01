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
	private long attackRuleCacheRefreshInterval = 60 * 60 * 1000l;
	private AttackFielterPolicy attackFielterPolicy;
	private Thread refresh = null;
	private boolean inited ;
	public WrapperAttackFielterPolicy(long attackRuleCacheRefreshInterval,AttackFielterPolicy attackFielterPolicy){
		this.attackFielterPolicy = attackFielterPolicy;
		this.attackRuleCacheRefreshInterval = attackRuleCacheRefreshInterval;
	}
	@Override
	public void init() {
		if(inited)
			return;
		synchronized (this) {
			if(inited)
				return;
			attackFielterPolicy.init();
			attackFielterPolicy.load();

			if (attackRuleCacheRefreshInterval > 0) {
				refresh = new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							synchronized (this) {
								try {
									wait(attackRuleCacheRefreshInterval);
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

		attackFielterPolicy.load();
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
}
