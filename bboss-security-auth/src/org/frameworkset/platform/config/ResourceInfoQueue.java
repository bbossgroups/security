package org.frameworkset.platform.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.frameworkset.platform.config.model.ResourceInfo;

/**
 * <p>Title: ResourceQueue</p>
 *
 * <p>Description: 存储定义资源信息接口队列</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class ResourceInfoQueue implements java.io.Serializable {
    private List list = new LinkedList();
    public static void main(String[] args) {
        ResourceInfoQueue resourcequeue = new ResourceInfoQueue();
    }

    public void addResourceInfo(ResourceInfo resourceInfo)
    {
        this.list.add(resourceInfo);
    }
    
    public ResourceInfo removeResourceInfo(ResourceInfo resourceInfo)
    {
    	this.list.remove(resourceInfo);
    	return resourceInfo;
    }

    public ResourceInfo getResourceInfo(int i)
    {
        return (ResourceInfo)list.get(i);
    }

    public List getList()
    {
        return Collections.unmodifiableList(this.list);
    }
    
    public int size()
    {
        return this.list.size();
    }
}
