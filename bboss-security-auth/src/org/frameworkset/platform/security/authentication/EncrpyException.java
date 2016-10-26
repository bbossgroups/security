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
public class EncrpyException extends Exception
							implements Serializable{
    
    public EncrpyException()
    {
        super();
    }

    public EncrpyException(String msg)
    {
        super(msg);
    }

}

