package com.frameworkset.platform.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.frameworkset.spi.ApplicationContext;
import org.frameworkset.spi.assemble.InterceptorInfo;
import org.frameworkset.spi.assemble.ManagerImport;
import org.frameworkset.spi.assemble.Param;
import org.frameworkset.spi.assemble.ProviderManagerInfo;
import org.frameworkset.spi.assemble.Reference;
import org.frameworkset.spi.assemble.RollbackException;
import org.frameworkset.spi.assemble.SecurityProviderInfo;
import org.frameworkset.spi.assemble.SynchronizedMethod;
import org.frameworkset.spi.assemble.Transactions;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.frameworkset.platform.config.model.ApplicationInfo;
import com.frameworkset.platform.config.model.AuthorTableInfo;
import com.frameworkset.platform.config.model.Context;
import com.frameworkset.platform.config.model.DataSourceConfig;
import com.frameworkset.platform.config.model.DatabaseConfig;
import com.frameworkset.platform.config.model.ImportResource;
import com.frameworkset.platform.config.model.LDAPConfig;
import com.frameworkset.platform.config.model.LoginModuleInfo;
import com.frameworkset.platform.config.model.NotifiableInfo;
import com.frameworkset.platform.config.model.Operation;
import com.frameworkset.platform.config.model.OperationGroup;
import com.frameworkset.platform.config.model.PermissionRoleMapInfo;
import com.frameworkset.platform.config.model.PropertiesFile;
import com.frameworkset.platform.config.model.ResourceInfo;
import com.frameworkset.platform.config.model.Resources;
import com.frameworkset.platform.config.model.ScheduleServiceInfo;
import com.frameworkset.platform.config.model.SchedulejobInfo;
import com.frameworkset.platform.config.model.TaskServiceInfo;
import com.frameworkset.platform.resource.ExcludeResource;
import com.frameworkset.platform.resource.UNProtectedResource;
import com.frameworkset.platform.util.I18nXMLParser;
import com.frameworkset.util.StringUtil;

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
public class ConfigParser extends I18nXMLParser  {
    private static Logger log = Logger.getLogger(ConfigParser.class) ;
    private Stack traceStack;
    private StringBuffer currentValue;
    private Map<String,ApplicationInfo> applicationInfos;
    private ApplicationInfo defaultApplicationInfo;
    private DataSourceConfig dataSourceConfig;
    private boolean sso= false;
    private String ssoDomain = "yourco.com" ;
    private boolean securityenabled= false;
    private boolean securitycookieenabled = false;    
    
    private TaskServiceInfo taskServiceInfo= null;
//    private CommunicationInfo communicationInfo;
    
    private String file;

    private Context context;
    
    
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
    private void handleResourceFile(Resources resources,ImportResource ir )
    {
    	if(resources == null )
    		return ;

		if(ir == null )
			return;
		String configFile = ir.getResourceFile();
		if(configFile == null || configFile.trim().equals(""))
			return ;
		try
		{
    		
//    		File configure = new File(ir.getResourceFile());
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
//            String configRealpath = confURL.getPath();
            ResourceFileParser handler = new ResourceFileParser(configFile,resources);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            //System.out.println("configRealpath:"+configRealpath);
            parser.parse(confURL.openStream(), handler);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error("Load resource file["+ configFile + "] failed:",e);
		}


    	
    }
    
    
    private void handlePropertyFile(ApplicationInfo ApplicationInfo,String propertiesfile )
    {
    	if(ApplicationInfo == null )
    		return ;

		if(propertiesfile == null )
			return;
//		String configFile = propertiesfile.getResourceFile();
		if(propertiesfile == null || propertiesfile.trim().equals(""))
			return ;
		try
		{
    		
//    		File configure = new File(ir.getResourceFile());
            URL confURL = ConfigManager.class.getClassLoader().getResource(
            		propertiesfile);
            if (confURL == null) {
                ConfigManager.class.getClassLoader().getResource("/" +
                		propertiesfile);
            }
      
            if (confURL == null) {
            	getTCL().getResource(propertiesfile);
            }
            if (confURL == null) {
                getTCL().getResource("/" + propertiesfile);
            }
            if (confURL == null) {
                confURL = ClassLoader.getSystemResource(propertiesfile);
            }
            if (confURL == null) {
                confURL = ClassLoader.getSystemResource("/" + propertiesfile);
            }
            String url = "";
            if (confURL == null) {
                url = System.getProperty("user.dir");

                url += "/" + propertiesfile;
            } else {
                url = confURL.toString();
            }
//            String configRealpath = confURL.getPath();
            PropertiesFileParser handler = new PropertiesFileParser(propertiesfile,ApplicationInfo);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            //System.out.println("configRealpath:"+configRealpath);
            parser.parse(confURL.openStream(), handler);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error("Load resource file["+ propertiesfile + "] failed:",e);
		}


    	
    }
    
    class ResourceFileParser extends DefaultHandler
    {
    	private Stack traceStack = new Stack();
        private StringBuffer currentValue;
        private String file;
        private Resources resources;
    	public ResourceFileParser(String file,Resources resources)
    	{
    		this.resources = resources;
    		currentValue = new StringBuffer();
    		this.file = file;
    	}
    	
