package org.frameworkset.platform.config.model;

import org.frameworkset.event.NotifiableFactory;

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
public class NotifiableInfo implements java.io.Serializable {
    private String type;
    private String factoryClass;
    private ApplicationInfo ApplicationInfo;
    private NotifiableFactory notifiableFactory;
    public static void main(String[] args) {
        NotifiableInfo notifiableinfo = new NotifiableInfo();
    }

    public String getFactoryClass() {
        return factoryClass;
    }

    public String getType() {
        return type;
    }

    public ApplicationInfo getApplicationInfo() {
        return ApplicationInfo;
    }

    public void setFactoryClass(String factoryClass) {
        this.factoryClass = factoryClass;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * setApplicationInfo
     *
     * @param ApplicationInfo ApplicationInfo
     */
    public void setApplicationInfo(ApplicationInfo ApplicationInfo) {
        this.ApplicationInfo = ApplicationInfo;
    }

    public NotifiableFactory getNotifiableFactory()
    {
        if(notifiableFactory == null)
        {
            try {
                notifiableFactory = (NotifiableFactory) Class.forName(this.
                        factoryClass).newInstance();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            }
        }
        return notifiableFactory;
    }



}
