package com.zhurylomihaylo.www.easyconnect;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "com.zhurylomihaylo.www.easyconnect.messages"; //$NON-NLS-1$

	private static ResourceBundle RESOURCE_BUNDLE;

	private Messages() {
	}
	
	static void init() {
		defineResourceBundle();
	}
	
	static void defineResourceBundle() {
		String languageTag = Props.get("languageTag");
		if (languageTag == null || languageTag.equals("auto"))
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
		else
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.forLanguageTag(languageTag));
	}

	static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