    	 public void startElement(String s1,String s2,String name,Attributes attributes) {
    	        currentValue.delete(0, currentValue.length());
    	        if(name.equals("resources"))
    	        {
    	        	traceStack.push(resources);
    	        }
    	        else if(name.equalsIgnoreCase("resourcefile"))//解析资源类型和操作文件
    	        {
    	        	Resources resources = (Resources)traceStack.peek();
    	        	
    	        	String resourceFile = attributes.getValue("src");
//    	        	ir.setResourceFile(resourceFile);
//    	        	resources.addImportResourceFile(ir);
    	        	if(resourceFile != null && resourceFile.trim().length() >0)
    	        	{
    	        		
    	        		ImportResource ir = new ImportResource();
    	        		String desc = attributes.getValue("desc");
    	        		ir.setDesc(desc);
    		        	ir.setResourceFile(resourceFile);
    		        	resources.addImportResourceFile(ir);
    		        	handleResourceFile(resources,ir );
    		        }
    	        }
    	        else if(name.equalsIgnoreCase("resource"))
    	        {
    	            Object obj = traceStack.peek();
    	            ResourceInfo resourceInfo = new ResourceInfo();
    	            resourceInfo.setId(attributes.getValue("id"));
    	            resourceInfo.setName(attributes.getValue("name"));
    	            resourceInfo.setLocaleNames(convertI18n(attributes, resourceInfo.getName(), resourceInfo.getId(), "resourceInfo"));
    	            resourceInfo.setResource(attributes.getValue("class"));
    	            resourceInfo.setAuto(StringUtil.getBoolean(attributes.getValue("auto"),false));
    	            resourceInfo.setUsed(StringUtil.getBoolean(attributes.getValue("used"),true));
    	            boolean isdefault = StringUtil.getBoolean(attributes.getValue("default"),true);
    	           
    	            resourceInfo.setStruction(StringUtil.replaceNull(attributes.getValue("struction"),"list"));
    	            resourceInfo.setSystem(StringUtil.replaceNull(attributes.getValue("system"),"module"));
    	            resourceInfo.setDefaultResourceInfo(isdefault);
    	            boolean maintaindata = StringUtil.getBoolean(attributes.getValue("maintaindata"),true);
    	            resourceInfo.setMaintaindata(maintaindata);
    	            String allowIfNoRequiredRole = attributes.getValue("allowIfNoRequiredRole");
    	            //如果设置了allowIfNoRequiredRole属性，修改defaultAllowIfNoRequiredRole标识为false，以便采用
    	            //屏蔽全局的allowIfNoRequiredRole属性，而采用为resource设置的allowIfNoRequiredRole属性配置
    	            if(allowIfNoRequiredRole != null)
    	            {
    	            	resourceInfo.setDefaultAllowIfNoRequiredRole(false);
    	            	resourceInfo.setAllowIfNoRequiredRole(StringUtil.getBoolean(allowIfNoRequiredRole,false));
    	            }
    	            
    	         
    	            if(obj instanceof Resources)
    	            {                
    	                Resources resources = (Resources)traceStack.peek();

    	                if(isdefault)
    	                    resources.setDefaultResourceInfo(resourceInfo);
    	                resources.addResourceInfo(resourceInfo);
    	            }
    	            else if(obj instanceof ResourceInfo)
    	            {
    	                ResourceInfo parent = (ResourceInfo)traceStack.peek();
    	                Resources resources = parent.getResources();
    	                resources.addNestingResourceInfo(resourceInfo);
    	                parent.addSubResourceInfo(resourceInfo);
    	            }
    	            traceStack.push(resourceInfo);
    	        }
    	        
    	        else if(name.equals("unprotected"))
    	        {
    	            ResourceInfo resourceInfo = (ResourceInfo)traceStack.peek();
    	            UNProtectedResource unProtectedResource = new UNProtectedResource();
    	            unProtectedResource.setResourceID(attributes.getValue("resourceid"));
    	            unProtectedResource.setResourceType(resourceInfo.getId());
    	            unProtectedResource.setResouceInfo(resourceInfo);
    	            resourceInfo.addUNProtectedResource(unProtectedResource);
    	            traceStack.push(unProtectedResource);
    	        }
    	        else if(name.equals("exclude"))
    	        {
    	            ResourceInfo resourceInfo = (ResourceInfo)traceStack.peek();
    	            ExcludeResource excludeResource = new ExcludeResource();
    	            excludeResource.setResourceID(attributes.getValue("resourceid"));
    	            excludeResource.setResourceType(resourceInfo.getId());
    	            excludeResource.setResouceInfo(resourceInfo);
    	            resourceInfo.addExcludeResource(excludeResource);
    	            traceStack.push(excludeResource);
    	        }

    	        else if(name.equals("operationgroup"))
    	        {
    	            ResourceInfo resourceInfo = (ResourceInfo)traceStack.peek();
    	            resourceInfo.setOperationGroupID(attributes.getValue("groupid"));
    	        }
    	        
    	        else if(name.equals("globaloperationgroup"))//指定资源的全局操作组
    	        {
    	        	ResourceInfo resourceInfo = (ResourceInfo)traceStack.peek();
    	            resourceInfo.setGlobalOperationGroupID(attributes.getValue("groupid"));
    	            resourceInfo.setGlobalresourceid(attributes.getValue("globalresourceid"));
    	        }
    	        else if(name.equals("operations"))
    	       {

    	       }

    	        else if(name.equals("group"))
    	        {
    	             Resources resources = (Resources)traceStack.peek();
    	             OperationGroup group = new OperationGroup();
    	             group.setGroupID(attributes.getValue("id"));
    	             group.setGroupName(attributes.getValue("name"));
    	             group.setLocaleNames(convertI18n(attributes, group.getGroupName(), group.getGroupID(), "OperationGroup"));
    	             resources.addOperationGroup(group);
    	             boolean d = StringUtil.getBoolean(attributes.getValue("default"),false);
    	             group.setDefaultable(d);
    	             if(d)
    	                 resources.setDefaultOperationGroup(group);
    	             traceStack.push(group) ;
    	        }

    	        else if(name.equals("operation"))
    	        {
    	        	Object temp = traceStack.peek();
    	        	
    	            
    	            if(temp instanceof OperationGroup)
    	            {
    	            	OperationGroup group = null;
    	            	group = (OperationGroup)temp;
    	            	Operation oper  = new Operation();
    	            	
    	                oper.setId(attributes.getValue("id"));
    	                oper.setName(attributes.getValue("name"));
    	                oper.setLocaleNames(convertI18n(attributes, oper.getName(), oper.getId(), "Operation"));
    	                oper.setPriority(StringUtil.replaceNull(attributes.getValue("priority"),"00"));
    	                oper.setManager(StringUtil.getBoolean(attributes.getValue("manager"),true));
    	                group.addOperation(oper);
    	                this.traceStack.push(oper);
    	            }
    	            else if(temp instanceof ExcludeResource)
    	            {
    	            	ExcludeResource exr = (ExcludeResource)temp;
    	            	Operation oper  = new Operation();
    	                oper.setId(attributes.getValue("id"));
    	                exr.addExcludeOperation(oper);
    	                
    	            }
    	            else if(temp instanceof UNProtectedResource)
    	            {
    	            	UNProtectedResource unp = (UNProtectedResource)temp;
    	            	Operation oper  = new Operation();
    	                oper.setId(attributes.getValue("id"));
    	                unp.addUnprotectedOperation(oper);
    	            }
    	            
    	        }
    	        else if(name.toLowerCase().equals("url"))
    	        {
    	        	this.currentValue.setLength(0);
    	        }
    	        else if(name.equals("description")){
    	        	Operation oper  = (Operation)traceStack.peek();
    	        	oper.setDescription(this.currentValue.toString());
    	        	oper.setLocaleDescriptions(convertI18n(attributes, oper.getName(), oper.getId(), "description"));
    	        }
    	        else if(name.equals("authoration"))
    	        {
    	        	
    	        }
    	        else
    	        {
    	        	log.warn("解析文件时[" + this.file + "]遇到元素[" + name + "]，忽略处理。");
    	        }
    	        
    	        
    	        

    	    }

