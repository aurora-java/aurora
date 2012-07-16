package aurora.presentation.component.std.config;

public class InputFieldConfig extends ComponentConfig {

	public static final String PROPERTITY_EMPTYTEXT = "emptytext";
	public static final String PROPERTITY_INPUTWIDTH = "inputwidth";
	public static final String PROPERTITY_EDITABLE = "editable";
	public static final String PROPERTITY_TABINDEX = "tabindex";

	public String getEmptyText() {
		return getString(PROPERTITY_EMPTYTEXT);
	}

	public void setEmptyText(String text) {
		putString(PROPERTITY_EMPTYTEXT, text);
	}

	public int getInputWidth() {
		return getInt(PROPERTITY_INPUTWIDTH, 100);
	}

	public void setInputWidth(int width) {
		putInt(PROPERTITY_INPUTWIDTH, width);
	}

	public boolean isEditable() {
		return getBoolean(PROPERTITY_EDITABLE, true);
	}

	public void setEditable(boolean editable) {
		put(PROPERTITY_EDITABLE, editable);
	}

	public int getTabIndex() {
		return getInt(PROPERTITY_TABINDEX, -1);
	}

	public void setTabIndex(int index) {
		putInt(PROPERTITY_TABINDEX, index);
	}
}
