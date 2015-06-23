package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;

public class Switch implements IViewBuilder {

	public static final String VERSION = "$Revision$";
	
	private static final String TEST_FIELD = "test";
	private static final String KEY_VALUE = "value";

	public void buildView(BuildSession session, ViewContext view_context) throws IOException, aurora.presentation.ViewCreationException {
		CompositeMap model = view_context.getModel();//.getParent();
		CompositeMap view = view_context.getView();
		
		if (model == null) return;
		String testField = view.getString(TEST_FIELD);
		if (testField == null) throw new aurora.presentation.ViewCreationException("selector: No test field specified");
		Object obj = model.getObject(testField);
		Iterator it = view.getChildIterator();
		if (it == null) throw new aurora.presentation.ViewCreationException("selector:No case found");
		String host_id = view.getString(ComponentConfig.PROPERTITY_HOST_ID);
		Collection child_views = null;
		while (it.hasNext()) {
			CompositeMap child = (CompositeMap) it.next();
			Object test_value = child.get(KEY_VALUE);
			if ("null".equals(test_value) && obj == null) {
				child_views = child.getChilds();
				break;
			}else if(test_value==null){
			    child_views = child.getChilds();
			    break;
			}else {
				String vl = test_value.toString();

				if ("*".equals(vl)) {
//					if (obj != null) {
						child_views = child.getChilds();
						break;
//					}
				}			
				vl = TextParser.parse(vl, model);
//				if(obj==null)
//				    break;
				if (obj != null && vl.equals(obj.toString())) {
					child_views = child.getChilds();
					break;
				}
			}
		}

		if (child_views != null)
		try {
	        Iterator it2 = child_views.iterator();
	        while (it2.hasNext()) {
	            CompositeMap child_view = (CompositeMap) it2.next();
	            if(null != host_id){
	            	child_view.putString(ComponentConfig.PROPERTITY_HOST_ID, host_id);
	            }
	            session.buildView(model, child_view);
	        }
		} catch (Exception e) {
			throw new aurora.presentation.ViewCreationException(e);
		}
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
}
