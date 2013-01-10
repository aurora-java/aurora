package aurora.presentation.component.std.config;

import aurora.application.AuroraApplication;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class ButtonConfig extends DynamicObject {
	
	public static final String VERSION = "$Revision$";
	public static final String TAG_NAME = "button";
	
	private String PROPERTITY_TEXT = "text";
	private String PROPERTITY_ICON = "icon";
	private String PROPERTITY_ICON_ALIGN = "iconalign";
	private String DEFAULT_ICON_ALIGN = "left";
	
	public static CompositeMap createContext(CompositeMap map) {
		CompositeMap context = new CompositeMap(TAG_NAME);
		context.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		if(map != null){
			context.copy(map);
		}
		return context;		
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
}
