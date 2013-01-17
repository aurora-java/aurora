package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ImageCodeConfig;

@SuppressWarnings("unchecked")
public class ImageCode extends Component {
	
	public static final String VERSION = "$Revision$";	
	
	private String PROPERTITY_SRC_VALUE = "imagecode";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		ImageCodeConfig icc = ImageCodeConfig.getInstance(view);
		if(icc.isEnable()){
			map.put(ImageCodeConfig.PROPERTITY_SRC, PROPERTITY_SRC_VALUE);			
		}else{
			addConfig(ImageCodeConfig.PROPERTITY_ENABLE, new Boolean(false));
			map.put(ImageCodeConfig.PROPERTITY_SRC, "");
		}
		map.put(CONFIG, getConfigString());
	}
}
