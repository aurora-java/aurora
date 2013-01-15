package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class BoxConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String ROWS = "row";
	public static final String COLUMNS = "column";
	
	public static final String PROPERTITY_CELLPADDING = "cellpadding";
	public static final String PROPERTITY_CELLSPACING = "cellspacing";
	public static final String PROPERTITY_VALIDALIGN = "validalign";
	public static final String PROPERTITY_PADDING = "padding";
	public static final String PROPERTITY_SHOWBORDER = "showborder";
	public static final String PROPERTITY_LABEL_SEPARATOR = "labelseparator";
	public static final String PROPERTITY_LABEL_WIDTH = "labelwidth";
	
	private int DEFAULT_LABEL_WIDTH = 75;
	
	public int getLabelWidth(){
		return getInt(PROPERTITY_LABEL_WIDTH, DEFAULT_LABEL_WIDTH);
	}
	
	public void setLabelWidth(int w){
		putInt(PROPERTITY_LABEL_WIDTH, w);
	}
	
	public int getRows() {
		return getInt(ROWS, -1);		
	}
	public void setRows(int rows){
		putInt(ROWS, rows);
	}
	
	public int getColumns() {
		return getInt(COLUMNS, -1);		
	}
	public void setColumns(int columns){
		putInt(COLUMNS, columns);
	}
	
	public int getCellpadding(){
		return getInt(PROPERTITY_CELLPADDING, 0);
	}
	public void setCellpadding(int padding){
		putInt(PROPERTITY_CELLPADDING, padding);
	}
	
	public int getCellspacing(){
		return getInt(PROPERTITY_CELLSPACING, 0);
	}
	public void setCellspacing(int spacing){
		putInt(PROPERTITY_CELLSPACING, spacing);
	}
	
	public int getPadding(){
		return getInt(PROPERTITY_PADDING, 0);
	}
	public void setPadding(int padding){
		putInt(PROPERTITY_PADDING, padding);
	}
	
	
	public void addChild(CompositeMap item){
		CompositeMap context = getObjectContext();
		context.addChild(item);
	}
	
	public Boolean isShowBorder(){
        return getBoolean(PROPERTITY_SHOWBORDER);
    }
    public void setShowBorder(boolean show){
        putBoolean(PROPERTITY_SHOWBORDER, show);
    }
    
    public String getLabelSeparator(){
    	return getString(PROPERTITY_LABEL_SEPARATOR);
    }
    public void setLabelSeparator(String labelSeparator){
    	putString(PROPERTITY_LABEL_SEPARATOR, labelSeparator);
    }
	
}
