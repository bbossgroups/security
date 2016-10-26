package org.frameworkset.platform.util;


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
	/**
	 * 认证操作
	 */
	public static final int AUTHENTICATE_OPER_TYPE = 5 ;
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
	public void log(String operUser,String operOrg,String logModule, String visitorial,
			String oper ,String remark1, int operType)  ;
	
	enum loglevel
	{
		RED("红色", 1), GREEN("绿色", 2), BLANK("白色", 3), YELLO("黄色", 4);
        // 成员变量
        private String name;
        private int index;

        // 构造方法
        private loglevel(String name, int index) {
            this.name = name;
            this.index = index;
        }

        // 普通方法
        public static String getName(int index) {
            for (loglevel c : loglevel.values()) {
                if (c.getIndex() == index) {
                    return c.name;
                }
            }
            return null;
        }

        // get set 方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

	
}
