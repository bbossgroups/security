package org.frameworkset.platform.framework;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.web.servlet.support.RequestContextUtils;

import org.frameworkset.platform.security.AuthorResource;

public abstract class BaseMenuItem extends AuthorResource implements MenuItem {
	protected String name;
	protected Map<Locale, String> localeNames;
	protected String id;
	protected String mouseoverimg;
	protected Map<Locale, String> localeMouseoverimgs;
	protected String mouseoutimg;
	protected Map<Locale, String> localeMouseoutimgs;
	protected String mouseclickimg;
	protected Map<Locale, String> localeMouseclickimgs;
	protected String mouseupimg;
	protected Map<Locale, String> localeMouseupimgs;
	private String desktop_width ="777";
	private String desktop_height ="500";
	/** outlookbar图标头部分 */
	protected String headimg;
	/**
	 * 展示模式，true-弹出窗口
	 * false 不弹出窗口
	 * 默认为false
	 * 可以在item和module上面设置popup属性
	 */
	protected String option ="{}";

	
	protected Map<Locale, String> localeHeadimgs;
	protected String title;
	protected Map<Locale, String> localeTitles;
	protected String target;

	protected boolean used = true;
	/**
	 * 格式:menu://rootid$type/subid$type
	 */
	protected String parentPath;

	protected String path;
	protected int code = 0;
	protected SubSystem subSystem;
	protected boolean showpage = false;
	protected Map<String,String> extendAttributes = null;
	
	public void addLocaleName(String locale,String name)
	{
		
	}
	public MenuItem getParent() {
		if (parentPath.equals(Framework.getSuperMenu(Framework
				.getSubsystemFromPath(parentPath)))) {
			if (this.subSystem == null)
				return Framework.getInstance().getRoot();
			else
				return Framework.getInstance(subSystem.getId()).getRoot();
		}
		if (this.subSystem == null)
			return Framework.getInstance().getMenuByPath(this.parentPath);
		else
			return Framework.getInstance(subSystem.getId()).getMenuByPath(
					this.parentPath);
	}
	public boolean isShowpage() {
		return showpage;
	}

	public void setShowpage(boolean showpage) {
		this.showpage = showpage;
	}
	public String getId() {
		return id;
	}

	public String getHeadimg() {
		return headimg;
	}
	
