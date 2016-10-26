/*
 *
 * Title:
 *
 * Copyright: Copyright (c) 2004
 *
 * Company: iSany Co., Ltd
 *
 * All right reserved.
 *
 * Created on 2004-10-19
 *
 * JDK version used		:1.4.1
 *
 * Modification history:
 *
 */
package org.frameworkset.platform.security.authorization.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.frameworkset.event.Event;
import org.frameworkset.event.Listener;
import org.frameworkset.event.NotifiableFactory;
import org.frameworkset.event.ResourceChangeEventType;

import org.frameworkset.platform.config.model.AuthorTableInfo;
import org.frameworkset.platform.security.authorization.AuthRole;
import org.frameworkset.platform.security.authorization.AuthUser;
import org.frameworkset.platform.security.authorization.AuthorizationTable;
import org.frameworkset.platform.security.context.AccessContext;
import org.frameworkset.platform.security.event.ACLEventType;

/**
 * To change for your class or interface
 *
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class BaseAuthorizationTable implements AuthorizationTable,
        Listener {
    private static final Logger log = Logger.getLogger(BaseAuthorizationTable.class);
    /**
     * 当用户没有授予任何角色时，将本空角色数组付给用户的缓冲
     */
    private static final AuthRole[] EMPTY_ROLES = new AuthRole[0]; 
    
    /**
     * 空的权限用户数组
     */
    private static final AuthUser[] EMPTY_USERS = new AuthUser[0];

    protected AuthorTableInfo authorTableInfo;
    public static final String guest = "guest___";
    public static final String password = "123456";

    /**缓冲用户与角色的关系*/
    private Map authorizationTable;
    /**缓冲每个用户都拥有的角色*/
    private Map everyOneGrantedRole;

    /**缓冲角色拥有的用户*/
    //2008-12-11  gao.tang 注释  没有使用的缓冲
//    private Map roleUser_cache;
    
    /**
     * 缓冲用户和资源许可的关系
     */
    private Map permission_users;

    protected AccessContext context;

    private boolean inited = false;

    public void setAccessContext(AccessContext context)
    {
        this.context = context;
        init();
    }
    

    public BaseAuthorizationTable() {
    }

    public void init()
    {
        if(inited)
            return;
        //        if (tc.isDebugEnabled()) {
//            Tr.debug(tc, "Regist ACLListener:" + getClass().getName());
//        }
        try {
            //将实例自身作为监听器注册到角色管理事件通知器
//            if (tc.isDebugEnabled()) {
//                Tr.debug(tc,
//                         "Regist ACLListener:" + getClass().getName() + "作为监听器注册到角色管理事件通知器");
//            }

            if(authorTableInfo.isCachable())
            {
                authorizationTable = Collections.synchronizedMap( new HashMap());
                everyOneGrantedRole = Collections.synchronizedMap( new HashMap());
//                roleUser_cache = Collections.synchronizedMap( new HashMap()); //gao.tang 2008-12-11 没有被使用的缓冲
                permission_users = Collections.synchronizedMap( new HashMap());
                List eventType = new ArrayList();
                
             
            	eventType.add(ACLEventType.USER_ROLE_INFO_CHANGE);
//            	eventType.add(ACLEventType.USER_INFO_CHANGE);
            	eventType.add(ACLEventType.USER_INFO_ADD);
            	eventType.add(ACLEventType.USER_INFO_DELETE);
            	eventType.add(ACLEventType.GROUP_ROLE_INFO_CHANGE);
            	eventType.add(ACLEventType.GROUP_INFO_CHANGE);
            	eventType.add(ACLEventType.ROLE_INFO_CHANGE);
            	
            	eventType.add(ACLEventType.USER_GROUP_INFO_CHANGE);
//            	eventType.add(ACLEventType.ORGUNIT_INFO_CHANGE);
            	eventType.add(ACLEventType.ORGUNIT_INFO_ADD);
            	eventType.add(ACLEventType.ORGUNIT_INFO_DELETE);
            	eventType.add(ACLEventType.ORGUNIT_ROLE_CHANGE);      
        		
        		eventType.add(ACLEventType.RESOURCE_ROLE_INFO_CHANGE);
		    	eventType.add(ACLEventType.RESOURCE_INFO_CHANGE);
		    	
		    	/**
		    	 * 内容管理相关事件管理
		    	 */
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_SITE_ADD);
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_SITE_DELETE);
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_SITE_UPDATE);
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_SITESTATUS_UPDATE);
		    	
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_SITE_CHANGE);
		    	
		    	
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_CHANNEL_ADD);
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_CHANNEL_DELETE);
		    	
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_CHANNEL_UPDATE);
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_CHANNEL_MOVE);
		    	
		    	eventType.add(ResourceChangeEventType.EVENT_CHANNEL_CHANGE);
		    
		    	
		    	NotifiableFactory.getNotifiable()
                        .addListener(this,eventType);
    //            if (tc.isDebugEnabled()) {
    //                Tr.debug(tc,
    //                         "Regist ACLListener:" + getClass().getName() + "作为监听器注册到组管理事件通知器");
    //            }
                //将实例自身作为监听器注册到组管理事件通知器
                
