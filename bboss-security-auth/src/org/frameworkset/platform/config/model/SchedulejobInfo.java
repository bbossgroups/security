package org.frameworkset.platform.config.model;

import java.util.HashMap;
import java.util.Map;

public class SchedulejobInfo implements java.io.Serializable {
	private ScheduleServiceInfo parent;
	
	private String name;
	private String id;
	private String clazz;
	private boolean used = true;
	private String cronb_time ;
	/**
	 * Map<String,String>
	 */
	private Map parameters = new HashMap();
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getCronb_time() {
		return cronb_time;
	}
	public void setCronb_time(String cronb_time) {
		this.cronb_time = cronb_time;
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
	
	public void addParameter(String name,String value)
	{
		this.parameters.put(name,value);
		
	}
	
	public String getParameter(String name)
	{
		return (String)parameters.get(name);		
	}
	
	public String getParameter(String name,String defaultValue)
	{
		String ret = (String)parameters.get(name);
		return ret != null?ret:defaultValue;		
	}
	public ScheduleServiceInfo getParent() {
		return parent;
	}
	public void setParent(ScheduleServiceInfo parent) {
		this.parent = parent;
	}
	public Map getParameters() {
		return parameters;
	}

}
