package org.frameworkset.platform.security.authorization;

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
public class AccessException extends RuntimeException  {
    public AccessException() {
    }

    public AccessException(String message) {
        super(message);
    }

    public AccessException(String message,Throwable e) {
       super(message,e);
   }


}
