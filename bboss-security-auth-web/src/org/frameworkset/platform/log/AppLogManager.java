package org.frameworkset.platform.log;

import org.apache.log4j.Logger;

public class AppLogManager extends DefaultLogManager{
	private static Logger logger = Logger.getLogger(AppLogManager.class);

	public AppLogManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void log(String userName, String operContent, String openModle, String operSource) {
		StringBuilder log = new StringBuilder();
		log.append("操作人：").append(userName).append(",操作内容：").append(operContent).append(",操作模块：").append(openModle).append(",操作来源：")
		.append(operSource);
		logger.debug(log.toString());
	}

	@Override
	public void log(String operUser, String operOrg, String logModule, String visitorial, String oper, String remark1,
			int operType) {
		StringBuilder log = new StringBuilder();
		log.append("操作人：").append(operUser).append(",操作部门：").append(operOrg)
		.append(",操作内容：").append(oper)
		.append(",操作模块：").append(logModule).append(",操作来源：")
		.append(visitorial).append(",备注：")
		.append(remark1).append(",备注：")
		.append(remark1);
		logger.debug(log.toString());
		 
	}

}
