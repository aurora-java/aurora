/*
 * Created on 2008-4-2
 */
package aurora.database.sql;

import aurora.database.DatabaseConstant;
import aurora.database.profile.ISqlBuilderRegistry;

public class OrderByField extends AbstractStatement {
    
    public static final String ASCENT = DatabaseConstant.ASCENT;
    public static final String DESCENT = DatabaseConstant.DESCENT;
    
    ISqlStatement       field;
    String              order;

    public static final String ORDER_BY_FIELD = "ORDER_BY_FIELD";

    public OrderByField(ISqlStatement   field, String order) {
        super(ORDER_BY_FIELD);
        setField(field);
        setOrder(order);
    }
    
    public OrderByField(ISqlStatement field){
        this(field,null);
    }

    /**
     * @return the field
     */
    public ISqlStatement getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(ISqlStatement field) {
        this.field = field;
    }

    /**
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(String order) {
        this.order = order;
    }
    
    public String toSql( ISqlBuilderRegistry registry){
        StringBuffer buf = new StringBuffer();
        buf.append(registry.getSql(field));
        if(order!=null) buf.append(" ").append(order);
        return buf.toString();
    }

}