    	    public void characters(char[] ch, int start, int length) {
    	        currentValue.append(ch, start, length);
    	    }

    	    public void endElement(String s1,String s2,String name) {

    	        if(name.equals("resources"))
    	        {
    	            Resources resources = (Resources)traceStack.pop();
//    	            List files = resources.getResourceFiles();
//    	            if(files != null && files.size() > 0)
//    	            {
//    	            	handleResourceFile(resources);
//    	            }
    	        }
    	        else if(name.equalsIgnoreCase("resource"))
    	        {
    	            ResourceInfo t = (ResourceInfo)traceStack.pop();
    	        }
    	        else if(name.equals("unprotected"))
    	        {
    	            UNProtectedResource unProtectedResource = (UNProtectedResource)traceStack.pop();

    	        }
    	        else if(name.equals("exclude"))
    	        {
    	           
    	            ExcludeResource excludeResource = (ExcludeResource)traceStack.pop();
    	        }
    	        else if(name.equals("group"))
    	        {
    	        	try{
    	        		OperationGroup group = (OperationGroup)traceStack.pop();
    	        		group.handle();
    	        	}
    	        	catch(Exception e)
    	        	{
    	        		e.printStackTrace();
    	        	}
    	        	
    	        	
    	        }
    	        else if(name.equals("operation"))
    	        {
    	        	
    	        	Object temp = traceStack.peek();            
    	            if(temp instanceof Operation)
    	            {
    	                this.traceStack.pop();
    	            }
    	        }  
    	        else if(name.toLowerCase().equals("url"))
    	        {
    	        	Object obj = this.traceStack.peek();
    	        	if(obj instanceof Operation)
    	        	{
    	        		Operation m = (Operation)obj;
    	        		String url = this.currentValue.toString();
    	        		String[] urls = url.split(",");
    	        		for(int i = 0; i < urls.length; i ++)
    	        		{
    	        			m.addAuthorResource(urls[i].trim());
    	        		}
    	        		
    	        	}
    	        	this.currentValue.setLength(0);
    	        }
    	        
    	        else if(name.equals("description")){
    	        	Operation oper  = (Operation)traceStack.peek();
    	        	oper.setDescription(this.currentValue.toString());
    	        	evalDefaultMessage(oper.getLocaleDescriptions(),oper.getDescription());
    	        }
    	    }
    	
    }
    
    
    class PropertiesFileParser extends DefaultHandler
    {
    	private Stack traceStack = new Stack();
        private StringBuffer currentValue;
        private String file;
        private ApplicationInfo ApplicationInfo;
    	public PropertiesFileParser(String file,ApplicationInfo ApplicationInfo)
    	{
    		this.ApplicationInfo = ApplicationInfo;
    		currentValue = new StringBuffer();
    		this.file = file;
    	}
    	
    	public void startElement(String s1,String s2,String name,Attributes attributes) {
            currentValue.delete(0, currentValue.length());
            
            if(name.equals("properties"))
            {
            	traceStack.push(ApplicationInfo);
            }
            //公共属性配置区
            else if(name.equals("property"))
            {
                ApplicationInfo ApplicationInfo = (ApplicationInfo)traceStack.peek();
                ApplicationInfo.addProperty(attributes.getValue("name"),attributes.getValue("value"));
            }
            else if(name.equals("propertiesfile"))
            {
                ApplicationInfo ApplicationInfo = (ApplicationInfo)traceStack.peek();
//                ApplicationInfo.addProperty(attributes.getValue("name"),attributes.getValue("value"));
                String propertiesfile = attributes.getValue("src");
                if(propertiesfile != null && propertiesfile.trim().length() > 0)
                {
                	PropertiesFile PropertiesFile = new PropertiesFile();
                	PropertiesFile.setPropertiesFile(propertiesfile);
                	String desc = attributes.getValue("desc");
                	PropertiesFile.setDesc(desc);
                	ApplicationInfo.addPropertyFile(PropertiesFile);
                	handlePropertyFile(ApplicationInfo,propertiesfile);
                }
            }
            else if(name.equals("authoration"))
	        {
	        	
	        }
            else
            {
            	log.warn("解析文件时[" + this.file + "]遇到元素[" + name + "]，忽略处理。");
            }
            
            
            

        }

        public void characters(char[] ch, int start, int length) {
            currentValue.append(ch, start, length);
        }

        public void endElement(String s1,String s2,String name) {

           
            if(name.equals("properties"))
            {
            	traceStack.pop();
            }

            //公共属性配置区
            else if(name.equals("property"))
            {

            }
            
            
            
        }
    	
    }
    
    
    /**
     * 系统初始化程序列表.
     * List<SystemInit>
     */
    private List systemInits;
    private String dictionary = "com.frameworkset.dictionary.ProfessionDataManager";
    ApplicationContext applicationContext;
    public ConfigParser(String file,ApplicationContext applicationContext) {
        traceStack = new Stack();
        applicationInfos = Collections.synchronizedMap(new HashMap());
        systemInits = new ArrayList();
        currentValue = new StringBuffer();
        this.file = file;
        this.applicationContext = applicationContext;
    }



