package org.frameworkset.platform.config.model;

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
public class Context implements java.io.Serializable {
    private String application;
    private String moduleName;
    public Context(String application,String moduleName)
    {
    	this.application = application;
    	this.moduleName = moduleName;
    	
    }
    public Context()
    {
    	
    }
    public static void main(String[] args) {
        Context context = new Context();
    }

    public String getApplication() {
        return application;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
