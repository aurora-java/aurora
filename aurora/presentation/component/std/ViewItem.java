package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

public class ViewItem implements IViewBuilder, ISingleton {
	
	public static final String PROPERTITY_VALUE = "value";

	@Override
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Writer out = session.getWriter();
		String val = view.getString(PROPERTITY_VALUE,"");
		String value = uncertain.composite.TextParser.parse(val, model);
		out.write("<span class='item-view'>"+value+"</span>");
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
