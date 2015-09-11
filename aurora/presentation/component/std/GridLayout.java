package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.BoxConfig;
import aurora.presentation.component.std.config.GridBoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.GridLayoutConfig;

/**
 * GridLayout.
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class GridLayout extends Component implements IViewBuilder{
	
	public GridLayout(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	public static final String ROWS = "row";
	public static final String COLUMNS = "column";
	
	protected static final int UNLIMITED = -1;
//	protected static final String PROPERTITY_CELLPADDING = "cellpadding";
//	protected static final String PROPERTITY_CELLSPACING = "cellspacing";
//	protected static final String PROPERTITY_VALIDALIGN = "validalign";
//	protected static final String PROPERTITY_PADDING = "padding";
//	protected static final String PROPERTITY_WRAPPER_ADJUST = "wrapperadjust";
	
	protected static final String TITLE_CLASS = "layout-title";
	protected static final String DEFAULT_TABLE_CLASS = "layout-table";
	protected static final String DEFAULT_TD_CELL = "layout-td-cell";
	protected static final String DEFAULT_TD_CONTAINER = "layout-td-con";
	
	
	protected int getDefaultWidth(){
		return 0;
	}
	
	protected int getDefaultHeight(){
		return 0;
	}
	
	protected int getRows(CompositeMap view,CompositeMap model){
		GridLayoutConfig glc = new GridLayoutConfig();
		glc.initialize(view);
		return glc.getRow(model, UNLIMITED);
	}
	
	protected int getColumns(CompositeMap view,CompositeMap model){
		GridLayoutConfig glc = new GridLayoutConfig();
		glc.initialize(view);
		return glc.getColumn(model, UNLIMITED);
	}
	private String buildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field,StringBuilder upper,StringBuilder under) throws Exception{
		if(isHidden(field, model)) return "";
		StringBuilder sb = new StringBuilder();

		String beforeCell = beforeBuildCell(session, model, view, field);
		String afterCell = afterBuildCell(session, model, view, field);
		String upperCell = upperBuildCell(session, model, view, field);
		String underCell = underBuildCell(session, model, view, field);
		sb.append(beforeCell);
		if(null!=field){
			GridLayoutConfig glc = new GridLayoutConfig();
			glc.initialize(view);
			String hostId = glc.getHostId();
			int padding = glc.getPadding(model,GridLayoutConfig.DEFAULT_PADDING);
			int colspan = field.getInt(GridLayoutConfig.PROPERTITY_COLSPAN,1);
			int rowspan = field.getInt(GridLayoutConfig.PROPERTITY_ROWSPAN,1);
			sb.append("<td class='");
			IViewBuilder builder = session.getPresentationManager().getViewBuilder(field);
			if(builder instanceof GridLayout){
				String labelPosition = view.getString(BoxConfig.PROPERTITY_LABEL_POSITION,"");
				String labelAlign = view.getString(BoxConfig.PROPERTITY_LABEL_ALIGN,"");
				if(!"".equals(labelPosition) && "".equals(field.getString(BoxConfig.PROPERTITY_LABEL_POSITION, "")))
					field.put(BoxConfig.PROPERTITY_LABEL_POSITION, labelPosition);
				if(!"".equals(labelAlign) && "".equals(field.getString(BoxConfig.PROPERTITY_LABEL_ALIGN, "")))
					field.put(BoxConfig.PROPERTITY_LABEL_ALIGN, labelAlign);
				sb.append(DEFAULT_TD_CONTAINER);
			} else{
				sb.append(DEFAULT_TD_CELL);
			}
			if(glc.isWrapperAdjust()){
				String width = field.getString(ComponentConfig.PROPERTITY_WIDTH);
	//			if(null == width){
	//				try{
	//					Class kls = (Class) session.getCurrentPackage().getClassRegistry().getFeatures(field).get(0);
	//					width = Component.class.getDeclaredMethod("getDefaultWidth").invoke(kls.newInstance()).toString();
	//				}catch (Exception e) {
	//				}
	//			}
				if(null!=width 
	//					&& Integer.parseInt(width)>0
				){
					sb.append("' width='"+width);
				}
			}
			if(colspan > 1){
				int n = 1,m=0;
				if(!"".equals(beforeCell)){
					n++;
					m=1;
				}
				if(!"".equals(afterCell)){
					n++;
					m=1;
				}
				sb.append("' colspan='"+(colspan*n-m));
			}
			if(rowspan > 1){
				int n = 1,m=0;
				if(!"".equals(upperCell)){
					n++;
					m=1;
				}
				if(!"".equals(underCell)){
					n++;
					m=1;
				}
				sb.append("' rowspan='"+(rowspan*n-m));
			}
			sb.append("' style='padding:"+padding+"px");
	//		if(GridBoxConfig.TAG_NAME.equals(field.getName())){
	//			GridBoxConfig gbc = GridBoxConfig.getInstance(field);
	////			boolean isField = gbc.getIsField();
	////			if(isField){
	////				String gridboxid = TextParser.parse(gbc.getId(),model);
	////				if(null == gridboxid || "".equals(gridboxid)){
	////					gridboxid = IDGenerator.getInstance().generate();
	////				}
	////				field.putString(ComponentConfig.PROPERTITY_ID, gridboxid);
	////				field.putInt(GridBoxConfig.PROPERTITY_PADDING, padding);
	//////				out.write("' id='"+gridboxid);
	////			}
	//		}
			sb.append("'>");
			if(null != hostId){
				transferHostId(field,hostId);
			}
			sb.append(session.buildViewAsString(model, field));
			if(builder instanceof GridLayout){}else{			
//			addInvalidMsg(field, out);
			}
		}else{
			sb.append("<td class='"+DEFAULT_TD_CELL+"'>");
		}
		sb.append("</td>");	
		sb.append(afterCell);
		upper.append(upperCell);
		under.append(underCell);
		return sb.toString();
	}
		
	protected String underBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		return "";
	}
	protected String upperBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		return "";
	}
	protected String beforeBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		return "";
	}
	
	protected String afterBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		return "";
	}
	
	protected void buildHead(BuildSession session, CompositeMap model,CompositeMap view, int rows ,int columns) throws Exception{
	}
	
	protected void buildFoot(BuildSession session, CompositeMap model,CompositeMap view,int columns) throws Exception{
	}
	
	protected void beforeBuildTop(BuildSession session, CompositeMap model,CompositeMap view,String id) throws Exception{	
	}
	
	protected void afterBuildTop(BuildSession session, CompositeMap model,CompositeMap view ,int columns) throws Exception{
	}
	
	protected void afterBuildBottom(BuildSession session, CompositeMap model,CompositeMap view ,int columns) throws Exception{
	}
	
	protected String getClassName(BuildSession session, CompositeMap model,CompositeMap view ) throws Exception{
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String className = DEFAULT_TABLE_CLASS;
		className += " " + cls; 
		return className;
	}
	
	protected String getStyle(BuildSession session, CompositeMap model,CompositeMap view ) throws Exception{
		return view.getString(ComponentConfig.PROPERTITY_STYLE, "");
	}
	
	private String buildRows(BuildSession session, CompositeMap model, CompositeMap view, Iterator it) throws Exception{
		StringBuilder sb = new StringBuilder();
		while(it.hasNext()){
			StringBuilder upper = new StringBuilder();
			StringBuilder cell = new StringBuilder();
			StringBuilder under = new StringBuilder();
			CompositeMap field = (CompositeMap)it.next();
			field.putInt(GridLayoutConfig.PROPERTITY_ROWSPAN, 1);
			field.putInt(GridLayoutConfig.PROPERTITY_COLSPAN, 1);
			cell.append(buildCell(session,model,view,field,upper,under));
			if(upper.length()!=0){
				sb.append("<tr>");
				sb.append(upper);	
				sb.append("</tr>");
			}
			sb.append("<tr>");
			sb.append(cell);	
			sb.append("</tr>");
			if(under.length()!=0){
				sb.append("<tr>");
				sb.append(under);	
				sb.append("</tr>");
			}
		}
		return sb.toString();
	}
	
	
	private String buildColumns(BuildSession session, CompositeMap model, CompositeMap view, Iterator it) throws Exception{
		StringBuilder sb = new StringBuilder();
		StringBuilder upper = new StringBuilder();
		StringBuilder cell = new StringBuilder();
		StringBuilder under = new StringBuilder();
		while(it.hasNext()){
			CompositeMap field = (CompositeMap)it.next();
			field.putInt(GridLayoutConfig.PROPERTITY_ROWSPAN, 1);
			field.putInt(GridLayoutConfig.PROPERTITY_COLSPAN, 1);
			cell.append(buildCell(session,model,view,field,upper,under));	
		}
		if(upper.length()!=0){
			sb.append("<tr>");
			sb.append(upper);
			sb.append("</tr>");
		}
		sb.append("<tr>");
		sb.append(cell);
		sb.append("</tr>");
		if(under.length()!=0){
			sb.append("<tr>");
			sb.append(under);
			sb.append("</tr>");
		}
		return sb.toString();
	}
	

	
	protected void buildTop(BuildSession session, CompositeMap model,CompositeMap view, Map map, int rows, int columns,String id) throws Exception{
		beforeBuildTop(session,model,view,id);
		GridLayoutConfig glc = new GridLayoutConfig();
		glc.initialize(view);
		Writer out = session.getWriter();
		int cellspacing = glc.getCellSpacing(model);
		int cellpadding = glc.getCellPadding(model);
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, false);		
		
//		String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, "0");
//		String wstr = uncertain.composite.TextParser.parse(widthStr, model);
//		int width = Integer.valueOf(wstr).intValue();
//		String heightStr = view.getString(ComponentConfig.PROPERTITY_HEIGHT, "0");
//		String hstr = uncertain.composite.TextParser.parse(heightStr, model);
//		int height = Integer.valueOf(hstr).intValue();
		
		int width = getComponentWidth(model, view, map).intValue();
		int height = getComponentHeight(model, view, map).intValue();
		
		String className = getClassName(session,model,view);
		String style = getStyle(session,model,view);
		
		if(showBorder) {
			cellspacing = 1;
			className += " layout-border";
		}
		out.write("<table border=0 class='"+className+"' id='"+id+"'");
		
		if(width != 0) out.write(" width=" + width);
		if(height != 0) out.write(" height=" + height);
		if(!"".equals(style)) {
			out.write(" style='"+style+"'");
		}
		
		out.write(" cellpadding="+cellpadding+" cellspacing="+cellspacing+">");
		buildHead(session,model,view, rows, columns);
		afterBuildTop(session,model,view,columns);
	}
	
	protected void buildBottom(BuildSession session, CompositeMap model,CompositeMap view,int columns) throws Exception{
		buildFoot(session,model,view,columns);
		Writer out = session.getWriter();
		out.write("</tbody>");
		out.write("</table>");
		afterBuildBottom(session,model,view,columns);
	}
	
	
	
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Map map = view_context.getMap();
		
		/** ID属性 **/
		String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if("".equals(id)) {
			id= IDGenerator.getInstance().generate();
		}
		
		Writer out = session.getWriter();
		Iterator it = view.getChildIterator();
		int rows = getRows(view,model);//glc.getRow(model,UNLIMITED);
		int columns = getColumns(view,model);//glc.getColumn(model,UNLIMITED);
		int cl = getChildLength(view,model);
		if(rows == 0 || columns == 0){
			
		}else if(rows == UNLIMITED && columns == UNLIMITED) {
			rows = UNLIMITED;
			columns = 1;
		}else if(rows == UNLIMITED && columns != UNLIMITED) {
			List children = view.getChilds();
			if(children!=null){
				rows = (int)Math.ceil((double)cl/columns);
			}else{
				rows = 1;				
			}
		} else if(rows != UNLIMITED && columns == UNLIMITED) {
			List children = view.getChilds();
			if(children!=null){
				columns = (int)Math.ceil((double)cl/rows);
			}else{
				columns = 1;				
			}
		}
		try {
			buildTop(session, model, view, map, rows, columns,id);
			if (it != null && rows!=0 && columns!=0) {
				StringBuilder sb = new StringBuilder();
				if(rows == UNLIMITED){
					sb.append(buildRows(session, model, view, it));
				}else if(columns == UNLIMITED){
					sb.append(buildColumns(session, model, view, it));
				}else{
					int[] rowspans = new int[columns];
					for(int i=0;i<columns;i++){
						rowspans[i]=0;
					}
					for( int n=0; n<rows; n++){
						StringBuilder upper = new StringBuilder();
						StringBuilder cell = new StringBuilder();
						StringBuilder under = new StringBuilder();
						int k=0;
						for(int j=0;j<columns;j++){
							if(rowspans[j]>0){
								k++;
								rowspans[j]--;
							}
						}
						for( ; k<columns; k++){
							if(it.hasNext()){
								CompositeMap field = (CompositeMap)it.next();
								if(isHidden(field, model)){
									k--;
									cl--;
								}else{
									int colspan = field.getInt(GridLayoutConfig.PROPERTITY_COLSPAN, 1);
									int rowspan = field.getInt(GridLayoutConfig.PROPERTITY_ROWSPAN, 1);
									boolean isField = false;
									if(GridBoxConfig.TAG_NAME.equals(field.getName())){
										GridBoxConfig gbc = GridBoxConfig.getInstance(field);
										isField = gbc.getIsField();
										colspan = gbc.getColumn()+1;
										field.putInt(GridLayoutConfig.PROPERTITY_COLSPAN, colspan);
									}
									if(rowspan > 1 || colspan > 1){
										if(colspan > 1){
											if(k + colspan > columns){
												colspan = columns - k;
												field.putInt(GridLayoutConfig.PROPERTITY_COLSPAN, colspan);
												if(isField)
													field.putInt(GridBoxConfig.PROPERTITY_COLUMN, colspan);
											}
										}
										for(int cn=0;cn<colspan;cn++){
											rowspans[k+cn] += rowspan-1;
										}
										k += colspan-1;
										cl += rowspan * colspan -1;
									}
									
								}
								rows = (int)Math.ceil((double)cl/columns);
								cell.append(buildCell(session,model,view,field,upper,under));	
							}else{
								cell.append(buildCell(session,model,view,null,upper,under));	
							}
						}
						if(upper.length()!=0){
							sb.append("<tr>");
							sb.append(upper);
							sb.append("</tr>");
						}
						sb.append("<tr>");
						sb.append(cell);
						sb.append("</tr>");
						if(under.length()!=0){
							sb.append("<tr>");
							sb.append(under);
							sb.append("</tr>");
						}
					}
				}
				out.append(sb);
			}
			buildBottom(session, model, view,columns);
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
//		addBoxScript(id, session, view);
	}
	
