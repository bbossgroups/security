package org.frameworkset.platform.resource;

import java.util.List;

import org.apache.log4j.Logger;

import org.frameworkset.platform.config.ConfigException;
import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.config.ResourceInfoQueue;
import org.frameworkset.platform.config.model.Operation;
import org.frameworkset.platform.config.model.OperationGroup;
import org.frameworkset.platform.config.model.OperationQueue;
import org.frameworkset.platform.config.model.ResourceInfo;
import org.frameworkset.platform.config.model.Resources;

/**
 * <p>Title: ResourceManager</p>
 *
 * <p>Description: 提供资源管理的所有方法</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class ResourceManager {
    private static Logger log = Logger.getLogger(ResourceManager.class);
    public ResourceInfo getResourceInfoByTypeOfModule(String module,
            String type) {
	    	ResourceInfo resInfo = null;
			try
			{
				resInfo = ConfigManager.getInstance().getResourceInfoByTypeOfModule(module,
											                    type);
			}
			catch (ConfigException e)
			{
				log.error(e);
				e.printStackTrace();
			}
	    	
            return resInfo;
        }

        public ResourceInfo getResourceInfo(String appName,
                                             String moduleName, String type) {
        	ResourceInfo resInfo = null;
			try
			{
				resInfo = ConfigManager.getInstance().getResourceInfo(appName,
	                    moduleName, type);
			}
			catch (ConfigException e)
			{
				log.error(e);
				e.printStackTrace();
			}
	    	
            return resInfo;
           
        }

        public ResourceInfo getResourceInfo(String module) {
            return ConfigManager.getInstance().getResourceInfo(module) ;
        }

        public ResourceInfo getResourceInfoByType(String resourcetype) {
            try
			{
				return ConfigManager.getInstance().getResourceInfoByType(resourcetype);
			}
			catch (ConfigException e)
			{
				log.error(e);
			}
			return null;
        }


        /**
         * 获取特定应用特定模块缺省的资源信息
         * @param appName String
         * @param moduleName String
         * @return ResourceInfo
         */
        public ResourceInfo getResourceInfo(String appName, String moduleName) {
            return ConfigManager.getInstance().getResourceInfo(appName,moduleName);
        }


        public ResourceInfo getResourceInfo() {
            return ConfigManager.getInstance().getResourceInfo();
        }

        /**
         * 获取系统缺省应用模块的资源信息队列
         * @param module String
         * @return ResourceInfoQueue
         */
        public ResourceInfoQueue getResourceInfoQueue(String module) {
            return ConfigManager.getInstance().getResourceInfoQueue(module);
        }
        
        public ResourceInfoQueue getResourceInfoQueue() {
            return this.getResources().getResourceQueue();
        
        }
        
        public List getResourceInfos() {
            return this.getResources().getResourceQueue().getList();
        }


        public OperationGroup getOperationGroup(String appName, String moduleName) {
            return ConfigManager.getInstance().getOperationGroup(appName,moduleName);
        }

        /**
         * 获取缺省类型资源对应的操作组
         * @return OperationGroup
         */
        public OperationGroup getOperationGroup() {
            return ConfigManager.getInstance().getOperationGroup();
        }

        public OperationGroup getOperationGroup(String resourceType) {
        	try
        	{
        		return ConfigManager.getInstance().getOperationGroup(resourceType);
        	}
        	catch(Exception e)
        	{
        		return null;
        	}
        }

        /**
         * 获取缺省的资源操作组
         * @return OperationGroup
         */
        public OperationGroup getDefaultOperationGroup() {
            return ConfigManager.getInstance().getDefaultOperationGroup();
        }

        /**
         * 获取特定资源类型的操作队列
         * @param resourceType String
         * @return OperationQueue
         */
        public OperationQueue getOperationQueue(String resourceType) {
            try
			{
				return ConfigManager.getInstance().getOperationQueue(resourceType);
			}
			catch (ConfigException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
        }
        
        /**
         * 获取特定资源类型操作组中，比给定操作的优先级低的操作队列
         * @param resourceType String
         * @param opid String
         * @return OperationQueue
         */
        public List getLowerOperations(String resourceType,String opid) {
            
				return this.getOperation(resourceType,opid).getLowerOperations().getList();
			
        }
        
        /**
         * 获取特定资源类型操作组中，与给定操作的互斥的操作队列
         * @param resourceType String
         * @param opid String
         * @return OperationQueue
         */
        public List getHuchiOperations(String resourceType,String opid) {
           
            	
				return this.getOperation(resourceType,opid).getHuchiOperations().getList();
			
        }
        
        /**
         * 获取特定资源类型的操作队列
         * @param resourceType String
         * @return OperationQueue
         */
        public List getOperations(String resourceType) {
            try
			{
				return ConfigManager.getInstance().getOperationQueue(resourceType).getList();
			}
			catch (ConfigException e)
			{
				// TODO Auto-generated catch block
				log.error("getOperations of resourceType =" + resourceType,e);
			}
			return null;
        }


        /**
         * 获取特定类型资源的操作项信息
         * @param resourceType String
         * @param operid String
         * @return Operation
         */
        public Operation getOperation(String resourceType, String operid) {
        	
            try
			{
				return ConfigManager.getInstance().getOperation(resourceType,operid);
			}
			catch (ConfigException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
        }


        /**
         * 获取特定应用模块的资源信息队列
         * @param appName String
         * @param moduleName String
         * @return ResourceInfoQueue
         */
        public ResourceInfoQueue getResourceInfoQueue(String appName,
                                                      String moduleName) {
            return ConfigManager.getInstance().getApplicationInfo(appName).getResourceInfoQueue(moduleName);
        }
        
        public Resources getResources()
        {
            return ConfigManager.getInstance().getResources();
        }
        
        /**
         * 获取特定类型资源的全局操作项信息
         * @param resourceType String
         * @param operid String
         * @return Operation
         */
        public Operation getGlobalOperation(String resourceType, String operid) throws ConfigException{
            return getResourceInfoByType(resourceType).getGlobalOperationByid(operid);
        }
        /**
         * 获取特定资源类型的全局操作队列
         * @param resourceType String
         * @return OperationQueue
         */
        public OperationQueue getGlobalOperationQueue(String resourceType) throws ConfigException{
        	ResourceInfo info = getResourceInfoByType(resourceType);
        	if(info == null)
        		return null;
            return info.getGlobalOperationQueue();
        }
        
        /**
         * 获取特定资源类型的全局操作队列
         * @param resourceType String
         * @return OperationQueue
         */
        public List getGlobalOperations(String resourceType) throws ConfigException{
        	ResourceInfo info = getResourceInfoByType(resourceType);
        	if(info == null)
        		return null;
        	OperationQueue temp = info.getGlobalOperationQueue();
        	if(temp != null && temp.size() > 0)
        		return temp.getList();
            return null;
        }
        /**
         * 获取所有用户都能授予的全局资源操作项列表
         * @param resourceType
         * @return
         * @throws ConfigException
         */
        public List getCommonGlobalOperations(String resourceType) throws ConfigException{
        	OperationQueue temp = getResourceInfoByType(resourceType).getCommonGlobalOperations();
        	if(temp != null && temp.size() > 0)
        		return temp.getList();
            return null;
        }
        
        /**
         * 获取只有管理员用户才能授予的全局资源操作项列表
         * @param resourceType
         * @return
         * @throws ConfigException
         */
        public List getManagerGlobalOperations(String resourceType) throws ConfigException{
        	OperationQueue temp = getResourceInfoByType(resourceType).getManagerGlobalOperations();
        	if(temp != null && temp.size() > 0)
        		return temp.getList();
            return null;
        }
        
        /**
         * 获取特定资源类型的全局操作队列
         * @param resourceType String
         * @return OperationQueue
         */
        public String getGlobalResourceid(String resourceType) throws ConfigException{
        	ResourceInfo resourceInfo = getResourceInfoByType(resourceType);
        	return resourceInfo == null?null:resourceInfo.getGlobalresourceid();
            
        }

}
