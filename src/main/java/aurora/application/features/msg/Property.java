package aurora.application.features.msg;

import java.util.Map;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;

public class Property extends AbstractLocatableObject{
	public String key;
	public String value;
	public String path;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void putToMap(CompositeMap context,Map map,boolean isOverride){
		if(path==null && value ==null){
			throw BuiltinExceptionFactory.createOneAttributeMissing(this, "path,value");
		}
		if(path != null && value !=null){
			throw BuiltinExceptionFactory.createConflictAttributesExcepiton(this, "path,value");
		}
		Object objectValue = value;
		String name = key;
		if(context != null){
			name = TextParser.parse(key, context);
			if(value != null)
				objectValue = TextParser.parse(value, context);
			else
				objectValue = context.getObject(path);
		}
		if(isOverride)
			map.put(name, objectValue);
		else{
			if(map.get(name) == null){
				map.put(name, objectValue);
			}
		}
	}
	
}
