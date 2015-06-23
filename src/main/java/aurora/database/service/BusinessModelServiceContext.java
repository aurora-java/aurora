/*
 * Created on 2008-5-20
 */
package aurora.database.service;

import uncertain.composite.CompositeMap;
import aurora.bm.BusinessModel;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.SqlRunner;
import aurora.database.sql.ISqlStatement;

public class BusinessModelServiceContext extends SqlServiceContext {
    
    public static final String KEY_BUSINESS_MODEL       = "BusinessModel";  

    public static final String KEY_STATEMENT     = "SqlStatement";

    public static final String KEY_OPERATION = "BusinessModelOperation";
    
    public static final String KEY_SQL_RUNNER = "SqlRunner";
    
    public static final String KEY_STATEMENT_TYPE = "SqlStatementType";
    
    public static final String KEY_SQL_GENERATION_MODE = "SqlGenerationMode";
    
    public static BusinessModelServiceContext getContextInstance( CompositeMap map ){
        BusinessModelServiceContext context = new BusinessModelServiceContext();
        context.initialize(map);
        return context;
    }    

    public BusinessModel getBusinessModel(){
        return (BusinessModel)get(KEY_BUSINESS_MODEL);
    }
    
    public void setBusinessModel(BusinessModel model){
        put(KEY_BUSINESS_MODEL, model);
    }

    public ISqlStatement getStatement(){
        return (ISqlStatement)get(KEY_STATEMENT);
    }
    
    public void setStatement(ISqlStatement statement){
        put(KEY_STATEMENT, statement);
    }
    
    public SqlRunner getSqlRunner(){
        return (SqlRunner)get(KEY_SQL_RUNNER);
    }
    
    public void setSqlRunner(SqlRunner runner){
        put(KEY_SQL_RUNNER, runner);
    }
    
    public String getOperation(){
        return getString(KEY_OPERATION);
    }
    
    public void setOperation(String action){
        putString(KEY_OPERATION, action);
    }
    
    public String getStatementType(){
        return getString(KEY_STATEMENT_TYPE);
    }
    
    public void setStatementType(String type){
        putString(KEY_STATEMENT_TYPE, type);
    }
    
    public void setSqlGenerationMode( String mode ){
        putString(KEY_SQL_GENERATION_MODE, mode);
    }
    
    public String getSqlGenerationMode(){
        return getString(KEY_SQL_GENERATION_MODE);
    }

    
    /**
     * Clear previous set local variables in service context
     */
    public void prepareForRun(){
        setSqlRunner(null);
        setSqlString(null);
        setBusinessModel(null);
        setSqlGenerationMode(null);
        setStatementType(null);
        setStatement(null);
    }    
}
