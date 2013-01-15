package aurora.presentation.component.std.config;

public class GridLayouConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	public static final String PROPERTITY_CELLPADDING = "cellpadding";
	public static final String PROPERTITY_CELLSPACING = "cellspacing";
	public static final String PROPERTITY_VALIDALIGN = "validalign";
	public static final String PROPERTITY_PADDING = "padding";
	public static final String PROPERTITY_WRAPPER_ADJUST = "wrapperadjust";
	
	public int getCellPadding(){
		return getInt(PROPERTITY_CELLPADDING,0);
	}
	public void setCellPadding(int padding){
		putInt(PROPERTITY_CELLPADDING, padding);
	}
	
	public int getCellSpacing(){
		return getInt(PROPERTITY_CELLSPACING,0);
	}
	public void setCellSpacing(int v){
		putInt(PROPERTITY_CELLSPACING, v);
	}
	
	public int getPadding(int defaultValue){
		return getInt(PROPERTITY_PADDING,defaultValue);
	}
	public void setPadding(int v){
		putInt(PROPERTITY_PADDING, v);
	}
	
	public boolean isWrapperAdjust(){
		return getBoolean(PROPERTITY_WRAPPER_ADJUST, true);
	}
	public void setWrapperAdjust(boolean v){
		putBoolean(PROPERTITY_WRAPPER_ADJUST, v);
	}
}
