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
public class LDAPConfig implements java.io.Serializable {
    private String initial_context_factory;
    private String provider_url;
    private String security_authentication;
    private String security_pricipal;
    private String security_credentials;
    private String name;
    private boolean defaultLDAP;

    public static void main(String[] args) {
        LDAPConfig ldapConfig = new LDAPConfig();
    }

    public String getInitial_context_factory() {
        return initial_context_factory;
    }

    public String getProvider_url() {
        return provider_url;
    }

    public String getSecurity_authentication() {
        return security_authentication;
    }

    public String getSecurity_credentials() {
        return security_credentials;
    }

    public String getSecurity_pricipal() {
        return security_pricipal;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultLDAP() {
        return defaultLDAP;
    }

    public void setInitial_context_factory(String initial_context_factory) {
        this.initial_context_factory = initial_context_factory;
    }

    public void setProvider_url(String provider_url) {
        this.provider_url = provider_url;
    }

    public void setSecurity_authentication(String security_authentication) {
        this.security_authentication = security_authentication;
    }

    public void setSecurity_credentials(String security_credentials) {
        this.security_credentials = security_credentials;
    }

    public void setSecurity_pricipal(String security_pricipal) {
        this.security_pricipal = security_pricipal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultLDAP(boolean defaultLDAP) {
        this.defaultLDAP = defaultLDAP;
    }
}
