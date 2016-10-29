
package org.frameworkset.platform.desktop.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.frameworkset.platform.framework.Framework;
import org.frameworkset.platform.framework.Item;
import org.frameworkset.platform.framework.ItemQueue;
import org.frameworkset.platform.framework.MenuHelper;
import org.frameworkset.platform.framework.MenuItem;
import org.frameworkset.platform.framework.Module;
import org.frameworkset.platform.framework.ModuleQueue;
import org.frameworkset.platform.framework.Root;
import org.frameworkset.platform.security.AccessControl;
import org.frameworkset.web.servlet.support.RequestContextUtils;

import com.frameworkset.common.tag.BaseTag;

/**
 * 左侧系统菜单
 * <div class="left_menu">
  <ul>
    <li class="select_links"><a href="#">辅导规则</a>
      <ul style="display:block">
        <li class="select_links"><a href="http://www.baidu.com.cn" target="rightFrame">配置辅导规则</a></li>
        <li><a href="apply_tutorship_power.html" target="rightFrame">申请辅导资格</a></li>
        <li><a href="exam_tutorship_power.html" target="rightFrame">审批辅导资格</a></li>
        <li><a href="stick_tutorship_power.html" target="rightFrame">维护辅导资格</a></li>
        <li><a href="deploy_tutorship_power.html" target="rightFrame">配置辅导关系</a></li>
        <li><a href="deploy_tutorship_power.html" target="rightFrame">配置辅导关系</a></li>
        <li><a href="auditingl_form.html" target="rightFrame">导师/学生报表</a></li>
        <li><a href="auditingl_form.html" target="rightFrame">导师/学生报表</a></li>
        <li><a href="auditingl_form.html" target="rightFrame">导师/学生报表</a></li>
      </ul>
    </li>
    <li><a href="#">辅导纪录</a>
      <ul>
        <li><a href="fill_tutorship_annal.html" target="rightFrame">填写辅导纪录</a></li>
        <li><a href="auditingl_tutorship_annal.html" target="rightFrame">审核辅导纪录</a></li>
        <li><a href="tutorship_annal_look.html" target="rightFrame">辅导纪录查询</a></li>
        <li><a href="tutorship_annal_detail.html" target="rightFrame">查看辅导纪录明细</a></li>
      </ul>
    </li>
    <li><a href="#">评价管理</a>
      <ul>
        <li><a href="fill_phase_value.html" target="rightFrame">填写阶段评价</a></li>
        <li><a href="check_phase_result.html" target="rightFrame">查询阶段评价</a></li>
        <li><a href="phase_value_detail.html" target="rightFrame">阶段评价明细</a></li>
      </ul>
    </li>
  </ul>
</div>
 * @author yinbp
 * @since 2012-3-28 下午3:12:40
 */
public class LeftMenuTag extends BaseTag{
	
	private static String header = "<div class=\"left_menu\">"; 
	private static String rooter = "</div>";
	
	private String menupath ;
//	public final static String  menupath_menuid = "sany_menupath";
	private String target = "rightFrame";
	
