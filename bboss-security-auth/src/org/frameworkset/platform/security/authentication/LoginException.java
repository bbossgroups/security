package org.frameworkset.platform.security.authentication;

import java.io.Serializable;

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
public class LoginException extends Exception implements Serializable{
    public static void main(String[] args) {
        LoginException loginexception = new LoginException();
    }
    public LoginException()
    {
        super();
    }

    public LoginException(String msg)
    {
        super(msg);
    }
	public LoginException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public LoginException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
