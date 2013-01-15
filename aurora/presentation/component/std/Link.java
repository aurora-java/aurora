package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.LinkConfig;
@SuppressWarnings("unchecked")
public class Link extends Component {
	public static final String VERSION = "$Revision$";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		super.getConfig().remove(ComponentConfig.PROPERTITY_HEIGHT);
		super.getConfig().remove(ComponentConfig.PROPERTITY_WIDTH);
		super.getConfig().remove(ComponentConfig.PROPERTITY_IS_CUST);
		super.getConfig().remove("listeners");
		CompositeMap view = context.getView();	
		LinkConfig lc = LinkConfig.getInstance(view);
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		
		String url = lc.getUrl();
		if (url != null) {
			addConfig(LinkConfig.PROPERTITY_URL, uncertain.composite.TextParser.parse(url, model));
		}else{
			String md = lc.getModel();
			String ma = lc.getModelAction();
			if(md!=null && ma != null){
				addConfig(LinkConfig.PROPERTITY_URL, model.getObject("/request/@context_path").toString() + "/autocrud/"+md+"/" + ma);
			}
		}
		map.put(CONFIG, getConfigString());
	}
}
