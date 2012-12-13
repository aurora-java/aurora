package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import aurora.application.AuroraApplication;
import aurora.application.features.cstm.CustomSourceCode;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.BoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;
import aurora.presentation.component.std.config.TextFieldConfig;

public class QueryForm extends Component implements IViewBuilder, ISingleton {
	private static final String DEFAULT_TABLE_CLASS = "layout-table";
	private static final String DEFAULT_WRAP_CLASS = "form_body_wrap";
	private static final String DEFAULT_HEAD_CLASS = "form_head";
	private static final String FORM_TOOL_BAR = "formToolBar";
	private static final String FORM_BODY = "formBody";

	private static final String PROPERTITY_EXPAND = "expand";
	private static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_RESULT_TARGET = "resulttarget";
	private static final String PROPERTITY_DEFAULT_QUERY_FIELD = "defaultqueryfield";
	private static final String PROPERTITY_DEFAULT_QUERY_HINT = "defaultqueryhint";
	private static final String PROPERTITY_DEFAULT_QUERY_PROMPT = "defaultqueryprompt";
	private static final String PROPERTITY_QUERY_HOOK = "queryhook";
	private static final String PROPERTITY_CREATE_SEARCH_BOX = "createsearchbox";
	IObjectRegistry	mObjectRegistry;
	private ISchemaManager schemaManager ;
	private String ds;
	private CompositeMap formToolBar;
	private CompositeMap formBody;
	protected int getDefaultWidth() {
		return 0;
	}

	protected int getDefaultHeight() {
		return 0;
	}
	public QueryForm( IObjectRegistry reg ) {
		mObjectRegistry = reg;
	}
	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		schemaManager = (ISchemaManager) mObjectRegistry.getInstanceOfType(ISchemaManager.class);
		if (schemaManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(),
					ISchemaManager.class, CustomSourceCode.class.getCanonicalName());
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Map map = view_context.getMap();

		/** ID属性 **/
		id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if ("".equals(id)) {
			id = IDGenerator.getInstance().generate();
		}
		ds = view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		String result_ds = view.getString(PROPERTITY_RESULT_TARGET);
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		boolean open = view.getBoolean(PROPERTITY_EXPAND, false);
		int width = getComponentWidth(model, view, map).intValue();
		int height = getComponentHeight(model, view, map).intValue();

