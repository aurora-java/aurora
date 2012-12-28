package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;

import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComboBoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;

public class InputField extends Field {
	
	public static final String VERSION = "$Revision$";
	
	protected static final String CLASSNAME_EMPTYTEXT = "item-emptyText";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		Map map = context.getMap();
		String wrapClass = super.getDefaultClass(session, context);
		String emptyText = view.getString(InputFieldConfig.PROPERTITY_EMPTYTEXT,"");
		String value = (String)map.get(ComponentConfig.PROPERTITY_VALUE);
		if(!"".equals(emptyText) && "".equals(value)) {
			wrapClass += " " + CLASSNAME_EMPTYTEXT;
		}
		return wrapClass;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context)throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		/** 输入框宽度**/
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		map.put(InputFieldConfig.PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-3));
		
		/** 值 **/
		String value = (String)map.get(ComponentConfig.PROPERTITY_VALUE);
		
		/** 文本提示 **/
		String emptyText = view.getString(InputFieldConfig.PROPERTITY_EMPTYTEXT,"");
		if(!"".equals(emptyText) && "".equals(value)) {
			map.put(ComponentConfig.PROPERTITY_VALUE, emptyText);
			addConfig(InputFieldConfig.PROPERTITY_EMPTYTEXT, emptyText);
		}
		addConfig(InputFieldConfig.PROPERTITY_EDITABLE,new Boolean(view.getBoolean(InputFieldConfig.PROPERTITY_EDITABLE,true)));
		map.put(ComponentConfig.PROPERTITY_TAB_INDEX, new Integer(view.getInt(ComponentConfig.PROPERTITY_TAB_INDEX, 0)));
	}

}
