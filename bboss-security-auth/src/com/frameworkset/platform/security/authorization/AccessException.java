package com.frameworkset.platform.security.authorization;

import java.io.Serializable;

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
public class AccessException extends Exception implements Serializable {
    public AccessException() {
    }

    public AccessException(String message) {
        super(message);
    }

    public AccessException(String message,Throwable e) {
       super(message,e);
   }


}
