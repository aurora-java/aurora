package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.util.template.TextTemplate;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.TextFieldConfig;

/**
 * 文本输入框
 * 
 * @version $Id: TextField.java v 1.0 2009-7-20 上午11:27:00 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 * 
 */
public class TextField extends InputField {	
	
	public static String INPUT_TYPE = "inputtype";
	public static String DEFAULT_INPUT_TYPE = "input";
	public static String IS_ROUND_BORDER = "isroundborder";
	private static final String TEMPLATE_ROUND_TEXTFILED = "roundTextField.tplt";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		
		
		Map map = context.getMap();
		CompositeMap view = context.getView();
		boolean isRoundBorder = view.getBoolean(IS_ROUND_BORDER, false);
		if(isRoundBorder){
			context.setTemplate(session.getTemplateByName(TEMPLATE_ROUND_TEXTFILED));
		}
		
		
		String typeCase = view.getString(TextFieldConfig.PROPERTITY_TYPE_CASE, "");
		if(!"".equals(typeCase)) {
			addConfig(TextFieldConfig.PROPERTITY_TYPE_CASE, typeCase.toLowerCase());
		}
		map.put(INPUT_TYPE, DEFAULT_INPUT_TYPE);
		map.put(CONFIG, getConfigString());
	}
	
	

	public void onLoadTemplate(BuildSession session, ViewContext view_context ) throws IOException{
	     CompositeMap view = view_context.getView();
	     if(view.getBoolean("round", false)){
	          TextTemplate template = session.getTemplateByName("toundTextField.tplt");
	          view_context.setTemplate(template);
	     }
	}

	
}