//	private void addInvalidMsg(CompositeMap field, Writer out) throws IOException {
//		String id = field.getString(Component.PROPERTITY_ID);
//		String align = field.getString(PROPERTITY_VALIDALIGN, "");
//		if("bottom".equals(align)){
//			out.write("<div class='item-clear'></div>");	
//			out.write("<span class='item-invalid-msg-bottom' id='"+ id +"_vmsg'></span>");		
//		}else{
//			out.write("<span class='item-invalid-msg-right' id='"+ id +"_vmsg'></span>");	
//		}
//	}
	
	
//	private void addBoxScript(String id, BuildSession session, CompositeMap view) throws IOException {
//		List cmps = new ArrayList();
//		Iterator cit = view.getChildIterator();
//		if(cit != null){
//			while(cit.hasNext()){
//				CompositeMap field = (CompositeMap)cit.next();
//				IViewBuilder builder = session.getPresentationManager().getViewBuilder(field);
//				if(builder instanceof GridLayout){}else{
//					String cid = field.getString(Component.PROPERTITY_ID);
//					cmps.add(cid);
//				}
//			}			
//		}
//		Writer out = session.getWriter();
//		out.write("<script>");
//		StringBuilder sb = new StringBuilder();
//		sb.append("new Aurora.Box({id:'").append(id).append("',");
//		sb.append("cmps:[");
//		Iterator it = cmps.iterator();
//		while(it.hasNext()){
//			sb.append("'").append(it.next()).append("'");
//			if(it.hasNext())
//			sb.append(",");
//		}
//		sb.append("]});");
//		out.write(sb.toString());
//		out.write("</script>");
//	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
