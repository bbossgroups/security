package org.frameworkset.platform.framework;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: iSany</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class Root extends BaseMenuItem{ 
	private int code = 0;
	private SubSystem subSystem; 
	private boolean showpage = false;

    public String getParentPath() {
    	if(this.subSystem == null)
    		return Framework.getSuperMenu("module");
    	else
    		return Framework.getSuperMenu(this.subSystem.getId());
    }

    public String getId() {
        return "";
    }

    public MenuItem getParent() {
        return null;
    }

    public String getName() {
        return "";
    }

    public String getMouseclickimg() {
        return "";
    }

    public String getMouseoutimg() {
        return "";
    }

    public String getMouseoverimg() {
        return "";
    }

    public String getTitle() {
        return "";
    }

    public String getPath() {
    	if(this.subSystem == null)
    		return Framework.getSuperMenu("module");
    	else
    		return Framework.getSuperMenu(this.subSystem.getId());
    }

    public String getMouseupimg() {
        return "";
    }

    public boolean isUsed() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.frameworkset.platform.framework.MenuItem#isMain()
     */
    public boolean isMain() {
        // TODO Auto-generated method stub
        return false;
    }

	public String getHeadimg() {
		// TODO Auto-generated method stub
		return "";
	}

	public void setHeadimg(String headimg) {
		// TODO Auto-generated method stub
		
	}

	public int getCode() {
		return code;
	}

	public SubSystem getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(SubSystem subSystem) {
		this.subSystem = subSystem;
	}

	public String getTarget()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isShowpage()
	{
		// TODO Auto-generated method stub
		return showpage;
	}

	public void setShowpage(boolean showpage)
	{
		this.showpage = showpage;
	}

	public String getArea() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean showleftmenu = false; 
	public boolean isShowleftmenu() {
		return showleftmenu;
	}

	public void setShowleftmenu(boolean showleftmenu) {
		this.showleftmenu = showleftmenu;
	}

}
