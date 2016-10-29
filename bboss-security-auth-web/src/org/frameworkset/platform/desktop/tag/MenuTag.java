package org.frameworkset.platform.desktop.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.MenuHelper; 
import org.frameworkset.platform.framework.MenuItem;
import org.frameworkset.platform.framework.MenuQueue;
import org.frameworkset.platform.framework.Module;
import org.frameworkset.platform.security.AccessControl;

import com.frameworkset.common.tag.BaseTag;
import com.frameworkset.orm.transaction.TransactionManager;

/**
 *
 * @author yinbp
 *
 */
public class MenuTag extends BaseTag {

	private static String dashboard3_header = "<ul class=\"page-sidebar-menu  page-header-fixed page-sidebar-menu-hover-submenu \" data-keep-expanded=\"false\" data-auto-scroll=\"true\" data-slide-speed=\"200\" style=\"padding-top: 20px\">"; 
	private static String dashboard1_header = "<ul class=\"page-sidebar-menu  \" data-keep-expanded=\"false\" data-auto-scroll=\"true\" data-slide-speed=\"200\" style=\"padding-top: 20px\">"; 
	
	public static final String personcenter_name = "personal_center";
	
	
	
	private boolean enableindex = true;
	private int level = 3;
	
	private void header(AccessControl control,MenuHelper menuHelper,StringBuilder datas )
	{
		String theme = control.getUserAttribute("theme");
		
		datas.append(theme == null || theme.equals("admin_3")?dashboard3_header:dashboard1_header);	
		datas.append("<!-- DOC: To remove the sidebar toggler from the sidebar you just need to completely remove the below \"sidebar-toggler-wrapper\" LI element -->");
		datas.append("<li class=\"sidebar-toggler-wrapper hide\">")	;
		datas.append("    <!-- BEGIN SIDEBAR TOGGLER BUTTON -->");
		datas.append("    <div class=\"sidebar-toggler\">");
		datas.append("        <span></span>");
		datas.append("    </div>");
		datas.append("    <!-- END SIDEBAR TOGGLER BUTTON -->");
		datas.append("</li>");
		datas.append("<!-- DOC: To remove the search box from the sidebar you just need to completely remove the below \"sidebar-search-wrapper\" LI element -->");
		datas.append("<li class=\"sidebar-search-wrapper\">");
		datas.append("    <!-- BEGIN RESPONSIVE QUICK SEARCH FORM -->");
		datas.append("    <!-- DOC: Apply \"sidebar-search-bordered\" class the below search form to have bordered search box -->");
		datas.append("    <!-- DOC: Apply \"sidebar-search-bordered sidebar-search-solid\" class the below search form to have bordered & solid search box -->");
		datas.append("    <form class=\"sidebar-search  \" action=\"page_general_search_3.html\" method=\"POST\">");
		datas.append("        <a href=\"javascript:;\" class=\"remove\">");
		datas.append("             <i class=\"icon-close\"></i>");
		datas.append("         </a>");
		datas.append("         <div class=\"input-group\">");
		datas.append("            <input type=\"text\" class=\"form-control\" placeholder=\"Search...\">");
		datas.append("            <span class=\"input-group-btn\">");
		datas.append("                 <a href=\"javascript:;\" class=\"btn submit\">");
		datas.append("                      <i class=\"icon-magnifier\"></i>");
		datas.append("               </a>");
		datas.append("           </span>");
		datas.append("        </div>");
		datas.append("    </form>");
		datas.append("    <!-- END RESPONSIVE QUICK SEARCH FORM -->");
		datas.append(" </li>"); 
	}
	
