package org.frameworkset.platform.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcludeResourceQueue implements Serializable
{
	private List queue = new ArrayList();
   

    public void addExcludeResource(ExcludeResource resource)
    {
        this.queue.add(resource);
    }

    public ExcludeResource getExcludeResource(int i)
    {
        return (ExcludeResource)this.queue.get(i);
    }

    public int size()
    {
        return this.queue.size();
    }

    public Iterator iterator()
    {
        return queue.iterator();
    }

}
