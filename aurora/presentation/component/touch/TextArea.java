package aurora.presentation.component.touch;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class TextArea extends InputField {
	
	private static final String PROPERTITY_ROWS= "rows";
	
	protected String getDefaultClass(BuildSession session, ViewContext context) {
		return "textarea";
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		Map map = context.getMap();	
		CompositeMap view = context.getView();
		map.put(PROPERTITY_ROWS, view.getInt(PROPERTITY_ROWS,4));
	}
}
