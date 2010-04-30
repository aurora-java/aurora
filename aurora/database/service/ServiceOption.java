/*
 * Created on 2008-6-18
 */
package aurora.database.service;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class ServiceOption extends DynamicObject {
    
    public static final String KEY_AUTO_COUNT = "autocount";

    public static final String MODE_FREE_QUERY = "freequery";
    
    public static final String MODE_BY_PK = "pkquery"; 
    
    String connectionName;
    public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public static ServiceOption createInstance(){
        CompositeMap map = new CompositeMap(20);
        map.setName("query-option");
        ServiceOption option = new ServiceOption();
        option.initialize(map);
        return option;
    }
    
    public ServiceOption(){
        super();
    }
    
    public String getQueryMode(){
        return getString("querymode");
    }
    
    public void setQueryMode( String mode ){
        putString("querymode", mode);
    }
    
    public boolean isFreeQuery(){
        return MODE_FREE_QUERY.equalsIgnoreCase(getQueryMode());
    }
    
    public boolean isPkQuery(){
        return MODE_BY_PK.equalsIgnoreCase(getQueryMode());
    }
    
    public boolean isAutoCount(){
        return getBoolean(KEY_AUTO_COUNT, false);
    }
    
    public void setAutoCount( boolean b){
        putBoolean(KEY_AUTO_COUNT, b);
    }   
}
