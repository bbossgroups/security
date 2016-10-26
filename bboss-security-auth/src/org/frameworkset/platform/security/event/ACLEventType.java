package org.frameworkset.platform.security.event;

import org.frameworkset.event.EventType;
import org.frameworkset.event.ResourceChangeEventTypeImpl;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author biaoping.yin
 * @version 1.0
 */
public interface ACLEventType extends EventType{

	
 
//	/**
//	 * 用户信息变化事件类型，变化的用户名称作为消息源
//	 */
//	// 
//	public static final int USER_INFO_CHANGE = 0;
//
//	// public static final int USER_INFO_DELETE = 1;
//	// public static final int USER_INFO_UPDATE = 2;
//	// public static final int USER_INFO_ADD = 3;
//	/**
//	 * 用户组信息变化事件类型 ，组中对应的用户名数组作为消息源 new String[][] { {组名1，用户名,用户名1、用户名2、用户名3},
//	 * {组名2，用户名,用户名1、用户名2、用户名3} }
//	 */
//	public static final int GROUP_INFO_CHANGE = 4;
//	// public static final int GROUP_INFO_DELETE = 5;
//	// public static final int GROUP_INFO_UPDATE = 6;
//	// public static final int GROUP_INFO_ADD = 7;
//	/**
//	 * 角色信息变化，角色名称作为消息源
//	 * 
//	 */
//	public static final int ROLE_INFO_CHANGE = 8;
//
//	// public static final int ROLE_INFO_UPDATE = 9;
//	// public static final int ROLE_INFO_DELETE = 10;
//
//	/**
//	 * 资源信息变化，资源名称和资源类型作为消息源，以数组方式封装资源名称和资源类型 消息格式为： new String[] {资源名称、资源类型}
//	 */
//	//
//	public static final int RESOURCE_INFO_CHANGE = 11;
//	//
//	public static final int RESOURCE_ROLE_INFO_CHANGE = 12;
//	// public static final int RESOURCE_INFO_UPDATE = 12;
//	// public static final int RESOURCE_INFO_DELETE = 13;
//
//	/**
//	 * 权限许可变化事件类型,角色/资源/操作关系变化 ，将角色名称、资源名称、资源类型、操作封装成数组作为消息源 消息格式为 new String[]
//	 * {角色名称,资源名称、资源类型、操作}
//	 */
//	public static final int PERMISSION_CHANGE = 14;
//	// public static final int PERMISSION_ADD = 15;
//	// public static final int PERMISSION_DELETE = 16;
//
//	/**
//	 * 用户/角色关系变化，以用户名，角色名称作为消息源，消息格式： new String[] {用户名,角色名}
//	 * 
//	 */
//	//
//	public static final int USER_ROLE_INFO_CHANGE = 17;
//	// public static final int USER_ROLE_INFO_ADD = 18;
//	// public static final int USER_ROLE_INFO_DELETE = 19;
//
//	/**
//	 * 用户组/角色关系变化,消息源为组中包含的用户名称数组 消息格式 new String[][] {
//	 * {组名1，用户名,用户名1、用户名2、用户名3}, {组名2，用户名,用户名1、用户名2、用户名3} }
//	 */
//	//	
//	public static final int GROUP_ROLE_INFO_CHANGE = 20;
//	// public static final int GROUP_ROLE_INFO_ADD = 21;
//	// public static final int GROUP_ROLE_INFO_DELETE = 22;
//
//	/**
//	 * 用户/用户组关系变化，在组中添加/删除用户,将这些用户以数组作为消息源 new String[][] {
//	 * {组名1，用户名,用户名1、用户名2、用户名3}, {组名2，用户名,用户名1、用户名2、用户名3} }
//	 */
//	// 
//	public static final int USER_GROUP_INFO_CHANGE = 23;
//	// public static final int USER_GROUP_INFO_DELETE = 24;
//	// public static final int USER_GROUP_INFO_ADD = 25;
//	
//	public static final int ORGUNIT_INFO_CHANGE = 24;
//	
//	public static final int ORGUNIT_ROLE_CHANGE = 25;
//
//	public static final int DICTIONARY_INFO_CHANGE = 26;
	
	/**
	 * 用户信息变化事件类型，变化的用户名称作为消息源
	 */

	public static final EventType USER_INFO_CHANGE = new ACLEventTypeImpl("USER_INFO_CHANGE");
			
		
	
	public static final EventType USER_INFO_ADD = new ACLEventTypeImpl("USER_INFO_ADD");
	
	public static final EventType USER_INFO_UPDATE = new ACLEventTypeImpl( "USER_INFO_UPDATE");
	
	public static final EventType USER_INFO_DELETE = new ACLEventTypeImpl("USER_INFO_DELETE");

