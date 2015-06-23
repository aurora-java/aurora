/*
 * Created on 2008-4-2
 */
package aurora.database.sql;


public class AbstractStatementWithWhere extends AbstractCompsiteStatement implements IWithWhereClause  {
    
    ConditionList    whereClause;
    
    public AbstractStatementWithWhere(String type){
        super(type);
        whereClause = new ConditionList();
        whereClause.setParent(this);
    }    
    
    public ConditionList getWhereClause(){
        return whereClause;
    }    

}
