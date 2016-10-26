package org.frameworkset.platform.config.model;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.security.AuthorResource;
import org.frameworkset.platform.security.authorization.impl.ResourceToken;
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
public class Operation extends I18nResource {
    private String id;
    private String name;
    private String priority;
    /**
     * 管理员和部门管理员特有操作标识，默认为false，所有的用户都可以授予该操作，为true时
     * 只有管理员和部门管理员才能授予改操作
     */
    private boolean manager = true;
    private OperationGroup operationGroup;
    private AuthorResource authorResource = new AuthorResource();
    public AuthorResource getAuthorResource() {
		return authorResource;
	}
    public void addAuthorResource(String authorResource)
	{
	
		this.authorResource.addAuthorResource(authorResource);
	}
    
    

	

	public Operation()
    {
    	
    }
    /**
     * 定义同操作组中比当前操作优先级低的操作队列
     */
    private OperationQueue lowerOperations;
    
    /**
     * 定义同操作组中比当前操作优先级相同的操作队列
     */
    private OperationQueue sblingOperations;
    
    
    /**
     * 定义同操作组中与当前操作互斥的操作队列
     */
    private OperationQueue huchiOperations;
	private String description;
    public static void main(String[] args) {
        Operation operation = new Operation();
    }

    public String getId() {
        return id;
    }



    public String getName() {
        return name;
    }

    public OperationGroup getOperationGroup() {
        return operationGroup;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setOperationGroup(OperationGroup operationGroup) {
        this.operationGroup = operationGroup;
    }
    /**
     * @return Returns the priority.
     */
    public String getPriority() {
        return priority;
    }
    /**
     * @param priority The priority to set.
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

	public OperationQueue getHuchiOperations() {
		
		return huchiOperations;
	}

	

	public OperationQueue getLowerOperations() {
		
		return lowerOperations;
	}
	
	public OperationQueue getSblingOperations() {
		
		return this.sblingOperations;
	}
	
	public void classify()
	{
		this.lowerOperations = new OperationQueue();
		this.sblingOperations = new OperationQueue();
		this.huchiOperations = new OperationQueue();
		OperationQueue temp = this.operationGroup.getOperationQueue();
		char[] priority_ = this.priority.toCharArray();
		
		for(int i = 0; temp != null && i < temp.size(); i ++)
		{
			Operation op = temp.getOperation(i);
			if(op.getId().equals(this.getId()))
				continue;
			char[] priority_temp = op.getPriority().toCharArray();
			if(priority_temp[1] != priority_[1])
			{
				this.huchiOperations.addOperation(op);
			}
			else
			{
				if(priority_temp[0] == priority_[0])
				{
					this.sblingOperations.addOperation(op);
				}
				else if(priority_temp[0] < priority_[0])
				{
					this.lowerOperations.addOperation(op);
				}
			}
		}
	}

	public void setDescription(String description) {
		this.description = description;
		
	}

	public String getDescription() {
		return description;
	}

	public boolean isManager() {
		return manager;
	}

	public void setManager(boolean manager) {
		this.manager = manager;
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
	
	public String getDescription(HttpServletRequest request) {
		String temp = super.getDescription(request);
    	return temp == null?description:temp;
		
	}
	
	public List<ResourceToken> getAuthoresouresList()
	{
		return this.authorResource.getAuthorResources();
	}




}
