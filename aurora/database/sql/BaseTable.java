/*
 * Created on 2008-5-23
 */
package aurora.database.sql;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTable extends AbstractStatement implements  IAliasSettable {
    
    String              tableName;
    String              alias;
    Map                 fields;    
    
    public BaseTable( String name ){
        super(name);
    }


    public void setAlias( String alias ){
        this.alias = alias;
    }
    
    public String getAlias(){
        return alias;
    }
    
    public void setTableName(String tableName){
        this.tableName = tableName;
    }
    
    public String getTableName(){
         return tableName;
    }
    
    protected abstract BaseField createFieldInstance( String name, String alias);
    
    public BaseField getField( String name ){
         if(fields==null) return null;
         return (BaseField)fields.get(name);
    }    
    
    public BaseField createField( String name, String alias ){
        BaseField field = getField(name);
        if(field!=null) return field;
        field = createFieldInstance(name, alias);
        if(fields==null)
            fields = new HashMap();
        fields.put(name, field);
        return field;
    }
    
    public BaseField createField( String name ){
        return createField(name,null);
    }
    
    public boolean removeField( String name ){
        if(fields==null) return false;
        String key = name;
        BaseField fld = (BaseField)fields.get(key);
        if(fld==null) return false;
        fld.setParent(null);
        fields.remove(key);
        return true;
    }    

}
