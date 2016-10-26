package org.frameworkset.platform.security.authentication;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Refreshable;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class Credential implements Refreshable,Destroyable,java.io.Serializable{
   
    private String loginModule;
    private CheckCallBack checkCallBack;
    public Credential()
    {
    	
    }
    public Credential(CheckCallBack checkCallBack,String loginModule )
    {
        this.checkCallBack = checkCallBack;
        this.loginModule = loginModule;
        
    }

    public void destroy() throws DestroyFailedException {
    }

    public boolean isDestroyed() {
        return false;
    }

    public boolean isCurrent() {
        return true;
    }

    public void refresh() throws RefreshFailedException {
    }

    public CheckCallBack getCheckCallBack() {
        return checkCallBack;
    }

    public String getLoginModule() {
        return loginModule;
    }

   

}
