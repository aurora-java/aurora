package aurora.presentation.component.std;

import java.io.Writer;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FieldSetConfig;
import aurora.presentation.component.std.config.FormConfig;

public class FieldSet extends Box {
	
	public static final String VERSION = "$Revision$";
	
	public FieldSet(IObjectRegistry registry) {
			super(registry);
	}
	
	private static final String DEFAULT_BODY_CLASS = "form_body";
	
	protected void buildHead(BuildSession session, CompositeMap model,CompositeMap view, int rows ,int columns) throws Exception{
		Writer out = session.getWriter();
		FieldSetConfig fsc = FieldSetConfig.getInstance(view);
		String title = fsc.getTitle();
		title = session.getLocalizedPrompt(title);
		out.write("<LEGEND class='field_head' unselectable='on'><SPAN>");
		out.write(title);
		out.write("</SPAN></LEGEND>");
		
	}
	
	protected void afterBuildTop(BuildSession session, CompositeMap model,CompositeMap view,int columns) throws Exception{
		Writer out = session.getWriter();
		out.write("<tbody class='"+DEFAULT_BODY_CLASS+"'>");
		super.afterBuildTop(session, model, view,columns);
	}
	
	protected void buildTop(BuildSession session, CompositeMap model,CompositeMap view,Map map,int rows, int columns,String id) throws Exception{
		FieldSetConfig fsc = FieldSetConfig.getInstance(view);
		Writer out = session.getWriter();
		String cls = fsc.getClassName("");
		String style = fsc.getStyle("");
		
//		String widthStr = String.valueOf(fsc.getWidth(0));//view.getString(ComponentConfig.PROPERTITY_WIDTH, "0");
//		String wstr = uncertain.composite.TextParser.parse(widthStr, model);
//		int width = Integer.valueOf(wstr).intValue();
//		String heightStr = String.valueOf(fsc.getHeight(0));//view.getString(ComponentConfig.PROPERTITY_HEIGHT, "0");
//		String hstr = uncertain.composite.TextParser.parse(heightStr, model);
//		int height = Integer.valueOf(hstr).intValue();
		int width = getComponentWidth(model, view, map).intValue();
		int height = getComponentHeight(model, view, map).intValue();
		
		int cellspacing = fsc.getCellSpacing(model);//view.getInt(PROPERTITY_CELLSPACING, 0);
		int cellpadding = fsc.getCellPadding(model);//view.getInt(PROPERTITY_CELLPADDING, 0);
		
		String className = DEFAULT_TABLE_CLASS;
		String title = fsc.getTitle();
		if(!"".equals(title)) className += " " + TITLE_CLASS;
		className += " " + cls;
		
		out.write("<FIELDSET class='item-fieldset' id='"+id+"'");
		StringBuilder sb = new StringBuilder();
		if(!"".equals(style)) {
			sb.append(style);
		}
		if(width != 0) sb.append("width:" + (width-2) + "px;");
		if(height != 0) sb.append("height:" + height + "px;");
		if(sb.length() !=0)
		out.write(" style='"+sb.toString()+"'");
		
		out.write(">");
		buildHead(session,model,view, rows, columns);
		out.write("<table width='100%' border=0");
		out.write(" cellpadding="+cellpadding+" cellspacing="+cellspacing+">");
		afterBuildTop(session,model,view,columns);
	}
	
	protected void buildBottom(BuildSession session, CompositeMap model,CompositeMap view,int columns) throws Exception{
		buildFoot(session,model,view,columns);
		Writer out = session.getWriter();
		String showmargin = view.getString(FormConfig.PROPERTITY_SHOWMARGIN, "true");
		if("true".equals(showmargin))out.write("<tr height='5'><td colspan="+columns*2+"></td></tr>");
		out.write("</tbody>");
		out.write("</table>");
		out.write("</FIELDSET>");	
	}
}