	private void renderItem(String contextpath,AccessControl control,MenuHelper menuHelper,Item item,boolean selected,StringBuilder datas,boolean isfirst)
	{ 
		String selectedclass = "";
		if(selected)
		{
			if(!isfirst)
				selectedclass = "class=\"nav-item active open\"";
			else
			{
				selectedclass = "class=\"nav-item start active open\"";
			}
			 
		}	
		else
		{
			if(!isfirst)
				selectedclass = "class=\"nav-item \"";
			else
			{
				selectedclass = "class=\"nav-item start \"";
			}
			 
		} 
	
		String mname = item.getName(request);
		String icon = item.getStringExtendAttribute("icon","icon-settings");
		String url =  MenuHelper.getRealUrl(contextpath, Framework.getWorkspaceContent(item,control),MenuHelper.menupath_menuid,item.getId());
		datas.append("<li ").append(selectedclass).append(">")
			 .append("<a href=\"javascript:void(0);\" onclick=\"DesktopMenus.gotoworkspace('','").append(url)
			 .append("')\" class=\"nav-link \">");
		if(icon != null && !icon.equals(""))
		{
			 datas.append("    <i class=\"").append(icon).append("\"></i>");
		}
		datas.append("    <span class=\"title\">").append(mname).append("</span>");
		if(selected)
		{
			 datas.append("   <span class=\"selected\"></span>");
			 
		}
		
		datas.append("</a>")
			 .append("</li>");
	}
	
	private void renderNosonModule(String contextpath,AccessControl control,MenuHelper menuHelper,Module item,boolean selected,StringBuilder datas,boolean isfirst)
	{ 
		String selectedclass = "";
		if(selected)
		{
			if(!isfirst)
				selectedclass = "class=\"nav-item active open\"";
			else
			{
				selectedclass = "class=\"nav-item start active open\"";
			}
			 
		}	
		else
		{
			if(!isfirst)
				selectedclass = "class=\"nav-item \"";
			else
			{
				selectedclass = "class=\"nav-item start \"";
			}
			 
		} 
	
		String mname = item.getName(request);
		String icon = item.getStringExtendAttribute("icon","icon-settings");
		String url = MenuHelper.getModuleUrl(item, contextpath,  control);
		if(url != null && !item.isUsesubpermission())
			datas.append("<li ").append(selectedclass).append(">")
				 .append("<a href=\"javascript:void(0);\" onclick=\"DesktopMenus.gotoworkspace('','").append(url)
				 .append("')\" class=\"nav-link \">");
		else
		{
			datas.append("<li ").append(selectedclass).append(">")
			 .append("<a href=\"javascript:void(0);\"  class=\"nav-link \">");
		}
		if(icon != null && !icon.equals(""))
		{
			 datas.append("    <i class=\"").append(icon).append("\"></i>");
		}
		datas.append("    <span class=\"title\">").append(mname).append("</span>");
		if(selected)
		{
			 datas.append("   <span class=\"selected\"></span>");
			 
		}
		
		datas.append("</a>")
			 .append("</li>");
	}
	
	private void renderModule(String contextpath,AccessControl control,MenuHelper menuHelper,Module item,boolean selected,StringBuilder datas,boolean isfirst,int current_level)
	{ 
		String selectedclass = "";
		if(selected)
		{
			if(!isfirst)
				selectedclass = "class=\"nav-item active open\"";
			else
			{
				selectedclass = "class=\"nav-item start active open\"";
			}
			 
		}	
		else
		{
			if(!isfirst)
				selectedclass = "class=\"nav-item \"";
			else
			{
				selectedclass = "class=\"nav-item start \"";
			}
			 
		} 
		String icon = item.getStringExtendAttribute("icon","icon-diamond");
		
		String mname = item.getName(request);
		datas.append("<li ").append(selectedclass).append(">");
		if(item.getUrl() == null || item.getUrl().equals("") || item.isUsesubpermission())
			datas.append("<a href=\"javascript:;\" class=\"nav-link nav-toggle\">");
		else
		{
			String url = MenuHelper.getModuleUrl(item, contextpath,  control);
			datas.append("<a href=\"javascript:void(0);\" onclick=\"DesktopMenus.gotoworkspace('','").append(url)
			 .append("')\" class=\"nav-link nav-toggle\">");
		}
		
		if(icon != null && !icon.equals(""))
		{
			 datas.append("    <i class=\"").append(icon).append("\"></i>");
		}
		datas.append("    <span class=\"title\">").append(mname).append("</span>"); 
		if(selected)
		{
			 datas.append("   <span class=\"selected\"></span>").append("    <span class=\"arrow open\"></span>");
		}
		else
		{
			datas.append("    <span class=\"arrow  \"></span>");
		}
			
		
		datas.append("</a>")
			 .append("<ul class=\"sub-menu\">");
		MenuQueue menus = item.getMenus();
		for(int i = 0; menus != null && i < menus.size() ; i ++)
		{
			MenuItem mi = menus.getMenuItem(i);
			if(!mi.isUsed())
				continue;
			if(mi instanceof Item)
			{
				this.renderItem(contextpath, control, menuHelper, (Item)mi, false, datas, false);
			}
			else
			{
				renderModule(  contextpath,  control,  menuHelper,(Module) mi,false,datas,false,0);
			}

		}
			 
		datas.append("   </ul>")
			 .append(" </li>  ");
		
	}
	
