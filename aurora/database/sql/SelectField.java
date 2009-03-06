/*
 * Created on 2008-3-28
 */
package aurora.database.sql;

public class SelectField extends BaseField {
    
    public static final String SELECT_LIST = "SELECT_LIST";
    
    SelectStatement     subQuery = null;
    boolean             isDistinct = false;
    boolean             isSubQuery = false;
    
    public SelectField(String any_expression){
        super(SELECT_LIST);
        fieldName = any_expression;
    }

    protected SelectField(ISqlStatement parent, String name) {
        super(SELECT_LIST);
        setParent(parent);
        setFieldName(name);
    }
    
    protected SelectField(ISqlStatement parent, SelectStatement subQuery){
        super(SELECT_LIST);
        setParent(parent);
        setSubQuery(subQuery);
    }

    public String getNameForOperate(){
        if(isSubQuery()) return alias;
        return super.getNameForOperate();
    }
    
    public String getNameForSelect(){
        String name = getNameForOperate();
        if(alias != null)
            return name + " AS " +alias;
        else
            return name;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
        isSubQuery = false;
        if(subQuery!=null){
            subQuery.setParent(null);
            subQuery=null;
        }
    }

    /**
     * @return the isDistinct
     */
    public boolean isDistinct() {
        return isDistinct;
    }

    /**
     * @param isDistinct the isDistinct to set
     */
    public void setDistinct(boolean isDistinct) {
        this.isDistinct = isDistinct;
    }

    /**
     * @return the subQuery
     */
    public SelectStatement getSubQuery() {
        return subQuery;
    }

    /**
     * @param subQuery the subQuery to set
     */
    public void setSubQuery(SelectStatement subQuery) {
        this.subQuery = subQuery;
        subQuery.setParent(this);
        this.fieldName = null;
    }

    /**
     * @return the isSubQuery
     */
    public boolean isSubQuery() {
        return isSubQuery;
    }

}
