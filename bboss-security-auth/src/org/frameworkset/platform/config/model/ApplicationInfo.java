package org.frameworkset.platform.config.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.frameworkset.spi.BaseSPIManager;
import org.frameworkset.spi.assemble.ManagerImport;
import org.frameworkset.spi.assemble.ProviderManagerInfo;
import org.frameworkset.spi.assemble.ServiceProviderManager;

import org.frameworkset.platform.config.LoginModuleInfoQueue;
import org.frameworkset.platform.config.ResourceInfoQueue;

/**
 * <p>Title: </p>
 *
 * <p>Description: 安全基本配置信息
          sso:单点登录控制
          application:指定应用名称
          module:指定web模块名称
          authorfaileddirect:登录失败跳转页面
          loginpage：登陆页面
   </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class ApplicationInfo implements java.io.Serializable {
    private boolean sso = false;
    private String application="";
    
    private List propertyFiles = new ArrayList();
    
    public void addPropertyFile(PropertiesFile PropertiesFile)
    {
    	propertyFiles.add(PropertiesFile);
    }
    
    public List getPropertyFiles()
    {
    	return this.propertyFiles;
    }
    /**
     * 缺省的web模块
     */
    private String module = "";
    private String authorfaileddirect = "";
    private String loginpage = "";
    private boolean securityenabled= false;

    /**
     * 记录providerManagerType列表
     */
    private Map providerManagerTypes;
    /**
     * 定义全局的缓冲开关
     */
    private boolean cachable = false;

    /**
     * 根据类型建立的消息激发器索引表
     */
    private Map notifiableInfos;

    /**
     * 根据名称建立的登录模块索引表
     */
    private Map loginModuleIndexs;

    /**
     * 登录模块列表
     */
    private LoginModuleInfoQueue loginModules;


    
   /**
          资源操作许可表
          cachable：可选属性，是否需要缓冲资源/操作/角色之间的关系
          class：资源/操作/角色之间的关系映射实现
   */

    private PermissionRoleMapInfo defaultPermissionrolemapinfo;
    private Map permissionRoleMapInfoIdxs;
    private Map<String,Resources> resourcsIndexByModule;
    /**
          权限控制表
          cachable：可选属性，是否需要缓冲用户/角色之间的关系
          class：用户/角色之间的关系映射实现
    */
    private AuthorTableInfo defaultAuthorTableInfo;
    private Map authorTableInfoIdxs;
    /**缺省安全配置信息标识*/
    private boolean defaultApplicationInfo;

    private String cacheType;

    /**
     * 系统通用配置属性，灵活配置
     */
    private Map commonProperties;
	private List managerimports;

    public ApplicationInfo()
    {
        
        resourcsIndexByModule = Collections.synchronizedMap(new HashMap());
        authorTableInfoIdxs = Collections.synchronizedMap(new HashMap());
        permissionRoleMapInfoIdxs = Collections.synchronizedMap(new HashMap());
//        providerManagerTypes = Collections.synchronizedList(new ArrayList());
        providerManagerTypes = new HashMap();
        commonProperties = Collections.synchronizedMap(new HashMap());
        notifiableInfos = Collections.synchronizedMap(new HashMap());
        loginModuleIndexs = Collections.synchronizedMap(new HashMap());
        loginModules = new LoginModuleInfoQueue();
        managerimports = new ArrayList();
       
    }


    public static void main(String[] args) {
        ApplicationInfo ApplicationInfo = new ApplicationInfo();
    }

    public String getApplication() {
        return application;
    }

    public String getAuthorfaileddirect() {
        return authorfaileddirect;
    }

    public String getLoginpage() {
        return loginpage;
    }

    public String getModule() {
        return module;
    }

    public boolean isSso() {
        return sso;
    }

    public Map getManagerInfos() {
//        return ServiceProviderManager.getInstance().getManagers();
        return BaseSPIManager.getDefaultApplicationContext().getManagers();
    }

    public ProviderManagerInfo getProviderManagerInfo(String id)
    {
        return (ProviderManagerInfo)getManagerInfos().get(id);
    }

    public AuthorTableInfo getAuthorizetableInfo() {
        return this.defaultAuthorTableInfo;
    }

    public PermissionRoleMapInfo getPermissionrolemapInfo() {
        return defaultPermissionrolemapinfo;
    }

    public boolean isSecurityenabled() {
        return securityenabled;
    }

    public boolean isDefaultApplicationInfo() {
        return defaultApplicationInfo;
    }

    public ResourceInfoQueue getResourceInfoQueue(String module) {
        return ((Resources)resourcsIndexByModule.get(module)).getResourceQueue();

    }

    public Resources getResources(String module)
    {
        return (Resources)resourcsIndexByModule.get(module);
    }




    public ResourceInfo getDefaultResourceInfo(String module) {
        return ((Resources)resourcsIndexByModule.get(module)).getDefaultResourceInfo();
    }



    public ResourceInfo getResourceInfoById(String module,String id) {
        return ((Resources)resourcsIndexByModule.get(module)).getResourceInfoByid(id);
    }
    /**
     * 通过资源类型id删除资源类型信息
     * @param module
     * @param id
     * @return
     */
    public ResourceInfo removeResourceInfoById(String module,String id)
    {
    	
    	return ((Resources)resourcsIndexByModule.get(module)).removeResourceInfo(getResourceInfoById( module,id));
    }


    public Map<String,Resources> getResourcsIndexByModule() {
		return resourcsIndexByModule;
	}

	public boolean isCachable() {
        return cachable;
    }

    public Map getProviderManagerTypes() {
        return providerManagerTypes;
    }

    public String getCacheType() {
        return cacheType;
    }

    public ProviderManagerInfo getDefaultProviderManagerInfo() {
//        return ServiceProviderManager.getInstance().getDefaultProviderManagerInfo();
        return BaseSPIManager.getDefaultApplicationContext().getServiceProviderManager().getDefaultProviderManagerInfo();
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setAuthorfaileddirect(String authorfaileddirect) {
        this.authorfaileddirect = authorfaileddirect;
    }

    public void setLoginpage(String loginpage) {
        this.loginpage = loginpage;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setSso(boolean sso) {
        this.sso = sso;
    }

    

    public void setAuthorizetable(AuthorTableInfo authorizetable) {
        authorizetable.setApplicationInfo(this);
        this.authorTableInfoIdxs.put(authorizetable.getModuleName(),authorizetable);
    }

    public void setPermissionRoleMapInfo(PermissionRoleMapInfo permissionrolemapinfo) {
        permissionrolemapinfo.setApplicationInfo(this);
        this.permissionRoleMapInfoIdxs.put(permissionrolemapinfo.getModuleName(),permissionrolemapinfo);
    }

    public PermissionRoleMapInfo getPermissionRoleMapInfo(String moduleName)
    {
        return (PermissionRoleMapInfo)permissionRoleMapInfoIdxs.get(moduleName);
    }

    public void setSecurityenabled(boolean securityenabled) {
        this.securityenabled = securityenabled;
    }

    public void setDefaultApplicationInfo(boolean defaultApplicationInfo) {
        this.defaultApplicationInfo = defaultApplicationInfo;
    }







    public void setCachable(boolean cachable) {
        this.cachable = cachable;
    }

//    public void setProviderManagerInfoTypes(List providerManagerTypes) {
//        this.providerManagerTypes = providerManagerTypes;
//    }
    
    public void addProviderManagerInfo(ProviderManagerInfo providerManager)
    {
    	this.providerManagerTypes.put(providerManager.getId(), providerManager);
//        this.providerManagerTypes.add(providerManager.getId());
//        providerManager.setApplicationInfo(this);
//        ServiceProviderManager.getInstance().addProviderManagerInfo(providerManager);
       
    }




    /**
     * setCacheType
     *
     * @param string String
     */
    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;

    }

    public void setDefaultProviderManagerInfo(ProviderManagerInfo
                                              defaultProviderManagerInfo) {
        BaseSPIManager.getDefaultApplicationContext().getServiceProviderManager().setDefaultProviderManagerInfo(defaultProviderManagerInfo);
//        this.defaultProviderManagerInfo = defaultProviderManagerInfo;
    }

    public void addLoginModuleInfo(LoginModuleInfo loginModuleInfo)
    {
        loginModuleInfo.setApplicationInfo(this);
        this.loginModuleIndexs.put(loginModuleInfo.getName(),loginModuleInfo);
        this.loginModules.addLoginModuleInfo(loginModuleInfo);
    }

    public LoginModuleInfo getLoginModuleInfo(String name)
    {
        return (LoginModuleInfo)this.loginModuleIndexs.get(name);
    }

    public LoginModuleInfoQueue getLoginModuleInfos()
    {
        return this.loginModules;
    }

    public void addNotifiableInfo(NotifiableInfo notifiableInfo)
    {
        notifiableInfo.setApplicationInfo(this);
        this.notifiableInfos.put(notifiableInfo.getType(),notifiableInfo);
    }

    public NotifiableInfo getNotifiableInfo(String type)
    {
        return (NotifiableInfo)this.notifiableInfos.get(type);
    }

    /**
     * 添加系统属性
     * @param name String
     * @param value String
     */
    public void addProperty(String name,String value)
    {
        this.commonProperties.put(name,value);
    }

    /**
     * 获取系统属性
     * @param name String
     * @return String
     */
    public String getProperty(String name)
    {
        return (String)commonProperties.get(name);
    }

    /**
     * setDefaultAuthorTableInfo
     *
     * @param authorTableInfo AuthorTableInfo
     */
    public void setDefaultAuthorTableInfo(AuthorTableInfo authorTableInfo) {
        defaultAuthorTableInfo = authorTableInfo;
    }

    public void addResources(Resources resources)
    {
        resources.setApplicationInfo(this);
        this.resourcsIndexByModule.put(resources.getModuleName(),resources);
    }



    /**
     * setDefaultPermissionrolemap
     *
     * @param permissionRoleMapInfo PermissionRoleMapInfo
     */
    public void setDefaultPermissionrolemap(PermissionRoleMapInfo
                                            permissionRoleMapInfo) {
        this.defaultPermissionrolemapinfo = permissionRoleMapInfo;
    }

    public AuthorTableInfo getAuthorTableInfo(String moduleName)
    {
        return (AuthorTableInfo)this.authorTableInfoIdxs.get(moduleName);
    }


	public void addManagerImport(ManagerImport mi) {
		this.managerimports.add(mi);
		
	}


	public List getManagerimports() {
		return managerimports;
	}
    
    
    
    


}
