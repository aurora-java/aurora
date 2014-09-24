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
public class ToolBar extends Component implements IViewBuilder {

	private static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_LEFT_SIDE = "leftSide";
	private static final String PROPERTITY_RIGHT_SIDE = "rightSide";

	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Map map = view_context.getMap();

		/** ID属性 **/
		String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if (!"".equals(id)) {
			id = "id=\"" + id + "\"";
		}
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		if (!"".equals(style)) {
			style = "style=\"" + style + "\"";
		}
		CompositeMap leftSide = view.getChild(PROPERTITY_LEFT_SIDE);
		CompositeMap rightSide = view.getChild(PROPERTITY_RIGHT_SIDE);
		String title = view.getString(PROPERTITY_TITLE);
		Writer out = session.getWriter();
		out.write("<div " + id + " class='touch-tool-bar' " + style + ">");
		if(null!=leftSide){
			Iterator lit = leftSide.getChildIterator();
			if (null != lit) {
				while (lit.hasNext()) {
					CompositeMap field = (CompositeMap) lit.next();
					try {
						session.buildView(model, field);
					} catch (Exception e) {
						throw new ViewCreationException(e);
					}
				}
			}
			
		}
		out.write("<div class='touch-tool-bar-title'>");
		if (null != title && !"".equals(title)) {
			out.write(title);
		}
		out.write("</div>");
		if(null!=rightSide){
			Iterator rit = rightSide.getChildIterator();
			if (null != rit) {
				while (rit.hasNext()) {
					CompositeMap field = (CompositeMap) rit.next();
					try {
						session.buildView(model, field);
					} catch (Exception e) {
						throw new ViewCreationException(e);
					}
				}
			}
			
		}
		out.write("</div>");
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		// TODO Auto-generated method stub
		return null;
	}
}
