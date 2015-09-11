package aurora.presentation.component.std.config;

import aurora.presentation.BuildSession;
import uncertain.composite.CompositeMap;

public class InputFieldConfig extends FieldConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_EMPTYTEXT = "emptytext";
	public static final String PROPERTITY_INPUTWIDTH = "inputwidth";
	public static final String PROPERTITY_EDITABLE = "editable";
	public static final String PROPERTITY_MAX_LENGHT = "maxlength";
	public static final String PROPERTITY_CHARA_TRANSFORM = "transformcharacter";
	public static final String PROPERTITY_AUTO_SELECT = "autoselect";
	public static final String PROPERTITY_FONT_STYLE = "fontstyle";
	

	private int DEFAULT_INPUT_WIDTH = 100;
	
	public String getEmptyText() {
		return getString(PROPERTITY_EMPTYTEXT,"");
	}
	
	public String getEmptyText(BuildSession session,CompositeMap model) {
		return session.getLocalizedPrompt(uncertain.composite.TextParser.parse(getString(PROPERTITY_EMPTYTEXT,""),model));
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
	
    public Integer getMaxLength(){
        return getInteger(PROPERTITY_MAX_LENGHT);
    }
    public void setMaxLength(Integer ml){
        putInt(PROPERTITY_MAX_LENGHT, ml);
    }
    
    public boolean isTransformCharacter(boolean defaultvalue){
        return getBoolean(PROPERTITY_CHARA_TRANSFORM, defaultvalue);
    }
    public void setTransformCharacter(boolean tc){
        putBoolean(PROPERTITY_CHARA_TRANSFORM, tc);
    }
    public boolean isAutoSelect(){
    	return getBoolean(PROPERTITY_AUTO_SELECT, true);
    }
    public void setAutoSelect(boolean as){
    	putBoolean(PROPERTITY_AUTO_SELECT, as);
    }

	public String getFontStyle(CompositeMap model) {
		return uncertain.composite.TextParser.parse(getString(PROPERTITY_FONT_STYLE), model);
	}
	
	public void setFontStyle(String fontStyle) {
		putString(PROPERTITY_FONT_STYLE, fontStyle);
	}
}
