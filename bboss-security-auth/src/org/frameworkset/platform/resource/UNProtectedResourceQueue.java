package org.frameworkset.platform.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class UNProtectedResourceQueue implements Serializable{
    private List queue = new ArrayList();
    public static void main(String[] args) {
        UNProtectedResourceQueue unprotectedresourcequeue = new
                UNProtectedResourceQueue();
    }

    public void addUNProtectedResource(UNProtectedResource unProtectedResource)
    {
        this.queue.add(unProtectedResource);
    }

    public UNProtectedResource getUNProtectedResource(int i)
    {
        return (UNProtectedResource)this.queue.get(i);
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
