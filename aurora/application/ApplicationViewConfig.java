/*
 * Created on 2010-6-18 下午02:30:24
 * $Id$
 */
package aurora.application;

import uncertain.composite.DynamicObject;

/**
 * UI related config section in application config
 */
public class ApplicationViewConfig extends DynamicObject {

	public static final String KEY_DEFAULT_TEMPLATE = "defaulttemplate";
	public static final String KEY_DEFAULT_PACKAGE = "defaultpackage";
	public static final String KEY_DEFAULT_TITLE = "defaulttitle";
	public static final String KEY_DEFAULT_LABEL_SEPARATOR = "defaultlabelseparator";
	public static final String KEY_DEFAULT_RADIO_SEPARATOR = "defaultradioseparator";
	public static final String KEY_DEFAULT_PAGE_SIZE = "defaultpagesize";
	public static final String KEY_DEFAULT_MARGIN_WIDTH = "defaultmarginwidth";
	public static final String KEY_DEFAULT_THEME = "defaulttheme";
	public static final String KEY_DEFAULT_RESOURCE_CACHE_NAME = "resourcecachename";
	public static final String KEY_DEFAULT_AUTO_COUNT = "defaultautocount";
	
	private static final String DEFAULT_THEME = "default";

	public String getDefaultPackage() {
		return getString(KEY_DEFAULT_PACKAGE);
	}

	public void setDefaultPackage(String pkg) {
		putString(KEY_DEFAULT_PACKAGE, pkg);
	}

	public String getDefaultTemplate() {
		return getString(KEY_DEFAULT_TEMPLATE);
	}

	public void setDefaultTemplate(String template) {
		putString(KEY_DEFAULT_TEMPLATE, template);
	}

	public String getDefaultTitle() {
		return getString(KEY_DEFAULT_TITLE);
	}

	public void setDefaultTitle(String title) {
		putString(KEY_DEFAULT_TITLE, title);
	}
	
	public String getDefaultTheme() {
		return getString(KEY_DEFAULT_THEME, DEFAULT_THEME);
	}

	public void setDefaultTheme(String theme) {
		putString(KEY_DEFAULT_THEME, theme);
	}
	
	public String getResourceCacheName() {
		return getString(KEY_DEFAULT_RESOURCE_CACHE_NAME, "");
	}

	public void setResourceCacheName(String theme) {
		putString(KEY_DEFAULT_RESOURCE_CACHE_NAME, theme);
	}

	public String getDefaultLabelSeparator() {
		return getString(KEY_DEFAULT_LABEL_SEPARATOR);
	}

	public void setDefaultLabelSeparator(String radioseparator) {
		putString(KEY_DEFAULT_LABEL_SEPARATOR, radioseparator);
	}
	
	public int getDefaultMarginWidth() {
		return getInt(KEY_DEFAULT_MARGIN_WIDTH,-1);
	}

	public void setDefaultMarginWidth(int w) {
		putInt(KEY_DEFAULT_MARGIN_WIDTH, w);
	}

	public String getDefaultRadioSeparator() {
		return getString(KEY_DEFAULT_RADIO_SEPARATOR);
	}

	public void setDefaultRadioSeparator(String radioseparator) {
		putString(KEY_DEFAULT_RADIO_SEPARATOR, radioseparator);
	}
	
	public boolean getDefaultAutoCount() {
		return getBoolean(KEY_DEFAULT_AUTO_COUNT,true);
	}

	public void setDefaultAutoCount(boolean autoCount) {
		putBoolean(KEY_DEFAULT_AUTO_COUNT, autoCount);
	}

	public int getDefaultPageSize() {
		try {
			return getInt(KEY_DEFAULT_PAGE_SIZE, -1);
		} catch (NumberFormatException e) {
		}
		return -1;
	}

	public void setDefaultPageSize(int size) {
		putInt(KEY_DEFAULT_PAGE_SIZE, size);
	}

}
