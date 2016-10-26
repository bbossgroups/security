package org.frameworkset.platform.security.event;

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
public abstract class NotifiableType implements Serializable{
    public static final int USER_NOTIFIABLE = 0;
    public static final int GROUP_NOTIFIABLE = 1;
    public static final int ROLE_NOTIFIABLE = 2;
    public static final int RESOURCE_NOTIFIABLE = 3;
    public static final int PERMISSION_NOTIFIABLE = 4;
    public static final int ORGUNIT_NOTIFIABLE = 5;
}

