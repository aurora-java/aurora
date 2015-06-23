package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;


public class GraphicConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "graphic";
	
	public static final String PROPERTITY_FILTERS = "filters";
	public static final String PROPERTITY_DROP_TO = "dropto";
	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_MOVEABLE = "moveable";
	public static final String PROPERTITY_EDITABLE = "editable";
	
	public static GraphicConfig getInstance(){
		GraphicConfig model = new GraphicConfig();
        model.initialize(GraphicConfig.createContext(null,TAG_NAME));
        return model;
    }
	
	public static GraphicConfig getInstance(CompositeMap context){
		GraphicConfig model = new GraphicConfig();
        model.initialize(GraphicConfig.createContext(context,TAG_NAME));
        return model;
    }
	
	public String getFilters(){
        return getString(PROPERTITY_FILTERS);
    }
    public void setShowBorder(String filters){
        putString(PROPERTITY_FILTERS, filters);
    }
    public String getDropTo(){
    	return getString(PROPERTITY_DROP_TO);
    }
    public void setDropTo(String dropTo){
    	putString(PROPERTITY_DROP_TO, dropTo);
    }
    public String getRenderer(){
		return getString(PROPERTITY_RENDERER);
	}
	public void setRenderer(String renderer){
		putString(PROPERTITY_RENDERER,renderer);
	}
	public boolean isMoveable(){
		return getBoolean(PROPERTITY_MOVEABLE, false);
	}
	public void setMoveable(boolean m){
		putBoolean(PROPERTITY_MOVEABLE, m);
	}
	public boolean isEditable(){
		return getBoolean(PROPERTITY_EDITABLE, false);
	}
	public void setEditable(boolean m){
		putBoolean(PROPERTITY_EDITABLE, m);
	}
}
