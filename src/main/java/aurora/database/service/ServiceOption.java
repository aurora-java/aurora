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
    
    public static final String KEY_DEFAULT_WHERE_CLAUSE = "defaultwhereclause";
    
    public static final String KEY_QUERY_ORDER_BY = "queryorderby";
    
    public static final String KEY_CONNECTION_NAME = "connectionname";
    
    public static final String KEY_FIELD_CASE = "fieldcase";    

    public static final String KEY_RECORD_NAME = "recordname";    
    
    public static final String KEY_UPDATE_PASSED_FIELD_ONLY = "updatepassedfieldonly";
    
    public String getConnectionName() {
		return getString(KEY_CONNECTION_NAME);
	}

	public void setConnectionName(String connectionName) {
		putString(KEY_CONNECTION_NAME, connectionName);
	}

	public static ServiceOption createInstance(){
        CompositeMap map = new CompositeMap(20);
        map.setName("service-option");
        ServiceOption option = new ServiceOption();
        option.initialize(map);
        return option;
    }
    
    public ServiceOption(){
        super();
    }
    
    /**
     * Mode of query
     * freeQuery: add where clause for each passed query parameter
     * pkQuery: query one record by primary key
     */
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
    
    /**
     * Only update field that exists in passed parameter
     * for example: in parameter { a=3 }, update sql will be "update t set a=3 where ..."
     * in parameter { a=3,b=4 }, update sql will be "update t set a=3,b=4 where ..."
     */
    public boolean isUpdatePassedFieldOnly(){
        return "true".equalsIgnoreCase(getString(KEY_UPDATE_PASSED_FIELD_ONLY));
    }
    
    /**
     * Whether automatically generate a query sql to get count of all records
     */
    public boolean isAutoCount(){
        return getBoolean(KEY_AUTO_COUNT, false);
    }
    
    public void setAutoCount( boolean b){
        putBoolean(KEY_AUTO_COUNT, b);
    }   
    
    /**
     * Order by expression in query sql
     * @return
     */
    public String getQueryOrderBy(){
        return getString(KEY_QUERY_ORDER_BY);
    }
    
    public void setQueryOrderBy( String order_by ){
        putString(KEY_QUERY_ORDER_BY, order_by);
    }
    
    /**
     * Default where clause in query sql
     * @return
     */
    public String getDefaultWhereClause(){
        return getString(KEY_DEFAULT_WHERE_CLAUSE);
    }
    
    public void setDefaultWhereClause( String where ){
        putString(KEY_DEFAULT_WHERE_CLAUSE, where);
    }
    
    /**
     * Case of table field name in response JSON or XML
     * @return
     */
    public byte getFieldCase(){
        return (byte)getInt(KEY_FIELD_CASE, Character.UNASSIGNED);
    }
    
    public void setFieldCase( byte field_case ){
        putInt(KEY_FIELD_CASE, field_case);
    }
    
    public void setUpdatePassedFieldOnly( boolean b ){
        putBoolean(KEY_UPDATE_PASSED_FIELD_ONLY, b);
    }
    
    public void setRecordName( String record_name ){
        putString(KEY_RECORD_NAME, record_name);
    }
    
    public String getRecordName(){
        return getString(KEY_RECORD_NAME);
    }
}
