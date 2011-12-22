/*
 * Created on 2010-5-13 下午03:45:34
 * $Id$
 */
package aurora.database.features;

import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.profile.IDatabaseProfile;

/**
 *  Implements "Standard who feature": created_by, creation_date, last_updated_by, last_updated_date 
 */
public class StandardWho {
    
    public static final String KEY_STANDARD_WHO_ADDED = "_standard_who_added";
    String  userIdPath = "/session/@user_id";
    String  createdByField = "CREATED_BY";
    String  creationDateField = "CREATION_DATE";
    String  lastUpdatedByField = "LAST_UPDATED_BY";
    boolean  forQuery=false;
    
    public boolean getForQuery() {
		return forQuery;
	}

	public void setForQuery(boolean forQuery) {
		this.forQuery = forQuery;
	}

	public String getCreatedByField() {
		return createdByField;
	}

	public void setCreatedByField(String createdByField) {
		this.createdByField = createdByField;
	}

	public String getCreationDateField() {
		return creationDateField;
	}

	public void setCreationDateField(String creationDateField) {
		this.creationDateField = creationDateField;
	}

	public String getLastUpdatedByField() {
		return lastUpdatedByField;
	}

	public void setLastUpdatedByField(String lastUpdatedByField) {
		this.lastUpdatedByField = lastUpdatedByField;
	}

	public String getLastUpdatedDateField() {
		return lastUpdatedDateField;
	}

	public void setLastUpdatedDateField(String lastUpdatedDateField) {
		this.lastUpdatedDateField = lastUpdatedDateField;
	}

	String  lastUpdatedDateField = "LAST_UPDATED_DATE";
    
    public static Field CREATED_BY = Field.createField("CREATED_BY");
    public static Field CREATION_DATE = Field.createField("CREATION_DATE");
    public static Field LAST_UPDATED_BY = Field.createField("LAST_UPDATED_BY");
    public static Field LAST_UPDATED_DATE = Field.createField("LAST_UPDATED_DATE"); 
    static {
        CREATED_BY.setDataType(Long.class.getName());
        CREATED_BY.setForUpdate(false);
        CREATED_BY.setForInsert(true);
        CREATED_BY.setForSelect(false);
        
        CREATION_DATE.setDataType(java.sql.Date.class.getName());
        CREATION_DATE.setForUpdate(false);
        CREATION_DATE.setForInsert(true);
        CREATION_DATE.setForSelect(false);
        
        LAST_UPDATED_BY.setDataType(Long.class.getName());
        LAST_UPDATED_BY.setForUpdate(true);
        LAST_UPDATED_BY.setForInsert(true);
        LAST_UPDATED_BY.setForSelect(false);
        LAST_UPDATED_BY.setForceUpdate(true);
        
        LAST_UPDATED_DATE.setDataType(java.sql.Date.class.getName());
        LAST_UPDATED_DATE.setForUpdate(true);
        LAST_UPDATED_DATE.setForInsert(true);
        LAST_UPDATED_DATE.setForSelect(false);
        LAST_UPDATED_DATE.setForceUpdate(true);
    }
    
    IDatabaseFactory mFactory;
    
    String getGlobalOption( String key, String default_value ){
        String value = (String)mFactory.getProperty(key);
        return value==null?default_value:value;
    }

    public StandardWho(IDatabaseFactory fact){
        mFactory = fact;
        userIdPath = getGlobalOption("user_id_path", userIdPath);
        createdByField = getGlobalOption("created_by_field", createdByField);
        creationDateField = getGlobalOption("creation_date_field", creationDateField);
        lastUpdatedByField = getGlobalOption("last_updated_by_field", lastUpdatedByField);
        lastUpdatedDateField = getGlobalOption("last_updated_date_field", lastUpdatedDateField);
    }
    
    public void onPrepareBusinessModel( BusinessModel model ){
    	boolean forSelect=false;
    	if(getForQuery()){
    		forSelect=true;
    	}
        Boolean standard_who_added = model.getObjectContext().getBoolean(KEY_STANDARD_WHO_ADDED);
        if(standard_who_added!=null)
            return;
        
        IDatabaseProfile profile = model.getDatabaseProfile(mFactory);
        String sysdate = profile.getKeyword(IDatabaseProfile.KEY_CURRENT_TIME);
        
        Field created_by = CREATED_BY.createCopy();
        created_by.setForSelect(forSelect);
        created_by.setParameterPath(userIdPath);
        created_by.setName(createdByField);
        model.addField(created_by);
        
        Field creation_date = CREATION_DATE.createCopy();
        creation_date.setForSelect(forSelect);
        creation_date.setInsertExpression(sysdate);
        creation_date.setName(creationDateField);
        model.addField(creation_date);
        
        Field last_updated_by = LAST_UPDATED_BY.createCopy();
        last_updated_by.setForSelect(forSelect);
        last_updated_by.setParameterPath(userIdPath);
        last_updated_by.setName(lastUpdatedByField);
        model.addField(last_updated_by);
        
        Field last_updated_date = LAST_UPDATED_DATE.createCopy();
        last_updated_date.setForSelect(forSelect);
        last_updated_date.setInsertExpression(sysdate);
        last_updated_date.setUpdateExpression(sysdate);
        last_updated_date.setName(lastUpdatedDateField);
        model.addField(last_updated_date);
        
        // do remember to call this!
        model.makeReady();
        
        model.getObjectContext().putBoolean(KEY_STANDARD_WHO_ADDED, true);
    }

}
