package org.frameworkset.platform.resource;

import java.io.Serializable;

import org.frameworkset.platform.config.model.Operation;
import org.frameworkset.platform.config.model.OperationQueue;
import org.frameworkset.platform.config.model.ResourceInfo;

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
public class UNProtectedResource implements Serializable{
    private ResourceInfo resouceInfo;
    private OperationQueue unprotectedoperations;
    private String resourceID;
    private String resourceType;
    public UNProtectedResource()
    {
    	unprotectedoperations = new OperationQueue();
    }
    
    public void addUnprotectedOperation(Operation opr)
    {
    	unprotectedoperations.addOperation(opr,false);
    }
    
    public OperationQueue getUnprotectedoperations()
    {
    	return this.unprotectedoperations;
    }
    
    public Operation getUNprotectedOperation(String oprid)
    {
    	for(int i =  0; i < this.unprotectedoperations.size(); i ++)
    	{
    		Operation opr = this.unprotectedoperations.getOperation(i);
    		if(opr.getId().equals(oprid))
    			return opr;
    	}
    	return null;
    }
    

    public static void main(String[] args) {
        UNProtectedResource unprotectedresource = new UNProtectedResource();
    }

    public String getResourceID() {
        return resourceID;
    }

    public String getResourceType() {
        return resourceType;
    }

    public ResourceInfo getResouceInfo() {
        return resouceInfo;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setResouceInfo(ResourceInfo resouceInfo) {
        this.resouceInfo = resouceInfo;
    }
}
