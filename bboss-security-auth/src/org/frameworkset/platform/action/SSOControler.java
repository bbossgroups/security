package org.frameworkset.platform.action;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.frameworkset.platform.config.ConfigManager;
import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.MenuHelper;
import org.frameworkset.platform.framework.MenuItem;
import org.frameworkset.platform.framework.Module;
import org.frameworkset.platform.framework.SubSystem;
import org.frameworkset.platform.security.AccessControl;
import org.frameworkset.platform.security.PermissionModule;
import org.frameworkset.platform.security.authorization.AccessException;
import org.frameworkset.util.FileCopyUtils;
import org.frameworkset.util.I18NUtil;
import org.frameworkset.util.annotations.AssertDToken;
import org.frameworkset.util.annotations.AssertTicket;
import org.frameworkset.web.interceptor.AuthenticateFilter;
import org.frameworkset.web.servlet.ModelMap;
import org.frameworkset.web.servlet.support.RequestContextUtils;
import org.frameworkset.web.token.TokenStore;

import com.frameworkset.util.StringUtil;
 

public class SSOControler {

    private static Logger log = Logger.getLogger(SSOControler.class);
    private boolean enableuseraccountsso = false;
    public String sso(ModelMap model) {
    	model.addAttribute("enableuseraccountsso", new Boolean(enableuseraccountsso));
    	if(enableuseraccountsso)
    		return "path:sso";
    	else
    		return "path:ssofailed";
    }
    public String cookieLocale(String language,HttpServletResponse response,HttpServletRequest request)
	{
	
		
		
//		StringUtil.addCookieValue(request, response, "cookie.localkey", language, 3600 * 24);
//		
//		
//		if(language.equals("en_US"))
//		{
//			request.getSession().setAttribute("session.localkey",java.util.Locale.US);
//		}
//		else
//		{
//			request.getSession().setAttribute("session.localkey",java.util.Locale.CHINA);
//		}
		
		try {
			I18NUtil.setLocale(request, response, language);
		} catch (Exception e) {
			log.error("",e);
		}
//			loginPathCookie.setPath(request.getContextPath());
		
		return AccessControl.redirectpathloginPage;
	}
    private String getSuccessRedirect(String loginStyle, String subsystem) {
        StringBuilder ret = new StringBuilder();
        if (StringUtil.isEmpty(subsystem))
        {
        	subsystem = AccessControl.getDefaultSUBSystemID();
        }
        if (StringUtil.isEmpty(subsystem)) {

            if (loginStyle == null || loginStyle.equals("5") || loginStyle.equals("6")) {
                ret.append("sanydesktop/indexcommon.page");
            } else if (loginStyle.equals("1")) {
                ret.append("index.jsp?subsystem_id=").append(subsystem);
            } else if (loginStyle.equals("3")) {
                ret.append("sanydesktop/index.page");
            } else if (loginStyle.equals("2")) {
                ret.append("desktop/desktop1.page");
            } else if (loginStyle.equals("4")) {
                ret.append("sanydesktop/webindex.page");
            } else {
                ret.append("sanydesktop/indexcommon.page");
            }
        } else {
            if (subsystem.equals("cms")) {
                ret.append("index.jsp?subsystem_id=").append(subsystem);
                return ret.toString();
            }
            SubSystem sys = Framework.getSubSystem(subsystem);
            if (sys != null && !StringUtil.isEmpty(sys.getSuccessRedirect()))
                ret.append(sys.getSuccessRedirect());
            else {
                if (loginStyle == null || loginStyle.equals("5") || loginStyle.equals("6")) {
                    ret.append("sanydesktop/indexcommon.page");
                } else if (loginStyle.equals("1")) {
                    ret.append("index.jsp?subsystem_id=").append(subsystem);
                } else if (loginStyle.equals("3")) {
                    ret.append("sanydesktop/index.page");
                } else if (loginStyle.equals("2")) {
                    ret.append("desktop/desktop1.page");
                } else if (loginStyle.equals("4")) {
                    ret.append("sanydesktop/webindex.page");
                } else {
                    ret.append("sanydesktop/indexcommon.page");
                }
            }
        }
        return ret.toString();
    }