    public void startElement(String s1,String s2,String name,Attributes attributes) {
        currentValue.delete(0, currentValue.length());
        if(name.equals("config-manager"))
        {
            this.sso = StringUtil.getBoolean(attributes.getValue("sso"),false);
            this.securityenabled = StringUtil.getBoolean(attributes.getValue("securityenabled"),false);
            this.ssoDomain = attributes.getValue("ssoDomain");
            this.securitycookieenabled = StringUtil.getBoolean(attributes.getValue("securitycookieenabled"),false);
        }
        else if(name.equals("application"))
        {
            ApplicationInfo applicationInfo = new ApplicationInfo();
            String app = StringUtil.replaceNull(attributes.getValue("name"),"bspf");
            applicationInfo.setApplication(app);

            String module = StringUtil.replaceNull(attributes.getValue("module"),"console");
            applicationInfo.setModule(module);

            applicationInfo.setCachable(StringUtil.getBoolean(attributes.getValue("cachable"),true));
            applicationInfo.setCacheType(attributes.getValue("cacheType"));
            boolean isdefault = StringUtil.getBoolean(attributes.getValue("default"),false);

            if(isdefault)
                defaultApplicationInfo = applicationInfo;
            applicationInfo.setSecurityenabled(StringUtil.getBoolean(attributes.
                    getValue("securityenabled"), false));
            applicationInfo.setDefaultApplicationInfo(isdefault);

            //根据安全配置信息所属的应用和应用模块来缓冲安全配置信息
//            applicationInfos.put(app + "$$" + module,applicationInfo);
            applicationInfos.put(app,applicationInfo);
            traceStack.push(applicationInfo);

        }
        else if(name.equals("context"))
        {
            context = new Context();
            context.setApplication(attributes.getValue("application"));
            context.setModuleName(attributes.getValue("webmodule"));
            this.traceStack.push(context);
        }
        else if(name.equals("managerimport"))
        {
        	ApplicationInfo applicationInfo = (ApplicationInfo)traceStack.peek();
        	ManagerImport mi = new ManagerImport();
        	mi.setFile(attributes.getValue("file"));
        	applicationInfo.addManagerImport(mi);
        }        	
        else if(name.equals("manager"))
        {
            ApplicationInfo applicationInfo = (ApplicationInfo)traceStack.peek();
            String id = attributes.getValue("id");
            ProviderManagerInfo providerManger = new ProviderManagerInfo();
            providerManger.setId(id);
            providerManger.setJndiName(attributes.getValue("jndiname"));
            String clazz = attributes.getValue("interceptor");
			if(clazz != null && !clazz.equals(""))
			{
				InterceptorInfo interceptor = new InterceptorInfo();
				interceptor.setClazz(clazz);
				providerManger.addInterceptor(interceptor);
			}
			
            providerManger.setSinglable(StringUtil.getBoolean(attributes.getValue("singlable"),true));
            providerManger.setDefaultable(StringUtil.getBoolean(attributes.getValue("default"),false));
            if(providerManger.isDefaultable())
                applicationInfo.setDefaultProviderManagerInfo(providerManger);
            applicationInfo.addProviderManagerInfo(providerManger);

            traceStack.push(providerManger);
        }
        else if (name.equals("interceptor")) {
			ProviderManagerInfo providerManagerInfo = (ProviderManagerInfo) this.traceStack
					.peek();
			InterceptorInfo interceptor = new InterceptorInfo();
			String clazz = attributes.getValue("class");
			interceptor.setClazz(clazz);
			
			providerManagerInfo.addInterceptor(interceptor);

		}
        else if(name.equals("provider"))
        {
            ProviderManagerInfo providerManager = (ProviderManagerInfo)traceStack.peek();
            SecurityProviderInfo provider = new SecurityProviderInfo();
            boolean isdefault = StringUtil.getBoolean(attributes.getValue("default"),false);
            provider.setIsdefault(isdefault);
            provider.setProviderClass(attributes.getValue("class"));
            provider.setType(attributes.getValue("type"));
            provider.setUsed(StringUtil.getBoolean(attributes.getValue("used"),false));

            if(isdefault)
                providerManager.setDefaulProviderInfo(provider);
            providerManager.addSecurityProviderInfo(provider);
            traceStack.push(provider);
        }
        else if(name.equals("resources"))
        {
            ApplicationInfo ApplicationInfo = (ApplicationInfo)traceStack.peek();
            String moduleName = attributes.getValue("module");
            if(moduleName == null)
                moduleName = "console";
            Resources resources = new Resources();
            resources.setModuleName(moduleName);
            this.languages = this.converLocales(attributes.getValue("languages"));
            resources.setLanguages(languages);
            ApplicationInfo.addResources(resources);
            traceStack.push(resources);
        }
        else if(name.equalsIgnoreCase("resourcefile"))//解析资源类型和操作文件
        {
        	Resources resources = (Resources)traceStack.peek();
        	
        	String resourceFile = attributes.getValue("src");
        	if(resourceFile != null && resourceFile.trim().length() >0)
        	{
        		ImportResource ir = new ImportResource();
        		String desc = attributes.getValue("desc");
        		ir.setDesc(desc);
	        	ir.setResourceFile(resourceFile);
	        	resources.addImportResourceFile(ir);
	        	handleResourceFile(resources,ir );
	        }
        }
        else if(name.equalsIgnoreCase("resource"))
        {
            Object obj = traceStack.peek();
            ResourceInfo resourceInfo = new ResourceInfo();
            resourceInfo.setId(attributes.getValue("id"));
            resourceInfo.setName(attributes.getValue("name"));
          
            resourceInfo.setResource(attributes.getValue("class"));
            resourceInfo.setAuto(StringUtil.getBoolean(attributes.getValue("auto"),false));
            resourceInfo.setUsed(StringUtil.getBoolean(attributes.getValue("used"),true));
            boolean isdefault = StringUtil.getBoolean(attributes.getValue("default"),true);
           
            resourceInfo.setStruction(StringUtil.replaceNull(attributes.getValue("struction"),"list"));
            resourceInfo.setSystem(StringUtil.replaceNull(attributes.getValue("system"),"module"));
            resourceInfo.setDefaultResourceInfo(isdefault);
            boolean maintaindata = StringUtil.getBoolean(attributes.getValue("maintaindata"),true);
            resourceInfo.setMaintaindata(maintaindata);
            String allowIfNoRequiredRole = attributes.getValue("allowIfNoRequiredRole");
            //如果设置了allowIfNoRequiredRole属性，修改defaultAllowIfNoRequiredRole标识为false，以便采用
            //屏蔽全局的allowIfNoRequiredRole属性，而采用为resource设置的allowIfNoRequiredRole属性配置
            if(allowIfNoRequiredRole != null)
            {
            	resourceInfo.setDefaultAllowIfNoRequiredRole(false);
            	resourceInfo.setAllowIfNoRequiredRole(StringUtil.getBoolean(allowIfNoRequiredRole,false));
            }
            
         
            if(obj instanceof Resources)
            {                
                Resources resources = (Resources)traceStack.peek();

                if(isdefault)
                    resources.setDefaultResourceInfo(resourceInfo);
                resources.addResourceInfo(resourceInfo);
            }
            else if(obj instanceof ResourceInfo)
            {
                ResourceInfo parent = (ResourceInfo)traceStack.peek();
                Resources resources = parent.getResources();
                resources.addNestingResourceInfo(resourceInfo);
                parent.addSubResourceInfo(resourceInfo);
            }
            traceStack.push(resourceInfo);
        }
        
        else if(name.equals("unprotected"))
        {
            ResourceInfo resourceInfo = (ResourceInfo)traceStack.peek();
            UNProtectedResource unProtectedResource = new UNProtectedResource();
            unProtectedResource.setResourceID(attributes.getValue("resourceid"));
            unProtectedResource.setResourceType(resourceInfo.getId());
            unProtectedResource.setResouceInfo(resourceInfo);
            resourceInfo.addUNProtectedResource(unProtectedResource);
            traceStack.push(unProtectedResource);
        }
        else if(name.equals("exclude"))
        {
            ResourceInfo resourceInfo = (ResourceInfo)traceStack.peek();
            ExcludeResource excludeResource = new ExcludeResource();
            excludeResource.setResourceID(attributes.getValue("resourceid"));
            excludeResource.setResourceType(resourceInfo.getId());
            excludeResource.setResouceInfo(resourceInfo);
            resourceInfo.addExcludeResource(excludeResource);
            traceStack.push(excludeResource);
        }

        else if(name.equals("operationgroup"))
        {
            ResourceInfo resourceInfo = (ResourceInfo)traceStack.peek();
            resourceInfo.setOperationGroupID(attributes.getValue("groupid"));
        }
        
        else if(name.equals("globaloperationgroup"))//指定资源的全局操作组
        {
        	ResourceInfo resourceInfo = (ResourceInfo)traceStack.peek();
            resourceInfo.setGlobalOperationGroupID(attributes.getValue("groupid"));
            resourceInfo.setGlobalresourceid(attributes.getValue("globalresourceid"));
        }
        else if(name.equals("operations"))
       {

       }

        else if(name.equals("group"))
        {
             Resources resources = (Resources)traceStack.peek();
             OperationGroup group = new OperationGroup();
             group.setGroupID(attributes.getValue("id"));
             group.setGroupName(attributes.getValue("name"));
             resources.addOperationGroup(group);
             boolean d = StringUtil.getBoolean(attributes.getValue("default"),false);
             group.setDefaultable(d);
             if(d)
                 resources.setDefaultOperationGroup(group);
             traceStack.push(group) ;
        }

        else if(name.equals("operation"))
        {
        	Object temp = traceStack.peek();
        	
            
            if(temp instanceof OperationGroup)
            {
            	OperationGroup group = null;
            	group = (OperationGroup)temp;
            	Operation oper  = new Operation();
            	
                oper.setId(attributes.getValue("id"));
                oper.setName(attributes.getValue("name"));
                oper.setPriority(StringUtil.replaceNull(attributes.getValue("priority"),"00"));
                oper.setManager(StringUtil.getBoolean(attributes.getValue("manager"),true));
                group.addOperation(oper);
                this.traceStack.push(oper);
            }
            else if(temp instanceof ExcludeResource)
            {
            	ExcludeResource exr = (ExcludeResource)temp;
            	Operation oper  = new Operation();
                oper.setId(attributes.getValue("id"));
                exr.addExcludeOperation(oper);
                
            }
            else if(temp instanceof UNProtectedResource)
            {
            	UNProtectedResource unp = (UNProtectedResource)temp;
            	Operation oper  = new Operation();
                oper.setId(attributes.getValue("id"));
                unp.addUnprotectedOperation(oper);
            }
            
        }
        else if(name.equals("description"))
        {
//        	Operation oper  = (Operation)traceStack.peek();
//        	oper.setDescription("");
        }

        else if(name.equals("permissionrolemap"))
        {
            ApplicationInfo ApplicationInfo = (ApplicationInfo)traceStack.peek();
            PermissionRoleMapInfo permissionRoleMapInfo = new PermissionRoleMapInfo();
            permissionRoleMapInfo.setCachable(StringUtil.getBoolean(attributes.getValue("cachable"),true));
            permissionRoleMapInfo.setCacheType(attributes.getValue("cachetype"));
            permissionRoleMapInfo.setModuleName(attributes.getValue("module"));
            permissionRoleMapInfo.setPermissionRoleMapClass(attributes.getValue("class"));
            permissionRoleMapInfo.setDefaultable(StringUtil.getBoolean(attributes.getValue("default"),false));
            permissionRoleMapInfo.setAllowIfNoRequiredRole(StringUtil.getBoolean(attributes.getValue("allowIfNoRequiredRole"),false));
            String providerType = attributes.getValue("providerType") ;
            if(providerType == null)
                providerType = "DB";
            permissionRoleMapInfo.setProviderType(providerType);

            if(permissionRoleMapInfo.isDefaultable())
                ApplicationInfo.setDefaultPermissionrolemap(permissionRoleMapInfo);
            ApplicationInfo.setPermissionRoleMapInfo(permissionRoleMapInfo);
            this.traceStack.push(permissionRoleMapInfo);
        }

        else if(name.equals("authorizetable"))
        {
            ApplicationInfo applicationInfo = (ApplicationInfo)traceStack.peek();
            AuthorTableInfo authorTableInfo = new AuthorTableInfo();
            authorTableInfo.setAuthorizetableClass(attributes.getValue("class"));
            authorTableInfo.setCachable(StringUtil.getBoolean(attributes.getValue("cachable"),true));
            authorTableInfo.setCacheType(attributes.getValue("cachetype"));
            authorTableInfo.setModuleName(attributes.getValue("module"));
            String providerType = attributes.getValue("providerType") ;
            //设置系统管理员角色，缺省为administrator
            String adminRole = StringUtil.replaceNull(attributes.getValue("adminrole"),"administrator") ;
            String everyonegrantedrole = StringUtil.replaceNull(attributes.getValue("everyonegrantedrole"),"roleofeveryone"); ;
            authorTableInfo.setAdminRole(adminRole);
            authorTableInfo.setEveryonegrantedrole(everyonegrantedrole);
            if(providerType == null)
                providerType = "DB";
            authorTableInfo.setProviderType(providerType);
            applicationInfo.setAuthorizetable(authorTableInfo);

            authorTableInfo.setDefaultable(StringUtil.getBoolean(attributes.getValue("default"),false));
            if(authorTableInfo.isDefaultable())
                applicationInfo.setDefaultAuthorTableInfo(authorTableInfo);
            traceStack.push(authorTableInfo);
        }

        else if(name.equals("authorizable"))
        {
            ApplicationInfo applicationInfo = (ApplicationInfo)traceStack.peek();
            applicationInfo.setAuthorfaileddirect(StringUtil.replaceNull(
                    attributes.getValue("authorfaileddirect"), "index.jsp"));


//            applicationInfo.setLoginpage(StringUtil.replaceNull(attributes.
//                    getValue("loginpage"), "index.jsp"));
        }
        else if(name.equals("authenticate"))
        {
            ApplicationInfo applicationInfo = (ApplicationInfo)traceStack.peek();

            applicationInfo.setLoginpage(StringUtil.replaceNull(attributes.
                    getValue("loginpage"), "index.page"));
            applicationInfo.setSso(StringUtil.getBoolean(attributes.
                                                      getValue("sso"), false));
        }
        else if(name.equals("notifiable"))
        {
            ApplicationInfo ApplicationInfo = (ApplicationInfo)traceStack.peek();
            NotifiableInfo notifiableInfo = new NotifiableInfo();
            notifiableInfo.setFactoryClass(attributes.getValue("factory"));
            notifiableInfo.setType(attributes.getValue("type"));
            ApplicationInfo.addNotifiableInfo(notifiableInfo);
            traceStack.push(notifiableInfo);
        }
        else if(name.equals("loginModule"))
        {
            ApplicationInfo applicationInfo = (ApplicationInfo)traceStack.peek();
            LoginModuleInfo loginModuleInfo = new LoginModuleInfo();
            loginModuleInfo.setDebug(StringUtil.getBoolean( attributes.getValue("debug"),false));
            loginModuleInfo.setLoginModule(attributes.getValue("class"));
            loginModuleInfo.setCallBackHandler(attributes.getValue("callbackHandler"));
            loginModuleInfo.setControlFlag(attributes.getValue("controlFlag"));
            loginModuleInfo.setName(attributes.getValue("name"));
            String registTable = attributes.getValue("registTable");
            if(registTable == null)
                registTable = "DB";
            loginModuleInfo.setRegistTable(registTable);

            applicationInfo.addLoginModuleInfo(loginModuleInfo);
            traceStack.push(loginModuleInfo);
        }
        //公共属性配置区
        else if(name.equals("properties"))
        {

        }
        //公共属性配置区
        else if(name.equals("property"))
        {
            ApplicationInfo ApplicationInfo = (ApplicationInfo)traceStack.peek();
            ApplicationInfo.addProperty(attributes.getValue("name"),attributes.getValue("value"));
        }
        //公共属性配置区
        else if(name.equals("propertiesfile"))
        {
            ApplicationInfo ApplicationInfo = (ApplicationInfo)traceStack.peek();
//            ApplicationInfo.addProperty(attributes.getValue("name"),attributes.getValue("value"));
            String propertiesfile = attributes.getValue("src");
            if(propertiesfile != null && propertiesfile.trim().length() > 0)
            {
            	PropertiesFile PropertiesFile = new PropertiesFile();
            	PropertiesFile.setPropertiesFile(propertiesfile);
            	String desc = attributes.getValue("desc");
            	PropertiesFile.setDesc(desc);
            	ApplicationInfo.addPropertyFile(PropertiesFile);
            	handlePropertyFile(ApplicationInfo,propertiesfile);
            }
        }
        else if(name.equals("datasource"))
        {
            dataSourceConfig = new DataSourceConfig();
            this.traceStack.push(dataSourceConfig);
        }
        else if(name.equals("database"))
        {

            DataSourceConfig dataSourceConfig = (DataSourceConfig)traceStack.peek();
            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.setName(attributes.getValue("name"));
            databaseConfig.setDefaultDB(StringUtil.getBoolean( attributes.getValue("default"),false));
            dataSourceConfig.addDbConfig(databaseConfig);

            if(databaseConfig.isDefaultDB())
                dataSourceConfig.setDefaultDatabaseConfig(databaseConfig);
            this.traceStack.push(databaseConfig);
        }
        else if(name.equals("ldap"))
        {
            DataSourceConfig dataSourceConfig = (DataSourceConfig)traceStack.peek();
            LDAPConfig ldapConfig = new LDAPConfig();
            ldapConfig.setName(attributes.getValue("name"));
            ldapConfig.setInitial_context_factory(attributes.getValue("initial_context_factory"));
            ldapConfig.setProvider_url(attributes.getValue("provider_url"));
            ldapConfig.setSecurity_authentication(attributes.getValue("security_authentication"));
            ldapConfig.setSecurity_pricipal(attributes.getValue("security_pricipal"));
            ldapConfig.setSecurity_credentials(attributes.getValue("security_credentials"));
            ldapConfig.setDefaultLDAP(StringUtil.getBoolean( attributes.getValue("default"),false));
            dataSourceConfig.addLDAPConfig(ldapConfig);
            if(ldapConfig.isDefaultLDAP())
                dataSourceConfig.setDefaultLDAPConfig(ldapConfig);
            this.traceStack.push(ldapConfig);
        }

        else if(name.equals("synchronize"))
        {

            ProviderManagerInfo providerManagerInfo = (ProviderManagerInfo)this.traceStack.peek();
            providerManagerInfo.setSynchronizedEnabled(StringUtil.getBoolean(attributes.getValue("enabled"),false));

        }
        else if(name.equals("transactions"))
        {
        	ProviderManagerInfo providerManagerInfo = (ProviderManagerInfo)this.traceStack.peek();        	
        	Transactions txs = new Transactions() ;
        	providerManagerInfo.setTransactions(txs);
        	traceStack.push(txs);
        	
        }
        else if(name.equals("reference"))
        {
        	ProviderManagerInfo providerManagerInfo = (ProviderManagerInfo)this.traceStack.peek();
        	Reference ref = new Reference(applicationContext);
        	String fieldname = attributes.getValue("fieldname");
        	String refid = attributes.getValue("refid");
        	String reftype = attributes.getValue("reftype");
        	String value = attributes.getValue("value");
        	ref.setFieldname(fieldname);
        	ref.setRefid(refid);
        	ref.setReftype(reftype);
        	ref.setValue(value);
        	providerManagerInfo.addReference(ref);
        	
        	
        }
       
        else if(name.equals("method"))
        {
            
            SynchronizedMethod synchronizedMethod = new SynchronizedMethod();
            synchronizedMethod.setMethodName(attributes.getValue("name"));   
            synchronizedMethod.setPattern(attributes.getValue("pattern"));
            synchronizedMethod.setTxtype(attributes.getValue("txtype"));  
            traceStack.push(synchronizedMethod);
        }
        else if(name.equals("rollbackexceptions"))
		{
        	//do nothing
		}
        else if(name.equals("exception"))
		{
        	RollbackException e = new RollbackException();
        	String exceptionName = attributes.getValue("class");
        	String exceptionType = attributes.getValue("type");
        	e.setExceptionName(exceptionName);
        	e.setExceptionType(exceptionType);
        	SynchronizedMethod method = (SynchronizedMethod)traceStack.peek();
        	method.addRollbackException(e);
        	
		}
        else if(name.equals("param")) //添加方法参数信息
        {
        	SynchronizedMethod method = (SynchronizedMethod)traceStack.peek();
        	String paramType = attributes.getValue("type");
        	Param param = new Param(applicationContext);
        	param.setClazz(paramType);
        	method.addParam(param);        	
        }
        
        else if(name.equals("taskservice"))
        {
        	taskServiceInfo = new TaskServiceInfo();
        	taskServiceInfo.setUsed(StringUtil.getBoolean( attributes.getValue("used"),true));
        	this.traceStack.push(taskServiceInfo);
        	
        }
        
        else if(name.equals("scheduleservice"))
        {
        	ScheduleServiceInfo scheduleServiceInfo = new ScheduleServiceInfo();
        	
        	scheduleServiceInfo.setClazz(attributes.getValue("class"));
        	scheduleServiceInfo.setId(attributes.getValue("id"));
        	scheduleServiceInfo.setName(attributes.getValue("name"));
        	scheduleServiceInfo.setUsed(StringUtil.getBoolean( attributes.getValue("used"),true));
        	taskServiceInfo.addScheduleServiceInfo(scheduleServiceInfo);
        	traceStack.push(scheduleServiceInfo);
        }
        else if(name.equals("schedulejob"))
        {
        	SchedulejobInfo schedulejobInfo = new SchedulejobInfo();
        	ScheduleServiceInfo scheduleServiceInfo = (ScheduleServiceInfo)this.traceStack.peek();
        	schedulejobInfo.setClazz(attributes.getValue("class"));
        	schedulejobInfo.setId(attributes.getValue("id"));
        	schedulejobInfo.setName(attributes.getValue("name"));
        	schedulejobInfo.setUsed(StringUtil.getBoolean( attributes.getValue("used"),true));
        	schedulejobInfo.setCronb_time(attributes.getValue("cronb_time"));
        	
        	scheduleServiceInfo.add(schedulejobInfo);
        	traceStack.push(schedulejobInfo);
        }
        else if(name.equals("parameter"))
        {
        	SchedulejobInfo schedulejobInfo = (SchedulejobInfo)this.traceStack.peek();
        	schedulejobInfo.addParameter(attributes.getValue("name"),attributes.getValue("value"));        	
        }
        else if(name.equals("dictionary"))
    	{
    		dictionary = StringUtil.replaceNull(attributes.getValue("dictionary"),"com.frameworkset.dictionary.ProfessionDataManager");
    	}
//        else if(name.equals("communication"))
//    	{
//        	this.communicationInfo = new CommunicationInfo();
//    		//dictionary = StringUtil.replaceNull(attributes.getValue("dictionary"),"com.frameworkset.dictionary.ProfessionDataManager");
//    	}
//        else if(name.equals("mail"))
//    	{
//        	
//        	String mail = StringUtil.replaceNull(attributes.getValue("class"),"");
//        	this.communicationInfo.setMail(mail);
//    		//dictionary = StringUtil.replaceNull(attributes.getValue("dictionary"),"com.frameworkset.dictionary.ProfessionDataManager");
//    	}
//        else if(name.equals("sms"))
//    	{
//        	//this.communicationInfo = new CommunicationInfo();
//        	String sms = StringUtil.replaceNull(attributes.getValue("class"),"");
//        	this.communicationInfo.setSms(sms);
//    		//dictionary = StringUtil.replaceNull(attributes.getValue("dictionary"),"com.frameworkset.dictionary.ProfessionDataManager");
//    	}
        else if(name.equals("system"))
        {
        	String systemClass = StringUtil.replaceNull(attributes.getValue("class"),"");
        	
        	if(!systemClass .equals( "") )
        	{
        		try {
					SystemInit systemInit = (SystemInit)Class.forName(systemClass).newInstance();
					this.systemInits.add(systemInit);
				} catch (InstantiationException e) {
					log.error("装载系统初始化程序信息异常：请检查[" + systemClass + "]是否存在,或者是否实现接口[com.frameworkset.platform.config.SystemInit]并且提供缺省的构造函数");
				} catch (IllegalAccessException e) {
					log.error("装载系统初始化程序信息异常：请检查[" + systemClass + "]是否存在,或者是否实现接口[com.frameworkset.platform.config.SystemInit]并且提供缺省的构造函数");
				} catch (ClassNotFoundException e) {
					log.error("装载系统初始化程序信息异常：请检查[" + systemClass + "]是否存在,或者是否实现接口[com.frameworkset.platform.config.SystemInit]并且提供缺省的构造函数");
				}
        	}
        }
        else if(name.equals("authoration"))
        {
        	
        }
        else
        {
        	log.warn("解析文件时[" + this.file + "]遇到元素[" + name + "]，忽略处理。");
        }
        
        
        

    }

