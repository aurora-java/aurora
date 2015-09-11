package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;
import aurora.presentation.component.std.config.LovConfig;

public class Lov extends TextField {
	
	public Lov(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		map.put(InputFieldConfig.PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-23));
		
		LovConfig lc = LovConfig.getInstance(view);
		
//		addConfig(LovConfig.PROPERTITY_LOV_URL, lc.getLovUrl());
//		addConfig(LovConfig.PROPERTITY_TITLE, lc.getTitle());
//		addConfig(LovConfig.PROPERTITY_VALUE_FIELD, lc.getValueField());
//		addConfig(LovConfig.PROPERTITY_DISPLAY_FIELD, lc.getDisplayField());
//		addConfig(LovConfig.PROPERTITY_LOV_MODEL, lc.getLovModel());
//		addConfig(LovConfig.PROPERTITY_LOV_SERVICE, lc.getLovService());
//		addConfig(LovConfig.PROPERTITY_LOV_WIDTH, new Integer(lc.getLovWidth()));
//		addConfig(LovConfig.PROPERTITY_LOV_AUTO_QUERY, Boolean.valueOf(lc.getLovAutoQuery()));
//		addConfig(LovConfig.PROPERTITY_LOV_LABEL_WIDTH, new Integer(lc.getLovLabelWidth()));
//		addConfig(LovConfig.PROPERTITY_LOV_HEIGHT, new Integer(lc.getLovHeight()));
//		addConfig(LovConfig.PROPERTITY_LOV_GRID_HEIGHT, new Integer(lc.getLovGridHeight()));
//		addConfig(LovConfig.PROPERTITY_FETCH_REMOTE, Boolean.valueOf(lc.getFetchRemote()));
//		addConfig(LovConfig.PROPERTITY_FETCH_SINGLE, Boolean.valueOf(lc.getFetchSingle()));
//		
//		String renderer = lc.getAutocompleteRenderer();
//		if(renderer != null)addConfig(LovConfig.PROPERTITY_AUTOCOMPLETE_RENDERER, renderer);
		map.put(CONFIG, getConfigString());
	}
}
