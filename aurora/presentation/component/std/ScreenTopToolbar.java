package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ButtonConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.ScreenTopToolbarConfig;
import aurora.presentation.component.std.config.ToolBarButtonConfig;

public class ScreenTopToolbar extends Component implements IViewBuilder {
	
	public static final String VERSION = "$Revision$";
	
	protected int getDefaultHeight() {
		return 44;
	}
	
	@SuppressWarnings("unchecked")
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Map map = view_context.getMap();
		try {
			
			Writer out = session.getWriter();
			StringBuffer sb = new StringBuffer();
			Integer height = getComponentHeight(model, view, map);
			ScreenTopToolbarConfig sttc = ScreenTopToolbarConfig.getInstance(view);
			sb.append("<div class='screenTopToolbar' ");
			sb.append("style='padding-left:4px;height:").append(height).append("px;");
			sb.append(sttc.getStyle(""));
			sb.append("'>");
			
			if(view != null && view.getChilds() != null) {
				
				Iterator it = view.getChildIterator();
				while(it.hasNext()){
					CompositeMap cmp = (CompositeMap)it.next();
					String cs = cmp.getString(ComponentConfig.PROPERTITY_STYLE,"margin-left:5px;");
					if(isButton(cmp.getNamespaceURI(), cmp.getName())){
						String marginTop = "3px;";
						if(ToolBarButtonConfig.TAG_NAME.equalsIgnoreCase(cmp.getName())||GridButton.TAG_NAME.equalsIgnoreCase(cmp.getName())) {
							marginTop = "15px;";						
						}
						cs = "float:left;margin-right:1px;margin-top:" + marginTop + cs;
					} else if(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE.equals(cmp.getNamespaceURI()) &&cmp.getName().equalsIgnoreCase("separator")){
						cs = "height:"+(height-4)+"px;margin-top:2px;float:left;margin-right:1px;" + cs;	
					} else if(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE.equals(cmp.getNamespaceURI())){
						cs = "float:left;margin-right:1px;margin-top:2px;line-height:"+(height)+"px;" + cs;
					} else{
						cs = "float:left;margin-right:1px;" + cs;					
					}
					cmp.put(ComponentConfig.PROPERTITY_STYLE, cs);
					sb.append(session.buildViewAsString(model, cmp));
				}
			}
			sb.append("</div>");
			out.write(sb.toString());
			out.flush();
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
	}
	
	public boolean isButton(String nameSpace,String name){
		boolean isBtn = false;
		if(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE.equals(nameSpace)){
			isBtn = ButtonConfig.TAG_NAME.equalsIgnoreCase(name)||ToolBarButtonConfig.TAG_NAME.equalsIgnoreCase(name)||GridButton.TAG_NAME.equalsIgnoreCase(name);
		}
		return isBtn;
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
}
