package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.util.template.TextTemplate;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.TextFieldConfig;

/**
 * 文本输入框
 * 
 * @version $Id: TextField.java 6975 2012-12-28 02:21:30Z njq.niu@gmail.com $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 * 
 */
public class MultiTextField extends TextField {	
	
	public MultiTextField(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision: 6975 $";
	public static String INPUT_TYPE = "inputtype";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap view = context.getView();
		TextFieldConfig tfc = TextFieldConfig.getInstance(view);
		String typeCase = tfc.getTypeCase();
		if(null != typeCase) {
			addConfig(TextFieldConfig.PROPERTITY_TYPE_CASE, typeCase.toLowerCase());
		}
		String restrict = tfc.getRestrict();
		if(null != restrict) {
			addConfig(TextFieldConfig.PROPERTITY_RESTRICT, restrict);
		}
		String restrictInfo = tfc.getRestrictInfo();
		if(null != restrictInfo) {
			addConfig(TextFieldConfig.PROPERTITY_RESTRICT_INFO, session.getLocalizedPrompt(restrictInfo));
		}
		map.put(CONFIG, getConfigString());
	}
	

	
}
