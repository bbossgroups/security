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
public class LoginModuleInfo implements java.io.Serializable {
    private String controlFlag;
    private String loginModule;
    private String callBackHandler;
    private String name;
    private boolean debug = false;
    private ApplicationInfo ApplicationInfo;
    private String registTable;

    public static void main(String[] args) {
        LoginModuleInfo loginmoduleinfos = new LoginModuleInfo();
    }

    public boolean isDebug() {
        return debug;
    }

    public String getLoginModule() {
        return loginModule;
    }

    public String getName() {
        return name;
    }



    public ApplicationInfo getApplicationInfo() {
        return ApplicationInfo;
    }

    public String getControlFlag() {
        return controlFlag;
    }

    public String getCallBackHandler() {
        return callBackHandler;
    }

    public String getRegistTable() {
        return registTable;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setLoginModule(String loginModule) {
        this.loginModule = loginModule;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * setApplicationInfo
     *
     * @param ApplicationInfo ApplicationInfo
     */
    public void setApplicationInfo(ApplicationInfo ApplicationInfo) {
        this.ApplicationInfo = ApplicationInfo;
    }

    public void setControlFlag(String controlFlag) {
        this.controlFlag = controlFlag;
    }

    public void setCallBackHandler(String callBackHandler) {
        this.callBackHandler = callBackHandler;
    }

    /**
     * setRegistTable
     *
     * @param registTable String
     */
    public void setRegistTable(String registTable) {
        this.registTable = registTable;
    }
}