	@Override
	public int doStartTag() throws JspException {	
		int ret = super.doStartTag();
		AccessControl control = AccessControl.getAccessControl();
		MenuHelper menuHelper =  MenuHelper.getMenuHelper(request);
		StringBuilder datas = new StringBuilder();
		header(  control,  menuHelper,datas );
		//添加首页
		
//		String personcenter = Framework.getInstance(control.getCurrentSystemID()).getMessage("sany.pdp.module.personcenter", RequestContextUtils.getRequestContextLocal(request));
		
		String selectedmenuid = request.getParameter(MenuHelper.selectedmodule);//查找选择的菜单项path,待处理
		 
		
		try{
			
			String contextpath = request.getContextPath();
			Item publicitem = menuHelper.getPublicItem();
			boolean hasputfirst = false;
			if(this.enableindex && publicitem != null && publicitem.isMain())
			{
//				datas.append(" <li class=\"heading\">")
//				.append("    <h3 class=\"uppercase\">Features</h3>")
//				.append("</li>");
				boolean selected = false;
				if(selectedmenuid == null || selectedmenuid.equals("publicitem"))
				{
					selected = true;
					hasputfirst = true;
				}
				renderItem(  contextpath,  control,  menuHelper,publicitem,  selected,  datas,true);
				
			}
			
			
			MenuQueue menus = menuHelper.getMenus();
			for (int i = 0; menus != null && i < menus.size(); i++) {
				MenuItem mi = menus.getMenuItem(i);
				if (!mi.isUsed()) {
					continue;
				}
			 
				if(!hasputfirst)
				{
					if(mi instanceof Item)
					{
						this.renderItem(contextpath, control, menuHelper, (Item)mi, true, datas, true);
					}
					else
					{
						Module module = (Module) mi;
						if(module.getMenus() != null && module.getMenus().size() > 0)
							renderModule(  contextpath,  control,  menuHelper,module,true,datas,true,0);
						else
						{
							renderNosonModule(  contextpath,  control,  menuHelper,module,true,datas,true);
							
						}
					}
					hasputfirst = true;
				}
				else
				{
				
					if(mi instanceof Item)
					{
						this.renderItem(contextpath, control, menuHelper, (Item)mi, false, datas, false);
					}
					else
					{
//						datas.append(" <li class=\"heading\">")
//						.append("    <h3 class=\"uppercase\">Features</h3>")
//						.append("</li>");
						Module module = (Module) mi;
						if(module.getMenus() != null && module.getMenus().size() > 0)
							renderModule(  contextpath,  control,  menuHelper,module,false,datas,false,0);
						else
						{
							renderNosonModule(  contextpath,  control,  menuHelper,module,false,datas,false);
							
						}
							
					}
				}
				
			    
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
			
		
		
		datas.append("</ul>");
		
		try {
//			System.out.println(datas.toString());
			this.out.write(datas.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			datas = null;
		}
		return ret;
	}
	
	
	

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {   
		this.level = level;
	}

	@Override
	public void doFinally() {
		// TODO Auto-generated method stub
		super.doFinally();
		this.level = 3;
		enableindex = true;
	}

	

 



	public boolean isEnableindex() {
		return enableindex;
	}



	public void setEnableindex(boolean enableindex) {
		this.enableindex = enableindex;
	}
	
}
