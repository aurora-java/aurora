package aurora.presentation.component.touch;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;

@SuppressWarnings("unchecked")
public class ScreenBody extends Component implements IViewBuilder {


	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();

		/** ID属性 **/
		String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if (!"".equals(id)) {
			id = "id=\"" + id + "\"";
		}
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		if (!"".equals(style)) {
			style = "style=\"" + style + "\"";
		}
		Iterator it = view.getChildIterator();
		Writer out = session.getWriter();
		out.write("<div " + id + " class='touch-screen-body' " + style + ">");
		out.write("<div class='touch-box-vertical' style='padding:6px'>");
		if (null != it) {
			while (it.hasNext()) {
				CompositeMap field = (CompositeMap) it.next();
				try {
					session.buildView(model, field);
				} catch (Exception e) {
					throw new ViewCreationException(e);
				}
			}
		}
		out.write("</div>");
		out.write("</div>");
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		// TODO Auto-generated method stub
		return null;
	}
}
