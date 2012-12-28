package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;

public class Link extends Component {
	
	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_URL = "url";
	private static final String PROPERTITY_MODEL = "model";
	private static final String PROPERTITY_MODEL_ACTION = "modelaction";
	
	@SuppressWarnings("unchecked")
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		super.getConfig().remove(ComponentConfig.PROPERTITY_HEIGHT);
		super.getConfig().remove(ComponentConfig.PROPERTITY_WIDTH);
		super.getConfig().remove(ComponentConfig.PROPERTITY_IS_CUST);
		super.getConfig().remove("listeners");
		CompositeMap view = context.getView();	
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		String url = view.getString(PROPERTITY_URL,"");
		if (!"".equals(url)) {
			addConfig(PROPERTITY_URL, uncertain.composite.TextParser.parse(url, model));
		}else{
			String md = view.getString(PROPERTITY_MODEL);
			String ma = view.getString(PROPERTITY_MODEL_ACTION);
			if(!"".equals(md) && !"".equals(ma)){
				addConfig(PROPERTITY_URL, model.getObject("/request/@context_path").toString() + "/autocrud/"+md+"/" + ma);
			}
		}
		map.put(CONFIG, getConfigString());
	}
}