	private void fromTag(StringBuffer datas ,String tokenurl) throws JspException {	
		
		AccessControl control = AccessControl.getAccessControl();
		
		String contextpath = request.getContextPath();
		
		datas.append(header);		
		
		if(menupath == null || menupath.equals(""))
		{
			menupath = request.getParameter(MenuHelper.menupath);			
		}
		
		if(menupath == null || menupath.equals(""))			
			;
		else
		{
			MenuHelper menuHelper = MenuHelper.getMenuHelper(request);
			String personcenter = Framework.getInstance(control.getCurrentSystemID()).getMessage("sany.pdp.module.personcenter", RequestContextUtils.getRequestContextLocal(request));
			
			MenuItem menu = null;
			if("isany_personcenter".equals(menupath))
			{
				
			}
			else
			{
				menu = menuHelper.getCurrentSystemMenu(menupath);
			}
			if(menu == null )
			{			
				if("isany_personcenter".equals(menupath))//根路径下的菜单模块
				{
					datas.append("<ul>");
					
					ItemQueue iq = menuHelper.getItems();
					if(iq.size() > 0)
					{	
						datas.append("<li class=\"select_links\"><a href=\"javascript:void(0)\">").append(personcenter).append("</a>");
						appendItems(datas,iq,null,contextpath,control,true,tokenurl);
						datas.append("</li>");		
					}
					datas.append("</ul>");
				}
			}
			else
			{				
				boolean isroot = false;
				String selectModule = null;
				String selectItem = null;
				if(menu instanceof Item )
				{
					selectItem = menu.getPath();
					if(menu.getParent() instanceof Root)
					{
						
					}
					else if(menu.getParent().getParent() instanceof Root)
					{
						selectModule = menu.getParent().getPath(); 
						menu = menu.getParent();
					}
					else
					{						
						do
						{	
							isroot = menu.getParent() instanceof Root;							
							if(isroot)
							{						
								break;						
							}
							selectModule = menu.getPath();
							menu = menu.getParent();
							
						}while(menu != null && !isroot);
					}
				}
				else
				{
					do
					{	
						isroot = menu.getParent() instanceof Root;
						
						if(isroot)
						{						
							break;						
						}
						selectModule = menu.getPath();
						menu = menu.getParent();
					}while(menu != null && !isroot);
				}
				
				Module module = (Module)menu;
				if(selectModule == null)
				{
					selectModule = module.getPath();
				}
				datas.append("<ul>");
				ModuleQueue mq = module.getSubModules();
				ItemQueue iq = module.getItems();
				for(int i = 0; i < mq.size(); i ++)
				{
					Module submodule = mq.getModule(i);
					if(selectModule != null && selectModule.equals(submodule.getPath()))
					{
						datas.append("<li class=\"select_links\"><a href=\"javascript:void(0)\">");
						datas.append(submodule.getName(request)).append("</a>");
						appendItems(datas,submodule.getItems(),selectItem,contextpath,control,true,tokenurl);
					}
					else
					{
						datas.append("<li><a href=\"javascript:void(0)\">");
						datas.append(submodule.getName(request)).append("</a>");
						appendItems(datas,submodule.getItems(),selectItem,contextpath,control,false,tokenurl);
					}
					
					datas.append("</li>");					
				}
				if(iq.size() > 0)
				{
					if(selectModule != null 
							&& selectModule.equals(module.getPath()))
					{
						datas.append("<li class=\"select_links\"><a href=\"javascript:void(0)\">").append(module.getName(request)).append("</a>");
						appendItems(datas,iq,selectItem,contextpath,control,true,tokenurl);
					}
					else
					{
						datas.append("<li ><a href=\"javascript:void(0)\">")
						.append(module.getName(request)).append("</a>");
						appendItems(datas,iq,selectItem,contextpath,control,false,tokenurl);
					}
								
					
					datas.append("</li>");		
				}
				datas.append("</ul>");
			}
		}
		datas.append(rooter);
	
	}
	
