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
import aurora.application.ApplicationConfig;
import aurora.application.ApplicationViewConfig;
import aurora.application.AuroraApplication;
import aurora.application.IApplicationConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ButtonConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.GridColumnConfig;
import aurora.presentation.component.std.config.GridConfig;
import aurora.presentation.component.std.config.NavBarConfig;

/**
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class Grid extends Component {
	
	public static final String VERSION = "$Revision$";
	
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
	private static final String UNLOCK_WIDTH = "unlockwidth";
	private static final String BODY_HEIGHT = "bodyHeight";
	private static final String TABLE_HEIGHT = "tableHeight";
	private static final String FOOTER_BAR = "footerBar";
	private static final String LOCK_COLUMNS = "lockcolumns";
	private static final String UNLOCK_COLUMNS = "unlockcolumns";
	
	private static final String COLUMN_TYPE = "type";
	private static final String TYPE_CELL_CHECKBOX = "cellcheck";
//	private static final String TYPE_CELL_RADIO = "cellradio";
	private static final String TYPE_ROW_CHECKBOX = "rowcheck";
	private static final String TYPE_ROW_RADIO = "rowradio";
	private static final String TYPE_ROW_NUMBER = "rownumber";
	
	
	public Grid(IObjectRegistry registry) {
		super(registry);
    }
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "grid/Grid-min.css");
		addJavaScript(session, context, "grid/Grid-min.js");
	}
	
	protected int getDefaultWidth() {
		return 800;
	}
	
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{	
		CompositeMap view = context.getView();
		int mDefaultMarginSize = -1;
		ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
		if (view_config != null) {
			mDefaultMarginSize = view_config.getDefaultMarginWidth();           
		}
		if(mDefaultMarginSize != -1){
			view.putInt(ComponentConfig.PROPERTITY_MARGIN_WIDTH, mDefaultMarginSize);
		}
		
		
		CompositeMap model = context.getModel();
		super.onCreateViewContent(session, context);
		GridConfig gc = GridConfig.getInstance(view);
		Map map = context.getMap();
		boolean hasToolBar = creatToolBar(session,context);
		boolean hasFooterBar = hasFooterBar(gc.getColumns());
		boolean hasNavBar = createNavgationToolBar(session,context);
		
		String style = "";
		if(hasToolBar){
			style += "border-top:none;";
		}
		if(hasNavBar||hasFooterBar){
			style += "border-bottom:none;";
		}
		Integer height = (Integer)map.get(ComponentConfig.PROPERTITY_HEIGHT);
		int sh = 0;
		if(hasToolBar) sh +=25;
		if(hasFooterBar) sh +=25;
		if(hasNavBar) sh +=25;
		map.put(TABLE_HEIGHT, new Integer(height.intValue()-sh));
		String rowRenderer = gc.getRowRenderer();
		if(rowRenderer!=null) addConfig(GridConfig.PROPERTITY_ROW_RENDERER, rowRenderer);
		if(!gc.isAutoFocus()) addConfig(GridConfig.PROPERTITY_AUTO_FOCUS, new Boolean(gc.isAutoFocus()));
		addConfig(GridConfig.PROPERTITY_AUTO_APPEND, gc.isAutoAppend() == null ? view_config.getDefaultAutoAppend() : gc.isAutoAppend());
		addConfig(GridConfig.PROPERTITY_SUBMASK, gc.getSubMask() == null ? view_config.getDefaultGridSubmask() : gc.getSubMask());
		addConfig(GridConfig.PROPERTITY_CAN_PASTE, new Boolean(gc.isCanPaste()));
		addConfig(GridConfig.PROPERTITY_CAN_WHEEL, new Boolean(gc.isCanWheel()));
		
		processRowNumber(map,view);
		processSelectable(map,view);
		createGridColumns(map,view,session,model);
		
		if(hasFooterBar)creatFooterBar(session, context);
		
		map.put("gridstyle", style);		
		createGridEditors(session,context);
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void processRowNumber(Map map,CompositeMap view){
		GridConfig gc = GridConfig.getInstance(view);
		Boolean showRowNumber = gc.isShowRowNumber();
		map.put(GridConfig.PROPERTITY_SHOW_ROWNUMBER, showRowNumber);
		addConfig(GridConfig.PROPERTITY_SHOW_ROWNUMBER, showRowNumber);
		
	}
	
	@SuppressWarnings("unchecked")
	private void processSelectable(Map map,CompositeMap view){
		GridConfig gc = GridConfig.getInstance(view);
		Boolean selectable = new Boolean(false);
		Boolean showCheckAll = new Boolean(true);
		String selectionmodel = "multiple";
		CompositeMap root = view.getRoot();
		List list = CompositeUtil.findChilds(root, "dataSet");
		if(list!=null){
			String dds = gc.getBindTarget();
			Iterator it = list.iterator();
			while(it.hasNext()){
				CompositeMap ds = (CompositeMap)it.next();
				String id = ds.getString(ComponentConfig.PROPERTITY_ID, "");
				if("".equals(id)) {
					id= IDGenerator.getInstance().generate();
				}
				if(id.equals(dds)){
					selectable = new Boolean(ds.getBoolean(DataSetConfig.PROPERTITY_SELECTABLE, false));
					showCheckAll = new Boolean(ds.getBoolean(DataSetConfig.PROPERTITY_SHOW_CHECKALL, true));
					selectionmodel = ds.getString(DataSetConfig.PROPERTITY_SELECTION_MODEL, "multiple");
					break;
				}
			}
			
		}
		map.put(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
		map.put(DataSetConfig.PROPERTITY_SHOW_CHECKALL, showCheckAll);
		map.put(DataSetConfig.PROPERTITY_SELECTION_MODEL, selectionmodel);
		addConfig(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
		addConfig(DataSetConfig.PROPERTITY_SELECTION_MODEL, selectionmodel);
	}
	
	@SuppressWarnings("unchecked")
	private void createGridColumns(Map map, CompositeMap view,BuildSession session,CompositeMap model){
		JSONArray jsons = new JSONArray(); 
		List cols = new ArrayList();
		Map lkpro = new HashMap();
		ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
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
		
		Integer height = (Integer)map.get(TABLE_HEIGHT);
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		Integer viewWidth = (Integer)map.get(ComponentConfig.PROPERTITY_OLD_WIDTH);
		float bl = 1;
		//TODO:判断,如果column的宽度之和小于总宽度就同比放大
		GridConfig gc = GridConfig.getInstance(view);
		Boolean isAutoAdjust = gc.isAutoAdjust() == null ? view_config.getDefaultAutoAdjustGrid() : gc.isAutoAdjust();
		String bindTarget = gc.getBindTarget();//view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
		if(isAutoAdjust)
		if(viewWidth!=null && viewWidth.intValue() !=0) bl = (width.floatValue()/viewWidth.floatValue());
		
		
		if(columns != null) {
			boolean showRowNumber = ((Boolean)map.get(GridConfig.PROPERTITY_SHOW_ROWNUMBER)).booleanValue();
			if(showRowNumber) {
				CompositeMap column = new CompositeMap("column");
				column.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				column.putBoolean(GridColumnConfig.PROPERTITY_LOCK,true);
				column.putInt(ComponentConfig.PROPERTITY_WIDTH, 35);
				column.putString(GridColumnConfig.PROPERTITY_ALIGN, "center");
				column.putBoolean(GridColumnConfig.PROPERTITY_RESIZABLE,false);
				column.putBoolean(GridColumnConfig.PROPERTITY_SORTABLE,false);
				column.putString(GridColumnConfig.PROPERTITY_PROMPT,"#");
				column.putString(GridColumnConfig.PROPERTITY_RENDERER, "Aurora.RowNumberRenderer");
				column.putString(COLUMN_TYPE,TYPE_ROW_NUMBER);
				lks.add(column);
			}
			
			boolean selectable = ((Boolean)map.get(DataSetConfig.PROPERTITY_SELECTABLE)).booleanValue();
			String selectmodel = (String)map.get(DataSetConfig.PROPERTITY_SELECTION_MODEL);
			if(selectable) {
				CompositeMap column = new CompositeMap("column");
				column.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
				column.putBoolean(GridColumnConfig.PROPERTITY_LOCK,true);
				column.putInt(ComponentConfig.PROPERTITY_WIDTH, 25);
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
					//if(column.getBoolean(GridColumnConfig.PROPERTITY_LOCK, false))
						column.putBoolean(GridColumnConfig.PROPERTITY_LOCK, column.getBoolean(GridColumnConfig.PROPERTITY_LOCK, false));
					//if(column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false))
						boolean hidden = column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false);
						if(hidden) column.putBoolean(GridColumnConfig.PROPERTITY_VISIABLE, false);
						column.putBoolean(GridColumnConfig.PROPERTITY_HIDDEN, hidden);
					//if(!column.getBoolean(GridColumnConfig.PROPERTITY_RESIZABLE, true))
						column.putBoolean(GridColumnConfig.PROPERTITY_RESIZABLE, column.getBoolean(GridColumnConfig.PROPERTITY_RESIZABLE, true));
					//if(column.getBoolean(GridColumnConfig.PROPERTITY_SORTABLE, false))
						column.putBoolean(GridColumnConfig.PROPERTITY_SORTABLE, column.getBoolean(GridColumnConfig.PROPERTITY_SORTABLE, false));
					//if(!column.getBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, true))
						column.putBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, hidden?false:column.getBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, column.getString(COLUMN_TYPE)==null?true:false));
					
						column.putBoolean(GridColumnConfig.PROPERTITY_AUTO_ADJUST, column.getBoolean(GridColumnConfig.PROPERTITY_AUTO_ADJUST, true));
						column.putInt(GridColumnConfig.PROPERTITY_MAX_ADJUST_WIDTH, column.getInt(GridColumnConfig.PROPERTITY_MAX_ADJUST_WIDTH, 300));
						column.putString(GridColumnConfig.PROPERTITY_PROMPT,session.getLocalizedPrompt(uncertain.composite.TextParser.parse(column.getString(GridColumnConfig.PROPERTITY_PROMPT,getFieldPrompt(session, column, bindTarget)),model)));
					String  editorFunction = column.getString(GridColumnConfig.PROPERTITY_EDITOR_FUNCTION);
					if(editorFunction!=null) column.put(GridColumnConfig.PROPERTITY_EDITOR_FUNCTION, uncertain.composite.TextParser.parse(editorFunction, model));
					float cwidth = column.getInt(ComponentConfig.PROPERTITY_WIDTH, COLUMN_WIDTH);
					String type = column.getString(COLUMN_TYPE);
					if(!"rowcheck".equals(type) && !"rowradio".equals(type)&& !"rownumber".equals(type) && column.getBoolean(GridColumnConfig.PROPERTITY_AUTO_ADJUST))cwidth = cwidth*bl;
					column.putInt(ComponentConfig.PROPERTITY_WIDTH, Math.round(cwidth));
					String editor = column.getString(GridConfig.PROPERTITY_EDITOR, "");
					if(isCheckBoxEditor(editor, view)){
						column.putString(COLUMN_TYPE, TYPE_CELL_CHECKBOX);
					}
					if(!"".equals(editor)) column.put(GridColumnConfig.PROPERTITY_EDITOR, uncertain.composite.TextParser.parse(editor, model));
					String renderer = column.getString(GridColumnConfig.PROPERTITY_RENDERER, "");
					if(!"".equals(renderer))  column.put(GridColumnConfig.PROPERTITY_RENDERER, uncertain.composite.TextParser.parse(renderer, model));
					String footerRenderer = column.getString(GridColumnConfig.PROPERTITY_FOOTER_RENDERER, "");
					if(!"".equals(footerRenderer))  column.put(GridColumnConfig.PROPERTITY_FOOTER_RENDERER, uncertain.composite.TextParser.parse(footerRenderer, model));
					toJSONForParentColumn(column,session,model,bindTarget);
					JSONObject json = new JSONObject(column);
					jsons.put(json);
				}
			}		
		}
		
//		map.put(ComponentConfig.PROPERTITY_BINDTARGET, bindTarget);
		map.put(HEAD_HEIGHT, new Integer(maxRow*DEFALUT_HEAD_HEIGHT));
		map.put(LOCK_COLUMNS, locks);
		map.put(UNLOCK_COLUMNS, unlocks);
		map.put(HTML_LOCKAREA, generateLockArea(map, locks, lkpro,session, bindTarget,model));
		map.put(HTML_UNLOCKAREA, generateUnlockArea(map, unlocks, ukpro,session, bindTarget,model));
		Integer lockWidth = (Integer)lkpro.get(LOCK_WIDTH);
		map.put(LOCK_WIDTH, lockWidth);
		map.put(UNLOCK_WIDTH, new Integer(width.intValue()-lockWidth.intValue()));
		map.put(BODY_HEIGHT, new Integer(height.intValue()-maxRow*DEFALUT_HEAD_HEIGHT));
		
		addConfig(GridConfig.PROPERTITY_COLUMNS, jsons);
		addConfig(ComponentConfig.PROPERTITY_WIDTH, map.get(ComponentConfig.PROPERTITY_WIDTH));
		addConfig(ComponentConfig.PROPERTITY_HEIGHT, map.get(ComponentConfig.PROPERTITY_HEIGHT));
		map.put(CONFIG, getConfigString());
	}
	
	
	private void toJSONForParentColumn(CompositeMap column,BuildSession session,CompositeMap model,String bindTarget){
		CompositeMap parent=null;
		if(column.get("_parent") instanceof CompositeMap){
			parent=(CompositeMap) column.get("_parent");
			if(parent!=null){
				if(!parent.getBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, true))
					parent.putBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, parent.getBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, true));
				parent.putString(GridColumnConfig.PROPERTITY_PROMPT,session.getLocalizedPrompt(uncertain.composite.TextParser.parse(parent.getString(GridColumnConfig.PROPERTITY_PROMPT,getFieldPrompt(session, column, bindTarget)),model)));
				toJSONForParentColumn(parent,session,model,bindTarget);
				column.put("_parent", new JSONObject(parent));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
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
				editor.put(ComponentConfig.PROPERTITY_IS_CUST, new Boolean(false));
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
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	private boolean creatToolBar(BuildSession session, ViewContext context) throws IOException{
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		CompositeMap toolbar = view.getChild(GridConfig.PROPERTITY_TOOLBAR);
		String dataset = (String)map.get(ComponentConfig.PROPERTITY_BINDTARGET);
		
		StringBuffer sb = new StringBuffer();
		boolean hasToolBar = false;
		if(toolbar != null && toolbar.getChilds() != null) {
			hasToolBar = true;
			CompositeMap tb = new CompositeMap(GridConfig.PROPERTITY_TOOLBAR);
			tb.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
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
//				item.put(ComponentConfig.PROPERTITY_IS_CUST, new Boolean(false));
				if("button".equals(item.getName())){
					String type = item.getString("type");
					if(!"".equals(type)){
						if("add".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_NEW"),"grid-add","background-position:0px 0px;","function(){$('"+dataset+"').create()}");
						}else if("delete".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_DELETE"),"grid-delete","background-position:0px -35px;","function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').remove()}");
						}else if("save".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_SAVE"),"grid-save","background-position:0px -17px;","function(){$('"+dataset+"').submit()}");
						}else if("clear".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_CLEAR"),"grid-clear","background-position:0px -53px;","function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').clear()}");
						}else if("excel".equalsIgnoreCase(type)){
							item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"grid-excel","background-position:0px -69px;","function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export()}");
						}else if("customize".equalsIgnoreCase(type)){
							String path = model.getObject("/request/@context_path").toString();
							item = createButton(item,session.getLocalizedPrompt("HAP_CUST"),"grid-cust","background-position:0px -88px;","function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').customize('"+path+"')}");
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
		if("".equals(button.getString(ButtonConfig.PROPERTITY_ICON,""))){
			button.put(ButtonConfig.PROPERTITY_ICON, "null");
			button.put(ButtonConfig.PROPERTITY_BUTTON_CLASS, clz);
			button.put(ButtonConfig.PROPERTITY_BUTTON_STYLE, style);
		}
		button.put(ButtonConfig.PROPERTITY_TEXT,button.getString(ButtonConfig.PROPERTITY_TEXT, text));
		if(!"".equals(function))button.put(ButtonConfig.PROPERTITY_CLICK, function);
		return button;
	}
	
//	public boolean hasFooterBar(BuildSession session, ViewContext context){
//		boolean hasFooterBar = false;
//		CompositeMap view = context.getView();
//		GridConfig gc = GridConfig.getInstance(view);
//		CompositeMap columns = gc.getColumns();
//		List childs = columns.getChilds();
//		if(childs!=null){
//			Iterator it = childs.iterator();
//			while(it.hasNext()){
//				CompositeMap column = (CompositeMap)it.next();
//				GridColumnConfig gcc = GridColumnConfig.getInstance(column);
//				String footerRenderer = gcc.getFooterRenderer();
//				if(footerRenderer!=null){
//					hasFooterBar = true;
//					break;
//				}
//			}
//		}
//		return hasFooterBar;		
//	}
	
	@SuppressWarnings("unchecked")
	public boolean hasFooterBar(CompositeMap column){
		boolean hasFooterBar = false;
		GridColumnConfig gcc = GridColumnConfig.getInstance(column);
		String footerRenderer = gcc.getFooterRenderer();
		if(footerRenderer!=null){
			return true;
		}
		List childs = column.getChilds();
		if(childs!=null){
			Iterator it = childs.iterator();
			while(it.hasNext()){
				CompositeMap col = (CompositeMap)it.next();
				if(hasFooterBar(col)){
					hasFooterBar = true;
					break;
				}
			}
		}
		return hasFooterBar;		
	} 
	
	@SuppressWarnings("unchecked")
	private void creatFooterBar(BuildSession session, ViewContext context) throws IOException{
		Map map = context.getMap();
		int lockWidth = ((Integer)map.get(LOCK_WIDTH)).intValue();
		StringBuffer sb = new StringBuffer();
		sb.append("<tr><td><div class='grid-footerbar' atype='grid.fb"+"' style='width:"+map.get(ComponentConfig.PROPERTITY_WIDTH)+"px'>");			
		if(lockWidth!=0){
			sb.append("<div atype='grid.lf' style='float:left;width:"+(lockWidth-1)+"px'>");//class='grid-la' 
			List locks = (List)map.get(LOCK_COLUMNS);
			if(locks!=null){
				sb.append("<table cellSpacing='0' cellPadding='0' border='0' atype='fb.lbt' ");
				Iterator it = locks.iterator();
				sb.append(createFooterBarTable(it,false));
			}
			sb.append("</div>");
		}

		sb.append("<div class='grid-ua' atype='grid.uf' style='width:"+map.get(UNLOCK_WIDTH)+"px'>");
		List unlocks = (List)map.get(UNLOCK_COLUMNS);
		if(unlocks!=null){
			sb.append("<table cellSpacing='0' cellPadding='0' border='0' atype='fb.ubt' ");
			Iterator it = unlocks.iterator();
			sb.append(createFooterBarTable(it,true));
		}
		sb.append("</div>");
		sb.append("</div></td></tr>");
		map.put(FOOTER_BAR, sb.toString());		
	}
	
	@SuppressWarnings("unchecked")
	private String createFooterBarTable(Iterator it,boolean hasSpan){
		int i = 0,w = 0;
		StringBuffer sb = new StringBuffer();
		StringBuffer tb = new StringBuffer();
		StringBuffer th = new StringBuffer();
		th.append("<tr class='grid-hl'>");
		while(it.hasNext()){
			CompositeMap column = (CompositeMap)it.next();
			if(column.getChilds()!=null) continue;
			GridColumnConfig gcc = GridColumnConfig.getInstance(column);
			w += gcc.getWidth();
			th.append("<th style='width:"+gcc.getWidth()+"px;' dataindex='"+gcc.getName()+"'></th>");
			tb.append("<td style='text-align:"+gcc.getAlign()+";");
//			tb.append(((i==0) ? "border:none;" : "")+"'");
			tb.append("'");
			if(gcc.getName()!=null) tb.append("dataindex='"+gcc.getName()+"'");
			tb.append(">&#160;</td>");
			i++;
		}
		if(hasSpan)th.append("<th style='width:17px;'></th>");
		sb.append("style='width:"+w+"px;table-layout:fixed;'>");//margin-right:20px;padding-right:20px;
		sb.append(th.toString()).append("</tr><tr>");
		
		sb.append(tb.toString());
		sb.append("</tr></table>");
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private boolean createNavgationToolBar(BuildSession session, ViewContext context) throws IOException{
		boolean hasNavBar = false;
		CompositeMap view = context.getView();
		GridConfig gc = GridConfig.getInstance(view);
		
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		StringBuffer sb = new StringBuffer();
		String dataset = (String)map.get(ComponentConfig.PROPERTITY_BINDTARGET);
		
		boolean hasNav = gc.hasNavBar();
		if(hasNav){
			hasNavBar = true;
			CompositeMap navbar = new CompositeMap("navBar");
			navbar.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
//			String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+getDefaultWidth());
//			String wstr = uncertain.composite.TextParser.parse(widthStr, model);
			Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);//Integer.valueOf("".equals(wstr) ?  "150" : wstr);
			
			
//			Integer width = Integer.valueOf(view.getString(ComponentConfig.PROPERTITY_WIDTH));
			navbar.put(ComponentConfig.PROPERTITY_ID, map.get(ComponentConfig.PROPERTITY_ID)+"_navbar");
			navbar.put(ComponentConfig.PROPERTITY_IS_CUST, new Boolean(false));
			navbar.put(ComponentConfig.PROPERTITY_WIDTH, new Integer(width.intValue()));
			navbar.put(ComponentConfig.PROPERTITY_CLASSNAME, "grid-navbar");
//			navbar.put(PROPERTITY_STYLE, "border:none;border-top:1px solid #cccccc;");
			navbar.put(NavBarConfig.PROPERTITY_DATASET, dataset);
			navbar.put(NavBarConfig.PROPERTITY_NAVBAR_TYPE, view.getString(NavBarConfig.PROPERTITY_NAVBAR_TYPE,"complex"));
			navbar.put(NavBarConfig.PROPERTITY_MAX_PAGE_COUNT, new Integer(view.getInt(NavBarConfig.PROPERTITY_MAX_PAGE_COUNT,10)));
			navbar.put(NavBarConfig.PROPERTITY_PAGE_SIZE_EDITABLE,new Boolean(view.getBoolean(NavBarConfig.PROPERTITY_PAGE_SIZE_EDITABLE,true)));
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
	
	
	@SuppressWarnings("unchecked")
	private void processColumns(CompositeMap parent, List children, List cols, Map pro){
		Iterator it = children.iterator();
		boolean plock =parent!=null? (parent.getBoolean(GridColumnConfig.PROPERTITY_LOCK) != null ? parent.getBoolean(GridColumnConfig.PROPERTITY_LOCK).booleanValue() : false ):false;
		while(it.hasNext()){
			CompositeMap column = (CompositeMap)it.next();
			if(plock)
				column.putBoolean(GridColumnConfig.PROPERTITY_LOCK, true);
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
	
	@SuppressWarnings("unchecked")
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
			int cs = Math.max(rowspan.intValue() -1,1);
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

	
	@SuppressWarnings("unchecked")
	private String generateLockArea(Map map, List columns, Map pro,BuildSession session, String dataSet, CompositeMap model){
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
					boolean hidden = column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false);
					if(hidden) continue;
					float cwidth = hidden? 0 : column.getInt(ComponentConfig.PROPERTITY_WIDTH, COLUMN_WIDTH);
					th.append("<th style='width:"+cwidth+"px;' dataindex='"+column.getString(GridColumnConfig.PROPERTITY_NAME,"")+"'></th>");
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
						boolean showCheckAll = ((Boolean)map.get(DataSetConfig.PROPERTITY_SHOW_CHECKALL)).booleanValue();
						if(TYPE_ROW_CHECKBOX.equals(ct)){
							if(showCheckAll){
								hsb.append("<TD class='grid-hc' atype='grid.rowcheck' rowspan='"+column.getInt(ROW_SPAN)+"'><center><div atype='grid.headcheck' class='grid-ckb item-ckb-u'></div></center></TD>");
							}else{
								hsb.append("<TD class='grid-hc' atype='grid.rowcheck' rowspan='"+column.getInt(ROW_SPAN)+"'></TD>");
							}
						}else if(TYPE_ROW_RADIO.equals(ct)) {
							hsb.append("<TD class='grid-hc' atype='grid.rowradio' rowspan='"+column.getInt(ROW_SPAN)+"'><div>&nbsp;</div></TD>");
						}else{
							boolean hidden =  column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false);
							if(hidden) continue;
							String prompt = getFieldPrompt(session, column, dataSet);
							String headTitle = session.getLocalizedPrompt(prompt);
							if(headTitle!=null && headTitle.equals(prompt)){
								headTitle = uncertain.composite.TextParser.parse(prompt, model);
							}
							hsb.append("<TD class='grid-hc' atype='grid.head' style='visibility:"+(hidden?"hidden":"")+"' colspan='"+column.getInt(COL_SPAN,1)+"' rowspan='"+column.getInt(ROW_SPAN)+"' dataindex='"+column.getString(GridColumnConfig.PROPERTITY_NAME,"")+"'><div>"+headTitle+"</div></TD>");
						}
					}
				}
				hsb.append("</TR>");
			}
			
			sb.append("<TABLE cellSpacing='0' atype='grid.lht' cellPadding='0' border='0' style='width:"+lockWidth+"px'><TBODY>");//margin-right:20px;padding-right:20px;
			sb.append("<TR class='grid-hl'>");
			sb.append(th.toString());
			sb.append("</TR>");
			sb.append(hsb);
			sb.append("</TBODY></TABLE>");
			
			Integer height = (Integer)map.get(TABLE_HEIGHT);
			sb.append("</DIV><DIV class='grid-lb' atype='grid.lb' style='width:100%;height:"+(height.intValue()-rows.intValue()*((Integer)pro.get(ROW_HEIGHT)).intValue())+"px'>");
			sb.append("</DIV></DIV>");
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String generateUnlockArea(Map map, List columns, Map pro,BuildSession session, String dataSet, CompositeMap model){
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
					boolean hidden = column.getBoolean(GridColumnConfig.PROPERTITY_HIDDEN, false);
					if(hidden)continue;
					float cwidth = hidden?0:column.getInt(ComponentConfig.PROPERTITY_WIDTH, COLUMN_WIDTH);
					th.append("<th style='width:"+cwidth+"px;' dataindex='"+column.getString(GridColumnConfig.PROPERTITY_NAME,"")+"'></th>");
					unlockWidth +=cwidth;				
				}				
			}
		}
		
		sb.append("<TABLE cellSpacing='0' atype='grid.uht' cellPadding='0' border='0' style='width:"+unlockWidth+"px'><TBODY>");//margin-right:20px;padding-right:20px;
		sb.append("<TR class='grid-hl'>");
		sb.append(th.toString());
		sb.append("<TH WIDTH='20'> </TH>");
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
					if(hidden)continue;
					String prompt = getFieldPrompt(session, column, dataSet);
					String headTitle = session.getLocalizedPrompt(prompt);
					if(headTitle!=null && headTitle.equals(prompt)){
						headTitle = uncertain.composite.TextParser.parse(prompt, model);
					}
					hsb.append("<TD class='grid-hc' atype='grid.head' style='visibility:"+(hidden?"hidden":"")+"' colspan='"+column.getInt(COL_SPAN,1)+"' rowspan='"+column.getInt(ROW_SPAN)+"' dataindex='"+column.getString(GridColumnConfig.PROPERTITY_NAME,"")+"'><div>"+headTitle+"</div></TD>");
				}
			}
			hsb.append("</TR>");
		}
		sb.append(hsb);
		sb.append("</TBODY></TABLE>");		
		return sb.toString();
	}
	
}
