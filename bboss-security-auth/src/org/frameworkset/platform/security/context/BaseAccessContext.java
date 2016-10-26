//Source file: D:\\environment\\eclipse\\workspace\\cjxerpsecurity\\src\\com\\westerasoft\\common\\security\\websphere\\authorization\\impl\\BaseAccessContext.java

package org.frameworkset.platform.security.context;

import java.util.Map;



//import com.ibm.ws.security.core.AccessContext;

/**
 *
 * To change for your class or interface
 *
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class BaseAccessContext implements AccessContext {
    private String appName;
    private String moduleName;
    private Map configParams;
    /**
     * @since 2004.12.15
     */
    public BaseAccessContext(String enterpriseAppName, String module,Map configParams) {
        this.appName = enterpriseAppName;
        this.moduleName = module;
        this.configParams = configParams ;
         

    }

    public String getAppName() {
        return appName;
    }

    public String getModuleName() {

        return moduleName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    /**
     * 获取配置参数
     */
    public String getConfigParam(String name) {
		if(configParams == null)
			return null;
		return (String)configParams.get(name);
	}
}
