package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;

public class ScreenTopToolbar extends Component implements IViewBuilder, ISingleton {
	
	
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
			sb.append("<div class='screenTopToolbar' ");
			sb.append("style='padding-left:4px;height:").append(height).append("px");
			String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
			sb.append(style);
			sb.append("'>");
			
			if(view != null && view.getChilds() != null) {
				
				Iterator it = view.getChildIterator();
				while(it.hasNext()){
					CompositeMap cmp = (CompositeMap)it.next();
					String cs = cmp.getString(ComponentConfig.PROPERTITY_STYLE,"");
					if(isButton(cmp.getNamespaceURI(), cmp.getName())){
						cs = "float:left;margin-right:1px;margin-top:15px;" + cs;
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
			isBtn = Button.TAG_NAME.equalsIgnoreCase(name)||ToolBarButton.TAG_NAME.equalsIgnoreCase(name)||GridButton.TAG_NAME.equalsIgnoreCase(name);
		}
		return isBtn;
	}

	@Override
	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
}
