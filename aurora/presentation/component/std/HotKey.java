package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;

import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

public class HotKey extends Component  implements IViewBuilder, ISingleton {
	
	public static final String VERSION = "$Revision$";
	
	private static final String KEYS = "keys";
	private static final String PROPERTITY_BIND = "bind";
	private static final String PROPERTITY_HANDLER = "handler";
	
	public void buildView(BuildSession session, ViewContext view_context)
			throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap keys = view.getChild(KEYS);
		if(null != keys){
			Iterator it = keys.getChildIterator();
			if(null!=it){
				Writer out = session.getWriter();
				out.write("<script>");
				while(it.hasNext()){
					CompositeMap key = (CompositeMap) it.next();
					String bind = key.getString(PROPERTITY_BIND);
					String handler = key.getString(PROPERTITY_HANDLER);
					out.write("$A.HotKey.addHandler('"+bind+"',"+handler+");");
				}
				out.write("</script>");
			}
		}
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
}
