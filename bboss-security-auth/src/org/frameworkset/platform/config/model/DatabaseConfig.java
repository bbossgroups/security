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
public class DatabaseConfig implements java.io.Serializable {
    public DatabaseConfig() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private boolean defaultDB;

    private String name;
    public static void main(String[] args) {
        DatabaseConfig databaseconfig = new DatabaseConfig();
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultDB() {
        return defaultDB;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultDB(boolean defaultDB) {
        this.defaultDB = defaultDB;
    }

    private void jbInit() throws Exception {
    }


}
