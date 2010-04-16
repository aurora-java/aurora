package aurora.presentation.component.std;

import java.io.Writer;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FormConfig;

public class Form extends Box {
	private static final String DEFAULT_HEAD_CLASS = "form_head";
	private static final String DEFAULT_BODY_CLASS = "form_body";
	
	protected void buildHead(BuildSession session, CompositeMap model,CompositeMap view, int rows ,int columns) throws Exception{
		Writer out = session.getWriter();
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		if(!"".equals(title)) {
			out.write("<thead><tr><th class='"+DEFAULT_HEAD_CLASS+"' colspan="+columns*2+">");
			out.write(title);
			out.write("</th></tr></thead>");
		}
	}
	protected void afterBuildTop(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
		Writer out = session.getWriter();
		out.write("<tbody class='"+DEFAULT_BODY_CLASS+"'>");
		
		String showmargin = view.getString(FormConfig.PROPERTITY_SHOWMARGIN, "true");
		if("true".equals(showmargin))out.write("<tr height='3'></tr>");
		super.afterBuildTop(session, model, view);
	}
	
	protected void buildTop(BuildSession session, CompositeMap model,CompositeMap view,int rows, int columns,String id) throws Exception{
		
		Writer out = session.getWriter();
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		int cellspacing = view.getInt(PROPERTITY_CELLSPACING, 0);
		int cellpadding = view.getInt(PROPERTITY_CELLPADDING, 0);
		
		String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, "0");
		String wstr = uncertain.composite.TextParser.parse(widthStr, model);
		int width = Integer.valueOf(wstr).intValue();
		String heightStr = view.getString(ComponentConfig.PROPERTITY_HEIGHT, "0");
		String hstr = uncertain.composite.TextParser.parse(heightStr, model);
		int height = Integer.valueOf(hstr).intValue();
		
		String className = DEFAULT_TABLE_CLASS;
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		if(!"".equals(title)) className += " " + TITLE_CLASS;
		className += " " + cls;
		
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
	
	protected void buildFoot(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
		super.afterBuildTop(session, model, view);
		Writer out = session.getWriter();
		String showmargin = view.getString(FormConfig.PROPERTITY_SHOWMARGIN, "true");
		if("true".equals(showmargin))out.write("<tr height='3'></tr>");
	}

}
