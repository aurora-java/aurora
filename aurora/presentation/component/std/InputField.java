package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationViewConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.InputFieldConfig;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;
import aurora.service.http.UserAgentTools;

@SuppressWarnings("unchecked")
public class InputField extends Field {
	
	public InputField(IObjectRegistry registry) {
		super(registry);
	}

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
		CompositeMap model = context.getModel();
		boolean mDefaultTransformCharacter = ApplicationViewConfig.DEFAULT_TRANSFORM_CHARACTER;
		if (mApplicationConfig != null) {
			ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
			if (null != view_config) {
				mDefaultTransformCharacter = view_config.getDefaultTransformCharacter();
			}
		}
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
		boolean isTranChara = ifc.isTransformCharacter(mDefaultTransformCharacter);
		if(!isTranChara)addConfig(InputFieldConfig.PROPERTITY_CHARA_TRANSFORM, isTranChara);
		
		/** 是否自动全选 **/
		boolean isAutoSelect = ifc.isAutoSelect();
		if(!isAutoSelect)addConfig(InputFieldConfig.PROPERTITY_AUTO_SELECT, isAutoSelect);
		
		
		/** 值 **/
		String value = (String)map.get(ComponentConfig.PROPERTITY_VALUE);
		
		/** 文本提示 **/
		String emptyText = ifc.getEmptyText(session,model);
		if(!"".equals(emptyText) && "".equals(value)) {
			map.put(ComponentConfig.PROPERTITY_VALUE, emptyText);
			addConfig(InputFieldConfig.PROPERTITY_EMPTYTEXT, emptyText);
		}
		
		/**解决chrome,safari光标位置不居中问题**/
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(model.getRoot());
		String[] userAgent = UserAgentTools.getBrowser(serviceInstance.getRequest().getHeader("User-Agent"));
		String browser = userAgent[1];
		if(browser!=null && browser.toLowerCase().indexOf("chrome")==-1 && browser.toLowerCase().indexOf("safari")==-1){
			Integer height = (Integer)map.get(ComponentConfig.PROPERTITY_HEIGHT);
			map.put("lineHeight", "line-height:"+ height+"px;");
		}
		
		String fontStyle = ifc.getFontStyle(model);
		if(null!=fontStyle){
			map.put(InputFieldConfig.PROPERTITY_FONT_STYLE, fontStyle);
		}
		addConfig(InputFieldConfig.PROPERTITY_EDITABLE, ifc.isEditable());
	}

}
