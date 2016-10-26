package org.frameworkset.platform.security.event;

import org.frameworkset.event.AbstractEventType;

/**
 * 
 * 
 * <p>Title: ACLEventTypeImpl.java</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: 三一集团</p>
 * @Date May 23, 2008 9:56:35 AM
 * @author biaoping.yin,尹标平
 * @version 1.0
 */
public class ACLEventTypeImpl extends AbstractEventType implements ACLEventType{
	
	public ACLEventTypeImpl()
	{
		
	}
	public ACLEventTypeImpl(String eventtype)
	{
		this.eventtype = eventtype;
	}
	
	
	

}
