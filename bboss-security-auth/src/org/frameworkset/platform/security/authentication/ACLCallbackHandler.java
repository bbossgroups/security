package org.frameworkset.platform.security.authentication;

import java.io.IOException;
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
public abstract class ACLCallbackHandler implements CallbackHandler,Serializable {
    /**
     * <p> Retrieve or display the information requested in the provided
     * Callbacks.
     *
     * @param callbacks an array of <code>Callback</code> objects provided
     *   by an underlying security service which contains the information
     *   requested to be retrieved or displayed.
     * @throws IOException if an input or output error occurs. <p>
     * @throws UnsupportedCallbackException if the implementation of this
     *   method does not support one or more of the Callbacks specified in
     *   the <code>callbacks</code> parameter.
     * @todo Implement this javax.security.auth.callback.CallbackHandler
     *   method
     */
//    public abstract void handle(Callback[] callbacks) throws IOException,
//            UnsupportedCallbackException ;

    public static void main(String[] args) {
    }
}
