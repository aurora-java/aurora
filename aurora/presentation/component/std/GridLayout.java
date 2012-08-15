package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.BoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;

/**
 * GridLayout.
 * 
 * @version $Id: GridLayout.java v 1.0 2009-7-29 上午10:26:52 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class GridLayout extends Component implements IViewBuilder, ISingleton {
	
	public static final String ROWS = "row";
	public static final String COLUMNS = "column";
	
	protected static final int UNLIMITED = -1;
	protected static final String PROPERTITY_CELLPADDING = "cellpadding";
	protected static final String PROPERTITY_CELLSPACING = "cellspacing";
	protected static final String PROPERTITY_VALIDALIGN = "validalign";
	protected static final String PROPERTITY_PADDING = "padding";
	
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
		
	protected int getRows(CompositeMap view){
		int rows = view.getInt(ROWS, UNLIMITED);
		return rows;
	}
	
	protected int getColumns(CompositeMap view){
		int columns = view.getInt(COLUMNS, UNLIMITED);
		return columns;
	}
	
	private void buildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		if(isHidden(field, model)) return;
		Writer out = session.getWriter();
		int padding = view.getInt(PROPERTITY_PADDING, 3);
		IViewBuilder builder = session.getPresentationManager().getViewBuilder(field);
		if(builder instanceof GridLayout){
			beforeBuildCell(session, model, view, field);
			out.write("<td class='"+DEFAULT_TD_CONTAINER+"' style='padding:"+padding+"px'>");
		} else{
			beforeBuildCell(session, model, view, field);
			out.write("<td class='"+DEFAULT_TD_CELL +"' style='padding:"+padding+"px'>");
		}
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
	
	protected void buildFoot(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
	}
	
	protected void afterBuildTop(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
	}
	
	
	private void buildRows(BuildSession session, CompositeMap model, CompositeMap view, Iterator it) throws Exception{
		Writer out = session.getWriter();
		while(it.hasNext()){
			out.write("<tr>");
			CompositeMap field = (CompositeMap)it.next();
			buildCell(session,model,view,field);	
			out.write("</tr>");
		}
	}
	
	
	private void buildColumns(BuildSession session, CompositeMap model, CompositeMap view, Iterator it) throws Exception{
		Writer out = session.getWriter();
		out.write("<tr>");
		while(it.hasNext()){
			CompositeMap field = (CompositeMap)it.next();
			buildCell(session,model,view,field);		
		}
		out.write("</tr>");
	}
	

	
	protected void buildTop(BuildSession session, CompositeMap model,CompositeMap view, Map map, int rows, int columns,String id) throws Exception{
		Writer out = session.getWriter();
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		int cellspacing = view.getInt(PROPERTITY_CELLSPACING, 0);
		int cellpadding = view.getInt(PROPERTITY_CELLPADDING, 0);
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, false);
		
		
//		String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, "0");
//		String wstr = uncertain.composite.TextParser.parse(widthStr, model);
//		int width = Integer.valueOf(wstr).intValue();
//		String heightStr = view.getString(ComponentConfig.PROPERTITY_HEIGHT, "0");
//		String hstr = uncertain.composite.TextParser.parse(heightStr, model);
//		int height = Integer.valueOf(hstr).intValue();
		
		int width = getComponentWidth(model, view, map).intValue();
		int height = getComponentHeight(model, view, map).intValue();
		
		String className = DEFAULT_TABLE_CLASS;
		className += " " + cls;
		
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
		afterBuildTop(session,model,view);
	}
	
	protected void buildBottom(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
		buildFoot(session,model,view);
		Writer out = session.getWriter();
		out.write("</tbody>");
		out.write("</table>");	
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
		
		int rows = getRows(view);
		int columns = getColumns(view);
		if(rows == UNLIMITED && columns == UNLIMITED) {
			rows = UNLIMITED;
			columns = 1;
		}else if(rows == UNLIMITED && columns != UNLIMITED) {
			List children = view.getChilds();
			if(children!=null){
				int cl = getChildLength(view,model);
				rows = (int)Math.ceil((double)cl/columns);
			}else{
				rows = 1;				
			}
		} else if(rows != UNLIMITED && columns == UNLIMITED) {
			List children = view.getChilds();
			if(children!=null){
				int cl = getChildLength(view,model);
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
					for( int n=0; n<rows; n++){
						out.write("<tr>");
						for( int k=0; k<columns; k++){
							if(it.hasNext()){
								CompositeMap field = (CompositeMap)it.next();
								if(isHidden(field, model))k--;
								buildCell(session,model,view, field);
							}else{
								out.write("<th class='layout-th'></th><td class='layout-td-cell'></td>");
//								break;
							}
						}
						out.write("</tr>");
					}
				}
			}
			buildBottom(session, model, view);
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
