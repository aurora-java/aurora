/*
 * Created on 2008-4-14
 */
package aurora.database.profile;

import java.util.HashMap;
import java.util.Map;

import aurora.database.sql.CompareExpression;
import aurora.database.sql.ComplexExpression;
import aurora.database.sql.CompositeStatement;
import aurora.database.sql.ConditionList;
import aurora.database.sql.DeleteStatement;
import aurora.database.sql.ExistsClause;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.InsertStatement;
import aurora.database.sql.Join;
import aurora.database.sql.OracleJoinExpression;
import aurora.database.sql.OrderByField;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectSource;
import aurora.database.sql.SelectStatement;
import aurora.database.sql.UpdateField;
import aurora.database.sql.UpdateStatement;
import aurora.database.sql.UpdateTarget;
import aurora.database.sql.builder.ConditionListBuilder;
import aurora.database.sql.builder.DefaultCompositeBuilder;
import aurora.database.sql.builder.DefaultDeleteBuilder;
import aurora.database.sql.builder.DefaultInsertBuilder;
import aurora.database.sql.builder.DefaultJoinBuilder;
import aurora.database.sql.builder.DefaultSelectBuilder;
import aurora.database.sql.builder.DefaultUpdateBuilder;

public class SqlBuilderRegistry implements ISqlBuilderRegistry {
    
    IDatabaseProfile    databaseProfile;
    // Class of statement  -> ISqlBuilder
    Map                 sqlBuilderMap = new HashMap();
    // Class of ISqlBuilder -> ISqlBuilder instance
    Map                 sqlBuilderTypeMap = new HashMap();
    ISqlBuilderRegistry parent;
    
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
        registerSqlBuilder(OracleJoinExpression.class, builder);
        
        builder = new DefaultUpdateBuilder();
        registerSqlBuilder(UpdateStatement.class, builder);
        registerSqlBuilder(UpdateTarget.class, builder);
        registerSqlBuilder(UpdateField.class, builder);
        
        builder = new DefaultInsertBuilder();
        registerSqlBuilder( InsertStatement.class, builder );
        
        builder = new DefaultDeleteBuilder();
        registerSqlBuilder( DeleteStatement.class, builder );
        
        builder = new DefaultCompositeBuilder();
        registerSqlBuilder(CompositeStatement.class, builder);
        
        builder = new DefaultJoinBuilder();
        registerSqlBuilder(Join.class, builder);
    }
    
    public SqlBuilderRegistry(){
        //databaseProfile = new DatabaseProfile("GeneralSQL92");
        loadDefaultBuilders();
    }
    
    public SqlBuilderRegistry(IDatabaseProfile databaseProfile){
        this.databaseProfile = databaseProfile;
        loadDefaultBuilders();
    }
    
    public void setDatabaseProfile(IDatabaseProfile database_profile){
        this.databaseProfile = database_profile;
    }
    
    public IDatabaseProfile getDatabaseProfile(){
        return databaseProfile;
    }
    
    public ISqlBuilder  getBuilder( ISqlStatement   statement ){
        ISqlBuilder builder =  (ISqlBuilder)sqlBuilderMap.get(statement.getClass());
        if(builder==null&&parent!=null)
            builder = parent.getBuilder(statement);
        return builder;
    }
    
    public void registerSqlBuilder( Class statement_type, ISqlBuilder sql_builder ){
        if(sql_builder.getRegistry()!=this)
            sql_builder.setRegistry(this);
        sqlBuilderMap.put(statement_type, sql_builder);
        sqlBuilderTypeMap.put(sql_builder.getClass(), sql_builder);
    }
    
    public ISqlBuilder getSqlBuilderByType( Class builder_type ){
        return (ISqlBuilder)sqlBuilderTypeMap.get(builder_type);
    }
    
    public String getSql( ISqlStatement statement ){
        ISqlBuilder builder = getBuilder(statement);
        if(builder!=null)
            return builder.createSql(statement);
        else
            return null;
    }

    public ISqlBuilderRegistry getParent() {
        return parent;
    }

    public void setParent(ISqlBuilderRegistry parent) {
        assert parent!=this;
        this.parent = parent;
    }

}
