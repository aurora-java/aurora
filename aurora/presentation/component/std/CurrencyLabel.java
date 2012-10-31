package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class CurrencyLabel extends Label {
	
	private static final String DEFAULT_RENDERER = "Aurora.formatMoney";
	private static final String DEFAULT_TEMPLATE = "label.tplt";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		context.setTemplate(session.getTemplateByName(DEFAULT_TEMPLATE));
		CompositeMap view = context.getView();
		
		String renderer = view.getString(PROPERTITY_RENDERER);
		if(renderer==null)view.putString(PROPERTITY_RENDERER, DEFAULT_RENDERER);		
		super.onCreateViewContent(session, context);
	}
}
