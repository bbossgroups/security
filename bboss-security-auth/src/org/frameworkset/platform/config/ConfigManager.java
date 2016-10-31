package org.frameworkset.platform.config;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.frameworkset.event.EventHandle;
import org.frameworkset.event.Notifiable;
import org.frameworkset.event.NotifiableFactory;
import org.frameworkset.platform.config.model.ApplicationInfo;
import org.frameworkset.platform.config.model.AuthorTableInfo;
import org.frameworkset.platform.config.model.Context;
import org.frameworkset.platform.config.model.DataSourceConfig;
import org.frameworkset.platform.config.model.DatabaseConfig;
import org.frameworkset.platform.config.model.LDAPConfig;
import org.frameworkset.platform.config.model.NotifiableInfo;
import org.frameworkset.platform.config.model.Operation;
import org.frameworkset.platform.config.model.OperationGroup;
import org.frameworkset.platform.config.model.OperationQueue;
import org.frameworkset.platform.config.model.PermissionRoleMapInfo;
import org.frameworkset.platform.config.model.ResourceInfo;
import org.frameworkset.platform.config.model.Resources;
import org.frameworkset.platform.config.model.ScheduleServiceInfo;
import org.frameworkset.platform.config.model.TaskServiceInfo;
import org.frameworkset.platform.log.DefaultLogManager;
import org.frameworkset.platform.resource.Resource;
import org.frameworkset.platform.resource.ResourceManagerInf;
import org.frameworkset.platform.security.AuthorResource;
import org.frameworkset.platform.security.DefaultPermissionModule;
import org.frameworkset.platform.security.PermissionModule;
import org.frameworkset.platform.security.authorization.impl.AppSecurityCollaborator;
import org.frameworkset.platform.security.authorization.impl.BaseAuthorizationTable;
import org.frameworkset.platform.security.authorization.impl.PermissionRoleMap;
import org.frameworkset.platform.security.authorization.impl.PermissionToken;
import org.frameworkset.platform.security.authorization.impl.PermissionTokenMap;
import org.frameworkset.platform.security.authorization.impl.ResourceToken;
import org.frameworkset.platform.security.context.AccessContext;
import org.frameworkset.platform.util.LogManagerInf;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.frameworkset.spi.assemble.ProviderInfoQueue;
import org.frameworkset.spi.assemble.ProviderManagerInfo;
import org.frameworkset.spi.assemble.SecurityProviderInfo;
import org.frameworkset.web.servlet.support.WebApplicationContextUtils;

