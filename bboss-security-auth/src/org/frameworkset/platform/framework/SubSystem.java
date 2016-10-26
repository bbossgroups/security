package org.frameworkset.platform.framework;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.web.servlet.support.RequestContextUtils;

/**
 * 定义系统中的子系统功能模块
 *		属性：name－子系统中文名称
 *			 id－子系统标识
 *			 module－模块文件名称
 *			 baseuri－如果子系统部署在其他的应用			
 * @author biaoping.yin 
 * @date 2006-11-8 14:20:16
 * @version v1.0
 * @company sany.com.cn
 */
public class SubSystem implements java.io.Serializable {
	private String name;
	private Map<Locale,String> localeNames;
	private String module;
	private String baseuri;
	private String id;
	private Framework framework;
	private String logoutredirect = null;
	private String successRedirect = null;
	
	/**
	 * 子系统对应的框架模板路经
	 */
	private String template;
	/**
	 * 当前子系统缩主
	 */
	private SubSystem parent;
	public String getBaseuri() {
		return baseuri;
	}
	public void setBaseuri(String baseuri) {
		this.baseuri = baseuri;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
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
	public void setName(String name) {
		this.name = name;
	}
	public Framework getFramework() {
		return framework;
	}
	public void setFramework(Framework framework) {
		this.framework = framework;
		if(framework != null)
		{
			framework.setTemplate(this.getTemplate());
			framework.refactorTemplatePaths();
		}
	}
	public SubSystem getParent() {
		return parent;
	}
	public void setParent(SubSystem parent) {
		this.parent = parent;
	}
	
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof SubSystem))
			return false;
		SubSystem temp = (SubSystem)obj;
		return this.nameEqual(this.getName(),temp.getName()) 
				&& this.baseuriEqual(this.getBaseuri(),temp.getBaseuri()) 
				&& this.idEqual(this.getId(),temp.getId()) && this.moduleEqual(this.getModule(),temp.getModule());
		
		
	}
	
	
	
	private boolean nameEqual(String name1,String name2)
	{
		if(name1 == null && name2 == null)
			return true;
		if(name1 == null || name2 == null)
			return false;
		return name1.equalsIgnoreCase(name2);
	}
	
	private boolean idEqual(String id1,String id2)
	{
		if(id1 == null && id2 == null)
			return true;
		if(id1 == null || id2 == null)
			return false;
		return id1.equalsIgnoreCase(id2);
	}
	
	private boolean baseuriEqual(String baseuri1,String baseuri2)
	{
		if(baseuri1 == null && baseuri2 == null)
			return true;
		if(baseuri1 == null || baseuri2 == null)
			return false;
		return baseuri1.equalsIgnoreCase(baseuri2);
	}
	
	private boolean moduleEqual(String module1,String module2)
	{
		if(module1 == null && module2 == null)
			return true;
		if(module1 == null || module2 == null)
			return false;
		return module1.equalsIgnoreCase(module2);
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public Map<Locale, String> getLocaleNames() {
		return localeNames;
	}
	public void setLocaleNames(Map<Locale, String> localeNames) {
		this.localeNames = localeNames;
	}
	public String getLogoutredirect() {
		return logoutredirect;
	}
	public void setLogoutredirect(String logoutredirect) {
		this.logoutredirect = logoutredirect;
	}
	public String getSuccessRedirect() {
		return successRedirect;
	}
	public void setSuccessRedirect(String successRedirect) {
		this.successRedirect = successRedirect;
	}
}
