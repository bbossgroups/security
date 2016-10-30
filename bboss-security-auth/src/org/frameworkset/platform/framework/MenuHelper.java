package org.frameworkset.platform.framework;

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.security.AccessControlInf;
import org.frameworkset.spi.BaseApplicationContext;

import org.frameworkset.platform.security.AccessControl;
import com.frameworkset.util.StringUtil;



/**
 * <p>
 * Title: MenuHelper
 * </p>
 * 
 * <p>
 * Description: 提供生成权限菜单的功能，以及栏目框架地址的功能
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company:bboss
 * </p>
 * 
 * @author biaoping.yin
 * @version 1.0
 */

public class MenuHelper  {
    private Item firstItem;
    private Map<String,Integer> permissionIndexs;
    private Principal principal;

    private Map permissionMenuIndex;
    private AccessControlInf control;
    private ModuleQueue modules;
    private MenuQueue menus;
    private ItemQueue items;
    private List menuQueue;
    private String subsystem;
    private AuthMenuItemQueue menuitemQueue;
    public static final int VISIBLE_PERMISSION = 1;
    public static final int SUB_VISIBLE_PERMISSION = 1;
    public static final int UNVISIBLE_PERMISSION = 0;

    Framework framework;

    public MenuHelper(List menuQueue, Principal principal, Map permissionIndexs, String subsystem) {
        this.subsystem = subsystem;
        framework = Framework.getInstance(subsystem);
        this.menuQueue = menuQueue;
        if (permissionIndexs == null)
            this.permissionIndexs = new HashMap();
        else
            this.permissionIndexs = permissionIndexs;
        this.principal = principal;
        menuitemQueue = new AuthMenuItemQueue(menuQueue);
        permissionMenuIndex = new HashMap();
    }

    public MenuHelper(List menuQueue, Principal principal, Map permissionIndexs) {
        this(menuQueue, principal, permissionIndexs, "");
    }

    public MenuHelper(String subsystem, Principal principal) {
        this.subsystem = subsystem;
        framework = Framework.getInstance(subsystem);
        this.permissionIndexs = new HashMap();
        this.principal = principal;
        menuitemQueue = new AuthMenuItemQueue(null);
        permissionMenuIndex = new HashMap();
    }

    public MenuHelper(Principal principal) {
        this("", principal);
    }
    
    public static void main(String[] args)
    {
        Principal principal = new org.frameworkset.platform.security.authorization.AuthPrincipal("userName",null,null);
        MenuHelper menuHelper = new MenuHelper("5", principal);
    }
    
    public MenuHelper(List menuQueue, AccessControlInf control, Map permissionIndexs, String subsystem) {
        this.subsystem = subsystem;
        framework = Framework.getInstance(subsystem);
        this.menuQueue = menuQueue;
        if (permissionIndexs == null)
            this.permissionIndexs = new HashMap();
        else
            this.permissionIndexs = permissionIndexs;
        this.control = control;
        menuitemQueue = new AuthMenuItemQueue(menuQueue);
        permissionMenuIndex = new HashMap();
    }

    public MenuHelper(List menuQueue, AccessControlInf control, Map permissionIndexs) {
        this(menuQueue, control, permissionIndexs, "");
    }

    public MenuHelper(String subsystem, AccessControlInf control) {
        this.subsystem = subsystem;
        framework = Framework.getInstance(subsystem);
        this.permissionIndexs = new HashMap();
        this.control = control;
        menuitemQueue = new AuthMenuItemQueue(null);
        permissionMenuIndex = new HashMap();
        
    }
  

    public MenuHelper(AccessControlInf control) {
        this("", control);
    }

    /**
     * 根据hash表中外部参数构造url的参数查询串
     * 
     * @param externalparams
     * @return
     */
    public static String getExternalQueryString(Map externalparams) {
        if (externalparams == null)
            return "";

        StringBuilder external_params = new StringBuilder();
        Set keys = externalparams.keySet();
        Iterator iterator = keys.iterator();
        boolean flag = false;
        while (iterator.hasNext()) {
            String key = (String)iterator.next();
            if (!flag) {
                external_params.append(key).append("=").append(externalparams.get(key));
                flag = true;
            } else {
                external_params.append("&").append(key).append("=").append(externalparams.get(key));
            }
        }
        if (flag) {
            return Framework.EXTERNAL_PARAMS_KEY + "=" + StringUtil.encode(external_params.toString());
        } else {
            return "";
        }
    }