import com.frameworkset.spi.assemble.CurrentlyInCreationException;
import com.frameworkset.util.ResourceInitial;
import com.frameworkset.util.StringUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class ConfigManager implements ResourceInitial {
    private static Logger log = Logger.getLogger(ConfigManager.class);
    private static ConfigManager instance;    
    private boolean inited = false;
    private ResourceManagerInf resourceManager;
    private PermissionModule permissionModule;
    private String _permissionModule;
    private LogManagerInf logManager;
    private static String defaultGloablConfigFile = "conf/manager-provider.xml";
    private String gloablConfigFile = defaultGloablConfigFile;
    private BaseApplicationContext gloablConfigIOCContext;
    
    public static void destroy()
    {
    	if(instance != null)
    	{
    		instance._destroy();
    		instance = null;
    	}
    }
    
  
    public synchronized void init()
    { 
    	if(inited )
    		return;
    	inited = true;
    	
    	 try {
    		 log.debug("装载系统配置文件config-manager.xml.....开始");
    		 loadConfiguration(null);
    		 
    		 startSystems();
    		
//    		 String userNamelength = ConfigManager.getInstance().getConfigValue("userNameLength");
    		
    		 log.debug("装载系统配置文件config-manager.xml.....结束");
    		 
         } catch (Exception ex) {
        	 ex.printStackTrace();
        	 log.debug("装载系统配置文件失败：" + ex.getMessage(),ex);
         }
    }
    private ConfigManager() {
    	
    }
    private void initGloablConfigIOCContext()
    {
    	gloablConfigIOCContext = DefaultApplicationContext.getApplicationContext(gloablConfigFile);
    }
    
    
    
    private void shutdownsystems()
    {
    	if(!inited)
    		return;
    	for(int i = 0; i < this.getSystemInits().size(); i ++)
    	{
    		 SystemInit init = (SystemInit)this.systemInits.get(i);
    		 try {
				init.destroy();
			} catch (Throwable e) {
				log.debug("执行系统初始化程序失败："  + e.getMessage(),e);
			}
    	}
    }
    private void startSystems()
    {
    	for(int i = 0; i < this.getSystemInits().size(); i ++)
    	{
    		 SystemInit init = (SystemInit)this.systemInits.get(i);
    		 try {
				init.init();
			} catch (InitException e) {
				log.debug("执行系统初始化程序失败："  + e.getMessage(),e);
			}
    	}
    }

    public static ConfigManager getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new ConfigManager();
        instance.init();
        return instance;
    }

    /**根据应用和应用模块建立的安全信息索引*/
    private Map<String,ApplicationInfo> ApplicationInfos;

    private ApplicationInfo defaultApplicationInfo;

    private DataSourceConfig dataSourceConfig;

    private boolean sso = false;
    private String ssoDomain = "yourco.com";
    private boolean securityenabled = false;
    private boolean securitycookieenabled = false;
    private String dictionary = "com.frameworkset.dictionary.ProfessionDataManager";


    private Context context = new Context("bspf","console");
    
    private TaskServiceInfo taskServiceInfo= null;
    
    //private CommunicationInfo communicationInfo;
    
    /**
     * 系统初始化程序列表.
     * List<SystemInit>
     */
    private List systemInits = new ArrayList();
    
    
    private String menus ;
    
    public String getMenus() {
		return menus;
	}


	public void setMenus(String menus) {
		this.menus = menus;
	}


	public DatabaseConfig getDbConfig(String dbType) {
        return dataSourceConfig.getDbConfig(dbType);
    }

    public LDAPConfig getLDAPConfig() {
        return dataSourceConfig.getDefaultLDAPConfig();
    }

    public DatabaseConfig getDbConfig() {
        return dataSourceConfig.getDefaultDatabaseConfig();
    }

    public LDAPConfig getLDAPConfig(String ldapType) {
        return dataSourceConfig.getLDAPConfig(ldapType);
    }


    /**
     * 系统信息配置文件
     */
    private String defaultConfigFile =
        "config-manager.xml";


    private File configure;

    /** Load DataSource info from XML and create a Service for each entry set. */

    public void loadConfiguration(String configFile) throws Exception {

        if (configFile == null) {
            configFile = defaultConfigFile;
        }
        // first try XML
        try {
            parseXML(configFile);
//            //监听服务配置文件内容变化
//            if (configure != null && configure.exists()) {
//                DaemonThread listen = new DaemonThread(configure,
//                    instance);
//                listen.start();
//            }
        } catch (Exception e) {

            log.error("\n** ERROR: Unable to parse XML file " +
                      configFile + "，请检查该文件是否存在: ", e);
        }

    }

   
    private void parseXML(String configFile) throws Exception {
        /* CHANGED TO USE JAXP */
       
        configure = new File(configFile);
        URL confURL = ConfigManager.class.getClassLoader().getResource(
            configFile);
        if (confURL == null) {
            ConfigManager.class.getClassLoader().getResource("/" +
                configFile);
        }
  
        if (confURL == null) {
            getTCL().getResource(configFile);
        }
        if (confURL == null) {
            getTCL().getResource("/" + configFile);
        }
        if (confURL == null) {
            confURL = ClassLoader.getSystemResource(configFile);
        }
        if (confURL == null) {
            confURL = ClassLoader.getSystemResource("/" + configFile);
        }
        String url = "";
        if (confURL == null) {
            url = System.getProperty("user.dir");

            url += "/" + configFile;
        } else {
            url = confURL.toString();
        }
//        String configRealpath = confURL.getPath();
        ConfigParser handler = new ConfigParser(url );
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        SAXParser parser = factory.newSAXParser();
        //System.out.println("configRealpath:"+configRealpath);
        parser.parse(confURL.openStream(), handler);
        this.defaultApplicationInfo = handler.getDefaultApplicationInfo();
        this.ApplicationInfos = handler.getApplicationInfos();
        this.dataSourceConfig = handler.getDataSourceConfig();
        this.sso = handler.isSso();
        this.securityenabled = handler.isSecurityenabled();
        this.ssoDomain = handler.getSsoDomain();
//        this.context = handler.getContext();
        this.securitycookieenabled = handler.isSecuritycookieenabled();
        
        this.taskServiceInfo = handler.getTaskServiceInfo();
        this.dictionary = handler.getDictionary();
//        this.communicationInfo = handler.getCommunicationInfo();
        this.systemInits = handler.getSystemInits();
        String _permissionModule = handler.getPermissionModule();
        
        if(StringUtil.isNotEmpty(_permissionModule))
        {
        	this._permissionModule = _permissionModule;
        	
        }



    	  String logmanager = handler.getLogmanager();
    	  if(StringUtil.isNotEmpty(logmanager))
          {
          	Class cl = Class.forName(logmanager);
          	try {
  				this.logManager = (LogManagerInf) cl.newInstance();
  			} catch (Exception e) {
  				logManager = new DefaultLogManager();
  			}
          }
          String _config = handler.getConfig();
          if(StringUtil.isNotEmpty(_config))
          {
          	this.gloablConfigFile = _config;
          }
          initGloablConfigIOCContext();
          String _menus = handler.getMenus();
          if(StringUtil.isNotEmpty(_menus))
          {
          	this.menus = _menus;
          }
          boolean enablejobfunction = ConfigManager.getInstance().getConfigBooleanValue("enablejobfunction",true);
          
        if(!enablejobfunction)
        	this.removeResourceInfoByType("job");
        buildPermissionTokenMap();

    }
   
    
    private void  buildResourcePermissionTokenMap(String appName,String moduleName,ResourceInfo resource )
    {	
    	PermissionTokenMap permissionTokenMap = AppSecurityCollaborator.getInstance().getPermissionTokenMap();
    	OperationQueue globalOperationQueue = resource.getGlobalOperationQueue();
    	String resourceType = resource.getId();
    	String region = appName+"_"+moduleName;
    	if(globalOperationQueue != null && globalOperationQueue.size() > 0)
    	{
    		String gid = resource.getGlobalresourceid();
    		for(int i = 0; i < globalOperationQueue.size(); i ++)
    		{
    			Operation op = globalOperationQueue.getOperation(i);
    			AuthorResource ar = op.getAuthorResource();
    			if(ar != null)
    			{
    				List<ResourceToken> ars = ar.getAuthorResources();
    				if(ars != null)
    				{
	    				for(int j = 0;  j < ars.size(); j ++)
	    				{
	    					PermissionToken token = new PermissionToken(resourceType, gid,
	    							op.getId());
	    					permissionTokenMap.addPermissionToken(ars.get(j),region, token);
	    				}
    				}
    			}
    		}
    		
    	}
    	if(!resource.isAuto())//可维护资源
    	{
    		
    		OperationQueue operationQueue = resource.getOperationQueue();
    		List<Resource> dbres = null;
    		boolean query = false;
    		if(operationQueue != null && operationQueue.size() > 0)
    		{ 
    			for(int i = 0; i < operationQueue.size(); i ++)
    			{
    				Operation op = operationQueue.getOperation(i);
        			AuthorResource ar = op.getAuthorResource();
        			if(ar != null)
        			{
        				List<ResourceToken> ars = ar.getAuthorResources();
        				
        				for(int j = 0; ars != null && j < ars.size(); j ++)
        				{
        					ResourceToken rt = ars.get(j);
        					if(rt.useResourceAuthCode())
        					{
        						PermissionToken token = new PermissionToken(resourceType, 
            							op.getId());
            					permissionTokenMap.addPermissionToken(ars.get(j),region, token);
        					}
        					else
        					{
        						if(!query)
        						{
        							query = true;
	        			    		 
	        						try {
	        							dbres = resourceManager.getResourcesByType(resourceType);
	        							
	        						} catch (Exception e) {
	        							// TODO Auto-generated catch block
	        							e.printStackTrace();
	        						}
	        						
        						}
        						if(dbres == null || dbres.size() == 0)
        							continue;
	        					for(int k = 0; dbres != null && k < dbres.size(); k ++)
	        					{
	        						Resource dr = dbres.get(k);
	        						PermissionToken token = new PermissionToken(resourceType, dr.getTitle(),
	            							op.getId());
	            					permissionTokenMap.addPermissionToken(ars.get(j),region, token);
	        					}
        					}
        				}
        			}
    			}
    		}
    	}
    }
    private void  buildModulePermissionTokenMap(String appName,String moduleName,Resources resources )
    {	
    	if(resources == null)
    		return ;
    	ResourceInfoQueue rq = resources.getResourceQueue();
    	for(int i = 0; rq != null && i < rq.size(); i ++)
    	{
    		
    		ResourceInfo res = rq.getResourceInfo(i);
    		buildResourcePermissionTokenMap(appName, moduleName, res );
    	}
		
		
    }
    
    private void  buildAppPermissionTokenMap(String appName,ApplicationInfo applicationInfo )
    {
    	if(applicationInfo == null)
    		return;
    	Map<String,Resources> aa = applicationInfo.getResourcsIndexByModule();
    	if(aa == null)
    		return;
    	Iterator<Map.Entry<String,Resources>> entrySet = aa.entrySet().iterator();
		while(entrySet.hasNext())
		{
			Map.Entry<String,Resources> entry = entrySet.next();
			Resources item = entry.getValue();
			String moduleName = entry.getKey();
			buildModulePermissionTokenMap( appName, moduleName, item );
		}
		
    }
    private PermissionTokenMap buildPermissionTokenMap()
	{
		PermissionTokenMap permissionTokenMap = AppSecurityCollaborator.getInstance().getPermissionTokenMap();
	
		if(this.ApplicationInfos != null && this.ApplicationInfos.size() > 0)
		{
//			permissionTokenMap.resetPermissionByRegion("column", this.getSystemid());
			Iterator<Map.Entry<String,ApplicationInfo>> entrySet = ApplicationInfos.entrySet().iterator();
			while(entrySet.hasNext())
			{
				Map.Entry<String,ApplicationInfo> entry = entrySet.next();
				ApplicationInfo item = entry.getValue();
				String appName = entry.getKey();
				buildAppPermissionTokenMap( appName, item );
			}
				
		}
		return permissionTokenMap ;
	}

    
	public static ClassLoader getTCL() throws IllegalAccessException,
        InvocationTargetException {
	    
        Method method = null;
        try {
            method = (java.lang.Thread.class).getMethod("getContextClassLoader", null);
        } catch (NoSuchMethodException e) {
            return null;
        }
        return (ClassLoader) method.invoke(Thread.currentThread(), null);
    }


    public static void main(String[] args) {
        ConfigManager securityconfig = new ConfigManager();
    }

    /**
     * 获取特定应用的服务提供者管理索引
     * @param applicationName String 应用名称
     * @param moduleName String 应用模块名称
     * @return Map
     */
    public Map getProviderManagerInfos(String applicationName) {
        return this.getApplicationInfo(applicationName).
            getManagerInfos();
    }

    public Map getApplicationInfos() {
        return ApplicationInfos;
    }

    public ApplicationInfo getApplicationInfo(String appName) {
//        return (ApplicationInfo) getApplicationInfos().get(appName + "$$" +
//                moduleName);
    	if(getApplicationInfos() != null)
    		return (ApplicationInfo) getApplicationInfos().get(appName);
    	return null;
    }


    /**
     * 获取缺省系统应用web模块的安全配置信息
     * @return ApplicationInfo
     */
    public ApplicationInfo getDefaultApplicationInfo() {
        return defaultApplicationInfo;
    }
    
    public String getDefaultLoginpage()
    {
    	return this.getDefaultApplicationInfo().getLoginpage();
    }

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public String getSsoDomain() {
        return ssoDomain;
    }

    public boolean isSecurityenabled() {
        return securityenabled;
    }

    /**
     * 获取缺省系统应用web模块是否允许单点登录
     * @return boolean
     */
    public boolean isSSO() {
        //return this.getDefaultApplicationInfo().isSso();
        return this.sso;
    }

    /**
     * 获取缓冲机制
     * @return String
     */
    public String getCacheType() {

        return this.getDefaultApplicationInfo().getCacheType();
    }

    /**
     * 获取特定应用模块的缓冲机制
     * @param appName String
     * @param module String
     * @return String
     */

    public String getCacheType(String appName) {
        return this.getApplicationInfo(appName).getCacheType();
    }

    /**
     * 获取缺省系统应用web模块缓冲机制是否启用
     * @return boolean
     */
    public boolean isCachable() {
        return this.getDefaultApplicationInfo().isCachable();
    }


    /**
     * 获取缺省系统应用web模块安全是否启用
     * @return boolean
     */
    public boolean securityEnabled() {
//        return this.getDefaultApplicationInfo().isSecurityenabled();
        return this.securityenabled;
    }

    /**
     * 获取缺省系统应用web模块的认证失败跳转页面
     * @return String
     */
    public String getAuthorfailedDirect() {
        return this.getApplicationInfo(this.getAppName()).getAuthorfaileddirect();
    }

    /**
     * 获取缺省的登录页面名称，用于登录失败时的跳转页面
     * @return String
     */
    public String getLoginPage() {

        return this.getApplicationInfo(this.getAppName()).getLoginpage();
    }

    /**
     * 获取默认的应用名称
     * @return String
     */
    public String getAppName() {
        return this.context.getApplication();
    }

    /**
     * 获取默认的系统web模块名称
     * @return String
     */
    public String getModuleName() {
        return this.context.getModuleName();
    }

    /*******************************************************
     * 以下方法根据不同的条件获取特定的AuthorTableInfo对象实例   *
     *******************************************************/

    /**
     * 获取缺省系统web模块中权限表信息
     * @return AuthorTableInfo
     */
    public AuthorTableInfo getAuthorTableInfo() {

        return getApplicationInfo(this.getAppName()).getAuthorTableInfo(this.
            getModuleName());
    }

    /**
     * 获取应用系统appName中的web模块moduleName中权限表信息
     * @return AuthorTableInfo
     */

    public AuthorTableInfo getAuthorTableInfo(String appName, String moduleName) {
        return this.getApplicationInfo(appName).getAuthorTableInfo(moduleName);
    }

    /**
     * 获取缺省系统web模块中权限表信息
     * @return AuthorTableInfo
     */
    public BaseAuthorizationTable getAuthorTable() {
        return this.getAuthorTableInfo().getAuthorizationTable();

    }

    /**
     * 获取应用系统appName中的web模块moduleName中权限表信息
     * @return AuthorTableInfo
     */

    public BaseAuthorizationTable getAuthorTable(String appName,
                                                 String moduleName) {
        return this.getAuthorTableInfo(appName, moduleName).
            getAuthorizationTable();
    }


    /************************************************************
     * 以下方法根据不同的条件获取特定的PermissionRoleMapInfo对象实例   *
     ************************************************************/

    /**
     * 获取缺省系统中缺省的web模块中的资源操作许可表
     * @return PermissionRoleMapInfo
     */
    public PermissionRoleMapInfo getPermissionRoleMapInfo() {
        return this.getDefaultApplicationInfo().getPermissionrolemapInfo();
    }

    public PermissionRoleMapInfo getPermissionRoleMapInfo(String appName,
        String moduleName) {

        return this.getApplicationInfo(appName).getPermissionRoleMapInfo(
            moduleName);
    }

    public PermissionRoleMap getPermissionRoleMap() {
        return this.getPermissionRoleMapInfo().getPermissionRoleMap();
    }

    public PermissionRoleMap getPermissionRoleMap(String appName,
                                                  String moduleName) {
        return this.getPermissionRoleMapInfo(appName, moduleName).
            getPermissionRoleMap();
    }


    /*******************************************************
     * 以下方法根据不同的条件获取特定的providerManager对象实例   *
     *******************************************************/


    public ProviderManagerInfo getProviderManagerInfo(String
        providerManagerType) {
        return this.getDefaultApplicationInfo().getProviderManagerInfo(
            providerManagerType);
    }

    public ProviderManagerInfo getProviderManagerInfo(String appName,
        String providerManagerType) {
        return this.getApplicationInfo(appName).getProviderManagerInfo(
            providerManagerType);
    }

    /*******************************************************
     * 以下方法根据不同的条件获取特定的provider信息实例           *
     *******************************************************/

    public SecurityProviderInfo getSecurityProviderInfo(String
        providerManagerType) {
        return this.getDefaultApplicationInfo().getProviderManagerInfo(
            providerManagerType).getDefaulProviderInfo();
    }


    public SecurityProviderInfo getSecurityProviderInfo(String
        providerManagerType,
        String providerType) {
        return this.getDefaultApplicationInfo().getProviderManagerInfo(
            providerManagerType).getSecurityProviderInfoByType(providerType);
    }


    public SecurityProviderInfo getSecurityProviderInfo(String appName,
        String providerManagerType,
        String providerType) {
        return this.getApplicationInfo(appName)
            .getProviderManagerInfo(providerManagerType)
            .getSecurityProviderInfoByType(providerType);
    }

    /**
     * 获取缺省的安全信息管理实现队列
     * @param providerManagerType String
     * @return ProviderInfoQueue
     */
    public ProviderInfoQueue getProviderInfoQueue(String providerManagerType) {
        return this.getDefaultApplicationInfo().getProviderManagerInfo(
            providerManagerType).getProviderInfoQueue();
    }

    public ProviderInfoQueue getProviderInfoQueue(String appName,
                                                  String providerManagerType) {
        return this.getApplicationInfo(appName)
            .getProviderManagerInfo(providerManagerType)
            .getProviderInfoQueue();
    }


    /*******************************************************
     * 以下方法根据不同的条件获取特定的provider对象实例           *
     *******************************************************/

    /**
     * 获取系统缺省的类型为managerType的缺省提供者
     * @param type String
     * @return Provider
     */
    public Object getProviderInstance(String providerManagerType) {
        try {
			return this.getSecurityProviderInfo(providerManagerType).getProvider();
		} catch (CurrentlyInCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    /**
     * 获取缺省系统web模块中特定资源类型针对的特定数据源的接口实现
     * @param providerManagerType String 对应资源、用户、角色、用户组、机构等类型
     * @param providerType String 相应providerManagerType类型针对不数据源有不同的实现，providerType用来标识不同的数据源
     * @return Provider
     */
    public Object getProviderInstance(String providerManagerType,
                                        String providerType) {
        try {
			return getSecurityProviderInfo(providerManagerType, providerType).
			    getProvider();
		} catch (CurrentlyInCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    /**
     * 获取系统应用appName中web模块moduleName中的特定资源类型针对的特定数据源的接口实现
     * @param appName String
     * @param moduleName String
     * @param providerManagerType String
     * @param providerType String
     * @return Provider
     */
    public Object getProviderInstance(String appName,
                                        String providerManagerType,
                                        String providerType) {
        try {
			return getSecurityProviderInfo(appName, providerManagerType,
			                               providerType).getProvider();
		} catch (CurrentlyInCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }


    


//    public List getProviderManagerTypes() {
//        return this.getDefaultApplicationInfo().getProviderManagerTypes();
//    }
//
//    public List getProviderManagerTypes(String appName) {
//        return this.getApplicationInfo(appName).
//            getProviderManagerTypes();
//    }

    public NotifiableFactory getNotifiableFactory(AccessContext context,
                                                  String factoryType) {
    	if(context != null)
    	{
	        return getApplicationInfo(context.getAppName()).getNotifiableInfo(
	            factoryType).
	            getNotifiableFactory();
    	}
    	else
    	{
    		return getApplicationInfo(this.getAppName()).getNotifiableInfo(
    	            factoryType).
    	            getNotifiableFactory();
    	}
    }

    public NotifiableInfo getNotifiableInfo(String type) {
        return getDefaultApplicationInfo().getNotifiableInfo(type);
    }

    /**
     *
     * @param context 系统环境上下文，
     * @param factoryType 工厂类型
     * @param notifiableType 事件激发器类型
     * @return Notifiable
     */
    public Notifiable getNotifiable(AccessContext context, String factoryType,
                                    int notifiableType) {
        return getNotifiableFactory(context,
            factoryType).getNotifiable();
    }
    
    /**
    *
    * @param context 系统环境上下文，
    * @param factoryType 工厂类型
    * @param notifiableType 事件激发器类型
    * @return Notifiable
    * @deprecated 
    */
   public static Notifiable getNotifiable() {
       return NotifiableFactory.getNotifiable();
   }

    public String getConfigValue(String name) {
//        if (this.getAppName() == null) {
//            return this.getDefaultApplicationInfo().getProperty(name);
//        } else {
//            return this.getApplicationInfo(this.getAppName()).getProperty(name);
//        }
        
        return gloablConfigIOCContext.getProperty(name);

    }

    public int getConfigIntValue(String name) throws ConfigException {
//        String value = this.getConfigValue(name);
//        if (value == null || value.trim().equals("")) {
//            throw new ConfigException("配置文件config-manager.xml没有指定属性[" + name +
//                                      "]或者属性[" + name + "]不是整型数据！");
//        }
//        try {
//            int ivalue = Integer.parseInt(value);
//            return ivalue;
//        } catch (Exception e) {
//            throw new ConfigException("属性[" + name + "]不是整型数据！");
//        }
        return gloablConfigIOCContext.getIntProperty(name);
    }

    public boolean getConfigBooleanValue(String name) throws ConfigException {
//        String value = this.getConfigValue(name);
//        if (value == null || (!value.trim().equalsIgnoreCase("true")
//                              && !value.trim().equalsIgnoreCase("false"))) {
//            throw new ConfigException("配置文件config-manager.xml没有指定属性[" + name +
//                                      "]或者属性[" + name + "]不是布尔型数据！");
//        }
//        try {
//            boolean ivalue = new Boolean(value.toLowerCase().trim()).
//                             booleanValue();
//            return ivalue;
//
//        } catch (Exception e) {
//            throw new ConfigException("配置文件config-manager.xml没有指定属性[" + name +
//                                      "]或者属性[" + name + "]不是布尔型数据！");
//        }
        return gloablConfigIOCContext.getBooleanProperty(name);
    }

    public String getConfigValue(String name, String defaultValue) {
//        String value = this.getDefaultApplicationInfo().getProperty(name);
//        if (value == null) {
//            log.debug("配置文件config-manager.xml没有指定属性[" + name + "]！返回缺省值" +
//                      defaultValue);
//            return defaultValue;
//        }
//        return value;
        return gloablConfigIOCContext.getProperty(name, defaultValue);
    }

    public int getConfigIntValue(String name, int defaultValue) {
//        String value = this.getConfigValue(name);
//        if (value == null || value.trim().equals("")) {
//            log.debug("配置文件config-manager.xml没有指定属性[" + name + "]或者属性[" + name +
//                      "]不是整型数据！返回缺省值" + defaultValue);
//            return defaultValue; //throw new ConfigException("配置文件config-manager.xml没有指定属性[" + name + "]或者属性[" + name + "]不是整型数据！");
//        }
//        try {
//            int ivalue = Integer.parseInt(value);
//            return ivalue;
//        } catch (Exception e) {
//            log.debug("属性[" + name + "]不是整型数据！返回缺省值" + defaultValue);
//            return defaultValue; //throw new ConfigException("属性[" + name + "]不是整型数据！");
//        }
        return gloablConfigIOCContext.getIntProperty(name, defaultValue);
    }

    public boolean getConfigBooleanValue(String name, boolean defaultValue) {
//        String value = this.getConfigValue(name);
//        if (value == null || (!value.trim().equalsIgnoreCase("true")
//            && !value.trim().equalsIgnoreCase("false"))) {
//            log.debug("配置文件config-manager.xml没有指定属性[" + name +
//                      "]或者属性[" + name + "]不是布尔型数据！返回缺省值" + defaultValue);
//            return defaultValue;
//            //throw new ConfigException("配置文件config-manager.xml没有指定属性[" + name +
////                                      "]或者属性[" + name + "]不是布尔型数据！");
//        }
//        try {
//            boolean ivalue = Boolean.valueOf(value).booleanValue();
//            return ivalue;
//        } catch (Exception e) {
//            log.debug("配置文件config-manager.xml没有指定属性[" + name +
//                      "]或者属性[" + name + "]不是布尔型数据！返回缺省值" + defaultValue);
//            return defaultValue;
//            //throw new ConfigException("配置文件config-manager.xml没有指定属性[" + name + "]或者属性[" + name + "]不是布尔型数据！" );
//        }
        return gloablConfigIOCContext.getBooleanProperty(name, defaultValue);
    }

    public ProviderManagerInfo getDefaultProviderManagerInfo() {
        return getDefaultApplicationInfo().getDefaultProviderManagerInfo();
    }

    public ProviderManagerInfo getDefaultProviderManagerInfo(String appName) {
        return getApplicationInfo(appName).getDefaultProviderManagerInfo();
    }
    
    public ResourceInfo getResourceInfoByTypeOfModule(String module,
            String type) throws ConfigException {
            return this.getDefaultApplicationInfo().getResourceInfoById(module,
                type);
        }

    public ResourceInfo getResourceInfo(String appName,
                                        String moduleName, String type)  
    	throws ConfigException {
    	ResourceInfo resourceInfo = this.getApplicationInfo(appName).getResourceInfoById(moduleName,
                type);
    	if(resourceInfo == null)
    	{
    		log.debug(new StringBuilder().append("获取")
    				.append("类型为[" )
    				.append( type )
    				.append( "]的资源信息为空，请检查config-manager.xml文件中是否配置了id为[" )
    				.append( type )
    				.append( "]资源类别！").toString());
    	}
        return resourceInfo;
    }

        public ResourceInfo getResourceInfo(String module) {
            return this.getDefaultApplicationInfo().getDefaultResourceInfo(module);
        }

        public ResourceInfo getResourceInfoByType(String resourcetype) throws ConfigException{
        	ResourceInfo ret = null;
        	ret = this.getApplicationInfo(getAppName()).getResourceInfoById(this.
                    getModuleName(), resourcetype);
        	if(ret == null)
        	{
        		log.debug(new StringBuilder().append("获取")
        				.append("类型为[" )
        				.append( resourcetype )
        				.append( "]的资源信息为空，请检查config-manager.xml文件中是否配置了id为[" )
        				.append( resourcetype )
        				.append( "]资源类别！").toString());
//        		throw new ConfigException("获取类型为[" + resourcetype + "]的资源信息异常[java.lang.NullpointException]，请检查config-manager.xml文件中是否配置了id为[" + resourcetype + "]资源类别！");
        	}
            return ret;
        }
        
        public ResourceInfo removeResourceInfoByType(String resourcetype) throws ConfigException{
        	ResourceInfo ret = null;
        	ret = this.getApplicationInfo(getAppName()).removeResourceInfoById(this.
                    getModuleName(), resourcetype);
        	if(ret == null)
        	{
        		log.debug(new StringBuilder().append("获取")
        				.append("类型为[" )
        				.append( resourcetype )
        				.append( "]资源信息为空，请检查config-manager.xml文件中是否配置了id为[" )
        				.append( resourcetype )
        				.append( "]资源类别！").toString());
        		}
            return ret;
        }
        
        


        /**
         * 获取特定应用特定模块缺省的资源信息
         * @param appName String
         * @param moduleName String
         * @return ResourceInfo
         */
        public ResourceInfo getResourceInfo(String appName, String moduleName) {
            return this.getApplicationInfo(appName).getDefaultResourceInfo(
                moduleName);
        }


        public ResourceInfo getResourceInfo() {
            return this.getApplicationInfo(getAppName()).getDefaultResourceInfo(
                getModuleName());
        }

        /**
         * 获取系统缺省应用模块的资源信息队列
         * @param module String
         * @return ResourceInfoQueue
         */
        public ResourceInfoQueue getResourceInfoQueue(String module) {
            return this.getDefaultApplicationInfo().getResourceInfoQueue(module);
        }

        public ResourceInfoQueue getResourceInfoQueue() {
            return this.getResourceInfoQueue(this.getAppName(), this.getModuleName());
        }


        public OperationGroup getOperationGroup(String appName, String moduleName) {
            return this.getApplicationInfo(appName).getDefaultResourceInfo(
                moduleName).getOperationGroup();
        }

        /**
         * 获取缺省类型资源对应的操作组
         * @return OperationGroup
         */
        public OperationGroup getOperationGroup() {
            return this.getApplicationInfo(getAppName()).getDefaultResourceInfo(
                getModuleName()).getOperationGroup();
        }

        public OperationGroup getOperationGroup(String resourceType) {
            return this.getApplicationInfo(getAppName()).getResourceInfoById(
                getModuleName(), resourceType).getOperationGroup();
        }
        
        
        

        /**
         * 获取缺省的资源操作组
         * @return OperationGroup
         */
        public OperationGroup getDefaultOperationGroup() {
            return this.getApplicationInfo(getAppName()).getResources(getModuleName()).
                getDefaultOperationGroup();
        }

        /**
         * 获取特定资源类型的操作队列
         * @param resourceType String
         * @return OperationQueue
         */
        public OperationQueue getOperationQueue(String resourceType) throws ConfigException{
            return getResourceInfoByType(resourceType).getOperationQueue();
        }
        
        /**
         * 获取特定类型资源的全局操作项信息
         * @param resourceType String
         * @param operid String
         * @return Operation
         */
        public Operation getGlobalOperation(String resourceType, String operid) throws ConfigException{
            return getResourceInfoByType(resourceType).getGlobalOperationByid(operid);
        }
        /**
         * 获取特定资源类型的全局操作队列
         * @param resourceType String
         * @return OperationQueue
         */
        public OperationQueue getGlobalOperationQueue(String resourceType) throws ConfigException{
            return getResourceInfoByType(resourceType).getGlobalOperationQueue();
        }
        
        /**
         * 获取特定资源类型的全局操作队列
         * @param resourceType String
         * @return OperationQueue
         */
        public String getGlobalResourceid(String resourceType) throws ConfigException{
            return getResourceInfoByType(resourceType).getGlobalresourceid();
        }


        /**
         * 获取特定类型资源的操作项信息
         * @param resourceType String
         * @param operid String
         * @return Operation
         */
        public Operation getOperation(String resourceType, String operid) throws ConfigException{
            return getResourceInfoByType(resourceType).getOperationByid(operid);
        }
        
        
        


        /**
         * 获取特定应用模块的资源信息队列
         * @param appName String
         * @param moduleName String
         * @return ResourceInfoQueue
         */
        public ResourceInfoQueue getResourceInfoQueue(String appName,
                                                      String moduleName) {
            return this.getApplicationInfo(appName).getResourceInfoQueue(moduleName);
        }
        
        public Resources getResources()
        {
            return this.getApplicationInfo(this.getAppName()).getResources(this.getModuleName());
        }


    public void reinit() {
    }


    /**
     * @return Returns the securitycookieenabled.
     */
    public boolean isSecuritycookieenabled() {
        return securitycookieenabled;
    }

	public TaskServiceInfo getTaskServiceInfo() {
		return taskServiceInfo;
	}

	public boolean isUseTaskservice() {
		return taskServiceInfo == null?false: this.taskServiceInfo.isUsed();
	}
	
	public List getScheduleServices()
	{
		return taskServiceInfo.getTaskServices();
	}
	
	public ScheduleServiceInfo getScheduleServiceInfo(String id)
	{
		return this.taskServiceInfo.getScheduleServiceInfo(id);
	}
	public String getDictionary()
	{
		return dictionary;
	}
	
//	/**
//	 * 获取系统邮件服务接口
//	 * @return
//	 */
//	public Mail getMailService()
//	{
//		if(this.communicationInfo == null)
//			return null;
//		return communicationInfo.getMailService();
//	}
	
//	/**
//	 * 获取系统短信发送服务接口
//	 * @return
//	 */
//	public SMS getSMSService()
//	{
//		if(this.communicationInfo == null)
//			return null;
//		return communicationInfo.getSMSService();
//	}

	public void _destroy() {

    	this.shutdownsystems();
    	this.systemInits = null;
    	this.ApplicationInfos = null;
    	this.context = null;
    	this.dataSourceConfig = null;
    	this.defaultApplicationInfo = null;
    	this.taskServiceInfo = null;
    	
//    	
		
	}

	public List getSystemInits() {
		return systemInits;
	}
	
	public static EventHandle getEventHandle()
	{
		return EventHandle.getInstance();
	}


	public PermissionModule getPermissionModule() {
		if(permissionModule != null)
			return permissionModule;
		synchronized(this)
		{
			if(permissionModule != null)
				return permissionModule;
			try {
	        	if(!_permissionModule.startsWith("mvc:"))
	        	{
		        	Class cl = Class.forName(_permissionModule);
		        	
						this.permissionModule = (PermissionModule) cl.newInstance();
					
	        	}
	        	else
	        	{
	        		BaseApplicationContext ioc = WebApplicationContextUtils.getWebApplicationContext();
	        		permissionModule = ioc.getTBeanObject(_permissionModule.substring(4), PermissionModule.class);
	        	}
	    	} catch (Exception e) {
				permissionModule = new DefaultPermissionModule();
			}
		}
		return permissionModule;
	}


	public ResourceManagerInf getResourceManager() {
		return resourceManager;
	}


	public LogManagerInf getLogManager() {
		return logManager;
	}

	
}
