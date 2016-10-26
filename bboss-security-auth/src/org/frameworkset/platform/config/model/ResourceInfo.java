package org.frameworkset.platform.config.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.config.ResourceInfoQueue;
import org.frameworkset.platform.resource.ExcludeResource;
import org.frameworkset.platform.resource.ExcludeResourceQueue;
import org.frameworkset.platform.resource.ResourceIndentity;
import org.frameworkset.platform.resource.UNProtectedResource;
import org.frameworkset.platform.resource.UNProtectedResourceQueue;
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
public class ResourceInfo extends I18nResource {
    private String resource;
    private String id;
    private boolean defaultResourceInfo = false;
    private ResourceInfo parent;
    private String parentID;
    private Resources resources;
    private ResourceInfoQueue subResources ;
    private ResourceIndentity resourceIndentity;
    private String name;
  

	/**自定义资源类型是否维护数据标识，默认为true，true标识维护数据，false，标识不维护数据*/
    private boolean maintaindata = true;
    
    private UNProtectedResourceQueue unProtectedResources;
    private ExcludeResourceQueue excludeResources;
    /**
     * 标识资源是否使用
     */
    private boolean used = true;
    /**
     * 标识资源是否是自动维护的，缺省为false
     */
    private boolean auto = false;
    
    /**
     * 资源类型对应的资源数据结构，目前提供两类：列表（list）和树型(tree),缺省为list
     */
    private String struction = "list";
    
    /**
     * 标识给定的资源属于那个系统的属性，可能有多个，以‘,’分隔。例如：module,cms
     */
    private String system = "module";
    
    /**
     * 存储每个系统名称
     */
    private List systems = new ArrayList();
    
    


    /**
     * 资源对应的操作组id
     */
    private String operationGroupID;
    
    
    /**
     * 本资源类别的全局资源标识，用来控制资源类别的全局资源操作
     */
    private String globalresourceid;
    
    private String globalOperationGroupID;
    
    
    
    
    private boolean defaultAllowIfNoRequiredRole = true;
    private boolean allowIfNoRequiredRole = false;

    public ResourceInfo()
    {
        unProtectedResources = new UNProtectedResourceQueue();
        excludeResources = new ExcludeResourceQueue();
        subResources = new ResourceInfoQueue();
    }
    public String getResource() {
        return resource;
    }

    public boolean isDefaultResourceInfo() {
        return defaultResourceInfo;
    }

    public String getId() {
        return id;
    }


    public ResourceIndentity getResourceIndentity() {
        if(resourceIndentity == null)
        {
            try {
                resourceIndentity = (ResourceIndentity) Class.forName(
                        getResource()).newInstance();
                resourceIndentity.setResourceInfo(this);
            } catch (ClassNotFoundException ex) {
            } catch (IllegalAccessException ex) {
            } catch (InstantiationException ex) {
            }
        }
        return resourceIndentity;
    }

    public String getName() {
        return name;
    }

