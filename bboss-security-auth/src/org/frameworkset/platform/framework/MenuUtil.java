package org.frameworkset.platform.framework;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * <p>Title: MenuUtil.java</p> 
 * <p>Description: 对菜单路径进行解析</p>
 * <p>bboss workgroup</p>
 * <p>Copyright (c) 2007</p>
 * @Date 2009-4-20 上午11:38:21
 * @author biaoping.yin
 * @version 1.0
 */
public class MenuUtil {
    static String  root = "menu://sysmenu$root";
    
    public static String[] getPositions(HttpServletRequest request)
    {
        String path = request.getParameter(Framework.MENU_PATH);
        if(path != null && !path.equals(""))
        {
            return getAllPosition(path,request);
        }
        else
        {
            throw new RuntimeException("菜单路径[" + path + "]不合法或者没有传递"+Framework.MENU_PATH +  "参数到当前页面."  );
        }
        
    }
    
    /**
     * 通过传入的menu path，获取包含本级在内的所有菜单的path
     * subsystem::menu://sysmenu$root/moduleid$module
     * @param path
     * @return
     */
    public static String[] getAllMenus(String path)
    {
       int idx = path.indexOf("::");
       if(idx == -1)
       {
           throw new RuntimeException("菜单路径[" + path + "]不合法."  );
       }
       else
       {
           String systemid = path.substring(0,idx);
           String path_ = path.substring(idx + 2 + 19 + 1);
           String[] paths_ = path_.split("/");
           String[] paths = new String[paths_.length];
           for(int i = 0; i < paths_.length; i ++)
           {
               if(i == 0)
                   paths[i] = systemid + "::" + root + "/" + paths_[i];
               else
               {
                   paths[i] = paths[i - 1] + "/" + paths_[i];
               }
           }
           
           return paths;
           
       }
       
        
    }
    
    /**
     * 通过传入的menu path，获取包含本级在内的所有菜单的path
     * @param path
     * @return
     */
    public static String[] getAllPosition(String path,HttpServletRequest request)
    {
        int idx = path.indexOf("::");
        if(idx == -1)
        {
            throw new RuntimeException("菜单路径[" + path + "]不合法."  );
        }
        else
        {
            String systemid = path.substring(0,idx);
            
            String systemName = Framework.getInstance(systemid).getDescription(request);
            String path_ = path.substring(idx + 2 + 19 + 1);
            String[] paths_ = path_.split("/");
            String[] paths = new String[paths_.length];
            String[] positions = new String[paths_.length + 1];
            positions[0] = systemName;
            for(int i = 1; i < positions.length; i ++)
            {
                
                if(i == 1)                    
                {
                    paths[0] = systemid + "::" + root + "/" + paths_[0];
                    positions[i] = Framework.getInstance(systemid).getMenu(paths[0]).getName(request);
                }
                else
                {
                    paths[i - 1] = paths[i - 2] + "/" + paths_[i - 1];
                    positions[i] = Framework.getInstance(systemid).getMenu(paths[i-1]).getName(request);
                }                
            }
            
            return positions;
            
        }
    }
    
    public static void main(String args[])
    {
        String path= "2::menu://sysmenu$root/grsw$module/personal_info$module/1555$item";
//        System.out.println(root.length());
//        int idx = path.indexOf("::");
//        String systemid = path.substring(0,idx);
//        String path_ = path.substring(idx + 2 + 19 + 1);
//        System.out.println(systemid);
//        System.out.println(path_);
//        
        String[] ss = getAllMenus(path);
        System.out.println(ss);
        
    }
    
    
    
    
    

}
