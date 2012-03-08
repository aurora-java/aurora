package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class SandBox extends Component {

	private static final String DEFAULT_CLASS = "sandbox";
	private static final String PROPERTITY_CONTEXT = "context";
	private static final String PROPERTITY_CONTENT = "content";

	protected int getDefaultWidth() {
		return 600;
	}

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "sandbox/SandBox.css");
		addJavaScript(session, context, "sandbox/SandBox.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		String content = uncertain.composite.TextParser.parse(view.getString(PROPERTITY_CONTENT),model);
		addConfig(PROPERTITY_CONTENT,content);
		addConfig(PROPERTITY_CONTEXT, session.getContextPath());
		map.put("view", buildScreenTemplate(session, context));
		map.put("btn", createButton(session, context));
		map.put("config", getConfigString());
	}

	private String buildScreenTemplate(BuildSession session, ViewContext context)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		CompositeMap view = context.getView();
		String prefix = view.getPrefix();
		sb.append("&lt;").append(prefix).append(":screen ").append("xmlns:")
				.append(prefix).append("=\"").append(view.getNamespaceURI())
				.append("\"&gt;").append("\n&lt;").append(prefix).append(
						":view").append("&gt;").append(
						createTextArea(session, context)).append("&lt;/")
				.append(prefix).append(":view&gt;\n").append("&lt;/").append(
						prefix).append(":screen&gt;");
		return sb.toString();
	}

	private String createTextArea(BuildSession session, ViewContext context)
			throws IOException {
		String height = context.getView().getString(
				ComponentConfig.PROPERTITY_HEIGHT, "150");
		CompositeMap model = context.getModel();
		CompositeMap textArea = new CompositeMap("textArea");
		textArea.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		textArea.put(ComponentConfig.PROPERTITY_ID, id + "_view");
		textArea.put(ComponentConfig.PROPERTITY_STYLE, "width:99%;height:"
				+ height + "px;");
		try {
			return session.buildViewAsString(model, textArea);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private String createButton(BuildSession session, ViewContext context)
			throws IOException {
		CompositeMap model = context.getModel();
		CompositeMap button = new CompositeMap("button");
		button.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		button.put(ComponentConfig.PROPERTITY_ID, id + "_btn");
		button.put(Button.PROPERTITY_ICON, null);
		button.put(Button.PROPERTITY_TEXT, "Try");
		button.put(ComponentConfig.PROPERTITY_WIDTH, 100);
		button.put(Button.PROPERTITY_CLICK, "function(){$('" + id
				+ "').send()}");
		try {
			return session.buildViewAsString(model, button);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
}
