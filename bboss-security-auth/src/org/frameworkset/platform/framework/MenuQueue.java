package org.frameworkset.platform.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuQueue {
	private List<MenuItem> menus = new ArrayList<MenuItem>();
	public MenuQueue() {
		// TODO Auto-generated constructor stub
	}
	public void addMenuItem(MenuItem module)
    {
        this.menus.add(module);
    }

    public MenuItem getMenuItem(int i)
    {
        return (MenuItem)this.menus.get(i);
    }

    public int size()
    {
        return this.menus.size();
    }

    public Iterator iterator()
    {
        return this.menus.iterator();
    }

    public void clear()
    {
        this.menus.clear();
    }

    public List<MenuItem> getList()
    {
        return this.menus;
    }

}
