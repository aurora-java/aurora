package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class PassWord extends TextField {
	
	public static final String VERSION = "$Revision$";
	
	public static String DEFAULT_INPUT_TYPE = "password";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();		
		map.put(INPUT_TYPE, DEFAULT_INPUT_TYPE);
		addConfig("detectCapsLock", new Boolean(false));
		map.put(CONFIG, getConfigString());
	}
}
