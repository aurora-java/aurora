/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

public abstract class AbstractStatement implements ISqlStatement{

    ISqlStatement      parent;
    String          type;
    
    public AbstractStatement(String type){
        setType(type);
    }
    
    public String   getType(){
        return type;    
    }
    
    protected void setType(String type){
        this.type = type;
    }

    public ISqlStatement   getParent(){
        return parent;
    }
    
    public void setParent(ISqlStatement parent){
        this.parent = parent;
    }
    /*
    public String toSql( ISqlBuilder creator ){
        if( this instanceof ISimpleSqlText)
            return toString();
        else
            return creator.createSql(this);
    }
    */
    
}
