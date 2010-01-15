package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Grid extends Component {
	
	public static final String PROPERTITY_COLUMNS = "columns";
	public static final String PROPERTITY_EDITORS = "editors";
	public static final String PROPERTITY_DATASET = "dataset";
	public static final String PROPERTITY_AUTOQUERY = "autoquery";
	
	public static final String COLUMN_DATAINDEX = "dataindex";
	public static final String COLUMN_LOCK = "lock";
	public static final String COLUMN_HIDDEN = "hidden";
	public static final String COLUMN_PROMPT = "prompt";
	
	public static final String HTML_LOCKAREA = "lockarea";
	public static final String HTML_UNLOCKAREA = "unlockarea";
	
	private static final int DEFALUT_HEAD_HEIGHT = 25;
	private static final int COLUMN_WIDTH = 100;
	
	private static final String MAX_ROWS = "maxRow";
	private static final String ROW_SPAN = "rowspan";
	private static final String COL_SPAN = "colspan";
	private static final String ROW_HEIGHT = "rowHeight";
	private static final String HEAD_HEIGHT = "headHeight";
	private static final String LOCK_WIDTH = "lockwidth";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "grid/Grid.css");
		addJavaScript(session, context, "grid/Grid.js");
	}

	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		createGridColumns(map,view);
