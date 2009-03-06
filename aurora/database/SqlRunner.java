/*
 * Created on 2007-10-31
 */
package aurora.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeAccessor;
import uncertain.composite.CompositeMap;
import uncertain.composite.ICompositeAccessor;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceContext;

public class SqlRunner {
    
    boolean                 trace = false;
    boolean                 use_batch_update = true;  
    ParsedSql               statement;
    //ISQLExceptionHandle     exception_handle; 
    ICompositeAccessor      parameter_accessor = CompositeAccessor.defaultInstance();

    SqlServiceContext       context;    
    Connection              conn = null;
    
    List                    bind_param_list;
    
    
    static void setException(ServiceContext context, Throwable ex){
        context.setException(ex);
        context.setSuccess(false);
    }    
    
    public static void fireUpdateFail( SqlServiceContext context, Throwable thr ){
        setException(context, thr);
        context.fireEvent("UpdateFail", new Object[]{context});        
    }

    public static void fireUpdateRecordFail(SqlServiceContext context, CompositeMap record, Throwable thr){
        //context.setCurrentFailedRecord(record);
        if(thr!=null)
            context.setException(thr);
        context.fireEvent("UpdateRecordFail", new Object[]{context});
    }
    
    public static Collection getSourceParameter( CompositeMap context, String path ){
        if(path==null) return context.getChilds();
        Object obj = context.getObject(path);
        if(obj==null) return null;
        if( obj instanceof CompositeMap ){
            return ((CompositeMap)obj).getChilds();
        }else if ( obj instanceof Collection ){
            return ((Collection)obj);
        }else
            return null;
    }
    
    
    public SqlRunner(SqlServiceContext context){
        setSqlServiceContext(context);
        ICompositeAccessor accessor = context.getCompositeAccessor();
        if(accessor!=null)
            parameter_accessor = accessor;
    }

    public SqlRunner(SqlServiceContext context, ParsedSql statement){
        this(context);
        this.statement = statement;
    }
    
    public void setSqlServiceContext(SqlServiceContext context){
        this.context = context;
        setTrace(context.isTrace());
    }
    
    void  prepareConnection(){
        conn = context.getConnection();
        if(conn==null) throw new IllegalStateException("No java.sql.Connection set in service context");
    }
    
    public String generateSQL( CompositeMap param ){
        if(statement.isStaticStatement()) return statement.getParsedSQL();
        StringBuffer buf = new StringBuffer( statement.getParsedSQL());
        int offset = 0;
        Iterator it = statement.getBindParameters().iterator();
        while(it.hasNext()){
            BindParameter p = (BindParameter)it.next();
            if(p.is_sql_statement){
                Object obj = parameter_accessor.get(param, p.input_path);
                if(obj!=null){
                    String sql_part = obj.toString();
                    buf.insert(p.position+offset, sql_part);
                    offset+=sql_part.length();
                }
            }
        }
        return buf.toString();
    }
    
    void addBindDescriptor( BindDescriptor desc ){
        if(bind_param_list==null)
            bind_param_list = new LinkedList();
        bind_param_list.add(desc);
    }
    
    public List getBindDescriptors(){
        return bind_param_list;
    }
    
    public String getBindDescription(){
        if(bind_param_list==null) return null;
        StringBuffer buf = new StringBuffer();
        Iterator it = bind_param_list.iterator();
        while(it.hasNext()){
            BindDescriptor bdesc = (BindDescriptor)it.next();
            buf.append(bdesc.toString()).append("\r\n");            
        }
        return buf.toString();
    }
    
    public void bindParameters( PreparedStatement ps, CompositeMap param )
        throws SQLException
    {
        if(trace && bind_param_list!=null )
            bind_param_list.clear();
        
        int index = 1;
        Iterator it = statement.getBindParameters().iterator();
        BindDescriptor bdesc = null;
        while(it.hasNext()){
            BindParameter p = (BindParameter)it.next();
            if(!p.is_sql_statement ){
                if(trace){
                    bdesc = new BindDescriptor(p.input_path, null, index );
                }
                if(p.is_input){
                    Object value = parameter_accessor.get(param, p.input_path);
                    p.setStatement(index, ps, value);
                    if(trace){
                        bdesc.setValue(value);
                    }
                }
                if(p.is_output){
                    CallableStatement cs = (CallableStatement)ps;
                    if(p.database_type_name==null){
                        //if(trace) System.out.println("registering out parameter for "+p.getPath()+" of type "+p.data_type.getJavaType().getName());
                        cs.registerOutParameter(index, p.data_type.getSqlType());
                    }
                    else
                        cs.registerOutParameter(index, p.data_type.getSqlType(), p.database_type_name);
                    if( trace ){
                        bdesc.setOutput(true);
                        bdesc.setDatabaseType(p.database_type_name);
                    }
                }
                index++;
                if( trace ){
                    addBindDescriptor(bdesc);
                }
            }
        }        
    }
    
