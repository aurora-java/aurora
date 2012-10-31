package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.NumberFieldConfig;

public class CurrencyField extends NumberField {
	
	private static final String DEFAULT_TEMPLATE = "numberField.tplt";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		context.setTemplate(session.getTemplateByName(DEFAULT_TEMPLATE));
		CompositeMap view = context.getView();
		view.putBoolean(NumberFieldConfig.PROPERTITY_ALLOWDECIMALS, true);
		view.putBoolean(NumberFieldConfig.PROPERTITY_ALLOWNEGATIVE, false);
		view.putInt(NumberFieldConfig.PROPERTITY_DECIMALPRECISION, 2);
		view.putBoolean(NumberFieldConfig.PROPERTITY_ALLOWFORMAT, true);
		
		super.onCreateViewContent(session, context);
	}
}
