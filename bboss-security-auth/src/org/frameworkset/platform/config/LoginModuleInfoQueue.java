package org.frameworkset.platform.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.frameworkset.platform.config.model.LoginModuleInfo;
import org.frameworkset.platform.security.authentication.ACLLoginModule;

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
public class LoginModuleInfoQueue implements java.io.Serializable {
    private List loginModuleQueue = new ArrayList();
    private List<ACLLoginModule> ACLLoginModules = new ArrayList<ACLLoginModule>();
    public List<ACLLoginModule> getACLLoginModules() {
		return ACLLoginModules;
	}
    public int getACLLoginModuleSize()
    {
    	return ACLLoginModules.size();
    }
    public ACLLoginModule getACLLoginModule(int i) {
		return ACLLoginModules.get(i);
	}

	public static void main(String[] args) {
        LoginModuleInfoQueue loginmodulequeue = new LoginModuleInfoQueue();
    }

    public void addLoginModuleInfo(LoginModuleInfo loginModuleInfo)
    {
        this.loginModuleQueue.add(loginModuleInfo);
        try
        {
        	ACLLoginModule aclLoginModule = (ACLLoginModule)(Class.forName(loginModuleInfo.getLoginModule()).newInstance());
        	ACLLoginModules.add(aclLoginModule);
        }
        catch(Exception e)
        {
        	
        }
    }

    public LoginModuleInfo getLoginModuleInfo(int i)
    {
        return (LoginModuleInfo)this.loginModuleQueue.get(i);
    }

    public int size()
    {
        return loginModuleQueue.size();
    }

    public Iterator iterator()
    {
        return loginModuleQueue.iterator();
    }



}
