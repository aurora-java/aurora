package aurora.presentation.component.std;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class RoundButton extends Button {
	
	
	public static final String TAG_NAME = "roundButton";
	private static final String DEFAULT_CLASS = " item-rtbtn ";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		String style = super.getDefaultClass(session, context);
		return style + DEFAULT_CLASS;
	}
	
	protected int getDefaultHeight(){
		return 30;
	}
}
