package aurora.presentation.component.std;

import java.io.Writer;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.component.std.config.BoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FormConfig;
import aurora.presentation.component.std.config.GridLayouConfig;

public class Form extends Box {
	
	public static final String VERSION = "$Revision$";
	
	
	private static final String DEFAULT_HEAD_CLASS = "form_head";
	private static final String DEFAULT_BODY_CLASS = "form_body";
	
	protected void buildHead(BuildSession session, CompositeMap model,CompositeMap view, int rows ,int columns) throws Exception{
		String theme = session.getTheme();
		if(THEME_MAC.equals(theme)){
			return;
		}
		Writer out = session.getWriter();
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		title = uncertain.composite.TextParser.parse(session.getLocalizedPrompt(title),model);
		if(!"".equals(title)) {
			out.write("<thead><tr><th class='"+DEFAULT_HEAD_CLASS+"' colspan="+columns*2+">");
			out.write(title);
			out.write("</th></tr></thead>");
		}
	}
	
	
	protected void afterBuildTop(BuildSession session, CompositeMap model,CompositeMap view,int columns) throws Exception{
		Writer out = session.getWriter();
		out.write("<tbody class='"+DEFAULT_BODY_CLASS+"'>");
		
		String showmargin = view.getString(FormConfig.PROPERTITY_SHOWMARGIN, "true");
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, false);
		if("true".equals(showmargin) && !showBorder)out.write("<tr height='5'><td colspan="+columns*2+"></td></tr>");
		super.afterBuildTop(session, model, view,columns);
	}
	
	protected String getClassName(BuildSession session, CompositeMap model,CompositeMap view ) throws Exception{
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, false);
		String className = DEFAULT_TABLE_CLASS + " layout-form";
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		if(!"".equals(title)) className += " " + TITLE_CLASS;
		className += " " + cls;
		if(showBorder) {
			className += " layout-border";
		}
		return className;
	}
	
	protected String getStyle(BuildSession session, CompositeMap model,CompositeMap view ) throws Exception{
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, false);
		if(showBorder) {
			style += " border:none;";
		}
		return style;
	}
	
	protected void beforeBuildTop(BuildSession session, CompositeMap model,CompositeMap view,String id ) throws Exception{		
		String theme = session.getTheme();
		if(THEME_MAC.equals(theme)){
			String style = getStyle(session,model,view);
			Writer out = session.getWriter();
			String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
			title = uncertain.composite.TextParser.parse(session.getLocalizedPrompt(title),model);
			if(!"".equals(title)) {
				out.write("<table border='0' class='mac_form' cellpadding='0' cellSpacing='0' id='"+id+"'");
				if(!"".equals(style)) {
					out.write(" style='"+style+"'");
				}
				out.write("><tr><td class='form_head'>");
				out.write(title);
				out.write("</td></tr><tr><td>");
			}
		}
	}
	
	
	protected void buildTop(BuildSession session, CompositeMap model,CompositeMap view, Map map, int rows, int columns,String id) throws Exception{
		
		beforeBuildTop(session,model,view,id);
		Writer out = session.getWriter();
		int cellspacing = view.getInt(GridLayouConfig.PROPERTITY_CELLSPACING, 0);
		int cellpadding = view.getInt(GridLayouConfig.PROPERTITY_CELLPADDING, 0);
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, false);		
		
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		title = session.getLocalizedPrompt(title);
		
		int width = getComponentWidth(model, view, map).intValue();
		int height = getComponentHeight(model, view, map).intValue();
		
		String className = getClassName(session,model,view);
		String style = getStyle(session,model,view);
		
		if(showBorder) {
			cellspacing = 1;
			className += " layout-border";
		}
		String theme = session.getTheme();
		if(THEME_MAC.equals(theme)){
			out.write("<table border=0 class='"+className+"'");
		}else{
			out.write("<table border=0 class='"+className+"' id='"+id+"'");
		}
		if(width != 0) out.write(" width=" + width);
		if(height != 0) out.write(" height=" + height);
		if(THEME_MAC.equals(theme) && !"".equals(title)){
			out.write(" style='width:100%'");
		}else {
			if(!"".equals(style)) {
				out.write(" style='"+style+"'");
			}
		}
		
		out.write(" cellpadding="+cellpadding+" cellspacing="+cellspacing+">");
		buildHead(session,model,view, rows, columns);
		afterBuildTop(session,model,view,columns);
	}

	
	protected void buildFoot(BuildSession session, CompositeMap model,CompositeMap view,int columns) throws Exception{
		super.buildFoot(session, model, view,columns);
		Writer out = session.getWriter();
		String showmargin = view.getString(FormConfig.PROPERTITY_SHOWMARGIN, "true");
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, false);
		if("true".equals(showmargin) && !showBorder)out.write("<tr height='5'><td colspan="+columns*2+"></td></tr>");
	}
	
	protected void afterBuildBottom(BuildSession session, CompositeMap model,CompositeMap view,int columns) throws Exception{
		String theme = session.getTheme();
		FormConfig fc = new FormConfig();
		fc.initialize(view);
		String title = fc.getTitle();
		title = session.getLocalizedPrompt(title);
		if(THEME_MAC.equals(theme) && !"".equals(title)){
			Writer out = session.getWriter();
			out.write("</td></tr></table>");
		}
	}

}
