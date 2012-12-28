package aurora.presentation.component.std;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.util.XMLWritter;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class SandBox extends Component {
	public static final String VERSION = "$Revision$";
	
	private static final String DEFAULT_CLASS = "sandbox";
	private static final String PROPERTITY_CONTEXT = "context";
	private static final String PROPERTITY_FILE_NAME = "filename";
	private static final String PROPERTITY_TAG = "tag";

	private static final String CDATA_END = "]]&gt;";
	private static final String CDATA_BEGIN = "&lt;![CDATA[";
	private static final String DEFAULT_INDENT = "    ";
	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	boolean inited = false;

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
		addConfig(PROPERTITY_CONTEXT, session.getContextPath());
		map.put("view", createView(session, context));
		map.put("btn", createButton(session, context));
		map.put("config", getConfigString());
	}

	private String createView(BuildSession session, ViewContext context)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		CompositeMap view = context.getView();
		String prefix = view.getPrefix();
		sb.append("&lt;").append(prefix).append(":screen ").append("xmlns:")
				.append(prefix).append("=\"").append(view.getNamespaceURI())
				.append("\"&gt;").append("\n&lt;").append(prefix)
				.append(":view").append("&gt;")
				.append(createTextArea(session, context)).append("&lt;/")
				.append(prefix).append(":view&gt;\n").append("&lt;/")
				.append(prefix).append(":screen&gt;");
		return sb.toString();

	}

	private String createTextArea(BuildSession session, ViewContext context)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<div id='");
		sb.append(id);
		sb.append("_wrapcontent' class='wrapcontent' contentEditable='true'>");
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		String prefix = view.getPrefix();
		String file_name = uncertain.composite.TextParser.parse(
				view.getString(PROPERTITY_FILE_NAME), model);
		File file = new File(file_name);
		if (!file.exists()) {
			sb.append("</div>");
			return sb.toString();
		}
		String tag_name = uncertain.composite.TextParser.parse(
				view.getString(PROPERTITY_TAG), model);
		CompositeLoader loader = new CompositeLoader();
		CompositeMap screenMap = null;
		try {
			screenMap = loader.loadByFullFilePath(file_name);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CompositeMap view_map = screenMap.getChild("view");
		if (view_map != null) {
			String content = getContent(0, view_map, prefix, tag_name);
			sb.append(content);
		}
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

	private static void getAttributeXML(Map map, StringBuffer attribs) {
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (value != null)
				attribs.append(" ").append(
						XMLWritter.getAttrib(key.toString(), value.toString()));
		}
	}

	private void getChildXML(int level, List childs, StringBuffer buf,
			String prefix, String tag_name) {
		if (childs == null)
			return;
		Iterator it = childs.iterator();
		while (it.hasNext()) {
			CompositeMap map = (CompositeMap) it.next();
			buf.append(getContent(level, map, prefix, tag_name));
		}
	}

	private String getIndentString(int level) {
		StringBuffer pre_indent = new StringBuffer();
		for (int i = 0; i < level; i++)
			pre_indent.append(DEFAULT_INDENT);
		return pre_indent.toString();
	}

	private String getContent(int level, CompositeMap map, String prefix,
			String tag_name) {
		StringBuffer content = new StringBuffer();
		StringBuffer attribs = new StringBuffer();
		StringBuffer childs = new StringBuffer();
		String indent_str = getIndentString(level - 1);
		String namespace_uri = map.getNamespaceURI();
		boolean mUseNewLine = true;
		boolean need_new_line_local = true;
		boolean need_div_close = false;

		getAttributeXML(map, attribs);
		if (map.getChilds() == null) {
			if (map.getText() != null) {
				need_new_line_local = false;
				childs.append(CDATA_BEGIN).append(map.getText())
						.append(CDATA_END);
			}
		} else
			getChildXML(level + 1, map.getChilds(), childs, prefix, tag_name);
		if (level == 0) {
			content.append(childs);
			return content.toString();
		}
		String elm = map.getName();
		if (namespace_uri != null)
			elm = prefix + ":" + elm;
		if (map.getName().equals(tag_name)) {
			content.append("<div");
			if (!inited) {
				content.append(" id='");
				content.append(id);
				content.append("_tagcontent'");
			}
			content.append(" class='tagcontent'>");
			need_div_close = true;
			mUseNewLine = false;
			inited = true;
		}
		content.append(indent_str).append("&lt;").append(elm);
		content.append(attribs);
		if (childs.length() > 0) {
			content.append("&gt;");
			if (need_new_line_local)
				content.append(LINE_SEPARATOR);
				content.append(childs);
			if (need_new_line_local)
				content.append(indent_str);
			content.append("&lt;/");
			content.append(elm);
			content.append("&gt;");
		} else
			content.append("/&gt;");
		if (need_div_close)
			content.append("</div>");
		if (mUseNewLine)
			content.append(LINE_SEPARATOR);
		return content.toString();
	}

}
