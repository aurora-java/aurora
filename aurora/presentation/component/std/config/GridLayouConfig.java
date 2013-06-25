package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class GridLayouConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	public static final String PROPERTITY_CELLPADDING = "cellpadding";
	public static final String PROPERTITY_CELLSPACING = "cellspacing";
	public static final String PROPERTITY_VALIDALIGN = "validalign";
	public static final String PROPERTITY_PADDING = "padding";
	public static final String PROPERTITY_ROW = "row";
	public static final String PROPERTITY_COLUMN = "column";
	public static final String PROPERTITY_WRAPPER_ADJUST = "wrapperadjust";
	public static final String PROPERTITY_ROWSPAN = "rowspan";
	public static final String PROPERTITY_COLSPAN = "colspan";
	
	public int getCellPadding(CompositeMap model){
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_CELLPADDING), model);
		if(null == str||"".equals(str)){
			return 0;
		}
		return Integer.valueOf(str).intValue();
	}
	public void setCellPadding(int padding){
		putInt(PROPERTITY_CELLPADDING, padding);
	}
	
	public int getCellSpacing(CompositeMap model){
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_CELLSPACING), model);
		if(null == str||"".equals(str)){
			return 0;
		}
		return Integer.valueOf(str).intValue();
	}
	public void setCellSpacing(int v){
		putInt(PROPERTITY_CELLSPACING, v);
	}
	
	public int getPadding(CompositeMap model,int defaultValue){
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_PADDING), model);
		if(null == str||"".equals(str)){
			return defaultValue;
		}
		return Integer.valueOf(str).intValue();
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
	public int getRow(CompositeMap model,int defaultValue){
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_ROW), model);
		if(null == str||"".equals(str)){
			return defaultValue;
		}
		return Integer.valueOf(str).intValue();
	}
	public void setRow(int row){
		putInt(PROPERTITY_ROW,row);
	}
	
	public int getColumn(CompositeMap model,int defaultValue){
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_COLUMN), model);
		if(null == str||"".equals(str)){
			return defaultValue;
		}
		return Integer.valueOf(str).intValue();
	}
	public void setColumn(int column){
		putInt(PROPERTITY_COLUMN,column);
	}
}
