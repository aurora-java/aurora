package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;
import aurora.application.AuroraApplication;


public class FieldBoxColumnConfig extends FieldBoxConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "FieldBoxColumn";

	
	public static FieldBoxColumnConfig getInstance(){
		FieldBoxColumnConfig model = new FieldBoxColumnConfig();
		CompositeMap map = FieldBoxColumnConfig.createContext(null,TAG_NAME);
		map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
        model.initialize(map);
        return model;
    }
	
	public static FieldBoxColumnConfig getInstance(CompositeMap context){
		FieldBoxColumnConfig model = new FieldBoxColumnConfig();
		CompositeMap map = FieldBoxColumnConfig.createContext(context,TAG_NAME);
		map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
        model.initialize(map);
        return model;
    }

	public Integer getLabelWidth(CompositeMap model) {
		String str = uncertain.composite.TextParser.parse(
				getString(PROPERTITY_LABEL_WIDTH), model);
		if (null == str || "".equals(str)) {
			return null;
		}
		return Integer.valueOf(str);
	}
}
