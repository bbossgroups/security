package org.frameworkset.platform.util;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.frameworkset.web.servlet.support.RequestContextUtils;

public abstract class I18nResource implements java.io.Serializable{
	protected Map<Locale, String> localeNames;
	protected Map<Locale,String> localeDescriptions;
    public Map<Locale, String> getLocaleNames() {
		return localeNames;
	}
	public void setLocaleNames(Map<Locale, String> localeNames) {
		this.localeNames = localeNames;
	}
	public Map<Locale, String> getLocaleDescriptions() {
		return localeDescriptions;
	}
	public void setLocaleDescriptions(Map<Locale, String> localeDescriptions) {
		this.localeDescriptions = localeDescriptions;
	}
	public String getName(HttpServletRequest request) {
    	
//    	if(this.localeNames == null)
//    		return null;
//    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
//    	String temp = this.localeNames.get(locale);
//    	if(temp == null)
//    		return null;
//    	return temp;
//    	
    	return getLocaleMessage(localeNames, request);
    }
	
	public String getDescription(HttpServletRequest request) {
    	
//    	if(this.localeDescriptions == null)
//    		return null;
//    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
//    	String temp = this.localeDescriptions.get(locale);
//    	if(temp == null)
//    		return null;
//    	return temp;
    	return getLocaleMessage(localeDescriptions, request);
    }
	
	private String getLocaleMessage(Map<Locale,String> messages,HttpServletRequest request) {
    	
    	if(messages == null)
    		return null;
    	Locale locale = RequestContextUtils.getRequestContextLocal(request);
    	String temp = messages.get(locale);
    	if(temp == null)
    		return null;
    	return temp;
    }

}
