package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;


public class PrintableScreen implements IViewBuilder, ISingleton {

	@Override
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		try {
			session.buildViews(model, view.getChilds());
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
		
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		// TODO Auto-generated method stub
		return null;
	}
}
