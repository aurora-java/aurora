package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class NumberField extends TextField {
	
	protected static final String PROPERTITY_ALLOWDECIMALS = "allowdecimals";
	protected static final String PROPERTITY_DECIMALPRECISION = "decimalprecision";	
	protected static final String PROPERTITY_ALLOWNEGATIVE = "allownegative";	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		
		Boolean allowDecimals = new Boolean(view.getBoolean(PROPERTITY_ALLOWDECIMALS, true));
		addConfig(PROPERTITY_ALLOWDECIMALS, allowDecimals);
		
		Boolean allowNegative = new Boolean(view.getBoolean(PROPERTITY_ALLOWNEGATIVE, true));
		addConfig(PROPERTITY_ALLOWNEGATIVE, allowNegative);
		
		Integer decimalPrecision = new Integer(view.getInt(PROPERTITY_DECIMALPRECISION, 2));
		addConfig(PROPERTITY_DECIMALPRECISION, decimalPrecision);
		
		Map map = context.getMap();		
		map.put(INPUT_TYPE, "input");
		map.put(CONFIG, getConfigString());
	}
}