    public Resources getResources() {
        return resources;
    }


    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setDefaultResourceInfo(boolean defaultResourceInfo) {
        this.defaultResourceInfo = defaultResourceInfo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public void setOperationGroupID(String operationGroupID) {
        this.operationGroupID = operationGroupID;
    }

    public void setParent(ResourceInfo parent) {
        this.parent = parent;
    }
    
    public void addSubResourceInfo(ResourceInfo res)
    {
        res.setParent(this);
        this.subResources.addResourceInfo(res);
    }
    
    

    public void addUNProtectedResource(UNProtectedResource unProtectedResource)
    {
        this.unProtectedResources.addUNProtectedResource(unProtectedResource);
    }

    public UNProtectedResourceQueue getUNProtectedResourceQueue()
    {
        return this.unProtectedResources;
    }


    public String getOperationGroupID() {
        return operationGroupID;
    }

    public OperationGroup getOperationGroup() {
    	if(getOperationGroupID() == null)
    		return new OperationGroup(); 
        return resources.getOperationGroup(getOperationGroupID());
    }

    public OperationQueue getOperationQueue() {
        
       return resources.getOperationGroup(getOperationGroupID()).getOperationQueue();
   }

   public Operation getOperationByid(String operID) {
       return resources.getOperationGroup(getOperationGroupID()).getOperationByID(operID);
   }

   

    public ResourceInfo getParent() {
        return parent;
    }
    /**
     * @return Returns the parentID.
     */
    public String getParentID() {
        return parentID;
    }
    /**
     * @param parentID The parentID to set.
     */
    public void setParentID(String parentID) {
        this.parentID = parentID;
    }
    /**
     * @return Returns the subResources.
     */
    public ResourceInfoQueue getSubResources() {
        return subResources;
    }
    /**
     * @return Returns the auto.
     */
    public boolean isAuto() {
        return auto;
    }
    /**
     * @param auto The auto to set.
     */
    public void setAuto(boolean auto) {
        this.auto = auto;
    }
    /**
     * @return Returns the used.
     */
    public boolean isUsed() {
        return used;
    }
    /**
     * @param used The used to set.
     */
    public void setUsed(boolean used) {
        this.used = used;
    }
	public String getStruction() {
		return struction;
	}
	public void setStruction(String struction) {
		this.struction = struction;
	}
	public boolean allowIfNoRequiredRole() {
		return allowIfNoRequiredRole;
	}
	public void setAllowIfNoRequiredRole(boolean allowIfNoRequiredRole) {
		this.allowIfNoRequiredRole = allowIfNoRequiredRole;
	}
	public boolean defaultAllowIfNoRequiredRole() {
		return defaultAllowIfNoRequiredRole;
	}
	public void setDefaultAllowIfNoRequiredRole(boolean defaultAllowIfNoRequiredRole) {
		this.defaultAllowIfNoRequiredRole = defaultAllowIfNoRequiredRole;
	}
	
	public void addExcludeResource(ExcludeResource excludeResource)
	{
		excludeResources.addExcludeResource(excludeResource);
		
	}
	public ExcludeResourceQueue getExcludeResources()
	{
		return excludeResources;
	}
	public String getSystem() {
		return system;
	}
	
	public List getSystems()
	{
		return this.systems;
	}
	public boolean containSystem(String system)
	{
		for(int i = 0; i < this.systems.size(); i ++)
		{
			if(systems.get(i).toString().equals(system))
				return true;
			if(systems.get(i).toString().equals("*"))
				return true;
			
		}
		return false;
	}
	public void setSystem(String system) {
		this.system = system;
		splitSystems(system);
	}
	private void splitSystems(String systems)
	{
		String[] systems_ = systems.split("\\,");
		for(int i =0; i < systems_.length; i ++)
		{
			this.systems.add(systems_[i]);
		}
	}
	public String getGlobalOperationGroupID() {
		return globalOperationGroupID;
	}
	public void setGlobalOperationGroupID(String globalOperationGroupID) {
		this.globalOperationGroupID = globalOperationGroupID;
	}
	public String getGlobalresourceid() {
		return globalresourceid;
	}
	public void setGlobalresourceid(String globalresourceid) {
		this.globalresourceid = globalresourceid;
	}
	
	public OperationQueue getGlobalOperationQueue()
	{
		return this.resources.getOperationQueue(this.getGlobalOperationGroupID());
	}
	
	public OperationQueue getCommonGlobalOperations()
	{
		return this.resources.getCommonOperationQueue(this.getGlobalOperationGroupID());
	}
	
	public OperationQueue getManagerGlobalOperations()
	{
		return this.resources.getManagerOperationQueue(this.getGlobalOperationGroupID());
	}
	
	
	 public Operation getGlobalOperationByid(String operID) {
		   if(getGlobalOperationGroupID() == null)
			   return null;
		   try
		   {
			   return resources.getOperationGroup(getGlobalOperationGroupID()).getOperationByID(operID);
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
			   return null;
		   }
	       
	   }
	public boolean maintaindata() {
		return maintaindata;
	}
	
	public boolean getMaintaindata(){
		return maintaindata;
	}
	
	public void setMaintaindata(boolean maintaindata) {
		this.maintaindata = maintaindata;
	}
	public String getName(HttpServletRequest request) {
    	
//    	if(this.localeNames == null)
//    		return null;
//    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
//    	String temp = this.localeNames.get(locale);
//    	if(temp == null)
//    		return null;
//    	return temp;
//    	
		String temp = super.getName(request);
    	return temp == null?name:temp;
    }
}
