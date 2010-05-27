package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class PassWord extends TextField {
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();		
		map.put(INPUT_TYPE, "password");
		addConfig("detectCapsLock", new Boolean(true));
		map.put(CONFIG, getConfigString());
	}
}
