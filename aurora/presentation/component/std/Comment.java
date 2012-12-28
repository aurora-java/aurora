package aurora.presentation.component.std;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import aurora.application.AuroraApplication;
import aurora.database.service.BusinessModelService;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.service.ServiceThreadLocal;

public class Comment extends Component {

	public static final String VERSION = "$Revision$";
	
	private static final String DEFAULT_CLASS = "comments";
	private IDatabaseServiceFactory factory;
	private String model = "doc.doc_comment";
	private static final String PROPERTITY_TABLE_NAME = "tablename";
	private static final String PROPERTITY_BIND_ID = "bindid";
	private static final String PROPERTITY_REGISTER_HANDLER = "registerhandler";
	private static final String PROPERTITY_LOGIN_HANDLER = "loginhandler";
	private SimpleDateFormat parseDate;
	private SimpleDateFormat formatDate;
	private Object userId;

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "comment/comment.css");
		addJavaScript(session, context, "comment/comment.js");
	}

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public Comment(IDatabaseServiceFactory factory) {
		this.factory = factory;
		parseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatDate = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		try {
			CompositeMap sessionmap = (CompositeMap) context.getModel().getObject("/session");
			if (null != sessionmap) {
				userId = sessionmap.get("user_id");
				addConfig("username", findUserName(userId));
			}
			CompositeMap comments = init(session, context);
			StringBuffer content;
			int length = 0;
			if (null != comments.getChilds()) {
				content = new StringBuffer("<ol class='comment-list'>");
				length = comments.getChilds().size();
				Iterator it = comments.getChildIterator();
				while (it.hasNext()) {
					content.append(createComment((CompositeMap) it.next()));
				}
				content.append("</ol>");
			} else
				content = new StringBuffer("<p class='comment-li'>暂时没有评论。</p>");
			map.put("length", new Integer(length));
			map.put("list", content.toString());

			map.put("txt", createTextArea(session, context));
			map.put("btn", createButton(session, context));
			
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		addConfig("submiturl", session.getContextPath() + "/autocrud/" + model);
		map.put("config", getConfigString());
	}

	private String createComment(CompositeMap comment) throws ParseException {
		String content = comment.getString("content");
		if (null == content)
			content = "";
		content = replaceAll(">", replaceAll("<", replaceAll("&", content,
				"&amp;"), "&lt;"), "&gt;");
		// content =
		// replaceAll("\\[/code\\]",replaceAll("\\[code\\]",replaceAll(">",
		// replaceAll("<", replaceAll("&", content,
		// "&amp;"), "&lt;"), "&gt;"),"<pre>"),"</pre>");
		String author = comment.getString("user_name");
		Long author_id = comment.getLong("created_by");
		String date = comment.getString("creation_date");
		String commentId = comment.getString("comment_id");
		date = formatDate.format(parseDate.parse(date));
		StringBuffer buffer = new StringBuffer("<li id='" + id + "_"
				+ commentId + "' class='comment-li'>");
		buffer.append("<div>");
		buffer.append("<div class='comment-nick'><cite>" + author
				+ "</cite> <span>留言于：" + date + "</span></div>");
		buffer.append("<div class='comment-txt'>" + content + "</div>");
		buffer.append("</div>");
		if (null != author_id && author_id.toString().equals(userId)) {
			buffer.append("<div class='comment-bar'><a href='javascript:$(\""
					+ id + "\").remove(\"" + commentId + "\")'>删除</a></div>");
		}
		buffer.append("</li>");
		return buffer.toString();
	}

	private String createTextArea(BuildSession session, ViewContext context)
			throws Exception {
		CompositeMap model = context.getModel();
		CompositeMap textArea = new CompositeMap("textArea");
		textArea.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		textArea.put(ComponentConfig.PROPERTITY_ID, id + "_txt");
		textArea
				.put(ComponentConfig.PROPERTITY_STYLE, "width:99%;height:150px");
		if(null == userId){
			textArea.put("readonly", new Boolean(true));
		}
		return session.buildViewAsString(model, textArea);
	}

	private String createButton(BuildSession session, ViewContext context)
			throws Exception {
		CompositeMap model = context.getModel();
		CompositeMap button = new CompositeMap("button");
		button.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		button.put(ComponentConfig.PROPERTITY_ID, id + "_btn");
		button.put(Button.PROPERTITY_ICON, null);
		button.put(Button.PROPERTITY_TEXT, "发 表 评 论");
		button.put(ComponentConfig.PROPERTITY_WIDTH, 100);
		button.put(Button.PROPERTITY_CLICK, "function(){$('" + id+ "').post()}");
		if(null == userId){
			button.put("disabled", new Boolean(true));
		}
		return session.buildViewAsString(model, button);
	}
	private CompositeMap init(BuildSession session, ViewContext view_context)
			throws Exception {
		CompositeMap view = view_context.getView();
		String tableName = view.getString(PROPERTITY_TABLE_NAME);
		String bindId = view.getString(PROPERTITY_BIND_ID);
		if (null == bindId) {
			throw new IllegalStateException(
					"The property 'bindId' of The comment component is required.");
		}
		String registerHandler = view.getString(PROPERTITY_REGISTER_HANDLER);
		if(null != registerHandler){
			addConfig(PROPERTITY_REGISTER_HANDLER, new JSONFunction(registerHandler));
		}
		String loginHandler = view.getString(PROPERTITY_LOGIN_HANDLER);
		if(null != loginHandler){
			addConfig(PROPERTITY_LOGIN_HANDLER, new JSONFunction(loginHandler));
		}
		bindId = TextParser.parse(bindId, view_context
				.getModel());
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		if (context == null)
			throw new IllegalStateException(
					"No service context set in ThreadLocal yet");

		BusinessModelService service = factory.getModelService(model, context);
		Map map = new HashMap();
		map.put("table_id", bindId);
		map.put("table_name", tableName);
		addConfig("tableid", bindId);
		addConfig("tablename", tableName);
		return service.queryAsMap(map);
	}

	private String findUserName(Object userId) throws Exception {
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		if (context == null)
			throw new IllegalStateException(
					"No service context set in ThreadLocal yet");

		BusinessModelService service = factory.getModelService("sys.sys_user",context);
		Map map = new HashMap();
		map.put("user_id", userId);
		CompositeMap cm = service.queryAsMap(map);
		if (null == cm)
			return null;
		Iterator childs = cm.getChildIterator();
		while (childs.hasNext()) {
			return (String) ((CompositeMap) childs.next()).get("user_name");
		}
		return null;
	}

	private String replaceAll(String regex, CharSequence input,
			String replacement) {
		return Pattern.compile(regex, Pattern.DOTALL).matcher(input)
				.replaceAll(replacement);
	}
}
