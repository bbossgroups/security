package org.frameworkset.platform.security.authorization.impl;

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

import org.frameworkset.platform.config.model.PermissionRoleMapInfo;
import org.frameworkset.platform.security.authorization.AuthRole;
import org.frameworkset.platform.security.context.AccessContext;
import org.frameworkset.platform.security.event.ACLEventType;


/**
 *
 * 定义角色资源映射
 *
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class PermissionRoleMap implements Listener {
    private static Logger log = Logger.getLogger(PermissionRoleMap.class);
    public static AuthRole NO_REQUIRED_ROLES[] = new AuthRole[0];
    public static AuthRole EMPTY_REQUIRED_ROLES[] = new AuthRole[0];
    /**资源许可与角色关系缓冲器*/
    Map prMap;
    
    /**
     * 保存资源是否受过权
     */
    Map resourceMap;
    /**角色资源许可关系缓冲器*/
//    Map role_resourceMap;
    
    
    /**
     * 保存资源是否受过权
     */
    Map resourceRoleMaps ;

    protected AccessContext context;

    private boolean inited = false;

    protected PermissionRoleMapInfo permissionRoleMapInfo ;

    public void setAccessContext(AccessContext context)
    {
        this.context = context;
    }

    /**
     * @since 2004.12.15
     */
    public PermissionRoleMap() {


    }

    public void init()
    {
        if(inited)
            return;
        try {
            if(this.permissionRoleMapInfo.isCachable())
            {
            	resourceMap = Collections.synchronizedMap(new HashMap());
                prMap = Collections.synchronizedMap(new HashMap());
//                role_resourceMap = Collections.synchronizedMap(new HashMap());
                resourceRoleMaps  = Collections.synchronizedMap(new HashMap());
                
                List eventType = new ArrayList();              
            	eventType.add(ACLEventType.PERMISSION_CHANGE);
            	eventType.add(ACLEventType.RESOURCE_ROLE_INFO_CHANGE );
            	eventType.add(ACLEventType.RESOURCE_INFO_CHANGE);
            	eventType.add(ACLEventType.ROLE_INFO_CHANGE);
            	
            	
                //将实例自身作为监听器注册到角色管理事件通知器
            	NotifiableFactory.getNotifiable().addListener(this,eventType);
    //            ConfigManager.getInstance().getNotifiable("security",NotifiableType.
    //                                                  ROLE_NOTIFIABLE)
    //                .addListener(this);
    //            //将实例自身作为监听器注册到组管理事件通知器
    //            ConfigManager.getInstance().getNotifiable("security",NotifiableType.
    //                                                  PERMISSION_NOTIFIABLE)
    //                .addListener(this);

                //将实例自身作为监听器注册到用户管理事件通知器
//                ConfigManager.getInstance().getNotifiable(context,"security",NotifiableType.
//                                                      RESOURCE_NOTIFIABLE)
//                    .addListener(this);
            }
            this.inited = true;
        } catch (Exception e) {
            log.error(e);
        }

    }

    /**
     * 获取资源访问角色，并且对期进行缓冲
     * Description:
     * @param accesscontext
     * @param accesspermission
     * @return
     * Role[]
     * @throws SecurityException 
     * add by 20080721 gao.tang 方法抛出SecurityException异常
     */

    public AuthRole[] getRequiredRoles(
        AccessContext accesscontext,
        AccessPermission accesspermission) throws SecurityException {
    	AuthRole asecurityroles[] = null;
        if(this.permissionRoleMapInfo.isCachable())
        {
            String cachKey = accesspermission.getResourceType() 
            				 + ":" + accesspermission.getResource() 
            				 + ":" + accesspermission.getAction();
            //定义缓冲资源所有操作都没有授权的信息            
            
            asecurityroles = (AuthRole[]) prMap.get(cachKey);
            
	        if (asecurityroles == null) {
	        	//update 20080721 gao.tang 增加SecurityException异常处理
	        	try
	        	{
		        	AuthRole newasecurityroles[] = null;
		        	try
		        	{
		        		newasecurityroles = getRequiredRoles(accesspermission.getResource(),
		                accesspermission.getAction(),
		                accesspermission.getResourceType());
		        	}
		        	catch(SecurityException e)
		        	{
		        		return NO_REQUIRED_ROLES;
		        	}
		            StringBuffer log_str = new StringBuffer("Get roles of ").append(accesspermission).append(":");
		            //如果资源的角色为null，则表示获取资源角色出现异常，不需要缓冲accesspermission的角色
		            if (newasecurityroles == null) {
		            	if(hasGrantedAnyRole(accesspermission.getResource(),accesspermission.getResourceType()))
		            	{
			                asecurityroles = EMPTY_REQUIRED_ROLES;
		            	}
		            	else
		            	{
		            		asecurityroles = NO_REQUIRED_ROLES;
		            		log_str.append("no role assigned.NO_REQUIRED_ROLES will be used.");
		            	}
		                
		            } else {
		                //prMap.put(accesspermission, newasecurityroles);
		                asecurityroles = newasecurityroles;
		                boolean flag = false;
		                for(int i = 0; i < asecurityroles.length; i ++)
		                {
		                    if(!flag)
		                    {
		                        log_str.append(asecurityroles[i]);
		                        flag = true;
		                    }
		                    else
		                    {
		                        log_str.append(",").append(asecurityroles[i]);
		                        
		                    }
		                }	                
		                log.debug(log_str.toString());
		            }
	        	}
	        	catch(Exception e)
	        	{
	        		
	        	}
	            
	            prMap.put(cachKey, asecurityroles);            
	            //缓冲角色与资源的关系
	            if (asecurityroles != NO_REQUIRED_ROLES &&  asecurityroles != EMPTY_REQUIRED_ROLES) {
	                cachResourceToRole(asecurityroles, accesspermission);
	            }
	        }
        }
        else
        {
        	//update 20080721 gao.tang 增加SecurityException异常处理
        	try
        	{
	            asecurityroles = getRequiredRoles(accesspermission.getResource(),
		                accesspermission.getAction(),
		                accesspermission.getResourceType());
	            if (asecurityroles == null) {
	                asecurityroles = NO_REQUIRED_ROLES;
	            }
        	}
        	catch(SecurityException e)
        	{
        		return NO_REQUIRED_ROLES;
        	}
        }

        return asecurityroles;
    }

    /**
     * 添加角色与资源的关系
     * 之所以需要缓冲角色和许可的关系，当角色发生变化时
     * Description:
     * @param accesspermission
     * @param roles
     * void
     */
    protected void cachResourceToRole(AuthRole[] roles,
                                      AccessPermission accesspermission) {
//    	AuthRole roleName = null;
//        Set set = null;
//        for (int i = 0; i < roles.length; i++) {
//            roleName = roles[i];
//            set = (Set) role_resourceMap.get(roleName);
//            if (set != null) {
//                set.add(accesspermission);
//            } else {
//                set = new TreeSet();
//                set.add(accesspermission);
//            }
//        }

    }

    /**
     * 清楚角色与资源的关系
     * Description:
     * @param accesspermission
     * void
     */
    protected void removeResourceToRole(AccessPermission accesspermission) {
//        String asecurityroles[] = (String[]) prMap.get(accesspermission);
//        for (int i = 0; asecurityroles != null && i < asecurityroles.length;
//                     i++) {
//            role_resourceMap.remove(asecurityroles[i]);
//        }
        
//        role_resourceMap.clear();

    }


    /**
     * 清楚角色与资源的关系
     * Description:
     * @param accesspermission
     * void
     */
    protected void removeRoleToResource(String roleName) {
//        Set permissions = (Set)this.role_resourceMap.get(roleName);
//        if (permissions != null) {
//            Iterator temp = permissions.iterator();
//            while (temp.hasNext()) {
//                prMap.remove(temp.next());
//            }
//
//        }
        prMap.clear();
        this.resourceMap.clear();
    }

    /**
     * 抽象方法，从资源角色表中获取资源所属角色，由具体的应用来实现
     * @param accessContext
     * @param resource
     * @param permission
     * @return com.ibm.etools.j2ee.common.Role[]
     * @throws SecurityException 
     * add by 20080721 gao.tang  
     * 抽象方法抛出SecurityException异常
     */
    protected abstract AuthRole[] getRequiredRoles(
        String resource,
        String permission,
        String resourceType) throws SecurityException;
    
