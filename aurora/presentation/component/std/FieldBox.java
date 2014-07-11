package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.BoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FieldBoxColumnConfig;
import aurora.presentation.component.std.config.FieldBoxConfig;
import aurora.presentation.component.std.config.FormConfig;
import aurora.presentation.component.std.config.GridLayoutConfig;

public class FieldBox extends Form {

	public static final String VERSION = "$Revision$";

	private static final String DEFAULT_HEAD_CLASS = "fieldbox_head";
	private static final String DEFAULT_BODY_CLASS = "fieldbox_body";

	private CompositeMap fieldBoxColumns;

	public FieldBox(IObjectRegistry registry) {
		super(registry);
	}

	protected String getHeadClass() {
		return DEFAULT_HEAD_CLASS;
	}

	protected String getBodyClass() {
		return DEFAULT_BODY_CLASS;
	}

	protected String getClassName(BuildSession session, CompositeMap model,
			CompositeMap view) throws Exception {
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String className = DEFAULT_TABLE_CLASS + " layout-fieldbox";
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		if(!"".equals(title)) className += " " + TITLE_CLASS;
		className += " " + cls;
		return className;
	}

	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		FieldBoxConfig fbc = FieldBoxConfig.getInstance(view);
		fieldBoxColumns = view.getChild(FieldBoxConfig.PROPERTITY_FIELDBOX_COLUMNS);
		if (null != fieldBoxColumns) {
			view.removeChild(fieldBoxColumns);
			List childs = fieldBoxColumns.getChilds();
			if (null != childs) {
				int length = childs.size();
				view.putInt(GridLayoutConfig.PROPERTITY_COLUMN, length);
				LinkedList[] list = new LinkedList[length];
				for (int i = 0; i < length; i++) {
					list[i] = new LinkedList();
				}
				Iterator it = childs.iterator();
				int i = 0, n = 0;
				while (it.hasNext()) {
					CompositeMap column = (CompositeMap) it.next();
					Iterator fieldList = column.getChildIterator();
					while (fieldList.hasNext()) {
						CompositeMap field = (CompositeMap) fieldList.next();
						int colspan = field.getInt(
								GridLayoutConfig.PROPERTITY_COLSPAN, 1);
						int rowspan = field.getInt(
								GridLayoutConfig.PROPERTITY_ROWSPAN, 1);
						list[i].add(field);
						for (int j = 0; j < rowspan; j++) {
							for (int k = 0; k < colspan; k++) {
								if (j != 0 || k != 0) {
									list[i + k].add(null);
								}
							}
						}
					}
					i++;
				}
				boolean isEmpty = true;
				CompositeMap preChild = null;
				for (i = 0; i < length;) {
					CompositeMap item = null;
					if (n < list[i].size()) {
						item = (CompositeMap) list[i].get(n);
						isEmpty = false;
					}
					if (null != item) {
						if (null == preChild) {
							for (int m = 0; m < i; m++) {
								if (n >= list[m].size()) {
									view.addChild(new CompositeMap("span"));
								}
							}
						}
						preChild = item;
						view.addChild(item);
					}
					i++;
					if (!isEmpty && i == length) {
						i = 0;
						n++;
						isEmpty = true;
						preChild = null;
					}
				}
			}
		}
		super.buildView(session, view_context);
	}

	protected void afterBuildTop(BuildSession session, CompositeMap model,
			CompositeMap view, int columns) throws Exception {
		super.afterBuildTop(session, model, view, columns);
		Writer out = session.getWriter();
		FieldBoxConfig fbc = FieldBoxConfig.getInstance(view);
		if (null != fieldBoxColumns) {
			out.write("<tr height='0'>");
			Iterator it = fieldBoxColumns.getChildIterator();
			while (it.hasNext()) {
				CompositeMap column = (CompositeMap) it.next();
				FieldBoxColumnConfig fbcc = FieldBoxColumnConfig
						.getInstance(column);
				Integer fieldWidth = fbcc.getFieldWidth(model) ==null? fbc
						.getFieldWidth(model) : fbcc.getFieldWidth(model);
				int labelWidth = fbcc.getLabelWidth(model) == null ? fbc
						.getLabelWidth(model) : fbcc.getLabelWidth(model).intValue();
				out.write("<th width='"+labelWidth+"'></th><td");
				if(null != fieldWidth){
					out.write(" width='"+fieldWidth+"'");
				}
				out.write("></td>");
			}
			out.write("</tr>");
		}
		out.write("<tr height='0'><td colspan=" + columns * 2 + "></td></tr>");
	}
}
