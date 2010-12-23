package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.GridColumnConfig;
import aurora.presentation.component.std.config.GridConfig;
import aurora.presentation.component.std.config.TableColumnConfig;
import aurora.presentation.component.std.config.TableConfig;

public class Table extends Component {

	private static final String ROW_SPAN = "rowspan";
	private static final String COL_SPAN = "colspan";
	private static final String DEFAULT_CLASS = "item-table";
	private static final String HEADS = "headss";
	private static final String FOOTS = "foots";

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	public void onPreparePageContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "table/Table-min.css");
		addJavaScript(session, context, "table/Table-min.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)
			throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getContextMap();
		CompositeMap view = context.getView();
		GridConfig gc = GridConfig.getInstance(view);
		
		String rowRenderer = gc.getRowRenderer();
		if (rowRenderer != null)
			addConfig(TableConfig.PROPERTITY_ROW_RENDERER, rowRenderer);
		List cols = new ArrayList();
		createHeads(map, view, session, cols);
		generateColumns(map,cols,hasFooterBar(gc.getColumns()));
		createGridEditors(session, context);
		map.put(CONFIG, getConfigString());
	}

	private void createGridEditors(BuildSession session, ViewContext context)
			throws IOException {
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		CompositeMap editors = view.getChild(TableConfig.PROPERTITY_EDITORS);
		StringBuffer sb = new StringBuffer();
		if (editors != null && editors.getChilds() != null) {
			Iterator it = editors.getChildIterator();
			while (it.hasNext()) {
				CompositeMap editor = (CompositeMap) it.next();
				editor.put(ComponentConfig.PROPERTITY_STYLE,
						"position:absolute;left:-1000px;top:-1000px;");
				try {
					sb.append(session.buildViewAsString(model, editor));
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}
			}
		}
		map.put("editors", sb.toString());
	}

	private void createHeads(Map map, CompositeMap view, BuildSession session,
			List cols) {
		CompositeMap columns = view.getChild(TableConfig.PROPERTITY_COLUMNS);
		String bindTarget = view
				.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		map.put(ComponentConfig.PROPERTITY_BINDTARGET, bindTarget);
		if (null == columns)
			return;
		List children = columns.getChilds();
		if (null == children || children.isEmpty())
			return;
		StringBuffer sb = new StringBuffer();
		Map pro = new HashMap();
		pro.put(ROW_SPAN, new Integer(1));
		processColumns(null, children, cols, pro);
		Integer rows = (Integer) pro.get(ROW_SPAN);
		List ukFirstList = (List) pro.get("l1");
		if (ukFirstList != null) {
			Iterator ufit = ukFirstList.iterator();
			while (ufit.hasNext()) {
				CompositeMap column = (CompositeMap) ufit.next();
				column.put(ROW_SPAN, rows);
				addRowSpan(column);
			}
		}

		for (int i = 1; i <= rows.intValue(); i++) {
			List list = (List) pro.get("l" + i);
			if (null != list) {
				sb.append("<TR height='25px'>");
				Iterator cit = list.iterator();
				if (null != cit) {
					while (cit.hasNext()) {
						sb.append(createColumn((CompositeMap) cit.next(),
								session, bindTarget));
					}
				}
				sb.append("</TR>");
			}
		}
		map.put("heads", sb.toString());
	}

	private void generateColumns(Map map,List cols,boolean hasFoot) {
		StringBuffer sb = new StringBuffer();
		JSONArray jsons = new JSONArray();
		Iterator it = cols.iterator();
		sb.append("<TFOOT><TR class='table-foot'>");
		while (it.hasNext()) {
			CompositeMap column = (CompositeMap) it.next();
			if (null == column.getChilds()) {
				JSONObject json = new JSONObject(column);
				jsons.put(json);
				sb.append("<TD dataindex='"+column.getString("name")+"'></TD>");
			}
		}
		sb.append("</TR></TFOOT>");
		if(hasFoot)map.put(FOOTS, sb.toString());
		addConfig(TableConfig.PROPERTITY_COLUMNS, jsons);
	}

	private void processColumns(CompositeMap parent, List children, List cols,
			Map pro) {
		Iterator it = children.iterator();
		while (it.hasNext()) {
			CompositeMap column = (CompositeMap) it.next();
			int level = (parent == null ? 1 : (parent.getInt("_level")
					.intValue() + 1));
			int rows = ((Integer) pro.get(ROW_SPAN)).intValue();
			if (level > rows)
				pro.put(ROW_SPAN, new Integer(level));

			column.put("_level", new Integer(level));
			column.put("_parent", parent);
			List hlist = (List) pro.get("l" + level);
			if (hlist == null) {
				hlist = new ArrayList();
				pro.put("l" + level, hlist);
			}
			hlist.add(column);
			cols.add(column);
			if (column.getChilds() != null && column.getChilds().size() > 0) {
				processColumns(column, column.getChilds(), cols, pro);
			} else {
				addColSpan(column);
			}
		}
	}

	private void minusRowSpan(CompositeMap column) {
		if (column == null)
			return;
		Integer rowspan = column.getInt(ROW_SPAN);
		if (rowspan != null) {
			int cs = rowspan.intValue() - 1;
			column.put(ROW_SPAN, new Integer(cs));
		}
		CompositeMap parent = (CompositeMap) column.get("_parent");
		if (parent != null) {
			minusRowSpan(parent);
		}

	}

	private void addRowSpan(CompositeMap column) {
		List children = column.getChilds();
		Integer psp = column.getInt(ROW_SPAN);
		if (children != null && children.size() > 0) {
			minusRowSpan(column);
			Iterator it = children.iterator();
			while (it.hasNext()) {
				CompositeMap child = (CompositeMap) it.next();
				child.put(ROW_SPAN, new Integer(psp.intValue() - 1));
				addRowSpan(child);
			}
		}
	}

	private void addColSpan(CompositeMap column) {
		if (column == null)
			return;
		CompositeMap parent = (CompositeMap) column.get("_parent");
		if (parent != null) {
			Integer colspan = parent.getInt(COL_SPAN);
			parent.put(COL_SPAN, new Integer(colspan == null ? 1 : (colspan
					.intValue() + 1)));
		}
		addColSpan(parent);
	}

	private String createColumn(CompositeMap column, BuildSession session,
			String dataset) {
		StringBuffer sb = new StringBuffer();
		String pw = column.getString(ComponentConfig.PROPERTITY_WIDTH);
		sb.append("<TD class='table-hc' colspan='" + column.getInt(COL_SPAN, 1)
				+ "' rowspan='" + column.getInt(ROW_SPAN) + "'"
				+ (pw == null ? "" : (" width='" + pw + "%'")) + ">");
		String text = session.getLocalizedPrompt(getFieldPrompt(session,
				column, dataset));
		sb.append("".equals(text) ? "&#160;" : text);
		sb.append("</TD>");
		return sb.toString();
	}

	public boolean hasFooterBar(CompositeMap column) {
		TableColumnConfig tcc = TableColumnConfig.getInstance(column);
		String footerRenderer = tcc.getFooterRenderer();
		if (footerRenderer != null) {
			return true;
		}
		List childs = column.getChilds();
		if (childs != null) {
			Iterator it = childs.iterator();
			while (it.hasNext()) {
				CompositeMap col = (CompositeMap) it.next();
				if (hasFooterBar(col)) {
					return true;
				}
			}
		}
		return false;
	}
}
