package org.frameworkset.platform.security.menu;

import java.util.Map;

/**
 * 
 * <p>Copyright: isany (c) 2010</p>
 * <p>Company: isany</p>
 * @author: 
 * @date: 2011-2-15
 * @version 1.0 
 */

public class MenuItemU {
	private String id;
	private String name;
	private String imageUrl;
	private String pathU;
	private String pathPopu;
	private String type;
	private String desktop_width;
	private String desktop_height;
	private String option;
	private boolean hasSon;
	private Map<String,String> extendAttribute;

	public Map<String, String> getExtendAttribute() {
		return extendAttribute;
	}
	public void setExtendAttribute(Map<String, String> extendAttribute) {
		this.extendAttribute = extendAttribute;
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
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getPathU() {
		return pathU;
	}
	public void setPathU(String pathU) {
		this.pathU = pathU;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean getHasSon() {
		return hasSon;
	}
	public void setHasSon(boolean hasSon) {
		this.hasSon = hasSon;
	}
	public String getDesktop_width() {
		return desktop_width;
	}
	public void setDesktop_width(String desktop_width) {
		this.desktop_width = desktop_width;
	}
	public String getDesktop_height() {
		return desktop_height;
	}
	public void setDesktop_height(String desktop_height) {
		this.desktop_height = desktop_height;
	}
	public void setOption(String option) {
		this.option = option;
		
	}
	public String getOption() {
		return option;
	}
	public String getPathPopu() {
		return pathPopu;
	}
	public void setPathPopu(String pathPopu) {
		this.pathPopu = pathPopu;
	}
	
	
	
	
	

}
