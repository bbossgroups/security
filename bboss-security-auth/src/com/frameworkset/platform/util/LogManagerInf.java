package com.frameworkset.platform.util;


public interface LogManagerInf {
	 //操作类型：无操作
	public static final int NULL_OPER_TYPE = 0 ;
    //操作类型：新增
	public static final int INSERT_OPER_TYPE = 1 ;
	//操作类型：更新
	public static final int UPDATE_OPER_TYPE = 2 ;
	//操作类型：删除
	public static final int DELETE_OPER_TYPE = 3 ;
	//操作类型：其他
	public static final int OTHER_OPER_TYPE = 4 ;
	void log(String userName, String operContent,
			String openModle, String operSource);
	/**
	 * <p>记录一条不带明细的日志</p>
	 * @param operUser
	 * @param operOrg
	 * @param logModule
	 * @param visitorial
	 * @param oper
	 * @param remark1
	 * @param operType
	 * @return
	 * @throws ManagerException
	 */
	public String log(String operUser,String operOrg,String logModule, String visitorial,
			String oper ,String remark1, int operType)  ;
}
