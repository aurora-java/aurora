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
