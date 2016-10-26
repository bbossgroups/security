package org.frameworkset.platform.security.authentication;


/**
 * 用户类型获取对象
 * <p>Title: UserTypeCallBack</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 * @Date 2007-11-6 11:17:23
 * @author biaoping.yin
 * @version 1.0
 */
public class UserTypeCallBack implements Callback{
	
	private String[] userTypes;
	
	private String prompt;

	public UserTypeCallBack(String prompt) {
		this.prompt = prompt;
	}

	public String[] getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(String[] userTypes) {
		this.userTypes = userTypes;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	

}
