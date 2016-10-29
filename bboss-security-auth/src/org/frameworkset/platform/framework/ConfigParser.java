package org.frameworkset.platform.framework;

/**
 * <p>Title: 解析系统菜单主程序</p>
 *
 * <p>Description: 国际化改造</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: iSany</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */





import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.xml.sax.Attributes;

import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.util.I18nXMLParser;
import com.frameworkset.util.StringUtil;

/**
 * SAXParser used by Configurator to parse the
 * poolman.xml file. It returns Collections of
 * generic pool properties, JDBC pool properties,
 * and admin-agent properties.
 */
public class ConfigParser extends I18nXMLParser  {
    private static Logger log = Logger.getLogger(ConfigParser.class) ;
    private ModuleQueue modules;
    private MenuQueue menus; 
    private ItemQueue items;
    private SubSystem ownersubsystem;
    private String file;
    private String systemid;
    /**
     * 系统模块菜单索引,
     * Map<String menuPath,MenuItem>
     */
    private Map indexs;
    /**
	 * 菜单id索引表
	 */
	private Map indexByIds;
    private String description;
    private Map<Locale,String> localeDescriptions;
    private Stack traceStack;
    private String currentSet;
    private String top_height="38";
    private String left_width="30";
    private String navigator_width="20%";
    private String workspace_height="75%";
    private String showhidden = "true";
    private BaseApplicationContext propertiesContext = null; 
    /**
     * 一级items是否显示左侧菜单，true显示，false不显示
     */
    private boolean showrootleftmenu = false;
    

    private Item publicItem;

    private Item defaultItem;

    private StringBuffer currentValue = new StringBuffer();
    private String global_target = "perspective_content";
    /**
     * 根据系统模型，根据是否启用岗位的开关，系统可以引入岗位管理，也可以屏蔽岗位管理功能
     * 如果系统不启用岗位管理功能，将不需要加载岗位管理对应的资源类型
     */
    private boolean isjob = false;

    private String messagesourcefiles;
     
    

	public String getMessagesourcefiles() {
		return messagesourcefiles;
	}

	public void setMessagesourcefiles(String messagesourcefiles) {
		this.messagesourcefiles = messagesourcefiles;
	}

    public ConfigParser(String file,String systemid) {

        this.modules = new ModuleQueue();
        this.items = new ItemQueue();
        this.menus = new MenuQueue();
        this.traceStack = new Stack();
        this.file = file;
        this.systemid = systemid;

//        indexs = Collections.synchronizedMap(new HashMap());
        indexs = new HashMap();
//        indexByIds = Collections.synchronizedMap(new HashMap());
        indexByIds = new HashMap();
    }

    public ModuleQueue getModules() {
        return this.modules;
    }

    public Map getIndexs() {
        return indexs;
    }
    public Map getIndexByIds() {
		
		return indexByIds;
	}
    
    private boolean isjob()
    {    	
    	return this.isjob;
    }

    public Item getDefaultItem() {
        return defaultItem;
    }

    public String getDescription() {
        return description;
    }
    private void evalExtendContent(Item item,Attributes attributes)
    {
    	if(attributes.getLength() > 0)
    	{
    		Map<String,String> eattrs = new HashMap<String,String>();
    		
    		for(int i =0; i < attributes.getLength(); i ++)
    		{
    			String attrName = attributes.getQName(i);
    			eattrs.put(attrName, attributes.getValue(i));
    		}
    		item.setWorkspacecontentExtendAttribute(eattrs);
    	}
    }
   
    
    