	// public static final int USER_INFO_DELETE = 1;
	// public static final int USER_INFO_UPDATE = 2;
	// public static final int USER_INFO_ADD = 3;
	/**
	 * 用户组信息变化事件类型 ，组中对应的用户名数组作为消息源 new String[][] { {组名1，用户名,用户名1、用户名2、用户名3},
	 * {组名2，用户名,用户名1、用户名2、用户名3} }
	 */
	public static final EventType GROUP_INFO_CHANGE = new ACLEventTypeImpl("GROUP_INFO_CHANGE");
	// public static final int GROUP_INFO_DELETE = 5;
	// public static final int GROUP_INFO_UPDATE = 6;
	// public static final int GROUP_INFO_ADD = 7;
	/**
	 * 角色信息变化，角色名称作为消息源
	 * 
	 */
	public static final EventType ROLE_INFO_CHANGE = new ACLEventTypeImpl("ROLE_INFO_CHANGE");

	// public static final int ROLE_INFO_UPDATE = 9;
	// public static final int ROLE_INFO_DELETE = 10;

	/**
	 * 资源信息变化，资源名称和资源类型作为消息源，以数组方式封装资源名称和资源类型 消息格式为： new String[] {资源名称、资源类型}
	 */
	//
	public static final EventType RESOURCE_INFO_CHANGE = new ResourceChangeEventTypeImpl("RESOURCE_INFO_CHANGE");
	//
	public static final EventType RESOURCE_ROLE_INFO_CHANGE = new ACLEventTypeImpl("RESOURCE_ROLE_INFO_CHANGE");
	// public static final int RESOURCE_INFO_UPDATE = 12;
	// public static final int RESOURCE_INFO_DELETE = 13;

	/**
	 * 权限许可变化事件类型,角色/资源/操作关系变化 ，将角色名称、资源名称、资源类型、操作封装成数组作为消息源 消息格式为 new String[]
	 * {角色名称,资源名称、资源类型、操作}
	 */
	public static final EventType PERMISSION_CHANGE = new ACLEventTypeImpl("PERMISSION_CHANGE");
	// public static final int PERMISSION_ADD = 15;
	// public static final int PERMISSION_DELETE = 16;

	/**
	 * 用户/角色关系变化，以用户名，角色名称作为消息源，消息格式： new String[] {用户名,角色名}
	 * 
	 */
	//
	public static final EventType USER_ROLE_INFO_CHANGE = new ACLEventTypeImpl("USER_ROLE_INFO_CHANGE");
	// public static final int USER_ROLE_INFO_ADD = 18;
	// public static final int USER_ROLE_INFO_DELETE = 19;

	/**
	 * 用户组/角色关系变化,消息源为组中包含的用户名称数组 消息格式 new String[][] {
	 * {组名1，用户名,用户名1、用户名2、用户名3}, {组名2，用户名,用户名1、用户名2、用户名3} }
	 */
	//	
	public static final EventType GROUP_ROLE_INFO_CHANGE = new ACLEventTypeImpl("GROUP_ROLE_INFO_CHANGE");
	// public static final int GROUP_ROLE_INFO_ADD = 21;
	// public static final int GROUP_ROLE_INFO_DELETE = 22;

	/**
	 * 用户/用户组关系变化，在组中添加/删除用户,将这些用户以数组作为消息源 new String[][] {
	 * {组名1，用户名,用户名1、用户名2、用户名3}, {组名2，用户名,用户名1、用户名2、用户名3} }
	 */
	// 
	public static final EventType USER_GROUP_INFO_CHANGE = new ACLEventTypeImpl("USER_GROUP_INFO_CHANGE");
	// public static final int USER_GROUP_INFO_DELETE = 24;
	// public static final int USER_GROUP_INFO_ADD = 25;
	
	public static final EventType ORGUNIT_INFO_CHANGE = new ACLEventTypeImpl("ORGUNIT_INFO_CHANGE");
	
	public static final EventType ORGUNIT_INFO_DELETE = new ACLEventTypeImpl("ORGUNIT_INFO_DELETE");
	
	public static final EventType ORGUNIT_INFO_ADD = new ACLEventTypeImpl("ORGUNIT_INFO_ADD");
	
	public static final EventType ORGUNIT_INFO_UPDATE = new ACLEventTypeImpl("ORGUNIT_INFO_UPDATE");
	
	public static final EventType ORGUNIT_INFO_SORT = new ACLEventTypeImpl("ORGUNIT_INFO_SORT");
	
	public static final EventType ORGUNIT_INFO_TRAN = new ACLEventTypeImpl("ORGUNIT_INFO_TRAN");
	
	/**
	 * 机构管理员删除
	 */
	public static final EventType ORGUNIT_MANAGER_DELETE = new ACLEventTypeImpl("ORGUNIT_MANAGER_DELETE");
	
	/**
	 * 机构管理添加
	 */
	
	public static final EventType ORGUNIT_MANAGER_ADD = new ACLEventTypeImpl("ORGUNIT_MANAGER_ADD");
	
	public static final EventType ORGUNIT_ROLE_CHANGE = new ACLEventTypeImpl("ORGUNIT_ROLE_CHANGE");
	
	
	

}