	public int doStartTag() throws JspException {	
		int ret = super.doStartTag();
		StringBuffer datas = new StringBuffer();
		String tokenurl = request.getContextPath() + "/token/getParameterToken.freepage"; 
		if(menupath == null || menupath.equals(""))
		{
			fromTopMenu(datas ,tokenurl)	;
		}
		else
		{
			fromTag(datas,tokenurl);
		}
		try {
			this.out.write(datas.toString());
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return ret;
	}
	
	private void fromTopMenu(StringBuffer datas ,String tokenurl)
	{
		AccessControl control = AccessControl.getAccessControl();
		String rootmodulepath = (String)request.getAttribute("rootmodulepath");
		String selectModule = (String)request.getAttribute("selectModule");
		String selectItem = (String)request.getAttribute("selectItem");
		String selectUrl = (String)request.getAttribute("selectUrl");
		
		String contextpath = request.getContextPath();
		MenuItem menu = (MenuItem)request.getAttribute("rootmodule");
	
		datas.append(header);	
		MenuHelper menuHelper = (MenuHelper)request.getAttribute("menuHelper");
		String personcenter = Framework.getInstance(control.getCurrentSystemID()).getMessage("sany.pdp.module.personcenter", RequestContextUtils.getRequestContextLocal(request));
						
		if("isany_personcenter".equals(rootmodulepath))//根路径下的菜单模块
		{
			datas.append("<ul>");
			
			ItemQueue iq = menuHelper.getItems();
			if(iq.size() > 0)
			{	
				datas.append("<li class=\"select_links\"><a href=\"javascript:void(0)\">").append(personcenter).append("</a>");
				appendItems(datas,iq,selectItem,contextpath,control,true,tokenurl);
				datas.append("</li>");		
			}
			datas.append("</ul>");
		}
		
		else
		{				
			
			Module module = (Module)menu;
			
			datas.append("<ul>");
			ModuleQueue mq = module.getSubModules();
			ItemQueue iq = module.getItems();
			if(iq.size() > 0)
			{
				if(selectModule != null 
						&& selectModule.equals(module.getPath()))
				{
					datas.append("<li class=\"select_links\"><a href=\"javascript:void(0)\">").append(module.getName(request)).append("</a>");
					appendItems(datas,iq,selectItem,contextpath,control,true,tokenurl);
				}
				else
				{
					datas.append("<li ><a href=\"javascript:void(0)\">")
					.append(module.getName(request)).append("</a>");
					appendItems(datas,iq,selectItem,contextpath,control,false,tokenurl);
				}
							
				
				datas.append("</li>");		
			}
			for(int i = 0; i < mq.size(); i ++)
			{
				Module submodule = mq.getModule(i);
				if(selectModule != null && selectModule.equals(submodule.getPath()))
				{
					datas.append("<li class=\"select_links\"><a href=\"javascript:void(0)\">");
					datas.append(submodule.getName(request)).append("</a>");
					appendItems(datas,submodule.getItems(),selectItem,contextpath,control,true,tokenurl);
				}
				else
				{
					datas.append("<li><a href=\"javascript:void(0)\">");
					datas.append(submodule.getName(request)).append("</a>");
					appendItems(datas,submodule.getItems(),selectItem,contextpath,control,false, tokenurl);
				}
				
				datas.append("</li>");					
			}
			
			datas.append("</ul>");
		}
		datas.append(rooter);
	}
	
	/**
	 * 
	 * @param datas
	 * @param iq
	 * @param selectmodulepath
	 * @param selectItem
	 */
	private void appendItems(StringBuffer datas,ItemQueue iq,String selectItem,String contextpath,AccessControl control,boolean selectedModule,String tokenurl)
	{
		 if(!selectedModule)
			 datas.append("<ul >");
		 else
			 datas.append("<ul style=\"display:block\">");
			 
		 for(int i = 0; i < iq.size(); i ++)
		 {
			 Item subitem = iq.getItem(i);
			 String area = subitem.getArea();
			String url = null;
			if(area != null && area.equals("main"))
			{
				url = MenuHelper.getMainUrl(contextpath, subitem,
						(java.util.Map) null);
			}
			else
			{
				url = MenuHelper.getRealUrl(contextpath, Framework.getWorkspaceContent(subitem,control),MenuHelper.menupath_menuid,subitem.getId());
			}
			 String mname = subitem.getName(request);
			 if(selectItem != null && subitem.getPath().equals(selectItem))
			 {
				
				 datas.append("<li  class=\"select_links\"><a href=\"javascript:void(0)\" onclick=\"leftnavto_sany_MenuItem('").append(tokenurl)
				.append("','").append(url).append("','").append(target).append("',").append(subitem.getOption()).append(",'").append(mname).append("')\">").append(mname).append("</a></li>");
			 }
			 else
			 {
				 datas.append("<li><a href=\"javascript:void(0)\" onclick=\"leftnavto_sany_MenuItem('").append(tokenurl)
				.append("','").append(url).append("','").append(target).append("',").append(subitem.getOption()).append(",'").append(mname).append("')\">").append(mname).append("</a></li>");
			 }
		 }  
	     datas.append("</ul>");
	}
	@Override
	public void doFinally() {
		
		this.menupath = null;
		this.target = "rightFrame";
		super.doFinally();
		
		
	}
	public String getMenupath() {
		return menupath;
	}
	public void setMenupath(String menupath) {
		this.menupath = menupath;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
