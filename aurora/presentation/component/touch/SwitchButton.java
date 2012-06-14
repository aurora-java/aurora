package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.IDGenerator;
import aurora.presentation.component.std.config.ComponentConfig;

@SuppressWarnings("unchecked")
public class SwitchButton extends Component {
	
	public static final String PROPERTITY_ON = "on";
	public static final String PROPERTITY_OFF = "off";
	public static final String PROPERTITY_ON_VALUE = "onvalue";
	public static final String PROPERTITY_OFF_VALUE = "offvalue";
	public static final String PROPERTITY_VALUE = "value";
	public static final String PROPERTITY_DEFAULT_STATUS = "defaultvalue";
	private static final String DEFAULT_CLASS = "switch-button";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		Map map = context.getMap();
		CompositeMap view = context.getView();
		id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if ("".equals(id))
			id = IDGenerator.getInstance().generate();
		super.onCreateViewContent(session, context);
		addConfig(ComponentConfig.PROPERTITY_ID, id);
		String on = view.getString(PROPERTITY_ON,"开");
		addConfig(PROPERTITY_ON, on);
		map.put(PROPERTITY_ON, on);
		String off = view.getString(PROPERTITY_OFF,"关");
		addConfig(PROPERTITY_OFF, off);
		map.put(PROPERTITY_OFF, off);
		String value = view.getString(PROPERTITY_VALUE,"");
		if(!"".equals(value)){
			addConfig(PROPERTITY_VALUE, value);
		}
		addConfig(PROPERTITY_ON_VALUE, view.getString(PROPERTITY_ON_VALUE,"Y"));
		addConfig(PROPERTITY_OFF_VALUE, view.getString(PROPERTITY_OFF_VALUE,"N"));
		addConfig(PROPERTITY_DEFAULT_STATUS, view.getString(PROPERTITY_DEFAULT_STATUS,"off"));
		
		map.put(CONFIG, getConfigString());
	}
}
