package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

@SuppressWarnings("unchecked")
public class ArrowButton extends Button {
	
	public ArrowButton(IObjectRegistry registry) {
		super(registry);
	}


	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "arrowButton";
	private static final String DEFAULT_CLASS = " item-abtn ";
	private static final String ARROW_CLASS = "arrow";
	private static final String CLASS_LEFT = "albtn";
	private static final String CLASS_RIGHT = "arbtn";
	
	protected static final String TYPE_LEFT = "left";
	protected static final String TYPE_RIGHT = "right";
	protected String getType(){
		return 	TYPE_RIGHT;	
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		String clz = super.getDefaultClass(session, context);
		CompositeMap view = context.getView();
		String type = getType();
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
		String type = getType();
		map.put(TYPE_LEFT.equals(type) ?  TYPE_LEFT : TYPE_RIGHT, ARROW_CLASS);
	}
}
