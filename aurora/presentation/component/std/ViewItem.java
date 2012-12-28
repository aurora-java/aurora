package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;

public class ViewItem implements IViewBuilder, ISingleton {
	
	public static final String VERSION = "$Revision$";
	
	private static final String PROPERTITY_VALUE = ComponentConfig.PROPERTITY_VALUE;
	private static final String PROPERTITY_FORMAT = "format";
	private static final String PROPERTITY_STYLE = ComponentConfig.PROPERTITY_STYLE;
	private static final String PROPERTITY_CLASSNAME = ComponentConfig.PROPERTITY_CLASSNAME;
	private static final String PROPERTITY_WIDTH = ComponentConfig.PROPERTITY_WIDTH;

	@Override
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Writer out = session.getWriter();
		String val = view.getString(PROPERTITY_VALUE,"");
		String format = view.getString(PROPERTITY_FORMAT,"");
		String clz = view.getString(PROPERTITY_CLASSNAME,"");
		int width = view.getInt(PROPERTITY_WIDTH, 150);
		String sty = view.getString(PROPERTITY_STYLE,"");
		Object obj = model.getObject(val);
		String value = "";
		if(obj instanceof Date){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			value = sdf.format((Date)obj);
		}else if(obj instanceof Long || obj instanceof Double){
			DecimalFormat df = new DecimalFormat(format);
			value = df.format(obj);
		}else if(obj != null){
			value = obj.toString();
		}
		out.write("<div ");
		out.write("style='width:"+width+"px;"+sty+"' ");
		out.write("class='item-view ");
		out.write(clz);
		out.write("'>"+value+"</div>");
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