	public String getHeadimg(HttpServletRequest request) {
    	
    	if(this.localeHeadimgs == null)
    		return headimg;
    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
    	String temp = this.localeHeadimgs.get(locale);
    	if(temp == null)
    		return headimg;
    	return temp;
    }


	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getTarget() {
		// TODO Auto-generated method stub
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getName() {
		return name;
	}
	  public String getName(HttpServletRequest request) {
	    	
	    	if(this.localeNames == null)
	    		return name;
	    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
	    	String temp = this.localeNames.get(locale);
	    	if(temp == null)
	    		return name;
	    	return temp;
	    }
	public String getMouseclickimg() {
		return mouseclickimg;
	}
	
	  public String getMouseclickimg(HttpServletRequest request) {
	    	
	    	if(this.localeMouseclickimgs == null)
	    		return mouseclickimg;
	    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
	    	String temp = this.localeMouseclickimgs.get(locale);
	    	if(temp == null)
	    		return mouseclickimg;
	    	return temp;
	    }

	public String getMouseoutimg() {
		return mouseoutimg;
	}
	  public String getMouseoutimg(HttpServletRequest request) {
	    	
	    	if(this.localeMouseoutimgs == null)
	    		return mouseoutimg;
	    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
	    	String temp = this.localeMouseoutimgs.get(locale);
	    	if(temp == null)
	    		return mouseoutimg;
	    	return temp;
	    }
	public String getMouseoverimg() {
		return mouseoverimg;
	}
	
	  public String getMouseoverimg(HttpServletRequest request) {
	    	
	    	if(this.localeMouseoverimgs == null)
	    		return mouseoverimg;
	    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
	    	String temp = this.localeMouseoverimgs.get(locale);
	    	if(temp == null)
	    		return mouseoverimg;
	    	return temp;
	    }

	public String getTitle() {
		return title;
	}
	
	  public String getTitle(HttpServletRequest request) {
	    	
	    	if(this.localeTitles == null)
	    		return title;
	    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
	    	String temp = this.localeTitles.get(locale);
	    	if(temp == null)
	    		return title;
	    	return temp;
	    }

	public String getParentPath() {
		return parentPath;
	}

	public String getMouseupimg() {
		return mouseupimg;
	}
	  public String getMouseupimg(HttpServletRequest request ) {		  
	    	if(this.localeMouseupimgs == null)
	    		return mouseupimg;
	    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
	    	String temp = this.localeMouseupimgs.get(locale);
	    	if(temp == null)
	    		return mouseupimg;
	    	return temp;
	    }
	public boolean isUsed() {
		return used;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMouseclickimg(String mouseclickimg) {
		this.mouseclickimg = mouseclickimg;
	}

	public void setMouseoutimg(String mouseoutimg) {
		this.mouseoutimg = mouseoutimg;
	}

	public void setMouseoverimg(String mouseoverimg) {
		this.mouseoverimg = mouseoverimg;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMouseupimg(String mouseupimg) {
		this.mouseupimg = mouseupimg;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public SubSystem getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(SubSystem subSystem) {
		this.subSystem = subSystem;
	}
	public Map<Locale, String> getLocaleNames() {
		return localeNames;
	}
	public void setLocaleNames(Map<Locale, String> localeNames) {
		this.localeNames = localeNames;
	}
	public Map<Locale, String> getLocaleMouseoverimgs() {
		return localeMouseoverimgs;
	}
	public void setLocaleMouseoverimgs(Map<Locale, String> localeMouseoverimgs) {
		this.localeMouseoverimgs = localeMouseoverimgs;
	}
	public Map<Locale, String> getLocaleMouseoutimgs() {
		return localeMouseoutimgs;
	}
	public void setLocaleMouseoutimgs(Map<Locale, String> localeMouseoutimgs) {
		this.localeMouseoutimgs = localeMouseoutimgs;
	}
	public Map<Locale, String> getLocaleMouseclickimgs() {
		return localeMouseclickimgs;
	}
	public void setLocaleMouseclickimgs(Map<Locale, String> localeMouseclickimgs) {
		this.localeMouseclickimgs = localeMouseclickimgs;
	}
	public Map<Locale, String> getLocaleMouseupimgs() {
		return localeMouseupimgs;
	}
	public void setLocaleMouseupimgs(Map<Locale, String> localeMouseupimgs) {
		this.localeMouseupimgs = localeMouseupimgs;
	}
	public Map<Locale, String> getLocaleHeadimgs() {
		return localeHeadimgs;
	}
	public void setLocaleHeadimgs(Map<Locale, String> localeHeadimgs) {
		this.localeHeadimgs = localeHeadimgs;
	}
	public Map<Locale, String> getLocaleTitles() {
		return localeTitles;
	}
	public void setLocaleTitles(Map<Locale, String> localeTitles) {
		this.localeTitles = localeTitles;
	}
	public Map<String, String> getExtendAttributes() {
		return extendAttributes;
	}
	public String getStringExtendAttribute(String name) {
		return extendAttributes != null ?extendAttributes.get(name):null;
	}
	
	public String getStringExtendAttribute(String name,String defaultValue) {
		String value = extendAttributes != null ?extendAttributes.get(name):null;
		if(value == null)
			value = defaultValue;
		return value;
	}
	public void setExtendAttributes(Map<String, String> extendAttributes) {
		this.extendAttributes = extendAttributes;
	}
	
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	
	public String getDesktop_width() {
		return desktop_width;
	}

	public void setDesktop_width(String desktop_width) {
		this.desktop_width = desktop_width;
	}

	public String getDesktop_height() {
		return desktop_height;
	}

	public void setDesktop_height(String desktop_height) {
		this.desktop_height = desktop_height;
	}	
	
	private boolean showleftmenu = false; 
	public boolean isShowleftmenu() {
		return showleftmenu;
	}

	public void setShowleftmenu(boolean showleftmenu) {
		this.showleftmenu = showleftmenu;
	}

}
