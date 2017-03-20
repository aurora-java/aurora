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
import uncertain.composite.CompositeUtil;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationViewConfig;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ButtonConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.NavBarConfig;
import aurora.presentation.component.std.config.TableColumnConfig;
import aurora.presentation.component.std.config.TableConfig;

@SuppressWarnings("unchecked")
public class Table extends Component {

	public Table(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	private static final String ROW_SPAN = "rowspan";
	private static final String COL_SPAN = "colspan";
	private static final String DEFAULT_CLASS = "item-table";
	private static final String HEADS = "heads";
	private static final String FOOTS = "foots";

	private static final String COLUMN_TYPE = "type";
	private static final String TYPE_ROW_CHECKBOX = "rowcheck";
	private static final String TYPE_ROW_RADIO = "rowradio";

	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return DEFAULT_CLASS;
	}

	protected int getDefaultWidth() {
		return -1;
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
		TableConfig tc = TableConfig.getInstance(view);

		String rowRenderer = tc.getRowRenderer();
		String percentWidth = tc.getPercentWidth();
		String width = tc.getWidthStr();
		if (rowRenderer != null)
			addConfig(TableConfig.PROPERTITY_ROW_RENDERER, rowRenderer);
		if (null != width)
			map.put(TableConfig.PROPERTITY_PERCENT_WIDTH, width + "px");
		else if (null != percentWidth)
			map.put(TableConfig.PROPERTITY_PERCENT_WIDTH, percentWidth + "%");
		List cols = new ArrayList();
		createHeads(map, view, session, cols);
		generateColumns(map, cols, hasFooterBar(tc.getColumns()));
		createTableEditors(session, context);
		createNavgationToolBar(session, context);
		String title = session.getLocalizedPrompt(tc.getTitle(context.getModel()));
		if (null != title && !"".equals(title)) {
			title = "<TR><TD class='table_title' colspan='" + cols.size()
					+ "'>" + title + "</TD></TR>";
			map.put(TableConfig.PROPERTITY_TITLE, title);
		}
		creatToolBar(session,context,cols);
		if (!tc.isAutoAppend())
			addConfig(TableConfig.PROPERTITY_AUTO_APPEND,
					Boolean.valueOf(tc.isAutoAppend()));
		addConfig(TableConfig.PROPERTITY_CAN_WHEEL,
				Boolean.valueOf(tc.isCanWheel()));
		addConfig(TableConfig.PROPERTITY_GROUP_SELECT,
				Boolean.valueOf(tc.isGroupSelect()));
		map.put(CONFIG, getConfigString());
		map.put(TableConfig.PROPERTITY_TAB_INDEX, new Integer(tc.getTabIndex()));
	}

