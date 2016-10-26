package org.frameworkset.platform.framework;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.web.servlet.support.RequestContextUtils;




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
public class Module extends BaseMenuItem{
    private String description;
    private Map<Locale,String> localeDescriptions;
    private MenuQueue menus; 
    private ItemQueue items;
    private ModuleQueue subModules;
    private String url;
    /**
     * 标识模块对应的module的权限状态：如果true标识module本身没有权限，但是下级菜单有权限，主要用于判别是否显示module的url地址，true不应该显示，因为没有module的直接权限，false时根据是否有module权限来显示url
     */
    private boolean usesubpermission = false;


   
 
    
   

    public Module()
    {
        this.items = new ItemQueue();
        this.subModules = new ModuleQueue();
        this.menus = new MenuQueue();
      
    }

    public void addSubModule(Module subModule)
    {
        this.subModules.addModule(subModule);
        this.menus.addMenuItem(subModule);
    }

    public void addItem(Item item)
    {
        this.items.addItem(item);
        this.menus.addMenuItem(item);
    }
    public String getDescription() {
        return description;
    }
    
    public String getDescription(HttpServletRequest request) {
    	
    	if(this.localeDescriptions == null)
    		return description;
    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
    	String temp = this.localeDescriptions.get(locale);
    	if(temp == null)
    		return description;
    	return temp;
    }

    public boolean isUsesubpermission() {
		return usesubpermission;
	}
	public void setUsesubpermission(boolean usesubpermission) {
		this.usesubpermission = usesubpermission;
	}

    public ItemQueue getItems() {
        return items;
    }

    

    public String getPath()
    {
        return path != null?path:(path = this.parentPath + "/" + this.id + "$module");
    }

    

    public ModuleQueue getSubModules() {
        return subModules;
    }

  

    public void setDescription(String description) {
        this.description = description;
    }

    public void setItems(ItemQueue items) {
        this.items = items;
    }


   

//    public MenuItem getParent() {
//        if(parentPath.equals(Framework.getSuperMenu(Framework.getSubsystemFromPath(parentPath))))
//        {
//        	if(this.subSystem == null)
//        		return Framework.getInstance().getRoot();
//        	else
//        		return Framework.getInstance(subSystem.getId()).getRoot();
//        }
//        if(this.subSystem == null)
//    		return Framework.getInstance().getMenu(this.parentPath);
//        else
//    		return Framework.getInstance(subSystem.getId()).getMenu(this.parentPath);
//    }

    /* (non-Javadoc)
     * @see com.frameworkset.platform.framework.MenuItem#isMain()
     */
    public boolean isMain() {
        // TODO Auto-generated method stub
        return false;
    }

	

	

	


	public String getArea() {
		// TODO Auto-generated method stub
		return null;
	}
	

	public Map<Locale, String> getLocaleDescriptions() {
		return localeDescriptions;
	}

	public void setLocaleDescriptions(Map<Locale, String> localeDescriptions) {
		this.localeDescriptions = localeDescriptions;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean hasSonOfModule()
	{
		
		return (getSubModules() != null && getSubModules().size() > 0) ||
				(getItems() != null && getItems().size() > 0);
				
	}

	public MenuQueue getMenus() {
		return menus;
	}

}
