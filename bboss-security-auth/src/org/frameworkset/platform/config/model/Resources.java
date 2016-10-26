package org.frameworkset.platform.config.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.frameworkset.platform.config.ResourceInfoQueue;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class Resources implements java.io.Serializable {
    private String moduleName;
    private ApplicationInfo ApplicationInfo;
    private Map<String,Locale> languages;
    public Map<String, Locale> getLanguages() {
		return languages;
	}

	public void setLanguages(Map<String, Locale> languages) {
		this.languages = languages;
	}


	/**
     * 资源识别器队列
     */
    private ResourceInfoQueue resourceQueue;

    /**
     * 操作组索引，根据操作组id建立的操作组索引
     */
    private Map operationGroups;

    /**
     * 缺省操作组
     */
    private OperationGroup defaultOperationGroup;
    
    private List resourceFiles = new ArrayList();

    /**
     * 根据id建立的资源识别器索引
     */
    private Map resourceIndexsByid;
    /**
     * 根据id建立的资源识别器索引
     */
    private ResourceInfo defaultResourceInfo;

    public void addImportResourceFile(ImportResource ir)
    {
    	resourceFiles.add(ir);
    }
    
    public List getResourceFiles()
    {
    	return this.resourceFiles;
    }
    public Resources()
    {
        resourceIndexsByid = new HashMap();
        operationGroups = new HashMap();
        resourceQueue = new ResourceInfoQueue();
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Resources resources = new Resources();
    }

    public ResourceInfo getDefaultResourceInfo() {
        if(defaultResourceInfo == null)
        {
            return this.resourceQueue.getResourceInfo(0);
        }
        return defaultResourceInfo;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ResourceInfoQueue getResourceQueue() {
        return resourceQueue;
    }

    public OperationGroup getDefaultOperationGroup() {
        return defaultOperationGroup;
    }

    public ApplicationInfo getApplicationInfo() {
        return ApplicationInfo;
    }

    public void setDefaultResourceInfo(ResourceInfo defaultResourceInfo) {
        this.defaultResourceInfo = defaultResourceInfo;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setDefaultOperationGroup(OperationGroup defaultOperationGroup) {
        this.defaultOperationGroup = defaultOperationGroup;
    }

    public void setApplicationInfo(ApplicationInfo ApplicationInfo) {
        this.ApplicationInfo = ApplicationInfo;
    }

    public ResourceInfo getResourceInfoByid(String id) {
    	if(id == null)
    		return null;
        return (ResourceInfo)this.resourceIndexsByid.get(id);
    }




    public void addResourceInfo(ResourceInfo resourceInfo)
    {
        resourceInfo.setResources(this);
        this.resourceIndexsByid.put(resourceInfo.getId(),resourceInfo);
        this.resourceQueue.addResourceInfo(resourceInfo);
    }
    
    /**
     * 从系统中删除资源信息
     * @param resourceInfo
     */
    public ResourceInfo removeResourceInfo(ResourceInfo resourceInfo)
    {
    	if(resourceInfo == null)
    		return null;
//        resourceInfo.setResources(this);
        this.resourceIndexsByid.remove(resourceInfo.getId());
        this.resourceQueue.removeResourceInfo(resourceInfo);
        return resourceInfo;
    }
    
    public void addNestingResourceInfo(ResourceInfo resourceInfo)
    {
        resourceInfo.setResources(this);
        this.resourceIndexsByid.put(resourceInfo.getId(),resourceInfo);        
    }
    
    

    public void addOperationGroup(OperationGroup operGroup)
    {
        operGroup.setResources(this);
        this.operationGroups.put(operGroup.getGroupID(),operGroup);
    }

    /**
     * 根据操作组id获取操作组
     * @param groupID String
     * @return OperationGroup
     */
    public OperationGroup getOperationGroup(String groupID)
    {
    	if(groupID == null)
    	{
    		return new OperationGroup();
    	}
        return (OperationGroup)operationGroups.get(groupID);
    }

    /**
     * 根据操作组id获取全部操作
     * @param groupID String
     * @return OperationQueue
     */
    public OperationQueue getOperationQueue(String groupID)
    {
    	if(groupID == null)
    		return null;
        OperationGroup operationGroup = getOperationGroup(groupID);
        if(operationGroup == null)
            return null;
        return operationGroup.getOperationQueue();
    }
    
    /**
     * 根据操作组id获取普通用户可以授予的操作
     * @param groupID String
     * @return OperationQueue
     */
    public OperationQueue getCommonOperationQueue(String groupID)
    {
    	if(groupID == null)
    		return null;
        OperationGroup operationGroup = getOperationGroup(groupID);
        if(operationGroup == null)
            return null;
        return operationGroup.getCommonOperationQueue();
    }
    
    /**
     * 根据操作组id获取只有管理员才能授予的操作
     * @param groupID String
     * @return OperationQueue
     */
    public OperationQueue getManagerOperationQueue(String groupID)
    {
    	if(groupID == null)
    		return null;
        OperationGroup operationGroup = getOperationGroup(groupID);
        if(operationGroup == null)
            return null;
        return operationGroup.getManagerOperationQueue();
    }


    private void jbInit() throws Exception {
    }


}
