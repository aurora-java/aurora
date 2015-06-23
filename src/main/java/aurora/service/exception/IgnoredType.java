package aurora.service.exception;

import uncertain.composite.CompositeMap;
import uncertain.exception.ConfigurationFileException;
import uncertain.ocm.IConfigurable;

public class IgnoredType implements IConfigurable{
	private CompositeMap config;
	private String name;
	public void setName(String name){
		try {
			Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationFileException("uncertain.exception.classnotfoundexception",new Object[]{name},e,config.asLocatable());
		}
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public void beginConfigure(CompositeMap config) {
		this.config = config;
		
	}
	public void endConfigure() {
		
	}
}
