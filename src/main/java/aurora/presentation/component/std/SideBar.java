package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.LeftBarConfig;
import aurora.presentation.component.std.config.RightBarConfig;
import aurora.presentation.component.std.config.SideBarConfig;

@SuppressWarnings("unchecked")
public class SideBar extends Component{
	protected static final String DEFAULT_CLASS = "item-sidebar";
	
	
	protected enum TYPES {LEFT,RIGHT};
	
	public SideBar(IObjectRegistry registry) {
		super(registry);
	}
	
	protected  TYPES getBarType(){		
		return null;
	}
	
	protected int getDefaultWidth() {
		return 250;
	}
	
	protected int getDefaultHeight() {
		return 500;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		switch(getBarType()){
		case LEFT:addConfig(SideBarConfig.BAR_TYPE, LeftBarConfig.LEFT_BAR);break;
		case RIGHT:addConfig(SideBarConfig.BAR_TYPE, RightBarConfig.RIGHT_BAR);break;
		}
		boolean expand = view.getBoolean(SideBarConfig.EXPAND, true);
		if(expand==false){
			map.put(SideBarConfig.BODY_STYLE, "display:none");
			map.put(ComponentConfig.PROPERTITY_WIDTH, 0);
		}
		
		String url = view.getString(SideBarConfig.URL);
		if(url!=null) addConfig(SideBarConfig.URL, url);
		
		String htstr = view.getString(ComponentConfig.PROPERTITY_HEIGHT);
		if(htstr==null) addConfig(SideBarConfig.FULL_HEIGHT, true);
		map.put(CONFIG, getConfigString());
	}

}
