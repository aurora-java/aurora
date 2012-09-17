package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.ocm.ISingleton;

import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

public class HotKey extends Component  implements IViewBuilder, ISingleton {
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
	}

	@Override
	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
}