    public void characters(char[] ch, int start, int length) {
        currentValue.append(ch, start, length);
    }

    public void endElement(String s1,String s2,String name) {

        if(name.equals("application"))
        {
            ApplicationInfo applicationInfo = (ApplicationInfo)traceStack.pop();
           
        }
        else if(name.equals("taskservice"))
        {
        	traceStack.pop();
        }
        else if(name.equals("scheduleservice"))
        {
        	traceStack.pop();
        }
        else if(name.equals("schedulejob"))
        {
        	traceStack.pop();
        }
        else if(name.equals("context"))
        {
            context = (Context)traceStack.pop();
        }

        else if(name.equals("manager"))
        {
            ProviderManagerInfo t = (ProviderManagerInfo)traceStack.pop();
        }
        else if(name.equals("provider"))
        {

            SecurityProviderInfo t = (SecurityProviderInfo)traceStack.pop();
        }
        else if(name.equals("method"))
        {        	
        	SynchronizedMethod synchronizedMethod = (SynchronizedMethod)this.traceStack.pop();      
        	Object parent = this.traceStack.peek();
        	if(parent instanceof ProviderManagerInfo)
        	{
	        	ProviderManagerInfo providerManagerInfo = (ProviderManagerInfo)this.traceStack.peek();
	        	
	            providerManagerInfo.addSynchronizedMethod(synchronizedMethod);
        	}
        	else
        		
        	{
        		Transactions txs = (Transactions)this.traceStack.peek();
        		txs.addTransactionMethod(synchronizedMethod);
        	}
        }
        else if(name.equals("transactions"))
        {
        	
        	traceStack.pop();        	
        }
        else if(name.equals("permissionrolemap"))
        {
            PermissionRoleMapInfo t = (PermissionRoleMapInfo)traceStack.pop();
        }
        else if(name.equals("authorizetable"))
        {
            AuthorTableInfo authorTableInfo = (AuthorTableInfo)traceStack.pop();
        }
        else if(name.equals("resources"))
        {
            Resources resources = (Resources)traceStack.pop();

//            List files = resources.getResourceFiles();
//            if(files != null && files.size() > 0)
//            {
//            	handleResourceFile(resources);
//            }
        }
        else if(name.equalsIgnoreCase("resource"))
        {
            ResourceInfo t = (ResourceInfo)traceStack.pop();
        }
        else if(name.equals("unprotected"))
        {
            UNProtectedResource unProtectedResource = (UNProtectedResource)traceStack.pop();

        }
        else if(name.equals("exclude"))
        {
           
            ExcludeResource excludeResource = (ExcludeResource)traceStack.pop();
        }
        else if(name.equals("group"))
        {
        	try{
        		OperationGroup group = (OperationGroup)traceStack.pop();
        		group.handle();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        	
        	
        }
        else if(name.equals("description")){
        	Operation oper  = (Operation)traceStack.peek();
        	oper.setDescription(this.currentValue.toString());
        }
        else if(name.equals("operation")){
        	
        	Object temp = traceStack.peek();            
            if(temp instanceof Operation)
            {
                this.traceStack.pop();
            }
        }
        else if(name.equals("authorizable"))
        {

        }
        else if(name.equals("authenticate"))
        {

        }
        else if(name.equals("notifiable"))
        {
            NotifiableInfo t = (NotifiableInfo)traceStack.pop();
        }
        else if(name.equals("loginModule"))
        {
            LoginModuleInfo t = (LoginModuleInfo)traceStack.pop();
        }
        //公共属性配置区
        else if(name.equals("properties"))
        {

        }

        //公共属性配置区
        else if(name.equals("property"))
        {

        }
        else if(name.equals("datasource"))
        {
            DataSourceConfig dataSourceConfig = (DataSourceConfig)this.traceStack.pop();
        }
        else if(name.equals("database"))
        {
            DatabaseConfig databaseConfig = (DatabaseConfig)traceStack.pop();
        }
        else if(name.equals("ldap"))
        {
            LDAPConfig ldapConfig = (LDAPConfig)traceStack.pop();
        }
        
        
    }

    class ConfigElement {
        String name;
        String value;
        String datatype;
        ConfigElement parent;
        ArrayList children;

        ConfigElement(String name, String value, String datatype) {
            this.name = name;
            this.value = value;
            this.datatype = datatype;
            this.parent = null;
            this.children = new ArrayList();
        }

        String getValue() {
            return this.value;
        }

        void setValue(String value) {
            this.value = value;
        }

        boolean hasChildren() {
            if (this.children.size() > 0)
                return true;
            return false;
        }

        ConfigElement getParent() {
            return this.parent;
        }

        ArrayList getChildren() {
            return this.children;
        }

        void addChild(ConfigElement child) {
            this.children.add(child);
        }
    }

    public Map<String,ApplicationInfo> getApplicationInfos() {
        return applicationInfos;
    }

    public ApplicationInfo getDefaultApplicationInfo() {
        return defaultApplicationInfo;
    }

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public Context getContext() {
        return context;
    }

    public boolean isSecurityenabled() {
        return securityenabled;
    }

    public boolean isSso() {
        return sso;
    }

    public String getSsoDomain() {
        return ssoDomain;
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



	public String getDictionary() {
		return dictionary;
	}



//	public CommunicationInfo getCommunicationInfo() {
//		return communicationInfo;
//	}



	public List getSystemInits() {
		return systemInits;
	}



	public void setSystemInits(List systemInits) {
		this.systemInits = systemInits;
	}
}
