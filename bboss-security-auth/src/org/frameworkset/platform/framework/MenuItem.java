package org.frameworkset.platform.framework;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


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
public interface MenuItem  {
    public String getParentPath();
    public String getId() ;
    public MenuItem getParent();

    public String getName(HttpServletRequest pageContext) ;

    public String getMouseclickimg(HttpServletRequest pageContext) ;

    public String getMouseoutimg(HttpServletRequest pageContext) ;

    public String getMouseoverimg(HttpServletRequest pageContext) ;
    public String getHeadimg(HttpServletRequest pageContext) ;

	public void setHeadimg(String headimg) ;
	 public String getMouseupimg(HttpServletRequest pageContext) ;
//	 public String getName(HttpServletRequest pageContext);

    public String getTitle(HttpServletRequest pageContext) ;

    public String getPath();
    
    /**获取节点编码*/
    public int getCode();

   
    public boolean isUsed();
    public boolean isMain();
    public SubSystem getSubSystem();
    public String getTarget();
    /**
     * 判断界面是否直接显示item中的workspace-content地址,true显示,false不显示,缺省为false
     * @return
     */
    public boolean isShowpage();
    
    public String getArea();
    
    public boolean isShowleftmenu() ;

	public void setShowleftmenu(boolean showleftmenu) ;
	public Map<String, String> getExtendAttributes() ;
	public void setExtendAttributes(Map<String, String> extendAttributes) ;
	
	public String getOption();
	public void setOption(String option);

}
