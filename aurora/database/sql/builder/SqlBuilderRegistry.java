/*
 * Created on 2008-4-14
 */
package aurora.database.sql.builder;

import java.util.HashMap;
import java.util.Map;

import aurora.database.sql.CompareExpression;
import aurora.database.sql.ComplexExpression;
import aurora.database.sql.ConditionList;
import aurora.database.sql.DeleteStatement;
import aurora.database.sql.ExistsClause;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertStatement;
import aurora.database.sql.OrderByField;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectSource;
import aurora.database.sql.SelectStatement;
import aurora.database.sql.UpdateField;
import aurora.database.sql.UpdateStatement;
import aurora.database.sql.UpdateTarget;

public class SqlBuilderRegistry implements ISqlBuilderRegistry {
    
    IDatabaseProfile    databaseProfile;
    Map                 sqlBuilderMap = new HashMap();
    
    protected void loadDefaultBuilders(){
        
        ISqlBuilder builder = new DefaultSelectBuilder();
        registerSqlBuilder(SelectStatement.class, builder );
        registerSqlBuilder(SelectSource.class, builder );
        registerSqlBuilder(SelectField.class, builder );
        registerSqlBuilder(OrderByField.class, builder );
        
        builder = new ConditionListBuilder();
        registerSqlBuilder(ConditionList.class, builder );
        registerSqlBuilder(CompareExpression.class, builder );
        registerSqlBuilder(ExistsClause.class, builder );
        registerSqlBuilder(ComplexExpression.class, builder );
        registerSqlBuilder(RawSqlExpression.class, builder );
        
        builder = new DefaultUpdateBuilder();
        registerSqlBuilder(UpdateStatement.class, builder);
        registerSqlBuilder(UpdateTarget.class, builder);
        registerSqlBuilder(UpdateField.class, builder);
        
        builder = new DefaultInsertBuilder();
        registerSqlBuilder( InsertStatement.class, builder );
        
        builder = new DefaultDeleteBuilder();
        registerSqlBuilder( DeleteStatement.class, builder );
    }
    
    public SqlBuilderRegistry(){
        databaseProfile = new DefaultDatabaseProfile("GeneralSQL92");
        loadDefaultBuilders();
    }
    
    public SqlBuilderRegistry(IDatabaseProfile databaseProfile){
        this.databaseProfile = databaseProfile;
        loadDefaultBuilders();
    }
    
    public void setDatabaseProfile(IDatabaseProfile database_profile){
        this.databaseProfile = database_profile;
    }
    
    public IDatabaseProfile getDatabaseProflie(){
        return databaseProfile;
    }
    
    public ISqlBuilder  getBuilder( ISqlStatement   statement ){
        return (ISqlBuilder)sqlBuilderMap.get(statement.getClass());
    }
    
    public void registerSqlBuilder( Class statement_type, ISqlBuilder sql_builder ){
        sql_builder.setRegistry(this);
        sqlBuilderMap.put(statement_type, sql_builder);
    }
    
    public String getSql( ISqlStatement statement ){
        ISqlBuilder builder = getBuilder(statement);
        if(builder!=null)
            return builder.createSql(statement);
        else
            return null;
    }

}
