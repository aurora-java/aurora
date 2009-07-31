package aurora.presentation.component.std;

import java.io.Writer;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;

/**
 * Box
 * @version $Id: Box.java v 1.0 2009-7-31 上午10:37:19 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class Box extends GridLayout {
	
	private static final String DEFAULT_TH_CLASS = "layout-th";
	private static final String DEFAULT_HEAD_CLASS = "layout-head";
	private static final String PROPERTITY_LABEL_WIDTH = "labelWidth";
	
	protected void buildHead(BuildSession session, CompositeMap model,CompositeMap view, int rows ,int columns) throws Exception{
		Writer out = session.getWriter();
		String title = view.getString("title", "");
		if(!"".equals(title)) {
			out.write("<thead><tr><th class='"+DEFAULT_HEAD_CLASS+"' colspan="+columns*2+">");
			out.write(title);
			out.write("</th></tr></thead>");
		}
	}
	
	protected void afterBuildTop(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
//		Writer out = session.getWriter();
//		String title = view.getString("title", "");
//		if(!"".equals(title)) {
//			out.write("<tr height=5><td><td></tr>");
//		}
	}
	
	protected int getLabelWidth(CompositeMap view){
		int labelWidth = view.getInt(PROPERTITY_LABEL_WIDTH, 75);
		return labelWidth;
	}
	
	protected void beforeBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		Writer out = session.getWriter();
		String label = field.getString("label", "");
		int labelWidth = view.getInt(PROPERTITY_LABEL_WIDTH, 75);
		if(!"".equals(label))
		out.write("<th class='"+DEFAULT_TH_CLASS+"'><div style='width:"+labelWidth+"px;'>"+label+":</div></th>");
	}
}
