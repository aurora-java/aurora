package aurora.presentation;

import freemarker.template.Configuration;

public interface IFreeMarkerTemplateProvider {
	
	public Configuration getFreeMarkerConfiguration();
	
	public String getDefaultEncoding();
}
