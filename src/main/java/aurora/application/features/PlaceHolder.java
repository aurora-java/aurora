package aurora.application.features;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.proc.IFeature;

public class PlaceHolder implements IFeature {

	public static final String PLACEHOLDER = "_PlaceHolder";

	public int attachTo(CompositeMap config, Configuration procConfig) {
		CompositeMap root = config.getRoot();
		String id = config.getString("id");
		if(id!=null){
			Map holders = (Map) root.get(PLACEHOLDER);
			if (holders == null) {
				holders = new HashMap();
				root.put(PLACEHOLDER, holders);
			}
			CompositeMap view = (CompositeMap)holders.get(id);
			if(view == null){
				holders.put(id, config);
			}
		}
		return IFeature.NORMAL;
	}

}
