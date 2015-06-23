/*
 * Created on 2008-6-30
 */
package aurora.database.features;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.RawSqlService;
import aurora.database.service.ServiceOption;
import aurora.database.sql.ISqlStatement;
import aurora.database.sql.OrderByField;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectStatement;

public class OrderByClauseCreator  implements ISingleton {
    
    public static final String KEY_ORDER_BY = "ORDER BY ";

    public static final String ORDER_FIELD_PARAM_NAME = "_ORDER_FIELD_PARAM_NAME";
    
    public static final String ORDER_TYPE_PARAM_NAME = "_ORDER_TYPE_PARAM_NAME";
    
    public static final String ORDER_FIELD = "ORDER_FIELD";
    
    public static final String ORDER_TYPE = "ORDER_TYPE";
    
    public static final String ORDER_BY_CLAUSE = "#ORDER_BY_CLAUSE#";
    
    static String getField( String param_name_field, String default_field, CompositeMap param ){
        String key = param.getString(param_name_field, default_field );
        return key==null?null:param.getString(key);
    }
    
    public void doPopulateQuerySql( BusinessModelServiceContext bmsc, StringBuffer sql ){
        int index = sql.indexOf(ORDER_BY_CLAUSE);
        if(index<0) return;
        CompositeMap param = bmsc.getCurrentParameter();
        String order_field = getField(ORDER_FIELD_PARAM_NAME, ORDER_FIELD, param);
        String replace = "";
        BusinessModel model = bmsc.getBusinessModel();
        
        if(order_field==null){
            if(model!=null){
                String defaultorderyby=model.getDefaultOrderby();
                if(defaultorderyby!=null)
                    replace = KEY_ORDER_BY + defaultorderyby;
            }
        }else{
            StringBuffer order_by_clause = new StringBuffer(KEY_ORDER_BY);
            order_by_clause.append(order_field);
            String order_type = getField(ORDER_TYPE_PARAM_NAME, ORDER_TYPE, param);
            if(order_type!=null){
                if(OrderByField.ASCENT.equalsIgnoreCase(order_type))
                    order_by_clause.append(' ').append(order_type);
                else if(OrderByField.DESCENT.equalsIgnoreCase(order_type))
                    order_by_clause.append(' ').append(order_type);
            }
            replace = order_by_clause.toString();
        }
        sql.replace(index, index+ORDER_BY_CLAUSE.length(), replace);
    }
    
    // for raw sql
    public void onPopulateQuerySql( BusinessModelServiceContext bmsc, RawSqlService service, StringBuffer sql ){
        doPopulateQuerySql(bmsc, sql);
    }
    
    public void onPopulateOperationSql(BusinessModelServiceContext bmsc, StringBuffer sql){
        doPopulateQuerySql(bmsc, sql);
    }

    // For model based auto-generated sql    
    public void onPopulateQueryStatement( BusinessModelServiceContext bmsc){        
        ISqlStatement s = bmsc.getStatement();
        CompositeMap param = bmsc.getCurrentParameter();
        if( s instanceof SelectStatement ){
            SelectStatement select = (SelectStatement)s;
            String order_by = null;
            ServiceOption option = bmsc.getServiceOption();
            if(option!=null)
                order_by = option.getQueryOrderBy();
            if(order_by!=null){
            	if(order_by.indexOf("$")!=-1){
            		order_by = uncertain.composite.TextParser.parse(order_by,bmsc.getModel());        		
            	}
                if(!"".equals(order_by))select.addOrderByField(new RawSqlExpression(order_by));
            }else{
                String order_field = getField(ORDER_FIELD_PARAM_NAME, ORDER_FIELD, param);
                BusinessModel model = bmsc.getBusinessModel();
                
                if(order_field!=null){
                    Field model_field = model.getField(order_field); 
                    if( model_field!=null){
                        String order_type = getField(ORDER_TYPE_PARAM_NAME, ORDER_TYPE, param);
                        SelectField field = select.getField(order_field);
                        if(field!=null)
                            select.addOrderByField(field, order_type);
                        else{
                            String qe = model_field.getQueryExpression(); 
                            if(qe==null)
                                qe = model_field.getExpression();                             
                            if(qe==null)
                                qe = model_field.getPhysicalName();
                            select.addOrderByField( new RawSqlExpression(qe), order_type);
                        }
                    }
                }else{
                	 String defaultorderyby=model.getDefaultOrderby();
                    // model.getDefaultOrderBy();
                     if (defaultorderyby!=null)
                     select.addOrderByField(new RawSqlExpression(defaultorderyby));
                }
            }
        }
    }

}
