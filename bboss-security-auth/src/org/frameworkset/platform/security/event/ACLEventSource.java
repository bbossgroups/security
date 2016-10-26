package org.frameworkset.platform.security.event;

import java.io.Serializable;

import org.frameworkset.platform.security.context.AccessContext;

/**
 * <p>Title: 系统安全管理事件源信息
 * </p>
 *
 * <p>Description: 包含事件发生的上下文和事件源消息</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class ACLEventSource implements Serializable{
    private AccessContext context;
    private Object message;

    public static void main(String[] args) {
        ACLEventSource acleventsource = new ACLEventSource();
    }

    public AccessContext getContext() {
        return context;
    }

    public Object getMessage() {
        return message;
    }

    public void setContext(AccessContext context) {
        this.context = context;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
