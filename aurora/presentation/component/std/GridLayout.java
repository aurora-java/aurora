package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.markup.HtmlPageContext;

/**
 * GridLayout.
 * 
 * @version $Id: GridLayout.java v 1.0 2009-7-29 上午10:26:52 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class GridLayout extends Component implements IViewBuilder, ISingleton {
	
	protected static final String ROWS = "row";
	protected static final String COLUMNS = "column";
	protected static final int UNLIMITED = -1;
	
	private static final String PROPERTITY_CLASS="className";
	private static final String PROPERTITY_STYLE="style";
	private static final String PROPERTITY_CELLPADDING = "cellPadding";
	private static final String PROPERTITY_CELLSPACING = "cellSpacing";
	
	private static final String DEFAULT_TABLE_CLASS = "layout-table";
	private static final String DEFAULT_TD_CELL = "layout-td-cell";
	private static final String DEFAULT_TD_CONTAINER = "layout-td-con";
		
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);		
		addJavaScript(session, context, "core/Box.js");
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
		Writer out = session.getWriter();
		IViewBuilder builder = session.getPresentationManager().getViewBuilder(field);
		if(builder instanceof GridLayout){
			beforeBuildCell(session, model, view, field);
			out.write("<td class='"+DEFAULT_TD_CONTAINER+"'>");
		} else{
			beforeBuildCell(session, model, view, field);
			out.write("<td class='"+DEFAULT_TD_CELL +"'>");
		}
		session.buildView(model, field);
		if(builder instanceof GridLayout){}else{
			String cid = field.getString(Component.PROPERTITY_ID);
			addInvalidMsg(cid, out);
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
	

	
	private void buildTop(BuildSession session, CompositeMap model,CompositeMap view, int rows, int columns) throws Exception{
		
		Writer out = session.getWriter();
		String cls = view.getString(PROPERTITY_CLASS, "");
		String style = view.getString(PROPERTITY_STYLE, "");
		int cellspacing = view.getInt(PROPERTITY_CELLSPACING, 0);
		int cellpadding = view.getInt(PROPERTITY_CELLPADDING, 0);
		int width = view.getInt(PROPERTITY_WIDTH, 0);
		
		String className = DEFAULT_TABLE_CLASS;
		if(!"".equals(className)){
			className += " " + cls;			
		}
		out.write("<table border=0 class='"+className+"'");
		if(width != 0) out.write(" width=" + width);
		if(!"".equals(style)) {
			out.write(" style='"+style+"'");
		}
		out.write(" cellpadding="+cellpadding+" cellspacing="+cellspacing+">");
		buildHead(session,model,view, rows, columns);
		out.write("<tbody>");
		afterBuildTop(session,model,view);
	}
	
	private void buildBottom(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
		Writer out = session.getWriter();
		buildFoot(session,model,view);
		out.write("</tbody>");
		out.write("</table>");	
	}
	
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Map map = view_context.getMap();
		
		String id = view.getString(PROPERTITY_ID, "");
		if("".equals(id)) {
			int idIndex = ((Integer)session.getSessionContext().get(ID_INDEX)).intValue();
			id= "aid_"+(idIndex++);
			session.getSessionContext().put(ID_INDEX, new Integer(idIndex));
		}
		
		Writer out = session.getWriter();
		Iterator it = view.getChildIterator();
		
		int rows = getRows(view);
		int columns = getColumns(view);
		if(rows == UNLIMITED && columns == UNLIMITED) {
			rows = UNLIMITED;
			columns = 1;
		}else if(rows == UNLIMITED && columns != UNLIMITED) {
			rows = (int)Math.ceil((double)view.getChilds().size()/columns);
		} else if(rows != UNLIMITED && columns == UNLIMITED) {
			columns = (int)Math.ceil((double)view.getChilds().size()/rows);
		}
		if (it != null) {
			try {
				buildTop(session, model, view ,rows, columns);
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
								buildCell(session,model,view, field);	
							}else{
								break;
							}
						}
						out.write("</tr>");
					}
				}
				buildBottom(session, model, view);
			} catch (Exception e) {
				throw new ViewCreationException(e);
			}
		}
		addBoxScript(id, session, view);
	}
	
	private void addInvalidMsg(String id, Writer out) throws IOException {
		//<div class='item-clear'></div>
		out.write("<div class='item-clear'></div><span class='item-invalid-msg' id='"+ id +"_vmsg'></span>");		
	}
	
	private void addBoxScript(String id, BuildSession session, CompositeMap view) throws IOException {
		List cmps = new ArrayList();
		Iterator cit = view.getChildIterator();
		if(cit != null){
			while(cit.hasNext()){
				CompositeMap field = (CompositeMap)cit.next();
				IViewBuilder builder = session.getPresentationManager().getViewBuilder(field);
				if(builder instanceof GridLayout){}else{
					String cid = field.getString(Component.PROPERTITY_ID);
					cmps.add(cid);
				}
			}			
		}
		Writer out = session.getWriter();
		out.write("<script>");
		StringBuffer sb = new StringBuffer();
		sb.append("var ").append(id).append("=");
		sb.append("new Aurora.Box({id:'").append(id).append("',");
		sb.append("cmps:[");
		Iterator it = cmps.iterator();
		while(it.hasNext()){
			sb.append("'").append(it.next()).append("'");
			if(it.hasNext())
			sb.append(",");
		}
		sb.append("]});");
		out.write(sb.toString());
		out.write("</script>");
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