//		createGridEvents(session,context);
		createGridEditors(session,context);
	}
	
	
	private void createGridColumns(Map map, CompositeMap view){
		List jsons = new ArrayList(); 
		List cols = new ArrayList();
		Map lkpro = new HashMap();
		lkpro.put(LOCK_WIDTH, 0);
		lkpro.put(ROW_SPAN, 1);
		Map ukpro = new HashMap();
		ukpro.put(ROW_SPAN, 1);
		
		CompositeMap columns = view.getChild(PROPERTITY_COLUMNS);
		
		List lks = new ArrayList();
		List uks = new ArrayList();
		
		List locks = new ArrayList();
		List unlocks = new ArrayList();
		int maxRow =1;
		
		if(columns != null) {
			Iterator cit = columns.getChildIterator();
			while(cit.hasNext()){
				CompositeMap column = (CompositeMap)cit.next();
				boolean isLock = column.getBoolean(COLUMN_LOCK, false);
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
				ukpro.put(MAX_ROWS, maxRow);
				ukpro.put(ROW_HEIGHT, DEFALUT_HEAD_HEIGHT);
				lkpro.put(MAX_ROWS, maxRow);
				lkpro.put(ROW_HEIGHT, lr == 0 ? DEFALUT_HEAD_HEIGHT : ur*DEFALUT_HEAD_HEIGHT/lr);
			} else{
				maxRow = lr;
				lkpro.put(MAX_ROWS, maxRow);
				ukpro.put(MAX_ROWS, maxRow);
				lkpro.put(ROW_HEIGHT, DEFALUT_HEAD_HEIGHT);
				ukpro.put(ROW_HEIGHT, ur == 0 ? DEFALUT_HEAD_HEIGHT : lr*DEFALUT_HEAD_HEIGHT/ur);
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
					column.putString(COLUMN_DATAINDEX, column.getString(COLUMN_DATAINDEX,"").toLowerCase());
					column.putBoolean(COLUMN_LOCK, column.getBoolean(COLUMN_LOCK, false));
					column.putBoolean(COLUMN_HIDDEN, column.getBoolean(COLUMN_HIDDEN, false));
					column.putInt(PROPERTITY_WIDTH, column.getInt(PROPERTITY_WIDTH, COLUMN_WIDTH));
					JSONObject json = new JSONObject(column);
					jsons.add(json);
				}
			}		
		}
		
		map.put(PROPERTITY_DATASET, view.getString(PROPERTITY_DATASET));
		map.put(PROPERTITY_AUTOQUERY, view.getString(PROPERTITY_AUTOQUERY,"false"));
		map.put(HEAD_HEIGHT, maxRow*DEFALUT_HEAD_HEIGHT);
		map.put(HTML_LOCKAREA, generateLockArea(map, locks, lkpro));
		map.put(HTML_UNLOCKAREA, generateUnlockArea(map, unlocks, ukpro));
		map.put(PROPERTITY_COLUMNS, jsons.toString());
		
		Integer height = (Integer)map.get(PROPERTITY_HEIGHT);
		Integer width = (Integer)map.get(PROPERTITY_WIDTH);
		map.put("unlockwidth", width.intValue()-(Integer)lkpro.get(LOCK_WIDTH));
		map.put("bodyHeight", height.intValue()-maxRow*DEFALUT_HEAD_HEIGHT);
	}

	
	private void createGridEvents(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap events = view.getChild(PROPERTITY_EVENTS);
		StringBuffer sb = new StringBuffer();
		if(events != null && events.getChilds() != null) {
			Iterator it = events.getChildIterator();
			while(it.hasNext()){
				CompositeMap event = (CompositeMap)it.next();
				String name = event.getString("name","");
				String handler = event.getString("handler","");
				String id = (String)map.get(PROPERTITY_ID);
				if(!"".equals(name) && !"".equals(handler)){
					sb.append("$('"+id+"').on('"+name+"',"+handler+");");
				}
			}
		}
		map.put("events", sb.toString());
	}
	
	
	private void createGridEditors(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		CompositeMap editors = view.getChild(PROPERTITY_EDITORS);
		StringBuffer sb = new StringBuffer();
		if(editors != null && editors.getChilds() != null) {
			Iterator it = editors.getChildIterator();
			while(it.hasNext()){
				CompositeMap editor = (CompositeMap)it.next();
//				editor.put(PROPERTITY_HIDDEN, true);
				editor.put(PROPERTITY_STYLE, "position:absolute;left:-1000px;top:-1000px;");
				try {
					sb.append(session.buildViewAsString(model, editor));
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}
			}
		}
		map.put("editors", sb.toString());
	}
	
	
	
	private void processColumns(CompositeMap parent, List children, List cols, Map pro){
		Iterator it = children.iterator();
		while(it.hasNext()){
			CompositeMap column = (CompositeMap)it.next();
			int level;
			if(parent == null){
				level = 1;				
			}else{
				level = parent.getInt("_level") + 1;
			}
			int rows = ((Integer)pro.get(ROW_SPAN)).intValue();
			if(level>rows)pro.put(ROW_SPAN, level);
			
			column.put("_level", level);
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
				child.put(ROW_SPAN, psp-1);
				addRowSpan(child);
			}
		}
		
	}
	
	private void minusRowSpan(CompositeMap column){
		if(column == null)return;
		Integer rowspan = column.getInt(ROW_SPAN);
		if(rowspan != null){
			int cs = rowspan.intValue() -1;
			column.put(ROW_SPAN, cs);
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
				parent.put(COL_SPAN, 1);
			}else{
				int cs = colspan.intValue() +1;
				parent.put(COL_SPAN, cs);
			}
		}
		addColSpan(parent);
	}

	
	
	private String generateLockArea(Map map, List columns, Map pro){
		StringBuffer sb = new StringBuffer();
		StringBuffer th = new StringBuffer();
		boolean hasLockColumn = false;
		Integer rows = (Integer)pro.get(ROW_SPAN);
		Iterator it = columns.iterator();
		int lockWidth = 0;
		while(it.hasNext()){
			CompositeMap column = (CompositeMap)it.next();
			if(column.getBoolean(COLUMN_LOCK, false)){
				hasLockColumn = true;
				List children = column.getChilds();
				if(children == null){
					th.append("<TH style='width:"+column.getInt(PROPERTITY_WIDTH, COLUMN_WIDTH)+"px;' dataindex='"+column.getString(COLUMN_DATAINDEX,"").toLowerCase()+"'></TH>");
					lockWidth +=column.getInt(PROPERTITY_WIDTH, COLUMN_WIDTH);				
				}				
			}
		}
		
		pro.put(LOCK_WIDTH, lockWidth);
		
		if(hasLockColumn){
			sb.append("<DIV class='grid-la' atype='grid.lc' style='width:"+(lockWidth-1)+"px;'>");
			sb.append("<DIV class='grid-lh' atype='grid.lh' unselectable='on' onselectstart='return false;' style='height:"+rows*(Integer)pro.get(ROW_HEIGHT)+"px;'>");
			
			StringBuffer hsb = new StringBuffer();
			for(int i=1;i<=rows;i++){
				List list = (List)pro.get("l"+i);
				hsb.append("<TR height="+pro.get(ROW_HEIGHT)+">");
				if(list!=null) {
					Iterator lit = list.iterator();
					while(lit.hasNext()){
						CompositeMap column = (CompositeMap)lit.next();
						Boolean hidden =  column.getBoolean(COLUMN_HIDDEN, false);
						if(!hidden)hsb.append("<TD class='grid-hc' colspan='"+column.getInt(COL_SPAN)+"' rowspan='"+column.getInt(ROW_SPAN)+"' dataindex='"+column.getString(COLUMN_DATAINDEX,"").toLowerCase()+"'><div>"+column.getString(COLUMN_PROMPT, "")+"</div></TD>");
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
			
			Integer height = (Integer)map.get(PROPERTITY_HEIGHT);
			sb.append("</DIV><DIV class='grid-lb' atype='grid.lb' style='width:100%;height:"+(height.intValue()-rows*(Integer)pro.get(ROW_HEIGHT))+"px'>");
			sb.append("</DIV></DIV>");
		}
		
		return sb.toString();
	}
	
	
	private String generateUnlockArea(Map map, List columns, Map pro){
		StringBuffer sb = new StringBuffer();
		StringBuffer th = new StringBuffer();
		
		Integer rows = (Integer)pro.get(ROW_SPAN);
		Iterator it = columns.iterator();
		int unlockWidth = 0;
		while(it.hasNext()){
			CompositeMap column = (CompositeMap)it.next();
			if(!column.getBoolean(COLUMN_LOCK, false)){
				List children = column.getChilds();
				if(children == null){
					th.append("<TH style='width:"+column.getInt(PROPERTITY_WIDTH, COLUMN_WIDTH)+"px;' dataindex='"+column.getString(COLUMN_DATAINDEX,"").toLowerCase()+"'></TH>");
					unlockWidth +=column.getInt(PROPERTITY_WIDTH, COLUMN_WIDTH);				
				}				
			}
		}
		
		sb.append("<TABLE cellSpacing='0' atype='grid.uht' cellPadding='0' border='0' style='margin-right:20px;padding-right:20px;width:"+unlockWidth+"px'><TBODY>");
		sb.append("<TR class='grid-hl'>");
		sb.append(th.toString());
		sb.append("</TR>");
		
		StringBuffer hsb = new StringBuffer();
		for(int i=1;i<=rows;i++){
			List list = (List)pro.get("l"+i);
			hsb.append("<TR height="+pro.get(ROW_HEIGHT)+">");
			if(list!=null) {
				Iterator lit = list.iterator();
				while(lit.hasNext()){
					CompositeMap column = (CompositeMap)lit.next();
					Boolean hidden =  column.getBoolean(COLUMN_HIDDEN, false);
					if(!hidden)hsb.append("<TD class='grid-hc' colspan='"+column.getInt(COL_SPAN)+"' rowspan='"+column.getInt(ROW_SPAN)+"' dataindex='"+column.getString(COLUMN_DATAINDEX,"").toLowerCase()+"'><div>"+column.getString(COLUMN_PROMPT, "")+"</div></TD>");
				}
			}
			hsb.append("</TR>");
		}
		
		sb.append(hsb);
		sb.append("</TBODY></TABLE>");
		
		
		return sb.toString();
	}
	
}
