package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import aurora.application.Namespace;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.GridColumnConfig;
import aurora.presentation.component.std.config.GridConfig;

/**
 * 
 * @version $Id: Grid.java v 1.0 2010-1-25 下午01:30:09 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class Grid extends Component {
	
	public static final String HTML_LOCKAREA = "lockarea";
	public static final String HTML_UNLOCKAREA = "unlockarea";
	
	private static final int DEFALUT_HEAD_HEIGHT = 25;
	private static final int COLUMN_WIDTH = 100;
	
	private static final String DEFAULT_CLASS = "item-grid-wrap";
	private static final String MAX_ROWS = "maxRow";
	private static final String ROW_SPAN = "rowspan";
	private static final String COL_SPAN = "colspan";
	private static final String ROW_HEIGHT = "rowHeight";
	private static final String HEAD_HEIGHT = "headHeight";
	private static final String LOCK_WIDTH = "lockwidth";
	
	private static final String COLUMN_TYPE = "type";
	private static final String TYPE_CELL_CHECKBOX = "cellcheck";
//	private static final String TYPE_CELL_RADIO = "cellradio";
	private static final String TYPE_ROW_CHECKBOX = "rowcheck";
	private static final String TYPE_ROW_RADIO = "rowradio";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "grid/Grid-min.css");
		addJavaScript(session, context, "grid/Grid.js");
	}
	
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{	
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		boolean hasToolBar = creatToolBar(session,context);
		boolean hasNavBar = createNavgationToolBar(session,context);
		String style = "";
		if(hasToolBar){
			style += "border-top:none;";
		}
		if(hasNavBar){
			style += "border-bottom:none;";
		}
		map.put("gridstyle", style);
		processSelectable(map,view);
		createGridColumns(map,view,session);
		createGridEditors(session,context);
	}
	
	private void processSelectable(Map map,CompositeMap view){
		Boolean selectable = new Boolean(false);
		String selectionmodel = "multiple";
		CompositeMap root = view.getRoot();
		List list = CompositeUtil.findChilds(root, "dataSet");
		if(list!=null){
			String dds = view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
			Iterator it = list.iterator();
			while(it.hasNext()){
				CompositeMap ds = (CompositeMap)it.next();
				String id = ds.getString(ComponentConfig.PROPERTITY_ID, "");
				if("".equals(id)) {
					id= IDGenerator.getInstance().generate();
				}
				if(id.equals(dds)){
					selectable = new Boolean(ds.getBoolean(DataSetConfig.PROPERTITY_SELECTABLE, false));
					selectionmodel = ds.getString(DataSetConfig.PROPERTITY_SELECTIONMODEL, "multiple");
					break;
				}
			}
			
		}
		map.put(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
		map.put(DataSetConfig.PROPERTITY_SELECTIONMODEL, selectionmodel);
	}
	
	private void createGridColumns(Map map, CompositeMap view,BuildSession session){
		List jsons = new ArrayList(); 
		List cols = new ArrayList();
		Map lkpro = new HashMap();
		lkpro.put(LOCK_WIDTH, new Integer(0));
		lkpro.put(ROW_SPAN, new Integer(1));
		Map ukpro = new HashMap();
		ukpro.put(ROW_SPAN, new Integer(1));
		
		CompositeMap columns = view.getChild(GridConfig.PROPERTITY_COLUMNS);
		
		List lks = new ArrayList();
		List uks = new ArrayList();
		
		List locks = new ArrayList();
		List unlocks = new ArrayList();
		int maxRow =1;
		
		Integer height = (Integer)map.get(ComponentConfig.PROPERTITY_HEIGHT);
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		Integer viewWidth = (Integer)map.get(ComponentConfig.OLD_WIDTH);
		float bl = 1;
		if(viewWidth!=null && viewWidth.intValue() !=0) bl = (width.floatValue()/viewWidth.floatValue());
		
		
		if(columns != null) {
			boolean selectable = ((Boolean)map.get(DataSetConfig.PROPERTITY_SELECTABLE)).booleanValue();
			String selectmodel = (String)map.get(DataSetConfig.PROPERTITY_SELECTIONMODEL);
			if(selectable) {
				CompositeMap column = new CompositeMap("column");
				column.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
				column.putBoolean(GridColumnConfig.PROPERTITY_LOCK,true);
				column.putInt(ComponentConfig.PROPERTITY_WIDTH,25);
				column.putBoolean(GridColumnConfig.PROPERTITY_RESIZABLE,false);
				column.putBoolean(GridColumnConfig.PROPERTITY_SORTABLE,false);
				if("multiple".equals(selectmodel)) {
					column.putString(COLUMN_TYPE,TYPE_ROW_CHECKBOX);
				}else{
					column.putString(COLUMN_TYPE,TYPE_ROW_RADIO);
				}
				lks.add(column);
			}
			
			Iterator cit = columns.getChildIterator();
			while(cit.hasNext()){
				CompositeMap column = (CompositeMap)cit.next();
				boolean isLock = column.getBoolean(GridColumnConfig.PROPERTITY_LOCK, false);
				if(isLock){
					lks.add(column);
				}else{
					uks.add(column);
				}
			}
			
			processColumns(null, lks, locks, lkpro);
			processColumns(null, uks, unlocks, ukpro);
			int lr = ((Integer)lkpro.get(ROW_SPAN)).intValue();
			int ur = ((Integer)ukpro.get(ROW_SPAN)).intValue();
			
			if(ur >= lr){			
				maxRow = ur;
				ukpro.put(MAX_ROWS, new Integer(maxRow));
				ukpro.put(ROW_HEIGHT, new Integer(DEFALUT_HEAD_HEIGHT));
				lkpro.put(MAX_ROWS, new Integer(maxRow));
				lkpro.put(ROW_HEIGHT, lr == 0 ? new Integer(DEFALUT_HEAD_HEIGHT) : new Integer(ur*DEFALUT_HEAD_HEIGHT/lr));
			} else{
				maxRow = lr;
				lkpro.put(MAX_ROWS, new Integer(maxRow));
				ukpro.put(MAX_ROWS, new Integer(maxRow));
				lkpro.put(ROW_HEIGHT, new Integer(DEFALUT_HEAD_HEIGHT));
				ukpro.put(ROW_HEIGHT, ur == 0 ? new Integer(DEFALUT_HEAD_HEIGHT) : new Integer(lr*DEFALUT_HEAD_HEIGHT/ur));
			}
			
			
			List lkFirstList = (List)lkpro.get("l1");
			if(lkFirstList!=null) {
				Iterator lfit = lkFirstList.iterator();
				while(lfit.hasNext()){
					CompositeMap column = (CompositeMap)lfit.next();
					column.put(ROW_SPAN, lkpro.get(MAX_ROWS));
					addRowSpan(column);
				}
			}
			
			
			List ukFirstList = (List)ukpro.get("l1");
			if(ukFirstList!=null) {
				Iterator ufit = ukFirstList.iterator();
				while(ufit.hasNext()){
					CompositeMap column = (CompositeMap)ufit.next();
					column.put(ROW_SPAN, ukpro.get(MAX_ROWS));
					addRowSpan(column);
				}
			}
			cols.addAll(locks);
			cols.addAll(unlocks);
			Iterator it = cols.iterator();
			while(it.hasNext()){
				CompositeMap column = (CompositeMap)it.next();
				if(column.getChilds() == null){
					String dataindex = column.getString(GridColumnConfig.PROPERTITY_NAME,"");
					if(!"".equals(dataindex)) column.putString(GridColumnConfig.PROPERTITY_NAME, dataindex);
					if(column.getBoolean(GridColumnConfig.PROPERTITY_LOCK, false))column.putBoolean(GridColumnConfig.PROPERTITY_LOCK, column.getBoolean(GridColumnConfig.PROPERTITY_LOCK, false));
					if(column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false))column.putBoolean(GridColumnConfig.PROPERTITY_HIDDEN, column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false));
					if(!column.getBoolean(GridColumnConfig.PROPERTITY_RESIZABLE, true))column.putBoolean(GridColumnConfig.PROPERTITY_RESIZABLE, column.getBoolean(GridColumnConfig.PROPERTITY_RESIZABLE, true));
					if(column.getBoolean(GridColumnConfig.PROPERTITY_SORTABLE, false))column.putBoolean(GridColumnConfig.PROPERTITY_SORTABLE, column.getBoolean(GridColumnConfig.PROPERTITY_SORTABLE, false));
					
					float cwidth = column.getInt(ComponentConfig.PROPERTITY_WIDTH, COLUMN_WIDTH);
					String type = column.getString(COLUMN_TYPE);
					if(!"rowcheck".equals(type) && !"rowradio".equals(type))cwidth = cwidth*bl;
					column.putInt(ComponentConfig.PROPERTITY_WIDTH, Math.round(cwidth));
					String editor = column.getString(GridConfig.PROPERTITY_EDITOR, "");
					if(isCheckBoxEditor(editor, view)){
						column.putString(COLUMN_TYPE, TYPE_CELL_CHECKBOX);
					}
					JSONObject json = new JSONObject(column);
					jsons.add(json);
				}
			}		
		}
		
		String bindTarget = view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		map.put(ComponentConfig.PROPERTITY_BINDTARGET, bindTarget);
		map.put(HEAD_HEIGHT, new Integer(maxRow*DEFALUT_HEAD_HEIGHT));
		map.put(HTML_LOCKAREA, generateLockArea(map, locks, lkpro,session, bindTarget));
		map.put(HTML_UNLOCKAREA, generateUnlockArea(map, unlocks, ukpro,session, bindTarget));
		map.put(GridConfig.PROPERTITY_COLUMNS, jsons.toString());
		map.put("unlockwidth", new Integer(width.intValue()-((Integer)lkpro.get(LOCK_WIDTH)).intValue()));
		map.put("bodyHeight", new Integer(height.intValue()-maxRow*DEFALUT_HEAD_HEIGHT));
	}
	
	private void createGridEditors(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		CompositeMap editors = view.getChild(GridConfig.PROPERTITY_EDITORS);
		StringBuffer sb = new StringBuffer();
		if(editors != null && editors.getChilds() != null) {
			Iterator it = editors.getChildIterator();
			while(it.hasNext()){
				CompositeMap editor = (CompositeMap)it.next();
				editor.put(ComponentConfig.PROPERTITY_STYLE, "position:absolute;left:-1000px;top:-1000px;");
				try {
					sb.append(session.buildViewAsString(model, editor));
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}
			}
		}
		map.put("editors", sb.toString());
	}
	
	private boolean isCheckBoxEditor(String id, CompositeMap view){
		boolean isChecBox = false;
		CompositeMap editors = view.getChild(GridConfig.PROPERTITY_EDITORS);
		if(editors != null && editors.getChilds() != null) {
			Iterator it = editors.getChildIterator();
			while(it.hasNext()){
				CompositeMap editor = (CompositeMap)it.next();
				String eid = editor.getString(ComponentConfig.PROPERTITY_ID,"");
				if(id.equals(eid)&& "checkBox".equals(editor.getName())){
					isChecBox = true;
					break;
				}
			}
		}
		return isChecBox;
	}
	
	private boolean creatToolBar(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		CompositeMap toolbar = view.getChild(GridConfig.PROPERTITY_TOOLBAR);
		String dataset = view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		
		StringBuffer sb = new StringBuffer();
		boolean hasToolBar = false;
		if(toolbar != null && toolbar.getChilds() != null) {
			hasToolBar = true;
			CompositeMap tb = new CompositeMap(GridConfig.PROPERTITY_TOOLBAR);
			tb.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
//			String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+getDefaultWidth());
//			String wstr = uncertain.composite.TextParser.parse(widthStr, model);
//			Integer width = Integer.valueOf("".equals(wstr) ?  "150" : wstr);
			Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
			
			tb.put(ComponentConfig.PROPERTITY_ID, map.get(ComponentConfig.PROPERTITY_ID)+"_tb");
			tb.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(width.intValue()));
			tb.put(ComponentConfig.PROPERTITY_CLASSNAME, "grid-toolbar");
			Iterator it = toolbar.getChildIterator();
			while(it.hasNext()){
				CompositeMap item = (CompositeMap)it.next();
				if("button".equals(item.getName())){
					String type = item.getString("type");
					if(!"".equals(type)){
						if("add".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("AURORA_NEW"),"grid-add","background-position:0px 0px;","function(){$('"+dataset+"').create()}");
						}else if("delete".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("AURORA_DELETE"),"grid-delete","background-position:0px -35px;","function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').remove()}");
						}else if("save".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("AURORA_SAVE"),"grid-save","background-position:0px -17px;","function(){$('"+dataset+"').submit()}");
						}
					}
				}
				tb.addChild(item);
			}
			sb.append("<tr><td>");
			try {
				sb.append(session.buildViewAsString(model, tb));
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
			sb.append("</td></tr>");
		}
		map.put(GridConfig.PROPERTITY_TOOLBAR, sb.toString());
		return hasToolBar;
	}
	
	private CompositeMap createButton(CompositeMap button, String text, String clz,String style,String function){
		if("".equals(button.getString(Button.PROPERTITY_ICON,""))){
			button.put(Button.PROPERTITY_ICON, "null");
			button.put(Button.BUTTON_CLASS, clz);
			button.put(Button.BUTTON_STYLE, style);
		}
		button.put(Button.PROPERTITY_TEXT,button.getString(Button.PROPERTITY_TEXT, text));
		if(!"".equals(function))button.put(Button.PROPERTITY_CLICK, function);
		return button;
	}
	
	
	
	private boolean createNavgationToolBar(BuildSession session, ViewContext context) throws IOException{
		boolean hasNavBar = false;
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		StringBuffer sb = new StringBuffer();
		String dataset = view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		
		String nav = view.getString(GridConfig.PROPERTITY_NAVBAR,"");
		if("true".equalsIgnoreCase(nav)){
			hasNavBar = true;
			CompositeMap navbar = new CompositeMap("navBar");
			navbar.setNameSpaceURI(Namespace.AURORA_FRAMEWORK_NAMESPACE);
//			String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+getDefaultWidth());
//			String wstr = uncertain.composite.TextParser.parse(widthStr, model);
			Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);//Integer.valueOf("".equals(wstr) ?  "150" : wstr);
			
			
//			Integer width = Integer.valueOf(view.getString(ComponentConfig.PROPERTITY_WIDTH));
			navbar.put(ComponentConfig.PROPERTITY_ID, map.get(ComponentConfig.PROPERTITY_ID)+"_navbar");
			navbar.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(width.intValue()));
			navbar.put(ComponentConfig.PROPERTITY_CLASSNAME, "grid-navbar");
//			navbar.put(PROPERTITY_STYLE, "border:none;border-top:1px solid #cccccc;");
			navbar.put(NavBar.PROPERTITY_DATASET, dataset);
			sb.append("<tr><td>");
			try {
				sb.append(session.buildViewAsString(model, navbar));
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
			sb.append("</td></tr>");
			map.put("navbar", sb.toString());
		}
		return hasNavBar;
	}
	
	
	
	private void processColumns(CompositeMap parent, List children, List cols, Map pro){
		Iterator it = children.iterator();
		while(it.hasNext()){
			CompositeMap column = (CompositeMap)it.next();
			int level;
			if(parent == null){
				level = 1;				
			}else{
				level = parent.getInt("_level").intValue() + 1;
			}
			int rows = ((Integer)pro.get(ROW_SPAN)).intValue();
			if(level>rows)pro.put(ROW_SPAN, new Integer(level));
			
			column.put("_level", new Integer(level));
			column.put("_parent", parent);
			List hlist = (List)pro.get("l"+level);
			if(hlist == null){
				hlist = new ArrayList();
				pro.put("l"+level, hlist);
			}
			hlist.add(column);
			cols.add(column);
			if(column.getChilds() != null && column.getChilds().size() >0){
				processColumns(column, column.getChilds(), cols, pro);
			}else{
				addColSpan(column);
			}
		}
	}
	
	
	private void addRowSpan(CompositeMap column){
		List children = column.getChilds();
		Integer psp = column.getInt(ROW_SPAN);
		if(children != null && children.size() >0){
			minusRowSpan(column);
			Iterator it = children.iterator();
			while(it.hasNext()){
				CompositeMap child = (CompositeMap)it.next();
				child.put(ROW_SPAN, new Integer(psp.intValue()-1));
				addRowSpan(child);
			}
		}
		
	}
	
	private void minusRowSpan(CompositeMap column){
		if(column == null)return;
		Integer rowspan = column.getInt(ROW_SPAN);
		if(rowspan != null){
			int cs = rowspan.intValue() -1;
			column.put(ROW_SPAN, new Integer(cs));
		}
		CompositeMap parent = (CompositeMap)column.get("_parent");
		if(parent != null){			
			minusRowSpan(parent);
		}
		
	}
	
	
	private void addColSpan(CompositeMap column){
		if(column == null)return;
		CompositeMap parent = (CompositeMap)column.get("_parent");
		if(parent != null){
			Integer colspan = parent.getInt(COL_SPAN);
			if(colspan == null){
				parent.put(COL_SPAN, new Integer(1));
			}else{
				int cs = colspan.intValue() +1;
				parent.put(COL_SPAN, new Integer(cs));
			}
		}
		addColSpan(parent);
	}

	
	
	private String generateLockArea(Map map, List columns, Map pro,BuildSession session, String dataSet){
		StringBuffer sb = new StringBuffer();
		StringBuffer th = new StringBuffer();
		boolean hasLockColumn = false;
		Integer rows = (Integer)pro.get(ROW_SPAN);
		Iterator it = columns.iterator();
		int lockWidth = 0;
		while(it.hasNext()){
			CompositeMap column = (CompositeMap)it.next();
			if(column.getBoolean(GridColumnConfig.PROPERTITY_LOCK, false)){
				hasLockColumn = true;
				List children = column.getChilds();
				if(children == null){
					float cwidth = column.getInt(ComponentConfig.PROPERTITY_WIDTH, COLUMN_WIDTH);
					th.append("<TH style='width:"+cwidth+"px;' dataindex='"+column.getString(GridColumnConfig.PROPERTITY_NAME,"")+"'></TH>");
					lockWidth +=cwidth;				
				}				
			}
		}
		
		pro.put(LOCK_WIDTH, new Integer(lockWidth));
		
		if(hasLockColumn){
			sb.append("<DIV class='grid-la' atype='grid.lc' style='width:"+(lockWidth-1)+"px;'>");
			sb.append("<DIV class='grid-lh' atype='grid.lh' unselectable='on' onselectstart='return false;' style='height:"+rows.intValue()*((Integer)pro.get(ROW_HEIGHT)).intValue()+"px;'>");
			
			StringBuffer hsb = new StringBuffer();
			for(int i=1;i<=rows.intValue();i++){
				List list = (List)pro.get("l"+i);
				hsb.append("<TR height="+pro.get(ROW_HEIGHT)+">");
				if(list!=null) {
					Iterator lit = list.iterator();
					while(lit.hasNext()){
						CompositeMap column = (CompositeMap)lit.next();
						String ct =  column.getString(COLUMN_TYPE);
						if(TYPE_ROW_CHECKBOX.equals(ct)){
							hsb.append("<TD class='grid-hc' atype='grid.rowcheck'><center><div atype='grid.headcheck' class='grid-ckb item-ckb-u'></div></center></TD>");
						}else if(TYPE_ROW_RADIO.equals(ct)) {
							hsb.append("<TD class='grid-hc' atype='grid.rowradio'><div>&nbsp;</div></TD>");
						}else{
							boolean hidden =  column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false);
							if(!hidden){
								String headTitle = getFieldPrompt(session, column, dataSet);
								headTitle = session.getLocalizedPrompt(headTitle);
								hsb.append("<TD class='grid-hc' atype='grid.head' colspan='"+column.getInt(COL_SPAN,1)+"' rowspan='"+column.getInt(ROW_SPAN)+"' dataindex='"+column.getString(GridColumnConfig.PROPERTITY_NAME,"")+"'><div>"+headTitle+"</div></TD>");
							}
						}
					}
				}
				hsb.append("</TR>");
			}
			
			sb.append("<TABLE cellSpacing='0' atype='grid.lht' cellPadding='0' border='0' style='margin-right:20px;padding-right:20px;width:"+lockWidth+"px'><TBODY>");
			sb.append("<TR class='grid-hl'>");
			sb.append(th.toString());
			sb.append("</TR>");
			sb.append(hsb);
			sb.append("</TBODY></TABLE>");
			
			Integer height = (Integer)map.get(ComponentConfig.PROPERTITY_HEIGHT);
			sb.append("</DIV><DIV class='grid-lb' atype='grid.lb' style='width:100%;height:"+(height.intValue()-rows.intValue()*((Integer)pro.get(ROW_HEIGHT)).intValue())+"px'>");
			sb.append("</DIV></DIV>");
		}
		
		return sb.toString();
	}
	
	
	private String generateUnlockArea(Map map, List columns, Map pro,BuildSession session, String dataSet){
		StringBuffer sb = new StringBuffer();
		StringBuffer th = new StringBuffer();
		
		Integer rows = (Integer)pro.get(ROW_SPAN);
		Iterator it = columns.iterator();
		int unlockWidth = 0;
		while(it.hasNext()){
			CompositeMap column = (CompositeMap)it.next();
			if(!column.getBoolean(GridColumnConfig.PROPERTITY_LOCK, false)){
				List children = column.getChilds();
				if(children == null){
					float cwidth = column.getInt(ComponentConfig.PROPERTITY_WIDTH, COLUMN_WIDTH);
					th.append("<TH style='width:"+cwidth+"px;' dataindex='"+column.getString(GridColumnConfig.PROPERTITY_NAME,"")+"'></TH>");
					unlockWidth +=cwidth;				
				}				
			}
		}
		
		sb.append("<TABLE cellSpacing='0' atype='grid.uht' cellPadding='0' border='0' style='margin-right:20px;padding-right:20px;width:"+unlockWidth+"px'><TBODY>");
		sb.append("<TR class='grid-hl'>");
		sb.append(th.toString());
		sb.append("</TR>");
		
		StringBuffer hsb = new StringBuffer();
		for(int i=1;i<=rows.intValue();i++){
			List list = (List)pro.get("l"+i);
			hsb.append("<TR height="+pro.get(ROW_HEIGHT)+">");
			if(list!=null) {
				Iterator lit = list.iterator();
				while(lit.hasNext()){
					CompositeMap column = (CompositeMap)lit.next();
					boolean hidden =  column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false);
					if(!hidden){
						String headTitle = getFieldPrompt(session, column, dataSet);
						headTitle = session.getLocalizedPrompt(headTitle);
						hsb.append("<TD class='grid-hc' atype='grid.head'  colspan='"+column.getInt(COL_SPAN,1)+"' rowspan='"+column.getInt(ROW_SPAN)+"' dataindex='"+column.getString(GridColumnConfig.PROPERTITY_NAME,"")+"'><div>"+headTitle+"</div></TD>");
					}
				}
			}
			hsb.append("</TR>");
		}
		
		sb.append(hsb);
		sb.append("</TBODY></TABLE>");
		
		
		return sb.toString();
	}
	
}
