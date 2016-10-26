package org.frameworkset.platform.security.authorization.impl;

import java.io.Serializable;

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
public class AccessPermission implements Serializable {
    private String resource;
    private String action;
    private String resourceType;
    public static void main(String[] args) {

    }
    public AccessPermission(String resource,String action,String resourceType)
    {
        this.resource = resource;
        this.action = action;
        this.resourceType = resourceType;
    }
    public String getAction() {
        return action;
    }

    public String getResource() {
        return resource;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    
    public boolean equal(Object obj)
    {
        if(obj == null || !(obj instanceof AccessPermission))
            return false;
        AccessPermission temp = (AccessPermission)obj;
        
        return temp.getAction().equals(this.getAction()) 
        	   && temp.getResource().equals(getResource()) 
        	   && temp.getResourceType().equals(getResourceType());  
        
        
    }
    
    public int hashCode()
    {
        return this.getResource().hashCode() 
		        + this.getAction().hashCode() 
		        + this.getResourceType().hashCode();
    }
    
    public String toString()
    {
        return new StringBuffer("Permission[resource=").append(this.resource)
        											.append(",operation=")
        											.append(this.action).append(",resourceType=")
        											.append(this.resourceType)
        											.append("]").toString();
    }
}
