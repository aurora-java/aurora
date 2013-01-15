package aurora.presentation.component.std.config;

import aurora.application.AuroraApplication;
import uncertain.composite.CompositeMap;

public class ButtonConfig extends FieldConfig {
	
	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "button";
	public static final String PROPERTITY_TEXT = "text";
	public static final String PROPERTITY_ICON = "icon";
	public static final String PROPERTITY_ICON_ALIGN = "iconalign";
	public static final String PROPERTITY_CLICK = "click";
	public static final String PROPERTITY_TITLE = "title";
	public static final String PROPERTITY_BUTTON_STYLE = "btnstyle";
	public static final String PROPERTITY_BUTTON_CLASS = "btnclass";
	public static final String PROPERTITY_DISABLED = "disabled";
	
	private String DEFAULT_ICON_ALIGN = "left";
	
	public static ButtonConfig getInstance(){
		ButtonConfig model = new ButtonConfig();
        model.initialize(ButtonConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static ButtonConfig getInstance(CompositeMap context){
		ButtonConfig model = new ButtonConfig();
        model.initialize(ButtonConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getText(){
        return getString(PROPERTITY_TEXT, "");
    }
    public void setText(String text){
        putString(PROPERTITY_TEXT, text);
    }
    
    public String getIcon(){
        return getString(PROPERTITY_ICON, "");
    }
    public void setIcon(String icon){
        putString(PROPERTITY_ICON, icon);
    }
    
    public String getIconAlign(){
        return getString(PROPERTITY_ICON_ALIGN, DEFAULT_ICON_ALIGN);
    }
    public void setIconAlign(String iconAlign){
        putString(PROPERTITY_ICON_ALIGN, iconAlign);
    }
    
    public String getClick(){
        return getString(PROPERTITY_CLICK, "");
    }
    public void setClick(String click){
        putString(PROPERTITY_CLICK, click);
    }
    
    public String getTitle(){
        return getString(PROPERTITY_TITLE, "");
    }
    public void setTitle(String title){
        putString(PROPERTITY_TITLE, title);
    }
    
    public String getButtonStyle(){
        return getString(PROPERTITY_BUTTON_STYLE, "");
    }
    public void setButtonStyle(String style){
        putString(PROPERTITY_BUTTON_STYLE, style);
    }
    
    public String getButtonClass(){
        return getString(PROPERTITY_BUTTON_CLASS, "");
    }
    public void setButtonClass(String clz){
        putString(PROPERTITY_BUTTON_CLASS, clz);
    }
    
    public boolean getDisabled(){
        return getBoolean(PROPERTITY_DISABLED, false);
    }
    public void setDisabled(boolean dis){
        putBoolean(PROPERTITY_DISABLED, dis);
    }
}
