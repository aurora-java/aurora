package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.NumberFieldConfig;

@SuppressWarnings("unchecked")
public class NumberField extends TextField {	
	
	public NumberField(IObjectRegistry registry) {
		super(registry);
	}


	public static final String VERSION = "$Revision$";
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap model = context.getModel();
		CompositeMap view = context.getView();
		
		NumberFieldConfig nfc = NumberFieldConfig.getInstance(view);
		
		if(!nfc.isAllowDecimals())addConfig(NumberFieldConfig.PROPERTITY_ALLOWDECIMALS, Boolean.FALSE);
		if(!nfc.isAllowNegative())addConfig(NumberFieldConfig.PROPERTITY_ALLOWNEGATIVE, Boolean.FALSE);
		if(!nfc.isAllowFormat())addConfig(NumberFieldConfig.PROPERTITY_ALLOWFORMAT, Boolean.FALSE);
		if(!nfc.isAllowPad()) addConfig(NumberFieldConfig.PROPERTITY_ALLOWPAD, Boolean.FALSE);
		if (null != nfc.getMin())
			addConfig(NumberFieldConfig.PROPERTITY_MIN, TextParser.parse(nfc.getMin(), model));
		if (null != nfc.getMax())
			addConfig(NumberFieldConfig.PROPERTITY_MAX,  TextParser.parse(nfc.getMax(), model));
		
		addConfig(NumberFieldConfig.PROPERTITY_DECIMALPRECISION, new Integer(nfc.getDecimalPrecision(model)));
		
		Map map = context.getMap();
		map.put(INPUT_TYPE, DEFAULT_INPUT_TYPE);
		map.put(CONFIG, getConfigString());
	}
}
