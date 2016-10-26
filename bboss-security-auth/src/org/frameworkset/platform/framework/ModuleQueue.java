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
public class ModuleQueue  {
    private List modules =   new ArrayList();


    public void addModule(Module module)
    {
        this.modules.add(module);
    }

    public Module getModule(int i)
    {
        return (Module)this.modules.get(i);
    }

    public int size()
    {
        return this.modules.size();
    }

    public Iterator iterator()
    {
        return this.modules.iterator();
    }

    public void clear()
    {
        this.modules.clear();
    }

    public List getList()
    {
        return this.modules;
    }
}
