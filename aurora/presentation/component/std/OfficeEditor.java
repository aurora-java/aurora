package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

@SuppressWarnings("unchecked")
public class OfficeEditor extends Component {
	
	public OfficeEditor(IObjectRegistry registry) {
		super(registry);
	}

	public static final String VERSION = "$Revision$";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "office/OfficeEditor.js");
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{	
		super.onCreateViewContent(session, context);
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		CompositeMap view = context.getView();
		String docUrl = view.getString("docurl","");
		if(docUrl!=null) docUrl = uncertain.composite.TextParser.parse(docUrl, model);
		addConfig("docUrl",docUrl);
		addConfig("docType",view.getString("doctype"));
		addConfig("readOnly",view.getBoolean("readonly",true));
		
		
		String saveUrl = view.getString("saveurl");
		if(saveUrl!=null) addConfig("saveUrl",uncertain.composite.TextParser.parse(saveUrl, model));
		String pkvalue = view.getString("pkvalue");
		if(pkvalue!=null) addConfig("pkvalue",uncertain.composite.TextParser.parse(pkvalue, model));
		String sourcetype = view.getString("sourcetype");
		if(sourcetype!=null) addConfig("sourcetype",uncertain.composite.TextParser.parse(sourcetype, model));
		
		
		String context_path = model.getObject("/request/@context_path").toString();
		map.put("context_path", context_path);
		map.put(CONFIG, getConfigString());
	}
	
}
