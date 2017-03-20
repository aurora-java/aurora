package aurora.presentation.component.std;

import java.io.IOException;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ButtonConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.GridConfig;

public class GridButton extends ToolBarButton {
	
	public GridButton(IObjectRegistry registry) {
		super(registry);
	}


	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "gridButton";
	
	private static final String PROPERTITY_TYPE = "type";
	private static final String PROPERTITY_BIND = "bind";
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
		CompositeMap model = context.getModel();
		CompositeMap view = context.getView();
		String type = view.getString(PROPERTITY_TYPE,"");
		String grid = view.getString(PROPERTITY_BIND,"");
		if(!"".equals(type) && !"".equals(grid)){			
			List list = CompositeUtil.findChilds(view.getRoot(), GridConfig.TAG_NAME, ComponentConfig.PROPERTITY_ID, grid);
			if(list != null && list.size() == 1){
				CompositeMap gc = (CompositeMap)list.get(0);
				String dataset = uncertain.composite.TextParser.parse(gc.getString(ComponentConfig.PROPERTITY_BINDTARGET,""),model);
				String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
				if ("".equals(id)) {
					id = grid + "_" + type;
					view.putString(ComponentConfig.PROPERTITY_ID, id);
				}
				
				String fileName = uncertain.composite.TextParser.parse(view.getString("filename",""),model);
				if("add".equalsIgnoreCase(type)){
					view = createButton(view,session.getLocalizedPrompt("HAP_NEW"),"grid-add2","","function(){$au('"+grid+"').showEditorByRecord($au('"+dataset+"').create())}");
				}else if("delete".equalsIgnoreCase(type)){
					view = createButton(view,session.getLocalizedPrompt("HAP_DELETE"),"grid-delete2","","function(){$au('"+grid+"').remove()}");
				}else if("save".equalsIgnoreCase(type)){
					view = createButton(view,session.getLocalizedPrompt("HAP_SAVE"),"grid-save2","","function(){$au('"+dataset+"').submit()}");
				}else if("clear".equalsIgnoreCase(type)){
					view = createButton(view,session.getLocalizedPrompt("HAP_CLEAR"),"grid-clear2","","function(){$au('"+grid+"').clear()}");
				}else if("excel".equalsIgnoreCase(type)){
					view = createButton(view,session.getLocalizedPrompt("HAP_EXPORT"),"grid-excel2","","function(){$au('"+grid+"')._export('xls','"+fileName+"')}");
				}else if("excel2007".equalsIgnoreCase(type)){
					view = createButton(view,session.getLocalizedPrompt("HAP_EXPORT"),"grid-excel2","","function(){$au('"+grid+"')._export('xlsx','"+fileName+"')}");
				}else if("txt".equalsIgnoreCase(type)){
					String separator = view.getString("separator","");
					view = createButton(view,session.getLocalizedPrompt("HAP_EXPORT"),"grid-excel2","","function(){$au('"+grid+"')._export('txt','"+fileName+"','"+separator+"')}");
				}else if("customize".equalsIgnoreCase(type)){
					view = createButton(view,session.getLocalizedPrompt("HAP_CUST"),"grid-cust2","","function(){$au('"+grid+"').customize()}");
				}
			}	
		}
		super.onCreateViewContent(session, context);
	}
	
	
	private CompositeMap createButton(CompositeMap button, String text, String clz,String style,String function){
		if("".equals(button.getString(ButtonConfig.PROPERTITY_ICON,""))){
			button.put(ButtonConfig.PROPERTITY_ICON, "null");
			button.put(ButtonConfig.PROPERTITY_BUTTON_CLASS, clz);
			button.put(ButtonConfig.PROPERTITY_BUTTON_STYLE, style);
		}
		button.put(ButtonConfig.PROPERTITY_TEXT,button.getString(ButtonConfig.PROPERTITY_TEXT, text));
		if(!"".equals(function))button.put(ButtonConfig.PROPERTITY_CLICK, function);
		return button;
	}
}
