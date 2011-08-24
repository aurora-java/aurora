package aurora.application.util;

import java.io.IOException;

import uncertain.composite.CompositeMap;

public interface ILoggingConifg {
	
	public void setLogginConfig(CompositeMap parameter) throws IOException;
	
	public void getAllLoggingConfig(CompositeMap parameter, String baseDir, String fileExt) throws Exception;
}
