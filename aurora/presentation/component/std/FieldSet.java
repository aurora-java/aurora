package aurora.presentation.component.std;

import java.io.Writer;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.component.std.config.ComponentConfig;

public class FieldSet extends Box {
	
	protected static final String PROPERTITY_TITLE="title";
	private static final String DEFAULT_BODY_CLASS = "form_body";
	
	protected void buildHead(BuildSession session, CompositeMap model,CompositeMap view, int rows ,int columns) throws Exception{
		Writer out = session.getWriter();
		String title = view.getString(PROPERTITY_TITLE, "");
		out.write("<LEGEND class='field_head' unselectable='on'><SPAN>");
		out.write(title);
		out.write("</SPAN></LEGEND>");
		
	}
	
	protected void afterBuildTop(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
		Writer out = session.getWriter();
		out.write("<tbody class='"+DEFAULT_BODY_CLASS+"'>");
		super.afterBuildTop(session, model, view);
	}
	
	protected void buildTop(BuildSession session, CompositeMap model,CompositeMap view,Map map,int rows, int columns,String id) throws Exception{
		
		Writer out = session.getWriter();
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		
		String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, "0");
		String wstr = uncertain.composite.TextParser.parse(widthStr, model);
		int width = Integer.valueOf(wstr).intValue();
		String heightStr = view.getString(ComponentConfig.PROPERTITY_HEIGHT, "0");
		String hstr = uncertain.composite.TextParser.parse(heightStr, model);
		int height = Integer.valueOf(hstr).intValue();
		
		int cellspacing = view.getInt(PROPERTITY_CELLSPACING, 0);
		int cellpadding = view.getInt(PROPERTITY_CELLPADDING, 0);
		
		String className = DEFAULT_TABLE_CLASS;
		String title = view.getString(PROPERTITY_TITLE, "");
		if(!"".equals(title)) className += " " + TITLE_CLASS;
		className += " " + cls;
		
		out.write("<FIELDSET class='item-fieldset' id='"+id+"'");
		StringBuffer sb = new StringBuffer();
		if(width != 0) sb.append("width:" + (width-22) + "px;");
		if(height != 0) sb.append("height:" + height + "px;");
		if(!"".equals(style)) {
			sb.append(style);
			out.write(" style='"+sb.toString()+"'");
		}
		out.write(">");
		buildHead(session,model,view, rows, columns);
		out.write("<table border=0");
		out.write(" cellpadding="+cellpadding+" cellspacing="+cellspacing+">");
		afterBuildTop(session,model,view);
	}
	
	protected void buildBottom(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
		buildFoot(session,model,view);
		Writer out = session.getWriter();
		out.write("</tbody>");
		out.write("</table>");
		out.write("</FIELDSET>");	
	}
}
