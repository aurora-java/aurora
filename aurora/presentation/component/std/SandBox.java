package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import uncertain.composite.CompositeMap;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class SandBox extends Component {

	private static final String DEFAULT_CLASS = "sandbox";
	private static final String PROPERTITY_CONTEXT = "context";
	private static final String PROPERTITY_CONTENT = "content";
	private static final String PROPERTITY_TAG = "tag";

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
		String content = uncertain.composite.TextParser.parse(
				view.getString(PROPERTITY_CONTENT), model);
		addConfig(PROPERTITY_CONTENT, content);
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
				.append("\"&gt;").append("\n&lt;").append(prefix)
				.append(":view").append("&gt;")
				.append(createTextArea(session, context, prefix))
				.append("&lt;/").append(prefix).append(":view&gt;\n")
				.append("&lt;/").append(prefix).append(":screen&gt;");
		return sb.toString();
	}

	// private String createTextArea(BuildSession session, ViewContext context)
	// throws IOException {
	// String height = context.getView().getString(
	// ComponentConfig.PROPERTITY_HEIGHT, "150");
	// CompositeMap model = context.getModel();
	// CompositeMap textArea = new CompositeMap("textArea");
	// textArea.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
	// textArea.put(ComponentConfig.PROPERTITY_ID, id + "_view");
	// textArea.put(ComponentConfig.PROPERTITY_STYLE, "width:99%;height:"
	// + height + "px;");
	// try {
	// return session.buildViewAsString(model, textArea);
	// } catch (Exception e) {
	// throw new IOException(e.getMessage());
	// }
	// }

	private String createTextArea(BuildSession session, ViewContext context,
			String prefix) {
		StringBuffer sb = new StringBuffer();
		sb.append("<div id='");
		sb.append(id);
		sb.append("_wrapcontent' class='wrapcontent' contentEditable='true'>");
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		String content = uncertain.composite.TextParser.parse(
				view.getString(PROPERTITY_CONTENT), model);
		if(null == content || content.isEmpty()){
			sb.append("</div>");
			return sb.toString();
		}
		String tag_name = uncertain.composite.TextParser.parse(
				view.getString(PROPERTITY_TAG), model);
		String tag = getTag(tag_name, prefix);
		String pattern = getPattern(tag_name, prefix);
		String defaultPattern = getDefaultPattern(tag_name, prefix);
		if (null != tag_name && !tag_name.isEmpty() && content.contains(tag)) {
			String left_wrap_content = getContent(
					getContent(content, defaultPattern, "$1"), pattern, "$1");
			String tag_content = getContent(
					getContent(content, defaultPattern, "$2"), pattern, "$2");
			String right_wrap_content = getContent(
					getContent(content, defaultPattern, "$3"), pattern, "$3");
			sb.append(parseContent(left_wrap_content.trim()));
			sb.append("<div id='");
			sb.append(id);
			sb.append("_tagcontent' class='tagcontent' contentEditable='true'>");
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			sb.append(parseContent(tag_content));
			sb.append("</div>");
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			sb.append(parseContent(right_wrap_content.trim()));
		} else
			sb.append(parseContent(content));
		sb.append("</div>");
		return sb.toString();
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

	private String getPattern(String tag, String prefix) {		
		StringBuffer sb = new StringBuffer("(.*)(<");
		sb.append(prefix);
		sb.append(":");
		sb.append(tag);
		sb.append("[^>]*/>)(.*)");		
		return sb.toString();
	}

	private String getDefaultPattern(String tag, String prefix) {
		StringBuffer sb = new StringBuffer("(.*)(<");
		sb.append(prefix);
		sb.append(":");
		sb.append(tag);
		sb.append("[^>]*>.*</");
		sb.append(prefix);
		sb.append(":");
		sb.append(tag);
		sb.append(">)(.*)");
		return sb.toString();
	}

	private String getTag(String tag, String prefix) {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append(":");
		sb.append(tag);
		return sb.toString();
	}

	private String parseContent(String content) {
		return content.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	private String getContent(String content, String pattern, String replacement) {
		if (null == content || "".equals(content))
			return "";
		return replaceAll(pattern, content, replacement);
	}

	private String replaceAll(String regex, CharSequence input,
			String replacement) {
		return Pattern.compile(regex, Pattern.DOTALL).matcher(input)
				.replaceAll(replacement);
	}
}
