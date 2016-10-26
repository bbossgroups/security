package org.frameworkset.platform.resource;

import java.io.Serializable;

import org.frameworkset.platform.config.model.Operation;
import org.frameworkset.platform.config.model.OperationQueue;
import org.frameworkset.platform.config.model.ResourceInfo;

public class ExcludeResource implements Serializable

{
	private ResourceInfo resouceInfo;
    private String resourceID;
    private String resourceType;
    private OperationQueue excludeoperations;
    public ExcludeResource()
    {
    	excludeoperations = new OperationQueue();
    }
    
    public void addExcludeOperation(Operation opr)
    {
    	excludeoperations.addOperation(opr,false);
    }
    
    public OperationQueue getExcludeoperations()
    {
    	return this.excludeoperations;
    }
    
    public Operation getExcludeOperation(String oprid)
    {
    	for(int i =  0; i < this.excludeoperations.size(); i ++)
    	{
    		Operation opr = this.excludeoperations.getOperation(i);
    		if(opr.getId().equals(oprid))
    			return opr;
    	}
    	return null;
    }
    
	public ResourceInfo getResouceInfo()
	{
		return resouceInfo;
	}
	public void setResouceInfo(ResourceInfo resouceInfo)
	{
		this.resouceInfo = resouceInfo;
	}
	public String getResourceID()
	{
		return resourceID;
	}
	public void setResourceID(String resourceID)
	{
		this.resourceID = resourceID;
	}
	public String getResourceType()
	{
		return resourceType;
	}
	public void setResourceType(String resourceType)
	{
		this.resourceType = resourceType;
	}

}
