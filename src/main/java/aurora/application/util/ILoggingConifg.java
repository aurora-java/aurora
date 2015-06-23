package aurora.application.util;

import java.io.IOException;

import uncertain.composite.CompositeMap;

public interface ILoggingConifg {
	
	public void setLogginConfig(CompositeMap parameter) throws Exception;
	
	public void getLoggingConfig(CompositeMap parameter, String baseDir, String fileExt) throws Exception;
}
