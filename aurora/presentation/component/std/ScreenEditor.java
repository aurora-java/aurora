package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationViewConfig;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ButtonConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.DataSetFieldConfig;
import aurora.presentation.component.std.config.GridColumnConfig;
import aurora.presentation.component.std.config.GridConfig;
import aurora.presentation.component.std.config.NavBarConfig;
import aurora.presentation.component.std.config.ScreenEditorConfig;

/**
 * 
 * @version $Id: Grid.java 8320 2014-06-05 02:11:24Z njq.niu@gmail.com $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class ScreenEditor extends Component {
	
	public static final String VERSION = "$Revision:$";
	private static final String THEME = "theme";
	
	public ScreenEditor(IObjectRegistry registry) {
		super(registry);
    }

	private static final String DEFAULT_CLASS = "item-screen-editor";
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "grid/Grid-min.css");
		addStyleSheet(session, context, "tab/Tab-min.css");
		addStyleSheet(session, context, "screeneditor/ScreenEditor.css");
		addJavaScript(session, context, "screeneditor/ScreenEditor.js");
	}
	
	protected int getDefaultWidth() {
		return -1;
	}
	
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{	
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		ScreenEditorConfig sec = ScreenEditorConfig.getInstance(view);
		addConfig(ScreenEditorConfig.PROPERTITY_SCREEN_RESOLUTION, sec.getScreenResolution());
		addConfig(THEME, session.getTheme());
		map.put(CONFIG, getConfigString());
	}
}