    public void startElement(String s1,String s2,String name, Attributes attributes) {

        currentValue.delete(0, currentValue.length());
        if(name.equals("system"))
        {
        	this.subsystems = new HashMap();
        	this.subsystemList = new ArrayList<SubSystem>();
        	 String las = attributes.getValue("languages");
             if(las != null && !las.equals(""))
             	this.languages = this.converLocales(las);
            
        }
        else if(name.equals("property"))
        {
        	
        	 String propertyFile = attributes.getValue("file");
             if(propertyFile != null && !propertyFile.equals(""))
             	this.propertiesContext = DefaultApplicationContext.getApplicationContext(propertyFile);
            
        }
        else if(name.toLowerCase().equals("subsystem"))
        {
        	SubSystem subsystem = new SubSystem();
        	subsystem.setId(attributes.getValue("id"));
        	subsystem.setName(attributes.getValue("name"));
        	subsystem.setBaseuri(attributes.getValue("baseuri"));
        	subsystem.setModule(attributes.getValue("module"));
         	subsystem.setTemplate(attributes.getValue("template"));
         	subsystem.setLogoutredirect(attributes.getValue("logoutredirect"));
         	subsystem.setSuccessRedirect(attributes.getValue("successRedirect"));         	
         	subsystem.setLocaleNames(this.convertI18n(attributes, subsystem.getName(), subsystem.getId(), "subsystem.localnames"));
        	subsystems.put(subsystem.getId(),subsystem);
        	subsystemList.add(subsystem);
        }
        else if(name.toLowerCase().equals("publicitem"))
        {
            this.publicItem = new Item();
            publicItem.setName(attributes.getValue("name"));
            publicItem.setId(attributes.getValue("id"));
            publicItem.setArea(attributes.getValue("area"));
            publicItem.setIsMain(StringUtil.getBoolean(attributes.getValue("main"),false));
            if(ownersubsystem != null)
            {
            	publicItem.setParentPath(Framework.getSuperMenu(ownersubsystem.getId()));
            	publicItem.setPath(Framework.getSuperMenu(ownersubsystem.getId()) + "/" + publicItem.getId() + "$item" );
            }
            else
            {
            	publicItem.setParentPath(Framework.getSuperMenu("module"));
            	publicItem.setPath(Framework.getSuperMenu("module") + "/" + publicItem.getId() + "$item" );
            }
            publicItem.setCode(3);
            publicItem.setSubSystem(ownersubsystem);
            //publicItem.setHeadimg(attributes.getValue("headimg"));
            publicItem.setLocaleNames(this.convertI18n(attributes, publicItem.getName(), publicItem.getId(), "publicitem"));
            traceStack.push(publicItem);
        }
        else if(name.toLowerCase().equals("sysmenu"))
        {
            currentSet = "sysmenu";
            this.traceStack.push("sysmenu");
            this.top_height = StringUtil.replaceNull(attributes.getValue("top_height"),top_height);
            this.left_width = StringUtil.replaceNull(attributes.getValue("left_width"),left_width);
            this.navigator_width = StringUtil.replaceNull(attributes.getValue("navigator_width"),navigator_width);
            this.workspace_height = StringUtil.replaceNull(attributes.getValue("workspace_height"),workspace_height);
            this.showhidden = StringUtil.replaceNull(attributes.getValue("showhidden"),"true");
            this.showhidden_width = StringUtil.replaceNull(attributes.getValue("showhidden_width"),"12");
            this.global_target = StringUtil.replaceNull(attributes.getValue("global_target"),"perspective_content");
            this.showrootleftmenu = StringUtil.getBoolean(attributes.getValue("showrootleftmenu"),false);
            this.logoutredirect = attributes.getValue("logoutredirect");
            this.successRedirect = attributes.getValue("successRedirect");
            String tempfiles = attributes.getValue("messagesource");
            if(tempfiles != null && !tempfiles.equals(""))
            	this.messagesourcefiles = tempfiles;
            String las = attributes.getValue("languages");
            if(las != null && !las.equals(""))
            	this.languages = this.converLocales(las);
           
        }
        else if(name.toLowerCase().equals("module"))
        {
            currentSet = "module";
            Module module = new Module();
            module.setSubSystem(ownersubsystem);
            Object obuj = traceStack.peek();
            String showleftmenu = null;
            String option = null;
            if(attributes != null)
            {
                module.setName(attributes.getValue("name"));
                module.setId(attributes.getValue("id"));
                //module.setHeadimg(attributes.getValue("headimg"));
                module.setUsed(StringUtil.getBoolean(attributes.getValue("used"),true));
               
                module.setShowpage(StringUtil.getBoolean(attributes.getValue("showpage"),false));
                showleftmenu = attributes.getValue("showleftmenu");
                module.setTarget(attributes.getValue("target"));
                module.setUrl(attributes.getValue("url"));
                if(showleftmenu != null && showleftmenu.equals("true"))
                	module.setShowleftmenu(true);
                option = attributes.getValue("option");
                if(option != null && !option.equals(""))
                	module.setOption(option);
                else
                	module.setOption("{}");
            }
            if(obuj == null)
            {
                log.debug("文档非法!");
            }
            else if(obuj instanceof String)
            {
            	
                String parent = "";
                if(ownersubsystem != null)
                {
                	parent = Framework.getSuperMenu(ownersubsystem.getId());
                }
                else
                {
                	parent = Framework.getSuperMenu("module");
                }
                //设置父路径
                module.setParentPath(parent);
                module.setPath(parent + "/" + module.getId() + "$module");
                
                module.setCode(1);
                //建立模块索引
                this.indexs.put(module.getPath(),module);
                this.indexByIds.put(module.getId(), module);
                this.modules.addModule(module);
                this.menus.addMenuItem(module);
            }
            else if(obuj instanceof Module)
            {
                Module item = (Module)obuj;                
                String parent = item.getPath();
                if(!item.isUsed())
                	module.setUsed(false);
                	
                module.setParentPath(parent);
                int code = item.getCode();
                
                if(code == 0)
                	module.setCode(1);
                else
                	module.setCode(0);
                module.setPath(parent + "/" + module.getId() + "$module");
                if(showleftmenu == null)
                	module.setShowleftmenu(item.isShowleftmenu());
//                if(popup == null)
//                	module.setPopup(item.isPopup());
                this.indexs.put(module.getPath(),module);
                this.indexByIds.put(module.getId(), module);
                item.addSubModule(module);
            }
            module.setLocaleNames(this.convertI18n(attributes, module.getName(), module.getId(), "module"));
            module.setExtendAttributes(this.evalExtendAttribute(attributes));
            this.traceStack.push(module);
        }
        else if(name.toLowerCase().equals("description"))
        {
        	if(!this.isjob())
        	{
        		
	            Object obj = traceStack.peek();
	
	        	 if(obj instanceof String)
	        	 {
	                 this.localeDescriptions = this.convertI18n(attributes, null, "system[" + this.systemid + "]", "sysmenu.description");
	        	 }
	             else
	             {
	                 Module obuj = (Module) obj;
	                 obuj.setLocaleDescriptions(this.convertI18n(attributes, null, obuj.getId(), "module.description"));
	             }
        	}
        }
        else if(name.toLowerCase().equals("item"))
        {        	
        	String id = attributes.getValue("id");
        	
        	if(id != null)
        	{
        		if(id.equals("jobmanage"))
        		{
        			boolean enablejobfunction = ConfigManager.getInstance().getConfigBooleanValue("enablejobfunction",true);
        			if(!enablejobfunction)
        			{
        				isjob = true;
        				return;
        			}
        		}
        	}
            Item item = new Item();
            item.setSubSystem(ownersubsystem);
            String showleftmenu = null;
            String option = null;
            if(attributes != null)
            {
            	String desktop_width=attributes.getValue("desktop_width");
            	String desktop_height=attributes.getValue("desktop_height");
                item.setName(attributes.getValue("name"));
                item.setId(attributes.getValue("id"));
                item.setArea(attributes.getValue("area"));
                item.setUsed(StringUtil.getBoolean(attributes.getValue("used"),true));
                //item.setHeadimg(attributes.getValue("headimg"));
                //设置缺省的栏目
                item.setIsdefault(StringUtil.getBoolean(attributes.getValue("default"),false));
                item.setIsMain(StringUtil.getBoolean(attributes.getValue("main"),false));
                item.setShowhidden(attributes.getValue("showhidden"));
                item.setTarget(attributes.getValue("target"));
                if(desktop_height != null&& !desktop_height.equals(""))
                		item.setDesktop_height(desktop_height);
                if(desktop_width != null&& !desktop_width.equals(""))
                		item.setDesktop_width(desktop_width);
                if(item.isIsdefault())
                {
                    this.defaultItem = item;
                    item.setAncestor(attributes.getValue("ancestor"));
                }
                item.setShowpage(StringUtil.getBoolean(attributes.getValue("showpage"),false));
                showleftmenu = attributes.getValue("showleftmenu");
                if(showleftmenu != null && showleftmenu.equals("true"))
                	item.setShowleftmenu(true);
                option = attributes.getValue("option");
                if(option != null && !option.equals(""))
                	item.setOption(option);
                else
                	item.setOption("{}");
                item.setLocaleNames(this.convertI18n(attributes, item.getName(), item.getId(), "module"));
                item.setExtendAttributes(this.evalExtendAttribute(attributes));
            }
//            Module obuj = (Module)traceStack.peek();
            Object parent = traceStack.peek();
            String parentPath = "";
            if(!(parent instanceof String))
            {
                Module obuj = (Module) traceStack.peek();
                parentPath = obuj.getPath();
                if(showleftmenu == null )
                	item.setShowleftmenu(obuj.isShowleftmenu());
//                if(popup == null )
//                	item.setPopup(obuj.isPopup());
                	
                if(obuj.getCode() == 0)
                	item.setCode(2);
                else
                	item.setCode(0);
                if(!obuj.isUsed())
                	item.setUsed(false);
            	obuj.addItem(item);
                
            }
            else
            {
            	 
                 if(ownersubsystem != null)
                 {
                	 parentPath = Framework.getSuperMenu(ownersubsystem.getId());
                 }
                 else
                 {
                	 parentPath = Framework.getSuperMenu("module");
                 }
                this.items.addItem(item);
                this.menus.addMenuItem(item);
                if(showleftmenu == null )
                	item.setShowleftmenu(showrootleftmenu);
//                if(popup == null )
//                	item.setPopup(popuprootleftmenu);
                item.setCode(2);
            }
            item.setParentPath(parentPath);
            item.setPath(parentPath + "/" + item.getId() + "$item");
            this.indexs.put(item.getPath(),item);
            this.indexByIds.put(item.getId(), item);
            traceStack.push(item);
        }
        else if(name.toLowerCase().equals("url"))
        {
        	this.currentValue.setLength(0);
        }
        else if(name.toLowerCase().equals("navigator"))
        {
        	if(this.isjob())
        		return ;
            this.currentSet = "navigator";
            Item obuj = (Item)traceStack.peek();
            obuj.setNavigator_width(attributes.getValue("width"));
        }

        else if(name.toLowerCase().equals("workspace"))
        {
        	if(this.isjob())
        		return ;
            this.currentSet = "workspace";
            Item obuj = (Item)traceStack.peek();
            obuj.setWorkspace_height(attributes.getValue("height"));
        }

        else if(name.toLowerCase().equals("status"))
        {
        	if(this.isjob())
        		return ;
            this.currentSet = "status";
            
            
        }
        else if(name.equals("left"))
        {
        	if(this.isjob)
        		return;
            Item obuj = (Item)traceStack.peek();
            obuj.setLeft_cols(attributes.getValue("cols"));
        }
        else if(name.toLowerCase().equals("top"))
        {
        	if(this.isjob())
        		return ;
            this.currentSet = "top";
            Item obuj = (Item)traceStack.peek();
            obuj.setTop_height(attributes.getValue("height"));
        }
        
        else if(name.equals("content")
        		|| name.equals("toolbar")
        		)
        {
        	if(name.equals("content"))
			{
        		Item obuj = (Item)traceStack.peek();
        		this.evalExtendContent(obuj, attributes);
        		String logoimage = attributes.getValue("logoimage");
        		if(logoimage != null)
        			obuj.setLocalLogoimages(this.convertI18n(attributes, logoimage, obuj.getId(), "item.logoimage"));
			}
        }
        else if(name.equals("mouseoutimg"))
		{
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
            	Module tt = (Module)obuj;
            	tt.setLocaleMouseoutimgs(this.convertI18n(attributes, null, tt.getId(), "module.mouseoutimg"));
            }
            else if(obuj instanceof Item)
            {
            	Item tt = (Item)obuj;
            	tt.setLocaleMouseoutimgs(this.convertI18n(attributes, null, tt.getId(), "item.mouseoutimg"));
                
            }
		}
        else if(name.equals("headimg") )
		{
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
            	Module tt = (Module)obuj;
            	tt.setLocaleHeadimgs(this.convertI18n(attributes, null, tt.getId(), "module.headimg"));
            }
            else if(obuj instanceof Item)
            {
            	Item tt = (Item)obuj;
            	tt.setLocaleHeadimgs(this.convertI18n(attributes, null, tt.getId(), "item.headimg"));
                
            }
		}
        else if( name.equals("mouseupimg"))
		{
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
            	Module tt = (Module)obuj;
            	tt.setLocaleMouseupimgs(this.convertI18n(attributes, null, tt.getId(), "module.mouseupimg"));
            }
            else if(obuj instanceof Item)
            {
            	Item tt = (Item)obuj;
            	tt.setLocaleMouseupimgs(this.convertI18n(attributes, null, tt.getId(), "item.mouseupimg"));
                
            }
		}
        else if(name.equals("mouseclickimg"))
		{
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
            	Module tt = (Module)obuj;
            	tt.setLocaleMouseclickimgs(this.convertI18n(attributes, null, tt.getId(), "module.mouseclickimg"));
            }
            else if(obuj instanceof Item)
            {
            	Item tt = (Item)obuj;
            	tt.setLocaleMouseclickimgs(this.convertI18n(attributes, null, tt.getId(), "item.mouseclickimg"));
                
            }
		}
        else if(name.equals("mouseoverimg"))
		{
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
            	Module tt = (Module)obuj;
            	tt.setLocaleMouseoverimgs(this.convertI18n(attributes, null, tt.getId(), "module.mouseoverimg"));
            }
            else if(obuj instanceof Item)
            {
            	Item tt = (Item)obuj;
            	tt.setLocaleMouseoverimgs(this.convertI18n(attributes, null, tt.getId(), "item.mouseoverimg"));
                
            }
		}
        else if(name.equals("authoration") || name.equals("title"))
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

        //如果是module、item、sysmenu节点，执行弹栈操作
        if (name.equals("module")
                || name.equals("item")
                || name.equals("sysmenu")
                || name.toLowerCase().equals("publicitem")) {
        	if(isjob)
        	{
        		isjob = false;
        		return;
        	}
        	if(name.equals("item") || name.toLowerCase().equals("publicitem")){
        		Item item = (Item)this.traceStack.pop();
        		item.parserVarible(this.propertiesContext);
        	}
        	else
        	{
        		this.traceStack.pop();
        	}
        	
            
        }
        else if(name.equals("property"))
        {
        	
        }
        else if(name.toLowerCase().equals("url"))
        {
        	Object obj = this.traceStack.peek();
        	if(obj instanceof BaseMenuItem)
        	{
        		BaseMenuItem m = (BaseMenuItem)obj;
        		String url = this.currentValue.toString();
        		String[] urls = url.split(",");
        		for(int i = 0; i < urls.length; i ++)
        		{
        			m.addAuthorResource(urls[i].trim());
        		}
        		
        	}
        	this.currentValue.setLength(0);
        }
        else if (name.equals("description")) {
        	if(this.isjob())
        		return ;
            Object obj = traceStack.peek();
            if(obj instanceof String)
            {
                description = currentValue.toString().trim();
                evalDefaultMessage(this.localeDescriptions,description);
            }
            else
            {
                Module obuj = (Module) obj;
                obuj.setDescription(currentValue.toString().trim());
                evalDefaultMessage(obuj.getLocaleDescriptions(),obuj.getDescription());
            }
        }
        else if(name.equals("title"))
        {
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
                ((Module)obuj).setTitle(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Module)obuj).setLocaleTitles(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
            else if(obuj instanceof Item)
            {
                ((Item)obuj).setTitle(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Item)obuj).setLocaleTitles(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
            
        }

        else if(name.equals("mouseoverimg"))
        {
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
                ((Module)obuj).setMouseoverimg(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Module)obuj).setLocaleMouseoverimgs(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
            else if(obuj instanceof Item)
            {
                ((Item)obuj).setMouseoverimg(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Item)obuj).setLocaleMouseoverimgs(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
        }
        else if(name.equals("mouseupimg"))
        {
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
                ((Module)obuj).setMouseupimg(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Module)obuj).setLocaleMouseupimgs(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
            else if(obuj instanceof Item)
            {
                ((Item)obuj).setMouseupimg(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Item)obuj).setLocaleMouseupimgs(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
        }
        else if(name.equals("mouseoutimg"))
        {
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
                ((Module)obuj).setMouseoutimg(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Module)obuj).setLocaleMouseoutimgs(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
            else if(obuj instanceof Item)
            {
                ((Item)obuj).setMouseoutimg(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Item)obuj).setLocaleMouseoutimgs(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
        }
        else if(name.equals("mouseclickimg"))
        {
        	if(this.isjob())
        		return ;
            Object obuj = traceStack.peek();
            if(obuj instanceof Module)
            {
                ((Module)obuj).setMouseclickimg(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Module)obuj).setLocaleMouseclickimgs(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
            else if(obuj instanceof Item)
            {
                ((Item)obuj).setMouseclickimg(currentValue.toString().trim());
                Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
                ((Item)obuj).setLocaleMouseclickimgs(extendsAttributes);
            	this.evalDefaultMessage( extendsAttributes, currentValue.toString().trim());
            }
          
        }
        else if(name.equals("headimg"))
        {
        	if(this.isjob())
        		return ;
        	BaseMenuItem obuj = (BaseMenuItem)traceStack.peek();
        	obuj.setHeadimg(currentValue.toString().trim());
        	Map<Locale,String> extendsAttributes = new HashMap<Locale,String>();
        	obuj.setLocaleHeadimgs(extendsAttributes);
        	this.evalDefaultMessage(obuj.getLocaleHeadimgs(), currentValue.toString().trim());
        }
        else if(name.equals("top"))
        {
        	if(this.isjob())
        		return ;
            Item obuj = (Item)traceStack.peek();
            obuj.setTop(currentValue.toString().trim());
        }

        else if(name.equals("menu"))
        {
            Item obuj = (Item)traceStack.peek();
            obuj.setMenu(currentValue.toString().trim());
        }
        else if(name.equals("main"))
        {
            Item obuj = (Item)traceStack.peek();
            obuj.setMain(currentValue.toString().trim());
        }
        else if(name.equals("left"))
        {
        	if(this.isjob())
        		return ;
            Item obuj = (Item)traceStack.peek();
            obuj.setLeft(currentValue.toString().trim());
        }

        else if(name.equals("bottom"))
        {
            Item obuj = (Item)traceStack.peek();
            obuj.setBottom(currentValue.toString().trim());
        }

        else if(currentSet != null && this.currentSet.equals("navigator") && name.equals("toolbar"))
        {
        	if(this.isjob())
        		return ;

            Item obuj = (Item)traceStack.peek();

            obuj.setNavigatorToolbar(currentValue.toString().trim());
        }
        else if(currentSet != null && this.currentSet.equals("navigator") && name.equals("content"))
        {

        	if(this.isjob())
        		return ;
            Item obuj = (Item) traceStack.peek();

            obuj.setNavigatorContent(currentValue.toString().trim());
        }

        else if(currentSet != null && this.currentSet.equals("workspace") && name.equals("toolbar"))
        {

        	if(this.isjob())
        		return ;
            Item obuj = (Item)traceStack.peek();

            obuj.setWorkspaceToolbar(currentValue.toString().trim());
        }
        else if(currentSet != null && this.currentSet.equals("workspace") && name.equals("content"))
        {
        	if(this.isjob())
        		return ;
            Item obuj = (Item) traceStack.peek();

            obuj.setWorkspaceContent(currentValue.toString().trim());
        }
        else if(currentSet != null && this.currentSet.equals("status") && name.equals("toolbar"))
        {
        	if(this.isjob())
        		return ;
            Item obuj = (Item) traceStack.peek();

            obuj.setStatusToolbar(currentValue.toString().trim());
        } else if (currentSet != null && this.currentSet.equals("status") && name.equals("content")) {
        	if(this.isjob())
        		return ;
            Item obuj = (Item) traceStack.peek();
            obuj.setStatusContent(currentValue.toString().trim());
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
    /**
     * @return Returns the left_width.
     */
    public String getLeft_width() {
        return left_width;
    }
    /**
     * @return Returns the navigator_width.
     */
    public String getNavigator_width() {
        return navigator_width;
    }
    /**
     * @return Returns the top_height.
     */
    public String getTop_height() {
        return top_height;
    }
    /**
     * @return Returns the workspace_height.
     */
    public String getWorkspace_height() {
        return workspace_height;
    }
    /**
     * @return Returns the publicItem.
     */
    public Item getPublicItem() {
        return publicItem;
    }

    public ItemQueue getItems() {
        return items;
    }

	public String getShowhidden() {
		return showhidden;
	}
	
	private String showhidden_width = "12";
	private Map subsystems;
	private List<SubSystem> subsystemList; 
	private String successRedirect;
	private String logoutredirect;


	public String getShowhidden_width() {
		return showhidden_width;
	}

	public Map getSubsystems() {
		// TODO Auto-generated method stub
		return subsystems;
	}

	public void setOwnersubsystem(SubSystem ownersubsystem) {
		this.ownersubsystem = ownersubsystem;
	}

	public String getGlobal_target()
	{
		return global_target;
	}

	public boolean isShowrootleftmenu() {
		return showrootleftmenu;
	}

	public Map<String,Locale> getLanguages() {
		return languages;
	}

	public Map<Locale, String> getLocaleDescriptions() {
		return localeDescriptions;
	}

	public MenuQueue getMenus() {
		return menus;
	}

	public void setMenus(MenuQueue menus) {
		this.menus = menus;
	}

	public List<SubSystem> getSubsystemList() {
		return subsystemList;
	}

	public String getSuccessRedirect() {
		return successRedirect;
	}

	public String getLogoutredirect() {
		return logoutredirect;
	}

}
