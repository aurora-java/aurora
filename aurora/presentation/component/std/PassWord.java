package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.ocm.IObjectRegistry;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class PassWord extends TextField {
	
	public PassWord(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	public static String DEFAULT_INPUT_TYPE = "password";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();		
		map.put(INPUT_TYPE, DEFAULT_INPUT_TYPE);
		addConfig("detectCapsLock", Boolean.FALSE);
		map.put(CONFIG, getConfigString());
	}
}
