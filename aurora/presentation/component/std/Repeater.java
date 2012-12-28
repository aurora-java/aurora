package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

public class Repeater implements IViewBuilder {
	
	public static final String VERSION = "$Revision$";
	
	private static final String KEY_DATAMODEL = "datamodel";
	
	public void buildView(BuildSession session, ViewContext view_context)throws IOException, ViewCreationException {
		CompositeMap model = view_context.getModel();
		CompositeMap view = view_context.getView();
		
		String dataModel = view.getString(KEY_DATAMODEL);
		//if("".equals(dataModel)) throw new aurora.presentation.ViewCreationException("repeater: No dataModel field specified");
		CompositeMap m = null;
		if(dataModel!=null)
		    m = (CompositeMap)model.getObject(dataModel);
		else
		    m = model;
		if (m == null) return;
		Iterator itm = m.getChildIterator();
		if (itm == null) return;
		Collection childViews = view.getChilds();
		if (childViews == null) return;
		
        try {
			while (itm.hasNext()) {
				CompositeMap childModel = (CompositeMap) itm.next();
				session.buildViews(childModel, childViews);
			}
        } catch (Exception e) {
        	throw new aurora.presentation.ViewCreationException(e.getMessage());
        }
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
