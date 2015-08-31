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
	public static final String KEY_DEFAULT_AUTO_APPEND = "defaultautoappend";
	public static final String KEY_DEFAULT_AUTO_ADJUST_GRID = "defaultautoadjustgrid";
	public static final String KEY_DEFAULT_MODIFIED_CHECK = "defaultmodifiedcheck";
	public static final String KEY_DEFAULT_GRID_SUBMASK = "defaultgridsubmask";
	public static final String KEY_DEFAULT_FUZZY_FETCH = "defaultfuzzyfetch";
	public static final String KEY_DEFAULT_CLIENT_RESIZE = "defaultclientresize";
	public static final String KEY_DEFAULT_EDITOR_BORDER = "defaulteditorborder";
	
	public static final String DEFAULT_THEME = "default";
	public static final boolean DEFAULT_FUZZY_FETCH = false;
	public static final boolean DEFAULT_MODIFIED_CHECK = true;
	public static final boolean DEFAULT_AUTO_COUNT = true;
	public static final boolean DEFAULT_AUTO_ADJUST_GRID = true;
	public static final boolean DEFAULT_GRID_SUBMASK = true;
	public static final boolean DEFAULT_CLIENT_RESIZE = true;
	public static final boolean DEFAULT_AUTO_APPEND = false;
	public static final boolean DEFAULT_EDITOR_BORDER = true;
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_MARGIN_WIDTH = -1;
	public static final String DEFAULT_LABEL_SEPARATOR = ":";
	public static final String DEFAULT_RADIO_SEPARATOR = ":";
	
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
		return getString(KEY_DEFAULT_LABEL_SEPARATOR,DEFAULT_LABEL_SEPARATOR);
	}

	public void setDefaultLabelSeparator(String labelSeparator) {
		putString(KEY_DEFAULT_LABEL_SEPARATOR, labelSeparator);
	}
	
	public int getDefaultMarginWidth() {
		return getInt(KEY_DEFAULT_MARGIN_WIDTH,DEFAULT_MARGIN_WIDTH);
	}

	public void setDefaultMarginWidth(int w) {
		putInt(KEY_DEFAULT_MARGIN_WIDTH, w);
	}

	public String getDefaultRadioSeparator() {
		return getString(KEY_DEFAULT_RADIO_SEPARATOR,DEFAULT_RADIO_SEPARATOR);
	}

	public void setDefaultRadioSeparator(String radioseparator) {
		putString(KEY_DEFAULT_RADIO_SEPARATOR, radioseparator);
	}
	
	public boolean getDefaultAutoCount() {
		return getBoolean(KEY_DEFAULT_AUTO_COUNT,DEFAULT_AUTO_COUNT);
	}
	

	public void setDefaultAutoCount(boolean autoCount) {
		putBoolean(KEY_DEFAULT_AUTO_COUNT, autoCount);
	}
	
	public void setDefaultAutoAdjustGrid(boolean autoAdjust) {
		putBoolean(KEY_DEFAULT_AUTO_ADJUST_GRID, autoAdjust);
	}
	
	public boolean getDefaultAutoAdjustGrid() {
		return getBoolean(KEY_DEFAULT_AUTO_ADJUST_GRID,DEFAULT_AUTO_ADJUST_GRID);
	}
	public void setDefaultModifiedCheck(boolean modifiedCheck) {
		putBoolean(KEY_DEFAULT_MODIFIED_CHECK, modifiedCheck);
	}
	
	public boolean getDefaultGridSubmask() {
		return getBoolean(KEY_DEFAULT_GRID_SUBMASK,DEFAULT_GRID_SUBMASK);
	}
	public void setDefaultGridSubmask(boolean submask) {
		putBoolean(KEY_DEFAULT_GRID_SUBMASK, submask);
	}
	public boolean getDefaultFuzzyFetch() {
		return getBoolean(KEY_DEFAULT_FUZZY_FETCH,DEFAULT_FUZZY_FETCH);
	}
	public void setDefaultFuzzyFetch(boolean fuzzyFetch) {
		putBoolean(KEY_DEFAULT_FUZZY_FETCH, fuzzyFetch);
	}
	public boolean getDefaultClientResize() {
		return getBoolean(KEY_DEFAULT_CLIENT_RESIZE,DEFAULT_CLIENT_RESIZE);
	}
	public void setDefaultClientResize(boolean fuzzyFetch) {
		putBoolean(KEY_DEFAULT_CLIENT_RESIZE, fuzzyFetch);
	}
	
	public boolean getDefaultModifiedCheck() {
		return getBoolean(KEY_DEFAULT_MODIFIED_CHECK,DEFAULT_MODIFIED_CHECK);
	}
	
	public boolean getDefaultAutoAppend() {
		return getBoolean(KEY_DEFAULT_AUTO_APPEND,DEFAULT_AUTO_APPEND);
	}

	public void setDefaultAutoAppend(boolean autoAppend) {
		putBoolean(KEY_DEFAULT_AUTO_APPEND, autoAppend);
	}
	public boolean getDefaultEditorBorder() {
		return getBoolean(KEY_DEFAULT_EDITOR_BORDER,DEFAULT_EDITOR_BORDER);
	}
	
	public void setDefaultEditorBorder(boolean editorborder) {
		putBoolean(KEY_DEFAULT_EDITOR_BORDER, editorborder);
	}

	public int getDefaultPageSize() {
		try {
			return getInt(KEY_DEFAULT_PAGE_SIZE, DEFAULT_PAGE_SIZE);
		} catch (NumberFormatException e) {
		}
		return DEFAULT_PAGE_SIZE;
	}

	public void setDefaultPageSize(int size) {
		putInt(KEY_DEFAULT_PAGE_SIZE, size);
	}

}
