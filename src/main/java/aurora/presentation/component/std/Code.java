package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.ocm.IObjectRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

public class Code extends Component implements IViewBuilder {
	
	public Code(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";

	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		session.getWriter().write(
				uncertain.composite.TextParser.parse(view_context.getView()
						.getText(), view_context.getModel()));
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