    public void fetchOutputParameters( CallableStatement cs, CompositeMap param)
        throws SQLException
    {
        int index = 1;
        Iterator it = statement.getBindParameters().iterator();
        while(it.hasNext()){
            BindParameter p = (BindParameter)it.next();
            if(!p.is_sql_statement && p.is_output ){
                Object value = p.data_type.getObject(cs, index);
                parameter_accessor.put(param, p.output_path, value);
            }
            index++;
        }
    }
    
    public ResultSet query( CompositeMap param )
        throws SQLException
    {
            prepareConnection();
            String sql = generateSQL(param);
            PreparedStatement ps = conn.prepareCall(sql);
            bindParameters(ps, param);
            ResultSet rs = ps.executeQuery();
            return rs;
    }
    
    void setException(Throwable ex){
        context.setException(ex);
        context.setSuccess(false);
    }
    
    protected int updateInternal( CompositeMap param, CompositeMap output )
        throws SQLException
    {
        prepareConnection();            
        String sql = generateSQL(param);            
        PreparedStatement ps = null;
        try{
            if(statement.hasOutputParameter())
                ps = conn.prepareCall(sql);
            else
                ps = conn.prepareStatement(sql);
            bindParameters(ps, param);
            int result = ps.executeUpdate();
            if(statement.hasOutputParameter())
                fetchOutputParameters((CallableStatement)ps, output);
            return result;
        }finally{
            if(ps!=null)
                DBUtil.closeStatement(ps);
        }
    }    
    
    /** Execute update statement
     *  @param param  A CompositeMap instance containing input parameter
     *  @param output A CompositeMap instance to put output values 
     *  @return rows affected by this update
     */
    public int update( CompositeMap param, CompositeMap output )
        throws SQLException
    {
        return updateInternal(param, output);
        /*
        try{
           return updateInternal(param, output);
        }catch(SQLException ex){
            fireUpdateFail(context, ex);
            return 0;
        }
        */
    }
    
    /**
     * @see update(CompositeMap,CompositeMap)
     * @param param A CompositeMap instance containing input parameter
     * @return rows affected by this update
     * @throws SQLException
     */
    public int update( CompositeMap param )
        throws SQLException
    {
        return update( param, param );
    }
    
    public boolean updateList( Collection param_list ) 
        throws SQLException
    {
        prepareConnection(); 
        boolean success = true;
        //context.setCurrentFailedRecord(null);
        //context.setException(null);        
        Iterator it = param_list.iterator();
        while(it.hasNext()){
            CompositeMap param = (CompositeMap)it.next();
            context.setCurrentParameter(param);
            updateInternal( param, param );
            /*
            try{
                updateInternal( param, param );
            }catch(SQLException ex){
                success = false;
                context.put("service_continue_batch", new Boolean(false));
                fireUpdateRecordFail(context, param, ex);
                if(!context.getBoolean("service_continue_batch", false))
                    break;
            }
            */
        }
        context.setSuccess(success);
        return success;        
        
    }
    
    public boolean updateListUsingBatch( Collection param_list )
        throws SQLException
    {
        boolean success = true;
        
        prepareConnection(); 
        if(!statement.isStaticStatement())
            throw new IllegalStateException("Can't do batch update with sql statement that contains dynamic part");
        String sql = statement.getParsedSQL();
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(sql);        
            Iterator it = param_list.iterator();
            while(it.hasNext()){
                CompositeMap param = (CompositeMap)it.next();
                bindParameters(ps, param);
                ps.addBatch();
            }
            ps.executeUpdate();
            /*
            try{    
            }catch(Throwable thr){
                fireUpdateFail(context, thr);
                context.fireEvent("UpdateFail", new Object[]{context});
                success = false;
            }
            */
            return success;
        }finally{
            DBUtil.closeStatement(ps);
        }
    }
   

    /**
     * @return the statement
     */
    public ParsedSql getStatement() {
        return statement;
    }
    

    /**
     * @param statement the statement to set
     */
    public void setStatement(ParsedSql statement) {
        this.statement = statement;
    }

    /**
     * @return the trace
     */
    public boolean isTrace() {
        return trace;
    }

    /**
     * @param trace the trace to set
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    /**
     * @return the context
     */
    public SqlServiceContext getSqlServiceContext() {
        return context;
    }
    

}
