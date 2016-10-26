package org.frameworkset.platform.config.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskServiceInfo implements java.io.Serializable {
	
	private List taskServices = new ArrayList();
	private Map taskServiceIdxs = new HashMap();
	
	private boolean used = true;

	public List getTaskServices() {
		return taskServices;
	}
	
	public void addScheduleServiceInfo(ScheduleServiceInfo scheduleServiceInfo)
	{
		taskServiceIdxs.put(scheduleServiceInfo.getId(),scheduleServiceInfo);
		this.taskServices.add(scheduleServiceInfo);
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
	
	public ScheduleServiceInfo getScheduleServiceInfo(String id)
	{
		return (ScheduleServiceInfo)taskServiceIdxs.get(id);
	}
}