//                ConfigManager.getInstance().getNotifiable(context,"security",NotifiableType.
//                                                      GROUP_NOTIFIABLE)
//                        .addListener(this);
    //            if (tc.isDebugEnabled()) {
    //                Tr.debug(tc,
    //                         "Regist ACLListener:" + getClass().getName() + "作为监听器注册到用户管理事件通知器");
    //            }
                //将实例自身作为监听器注册到用户管理事件通知器
                
//                ConfigManager.getInstance().getNotifiable(context,"security",NotifiableType.
//                                                      USER_NOTIFIABLE)
//                        .addListener(this);
//                ConfigManager.getInstance().getNotifiable(context,"security",NotifiableType.ORGUNIT_NOTIFIABLE)
//                        .addListener(this);
            }
            this.inited = true;
        } catch (Exception e) {
            log.error("Register listener error:" + e.getMessage(),e);
        }


    }




    /**
     * 判断用户是否被授予角色roleName
     * @since 2004.12.15
     * @param arg0
     * @param arg1
     * @param arg2
     * @return boolean
     * @throws com.ibm.websphere.security.SecurityProviderException
     */
    public boolean isGrantedRole(AuthRole role,
                                 Principal callerPrincipal) throws
            SecurityException {
        return isGrantedAnyRole(new AuthRole[]{role},callerPrincipal);
    }

    /**
     * 为数组中包含的每个角色添加一个用户
     * Description:
     * @param roles
     * @param userName
     * void
     */
    protected void cacheUsersofRole(AuthRole[] roles, String userName) {
    	//gao.tang 2008-12-11 没有被使用的缓冲
//        for (int i = 0; i < roles.length; i++) {
//            Set users = (Set) roleUser_cache.get(roles[i]);
//            if (users == null) {
//                users = new TreeSet();
//                roleUser_cache.put(roles[i],users);
//            }
//            users.add(userName);
//        }
    }

    /**
     * 为数组中包含的每个角色添加一个用户
     * Description:
     * @param roles
     * @param userName
     * void
     */
    protected void removeUserFromRole(String userName) {
    	//gao.tang 2008-12-11 没有被使用的缓冲
//        roleUser_cache.clear();
//        String roles[] = (String[])this.authorizationTable.get(userName);
//        for (int i = 0; roles != null && i < roles.length; i++) {
//            roleUser_cache.remove(roles[i]);
//        }

    }

    /**
     * 为数组中包含的每个角色添加一个用户
     * Description:
     * @param roles
     * @param userName
     * void
     */
    protected void removeUserAuth(String roleName) {
//        Set users = (Set) roleUser_cache.get(roleName);
//        if (users == null) {
//            return;
//        }
//
//        Iterator iterator = users.iterator();
//
////        String key = appName + ":" + moduleName + ":";
//        synchronized (authorizationTable) {
//            while (iterator.hasNext()) {
////                authorizationTable.remove(key + (String) iterator.next());
//                  authorizationTable.remove(iterator.next());
//            }
//        }
        authorizationTable.clear();
    }


    /**
     * 判定是否所有的授予资源访问的角色是否包含每个人都授予的角色
     * @since 2004.12.15
     * @param arg0
     * @param arg1
     * @return boolean
     * @throws com.ibm.websphere.security.SecurityProviderException
     */
    public boolean isEveryoneGranted(AuthRole[] requireRoles) throws
            SecurityException {
        //如果系统安全模型支持该中模式直接调用这种模式


        boolean everyOneGranted = false;
        String temp = null;

        for (int i = 0; i < requireRoles.length; i++) {
            //cachKey = application + ":"
            if(this.authorTableInfo.isCachable())
            {
            	AuthRole cachKey = requireRoles[i];
	            temp = (String) everyOneGrantedRole.get(cachKey);
	            if (temp != null) {
	                if(temp.equals("true"))
	                {
	                    everyOneGranted = true;
	                    break;
	                }
	            } else {
	                everyOneGranted = isEveryoneGranted(requireRoles[i]);
	                everyOneGrantedRole.put(cachKey, everyOneGranted + "");
	                if (everyOneGranted) {
	                    break;
	                }
	            }
            }
            else
            {
                everyOneGranted = isEveryoneGranted(requireRoles[i]);
                if (everyOneGranted) {
                    break;
                }
            }
        }
        return everyOneGranted;
    }

    /**
     * @since 2004.12.15
     * @param arg0
     * @param arg1
     * @param arg2
     * @return boolean
     * @throws com.ibm.websphere.security.SecurityProviderException
     */
    public boolean isGrantedAnyRole(
    		AuthRole[] requireRoles,
            Principal callerPrincipal) throws SecurityException {


        if(callerPrincipal == null)
        {
            throw new SecurityException("Get all roles of callerPrincipal error:callerPrincipal is null." );
        }
        String userName = callerPrincipal.getName();

        AuthRole roles[] = null;
        //获取相应结点的服务器企业应用中用户所属的应用
        //自定义角色（不包括部署描述符中定义平台角色）
        if(this.authorTableInfo.isCachable())
        {
	        String cachKey = userName;
	        if ((roles = (AuthRole[]) authorizationTable.get(cachKey)) == null) {
	        	//update 20080721 gao.tang 如果抛出SecurityException异常返回false
	        	try
	        	{
	        		if(!userName.equals(guest))
	        		{
	        			roles = this.getAllRoleOfPrincipal(userName);
	        		}
	        		else
	        		{	 
	        			AuthRole authrole = new AuthRole();
						authrole.setRoleName("guest");
						authrole.setRoleId("99");
						authrole.setRoleType(AuthRole.TYPE_ROLE);
						roles = new AuthRole[]{authrole};						
	        		}
	        	}
	        	catch(Exception se)
	        	{
	        		log.debug("Get all roles of "
	                        + userName + " error: " + se.getMessage());
	        		return false;
	        		
	        	}
	            if (roles == null || roles.length <= 0) {
	                roles = EMPTY_ROLES;
	                log.debug("Get all roles of "
	                        + userName + ": no role assign to this user!");
//	                
//	                throw new SecurityException("Get all roles of "
//	                        + userName + " error: no role assign to this user!");
	            }
	            else
	            {
	                StringBuffer log_str = new StringBuffer("Get all roles of " + userName + ":" );
	                boolean flag = false;
	                for(int i = 0; i < roles.length; i ++)
	                {
	                    if(!flag)
	                    {
	                        log_str.append(roles[i]);
	                        flag = true;	   
	                    }
	                    else
	                    {
	                        log_str.append(",")
	                        	   .append(roles[i]);
	                    }
	                        
	                }
	                log.debug(log_str.toString());
	            }
	            authorizationTable.put(cachKey, roles);
	        }
        }
        else
        {
        	//update 20080721 gao.tang 如果抛出SecurityException异常返回false
        	try
        	{
        		
        		
        		if(!userName.equals(guest))
        		{
        			roles = this.getAllRoleOfPrincipal(userName);
        		}
        		else
        		{	 
        			AuthRole authrole = new AuthRole();
					authrole.setRoleName("guest");
					authrole.setRoleId("99");
					authrole.setRoleType(AuthRole.TYPE_ROLE);
					roles = new AuthRole[]{authrole};						
        		}
        	}
        	catch(Exception se)
        	{
        		log.debug("Get all roles of "
                        + userName + " error: " + se.getMessage());
        		return false;
        	}
            if (roles == null) {
                roles = EMPTY_ROLES;
                log.debug("Get all roles of "
                        + userName + ": no role assign to this user!");
//                throw new SecurityException("Get all roles of "
//                        + userName + " error: no role assign to this user!");
            }
            else
            {
                StringBuffer log_str = new StringBuffer("Get all roles of " + userName + ":" );
                boolean flag = false;
                for(int i = 0; i < roles.length; i ++)
                {
                    if(!flag)
                    {
                        log_str.append(roles[i]);
                        flag = true;	   
                    }
                    else
                    {
                        log_str.append(",")
                        	   .append(roles[i]);
                    }
                        
                }
                log.debug(log_str.toString());
            }
        }
        
      
        boolean grantedAnyRole = false;
        for (int i = 0; i < roles.length; i++) {
            if ((grantedAnyRole = containRole(roles[i], requireRoles))) {
                return grantedAnyRole;
            }
        }
        return grantedAnyRole;
    }

    private boolean containRole(AuthRole role, AuthRole[] roles) {
        for (int i = 0; i < roles.length; i++) {
            if (role.equals(roles[i])) {
                return true;
            }
        }
        return false;
    }


    /**
     * Description:访问安全底层service判断
     * @param getAppName()
     * @param cell
     * @param node
     * @param roles
     * @return
     * @throws SecurityProviderException
     * boolean
     */
    public boolean isEveryoneGranted(AuthRole roleName) throws
            SecurityException
    {
        if(roleName == null)
            throw new SecurityException("Check Everyone Granted Role error: [RoleName=" + roleName + "]");
        return roleName.equals(authorTableInfo.getEveryonegrantedrole());
    }

    /**
     *
     * Description:
     * @param appName
     * @param server
     * @param cell
     * @param userName
     * @return
     * String[]
     */


    public abstract AuthRole[] getAllRoleOfPrincipal(String userName) throws SecurityException;


    /**
     *  Description:
     * @param e
     * @see com.frameworkset.platform.security.event.ACLListener#handle(com.frameworkset.platform.security.event.Event)
     */
    public void handle(Event e) {
//        String cellName = (String) SecurityConfig.getConfig().getValue(
//                "security.activeUserRegistry.realm");
//        String getAppName() = (String) SecurityConfig.getConfig().getValue(
//                "process.getAppName()");
        //mark$$

        //当用户角色关系发生变化时，清楚用户所缓冲的权限资源
        //当用户信息发生改变时清楚原有用户所缓冲的权限资源
        if (e.getType() .equals( ACLEventType.USER_ROLE_INFO_CHANGE)
            || e.getType() .equals( ACLEventType.USER_INFO_ADD)
            || e.getType() .equals( ACLEventType.USER_INFO_DELETE)
            || e.getType() .equals( ACLEventType.GROUP_ROLE_INFO_CHANGE)
            || e.getType() .equals( ACLEventType.GROUP_INFO_CHANGE)
            || e.getType() .equals( ACLEventType.ROLE_INFO_CHANGE)
            || e.getType() .equals( ACLEventType.USER_GROUP_INFO_CHANGE)
            || e.getType() .equals( ACLEventType.ORGUNIT_INFO_ADD)
            || e.getType() .equals( ACLEventType.ORGUNIT_INFO_DELETE)
            || e.getType() .equals( ACLEventType.ORGUNIT_ROLE_CHANGE)
            
            ) {
//            String userName = (String) e.getSource();
//            //定义用户角色关系缓冲key
//            String authorizeCachKey = userName;
//            removeUserFromRole(userName);
//            authorizationTable.remove(authorizeCachKey);
            removeUserFromRole("");
            authorizationTable.clear();
            permission_users.clear();
        }
        if( e.getType() .equals( ACLEventType.RESOURCE_ROLE_INFO_CHANGE)
                || e.getType() .equals( ACLEventType.RESOURCE_INFO_CHANGE )|| (e.getType() instanceof ResourceChangeEventType))
        {
        	permission_users.clear();
        }

        //当角色名称发生变化时，清除为原有角色所缓冲的权限资源
        if (e.getType() .equals( ACLEventType.ROLE_INFO_CHANGE)) {
        	String roleName = (String) e.getSource();

            //定义角色与每个用户是否有关系的缓冲key
            //String roleCachKey = roleName;
            //清除每个用户的都授予的角色缓冲
            //everyOneGrantedRole.remove(roleCachKey);

            //清除所有根角色roleName相关的用户权限列表
            //removeUserAuth(roleName);
            everyOneGrantedRole.clear();
        }

        //组和角色的关系发生变化时，删除组所属的用户的权限缓冲资源
        //当组被删除时，清除原来所缓冲组相关用户的权限资源
        if (e.getType() .equals( ACLEventType.GROUP_ROLE_INFO_CHANGE )||
            e.getType() .equals( ACLEventType.GROUP_INFO_CHANGE)) {

//            String groupUsers = (String) e.getSource();
//            StringTokenizer token = new StringTokenizer(groupUsers, "||", false);
//
//
//            while (token.hasMoreTokens()) {
//                String userName = token.nextToken();
//                //定义用户角色关系缓冲key
//                String authorizeCachKey = userName;
//                removeUserFromRole(userName);
//                authorizationTable.remove(authorizeCachKey);
//            }
            

        }

        //当用户被加入到某个组或者从某个组删除时，清除权限表中缓冲的改用户与角色的对应关系
        if (e.getType() .equals( ACLEventType.USER_GROUP_INFO_CHANGE)) {
//            String changeUser = (String) e.getSource();
//
//            String authorizeCachKey = changeUser;
//            removeUserFromRole(changeUser);
//
//            authorizationTable.remove(authorizeCachKey);

        }
    }
    
    /**
     * 重新加载
     */
    public void reset(){
		 authorizationTable.clear();
         everyOneGrantedRole.clear();
       //gao.tang 2008-12-11 没有被使用的缓冲
//         roleUser_cache.clear();
         permission_users.clear();
    }
    public void destroy()
    {
    	 authorizationTable.clear();
         everyOneGrantedRole.clear();
       //gao.tang 2008-12-11 没有被使用的缓冲
//         roleUser_cache.clear();
         permission_users.clear();
         authorizationTable = null;
         everyOneGrantedRole = null;
       //gao.tang 2008-12-11 没有被使用的缓冲
//         roleUser_cache.clear();
         permission_users = null;
    }

    public AuthorTableInfo getAuthorTableInfo() {
        return authorTableInfo;
    }

    public void setAuthorTableInfo(AuthorTableInfo authorTableInfo) {
        this.authorTableInfo = authorTableInfo;
    }

    protected String getProviderType()
    {
        return this.authorTableInfo.getProviderType();
    }

    /**
     * 判断用户是否为系统管理员
     * @param principal Principal
     * @return boolean
     */
    public boolean isAdmin(Principal principal)
     {
         try {
            return isGrantedRole(authorTableInfo.getAdminRole(),
                                     principal);
        } catch (SecurityException e) {
           log.debug(e.getMessage());
           return false;
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            //System.out.println(e);
            return false;
        }

     }
    
    /**
     * 获取资源操作许可的用户列表的抽象方法，由具体的应用去实现
     * @param resourceid
     * @param operation
     * @param resourceType
     * @return
     */
    public abstract AuthUser[] getAllPermissionUsersOfResource(String resourceid,String operation,String resourceType) throws SecurityException;
    
    /**
     * 获取资源操作许可的用户列表的抽象方法，
     * 并且根据系统配置对获取的结果进行缓冲
     * @param resourceid
     * @param operation
     * @param resourceType
     * @return
     * @throws SecurityException
     */
    public AuthUser[] getAllPermissionPrincipalsOfResource(String resourceid,String operation,String resourceType) throws SecurityException
    {
    
    	
    	String cacheKey = resourceid.concat(":").concat(operation).concat(":").concat(resourceType);
    	AuthUser[] users = null;
    	if(this.authorTableInfo.isCachable())
    	{
    		users = (AuthUser[])this.permission_users.get(cacheKey);
	    	if(users == null)
	    	{
	    		try
	    		{
		    		users = this.getAllPermissionUsersOfResource(resourceid,operation,resourceType);
		    		if(users == null || users.length == 0)
		    		{
		    			users = EMPTY_USERS;
		    		}
		    		this.permission_users.put(cacheKey,users);
	    		}
	    		catch(Exception e)
	    		{
	    			users = EMPTY_USERS;
	    		}
	    		
	    	}
    	}
    	else
    	{	
    		
    		try
    		{
	    		users = this.getAllPermissionUsersOfResource(resourceid,operation,resourceType);
	    		if(users == null || users.length == 0)
	    		{
	    			users = EMPTY_USERS;
	    		}
    		}
    		catch(Exception e)
    		{
    			users = EMPTY_USERS;
    		}
    	}
    		
    	
    	return users;
    	
    }
    
    	
    
    
}
