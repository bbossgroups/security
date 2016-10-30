package org.frameworkset.platform.desktop.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.MenuHelper;
import org.frameworkset.platform.framework.MenuItem;
import org.frameworkset.platform.framework.Module;
import org.frameworkset.platform.framework.Root;
import org.frameworkset.platform.security.AccessControl;
import org.frameworkset.web.servlet.support.RequestContextUtils;

import com.frameworkset.common.tag.BaseTag;

/**
<ul class="page-breadcrumb">
                            <li>
                                <a href="index.html">Home</a>
                                <i class="fa fa-circle"></i>  <i class="fa fa-angle-right"></i>
                            </li>
                            <li>
                                <span>Dashboard</span>
                            </li>
                        </ul>
 * @author yinbp
 *
 */
public class MenuPathTag extends BaseTag {
	private static String header = "<ul class=\"page-breadcrumb\">"; 
	private static String rooter = "</ul>";
	private final static String PRE_TITLE = "您的当前位置：";
	private String menuid ;
	private boolean showappname = false;
	public boolean isShowappname() {
		return showappname;
	}
	public void setShowappname(boolean showappname) {
		this.showappname = showappname;
	}
//	public final static String  sanymenupath_menuid = "sanymenupath_menuid";
	public String getMenuid() {
		return menuid;
	}
	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}
	private String pretitle = null;
	public String getPretitle() {
		return pretitle;
	}
	public void setPretitle(String pretitle) {
		this.pretitle = pretitle;
	}
	@Override
	public int doStartTag() throws JspException {	
		int ret = super.doStartTag();
		AccessControl control = AccessControl.getAccessControl(this.request,this.response);		
		Framework framework = Framework.getInstance(control.getCurrentSystemID());
		StringBuilder datas = new StringBuilder();
		if(this.pretitle == null)
			pretitle = framework.getMessage("sany.pdp.module.currentposition", RequestContextUtils.getRequestContextLocal(request));
		 
		datas.append(header);
		
		if(showappname)
			datas.append("<li><span>").append(framework.getDescription(request)).append("</span></li>");
		String theme = control.getUserAttribute("theme");
		
		 
		
		Item publicitem = framework.getPublicItem();
		String mname = publicitem.getName(request);
		String contextpath = request.getContextPath();
		String arrowclass = theme == null || theme.equals("admin_3")?"fa fa-angle-right":"fa fa-circle";
		String url =  MenuHelper.getRealUrl(contextpath, "theme/admin/index.page",MenuHelper.menupath_menuid,publicitem.getId());
				
		datas.append(" <li>");
		if(showappname)
			datas.append("<i class=\"").append(arrowclass).append("\"></i>");
		datas.append("<a href=\"").append(url).append("\">").append(mname).append("</a>") //  <i class="fa fa-angle-right"></i>
		.append("</li>");
		if(menuid == null || menuid.equals(""))
		{
			menuid = request.getParameter(MenuHelper.menupath_menuid);			
		}
		
		if(menuid != null && !menuid.equals(""))
		{
			
		
			MenuItem menu = framework.getMenuByID(menuid);
			if(menu != null )
			
			{	
				
				boolean isroot = false;
				List<MenuItem> ms = new ArrayList<MenuItem>();
				do
				{	
					isroot = menu.getParent() instanceof Root;
					ms.add(menu);
					
					menu = menu.getParent();
					
					
					
				}while(menu != null && !isroot);
				if(ms.size() >= 0)
				{
					
					
					
					for(int i = ms.size() - 1;  i >=0; i --)
					{
						menu = ms.get(i);
						if(menu instanceof Item || i == 0)
						{
							datas.append("<li><i class=\"").append(arrowclass).append("\"></i><span>").append(menu.getName(request)).append("</span></li>");
						}
						else
						{
							Module m = (Module)menu;
							if(m.isUsesubpermission())
								datas.append("<li><i class=\"").append(arrowclass).append("\"></i><span>").append(menu.getName(request)).append("</span></li>");
							else
							{
								url =  MenuHelper.getRealUrl(contextpath, "theme/admin/index.page",MenuHelper.menupath_menuid,m.getId());
								datas.append("<li><i class=\"").append(arrowclass).append("\"></i><a href=\"").append(url).append("\">").append(menu.getName(request)).append("</a></span></li>");
							}
							
						}
							
						 
						
						
					}
					
				}
				
				 
				
			}	
		}
		datas.append(rooter);
		try {
			this.out.write(datas.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public void doFinally() {
		
		super.doFinally();
		this.menuid = null;
		this.showappname = false;
		this.pretitle = null;
	}
	
	
	
	
}
