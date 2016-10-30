package org.frameworkset.platform.desktop.action;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.MenuHelper;
import org.frameworkset.platform.framework.MenuItem;
import org.frameworkset.platform.framework.Module;
import org.frameworkset.platform.security.AccessControl;
import org.frameworkset.web.servlet.ModelMap;

import com.frameworkset.util.StringUtil;

public class AdminController {

	public AdminController() {
		// TODO Auto-generated constructor stub
	}
	
	public String iframe(ModelMap model,HttpServletRequest request)
	{
		String workspaceurl = request.getParameter(MenuHelper.selecturl);
		model.addAttribute("workspaceurl", workspaceurl);
		return "path:iframe";
	}
	public String index(ModelMap model,HttpServletRequest request)
	{
		AccessControl control = AccessControl.getAccessControl();
		model.addAttribute("sysname",control.getCurrentSystemName());
//		boolean successed = AccessControl.getAccessControl().checkPermission("menu1","view", "menu");
//		System.out.println(successed);
		Framework  framework = Framework.getInstance(control.getCurrentSystemID());
		String menuid = request.getParameter(MenuHelper.menupath_menuid);
		String selecturl = request.getParameter(MenuHelper.selecturl);
		if(selecturl == null || selecturl.equals(""))
		{
			MenuItem publicitem = menuid == null || menuid.equals("")?framework.getPublicItem():framework.getMenuByID(menuid);
			if(publicitem == null) publicitem = framework.getPublicItem();
			if(publicitem instanceof Item)
			{
				String url =  MenuHelper.getRealUrl(request.getContextPath(), Framework.getWorkspaceContent((Item)publicitem,control),MenuHelper.menupath_menuid,publicitem.getId());
				
				String iframe = ((Item)publicitem).getStringExtendAttribute("iframe");
				
				if(iframe == null || !iframe.equals("true"))
				{
					
				}
				else
				{
					
					url =  MenuHelper.getRealUrl(request.getContextPath(), "theme/admin/iframe.page",MenuHelper.selecturl,StringUtil.urlencode(url,"UTF-8"));
				}
				model.addAttribute("workspaceurl", url);
			}
			else
			{
				String url = MenuHelper.getModuleUrl((Module)publicitem, request.getContextPath(),  control);
				String iframe = ((Module)publicitem).getStringExtendAttribute("iframe");
				
				if(iframe == null || !iframe.equals("true"))
				{
					
				}
				else
				{
					
					url =  MenuHelper.getRealUrl(request.getContextPath(), "theme/admin/iframe.page",MenuHelper.selecturl,StringUtil.urlencode(url,"UTF-8"));
				}
				model.addAttribute("workspaceurl", url);
			}
		}
		else
		{
			selecturl = MenuHelper.getRealUrl(request.getContextPath(), selecturl);
			model.addAttribute("workspaceurl", selecturl);
		}
		
		
		String theme = AccessControl.getAccessControl().getUserAttribute("theme");
		String theme_style = AccessControl.getAccessControl().getUserAttribute("theme_style");
		
		model.addAttribute("theme", theme);
		model.addAttribute("theme_style", theme_style);

		return "path:index_admin_1";
	}
	
	public String content(ModelMap model)
	{
		model.addAttribute("sysname",AccessControl.getAccessControl().getCurrentSystemName());		
		String theme = AccessControl.getAccessControl().getUserAttribute("theme");
		if(theme == null)
			return "path:content_admin_3";
		else if(theme.equals("admin_1"))
			return "path:content_admin_1";
		else if(theme.equals("admin_3"))
			return "path:content_admin_3";
		else
			return "path:content_admin_1";
	}


}
