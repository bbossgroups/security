package org.frameworkset.platform.framework;

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
public class ItemQueue  {

    private List items =   new ArrayList();


    public void addItem(Item item)
    {
        this.items.add(item);
    }

    public Item getItem(int i)
    {
        return (Item)this.items.get(i);
    }

    public int size()
    {
        return this.items.size();
    }

    public Iterator iterator()
    {
        return this.items.iterator();
    }

    public void clear()
    {
        this.items.clear();
    }

    public List getList()
    {
        return this.items;
    }

}
