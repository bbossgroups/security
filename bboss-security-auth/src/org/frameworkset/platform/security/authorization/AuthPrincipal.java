package org.frameworkset.platform.security.authorization;

import java.security.Principal;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AuthPrincipal implements Principal,java.io.Serializable {
   
    private String name;
    private String loginModuleName;
    private String userID;
    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     * @todo Implement this java.security.Principal method
     */
    public String getName() {
        return name;
    }

   

    public String getLoginModuleName() {
        return loginModuleName;
    }
    public AuthPrincipal()
    {
    	
    }
    public AuthPrincipal(String name,String loginModuleName)
    {
        this.name = name;
        this.loginModuleName = loginModuleName;
    }
    
    public AuthPrincipal(String name ,String loginModuleName,String userID)
    {
        this.name = name;
        this.loginModuleName = loginModuleName;
        this.userID = userID;
    }

    public boolean equals(Object anotherPrincipal)
    {
        if(anotherPrincipal == null)
            return false;
        if(anotherPrincipal instanceof AuthPrincipal)
        {
            AuthPrincipal principal = (AuthPrincipal)anotherPrincipal;
            if(this.loginModuleName == null)
                if(principal.getLoginModuleName() != null )
                    return false;
            if(this.loginModuleName != null && principal.getLoginModuleName() == null )
                return false;
            if(this.loginModuleName == null && principal.getLoginModuleName() == null)
                return this.name.equals(principal.getName());
            else
                return this.name.equals(principal.getName())
                     && this.loginModuleName.equals(principal.getLoginModuleName());
        }
        else
            return false;
    }

    public int hashCode()
    {
        return this.name.hashCode()
                + this.loginModuleName.hashCode();
    }

    public String toString()
    {
        return "Principal[loginModule=" + loginModuleName +",name=" + name+"]";
    }

    public static void main(String[] args) {
    }

	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
}
