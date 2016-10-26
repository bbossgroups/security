package org.frameworkset.platform.config.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.util.I18nResource;

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
public class OperationGroup extends I18nResource {
//	/**
//	 * 区分操作组是否是
//	 */
//	private boolean global = false;
	
	
    private String groupID;
    private String groupName;
    private OperationQueue operationQueue;
    //获取所有用户都能授予的全局操作
    private OperationQueue commonOperationQueue;
  //获取所有用户都能授予的全局操作
    private OperationQueue managerOperationQueue;
    private Map operationIndexByID;
    private Resources resources;
    private boolean defaultable;
    public OperationGroup()
    {
        operationQueue = new OperationQueue();
        commonOperationQueue = new OperationQueue();
        managerOperationQueue = new OperationQueue();
        operationIndexByID = new HashMap();
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        OperationGroup operationgroup = new OperationGroup();
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public OperationQueue getOperationQueue() {
    	if(operationQueue == null)
    		return new OperationQueue();
        return operationQueue;
    }

    public Resources getResources() {
        return resources;
    }

    public boolean isDefaultable() {
        return defaultable;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public void setDefaultable(boolean defaultable) {
        this.defaultable = defaultable;
    }

    public void addOperation(Operation operation)
    {
        operation.setOperationGroup(this);
        operationIndexByID.put(operation.getId(),operation);
        operationQueue.addOperation(operation);
        if(operation.isManager()){
        	managerOperationQueue.addOperation(operation);
        }else{
        	commonOperationQueue.addOperation(operation);
        }
    }
    

    public Operation getOperationByID(String id)
    {
        return (Operation)operationIndexByID.get(id);
    }

    private void jbInit() throws Exception {
    }
    
    /**
     * 设置每个操作的优先级低的操作和互斥的操作以及同级的操作
     */
    public void handle()
    {
    	Set sets = operationIndexByID.entrySet();
    	Iterator its = sets.iterator();
    	for(int i = 0; i < this.operationQueue.size(); i ++)
    	{
    		Map.Entry et = (Map.Entry)its.next();
    		Operation operation = (Operation)et.getValue();
    		operation.classify();    		 
    	}
    }
//	public boolean isGlobal() {
//		return global;
//	}
//	public void setGlobal(boolean global) {
//		this.global = global;
//	}
	public OperationQueue getCommonOperationQueue() {
		return commonOperationQueue;
	}
	public OperationQueue getManagerOperationQueue() {
		return managerOperationQueue;
	}
	
	public String getGroupName(HttpServletRequest request) {
	    	
	//    	if(this.localeNames == null)
	//    		return null;
	//    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
	//    	String temp = this.localeNames.get(locale);
	//    	if(temp == null)
	//    		return null;
	//    	return temp;
	//    	
			String temp = super.getName(request);
	    	return temp == null?this.groupName:temp;
	    }

}
