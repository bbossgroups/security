package org.frameworkset.platform.entity;

public class UserUtil {

	public UserUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static User getUser(String userAccount)
	{
		User user = new User();
		user.setUserAccount(userAccount);
		user.setLeader("jackson");
		user.setUserSex("female");
		user.setUserName("张三");
		user.setPassword("123456");
		user.setUserId(userAccount);
		return user;
	}

}
