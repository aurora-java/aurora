package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.BoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.GridLayouConfig;

/**
 * GridLayout.
 * 
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class GridLayout extends Component implements IViewBuilder{
	
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
		GridLayouConfig glc = new GridLayouConfig();
		glc.initialize(view);
		return glc.getRow(model, UNLIMITED);
	}
	
	protected int getColumns(CompositeMap view,CompositeMap model){
		GridLayouConfig glc = new GridLayouConfig();
		glc.initialize(view);
		return glc.getColumn(model, UNLIMITED);
	}
	private void buildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		if(isHidden(field, model)) return;
		GridLayouConfig glc = new GridLayouConfig();
		glc.initialize(view);
		Writer out = session.getWriter();
		int padding = glc.getPadding(model,3);
		int colspan = field.getInt(GridLayouConfig.PROPERTITY_COLSPAN,1);
		int rowspan = field.getInt(GridLayouConfig.PROPERTITY_ROWSPAN,1);
		IViewBuilder builder = session.getPresentationManager().getViewBuilder(field);
		beforeBuildCell(session, model, view, field);
		out.write("<td class='");
		if(builder instanceof GridLayout){
			out.write(DEFAULT_TD_CONTAINER);
		} else{
			out.write(DEFAULT_TD_CELL);
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
				out.write("' width='"+width);
			}
		}
		if(colspan > 1)out.write("' colspan='"+(colspan*2-1));
		if(rowspan > 1)out.write("' rowspan='"+rowspan);
		out.write("' style='padding:"+padding+"px'>");
		session.buildView(model, field);
		if(builder instanceof GridLayout){}else{			
//			addInvalidMsg(field, out);
		}
		out.write("</td>");	
	}
		
	
	protected void beforeBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
	}
	
	protected void afterBuildCell(BuildSession session, CompositeMap model, CompositeMap field) throws Exception{
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
	
	private void buildRows(BuildSession session, CompositeMap model, CompositeMap view, Iterator it) throws Exception{
		Writer out = session.getWriter();
		while(it.hasNext()){
			out.write("<tr>");
			CompositeMap field = (CompositeMap)it.next();
			field.putInt(GridLayouConfig.PROPERTITY_ROWSPAN, 1);
			field.putInt(GridLayouConfig.PROPERTITY_COLSPAN, 1);
			buildCell(session,model,view,field);	
			out.write("<td width='100%'></td></tr>");
		}
	}
	
	
	private void buildColumns(BuildSession session, CompositeMap model, CompositeMap view, Iterator it) throws Exception{
		Writer out = session.getWriter();
		out.write("<tr>");
		while(it.hasNext()){
			CompositeMap field = (CompositeMap)it.next();
			field.putInt(GridLayouConfig.PROPERTITY_ROWSPAN, 1);
			field.putInt(GridLayouConfig.PROPERTITY_COLSPAN, 1);
			buildCell(session,model,view,field);		
		}
		out.write("<td width='100%'></td></tr>");
	}
	

	
	protected void buildTop(BuildSession session, CompositeMap model,CompositeMap view, Map map, int rows, int columns,String id) throws Exception{
		beforeBuildTop(session,model,view,id);
		GridLayouConfig glc = new GridLayouConfig();
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
		if(rows == UNLIMITED && columns == UNLIMITED) {
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
			if (it != null) {
				if(rows == UNLIMITED){
					buildRows(session, model, view, it);
				}else if(columns == UNLIMITED){
					buildColumns(session, model, view, it);
				}else{
					int[] rowspans = new int[columns];
					for(int i=0;i<columns;i++){
						rowspans[i]=0;
					}
					for( int n=0; n<rows; n++){
						out.write("<tr>");
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
									int colspan = field.getInt(GridLayouConfig.PROPERTITY_COLSPAN, 1);
									int rowspan = field.getInt(GridLayouConfig.PROPERTITY_ROWSPAN, 1);
									if(rowspan > 1){
										cl += rowspan - 1;
									}
									rowspans[k] += rowspan-1;
									if(colspan > 1){
										if(k + colspan > columns){
											colspan = columns - k;
											field.putInt(GridLayouConfig.PROPERTITY_COLSPAN, colspan);
										}
										k += colspan - 1;
										cl += colspan - 1;
									}
									
								}
								rows = (int)Math.ceil((double)cl/columns);
								buildCell(session,model,view, field);
							}else{
								out.write("<th class='layout-th'></th><td class='layout-td-cell'></td>");
//								break;
							}
						}
						out.write("<td width='100%'></td></tr>");
					}
				}
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
//		StringBuffer sb = new StringBuffer();
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
