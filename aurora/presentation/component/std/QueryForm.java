package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.BoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FormConfig;

public class QueryForm extends Component implements IViewBuilder, ISingleton {
	private static final String DEFAULT_TABLE_CLASS = "layout-table";
	private static final String DEFAULT_WRAP_CLASS = "form_body_wrap";
	private static final String FORM_HEAD = "formHead";
	private static final String FORM_BODY = "formBody";
	
	private static final String PROPERTITY_OPEN = "open";
	
	protected int getDefaultWidth(){
		return 0;
	}
	
	protected int getDefaultHeight(){
		return 0;
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
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		boolean open = view.getBoolean(PROPERTITY_OPEN, true);
		int width = getComponentWidth(model, view, map).intValue();
		int height = getComponentHeight(model, view, map).intValue();
		
		String className = DEFAULT_TABLE_CLASS + " layout-form layout-title " + cls;
		Writer out = session.getWriter();
		try{
			out.write("<table cellspacing='0' cellpadding='0' class='"+className+"' id='"+id+"'");
			if(width != 0) style ="width:" + width+"px;" + style;
			if(!"".equals(style)) {
				out.write(" style='"+style+"'");
			}
			out.write(">");
			CompositeMap formHead = view.getChild(FORM_HEAD);
			out.write("<thead><tr><th>");
			if(null!=formHead){
				Iterator it = formHead.getChildIterator();
				if(null !=it){
					formHead.setName("hBox");
					session.buildView(model, formHead);
				}
			}
			out.write("</th></tr></thead>");
			CompositeMap formBody = view.getChild(FORM_BODY);
			out.write("<tbody><tr><td>");
			out.write("<div class='"+DEFAULT_WRAP_CLASS+"'");
			if(!open) {
				out.write(" style='height:0'");
			}
			out.write(">");
			if(null!=formBody){
				Iterator it = formBody.getChildIterator();
				if(null !=it){
					formBody.setName("box");
					if(height != 0)formBody.put(ComponentConfig.PROPERTITY_HEIGHT, height - 26);
					session.buildView(model, formBody);
				}
			}
			out.write("</div></td></tr></tbody>");
			out.write("</table>");
			out.write("<script>");
			out.write("new $A.QueryForm({id:'"+id+"',isopen:"+open+"})");
			out.write("</script>");
		}catch (Exception e) {
			throw new ViewCreationException(e);
		}
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
}