//    /**
//     * 判断资源是否授予特定的角色，只要有角色拥有资源的一个操作权限，就返回true
//     * 否则返回false
//     * @param resource
//     * @param resourceType
//     * @return
//     */
//    protected abstract boolean hasGrantedRoles(
//            String resource,            
//            String resourceType);
//    
    

    /**
     *  Description:
     * @param e
     * @see com.frameworkset.platform.security.authorization.Listener#handle(com.frameworkset.platform.security.authorization.ACLEventType)
     */
    public void handle(Event e) {

        //许可改变以后，必需要清除原有资源得资源角色关系缓冲
        if (e.getType() .equals( ACLEventType.PERMISSION_CHANGE)
            || e.getType() .equals( ACLEventType.RESOURCE_ROLE_INFO_CHANGE) 
            || e.getType() .equals( ACLEventType.RESOURCE_INFO_CHANGE) || (e.getType() instanceof ResourceChangeEventType)) {
//            String permission = (String) e.getSource();            
//            StringTokenizer token = new StringTokenizer(permission, "||", false);
////			if(tc.isDebugEnabled())
////				Tr.debug(tc,"handle permission change" );
//            AccessPermission appPermission = new AccessPermission(token.
//                nextToken(),
//                token.nextToken(),token.nextToken());
////			if(tc.isDebugEnabled())
////				Tr.debug(tc,"remove appPermission:" + appPermission);
//            removeResourceToRole(appPermission);
//            synchronized (prMap) {
//                prMap.remove(appPermission);
//            }
            removeResourceToRole(null);
//            removeRoleToResource("");
            prMap.clear();
            this.resourceMap.clear();
            resourceRoleMaps.clear();
        }

        //资源名称改变后必须及时清除缓冲中原有资源角色之间的关系
        if (e.getType() .equals( ACLEventType.RESOURCE_INFO_CHANGE)) {
//            String permissions = (String) e.getSource();
//
////			if(tc.isDebugEnabled())
////				Tr.debug(tc,"handle resource change" );
//
//            AccessPermission appPermission = null;
//            StringTokenizer token = new StringTokenizer(permissions, "||", false);
//            String resourceName = token.nextToken();
//            //清除缓冲中所有根资源有关的操作许可
//
//            while (token.hasMoreTokens()) {
//                appPermission = new AccessPermission(resourceName,
//                    token.nextToken(),token.nextToken());
//                removeResourceToRole(appPermission);
//                synchronized (prMap) {
//                    prMap.remove(appPermission);
//                }
//            }
        }

        //角色信息改变后必须及时清除缓冲中原有角色与资源之间的关系
        //角色的所有权限改变后必须及时清除缓冲中原有资源角色之间的关系
        if (e.getType() .equals( ACLEventType.ROLE_INFO_CHANGE)
            || e.getType() .equals( ACLEventType.PERMISSION_CHANGE)) {
//            String roleName = (String) e.getSource();
//            removeRoleToResource(roleName);
//            role_resourceMap.remove(roleName);
            removeRoleToResource("");
//            role_resourceMap.clear();
            resourceRoleMaps.clear();
        }
    }

    public PermissionRoleMapInfo getPermissionRoleMapInfo() {
        return permissionRoleMapInfo;
    }

    public void setPermissionRoleMapInfo(PermissionRoleMapInfo
                                         permissionRoleMapInfo) {
        this.permissionRoleMapInfo = permissionRoleMapInfo;
    }
    
    /**
     * 判断特定类型的资源是否授过权
     * true:标识已授过权限
     * false:标识没有受过权限
     * @param resourceType
     * @param resource
     * @return
     */
    public boolean hasGrantedAnyRole(String resource,String resourceType)
    {
    	String rescachKey = resourceType
		 + ":" + resource ;
    	Boolean hasGranted = (Boolean)this.resourceMap.get(rescachKey);
    	if(this.permissionRoleMapInfo.isCachable())
    	{
	    	if(hasGranted == null )
	    	{
	    		//update 20080721 gao.tang 如果抛出SecurityException异常返回false
	    		try
	    		{
		    		boolean granted = hasGrantedRoles( resource,resourceType);
		    		hasGranted = new Boolean(granted);
		    		resourceMap.put(rescachKey,hasGranted);
		    		return granted;
	    		}
	    		catch(SecurityException e)
	    		{
	    			return false;
	    		}
	    	}
	    	else
	    		return hasGranted.booleanValue();
    	}
    	else
    	{
    		//update 20080721 gao.tang 如果抛出SecurityException异常返回false
    		try
    		{
    			return  hasGrantedRoles( resource,resourceType);
    		}
    		catch(SecurityException e)
    		{
    			return false;
    		}
    	}
    		
    }
    
    /**
     * 
     * @param resource
     * @param resourceType
     * @return
     * @throws SecurityException
     * add by 20080721 gao.tang 抽象方法抛出SecurityException异常
     */
    public abstract boolean hasGrantedRoles(String resource,String resourceType) throws SecurityException;
    

    protected String getProviderType()
    {
        return this.permissionRoleMapInfo.getProviderType();
    }
    
    public boolean grantRole(AuthRole role, String resource, String resourceType)
    {
    	 if(this.permissionRoleMapInfo.isCachable())
         {
    		//update 20080721 gao.tang 如果抛出SecurityException异常返回false
    		 try{
	             String cachKey = resourceType 
	             				 + ":" + resource 
	             				 ;
	             
	             Boolean has = (Boolean)this.resourceRoleMaps.get(cachKey);
	             if(has == null)
	             {
	            	 boolean has_b = hasGrantRole( role,  resource,  resourceType);
	            	 resourceRoleMaps.put(cachKey,new Boolean(has_b));
	            	 return has_b;
	             }
	             else
	            	 return has.booleanValue();
    		 }catch(SecurityException e){
    			 return false;
    		 }
             
            
 	           
 	       
         }
         else
         {
        	//update 20080721 gao.tang 如果抛出SecurityException异常返回false
        	 try{
	        	 boolean has_b = hasGrantRole( role,  resource,  resourceType);
	        	 return has_b;
        	 }catch(SecurityException e){
        		 return false;
        	 }
         }
    }
    
    /**
     * 
     * @param role
     * @param resource
     * @param resourceType
     * @return
     * @throws SecurityException
     * add by 20080721 抽象方法抛出SecurityException异常
     */
    public abstract boolean hasGrantRole(AuthRole role, String resource, String resourceType) throws SecurityException;
    
    /**
     * 重新加载
     */
    public void reset(){
    	resourceMap.clear();
        prMap.clear();
//        role_resourceMap.clear();
        resourceRoleMaps.clear();
    }
    
    public void destroy()
    {
    	resourceMap.clear();
        prMap.clear();
//        role_resourceMap.clear();
        resourceRoleMaps.clear();
        resourceMap = null;
        prMap = null;
//        role_resourceMap.clear();
        resourceRoleMaps = null;
        this.inited = false;
        this.context = null;
    }
    public static void main()
    {
    	
    }
}
