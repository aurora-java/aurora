package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.NumberFieldConfig;

public class NumberField extends TextField {	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap model = context.getModel();
		CompositeMap view = context.getView();
		
		NumberFieldConfig nfc = NumberFieldConfig.getInstance(view);
		
		if(!nfc.isAllowDecimals())addConfig(NumberFieldConfig.PROPERTITY_ALLOWDECIMALS, new Boolean(false));
		if(!nfc.isAllowNegative())addConfig(NumberFieldConfig.PROPERTITY_ALLOWNEGATIVE, new Boolean(false));
		if(!nfc.isAllowFormat())addConfig(NumberFieldConfig.PROPERTITY_ALLOWFORMAT, new Boolean(false));
		
		addConfig(NumberFieldConfig.PROPERTITY_DECIMALPRECISION, new Integer(nfc.getDecimalPrecision(model)));
		
		Map map = context.getMap();		
		map.put(INPUT_TYPE, DEFAULT_INPUT_TYPE);
		map.put(CONFIG, getConfigString());
	}
}
