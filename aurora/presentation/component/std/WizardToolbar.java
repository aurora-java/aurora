package aurora.presentation.component.std;

import java.io.IOException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;

public class WizardToolbar implements IViewBuilder, ISingleton{
	
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		CompositeLoader loader = new CompositeLoader();
		try {
			CompositeMap div = loader.loadFromString("<div class='win-toolbar' style='width:100%;height:40px;position:absolute; bottom:0px;'></div>");
			view.setName("hBox");
			view.put(ComponentConfig.PROPERTITY_STYLE, "float:right;margin-right:10px;margin-top:5px;");
			div.addChild(view);
			session.buildView(model, div);
		} catch (Exception e) {
			throw new IOException(e);
		} 
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
	
	
}