	private void processSelectable(Map map, CompositeMap view, CompositeMap cols,String bindTarget) {
		Boolean selectable = Boolean.FALSE;
		String selectionmodel = "multiple";
		CompositeMap root = view.getRoot();
		List list = CompositeUtil.findChilds(root, "dataSet");
		if (list != null) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				CompositeMap ds = (CompositeMap) it.next();
				DataSetConfig dsc = DataSetConfig.getInstance(ds);
				String id = dsc.getId("");
				if ("".equals(id)) {
					id = IDGenerator.getInstance().generate();
				}
				if (id.equals(bindTarget)) {
					selectable = Boolean.valueOf(dsc.isSelectable());
					selectionmodel = dsc.getSelectionModel();
					break;
				}
			}

		}
		map.put(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
		map.put(DataSetConfig.PROPERTITY_SELECTION_MODEL, selectionmodel);
		addConfig(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
		addConfig(DataSetConfig.PROPERTITY_SELECTION_MODEL, selectionmodel);
		if (selectable.booleanValue()) {
			CompositeMap column = new CompositeMap("column");
			column.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			column.putInt(ComponentConfig.PROPERTITY_WIDTH, 25);
			if ("multiple".equals(selectionmodel)) {
				column.putString(COLUMN_TYPE, TYPE_ROW_CHECKBOX);
			} else {
				column.putString(COLUMN_TYPE, TYPE_ROW_RADIO);
			}
			cols.addChild(0, column);
		}
	}

	private void createTableEditors(BuildSession session, ViewContext context)
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
				editor.put(ComponentConfig.PROPERTITY_TAB_INDEX, new Integer(-1));
				editor.put(ComponentConfig.PROPERTITY_STYLE,
						"position:absolute;left:-1000px;top:-1000px;");
				try {
					sb.append(session.buildViewAsString(model, editor));
				} catch (Exception e) {
					throw new IOException(e);
				}
			}
		}
		map.put(TableConfig.PROPERTITY_EDITORS, sb.toString());
	}

	private void createHeads(Map map, CompositeMap view, BuildSession session,
			List cols) {
		CompositeMap columns = view.getChild(TableConfig.PROPERTITY_COLUMNS);
		TableConfig tc = TableConfig.getInstance(view);
		String bindTarget = tc.getBindTarget();
//		map.put(ComponentConfig.PROPERTITY_BINDTARGET, bindTarget);
		if (null == columns)
			return;
		processSelectable(map, view, columns,bindTarget);
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
		boolean showHead = tc.isShowHead();
		for (int i = 1; i <= rows.intValue(); i++) {
			List list = (List) pro.get("l" + i);
			if (null != list) {
				sb.append("<TR class='table-head' unselectable='on' onselectstart='return false;' style='cursor:default'  height='");
				sb.append(showHead?"25":"0");
				sb.append("'>");
				Iterator cit = list.iterator();
				if (null != cit) {
					while (cit.hasNext()) {
						sb.append(createColumn((CompositeMap) cit.next(),
								session, bindTarget,showHead));
					}
				}
				sb.append("</TR>");
			}
		}
		map.put(HEADS, sb.toString());
	}

	private void generateColumns(Map map, List cols, boolean hasFoot) {
		StringBuffer sb = new StringBuffer();
		JSONArray jsons = new JSONArray();
		Iterator it = cols.iterator();
		sb.append("<TFOOT><TR class='table-foot'>");
		while (it.hasNext()) {
			CompositeMap column = (CompositeMap) it.next();
			if (null == column.getChilds()) {
				TableColumnConfig tcc = TableColumnConfig.getInstance(column);
				if(tcc.isHidden())column.putBoolean(TableColumnConfig.PROPERTITY_HIDDEN, tcc.isHidden());
				if(tcc.isSortable())column.putBoolean(TableColumnConfig.PROPERTITY_SORTABLE, tcc.isSortable());
				if(tcc.isGroup()) column.putBoolean(TableColumnConfig.PROPERTITY_GROUP, tcc.isGroup());
				JSONObject json = new JSONObject(column);
				jsons.put(json);
				sb.append("<TD dataindex='");
				sb.append(tcc.getName());
				sb.append("' align='");
				sb.append(tcc.getAlign());
				sb.append("'");
				if (tcc.isHidden()) {
					sb.append(" style='display:none'");
				}
				sb.append(">&#160;</TD>");
			}
		}
		sb.append("</TR></TFOOT>");
		if (hasFoot)
			map.put(FOOTS, sb.toString());
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
			parent.put(COL_SPAN,
					new Integer(colspan == null ? 1 : (colspan.intValue() + 1)));
		}
		addColSpan(parent);
	}

	private String createColumn(CompositeMap column, BuildSession session,
			String dataset,boolean showHead) {
		StringBuffer sb = new StringBuffer();
		TableColumnConfig tcc = TableColumnConfig.getInstance(column);
		String ct = column.getString(COLUMN_TYPE);
		String pw = "";
		if (null != tcc.getPercentWidth())
			pw = tcc.getPercentWidth() + "%";
		if (null != column.getString(ComponentConfig.PROPERTITY_WIDTH))
			pw = column.getString(ComponentConfig.PROPERTITY_WIDTH);

		sb.append("<TD");
		if(showHead)sb.append(" class='table-hc'");
		if (TYPE_ROW_CHECKBOX.equals(ct)) {
			sb.append(" atype='table.rowcheck' style='width:25px;' rowspan='");
			sb.append(column.getInt(ROW_SPAN));
			sb.append("'><center><div atype='table.headcheck' class='table-ckb item-ckb-u'></div></center>");
		} else if (TYPE_ROW_RADIO.equals(ct)) {
			sb.append(" atype='table.rowradio' style='width:25px;' rowspan='");
			sb.append(column.getInt(ROW_SPAN));
			sb.append("'><div style='width:13px'>&nbsp;</div>");
		} else {
			sb.append(" dataindex='");
			sb.append(tcc.getName());
			sb.append("' colspan='");
			sb.append(column.getInt(COL_SPAN, 1));
			sb.append("' rowspan='");
			sb.append(column.getInt(ROW_SPAN));
			sb.append("'");
			sb.append((pw == null ? "" : (" width='" + pw + "'")));
			if (tcc.isHidden()) {
				sb.append(" style='display:none'");
			}
			sb.append(">");
			if(showHead){
			String text = session.getLocalizedPrompt(getFieldPrompt(session,
					column, dataset));
			sb.append("".equals(text) ? "&#160;" : text);
			}
		}
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
	
	private CompositeMap createButton(CompositeMap button, String text, String clz,String style,String function){
		if("".equals(button.getString(ButtonConfig.PROPERTITY_ICON,""))){
			button.put(ButtonConfig.PROPERTITY_ICON, "null");
			button.put(ButtonConfig.PROPERTITY_BUTTON_CLASS, clz);
			button.put(ButtonConfig.PROPERTITY_BUTTON_STYLE, style);
		}
		button.put(ButtonConfig.PROPERTITY_TEXT,button.getString(ButtonConfig.PROPERTITY_TEXT, text));
		if(!"".equals(function))button.put(ButtonConfig.PROPERTITY_CLICK, function);
		return button;
	}
	
	private boolean creatToolBar(BuildSession session, ViewContext context,List cols) throws IOException{
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		CompositeMap toolbar = view.getChild(TableConfig.PROPERTITY_TOOLBAR);
		String dataset = (String)map.get(ComponentConfig.PROPERTITY_BINDTARGET);
		
		StringBuilder sb = new StringBuilder();
		boolean hasToolBar = false;
		if(toolbar != null && toolbar.getChilds() != null) {
			hasToolBar = true;
			CompositeMap tb = new CompositeMap(TableConfig.PROPERTITY_TOOLBAR);
			tb.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
//			String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+getDefaultWidth());
//			String wstr = uncertain.composite.TextParser.parse(widthStr, model);
//			Integer width = Integer.valueOf("".equals(wstr) ?  "150" : wstr);
			Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
			
			tb.put(ComponentConfig.PROPERTITY_ID, map.get(ComponentConfig.PROPERTITY_ID)+"_tb");
			tb.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(width.intValue()));
			tb.put(ComponentConfig.PROPERTITY_CLASSNAME, "table-toolbar");
			Iterator it = toolbar.getChildIterator();
			while(it.hasNext()){
				CompositeMap item = (CompositeMap)it.next();
//				item.put(ComponentConfig.PROPERTITY_IS_CUST, Boolean.FALSE);
				if("button".equals(item.getName())){
					String type = item.getString("type");
					String fileName = uncertain.composite.TextParser.parse(item.getString("filename",""),model);
					if(!"".equals(type)){
						if("add".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_NEW"),"table-add","background-position:0px 0px;","function(){$au('"+dataset+"').create()}");
						}else if("delete".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_DELETE"),"table-delete","background-position:0px -35px;","function(){$au('"+map.get(ComponentConfig.PROPERTITY_ID)+"').remove()}");
						}else if("save".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_SAVE"),"table-save","background-position:0px -17px;","function(){$au('"+dataset+"').submit()}");
						}else if("clear".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_CLEAR"),"table-clear","background-position:0px -52px;","function(){$au('"+map.get(ComponentConfig.PROPERTITY_ID)+"').clear()}");
						}else if("excel".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"table-excel","background-position:0px -69px;","function(){$au('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export('xls','"+fileName+"')}");
						}else if("excelmemo".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"table-excel","background-position:0px -69px;","function(){$au('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export('xls_memory','"+fileName+"')}");
						}else if("excel2007".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"table-excel","background-position:0px -126px;","function(){$au('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export('xlsx','"+fileName+"')}");
						}else if("txt".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"table-excel","background-position:0px -107px;","function(){$au('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export('txt','"+fileName+"')}");
						}else if("customize".equalsIgnoreCase(type)){
							String path = model.getObject("/request/@context_path").toString();
							item = createButton(item,session.getLocalizedPrompt("HAP_CUST"),"table-cust","background-position:0px -88px;","function(){$au('"+map.get(ComponentConfig.PROPERTITY_ID)+"').customize('"+path+"')}");
						}
					}
				}
				tb.addChild(item);
			}
			sb.append("<tr><td colspan=\""+cols.size()+"\">");
			try {
				sb.append(session.buildViewAsString(model, tb));
			} catch (Exception e) {
				throw new IOException(e);
			}
			sb.append("</td></tr>");
		}
		map.put(TableConfig.PROPERTITY_TOOLBAR, sb.toString());
		return hasToolBar;
	}
	private boolean createNavgationToolBar(BuildSession session,
			ViewContext context) throws IOException {
		boolean hasNavBar = false;
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		TableConfig tc = TableConfig.getInstance(view);
		StringBuffer sb = new StringBuffer();
		String dataset = tc.getBindTarget();
		if (tc.hasNavBar()) {
			hasNavBar = true;
			CompositeMap navbar = new CompositeMap("navBar");
			navbar.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
			Integer width = (Integer) map.get(ComponentConfig.PROPERTITY_WIDTH);

			navbar.put(ComponentConfig.PROPERTITY_ID,
					map.get(ComponentConfig.PROPERTITY_ID) + "_navbar");
			navbar.put(ComponentConfig.PROPERTITY_CLASSNAME, "table-navbar");
			navbar.put(NavBarConfig.PROPERTITY_DATASET, dataset);
			DataSetConfig dc = DataSetConfig.getInstance(getDataSet(session, dataset));
			boolean mDefaultAutoCount = ApplicationViewConfig.DEFAULT_AUTO_COUNT;
			if (mApplicationConfig != null) {
				ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
				if (null != view_config) {
					mDefaultAutoCount = view_config.getDefaultAutoCount();
				}
			}
			boolean autoCount = dc.isAutoCount(mDefaultAutoCount);
			navbar.put(NavBarConfig.PROPERTITY_NAVBAR_TYPE,
					view.getString(NavBarConfig.PROPERTITY_NAVBAR_TYPE, autoCount?"complex":"tiny"));
			navbar.put(
					NavBarConfig.PROPERTITY_MAX_PAGE_COUNT,
					new Integer(view.getInt(NavBarConfig.PROPERTITY_MAX_PAGE_COUNT,
							10)));
			navbar.put(
					NavBarConfig.PROPERTITY_PAGE_SIZE_EDITABLE,
					Boolean.valueOf(view.getBoolean(
							NavBarConfig.PROPERTITY_PAGE_SIZE_EDITABLE, true)));
			sb.append("<caption align='bottom'>");
			try {
				sb.append(session.buildViewAsString(model, navbar));
			} catch (Exception e) {
				throw new IOException(e);
			}
			sb.append("</caption>");
			map.put("navbar", sb.toString());
		}
		return hasNavBar;
	}
}