    public String login(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception,
            Exception {
        HttpSession session = request.getSession(false);
        

        String u = "", p = "", ck = "";
        boolean fromredirect = false;

        String successRedirect = request.getParameter(AuthenticateFilter.referpath_parametername);
        if (successRedirect != null) {
            if ((successRedirect.equals(request.getContextPath())
                    || successRedirect.equals(request.getContextPath() + "/jsp") || successRedirect.equals("login.jsp") || successRedirect
                        .equals("login.page"))) {
                successRedirect = null;
            } else {
                fromredirect = true;
                model.addAttribute("successRedirect", successRedirect);
            }

        }
        String language = request.getParameter("language");
        PermissionModule permissionModule = ConfigManager.getInstance().getPermissionModule();
        boolean enable_login_validatecode = ConfigManager.getInstance().getConfigBooleanValue(
                "enable_login_validatecode", true);

        model.addAttribute("enable_login_validatecode", enable_login_validatecode);
        String errorMessage = null;

        String userName = request.getParameter("userName");
        int expiredays = userName != null ? permissionModule.getUserPasswordDualTimeByUserAccount(
                userName) : 0;
        String expriedtime_ = "";
        Date expiretime = expiredays > 0 && userName != null ? permissionModule.getPasswordExpiredTimeByUserAccount(userName) : null;
        if (expiretime != null) {
            SimpleDateFormat dateformt = new SimpleDateFormat("yyyy-MM-dd");
            expriedtime_ = dateformt.format(expiretime);
            model.addAttribute("expriedtime_", expriedtime_);
            model.addAttribute("userName", userName);
            model.addAttribute("expiredays", expiredays);

        }
        String loginStyle = null;
        String system_id = null;
        if (language == null) {
            language = RequestContextUtils.getLocaleResolver(request).resolveLocaleCode(request);

        }
        else
        {
        	RequestContextUtils.getLocaleResolver(request).setLocale(request, response, language);
        }
        model.addAttribute("language", language);

        /*
         * if(language.equals("zh_CN")){
         * request.getSession().setAttribute("languageKey",
         * java.util.Locale.CHINA); } else if(language.equals("en_US")){
         * request.getSession().setAttribute("languageKey",
         * java.util.Locale.US); }
         */
        String loginPath = request.getParameter("loginPath");//登陆界面风格
        String subsystem_id = request.getParameter("subsystem_id");

        loginStyle = StringUtil.getCookieValue(request, "loginStyle");
        system_id = StringUtil.getCookieValue(request, "subsystem_id");

        if (loginPath != null) {
            StringUtil.addCookieValue(request, response, "loginStyle", loginPath);
            loginStyle = loginPath; 

        }
        if(loginStyle == null)
        	loginStyle = "5";
        if (subsystem_id != null) {
            StringUtil.addCookieValue(request, response, "subsystem_id", subsystem_id);

        }
        model.addAttribute("system_id", system_id);
        model.addAttribute("loginStyle", loginStyle);
        model.addAttribute("defaultmodulename", Framework.getSystemName("module", request));
      

        String machineIP = StringUtil.getClientIP(request);
        String specialuser = permissionModule.isSpesialUser(machineIP);
        if ((specialuser != null ) && userName == null) {

            String subsystem = request.getParameter("subsystem_id");
            try// uim检测
            {
                 
                userName = specialuser;
                    
                AccessControl control = AccessControl.getInstance();
                control.checkAccess(request, response, false);
                String user = control.getUserAccount();
                request.setAttribute("fromsso", "true");
                if (user == null || "".equals(user) || !userName.equals(user)) {

                    try {
                        if (!userName.equals(user))
                            control.resetSession(session);
                        String password = permissionModule.getUserPassword(userName);
                        control = AccessControl.getInstance();
                        control.login(request, response, userName, password);

                        if (subsystem == null)
                            subsystem = AccessControl.getDefaultSUBSystemID();
                        if (successRedirect == null) {
                            successRedirect = getSuccessRedirect(loginPath, subsystem);
                        }
                        if (!fromredirect) {
                            AccessControl.recordIndexPage(request, successRedirect);
                            AccessControl.recordeSystemLoginPage(request, response);
                        }
                        response.sendRedirect(successRedirect);
                        return null;
                    } catch (Exception e) {

                        response.sendRedirect(new StringBuilder().append(request.getContextPath() ).append( "/jsp/common/ssofail.jsp?userName=").append(userName ).append( "&ip=" ).append( machineIP).toString());
                        return null;
                    }

                } else {

                    if (subsystem == null)
                        subsystem = AccessControl.getDefaultSUBSystemID();
                    if (successRedirect == null) {
                        successRedirect = getSuccessRedirect(loginPath, subsystem);

                    }
                    if (!fromredirect) {
                        AccessControl.recordIndexPage(request, successRedirect);
                        AccessControl.recordeSystemLoginPage(request, response);
                    }
                    response.sendRedirect(successRedirect);
                    return null;
                }

            } catch (Exception e)// 检测失败,继续平台登录
            {

            }

        } else {
           String flag = request.getParameter("flag"); // 是否触发提交

            // 登陆名称的长度

            if (flag == null) {
            } else {
                // String successRedirect =
                // request.getParameter("successRedirect");

                String password = request.getParameter("password");
               

                if (userName != null) {
                    try {
                        if (enable_login_validatecode) {

                           
                            permissionModule.validatecode(request);
                            
                        }

                        AccessControl.getInstance().login(request, response, userName, password);
                        String subsystem = request.getParameter("subsystem_id");
                       
                        if (subsystem == null)
                            subsystem = AccessControl.getDefaultSUBSystemID();
                        /**
                         * 需要全屏时，将response.sendRedirect("index.jsp");注释掉，
                         * 将response.sendRedirect(
                         * "sysmanager/refactorwindow.jsp");打开
                         */
                        if (successRedirect == null) {
                            successRedirect = getSuccessRedirect(loginPath, subsystem);

                        }
                        if (!fromredirect) {
                            AccessControl.recordIndexPage(request, successRedirect);
                            AccessControl.recordeSystemLoginPage(request, response);
                        }
                        response.sendRedirect(successRedirect);
                        return null;
                        // response.sendRedirect("sysmanager/refactorwindow.jsp?subsystem_id="
                        // + subsystem);
                    } catch (AccessException ex) {

                        errorMessage = ex.getMessage();
                        if (errorMessage != null) {
                           
                        } else {
                            errorMessage = org.frameworkset.web.servlet.support.RequestContextUtils.getI18nMessage(
                                    "sany.pdp.login.failed", request);
                        }

                        // if(errorMessage==null){
                        // out.print("登陆失败，请确保输入的用户名和口令是否正确！");
                        // }
                        // else{
                        // out.print(errorMessage);
                        // }

                    } catch (Exception ex) {
                        errorMessage = ex.getMessage();

                        if (errorMessage != null) {
                            // errorMessage = errorMessage.replaceAll("\\n",
                            // "\\\\n");
                            // errorMessage = errorMessage.replaceAll("\\r",
                            // "\\\\r");
                        } else {
                            errorMessage = org.frameworkset.web.servlet.support.RequestContextUtils.getI18nMessage(
                                    "sany.pdp.login.failed", request);
                        }
                        // out.print(errorMessage+ "登陆失败，请确保输入的用户名和口令是否正确！");

                    }
                }

            }

        }
        if (errorMessage != null)
            model.addAttribute("errorMessage", errorMessage);
        List<SubSystem> subsystemList = Framework.getInstance().getSubsystemList();
        List<SysInfo> syses = new ArrayList<SysInfo>();
        SysInfo sys = new SysInfo();
        sys.setName(Framework.getSystemName("module", request));
        sys.setSysid("module");
        syses.add(sys);
        if (subsystemList != null && subsystemList.size() > 0) {
            for (SubSystem sub : subsystemList) {
                sys = new SysInfo();
                sys.setName(Framework.getSystemName(sub.getId(), request));
                sys.setSysid(sub.getId());
                if (system_id != null && system_id.equals(sys.getSysid()))
                    sys.setSelected(true);
                syses.add(sys);
            }

        }
        model.addAttribute("systemList", syses);
        return "path:login";
    }

    @AssertTicket
    public void ssowithticket(HttpServletRequest request, HttpServletResponse response) {
        _ssowithtoken(request, response);
    }

    @AssertDToken
    public void ssowithtoken(HttpServletRequest request, HttpServletResponse response) {
        _ssowithtoken(request, response);
    }

    
    /**
     * 强制要求系统必须携带令牌
     * 
     * @return
     */

    public void _ssowithtoken(HttpServletRequest request, HttpServletResponse response) {
        // return "path:sso";

        String u = "", p = "", ck = "";

        String successRedirect = request.getParameter("successRedirect");
        
        if (!StringUtil.isEmpty(successRedirect)) {
            successRedirect = StringUtil.getRealPath(request, successRedirect, true);
        }
        String userName = (String) request.getAttribute(TokenStore.token_request_account_key);
        String worknumber = (String) request.getAttribute(TokenStore.token_request_worknumber_key);
        String loginType = "1";
        if (StringUtil.isEmpty(userName)) {
            userName = worknumber;
            loginType = "2";
        }
        PermissionModule permissionModule = ConfigManager.getInstance().getPermissionModule();
        String loginMenu = request.getParameter("loginMenu");
        String contextpath = request.getContextPath();
        String menuid = "newGetDoc";
        if (loginMenu != null) {

            menuid = loginMenu;

        }
        HttpSession session = request.getSession();
         
        try {
            AccessControl control = AccessControl.getInstance();
            control.checkAccess(request, response, false);
            String user = control.getUserAccount();

            worknumber = control.getUserAttribute("userWorknumber");
            boolean issameuser = false;
            if (loginType.equals("2")) {
                if (worknumber != null && !worknumber.equals(""))
                    issameuser = userName.equals(worknumber);
            } else {
                if (user != null && !user.equals(""))
                    issameuser = userName.equals(user);
            }

            if (user == null || "".equals(user) || !issameuser) {

                if (!issameuser) {
                    control.resetSession(session);
                }

                try {
                    // 1-域账号登录 2-工号登录
                    String password = null;
                    if (loginType.equals("1")) {

                        password = permissionModule.getUserPassword(userName);
                        if (password == null)
                            throw new AccessException("用户" + userName + "不存在。");
                    } else {
                        java.util.Map data = permissionModule.getUserNameAndPasswordByWorknumber(userName);
                        if (data == null)
                            throw new AccessException("工号为" + userName + "的用户不存在。");
                        userName = (String) data.get("USER_NAME");
                        password = (String) data.get("USER_PASSWORD");
                    }
                    control = AccessControl.getInstance();
                    request.setAttribute("fromsso", "true");
                    // System.out.println("-----------userName="+userName+",password="+password);
                    control.login(request, response, userName, password);
                    if (StringUtil.isEmpty(successRedirect)) {
                        Framework framework = Framework.getInstance(control.getCurrentSystemID());
                        MenuItem menuitem = framework.getMenuByID(menuid);
                        if (menuitem instanceof Item) {

                            Item menu = (Item) menuitem;
                            successRedirect = MenuHelper.getRealUrl(contextpath,
                                    Framework.getWorkspaceContent(menu, control), MenuHelper.sanymenupath_menuid,
                                    menu.getId());
                        } else {

                            Module menu = (Module) menuitem;
                            StringBuilder framepath = new StringBuilder();
                            framepath .append(contextpath ).append( "/sanydesktop/singleframe.page?").append(MenuHelper.sanymenupath ).append( "=").append( menu.getPath());
                            successRedirect = framepath.toString();
                        }
                        AccessControl.recordIndexPage(request, successRedirect);
                    } else {
                        successRedirect = URLDecoder.decode(successRedirect);
                    }
                    response.sendRedirect(successRedirect);
                    return;
                } catch (Exception e) {
                    log.info("", e);
                    String msg = e.getMessage();
                    if (msg == null)
                        msg = "";
                    StringBuilder builder = new StringBuilder();
                    builder.append(contextpath ).append( "/webseal/websealloginfail.jsp?userName=").append( userName)
                	.append( "&ip=").append(StringUtil.getClientIP(request) ).append( "&errormsg=" ).append( java.net.URLEncoder.encode(java.net.URLEncoder.encode(msg, "UTF-8"),"UTF-8"));
                    response.sendRedirect(builder.toString());
                    return;
                }

            } else {
                control.resetUserAttributes();
                if (StringUtil.isEmpty(successRedirect)) {
                    Framework framework = Framework.getInstance(control.getCurrentSystemID());
                    MenuItem menuitem = framework.getMenuByID(menuid);
                    if (menuitem instanceof Item) {

                        Item menu = (Item) menuitem;
                        successRedirect = MenuHelper.getRealUrl(contextpath,
                                Framework.getWorkspaceContent(menu, control), MenuHelper.sanymenupath_menuid,
                                menu.getId());
                    } else {

                        Module menu = (Module) menuitem;
                        StringBuilder framepath = new StringBuilder();
                        framepath .append(contextpath ).append( "/sanydesktop/singleframe.page?" ).append( MenuHelper.sanymenupath
                        		).append( "=" ).append( menu.getPath());
                        successRedirect = framepath.toString();
                    }
                    AccessControl.recordIndexPage(request, successRedirect);
                } else {
                    successRedirect = URLDecoder.decode(successRedirect);
                }
                response.sendRedirect(successRedirect);
                return;
            }

        } catch (Throwable ex) {
            log.info("", ex);
            String errorMessage = ex.getMessage();
            if (errorMessage == null)
                errorMessage = "";

            try {
                FileCopyUtils.copy(errorMessage + "," + userName + "登陆失败，请确保输入的用户名和口令是否正确！",
                        new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
            } catch (IOException e) {
                log.info("", e);
            }

        }
         

    }

}
