package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;
import aurora.application.AuroraApplication;

public class FieldGroupConfig extends FieldBoxConfig {

	public static final String VERSION = "$Revision$";

	public static final String TAG_NAME = "FieldBox";

	public static final String PROPERTITY_FIELDBOX_COLUMNS = "fieldBoxColumns";
	public static final String PROPERTITY_FIELD_WIDTH = "fieldwidth";
	public static final String PROPERTITY_FIELD_HEIGHT = "fieldheight";

	public static FieldGroupConfig getInstance() {
		FieldGroupConfig model = new FieldGroupConfig();
		CompositeMap map = FieldGroupConfig.createContext(null, TAG_NAME);
		map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		model.initialize(map);
		return model;
	}

	public static FieldGroupConfig getInstance(CompositeMap context) {
		FieldGroupConfig model = new FieldGroupConfig();
		CompositeMap map = FieldGroupConfig.createContext(context, TAG_NAME);
		map.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		model.initialize(map);
		return model;
	}

	public CompositeMap getFieldBoxColumns() {
		return getObjectContext().getChild(PROPERTITY_FIELDBOX_COLUMNS);
	}

	public Integer getFieldWidth(CompositeMap model) {
		String str = uncertain.composite.TextParser.parse(
				getString(PROPERTITY_FIELD_WIDTH), model);
		if (null == str || "".equals(str)) {
			return null;
		}
		return Integer.valueOf(str);
	}

	public void setFieldWidth(int fieldWidth) {
		putInt(PROPERTITY_FIELD_WIDTH, fieldWidth);
	}
	
	public Integer getFieldHeight(CompositeMap model) {
		String str = uncertain.composite.TextParser.parse(
				getString(PROPERTITY_FIELD_HEIGHT), model);
		if (null == str || "".equals(str)) {
			return null;
		}
		return Integer.valueOf(str);
	}
	
	public void setFieldHeight(int fieldHeight) {
		putInt(PROPERTITY_FIELD_HEIGHT, fieldHeight);
	}

}
