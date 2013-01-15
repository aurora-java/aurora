package aurora.presentation.component.std.config;

public class InputFieldConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_EMPTYTEXT = "emptytext";
	public static final String PROPERTITY_INPUTWIDTH = "inputwidth";
	public static final String PROPERTITY_EDITABLE = "editable";

	private int DEFAULT_INPUT_WIDTH = 100;
	
	public String getEmptyText() {
		return getString(PROPERTITY_EMPTYTEXT,"");
	}

	public void setEmptyText(String text) {
		putString(PROPERTITY_EMPTYTEXT, text);
	}

	public int getInputWidth() {
		return getInt(PROPERTITY_INPUTWIDTH, DEFAULT_INPUT_WIDTH);
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
}
