package org.frameworkset.platform.config.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleServiceInfo implements java.io.Serializable {
	private String name;
	private String id;
	private String clazz;
	private boolean used = true;
	
	/**
	 * Map<String,SchedulejobInfo>
	 */
	private Map jobsbyIds = new HashMap();
	/**
	 * List<SchedulejobInfo>
	 */
	private List jobs = new ArrayList();
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	
	public void add(SchedulejobInfo schedulejobInfo)
	{
		schedulejobInfo.setParent(this);
		this.jobs.add(schedulejobInfo);
	}
	
	/**
	 * 
	 * @return List<SchedulejobInfo>
	 */
	public List getJobs()
	{
		return this.jobs;
	}
	
	public SchedulejobInfo getSchedulejobInfoByID(String id)
	{
		return (SchedulejobInfo)this.jobsbyIds.get(id);
	}
	

}