    public static String getRootUrl(String context, String menuPath, Map externalparams) {
        return getRootUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取菜单的根url地址，通过本地址将生成包含整个栏目所配置的块的系统框架
     * 
     * @param context
     * @param menu
     * @param params
     * @return
     */
    public static String getRootUrl(String context, String menuPath, Map externalparams, String subsystem) {
        StringBuilder url = new StringBuilder();
        if (context != null && !"".equals(context)) {
            url.append(context).append("/");
        }
        url.append("main.frame?").append(Framework.MENU_TYPE).append("=").append(Framework.ROOT_CONTAINER);
        url.append("&").append(Framework.MENU_PATH).append("=").append(menuPath);
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static MenuItem getMenu(String path) {
        int idx = path.indexOf("::");
        String subsystem = path.substring(0, idx);
        return Framework.getInstance(subsystem).getMenu(path);
    }
    
    
    public MenuItem getCurrentSystemMenu(String modulePath) {
        
    	 MenuItem module = (MenuItem)permissionMenuIndex.get(modulePath);

         if (module == null) {
             module = framework.getCurrentSystemMenu(modulePath);
             if (module == null) {
                 //System.out.println("modulePath=[" + modulePath + "]的菜单项是不存在。");
                 return null;
             } else if (module instanceof Item) {
                 //System.out.println("modulePath=[" + modulePath + "]的菜单项是一个item，不能再包含item");
            	 AuthorItem am = new AuthorItem((Item)module);
                 permissionMenuIndex.put(am.getPath(), am); // 可能会有问题
                 return am;
             } else if (module instanceof Module) {
                 AuthorModule am = new AuthorModule((Module)module);
                 permissionMenuIndex.put(am.getPath(), am); // 可能会有问题
                 return am;
             } else
                 return module;
         } else {
             return module;
         }
        
    }
    
    public MenuItem getCurrentSystemMenu(MenuItem menu) {
        //System.out.println("menu----------------------" + menu);
        //System.out.println("permissionMenuIndex----------------------" + permissionMenuIndex);
   	 	MenuItem module = (MenuItem)permissionMenuIndex.get(menu.getPath());

        if (module == null) {
            module = menu;
            if (module == null) {
                //System.out.println("modulePath=[" + modulePath + "]的菜单项是不存在。");
                return null;
            } else if (module instanceof Item) {
                //System.out.println("modulePath=[" + modulePath + "]的菜单项是一个item，不能再包含item");
           	 	AuthorItem am = new AuthorItem((Item)module);
                permissionMenuIndex.put(am.getPath(), am); // 可能会有问题
                return am;
            } else if (module instanceof Module) {
                AuthorModule am = new AuthorModule((Module)module);
                permissionMenuIndex.put(am.getPath(), am); // 可能会有问题
                return am;
            } else
                return module;
        } else {
            return module;
        }
       
   }

    public static String getMainUrl(String context, String menuPath, Map externalparams) {
        return getMainUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取主框架url地址，通过本地址生成的系统框架包含leftside menu、navigaitor、workspace以及status块的系统框架
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getMainUrl(String context, String menuPath, Map externalparams, String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.MAIN_CONTAINER_URL)).append(
        // Framework.MENU_PATH).append("=")
        // .append(StringUtil.encode(menuPath))
        // .append("&")
        // .append(Framework.MENU_TYPE)
        // .append("=")
        // .append(Framework.MAIN_CONTAINER);
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }

        return getMainUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getMainUrl(String context, String menuPath, Map externalparams, String subsystem,
                                    String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.MAIN_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menuPath)).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.MAIN_CONTAINER);
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }

        return url.toString();
    }

    public static String getPerspectiveContentUrl(String context, String menuPath, Map externalparams) {
        return getPerspectiveContentUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取主工作区的url地址，通过本地址生成的系统框架包含navigator，workspace和status块的系统框架
     * 
     * @param context
     * @param menu
     * @param params
     * @return
     */
    public static String getPerspectiveContentUrl(String context, String menuPath, Map externalparams,
                                                  String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.CONTENT_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append( Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.CONTENT_CONTAINER);
        //        
        // // 外部参数，可以传递到各框架中,"external_params="
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getPerspectiveContentUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getPerspectiveContentUrl(String context, String menuPath, Map externalparams,
                                                  String subsystem, String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.CONTENT_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menuPath)).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.CONTENT_CONTAINER);

        // 外部参数，可以传递到各框架中,"external_params="
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getActionContainerUrl(String context, String menuPath, Map externalparams) {
        return getActionContainerUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取actioncontainer地址，通过本地址将生成包含workspace和status块的系统框架
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getActionContainerUrl(String context, String menuPath, Map externalparams,
                                               String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.ACTION_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append( Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.ACTION_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getActionContainerUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getActionContainerUrl(String context, String menuPath, Map externalparams,
                                               String subsystem, String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.ACTION_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menuPath)).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.ACTION_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getWorkspaceUrl(String context, String menuPath, Map externalparams) {
        return getWorkspaceUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取工作区页面地址，通过该地址可以方便地
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getWorkspaceUrl(String context, String menuPath, Map externalparams, String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.PROPERTIES_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append(Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.PROPERTIES_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getWorkspaceUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getWorkspaceUrl(String context, String menuPath, Map externalparams,
                                         String subsystem, String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.PROPERTIES_CONTAINER_URL, sessionid),true);
        url.append(murl)
            .append(Framework.MENU_PATH).append("=").append(StringUtil.encode(menuPath)).append("&")
            .append(Framework.MENU_TYPE).append("=").append(Framework.PROPERTIES_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getStatusUrl(String context, String menuPath, Map externalparams) {
        return getStatusUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取工作区页面地址，通过该地址可以方便地
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getStatusUrl(String context, String menuPath, Map externalparams, String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append( Framework.getUrl(Framework.STATUS_CONTAINER_URL))
        // .append( Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append( Framework.MENU_TYPE )
        // .append( "=" )
        // .append( Framework.STATUS_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getStatusUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getStatusUrl(String context, String menuPath, Map externalparams, String subsystem,
                                      String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.STATUS_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menuPath)).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.STATUS_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getRootUrl(String context, String menuPath, String externalparams) {
        return getRootUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取菜单的根url地址，通过本地址将生成包含整个栏目所配置的块的系统框架
     * 
     * @param context
     * @param menu
     * @param params
     * @return
     */
    public static String getRootUrl(String context, String menuPath, String externalparams, String subsystem) {
        StringBuilder url = new StringBuilder();
        if (context != null && !"".equals(context)) {
            url.append(context).append("/");
        }
        url.append("main.frame?" + Framework.MENU_TYPE + "=" + Framework.ROOT_CONTAINER);
        url.append("&").append(Framework.MENU_PATH).append("=").append(menuPath);
        if (externalparams != null && !externalparams.equals("")) {
            url.append("&").append(Framework.EXTERNAL_PARAMS_KEY).append("=")
                .append(StringUtil.encode(externalparams));
        }
        return url.toString();
    }

    public static String getMainUrl(String context, String menuPath, String externalparams) {
        return getMainUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取主框架url地址，通过本地址生成的系统框架包含leftside menu、navigaitor、workspace以及status块的系统框架
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getMainUrl(String context, String menuPath, String externalparams, String subsystem) {

        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.MAIN_CONTAINER_URL)).append(
        // Framework.MENU_PATH).append("=")
        // .append(StringUtil.encode(menuPath))
        // .append("&")
        // .append(Framework.MENU_TYPE)
        // .append("=")
        // .append(Framework.MAIN_CONTAINER);
        // if(externalparams != null && !externalparams.equals(""))
        // {
        // url.append("&")
        // .append(Framework.EXTERNAL_PARAMS_KEY).append("=")
        // .append(StringUtil.encode(externalparams));
        // }
        // return url.toString();
        return getMainUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getMainUrl(String context, String menuPath, String externalparams, String subsystem,
                                    String sessionid) {

        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.MAIN_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menuPath)).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.MAIN_CONTAINER);
        if (externalparams != null && !externalparams.equals("")) {
            url.append("&").append(Framework.EXTERNAL_PARAMS_KEY).append("=")
                .append(StringUtil.encode(externalparams));
        }
        return url.toString();
    }

    public static String getPerspectiveContentUrl(String context, String menuPath, String externalparams) {
        return getPerspectiveContentUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取主工作区的url地址，通过本地址生成的系统框架包含navigator，workspace和status块的系统框架
     * 
     * @param context
     * @param menu
     * @param params
     * @return
     */
    public static String getPerspectiveContentUrl(String context, String menuPath, String externalparams,
                                                  String subsystem, String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.CONTENT_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menuPath)).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.CONTENT_CONTAINER);

        // 外部参数，可以传递到各框架中,"external_params="
        if (externalparams != null && !externalparams.equals("")) {
            url.append("&").append(Framework.EXTERNAL_PARAMS_KEY).append("=")
                .append(StringUtil.encode(externalparams));
        }
        return url.toString();
    }

    public static String getPerspectiveContentUrl(String context, String menuPath, String externalparams,
                                                  String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.CONTENT_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append( Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.CONTENT_CONTAINER);
        //        
        // // 外部参数，可以传递到各框架中,"external_params="
        // if(externalparams != null && !externalparams.equals(""))
        // {
        // url.append("&")
        // .append(Framework.EXTERNAL_PARAMS_KEY).append("=")
        // .append(StringUtil.encode(externalparams));
        // }
        // return url.toString();
        return getPerspectiveContentUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getNavigatorContainerUrl(String context, String menuPath, String externalparams) {
        return getNavigatorContainerUrl(context, menuPath, externalparams, "");
    }

    /**
     * --新增 gao.tang 20080916
     * 
     * @param context
     * @param menuPath
     * @param externalparams
     * @param subsystem
     * @return
     */
    public static String getNavigatorContainerUrl(String context, String menuPath, Map externalparams) {
        return getNavigatorContainerUrl(context, menuPath, externalparams, "");
    }

    /**
     * --新增 gao.tang 20080916
     * 
     * @param context
     * @param menuPath
     * @param externalparams
     * @param subsystem
     * @return
     */
    public static String getNavigatorContainerUrl(String context, String menuPath, Map externalparams,
                                                  String subsystem) {
        String external_params = "";
        if (externalparams != null) {
            external_params = getExternalQueryString(externalparams);
        }
        return getNavigatorContainerUrl(context, menuPath, external_params, subsystem);
    }

    /**
     * 获取actioncontainer地址，通过本地址将生成包含workspace和status块的系统框架
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getNavigatorContainerUrl(String context, String menuPath, String externalparams,
                                                  String subsystem) {

        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.NAVIGATOR_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append(Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.NAVIGATOR_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        // if(externalparams != null && !externalparams.equals(""))
        // {
        // url.append("&")
        // .append(Framework.EXTERNAL_PARAMS_KEY).append("=")
        // .append(StringUtil.encode(externalparams));
        // }
        // return url.toString();
        return getNavigatorContainerUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getNavigatorContainerUrl(String context, String menuPath, String externalparams,
                                                  String subsystem, String sessionid) {

        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.NAVIGATOR_CONTAINER_URL, sessionid),true);
        url.append(murl)
            .append(Framework.MENU_PATH).append("=").append(StringUtil.encode(menuPath)).append("&")
            .append(Framework.MENU_TYPE).append("=").append(Framework.NAVIGATOR_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="
        if (externalparams != null && !externalparams.equals("")) {
            url.append("&").append(Framework.EXTERNAL_PARAMS_KEY).append("=")
                .append(StringUtil.encode(externalparams));
        }
        return url.toString();
    }

    public static String getActionContainerUrl(String context, String menuPath, String externalparams) {
        return getActionContainerUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取actioncontainer地址，通过本地址将生成包含workspace和status块的系统框架
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getActionContainerUrl(String context, String menuPath, String externalparams,
                                               String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.ACTION_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append( Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.ACTION_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        // if(externalparams != null && !externalparams.equals(""))
        // {
        // url.append("&")
        // .append(Framework.EXTERNAL_PARAMS_KEY).append("=")
        // .append(StringUtil.encode(externalparams));
        // }
        // return url.toString();
        return getActionContainerUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getActionContainerUrl(String context, String menuPath, String externalparams,
                                               String subsystem, String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.ACTION_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menuPath)).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.ACTION_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="
        if (externalparams != null && !externalparams.equals("")) {
            url.append("&").append(Framework.EXTERNAL_PARAMS_KEY).append("=")
                .append(StringUtil.encode(externalparams));
        }
        return url.toString();
    }

    public static String getWorkspaceUrl(String context, String menuPath, String externalparams) {
        return getWorkspaceUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取工作区页面地址，通过该地址可以方便地
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getWorkspaceUrl(String context, String menuPath, String externalparams,
                                         String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.PROPERTIES_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append(Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.PROPERTIES_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        //        
        // if(externalparams != null && !externalparams.equals(""))
        // {
        // url.append("&")
        // .append(Framework.EXTERNAL_PARAMS_KEY).append("=")
        // .append(StringUtil.encode(externalparams));
        // }
        // return url.toString();
        return getWorkspaceUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getWorkspaceUrl(String context, String menuPath, String externalparams,
                                         String subsystem, String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.PROPERTIES_CONTAINER_URL, sessionid),true);
        url.append(murl)
            .append(Framework.MENU_PATH).append("=").append(StringUtil.encode(menuPath)).append("&")
            .append(Framework.MENU_TYPE).append("=").append(Framework.PROPERTIES_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="

        if (externalparams != null && !externalparams.equals("")) {
            url.append("&").append(Framework.EXTERNAL_PARAMS_KEY).append("=")
                .append(StringUtil.encode(externalparams));
        }
        return url.toString();
    }

    public static String getStatusUrl(String context, String menuPath, String externalparams) {
        return getStatusUrl(context, menuPath, externalparams, "");
    }

    /**
     * 获取工作区页面地址，通过该地址可以方便地
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getStatusUrl(String context, String menuPath, String externalparams, String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.STATUS_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menuPath) )
        // .append( "&")
        // .append(Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.STATUS_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        //        
        // if(externalparams != null && !externalparams.equals(""))
        // {
        // url.append("&")
        // .append(Framework.EXTERNAL_PARAMS_KEY).append("=")
        // .append(StringUtil.encode(externalparams));
        // }
        // return url.toString();
        return getStatusUrl(context, menuPath, externalparams, subsystem, null);
    }

    public static String getStatusUrl(String context, String menuPath, String externalparams,
                                      String subsystem, String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.STATUS_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menuPath)).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.STATUS_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="

        if (externalparams != null && !externalparams.equals("")) {
            url.append("&").append(Framework.EXTERNAL_PARAMS_KEY).append("=")
                .append(StringUtil.encode(externalparams));
        }
        return url.toString();
    }

    public static String getRootUrl(String context, MenuItem menu, Map externalparams) {
        return getRootUrl(context, menu, externalparams, "");
    }

    /**
     * 获取菜单的根url地址，通过本地址将生成包含整个栏目所配置的块的系统框架
     * 
     * @param context
     * @param menu
     * @param params
     * @return
     */
    public static String getRootUrl(String context, MenuItem menu, Map externalparams, String subsystem) {
        StringBuilder url = new StringBuilder();
        if (context != null && !"".equals(context)) {
            url.append(context).append("/");
        }
        url.append("main.frame?").append(Framework.MENU_TYPE).append("=").append(Framework.ROOT_CONTAINER);
        url.append("&").append(Framework.MENU_PATH).append("=").append(menu.getPath());
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getMainUrl(MenuItem menu) {
        return getMainUrl(null, menu, (Map)null);
    }
    
    public static String getMainUrl(String contextpath,MenuItem menu) {
        return getMainUrl(contextpath, menu, (Map)null);
    }

    public static String getMainUrl(String menuPath) {

        return getMainUrl(null, MenuHelper.getMenu(menuPath), (Map)null);
    }

    public static String getMainUrl(String context, MenuItem menu, Map externalparams) {
        return getMainUrl(context, menu, externalparams, "");
    }
    
    public static String getRealUrl(String context, String url,String... params) {
    	if(params != null)
    	{
	    	if(params.length == 1 )
	    	{
	    		if(url.indexOf("?") > 0)
	    			url = url + "&" + params[0]; 
	    		else
	    			url = url + "?" + params[0];
	    	}
	    	else if(params.length > 1)
	    	{
	    		StringBuilder data = new StringBuilder();
	    		if(url.indexOf("?") > 0)
	    			data.append(url).append("&").append(params[0]).append("=").append(params[1]); 
	    		else
	    			data.append(url).append("?").append(params[0]).append("=").append(params[1]);
	    		for(int i =2; i < params.length; )
	    		{
	    			data.append("&").append(params[i]).append("=").append(params[i + 1]);
	    			i = i + 2;
	    		}
	    		url = data.toString();
	    	}
    	}    		
    		
//        if(url.startsWith("http://") || url.startsWith("https://") ||url.startsWith("ftp://") || url.startsWith("tps://"))
//            return url;
    	if(StringUtil.isHttpUrl(url))
    		return url;
        return StringUtil.getRealPath(context, url,true);
        
    }

    /**
     * 获取主框架url地址，通过本地址生成的系统框架包含leftside menu、navigaitor、workspace以及status块的系统框架
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getMainUrl(String context, MenuItem menu, Map externalparams, String subsystem) {

        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.MAIN_CONTAINER_URL))
        // .append(Framework.MENU_PATH).append("=")
        // .append(StringUtil.encode(menu.getPath(),null))
        // .append("&")
        // .append(Framework.MENU_TYPE)
        // .append("=")
        // .append(Framework.MAIN_CONTAINER);
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getMainUrl(context, menu, externalparams, subsystem, null);
    }

    public static String getMainUrl(String context, MenuItem menu, Map externalparams, String subsystem,
                                    String sessionid) {

        StringBuilder url = new StringBuilder();
        String murl = Framework.getUrl(Framework.MAIN_CONTAINER_URL, sessionid);
        murl = StringUtil.getRealPath(context, murl, true);
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
//        else
//        {
//        	
//        }
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menu.getPath(), null)).append("&")
            .append(Framework.MENU_TYPE).append("=").append(Framework.MAIN_CONTAINER);
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getPerspectiveContentUrl(String context, MenuItem menu, Map externalparams) {
        return getPerspectiveContentUrl(context, menu, externalparams, "");
    }

    /**
     * 获取主工作区的url地址，通过本地址生成的系统框架包含navigator，workspace和status块的系统框架
     * 
     * @param context
     * @param menu
     * @param params
     * @return
     */
    public static String getPerspectiveContentUrl(String context, MenuItem menu, Map externalparams,
                                                  String subsystem) {

        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.CONTENT_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menu.getPath()) )
        // .append( "&")
        // .append( Framework.MENU_TYPE )
        // .append( "=" )
        // .append( Framework.CONTENT_CONTAINER);
        //        
        // // 外部参数，可以传递到各框架中,"external_params="
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getPerspectiveContentUrl(context, menu, externalparams, subsystem, null);
    }

    public static String getPerspectiveContentUrl(String context, MenuItem menu, Map externalparams,
                                                  String subsystem, String sessionid) {

        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.CONTENT_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menu.getPath())).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.CONTENT_CONTAINER);

        // 外部参数，可以传递到各框架中,"external_params="
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getActionContainerUrl(String context, MenuItem menu, Map externalparams) {
        return getActionContainerUrl(context, menu, externalparams, "");
    }

    /**
     * 获取actioncontainer地址，通过本地址将生成包含workspace和status块的系统框架
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @param subsystem
     * @return
     */
    public static String getActionContainerUrl(String context, MenuItem menu, Map externalparams,
                                               String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.ACTION_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menu.getPath()) )
        // .append( "&")
        // .append(Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.ACTION_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getActionContainerUrl(context, menu, externalparams, subsystem, null);
    }

    public static String getActionContainerUrl(String context, MenuItem menu, Map externalparams,
                                               String subsystem, String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.ACTION_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menu.getPath())).append("&").append(Framework.MENU_TYPE)
            .append("=").append(Framework.ACTION_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getWorkspaceUrl(String context, MenuItem menu, Map externalparams) {
        return getWorkspaceUrl(context, menu, externalparams, "");
    }

    /**
     * 获取工作区页面地址，通过该地址可以方便地
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getWorkspaceUrl(String context, MenuItem menu, Map externalparams, String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append(Framework.getUrl(Framework.PROPERTIES_CONTAINER_URL))
        // .append( Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menu.getPath()) )
        // .append( "&")
        // .append( Framework.MENU_TYPE )
        // .append( "=" )
        // .append(Framework.PROPERTIES_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getWorkspaceUrl(context, menu, externalparams, subsystem, null);
    }

    public static String getWorkspaceUrl(String context, MenuItem menu, Map externalparams, String subsystem,
                                         String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.PROPERTIES_CONTAINER_URL, sessionid),true);
        url.append(murl)
            .append(Framework.MENU_PATH).append("=").append(StringUtil.encode(menu.getPath())).append("&")
            .append(Framework.MENU_TYPE).append("=").append(Framework.PROPERTIES_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public static String getStatusUrl(String context, MenuItem menu, Map externalparams) {
        return getStatusUrl(context, menu, externalparams, "");
    }

    /**
     * 获取工作区页面地址，通过该地址可以方便地
     * 
     * @param context
     * @param menu
     * @param externalparams
     * @return
     */
    public static String getStatusUrl(String context, MenuItem menu, Map externalparams, String subsystem) {
        // StringBuilder url = new StringBuilder();
        // if(context != null && !"".equals(context)){
        // url.append(context).append("/");
        // }
        // url.append( Framework.getUrl(Framework.STATUS_CONTAINER_URL))
        // .append(Framework.MENU_PATH )
        // .append( "=" )
        // .append(StringUtil.encode(menu.getPath(), null) )
        // .append( "&")
        // .append( Framework.MENU_TYPE )
        // .append( "=" )
        // .append( Framework.STATUS_CONTAINER);
        // // 外部参数，可以传递到各框架中,"external_params="
        // String external_params = getExternalQueryString(externalparams);
        // if(!external_params.equals(""))
        // {
        // url.append("&").append(external_params);
        // }
        // return url.toString();
        return getStatusUrl(context, menu, externalparams, subsystem, null);
    }

    public static String getStatusUrl(String context, MenuItem menu, Map externalparams, String subsystem,
                                      String sessionid) {
        StringBuilder url = new StringBuilder();
//        if (context != null && !"".equals(context)) {
//            url.append(context).append("/");
//        }
        String murl = StringUtil.getRealPath(context,Framework.getUrl(Framework.STATUS_CONTAINER_URL, sessionid),true);
        url.append(murl).append(Framework.MENU_PATH)
            .append("=").append(StringUtil.encode(menu.getPath(), null)).append("&")
            .append(Framework.MENU_TYPE).append("=").append(Framework.STATUS_CONTAINER);
        // 外部参数，可以传递到各框架中,"external_params="
        String external_params = getExternalQueryString(externalparams);
        if (!external_params.equals("")) {
            url.append("&").append(external_params);
        }
        return url.toString();
    }

    public void resetControl(AccessControlInf control) {
        this.control = control;
        this.permissionIndexs = new HashMap();
        menuitemQueue = new AuthMenuItemQueue(null);
        permissionMenuIndex = new HashMap();

    }

    public Iterator iterator() {
        return menuitemQueue.iterator();
    }

    public Item getFirstAuthorItem() {
        return this.firstItem;
    }

    public ModuleQueue getModules() {
        if (modules != null)
            return modules;
        return menuitemQueue.getModules();
    }

    public ItemQueue getItems() {

        if (items != null)
            return items;
        return menuitemQueue.getItems();
    }

    /**
     * 根据模块路径获取带权限的栏目
     * 
     * @param modulePath
     * @return
     */
    public ItemQueue getSubItems(String modulePath) {

        return menuitemQueue.getSubItems(modulePath);
    }
    public boolean isShowrootmenuleft()
    {
    	return this.framework.isShowrootleftmenu();
    }
    /**
     * 根据模块路径获取带权限的子模块，
     * 
     * @param modulePath
     * @return
     */
    public ModuleQueue getSubModules(String modulePath) {
        return menuitemQueue.getSubModules(modulePath);
    }

    public Module getModule(String path) {
//        Module m = (Module)this.permissionMenuIndex.get(path);
//        if (m != null)
//            return m;
//        else {
//            Module m1 = framework.getModule(path);
//            if(m1 == null)
//            	return null;
//            m = new AuthorModule(m1);
//            this.permissionMenuIndex.put(path, m);
//            return m;
//        }
    	return (Module)getMenuItem(path);
    }
    
    public MenuItem getMenuItem(String path) {
    	
    	MenuItem m = (MenuItem)this.permissionMenuIndex.get(path);
        if (m != null)
            return m;
        else 
        {
        	m = framework.getMenuByPath(path);
        	 if(m == null)
             	return null;
        	Integer permission = this.permissionIndexs.get(m.getId());
            if(permission != null)
            {
            	if(permission == UNVISIBLE_PERMISSION )
            	{
            		return null;
            	}
            }
           
            if(control.checkPermission(m.getId(), AccessControl.VISIBLE_PERMISSION,
                    AccessControl.COLUMN_RESOURCE))
            {
            	
            	if(m instanceof Module)
            	{
		            m = new AuthorModule((Module)m);
		            permissionMenuIndex.put(path, m);
		            permissionIndexs.put(m.getId(), VISIBLE_PERMISSION);
		            return m;
            	}
            	else if(m instanceof Item)
            	{
		            m = new AuthorItem((Item)m);
		            this.permissionMenuIndex.put(path, m);
		            permissionIndexs.put(m.getId(), VISIBLE_PERMISSION);
		            return m;
            	}
            	else
            		throw new java.lang.IllegalArgumentException(path+":不支持的菜单对象类型"+m.getClass().getCanonicalName());
            }
            else
            {
            	if(m instanceof Module)
            	{
            		AuthorModule amodule = new AuthorModule((Module)m);
		            ModuleQueue submoduleQueue = amodule.getSubModules();
		            if (submoduleQueue != null && submoduleQueue.size() > 0) {
		                permissionIndexs.put(amodule.getId(), SUB_VISIBLE_PERMISSION);
		                permissionMenuIndex.put(amodule.getPath(), amodule);
		                amodule.setUsesubpermission(true);
		                return amodule;
		                
		            }
		            ItemQueue items = amodule.getItems();
		            if (items != null && items.size() > 0) {
		                permissionIndexs.put(amodule.getId(), SUB_VISIBLE_PERMISSION);
		                permissionMenuIndex.put(amodule.getPath(), amodule);
		                amodule.setUsesubpermission(true);
		                return amodule;
		                 
		            }
            	}
            	 permissionIndexs.put(m.getId(), UNVISIBLE_PERMISSION);
            	return null;
            }
        }
       

    }

    public Item getItem(String path) {
//        Item item = (Item)this.permissionMenuIndex.get(path);
//        if (item == null) {
//        	Item temp = framework.getItem(path);
//        	if(temp != null)
//        	{
//	            item = new AuthorItem(temp);
//	            this.permissionMenuIndex.put(path, item);
//        	}
//        }
//        return item;
    	return (Item)getMenuItem(path);
    }

    class AuthMenuItemQueue {

        private List menuItemQueue;

        public AuthMenuItemQueue(List menuItemQueue) {
            this.menuItemQueue = menuItemQueue;
        }

        /**
         * 获取权限信息的模块队列
         * 
         * @return
         */
        public ModuleQueue getModules() {
        	if(modules != null)
        	{
        		return modules;
        	}
            modules = new ModuleQueue();
            if (framework.getModules() == null || framework.getModules().size() == 0)
                return modules;
            Iterator m = new MenuItemIterator(framework.getModules().getList().iterator());
            Module temp = null;
            while (m.hasNext()) {
                temp = (Module)m.next();
                // permissionMenuIndex.put(temp.getPath(),temp);
                modules.addModule(temp);
            }
            return modules;
        }
        
        
        /**
         * 获取权限信息的模块队列
         * 
         * @return
         */
        public MenuQueue getMenuItems() {
        	if(menus != null)
        		return menus;
            menus = new MenuQueue();
            if (framework.getMenus() == null || framework.getMenus().size() == 0)
                return menus;
            Iterator m = new MenuItemIterator(framework.getMenus().getList().iterator());
            MenuItem temp = null;
            while (m.hasNext()) {
                 temp = (MenuItem)m.next();
                // permissionMenuIndex.put(temp.getPath(),temp);
                menus.addMenuItem(temp);
            }
            return menus;
        }

        public Iterator iterator() {
            return new MenuItemIterator(menuItemQueue.iterator());
        }

        /**
         * 获取带权限的栏目队列
         * 
         * @return
         */
        public ItemQueue getItems() {
        	if(items != null)
        		return items;
            items = new ItemQueue();
            if (framework.getItems() == null || framework.getItems().size() == 0)
                return items;
            Iterator i = new MenuItemIterator(framework.getItems().getList().iterator());
            Item temp = null;
            while (i.hasNext()) {
                temp = (Item)i.next();
                // permissionMenuIndex.put(temp.getPath(),temp);
                items.addItem(temp);
            }
            return items;
        }

        /**
         * 
         * 根据模块路径获取带权限的栏目 修改：2007-4-5 zhuo.wang
         * 
         * @param modulePath
         * @return
         */
        public ItemQueue getSubItems(String modulePath) {
            if (modulePath == null
                || modulePath.equals(Framework.getSuperMenu(Framework.getSubsystemFromPath(modulePath)))) {
                return this.getItems();
            }
            // Module module = (Module)permissionMenuIndex.get(modulePath);
            MenuItem module = (MenuItem)permissionMenuIndex.get(modulePath);

            if (module == null) {
                module = framework.getCurrentSystemMenu(modulePath);
                if (module == null) {
                    System.out.println("modulePath=[" + modulePath + "]的菜单项是不存在。");
                    return new ItemQueue();
                } else if (module instanceof Item) {
                    System.out.println("modulePath=[" + modulePath + "]的菜单项是一个item，不能再包含item");
                    return new ItemQueue();
                } else if (module instanceof Module) {
                    AuthorModule am = new AuthorModule((Module)module);
                    permissionMenuIndex.put(am.getPath(), am); // 可能会有问题
                    return am.getItems();
                } else
                    return new ItemQueue();
            } else {
                if (module instanceof Item) {
                    System.out.println("modulePath=[" + modulePath + "]的菜单项是一个item，不能再包含item");
                    return new ItemQueue();
                } else if (module instanceof Module) {

                    return ((Module)module).getItems();
                } else
                    return new ItemQueue();
            }

        }

        /**
         * 根据模块路径获取带权限的子模块，
         * 
         * @param modulePath
         * @return
         */
        public ModuleQueue getSubModules(String modulePath) {
            if (modulePath == null
                || modulePath.equals(Framework.getSuperMenu(Framework.getSubsystemFromPath(modulePath)))) {
                return this.getModules();
            }
            Module module = (Module)permissionMenuIndex.get(modulePath);
            if (module == null) {
                MenuItem menuItem = null;
                menuItem = framework.getCurrentSystemMenu(modulePath);
                if (menuItem instanceof Module) {
                    module = (Module)menuItem;
                    AuthorModule am = new AuthorModule(module);
                    permissionMenuIndex.put(am.getPath(), am);
                    return am.getSubModules();
                } else {
                    return null;
                }
            } else
                return module.getSubModules();
        }
    }

    class MenuItemIterator implements Iterator {

        Iterator allData;
        MenuItem decorator;

        public MenuItemIterator(Iterator allData) {
            this.allData = allData;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {

            boolean found = false;
            while (allData.hasNext() && !found) {
                decorator = (MenuItem)allData.next();

                found = found();
            }

            if (!found)
                decorator = null;

            return found;
        }

        /**
         * 查找拥有可见权限的字模块或者栏目
         * 
         * @return boolean
         */
        private boolean found() {
            if (decorator instanceof Module) {
                int  permission = getModulePermission((Module)decorator);
                return permission == VISIBLE_PERMISSION || permission == SUB_VISIBLE_PERMISSION;
            } else if (decorator instanceof Item) {
            	int permission = this.getItemPermission((Item)decorator);
                boolean ret = permission == VISIBLE_PERMISSION;
                if (ret && firstItem == null && decorator instanceof Item && decorator.isUsed()) {
                    firstItem = (Item)decorator;
                }
                return ret;

            }
            return false;

        }
        
        private boolean checkPermission(String resid,String action,
                                       String restype)
        
        {
            if(control != null)
                return control.checkPermission(resid, action,
                                               restype);
            else
            {
                return AccessControl.checkPermission(principal, resid, action,
                                                     restype);
            }
        }

        private int getModulePermission(Module module) {
        	Integer permission = permissionIndexs.get(module.getId());
            if (permission != null)
            {
            	if(permission ==  VISIBLE_PERMISSION || permission ==  SUB_VISIBLE_PERMISSION)
            	{
            		this.decorator = (MenuItem) permissionMenuIndex.get(module.getPath());
            		if(decorator == null)
            		{
            			throw new java.lang.RuntimeException("菜单配置文件["+MenuHelper.this.framework.getConfigFile()+"]中存在多个id为"+module.getId()+"的菜单配置。");
            		}
            	}
                return permission;
            }
            AuthorModule amodule = new AuthorModule(module);
            if (checkPermission(module.getId(), AccessControl.VISIBLE_PERMISSION,
                    AccessControl.COLUMN_RESOURCE)) {
				permissionIndexs.put(module.getId(), VISIBLE_PERMISSION);
				permissionMenuIndex.put(module.getPath(), amodule);
				this.decorator = amodule;
				return VISIBLE_PERMISSION;
			}
            ModuleQueue submoduleQueue = amodule.getSubModules();
            if (submoduleQueue != null && submoduleQueue.size() > 0) {
                permissionIndexs.put(module.getId(), SUB_VISIBLE_PERMISSION);
                this.decorator = amodule;
                permissionMenuIndex.put(module.getPath(), amodule);
                amodule.setUsesubpermission(true);
                return SUB_VISIBLE_PERMISSION;
                // MenuItemIterator moduleIterator = new
                // MenuItemIterator(submoduleQueue.iterator());
                // while(moduleIterator.hasNext())
                // {
                // permissionIndexs.put(module.getId(),AccessControl.
                // VISIBLE_PERMISSION);
                // return AccessControl.VISIBLE_PERMISSION;
                // }
            }
            ItemQueue items = amodule.getItems();
            if (items != null && items.size() > 0) {
                permissionIndexs.put(module.getId(), SUB_VISIBLE_PERMISSION);
                permissionMenuIndex.put(module.getPath(), amodule);
                this.decorator = amodule;
                amodule.setUsesubpermission(true);
                return SUB_VISIBLE_PERMISSION;
                // MenuItemIterator moduleIterator = new
                // MenuItemIterator(items.iterator());
                // while(moduleIterator.hasNext())
                // {
                // permissionIndexs.put(module.getId(),AccessControl.
                // VISIBLE_PERMISSION);
                // return AccessControl.VISIBLE_PERMISSION;
                // }
            }
            
            permissionIndexs.put(module.getId(), UNVISIBLE_PERMISSION);
            
            return UNVISIBLE_PERMISSION;
            

        }

        private int getItemPermission(Item item) {
            
            Integer permission = permissionIndexs.get(item.getId());
            if (permission != null)
            {
            	if(permission ==  VISIBLE_PERMISSION )
            	{
            		this.decorator = (MenuItem) permissionMenuIndex.get(item.getPath());
            		if(decorator == null)
            		{
            			throw new java.lang.RuntimeException("菜单配置文件["+MenuHelper.this.framework.getConfigFile()+"]中存在多个id为"+item.getId()+"的菜单配置。");
            		}
            	}
                return permission;
            }
            if (checkPermission(item.getId(), AccessControl.VISIBLE_PERMISSION,
                                        AccessControl.COLUMN_RESOURCE)) {
                permissionIndexs.put(item.getId(), VISIBLE_PERMISSION);
                AuthorItem aitem = new AuthorItem(item);
                permissionMenuIndex.put(item.getPath(), aitem);
                this.decorator = aitem;
                return VISIBLE_PERMISSION;
            } else {
                permissionIndexs.put(item.getId(), UNVISIBLE_PERMISSION);
              
               
                return UNVISIBLE_PERMISSION;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#next()
         */
        public Object next() {
            if (decorator != null)
                return decorator;
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("remove(),未支持的操作");
        }
    }

    class AuthorModule extends Module {
        private Module module;
        private ModuleQueue submodules;
        private ItemQueue items;
        private MenuQueue menus;

        public AuthorModule(Module module) {
            this.module = module;
        }
        public String getOption() {
    		return module.getOption();
    	}
    	
        public MenuItem getParent() {
        	return getCurrentSystemMenu(this.module.getParent());
//            return this.module.getParent();
        }

        public void addSubModule(Module subModule) {
            throw new UnsupportedOperationException("addSubModule(Module subModule)");
        }

        public void addItem(Item item) {
            throw new UnsupportedOperationException("addItem(Item item)");
        }

        public String getDescription() {
            return this.module.getDescription();
        }
        public String getDescription(HttpServletRequest request) {
            return this.module.getDescription( request);
        }

        public String getId() {
            return this.module.getId();
        }

        public ItemQueue getItems() {
            if (items != null)
                return items;
            items = new ItemQueue();
            Iterator t = new AuthMenuItemQueue(this.module.getItems().getList()).iterator();
            while (t.hasNext()) {
                Item temp = (Item)t.next();
                this.items.addItem(temp);
            }
            return items;
        }

        public String getName() {
            return this.module.getName();
        }
        public String getName(HttpServletRequest request) {
            return this.module.getName(request);
        }

        public String getMouseclickimg() {
            return this.module.getMouseclickimg();
        }
        public String getMouseclickimg(HttpServletRequest request) {
            return this.module.getMouseclickimg( request);
        }

        public String getMouseoutimg() {
            return this.module.getMouseoutimg();
        }
        public String getMouseoutimg(HttpServletRequest request) {
            return this.module.getMouseoutimg( request);
        }

        public String getMouseoverimg() {
            return this.module.getMouseoverimg();
        }
        public String getMouseoverimg(HttpServletRequest request) {
            return this.module.getMouseoverimg( request);
        }

        public String getTitle() {
            return this.module.getTitle();
        }
    	public String getUrl() {
    		return this.module.getUrl();
    	}
        public String getTitle(HttpServletRequest request) {
            return this.module.getTitle(request);
        }

        public String getParentPath() {
            return this.module.getParentPath();
        }

        public String getPath() {
            return this.module.getPath();
        }

        public String getMouseupimg() {
            return this.module.getMouseupimg();
        }
        public String getMouseupimg(HttpServletRequest request) {
            return this.module.getMouseupimg( request);
        }

        public ModuleQueue getSubModules() {
            if (submodules != null)
                return submodules;
            else {
                submodules = new ModuleQueue();
                Iterator t = new AuthMenuItemQueue(this.module.getSubModules().getList()).iterator();

                while (t.hasNext()) {
                    Module temp = (Module)t.next();
                   
                    submodules.addModule(temp);
                }
                return submodules;
            }
        }

        public boolean isUsed() {
            return this.module.isUsed();
        }

        public String getHeadimg() {
            // TODO Auto-generated method stub
            return this.module.getHeadimg();
        }
        public String getHeadimg(HttpServletRequest request) {
            // TODO Auto-generated method stub
            return this.module.getHeadimg( request);
        }

        public int getCode() {
            return module.getCode();
        }

        public SubSystem getSubSystem() {
            return module.getSubSystem();
        }

        public String getTarget() {
            // TODO Auto-generated method stub
            return module.getTarget();
        }

        public boolean isShowpage() {
            return module.isShowpage();
        }
        public boolean isShowleftmenu() {
			// TODO Auto-generated method stub
			return module.isShowleftmenu();
		}

		@Override
		public Map<String, String> getExtendAttributes() {
			// TODO Auto-generated method stub
			return module.getExtendAttributes();
		}

		@Override
		public String getStringExtendAttribute(String name) {
			// TODO Auto-generated method stub
			return module.getStringExtendAttribute(name);
		}

		@Override
		public String getStringExtendAttribute(String name, String defaultValue) {
			// TODO Auto-generated method stub
			return module.getStringExtendAttribute(name, defaultValue);
		}
		private boolean inited = false;
		private boolean hasSon = false;
		public boolean hasSonOfModule()
		{
			if(inited)
				return hasSon;
			
			hasSon = (getSubModules() != null && getSubModules().size() > 0) ||
					(getItems() != null && getItems().size() > 0);
			inited = true;
			return hasSon;
					
		}
		@Override
		public boolean isUsesubpermission() {
			// TODO Auto-generated method stub
			return super.isUsesubpermission();
		}
		@Override
		public MenuQueue getMenus() {
			 if (menus != null)
	                return menus;
	            else {
	            	menus = new MenuQueue();
	                Iterator t = new AuthMenuItemQueue(this.module.getMenus().getList()).iterator();

	                while (t.hasNext()) {
	                	MenuItem temp = (MenuItem)t.next();
	                  
	                    menus.addMenuItem(temp);
	                }
	                return menus;
	            }
		}
		

    }
    

    class AuthorItem extends Item {
        Item item;

        public AuthorItem(Item item) {
            this.item = item;
        }

        public String getBottom() {
            return item.getBottom();
        }

        public String getId() {
            return item.getId();
        }
        public String getOption() {
    		return item.getOption();
    	}
        public String getLeft() {
            return item.getLeft();
        }

        public String getMain() {
            return item.getMain();
        }

        public String getMenu() {
            return item.getMenu();
        }

        // public List getModules() {
        // return modules;
        // }

        public String getName() {
            return item.getName();
        }
        
        public String getName(HttpServletRequest request) {
            return item.getName(request);
        }

        public String getTop() {
            return item.getTop();
        }

        public String getMouseclickimg() {
            return item.getMouseclickimg();
        }
        public String getMouseclickimg(HttpServletRequest request) {
            return item.getMouseclickimg( request);
        }

        public String getMouseoutimg() {
            return item.getMouseoutimg();
        }
        public String getMouseoutimg(HttpServletRequest request) {
            return item.getMouseoutimg( request);
        }

        public String getMouseoverimg() {
            return item.getMouseoverimg();
        }
        public String getMouseoverimg(HttpServletRequest request) {
            return item.getMouseoverimg( request);
        }

        public String getTitle() {
            return item.getTitle();
        }
        public String getTitle(HttpServletRequest request) {
            return item.getTitle( request);
        }

        public String getNavigatorToolbar() {
            return item.getNavigatorToolbar();
        }

        public String getNavigatorContent() {
            return item.getNavigatorContent();
        }

        public String getStatusContent() {
            return item.getStatusContent();
        }

        public String getStatusToolbar() {
            return item.getStatusToolbar();
        }

        public String getWorkspaceContent() {
            return item.getWorkspaceContent();
        }
        public String getWorkspacecontentExtendAttribute(String attribute) {
    		return item.getWorkspacecontentExtendAttribute(attribute);
    	}
        public String getWorkspaceToolbar() {
            return this.item.getWorkspaceToolbar();
        }

        public String getParentPath() {
            return this.item.getParentPath();
        }

        public boolean isIsdefault() {
            return this.item.isIsdefault();
        }

        public String getPath() {
            return this.item.getPath();
        }

        public String getMouseupimg() {
            return this.item.getMouseupimg();
        }
        
        public String getMouseupimg(HttpServletRequest request) {
            return this.item.getMouseupimg( request);
        }

        public String getAncestor() {
            return item.getAncestor();
        }

        public boolean isUsed() {
            return item.isUsed();
        }

        public MenuItem getParent() {
        	return getCurrentSystemMenu(item.getParent());
            
        }

        /**
         * @return Returns the left_cols.
         */
        public String getLeft_cols() {
            return item.getLeft_cols();
        }

        public String getHeadimg() {
            // TODO Auto-generated method stub
            return item.getHeadimg();
        }
        public String getHeadimg(HttpServletRequest request) {
            // TODO Auto-generated method stub
            return item.getHeadimg( request);
        }

        public String getNavigator_width() {
            return item.getNavigator_width();
        }

        public String getTop_height() {
            return item.getTop_height();
        }

        public String getWorkspace_height() {
            return item.getWorkspace_height();
        }

        public String getShowhidden() {
            return item.getShowhidden();
        }

        public int getCode() {
            // TODO Auto-generated method stub
            return item.getCode();
        }

        public SubSystem getSubSystem() {
            return item.getSubSystem();
        }

        public String getTarget() {
            return item.getTarget();
        }

        public boolean isShowpage() {
            return item.isShowpage();
        }

		@Override
		public boolean hasLeftVaribale() {
			// TODO Auto-generated method stub
			return item.hasLeftVaribale();
		}

		@Override
		public boolean hasTopVaribale() {
			// TODO Auto-generated method stub
			return item.hasTopVaribale();
		}

		@Override
		public boolean hasNavigatorToolbarVariables() {
			// TODO Auto-generated method stub
			return item.hasNavigatorToolbarVariables();
		}

		@Override
		public boolean hasNavigatorContentVariables() {
			// TODO Auto-generated method stub
			return item.hasNavigatorContentVariables();
		}

		@Override
		public boolean hasStatusContentVariables() {
			// TODO Auto-generated method stub
			return item.hasStatusContentVariables();
		}

		@Override
		public boolean hasStatusToolbarVariables() {
			// TODO Auto-generated method stub
			return item.hasStatusToolbarVariables();
		}

		@Override
		public boolean hasWorkspaceContentVariables() {
			// TODO Auto-generated method stub
			return item.hasWorkspaceContentVariables();
		}

		@Override
		public boolean hasWorkspaceToolbarVariables() {
			// TODO Auto-generated method stub
			return item.hasWorkspaceToolbarVariables();
		}

		@Override
		public void parserVarible(BaseApplicationContext propertiesContext) {
			// TODO Auto-generated method stub
			item.parserVarible( propertiesContext);
		}

		@Override
		public ItemUrlStruction getWorkspaceContentItemUrlStruction() {
			// TODO Auto-generated method stub
			return item.getWorkspaceContentItemUrlStruction();
		}

		@Override
		public ItemUrlStruction getWorkspaceToolbarItemUrlStruction() {
			// TODO Auto-generated method stub
			return item.getWorkspaceToolbarItemUrlStruction();
		}

		@Override
		public ItemUrlStruction getNavigatorContentItemUrlStruction() {
			// TODO Auto-generated method stub
			return item.getNavigatorContentItemUrlStruction();
		}

		@Override
		public ItemUrlStruction getNavigatorToolbarItemUrlStruction() {
			// TODO Auto-generated method stub
			return item.getNavigatorToolbarItemUrlStruction();
		}

		@Override
		public ItemUrlStruction getStatusContentItemUrlStruction() {
			// TODO Auto-generated method stub
			return item.getStatusContentItemUrlStruction();
		}

		@Override
		public ItemUrlStruction getStatusToolbarItemUrlStruction() {
			// TODO Auto-generated method stub
			return item.getStatusToolbarItemUrlStruction();
		}

		@Override
		public ItemUrlStruction getTopItemUrlStruction() {
			// TODO Auto-generated method stub
			return item.getTopItemUrlStruction();
		}

		@Override
		public ItemUrlStruction getLeftItemUrlStruction() {
			// TODO Auto-generated method stub
			return item.getLeftItemUrlStruction();
		}
		
		public String getDesktop_width() {
			return item.getDesktop_width();
		}


		public String getDesktop_height() {
			return item.getDesktop_height();
		}
		
		public String getArea()
		{
			return item.getArea();
		}

		
		public boolean isShowleftmenu() {
			// TODO Auto-generated method stub
			return item.isShowleftmenu();
		}
		
		@Override
		public Map<String, String> getExtendAttributes() {
			// TODO Auto-generated method stub
			return item.getExtendAttributes();
		}

		@Override
		public String getStringExtendAttribute(String name) {
			// TODO Auto-generated method stub
			return item.getStringExtendAttribute(name);
		}

		@Override
		public String getStringExtendAttribute(String name, String defaultValue) {
			// TODO Auto-generated method stub
			return item.getStringExtendAttribute(name, defaultValue);
		}


    }

    public Item getPublicItem() {
        return this.framework.getPublicItem();
    }
    public static final String MENUHELPER_REQUEST_ATTRIBUTE = "com.frameworkset.platform.framework.MENUHELPER_REQUEST_ATTRIBUTE";
    public static MenuHelper getMenuHelper(HttpServletRequest request)
    {
    	MenuHelper menuHelper = (MenuHelper)request.getAttribute(MENUHELPER_REQUEST_ATTRIBUTE);
    	if(menuHelper == null)
    	{
    		AccessControl control = AccessControl.getAccessControl();
    		menuHelper = new MenuHelper(
    				control.getCurrentSystemID(), control);
    		request.setAttribute(MENUHELPER_REQUEST_ATTRIBUTE,menuHelper);
    	}
    	return menuHelper;
    }
    
    public static MenuHelper getMenuHelper(HttpServletRequest request,boolean refreshCurrentSystemID)
    {
    	MenuHelper menuHelper = (MenuHelper)request.getAttribute(MENUHELPER_REQUEST_ATTRIBUTE);
    	if(menuHelper == null)
    	{
    		AccessControl control = AccessControl.getAccessControl();
    		if(refreshCurrentSystemID)
    		{
    			control.refreshCurrentSystemID(request);
    		}
    		menuHelper = new MenuHelper(
    				control.getCurrentSystemID(), control);
    		request.setAttribute(MENUHELPER_REQUEST_ATTRIBUTE,menuHelper);
    	}
    	return menuHelper;
    }

	public Framework getFramework() {
		return framework;
	}
	public final static String  menupath_menuid = "menupath_menuid";
	public final static String  menupath = "pdp_menupath";
	public final static String  selecturl = "pdp_selecturl";
	public final static String  selectedmodule = "pdp_selectedmodule";
	public static String getItemUrl(Item subitem,String contextpath,String framepath,AccessControl control)
	{
		return getItemUrl(subitem,contextpath,framepath,(String)null,control);
	}
	
	public static String getItemUrl(Item subitem,String contextpath,String framepath,String selecturl,AccessControl control)
	{
		String area = subitem.getArea();
		String url = null;
		if(!subitem.isShowleftmenu())
		{
			if(area != null && area.equals("main"))
			{
				java.util.Map<String,String> param = new HashMap<String,String>();
				param.put(menupath_menuid, subitem.getId());
				if(selecturl == null || selecturl.equals(""))
				{				
					
				}
				else
				{
					selecturl = java.net.URLEncoder.encode(selecturl);
					param.put(selecturl, selecturl);
				}
				url = MenuHelper.getMainUrl(contextpath, subitem,
						param);
			}
			else
			{
				if(selecturl == null || selecturl.equals(""))
				{				
					url = MenuHelper.getRealUrl(contextpath, 
							Framework.getWorkspaceContent(subitem,control),menupath_menuid,subitem.getId());
				}
				else
				{
//					selecturl = java.net.URLEncoder.encode(selecturl);
					url = MenuHelper.getRealUrl(contextpath, 
							selecturl,menupath_menuid,subitem.getId());
				}
				
				
			}
		}
		else
		{
			if(selecturl == null || selecturl.equals("")){
				url = new StringBuilder().append(framepath).append("?")
						 .append(menupath)
						 .append("=")
						 .append(subitem.getPath()).toString();
			}
			else
			{
				selecturl = java.net.URLEncoder.encode(selecturl);
				url = new StringBuilder().append(framepath).append("?")
						 .append(menupath)
						 .append("=")
						 .append(subitem.getPath()).append("&")
						 .append(selecturl)
						 .append("=")
						 .append(selecturl).toString();
			}
		}
		return url;
	}
	
	public static String getModuleUrl(Module subitem,String contextpath,AccessControl control)
	{
		String url = subitem.getUrl();
		if(url == null || url.equals(""))
			return null;
		url = MenuHelper.getRealUrl(contextpath, 
							url,menupath_menuid,subitem.getId());
		return url;
	}

	public MenuQueue getMenus() {
		if (menus != null)
            return menus;
        return menus = menuitemQueue.getMenuItems();
	}
	
}
