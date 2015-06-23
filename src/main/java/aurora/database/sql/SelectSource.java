/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

public class SelectSource extends BaseTable {
    
    public static final String SELECT_FROM = "SELECT_FROM";    
    
    SelectStatement     subQuery;
    boolean             isSubQuery;
    
    public SelectSource(String tableName){
        super(SELECT_FROM);
        setTableName(tableName);
    }
    
    public SelectSource(SelectStatement subQuery){
        super(SELECT_FROM);
        setSubQuery(subQuery);
    }
    
    public void setTableName(String tableName){
        super.setTableName(tableName);
        isSubQuery = false;        
        if(subQuery!=null){
            subQuery.setParent(null);
            subQuery = null;
        }
    }
    
    public String getTableName(){
        if(isSubQuery())
            return subQuery.toString();
        else
            return super.getTableName();
    }    
    
    public void setSubQuery(SelectStatement subQuery){
        isSubQuery = true;
        this.tableName = null;
        this.subQuery = subQuery;
        this.subQuery.setParent(this);
    }
    
    public SelectStatement getSubQuery(){
        return subQuery;
    }
    
    public boolean isSubQuery(){
        return isSubQuery;
    }
    
    protected BaseField createFieldInstance( String name, String alias){
        SelectField field = new SelectField(this, name);
        if(alias!=null)
            field.setAlias(alias);
        return field;
    }
    
    public BaseField getField( String name ){
        if(isSubQuery){
            return subQuery.getField(name);
        }else{
            return super.getField(name);
        }
    }
    
    public SelectField getSelectField( String name ){
        return (SelectField)getField(name);
    }
    
    public SelectField createSelectField( String name, String alias ){
       return (SelectField)createField(name, alias);
    }
    
    public SelectField createSelectField( String name ){
        return (SelectField)createField(name, null);
    }

}
