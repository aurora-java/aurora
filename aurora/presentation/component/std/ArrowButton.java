package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

@SuppressWarnings("unchecked")
public class ArrowButton extends Button {
	
	public static final String TAG_NAME = "arrowButton";
	private static final String DEFAULT_CLASS = " item-abtn ";
	private static final String PROPERTITY_TYPE = "type";
	private static final String ARROW_CLASS = "arrow";
	private static final String TYPE_LEFT = "left";
	private static final String TYPE_RIGHT = "right";
	private static final String CLASS_LEFT = "albtn";
	private static final String CLASS_RIGHT = "arbtn";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		String clz = super.getDefaultClass(session, context);
		CompositeMap view = context.getView();
		String type = view.getString(PROPERTITY_TYPE,TYPE_RIGHT);
		return clz + DEFAULT_CLASS + (TYPE_LEFT.equals(type) ? CLASS_LEFT : CLASS_RIGHT);
	}
	
	protected int getDefaultHeight(){
		return 30;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap model = context.getModel();
		CompositeMap view = context.getView();
		Map map = context.getMap();
		String type = view.getString(PROPERTITY_TYPE,TYPE_RIGHT);
		map.put(TYPE_LEFT.equals(type) ?  TYPE_LEFT : TYPE_RIGHT, ARROW_CLASS);
	}
}
