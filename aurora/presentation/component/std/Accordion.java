package aurora.presentation.component.std;

import java.io.IOException;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Accordion extends Component {

	private static final String DEFAULT_CLASS = "layou-accordion";
	
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		return DEFAULT_CLASS;
	}
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
//		addStyleSheet(session, context, "grid/Grid-min.css");
//		addJavaScript(session, context, "grid/Grid-min.js");
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{	
		super.onCreateViewContent(session, context);
		context.getView();
		context.getMap();
	}

}
