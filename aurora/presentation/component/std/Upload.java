package aurora.presentation.component.std;

import java.io.IOException;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class Upload extends Component {
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		
		super.onPreparePageContent(session, context);
		addJavaScript(session, context, "upload/swfupload.js");
		addJavaScript(session, context, "upload/swfupload.queue.js");
	}
}
