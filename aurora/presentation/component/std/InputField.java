package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;

@SuppressWarnings("unchecked")
public class InputField extends Field {
	
	public static final String VERSION = "$Revision$";
	
	protected static final String CLASSNAME_EMPTYTEXT = "item-emptyText";
	
	protected String getDefaultClass(BuildSession session, ViewContext context){
		CompositeMap view = context.getView();
		InputFieldConfig ifc = new InputFieldConfig();
		ifc.initialize(view);
		
		Map map = context.getMap();
		String wrapClass = super.getDefaultClass(session, context);
		String emptyText = ifc.getEmptyText();
		String value = (String)map.get(ComponentConfig.PROPERTITY_VALUE);
		if(!"".equals(emptyText) && "".equals(value)) {
			wrapClass += " " + CLASSNAME_EMPTYTEXT;
		}
		return wrapClass;
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context)throws IOException{
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		InputFieldConfig ifc = new InputFieldConfig();
		ifc.initialize(view);
		Map map = context.getMap();
		
		/** 输入框宽度**/
		Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
		map.put(InputFieldConfig.PROPERTITY_INPUTWIDTH, new Integer(width.intValue()-3));
		
		/** MaxLength **/
		Integer maxlength = ifc.getMaxLength();
		if(maxlength != null)
		addConfig(InputFieldConfig.PROPERTITY_MAX_LENGHT, maxlength);
		
		
		/** 是否转换全角 **/
		boolean isTranChara = ifc.isTransformCharacter();
		addConfig(InputFieldConfig.PROPERTITY_CHARA_TRANSFORM, isTranChara);
		
		/** 值 **/
		String value = (String)map.get(ComponentConfig.PROPERTITY_VALUE);
		
		/** 文本提示 **/
		String emptyText = ifc.getEmptyText();
		if(!"".equals(emptyText) && "".equals(value)) {
			map.put(ComponentConfig.PROPERTITY_VALUE, emptyText);
			addConfig(InputFieldConfig.PROPERTITY_EMPTYTEXT, emptyText);
		}
		addConfig(InputFieldConfig.PROPERTITY_EDITABLE, ifc.isEditable());
	}

}
