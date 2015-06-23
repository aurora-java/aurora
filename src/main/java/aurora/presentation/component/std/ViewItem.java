package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ViewItemConfig;

public class ViewItem implements IViewBuilder {
	
	public static final String VERSION = "$Revision$";

	@Override
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Writer out = session.getWriter();
		ViewItemConfig vic = ViewItemConfig.getInstance(view);
		String val = vic.getValue("");
		String format = vic.getFormat();
		String clz = vic.getClassName("");
		int width = vic.getWidth();
		String sty = vic.getStyle("");
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
