package org.frameworkset.platform.desktop.action;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.MenuHelper;
import org.frameworkset.platform.framework.MenuItem;
import org.frameworkset.platform.framework.Module;
import org.frameworkset.platform.security.AccessControl;
import org.frameworkset.web.servlet.ModelMap;

public class AdminController {

	public AdminController() {
		// TODO Auto-generated constructor stub
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
				model.addAttribute("workspaceurl", url);
			}
			else
			{
				String url = MenuHelper.getModuleUrl((Module)publicitem, request.getContextPath(),  control);
				model.addAttribute("workspaceurl", url);
			}
		}
		else
		{
			MenuHelper.getRealUrl(request.getContextPath(), selecturl);
			model.addAttribute("workspaceurl", selecturl);
		}
		
		
		String theme = AccessControl.getAccessControl().getUserAttribute("theme");
		
		model.addAttribute("theme", theme);

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
