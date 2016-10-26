package org.frameworkset.platform.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public abstract class I18nXMLParser extends DefaultHandler {
	private static Logger log = Logger.getLogger(I18nXMLParser.class);
	protected Map<String, Locale> languages;

	public void setLanguages(Map<String, Locale> languages) {
		this.languages = languages;
	}

	/**
	 * 
	 * 
	 * 
	 * @param locales
	 * @return
	 */
	protected Map<String, Locale> converLocales() {

		Map<String, Locale> lm = new HashMap<String, Locale>();

		{
			// if(locale.equals(Locale.SIMPLIFIED_CHINESE))
			{
				lm.put(String.valueOf(Locale.SIMPLIFIED_CHINESE),
						Locale.SIMPLIFIED_CHINESE);
			}
			// else if(locale.equals(Locale.ENGLISH))
			{
				lm.put(String.valueOf(Locale.ENGLISH), Locale.ENGLISH);
			}
			// else if(locale.equals(Locale.US))
			{
				lm.put(String.valueOf(Locale.US), Locale.US);
			}
			// else if(locale.equals(Locale.JAPANESE))
			{
				lm.put(String.valueOf(Locale.JAPANESE), Locale.JAPANESE);
			}
			// else if(locale.equals(Locale.FRENCH))
			{
				lm.put(String.valueOf(Locale.FRENCH), Locale.FRENCH);
			}
			// else if(locale.equals(Locale.CANADA_FRENCH))
			{
				lm.put(String.valueOf(Locale.CANADA_FRENCH),
						Locale.CANADA_FRENCH);
			}
			// else if(locale.equals(Locale.CANADA))
			{
				lm.put(String.valueOf(Locale.CANADA), Locale.CANADA);
			}
			// else if(locale.equals(Locale.UK))
			{
				lm.put(String.valueOf(Locale.UK), Locale.UK);
			}
			// else if(locale.equals(Locale.TAIWAN))
			{
				lm.put(String.valueOf(Locale.TAIWAN), Locale.TAIWAN);
			}
			// else if(locale.equals(Locale.PRC))
			{
				lm.put(String.valueOf(Locale.PRC), Locale.PRC);
			}
			// else if(locale.equals(Locale.KOREA))
			{
				lm.put(String.valueOf(Locale.KOREA), Locale.KOREA);
			}
			// else if(locale.equals(Locale.JAPAN))
			{
				lm.put(String.valueOf(Locale.JAPAN), Locale.JAPAN);
			}
			// else if(locale.equals(Locale.ITALY))
			{
				lm.put(String.valueOf(Locale.ITALY), Locale.ITALY);
			}
			// else if(locale.equals(Locale.GERMANY))
			{
				lm.put(String.valueOf(Locale.GERMANY), Locale.GERMANY);
			}
			// else if(locale.equals(Locale.FRANCE))
			{
				lm.put(String.valueOf(Locale.FRANCE), Locale.FRANCE);
			}
			// else if(locale.equals(Locale.TRADITIONAL_CHINESE))
			{
				lm.put(String.valueOf(Locale.TRADITIONAL_CHINESE),
						Locale.TRADITIONAL_CHINESE);
			}
			// else if(locale.equals(Locale.CHINESE))
			{
				lm.put(String.valueOf(Locale.CHINESE), Locale.CHINESE);
			}
			// else if(locale.equals(Locale.KOREAN))
			{
				lm.put(String.valueOf(Locale.KOREAN), Locale.KOREAN);
			}
			// else if(locale.equals(Locale.ITALIAN))
			{
				lm.put(String.valueOf(Locale.ITALIAN), Locale.ITALIAN);
			}
			// else if(locale.equals(Locale.GERMAN))
			{
				lm.put(String.valueOf(Locale.GERMAN), Locale.GERMAN);
			}
			lm.put("ROOT", Locale.ROOT);
			// else
			// {
			// log.debug("不正确的语言代码:"+ locale);
			// }
		}
		return lm;
	}

	/**
	 * 
	 * 
	 * 
	 * @param locales
	 * @return
	 */
	protected Map<String, Locale> converLocales(String locales) {
		if (locales == null || locales.trim().equals(""))
			return null;
		String[] locales_ = locales.split("\\,");
		Map<String, Locale> lm = new HashMap<String, Locale>();
		for (String locale : locales_) {
			if (locale.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
				lm.put(locale, Locale.SIMPLIFIED_CHINESE);
			} else if (locale.equals(Locale.ENGLISH.toString())) {
				lm.put(locale, Locale.ENGLISH);
			} else if (locale.equals(Locale.US.toString())) {
				lm.put(locale, Locale.US);
			} else if (locale.equals(Locale.JAPANESE.toString())) {
				lm.put(locale, Locale.JAPANESE);
			} else if (locale.equals(Locale.FRENCH.toString())) {
				lm.put(locale, Locale.FRENCH);
			} else if (locale.equals(Locale.CANADA_FRENCH.toString())) {
				lm.put(locale, Locale.CANADA_FRENCH);
			} else if (locale.equals(Locale.CANADA.toString())) {
				lm.put(locale, Locale.CANADA);
			} else if (locale.equals(Locale.UK.toString())) {
				lm.put(locale, Locale.UK);
			} else if (locale.equals(Locale.TAIWAN.toString())) {
				lm.put(locale, Locale.TAIWAN);
			} else if (locale.equals(Locale.PRC.toString())) {
				lm.put(locale, Locale.PRC);
			} else if (locale.equals(Locale.KOREA.toString())) {
				lm.put(locale, Locale.KOREA);
			} else if (locale.equals(Locale.JAPAN.toString())) {
				lm.put(locale, Locale.JAPAN);
			} else if (locale.equals(Locale.ITALY.toString())) {
				lm.put(locale, Locale.ITALY);
			} else if (locale.equals(Locale.GERMANY.toString())) {
				lm.put(locale, Locale.GERMANY);
			} else if (locale.equals(Locale.FRANCE.toString())) {
				lm.put(locale, Locale.FRANCE);
			} else if (locale.equals(Locale.TRADITIONAL_CHINESE.toString())) {
				lm.put(locale, Locale.TRADITIONAL_CHINESE);
			} else if (locale.equals(Locale.CHINESE.toString())) {
				lm.put(locale, Locale.CHINESE);
			} else if (locale.equals(Locale.KOREAN.toString())) {
				lm.put(locale, Locale.KOREAN);
			} else if (locale.equals(Locale.ITALIAN.toString())) {
				lm.put(locale, Locale.ITALIAN);
			} else if (locale.equals(Locale.GERMAN.toString())) {
				lm.put(locale, Locale.GERMAN);
			} else {
				log.debug("不正确的语言代码:" + locale + ",build new Locale for "
						+ locale + ".");
				lm.put(locale, new Locale(locale));
			}
		}
		return lm;
	}

	protected Map<Locale, String> convertI18n(Attributes attributes,
			String defaultMessage, String id, String type) {
		if (this.languages == null || this.languages.size() == 0)
			return null;
		Map<Locale, String> extendsAttributes = new HashMap<Locale, String>();
		if (attributes == null || attributes.getLength() == 0) {

		} else {

			int length = attributes.getLength();

			for (int i = 0; i < length; i++) {
				String name = attributes.getQName(i);
				if (name.startsWith("i18n:")) {
					String language = name.substring(5);
					Locale locale = this.languages.get(language);
					if (locale == null) {
						log.debug("[" + id + "]为" + type + "设置了不正确的语言代码:"
								+ language + ",系统忽略处理");
						continue;
					}
					extendsAttributes.put(locale, attributes.getValue(i));
				}
			}
		}
		evalDefaultMessage(extendsAttributes, defaultMessage);
		return extendsAttributes;
	}
	
	protected Map<String, String> evalExtendAttribute(Attributes attributes) {
		
		Map<String, String> extendsAttributes = new HashMap<String, String>();
		if (attributes == null || attributes.getLength() == 0) {

		} else {

			int length = attributes.getLength();

			for (int i = 0; i < length; i++) {
				String name = attributes.getQName(i);
				if (name.startsWith("i18n:") || name.equals("name")
						|| name.equals("id")
						|| name.equals("used")
						|| name.equals("showleftmenu")
						|| name.equals("area")						
						|| name.equals("showpage")
						|| name.equals("target")
						|| name.equals("url")
						|| name.equals("default")
						|| name.equals("showhidden")
						|| name.equals("main")
						) {
					continue;
				}
				else
					extendsAttributes.put(name, attributes.getValue(name));
			}
		}
		
		return extendsAttributes;
	}

	protected void evalDefaultMessage(Map<Locale, String> extendsAttributes,
			String defaultMessage) {
		if (this.languages == null || this.languages.size() == 0)
			return;
		if (defaultMessage != null) {
			Set<String> keys = this.languages.keySet();
			Locale locale = null;
			for (String key : keys) {
				locale = this.languages.get(key);
				if (!extendsAttributes.containsKey(locale)) {
					extendsAttributes.put(locale, defaultMessage);
				}
			}
		}
	}

}