		String className = DEFAULT_TABLE_CLASS + " query-form layout-title"
				+ view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String title = session.getLocalizedPrompt(view.getString(
				PROPERTITY_TITLE, ""));
		String queryhook = view.getString(PROPERTITY_QUERY_HOOK);
		String queryfield = view.getString(PROPERTITY_DEFAULT_QUERY_FIELD);
		Writer out = session.getWriter();
		try {
			processFormToolBar(view, session, model);
			boolean hasBody = processFormBody(view, session, model, height);
			out.write("<table cellspacing='0' cellpadding='0' class='"
					+ className + "' id='" + id + "'");
			if (width != 0)
				style = "width:" + width + "px;" + style;
			if (!"".equals(style)) {
				out.write(" style='" + style + "'");
			}
			out.write("><thead>");
			if (!"".equals(title)) {
				out.write("<tr><th class='" + DEFAULT_HEAD_CLASS + "'>" + title
						+ "</th></tr>");
			}
			out.write("<tr><th class='query_form_head'>");
			session.buildView(model, formToolBar);
			out.write("</th></tr></thead>");
			if(hasBody){
				out.write("<tbody ");
				if (!open) {
					out.write(" style='display:none'");
				}
				out.write("><tr><td><div class='"
						+ DEFAULT_WRAP_CLASS + "'>");
				session.buildView(model, formBody);
				out.write("</div></td></tr></tbody>");
			}else open = false;
			out.write("</table>");
			out.write("<script>");
			out.write("new $A.QueryForm({id:'" + id + "',isopen:" + open +",resulttarget:'"+result_ds+ "',");
			out.write(null == queryhook ? "queryfield:'"+queryfield+"'": "queryhook:"+queryhook);
			out.write("});");
			if (null != ds)
				out.write("$('" + id + "').bind('" + ds + "');");
			out.write("</script>");
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

	private void processFormToolBar(CompositeMap view, BuildSession session,
			CompositeMap model) throws Exception {
		formToolBar = view.getChild(FORM_TOOL_BAR);
		CompositeMap searchField = null;
		String hint = view.getString(PROPERTITY_DEFAULT_QUERY_HINT);
		String queryPrompt = view.getString(PROPERTITY_DEFAULT_QUERY_PROMPT);
		String queryId = id + "_query";
		String style = "";
		String searchFunction = "function(){$('" + id + "').doSearch()}";
		boolean createSearchBox = view.getBoolean(PROPERTITY_CREATE_SEARCH_BOX, true);
		if (null == formToolBar || null == formToolBar.getChildIterator()) {
			if(null == formToolBar){
				formToolBar = new CompositeMap("hBox");
				formToolBar.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			}
			if(createSearchBox){
				searchField = new CompositeMap("textField");
				searchField
						.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				searchField.putString(ComponentConfig.PROPERTITY_STYLE,
						"width:100%");
				CompositeMap btn = new CompositeMap(ToolBarButton.TAG_NAME);
				btn.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				btn.putString(Button.PROPERTITY_TEXT, session.getLocalizedPrompt("HAP_QUERY"));
				btn.putInt(ComponentConfig.PROPERTITY_WIDTH, 80);
				btn.putString(Button.PROPERTITY_CLICK, searchFunction);
				formToolBar.addChild(searchField);
				formToolBar.addChild(btn);
			}
		} else {
			bindDataset(formToolBar);				
			if(createSearchBox)
				searchField = findTextField(formToolBar);
		}
		formToolBar.setName("hBox");
		style = formToolBar.getString(ComponentConfig.PROPERTITY_STYLE,"");
		if (null != searchField) {
			searchField.putString(ComponentConfig.PROPERTITY_ID, queryId);
			CompositeMap events = searchField.getChild(EventConfig.PROPERTITY_EVENTS);
			if(null == events){
				events = new CompositeMap(EventConfig.PROPERTITY_EVENTS);
				events.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				searchField.addChild(events);
			}
			CompositeMap event = new CompositeMap("event");
			event.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			event.putString(EventConfig.PROPERTITY_EVENT_NAME, "enterdown");
			event.putString(EventConfig.PROPERTITY_EVENT_HANDLER, searchFunction);
			events.addChild(event);
			if (null != hint) {
				searchField.putString(TextFieldConfig.PROPERTITY_EMPTYTEXT,session.getLocalizedPrompt(hint));
			}
			if (null != queryPrompt) {
				searchField.putString(ComponentConfig.PROPERTITY_PROMPT,
						session.getLocalizedPrompt(queryPrompt));
			}
		}
		formToolBar.putBoolean(GridLayout.PROPERTITY_WRAPPER_ADJUST, true);
		formToolBar.putString("style", "width:100%;"+style);
	}

	private boolean processFormBody(CompositeMap view, BuildSession session,
			CompositeMap model, int height) throws Exception {
		formBody = view.getChild(FORM_BODY);
		if (null != formBody && null != formBody.getChildIterator()) {
			bindDataset(formBody);
			formBody.setName("box");
			if (height != 0)
				formBody.put(ComponentConfig.PROPERTITY_HEIGHT, height - 26);
			formBody.put(BoxConfig.PROPERTITY_PADDING, 0);
			CompositeMap btn = new CompositeMap(ToolBarButton.TAG_NAME);
			btn.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			btn.putString(ComponentConfig.PROPERTITY_STYLE, "float:right");
			btn.putString(Button.PROPERTITY_TEXT, session.getLocalizedPrompt("HAP_MORE"));
			btn.putInt(ComponentConfig.PROPERTITY_WIDTH, 80);
			btn.putString(Button.PROPERTITY_CLICK, "function(){$('" + id
					+ "').trigger()}");
			formToolBar.addChild(btn);
			return true;
		}
		return false;
	}
	private void bindDataset(CompositeMap parent){
		if(null!= ds && null != parent){
			Iterator it = parent.getChildIterator();
			if (null != it) {
				QualifiedName bindTarget = new QualifiedName("bindTarget");
				while (it.hasNext()) {
					CompositeMap child = (CompositeMap) it.next();
					Element ele = schemaManager.getElement(child);
					Iterator attrs= ele.getAllAttributes().iterator();
					while(attrs.hasNext()){
						Attribute attr = (Attribute) attrs.next();
						if(bindTarget.equals(attr.getQName())){
							child.putString(ComponentConfig.PROPERTITY_BINDTARGET, ds);
							break;
						}
					}
					bindDataset(child);
				}
			}
		}
	}
	private CompositeMap findTextField(CompositeMap parent) {
		boolean findTextField = false;
		QualifiedName qName = new QualifiedName(
				AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "textField");
		if (null != parent) {
			Iterator it = parent.getChildIterator();
			if (null != it) {
				while (it.hasNext()) {
					CompositeMap child = (CompositeMap) it.next();
					if (qName.equals(child.getQName())) {
						return child;
					}else {
						return findTextField(child);
					}
				}
			}
		}
		return null;
	}
}
