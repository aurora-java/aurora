package aurora.presentation;

import uncertain.core.IGlobalInstance;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

public class FreeMarkerProvider implements IFreeMarkerTemplateProvider, IGlobalInstance {
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	private Configuration freemarkerConfiguration;
	
	private String defaultEncoding;
	
	public Configuration getFreeMarkerConfiguration(){
		return freemarkerConfiguration;
	}
	
	
	public void onInitialize() throws Exception {
		freemarkerConfiguration = new Configuration();
		freemarkerConfiguration.setTemplateUpdateDelay(60000);
		freemarkerConfiguration.setDefaultEncoding(getDefaultEncoding());
		freemarkerConfiguration.setOutputEncoding(getDefaultEncoding());
		freemarkerConfiguration.setNumberFormat("#");
		freemarkerConfiguration.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
	}


	public String getDefaultEncoding() {
		return defaultEncoding == null ? DEFAULT_ENCODING : defaultEncoding;
	}


	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}
}
