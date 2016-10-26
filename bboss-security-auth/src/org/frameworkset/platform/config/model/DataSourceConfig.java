package org.frameworkset.platform.config.model;

import java.util.Map;

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
public class DataSourceConfig implements java.io.Serializable {

    private Map dbConfigs;
    private Map ldapConfigs;

    private DatabaseConfig defaultDatabaseConfig;
    private LDAPConfig defaultLDAPConfig;

    public DataSourceConfig()
    {
        dbConfigs = java.util.Collections.synchronizedMap(new java.util.HashMap());
        ldapConfigs = java.util.Collections.synchronizedMap(new java.util.HashMap());
    }

    public static void main(String[] args) {
        DataSourceConfig datasourceinfo = new DataSourceConfig();
    }

    public DatabaseConfig getDbConfig(String dbType)
    {
        return (DatabaseConfig)this.dbConfigs.get(dbType);
    }

    public LDAPConfig getLDAPConfig(String ldapType)
    {
        return (LDAPConfig)this.ldapConfigs.get(ldapType);
    }


    public void addDbConfig(DatabaseConfig databaseConfig)
    {
        this.dbConfigs.put(databaseConfig.getName(),databaseConfig)  ;
    }

    public void addLDAPConfig(LDAPConfig ldapConfig)
    {
        this.ldapConfigs.put(ldapConfig.getName(),ldapConfig)  ;
    }

    public DatabaseConfig getDefaultDatabaseConfig() {
        return defaultDatabaseConfig;
    }

    public LDAPConfig getDefaultLDAPConfig() {
        return defaultLDAPConfig;
    }

    public void setDefaultDatabaseConfig(DatabaseConfig defaultDatabaseConfig) {
        this.defaultDatabaseConfig = defaultDatabaseConfig;
    }

    public void setDefaultLDAPConfig(LDAPConfig defaultLDAPConfig) {
        this.defaultLDAPConfig = defaultLDAPConfig;
    }

}
