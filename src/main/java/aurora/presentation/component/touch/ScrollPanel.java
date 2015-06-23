package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.touch.Component;

@SuppressWarnings("unchecked")
public class ScrollPanel extends Component {

	protected static final String DEFAULT_CLASS = "touch-scroll-panel";

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "base/iscroll-min.js");
	}

	@Override
	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		Iterator it = view.getChildIterator();
		StringBuffer out = new StringBuffer();
		if (null != it) {
			try {
				while (it.hasNext()) {
					CompositeMap child = (CompositeMap) it.next();
					out.append(session.buildViewAsString(model, child));
				}
			} catch (Exception e) {
				throw new IOException(e); 
			}
		}
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		if (!"".equals(style)) {
			style="style=\"" + style+"\"";
		}
		map.put(ComponentConfig.PROPERTITY_STYLE, style);
		map.put("content", out.toString());
		map.put(CONFIG, getConfigString());
	}
}
