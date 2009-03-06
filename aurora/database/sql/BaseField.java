/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

public abstract class BaseField extends AbstractStatement implements IAliasSettable {

    String              alias;
    String              fieldName;
    
    public BaseField(String name){
        super(name);
    }

    public void setAlias( String alias ){
        this.alias = alias;
    }
    
    public String getAlias(){
        return alias;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    

    public String getNameForOperate(){
        String parent_alias = null;
        if(parent!=null)
            if(parent instanceof IAliasSettable){
                parent_alias = ((IAliasSettable)parent).getAlias();
            }
        if(parent_alias!=null)
            return parent_alias + '.' + fieldName;
        else
            return fieldName;
    }        

}
