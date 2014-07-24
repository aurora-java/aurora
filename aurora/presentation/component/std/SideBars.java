package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.event.EventModel;

import aurora.events.E_PrepareServiceConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.service.IService;

public class SideBars implements IViewBuilder, E_PrepareServiceConfig{

	@Override
	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		try {
			session.buildViews(view_context.getModel(), view_context.getView().getChilds());
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
		
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

	@Override
	public int onPrepareServiceConfig(IService service) throws Exception {
		return EventModel.HANDLE_NORMAL;
	}

}
