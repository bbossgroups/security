package org.frameworkset.platform.desktop.action;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.MenuHelper;
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
		Item publicitem = framework.getPublicItem();
		String url =  MenuHelper.getRealUrl(request.getContextPath(), Framework.getWorkspaceContent(publicitem,control),MenuHelper.menupath_menuid,publicitem.getId());
		model.addAttribute("workspaceurl", url);
		String theme = AccessControl.getAccessControl().getUserAttribute("theme");
		
		model.addAttribute("theme", theme);
//		if(theme == null)
//			return "path:index_admin_3";
//		else if(theme.equals("admin_1"))
//			return "path:index_admin_1";
//		else if(theme.equals("admin_3"))
//			return "path:index_admin_3";
//		else
//			return "path:index_admin_1";
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
