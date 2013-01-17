package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.CurrencyLabelConfig;
import aurora.presentation.component.std.config.LabelConfig;

public class CurrencyLabel extends Label {
	
	public static final String VERSION = "$Revision$";
	
	private static final String DEFAULT_RENDERER = "Aurora.formatMoney";
	private static final String DEFAULT_TEMPLATE = "label.tplt";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		context.setTemplate(session.getTemplateByName(DEFAULT_TEMPLATE));
		CompositeMap view = context.getView();
		CurrencyLabelConfig lc = CurrencyLabelConfig.getInstance(view);
		
		String renderer = lc.getRenderer();
		if(renderer==null)view.putString(LabelConfig.PROPERTITY_RENDERER, DEFAULT_RENDERER);		
		super.onCreateViewContent(session, context);
	}
}
