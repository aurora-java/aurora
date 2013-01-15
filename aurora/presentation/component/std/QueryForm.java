package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import aurora.application.AuroraApplication;
import aurora.application.features.cstm.CustomSourceCode;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ButtonConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;
import aurora.presentation.component.std.config.TextFieldConfig;

/**
 * QueryForm 只有2种方式
 * (1)没有formToolBar 则默认创建一个通用的searchField和searchButton
 * (2)有formToolBar和formBody 则默认创建searchButton和moreButton
 * @version $Id$
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class QueryForm extends Component implements IViewBuilder, ISingleton {
	
	public static final String VERSION = "$Revision$";
	
	
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
	private static final String PROPERTITY_CREATE_SEARCH_BUTTON = "createsearchbutton";
	
	
	private static final String DEFAULT_QUERY_PROMPT = "HAP_QUERY";
	private static final String DEFAULT_MORE_PROMPT = "HAP_MORE";
	private static final String DEFAUTL_BUTTON_THEME = "item-rbtn-gray";
	
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
	
	
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		schemaManager = (ISchemaManager) mObjectRegistry.getInstanceOfType(ISchemaManager.class);
		if (schemaManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(),ISchemaManager.class, CustomSourceCode.class.getCanonicalName());
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

		String className = DEFAULT_TABLE_CLASS + " query-form layout-title" + view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String title = session.getLocalizedPrompt(view.getString(
				PROPERTITY_TITLE, ""));
		String queryhook = view.getString(PROPERTITY_QUERY_HOOK);
		String queryfield = view.getString(PROPERTITY_DEFAULT_QUERY_FIELD);
		Writer out = session.getWriter();
		try {
			processFormToolBar(view, session, model);
			boolean hasBody = processFormBody(id,view, session, model, height);
			out.write("<table cellspacing='0' cellpadding='0' class='" + className + "' id='" + id + "'");
			if (width != 0) style = "width:" + width + "px;" + style;
			if (!"".equals(style)) {
				out.write(" style='" + style + "'");
			}
			out.write("><thead>");
			if (!"".equals(title)) {
				out.write("<tr><th class='" + DEFAULT_HEAD_CLASS + "'>" + title + "</th></tr>");
			}
			out.write("<tr><th class='query_form_head'>");
			session.buildView(model, formToolBar);
			out.write("</th></tr></thead>");
			if(hasBody){
				out.write("<tbody ");
				if (!open) {
					out.write(" style='display:none'");
				}
				out.write("><tr><td><div class='" + DEFAULT_WRAP_CLASS + "'>");
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

	private void processFormToolBar(CompositeMap view, BuildSession session, CompositeMap model) throws Exception {
		formToolBar = view.getChild(FORM_TOOL_BAR);
		CompositeMap searchField = null;
		String hint = view.getString(PROPERTITY_DEFAULT_QUERY_HINT);
		String queryPrompt = view.getString(PROPERTITY_DEFAULT_QUERY_PROMPT);
		String queryId = id + "_query";
		String style = "";
		String searchFunction = "function(){$('" + id + "').doSearch()}";
		boolean createSearchButton = view.getBoolean(PROPERTITY_CREATE_SEARCH_BUTTON, true);
		if (null == formToolBar || null == formToolBar.getChildIterator()) {
			if(null == formToolBar){
				formToolBar = new CompositeMap("hBox");
				formToolBar.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			} else {
				formToolBar.setName("hBox");
				style = formToolBar.getString(ComponentConfig.PROPERTITY_STYLE,"");
			}
			searchField = new CompositeMap(TextFieldConfig.TAG_NAME);
			searchField.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			searchField.putString(ComponentConfig.PROPERTITY_STYLE,"width:100%");
			searchField.putString(ComponentConfig.PROPERTITY_ID, queryId);
			CompositeMap events = searchField.getChild(EventConfig.PROPERTITY_EVENTS);
			if(null == events){
				events = new CompositeMap(EventConfig.PROPERTITY_EVENTS);
				events.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				searchField.addChild(events);
			}
			CompositeMap event = new CompositeMap(EventConfig.TAG_NAME);
			event.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			event.putString(EventConfig.PROPERTITY_EVENT_NAME, "enterdown");
			event.putString(EventConfig.PROPERTITY_EVENT_HANDLER, searchFunction);
			events.addChild(event);
			if (null != hint) {
				searchField.putString(TextFieldConfig.PROPERTITY_EMPTYTEXT,session.getLocalizedPrompt(hint));
			}
			if (null != queryPrompt) {
				searchField.putString(ComponentConfig.PROPERTITY_PROMPT,session.getLocalizedPrompt(queryPrompt));
			}
			
			
			CompositeMap btn = new CompositeMap(ToolBarButton.TAG_NAME);
			btn.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			btn.putString(ComponentConfig.PROPERTITY_CLASSNAME, DEFAUTL_BUTTON_THEME);
			btn.putString(ButtonConfig.PROPERTITY_TEXT, session.getLocalizedPrompt(DEFAULT_QUERY_PROMPT));
			btn.putInt(ComponentConfig.PROPERTITY_WIDTH, 80);
			btn.putString(ButtonConfig.PROPERTITY_CLICK, searchFunction);
			formToolBar.putString("style", "width:100%;"+style);
			formToolBar.addChild(searchField);
			formToolBar.addChild(btn);
		} else {
			formToolBar.setName("hBox");
			bindDataset(id,formToolBar);				
//			if(createSearchBox) searchField = findTextField(formToolBar);
			style = formToolBar.getString(ComponentConfig.PROPERTITY_STYLE,"");
			if(createSearchButton){
				CompositeMap btn = new CompositeMap(ToolBarButton.TAG_NAME);
				btn.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				btn.putString(ComponentConfig.PROPERTITY_CLASSNAME, DEFAUTL_BUTTON_THEME);
				btn.putString(ButtonConfig.PROPERTITY_TEXT, session.getLocalizedPrompt(DEFAULT_QUERY_PROMPT));
				btn.putInt(ComponentConfig.PROPERTITY_WIDTH, 80);
				btn.putString(ButtonConfig.PROPERTITY_CLICK, searchFunction);
				formToolBar.putString("style", "width:100%;"+style);
				formToolBar.addChild(btn);
			}
		}
		formToolBar.putBoolean(GridLayout.PROPERTITY_WRAPPER_ADJUST, true);
		formToolBar.putString("style", "width:100%;"+style);
	}

	private boolean processFormBody(String id, CompositeMap view, BuildSession session, CompositeMap model, int height) throws Exception {
		formBody = view.getChild(FORM_BODY);
		if (null != formBody && null != formBody.getChildIterator()) {
			bindDataset(id,formBody);
			formBody.setName("box");
			if (height != 0) formBody.put(ComponentConfig.PROPERTITY_HEIGHT, height - 26);
//			formBody.put(BoxConfig.PROPERTITY_PADDING, 0);
			CompositeMap btn = new CompositeMap(ToolBarButton.TAG_NAME);
			btn.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			btn.putString(ComponentConfig.PROPERTITY_CLASSNAME, DEFAUTL_BUTTON_THEME);
			btn.putString(ComponentConfig.PROPERTITY_STYLE, "float:right");
			btn.putString(ButtonConfig.PROPERTITY_TEXT, session.getLocalizedPrompt(DEFAULT_MORE_PROMPT));
			btn.putInt(ComponentConfig.PROPERTITY_WIDTH, 80);
			btn.putString(ButtonConfig.PROPERTITY_CLICK, "function(){$('" + id + "').trigger()}");
			formToolBar.addChild(btn);
			return true;
		}
		return false;
	}
	private void bindDataset(String id, CompositeMap parent){
		if(null!= ds && null != parent){
			Iterator it = parent.getChildIterator();
			if (null != it) {
				QualifiedName bindTarget = new QualifiedName("bindTarget");
				while (it.hasNext()) {
					CompositeMap child = (CompositeMap) it.next();
					Element ele = schemaManager.getElement(child);
					if(ele!=null) {
						Iterator attrs= ele.getAllAttributes().iterator();
						while(attrs.hasNext()){
							Attribute attr = (Attribute) attrs.next();
							if(bindTarget.equals(attr.getQName())){
								child.putString(ComponentConfig.PROPERTITY_BINDTARGET, ds);
								break;
							}
						}
					}
					bindDataset(id,child);
					addEnterDownHanlder(id,child);
				}
			}
		}
	}
	
	//判断是否有events
	private void addEnterDownHanlder(String id, CompositeMap item){
		Element ele = schemaManager.getElement(item);
		Iterator arrays = ele.getAllArrays().iterator();
		boolean hasEvents = false;
		while(arrays.hasNext()){
			Array arr =  (Array)arrays.next();
			if("a:events".equals(arr.getName())){
				hasEvents = true;
				break;
			}
		}
		if(hasEvents) {
			CompositeMap events = item.getChild(ComponentConfig.PROPERTITY_EVENTS);
			if(events == null){
				events = new CompositeMap(ComponentConfig.PROPERTITY_EVENTS);
				events.setNameSpace(null, AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				item.addChild(events);			
			}
			List list = events.getChilds();
			boolean hasEnterDown = false;
			if (list != null) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					CompositeMap event = (CompositeMap) it.next();
					EventConfig eventConfig = EventConfig.getInstance(event);
					String eventName = eventConfig.getEventName();
					if(EventConfig.EVENT_ENTERDOWN.equals(eventName)) {
						hasEnterDown = true;
						break;
					}	
				}
			}
			if(!hasEnterDown){
				EventConfig evt = EventConfig.getInstance();
				evt.setEventName(EventConfig.EVENT_ENTERDOWN);
				evt.setHandler("function(){$('" + id + "').doSearch()}");
				events.addChild(evt.getObjectContext());			
			}
		}
	}
	
	
//	private CompositeMap findTextField(CompositeMap parent) {
//		QualifiedName qName = new QualifiedName(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, TextFieldConfig.TAG_NAME);
//		if (null != parent) {
//			Iterator it = parent.getChildIterator();
//			if (null != it) {
//				while (it.hasNext()) {
//					CompositeMap child = (CompositeMap) it.next();
//					if (qName.equals(child.getQName())) {
//						return child;
//					}else {
//						return findTextField(child);
//					}
//				}
//			}
//		}
//		return null;
//	}
}
