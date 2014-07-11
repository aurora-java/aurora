package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class BoxConfig extends GridLayoutConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_SHOWBORDER = "showborder";
	public static final String PROPERTITY_LABEL_SEPARATOR = "labelseparator";
	public static final String PROPERTITY_LABEL_WIDTH = "labelwidth";
	
	private int DEFAULT_LABEL_WIDTH = 75;
	
	public Integer getLabelWidth(CompositeMap model){
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_LABEL_WIDTH), model);
		if(null == str||"".equals(str)){
			return new Integer(DEFAULT_LABEL_WIDTH);
		}
		return Integer.valueOf(str);
	}
	
	public void setLabelWidth(int w){
		putInt(PROPERTITY_LABEL_WIDTH, w);
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
