package org.frameworkset.platform.resource;

import java.io.Serializable;

import org.frameworkset.platform.config.model.ResourceInfo;

/**
 * <p>Title: </p>
 *
 * <p>Description:资源识别器 </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public interface ResourceIndentity extends Serializable{
    public void setResourceInfo(ResourceInfo resourceInfo);
}
