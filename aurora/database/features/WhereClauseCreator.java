/*
 * Created on 2008-5-28
 */
package aurora.database.features;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import aurora.bm.BusinessModel;
import aurora.bm.DataFilter;
import aurora.bm.Field;
import aurora.bm.QueryField;
import aurora.database.profile.IDatabaseFactory;
import aurora.database.profile.IDatabaseProfile;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.RawSqlService;
import aurora.database.service.ServiceOption;
import aurora.database.sql.ConditionList;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.IWithWhereClause;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectStatement;

public class WhereClauseCreator {
    
    public static final String WHERE_CLAUSE = "#WHERE_CLAUSE#";
    
    IDatabaseFactory mFactory;
    
    public WhereClauseCreator(IDatabaseFactory  fact){
        mFactory = fact;
    }

    static boolean isInAction(String action, String[] actions){
        if(action==null) return true;
        boolean found = false;
        for( int n=0; n<actions.length; n++)
            if(action.equalsIgnoreCase(actions[n])){
               found = true;
               break;
            }
        return found;
    }
    
    public void addDataFilterConditions( String action, ConditionList conditions, DataFilter[] filters){
        //DataFilter[] filters = model.getDataFilters();
        if(filters==null) return ;
        for(int i=0; i<filters.length; i++){
            DataFilter filter = filters[i];
            String[] actions = filter.getEnforceActions();
            if(actions!=null){
                if(!isInAction(action,actions))
                    continue;                
            }
            String exp = filter.getExpression();
            if(exp==null)
                throw new ConfigurationError("Must set 'expression' property for "+filter.getObjectContext().toXML()+" No."+(i+1));
            conditions.addCondition( new RawSqlExpression(exp));
        }
    }

    
    public void addQueryConditions( CompositeMap parameter, SelectStatement select, BusinessModel model  ){
        QueryField[] fields = model.getQueryFieldsArray();

        if(fields==null) return;
        ConditionList where = select.getWhereClause();      
        
        for(int i=0; i<fields.length; i++){
            QueryField qf = fields[i];
            
            String field_name = qf.getField();
            Field field = null;
            // First check if query parameter refers to a existing field
            boolean has_field = true;
            if(field_name!=null){
                field = model.getField(field_name);
                if(field != null)
                    if(field.isReferenceField()){
                    // ##### pending 
                    }
            // If not, the query field is defined as separate field
            }else{
                has_field = false;
                field_name = qf.getName();
                if(field_name==null)
                    throw new ConfigurationError("must set either 'name' or 'field' property for query field: " + qf.getObjectContext().toXML() );
                field = (Field)qf.castTo(Field.class);
            }

            String path = field.getInputPath();
            if( parameter.getObject(path)!=null ){
                if(has_field){
                    SelectField select_field = select.getField(field.getName());
                    qf.addToWhereClause(where, select_field, field.getInputPath());
                }else
                    qf.addToWhereClause(where, "@"+qf.getName());
            }
        }
    }

    public void doPopulateStatement( BusinessModelServiceContext bmsc){
        ISqlStatement s = bmsc.getStatement();
        if( s instanceof IWithWhereClause){
            // Add data filter
            IWithWhereClause statement = (IWithWhereClause)s;
            ConditionList where = statement.getWhereClause();
            BusinessModel model = bmsc.getBusinessModel();
            if(model==null) return;
            String action = bmsc.getAction();
            addDataFilterConditions(action, where, model.getDataFilters());
            // Add data filter from query action config
            ServiceOption option = bmsc.getServiceOption();
            if(option!=null){
                String action_defined_where = option.getDefaultWhereClause();
                if(action_defined_where!=null)
                where.addCondition( new RawSqlExpression(action_defined_where) );
            }
            // Add queriable fields
            if( s instanceof SelectStatement ){
                addQueryConditions( bmsc.getCurrentParameter(), (SelectStatement)s, model);
            }
        }
    }
    
    public void onPopulateUpdateStatement( BusinessModelServiceContext bmsc){
        doPopulateStatement(bmsc);
    }
    
    public void onPopulateDeleteStatement( BusinessModelServiceContext bmsc){
        doPopulateStatement(bmsc);
    }
    
    public void onPopulateQueryStatement( BusinessModelServiceContext bmsc){
        doPopulateStatement(bmsc);
    }        
    
    public void onPopulateQuerySql( BusinessModelServiceContext bmsc, RawSqlService service, StringBuffer sql ){
        //ServiceOption option = bmsc.getServiceOption();
        
        int index = sql.indexOf(WHERE_CLAUSE);
        if(index<0) return;
        SelectStatement select = new SelectStatement();
        ConditionList where = select.getWhereClause();
        addDataFilterConditions(bmsc.getAction(), where, service.asBusinessModel().getDataFilters());
        addQueryConditions( bmsc.getCurrentParameter(), select, service.asBusinessModel()  );
        String db_type = service.getDatabaseType();
        IDatabaseProfile profile = db_type==null?mFactory.getDefaultDatabaseProfile():mFactory.getDatabaseProfile(db_type);
        if(profile==null)
            throw new IllegalArgumentException("Unkown database type:"+db_type);
        String where_clause = profile.getSqlBuilderRegistry().getSql(where);
        if(where_clause==null)
            where_clause = "";
        else{
            where_clause = where_clause.trim();
            if(where_clause.length()>0) where_clause = " WHERE " + where_clause;
        }
        sql.replace(index, index+WHERE_CLAUSE.length(), where_clause);
    }

}
