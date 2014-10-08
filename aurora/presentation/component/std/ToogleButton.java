package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ButtonConfig;
import aurora.presentation.component.std.config.ToogleButtonConfig;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

public class ToogleButton extends Field {

	public static final String VERSION = "$Revision$";
	
	private String CLASSNAME_WRAP = "iconfont item-tgl-btn ";
	private static final String PLUS_TEXT = "&#xe6e3;";
	private static final String MINUS_TEXT = "&#xe6c4";
	private static final String TOOGLE_TEXT = "toogleText";

	public ToogleButton(IObjectRegistry registry) {
		super(registry);
	}
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		ToogleButtonConfig bc = ToogleButtonConfig.getInstance(view);
		String wrapClass = CLASSNAME_WRAP;
		String className = bc.getClassName();
		if (className != null) {
			wrapClass +=className;
		}
		return wrapClass;
	}

	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);		
		CompositeMap model = context.getModel();
		CompositeMap view = context.getView();
		ToogleButtonConfig bc = ToogleButtonConfig.getInstance(view);
		
		Map map = context.getMap();
		String clickEvent = bc.getClick();
		if(!"".equals(clickEvent)){
			if(clickEvent.indexOf("${") != -1)  //和$()有冲突
			clickEvent = uncertain.composite.TextParser.parse(clickEvent, model);
			addEvent(id, "click", clickEvent);
		}
		
		boolean toogled = bc.isToolged();
		addConfig(ToogleButtonConfig.PROPERTITY_TOOGLED, Boolean.valueOf(toogled));
		
		String toogleId = bc.getToogleId();
		if(toogleId!=null){
			addConfig(ToogleButtonConfig.PROPERTITY_TOOGLE_ID, toogleId);
		}
		map.put(TOOGLE_TEXT, toogled ? MINUS_TEXT : PLUS_TEXT);
		map.put(CONFIG, getConfigString());
	}

}