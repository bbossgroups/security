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
public class DefaultAttackFielterPolicy extends BaseAttackFielterPolicy{
	private static Logger logger = LoggerFactory.getLogger(DefaultAttackFielterPolicy.class);



	@Override
	public void attackHandle(AttackContext attackContext) throws AttackException {
		String values[] = attackContext.getValues();
		int position = attackContext.getPosition();
		String value = values[position];
		values[position] = null;
		if(logger.isWarnEnabled()) {
			logger.warn(new StringBuilder().append("参数").append(attackContext.getParamName()).append("值").append(value).append("包含敏感词:"
			).append(attackContext.getAttackRule()).append(",存在安全隐患,系统自动过滤掉参数值!").toString());
		}

	}
}
