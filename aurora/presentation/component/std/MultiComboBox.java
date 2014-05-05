package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComboBoxConfig;

/**
 * ComboBox组件.
 * 
 * @version $Id: ComboBox.java 8106 2014-02-14 04:34:07Z hugh.hz.wu@gmail.com $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class MultiComboBox extends ComboBox {
	
	public MultiComboBox(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";

	public void onCreateViewContent(BuildSession session, ViewContext view_context) throws IOException{
		super.onCreateViewContent(session, view_context);
		Map map = view_context.getMap();
		addConfig(ComboBoxConfig.PROPERTITY_EDITABLE, false);
		map.put(CONFIG, getConfigString());
	}

	
}
