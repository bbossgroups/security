package org.frameworkset.platform.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

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
public class FrameworkConfiguration  {
    private static Logger log = Logger.getLogger(FrameworkConfiguration.class);

    private String configFile = "module.xml" ;
    private ConfigParser handler;
    private ModuleQueue modules;
    private MenuQueue menus; 
    /**
     * 一级items是否显示左侧菜单，true显示，false不显示
     */
    private boolean showrootleftmenu = false;
    private Map indexs;
	private String successRedirect;
	private String logoutredirect;
    /**
	 * 菜单id索引表
	 */
	private Map indexByIds;
    private String description;
    private Item defaultItem;
    private Item publicItem;
    private ItemQueue items;
    private String top_height="38";
    private String left_width="30";
    private String navigator_width="20%";
    private String workspace_height="75%";
    private String showhidden = "true";
    private String showhidden_width = "12";
    private String global_target = "perspective_content";
    private boolean isfile = false;

	private Map subsystems;
	private List<SubSystem> subsystemList; 
	private SubSystem ownersubsystem;
	private String messagesourcefiles;

	private String systemid;
	private Map<Locale,String> localeDescriptions;
	 private Map<String,Locale> languages;
	public Map<Locale, String> getLocaleDescriptions() {
		return localeDescriptions;
	}

	public void setLocaleDescriptions(Map<Locale, String> localeDescriptions) {
		this.localeDescriptions = localeDescriptions;
	}
	public Map<String,Locale> getLanguages() {
		return languages;
	}
	public void setLanguages(Map<String, Locale> languages) {
		this.languages = languages;
	}
	public String getMessagesourcefiles() {
		return messagesourcefiles;
	}

	public void setMessagesourcefiles(String messagesourcefiles) {
		this.messagesourcefiles = messagesourcefiles;
	}

    public FrameworkConfiguration(String configFile,boolean isfile,String systemid) {
        if(configFile != null && !configFile.trim().equals(""))
            this.configFile = configFile;
        this.isfile = isfile;
        this.systemid = systemid;
    }

    /** Load DataSource info from XML and create a Service for each entry set. */
    public void loadConfiguration() throws Exception {

        // first try XML
        try {
            parseXML();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            // then try deprecated properties
            log.error("\n** ERROR: Unable to find XML file " + configFile + ": " + ne);
            // don't try the props files anymore, it's been over a year
            //System.out.println("** WARNING: Attempting to use deprecated properties files\n");
            //this.datasources = parseProperties(PoolManConstants.PROPS_CONFIG_FILE);

        } catch (Exception e) {
            log.error("\n** ERROR: Unable to parse XML file " + configFile + ": " + e);
        }

    }

    
    private void parseXML()
            throws Exception {

            /* CHANGED TO USE JAXP */

            String url = "";
//            String parentFolder = BaseSPIManager.getProperty("menu.folder", "");
//            String configFile = parentFolder + "/" + this.configFile;
            if(!this.isfile)
            {
                URL confURL = FrameworkConfiguration.class.getClassLoader().getResource(configFile);
                if (confURL == null)
                    confURL = FrameworkConfiguration.class.getClassLoader().getResource("/" + configFile);
    
                if (confURL == null)
                    confURL = getTCL().getResource(configFile);
                if (confURL == null)
                    confURL = getTCL().getResource("/" + configFile);
                if (confURL == null)
                    confURL = ClassLoader.getSystemResource(configFile);
                if (confURL == null)
                    confURL = ClassLoader.getSystemResource("/" + configFile);
                
                if(confURL == null)
                {
                    url = System.getProperty("user.dir");
                    url += "/" + configFile;
                }
                else
                {
                    url = confURL.toString();
                }
            }
            else
            {
                url = configFile;
            }
            this.handler = new ConfigParser(url,this.systemid);
            handler.setLanguages(languages);
            handler.setOwnersubsystem(ownersubsystem);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
			try
			{
			    parser.parse(url, handler);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			this.showrootleftmenu = handler.isShowrootleftmenu();
            this.modules = handler.getModules();
            this.indexs = handler.getIndexs();
            this.indexByIds = handler.getIndexByIds();
            this.defaultItem = handler.getDefaultItem();
            this.description = handler.getDescription();
            this.top_height = handler.getTop_height();
            this.left_width = handler.getLeft_width();
            this.navigator_width = handler.getNavigator_width();
            this.workspace_height = handler.getWorkspace_height();
            this.publicItem = handler.getPublicItem();
            this.items = handler.getItems();
            menus = handler.getMenus(); 
            this.showhidden = handler.getShowhidden();
            showhidden_width = handler.getShowhidden_width();
            this.subsystems = handler.getSubsystems();
            this.subsystemList = handler.getSubsystemList();
            this.global_target =handler.getGlobal_target();
            this.messagesourcefiles = handler.getMessagesourcefiles();
            this.languages = handler.getLanguages();
            this.localeDescriptions = handler.getLocaleDescriptions();
            this.logoutredirect = handler.getLogoutredirect();
            this.successRedirect = handler.getSuccessRedirect();
    }

    private static ClassLoader getTCL()
        throws IllegalAccessException, InvocationTargetException
    {
        Method method = null;
        try
        {
            method = (java.lang.Thread.class).getMethod("getContextClassLoader", null);
        }
        catch(NoSuchMethodException e)
        {
            return null;
        }
        return (ClassLoader)method.invoke(Thread.currentThread(), null);
    }

    public ModuleQueue getModules() {
        return modules;
    }



    public Map getIndexs() {
        return indexs;
    }
    public Map getIndexByIds() {
		
		return indexByIds;
	}


    /**
     * getDefaultItem
     *
     * @return Item
     */
    public Item getDefaultItem() {
        return defaultItem;
    }

    public String getDescription() {
        return description;
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
		// TODO Auto-generated method stub
		return showhidden;
	}

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

	public MenuQueue getMenus() {
		return menus;
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
