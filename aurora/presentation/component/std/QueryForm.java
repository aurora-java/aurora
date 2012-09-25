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
import uncertain.schema.Extension;
import uncertain.schema.ISchemaManager;
import aurora.application.AuroraApplication;
import aurora.application.features.cstm.CustomSourceCode;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.TextFieldConfig;

public class QueryForm extends Component implements IViewBuilder, ISingleton {
	private static final String DEFAULT_TABLE_CLASS = "layout-table";
	private static final String DEFAULT_WRAP_CLASS = "form_body_wrap";
	private static final String DEFAULT_HEAD_CLASS = "form_head";
	private static final String FORM_TOOL_BAR = "formToolBar";
	private static final String FORM_BODY = "formBody";

	private static final String PROPERTITY_EXPAND = "expand";
	private static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_DEFAULT_QUERY_FIELD = "defaultqueryfield";
	private static final String PROPERTITY_DEFAULT_QUERY_HINT = "defaultqueryhint";
	private static final String PROPERTITY_DEFAULT_QUERY_PROMPT = "defaultqueryprompt";
	private static final String PROPERTITY_QUERY_HOOK = "queryhook";
	IObjectRegistry	mObjectRegistry;
	private ISchemaManager schemaManager ;
	private String ds;
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
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		boolean open = view.getBoolean(PROPERTITY_EXPAND, false);
		int width = getComponentWidth(model, view, map).intValue();
		int height = getComponentHeight(model, view, map).intValue();

		String className = DEFAULT_TABLE_CLASS + " layout-form layout-title "
				+ view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String title = session.getLocalizedPrompt(view.getString(
				PROPERTITY_TITLE, ""));
		String queryhook = view.getString(PROPERTITY_QUERY_HOOK);
		String queryfield = view.getString(PROPERTITY_DEFAULT_QUERY_FIELD);
		Writer out = session.getWriter();
		try {
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
			out.write("<tr><th>");
			buildToolBar(view, session, model);
			out.write("</th></tr></thead><tbody><tr><td><div class='"
					+ DEFAULT_WRAP_CLASS + "'");
			if (!open) {
				out.write(" style='height:0'");
			}
			out.write(">");
			buildBody(view, session, model, height);
			out.write("</div></td></tr></tbody></table>");
			out.write("<script>");
			out.write("new $A.QueryForm({id:'" + id + "',isopen:" + open + ",");
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

	private void buildToolBar(CompositeMap view, BuildSession session,
			CompositeMap model) throws Exception {
		CompositeMap formHead = view.getChild(FORM_TOOL_BAR);
		CompositeMap searchField = null;
		String hint = view.getString(PROPERTITY_DEFAULT_QUERY_HINT);
		String queryPrompt = view.getString(PROPERTITY_DEFAULT_QUERY_PROMPT);
		String queryId = id + "_query";
		String style = "";
		if (null == formHead || null == formHead.getChildIterator()) {
			formHead = new CompositeMap("hBox");
			formHead.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			searchField = new CompositeMap("textField");
			searchField
					.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			searchField.putString(ComponentConfig.PROPERTITY_STYLE,
					"width:100%");
			CompositeMap btn = new CompositeMap("button");
			btn.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			btn.putString(Button.PROPERTITY_TEXT, session.getLocalizedPrompt("HAP_QUERY"));
			btn.putInt(ComponentConfig.PROPERTITY_WIDTH, 80);
			btn.putString(Button.PROPERTITY_CLICK, "function(){$('" + id
					+ "').doSearch()}");
			formHead.addChild(searchField);
			formHead.addChild(btn);
		} else {
			searchField = findTextFieldAndCreateExpandButton(formHead);
			formHead.setName("hBox");
			style = formHead.getString(ComponentConfig.PROPERTITY_STYLE);
		}
		if (null != searchField) {
			searchField.putString(ComponentConfig.PROPERTITY_ID, queryId);
			if (null != hint) {
				searchField.putString(TextFieldConfig.PROPERTITY_EMPTYTEXT,
						hint);
			}
			if (null != queryPrompt) {
				searchField.putString(ComponentConfig.PROPERTITY_PROMPT,
						session.getLocalizedPrompt(queryPrompt));
			}
		}
		formHead.putBoolean(GridLayout.PROPERTITY_WRAPPER_ADJUST, true);
		formHead.putString("style", "width:100%;"+style);
		session.buildView(model, formHead);
	}

	private void buildBody(CompositeMap view, BuildSession session,
			CompositeMap model, int height) throws Exception {
		CompositeMap formBody = view.getChild(FORM_BODY);
		if (null != formBody && null != formBody.getChildIterator()) {
				bindDataset(formBody);
				formBody.setName("box");
				if (height != 0)
					formBody.put(ComponentConfig.PROPERTITY_HEIGHT, height - 26);
				
				session.buildView(model, formBody);
		}
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
	private CompositeMap findTextFieldAndCreateExpandButton(CompositeMap parent) {
		CompositeMap textField = null;
		boolean findTextField = false;
		QualifiedName qName = new QualifiedName(
				AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "textField");
		QualifiedName qName2 = new QualifiedName(
				AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "expandButton");
		if (null != parent) {
			Iterator it = parent.getChildIterator();
			if (null != it) {
				while (it.hasNext()) {
					CompositeMap child = (CompositeMap) it.next();
					if (qName.equals(child.getQName())) {
						if (!findTextField) {
							textField = child;
							findTextField = true;
						}
					} else if (qName2.equals(child.getQName())) {
						child.setName("button");
						child.putString("click", "function(){$('" + id
								+ "').trigger()}");
					} else {
						textField = findTextFieldAndCreateExpandButton(child);
					}
				}
			}
		}
		return textField;
	}
}
