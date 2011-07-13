/*
 * Created on 2007-11-1
 */
package aurora.database.service;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.event.Configuration;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.database.DatabaseConstant;
import aurora.database.DBUtil;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.ParsedSql;
import aurora.database.ResultSetLoader;
import aurora.database.SqlRunner;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.service.ServiceContext;
import aurora.service.validation.Parameter;
import aurora.service.validation.ParameterListIterator;
import aurora.service.validation.ParameterParser;
import aurora.service.validation.ValidationException;

public class RawSqlService implements IConfigurable

{
    public static final String UPDATE = "update";
    
    public static final String QUERY = "query";
    
    public static final FetchDescriptor DEFAULT_FETCH_DESCRIPTOR = new FetchDescriptor();

    // service parameter in list of aurora.service.validation.Parameter
    Collection          mParamList;
    // sql string 
    StringBuffer        mSql;
    // type of sql: query or update
    String              mType;
    // Configuration to hold default feature instances
    Configuration       mConfiguration;
    // type of database
    String              mDatabaseType;
    // trace flag
    boolean             mTrace;        
    ResultSetLoader     mRsLoader = new ResultSetLoader();
    BusinessModel       mModel;
    OCManager           mOcManager;
    String 				dataSourceName;
    //CompositeMap        mConfigMap;
    
    public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	protected RawSqlService( OCManager ocManager ){
        this.mOcManager = ocManager;
    }

    /**
     * @return the type
     */
    public String getType() {
        return mType;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.mType = type;
    }
    
    public boolean isQuery(){
        return QUERY.equalsIgnoreCase(mType);
    }
    
    public boolean isUpdate(){
        return UPDATE.equalsIgnoreCase(mType);
    }
/*
    public Collection getParameters(){
        return params_list;
    }
 */   
    public void addParameters(CompositeMap params){
        //param_map = params;
        Iterator it = params.getChildIterator();
        if(it==null) return;
        if(mParamList!=null) mParamList.clear();
        mParamList = new LinkedList();
        while(it.hasNext()){
            CompositeMap map = (CompositeMap)it.next();
            Parameter param = new Parameter();
            mOcManager.populateObject(map, param);
            mParamList.add(param);
        }        
    }
    
    void setSqlString( String sql ){
        if(mSql==null)
            mSql = new StringBuffer(sql);
        else{
            mSql.setLength(0);
            mSql.append(sql);
        }
    }
    
    public void addSql(CompositeMap m){
        setSqlString(m.getText());
    }   
    
    public void addUpdate(CompositeMap m){
        setSqlString(m.getText());
        this.mType = UPDATE;
    }
    
    public void addQuery(CompositeMap m){
        setSqlString(m.getText());
        this.mType = QUERY;
    }
    
    public void parseParameter( SqlServiceContext context )
        throws ValidationException
    {
        CompositeMap parameter = context.getCurrentParameter();
        boolean parsed = parameter.getBoolean(ServiceContext.KEY_PARAMETER_PARSED, false);
        if(!parsed){
            ParameterParser parser = ParameterParser.getInstance();
            List desc = null;
            if(mParamList!=null)
                desc = parser.parse( parameter,  new ParameterListIterator(mParamList.iterator()) );
            if(desc!=null){ 
                ValidationException exp = new ValidationException(parameter, desc);
                throw exp;
            }
            if(mModel.getQueryFieldsList()!=null){
                BusinessModel.QueryFieldIterator it = mModel.new QueryFieldIterator();
                desc = parser.parse( parameter, it );
                if(desc!=null){ 
                    ValidationException exp = new ValidationException(parameter, desc);
                    throw exp;
                }            
            }
            parameter.putBoolean(ServiceContext.KEY_PARAMETER_PARSED, true);
        }

    }    

    ParsedSql createStatement( String sql ){
        ParsedSql stmt = new ParsedSql();
        if(mParamList!=null)
            stmt.defineParameters(mParamList);
        
        stmt.parse(sql);
        return stmt;
    }
    
    ParsedSql createStatement(){
        return createStatement(mSql.toString());
    }
    
    void printTraceInfo(String type, SqlRunner runner, long exec_time ){
        //if(!getTrace()) return;
        ILogger logger = LoggingContext.getLogger(runner.getSqlServiceContext().getObjectContext(), DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
        DBUtil.printTraceInfo( type, logger, runner);
        logger.log(Level.CONFIG, "Execution time:{0}", new Object[]{new Long(exec_time)} );
    }
    
    SqlRunner createRunner( StringBuffer sql, SqlServiceContext context ){
        ParsedSql stmt = createStatement(sql.toString()); 
        SqlRunner runner = new SqlRunner(context, stmt);
        runner.setConnectionName(dataSourceName);
        runner.setTrace(getTrace());
        return runner;        
    }
    
    public SqlRunner createRunner( SqlServiceContext context ){
        return createRunner(mSql, context);
    }
    
    public CompositeMap queryAsMap( SqlServiceContext context, FetchDescriptor desc  )
        throws Exception
    {
        CompositeMap result = new CompositeMap("result");
        CompositeMapCreator compositeCreator = new CompositeMapCreator(result);
        query(context, compositeCreator, desc);
        return result;
    }
    
    /*
    public CompositeMap queryOneRecord( SqlServiceContext context )
        throws Exception
    {
        return null;
    }
    */
    
    public void query(SqlServiceContext context, IResultSetConsumer consumer, FetchDescriptor desc )
        throws Exception
    {
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), DatabaseConstant.AURORA_DATABASE_LOGGING_TOPIC);
        parseParameter(context);
        mConfiguration.fireEvent("PopulateQuerySql", context.getObjectContext(), new Object[]{ this, mSql} );
        SqlRunner runner = createRunner(mSql, context);       
        context.setSqlString( mSql );
        ResultSet rs = null;
        long exec_time = 0;
        try{
            long tick = System.currentTimeMillis();            
            rs = runner.query(context.getCurrentParameter());
            logger.config("query execute time:"+(System.currentTimeMillis()-tick));
            if(rs!=null){
                if(mModel!=null && mModel.getFields()!=null){
                    mRsLoader.loadByConfig( rs, desc, mModel, consumer );
                }
                else
                    mRsLoader.loadByResultSet( rs, desc, consumer );                
            }
            exec_time = System.currentTimeMillis() - tick;
            mConfiguration.fireEvent("QueryFinish", context.getObjectContext(), null );
        }finally{
            DBUtil.closeResultSet(rs);
            printTraceInfo(QUERY, runner, exec_time);
        } 
    }
    
    public void update(SqlServiceContext context)
        throws Exception
    {        
        parseParameter(context);
        mConfiguration.fireEvent("PopulateQuerySql", context.getObjectContext(), new Object[]{ this, mSql} );
        SqlRunner runner = createRunner(context);        
        try{
            runner.update(context.getCurrentParameter());
        } finally{
            printTraceInfo(UPDATE, runner, runner.getLastExecutionTime());
        }
    }
    
    public boolean updateBatch(SqlServiceContext context, Collection params )
        throws Exception
    {
        parseParameter(context);
        mConfiguration.fireEvent("PopulateQuerySql", context.getObjectContext(), new Object[]{ this, mSql} );
        SqlRunner runner = createRunner(context);
        try{
            return runner.updateList(params);
        }finally{
            printTraceInfo(UPDATE,runner, runner.getLastExecutionTime());
        }
    }

    /**
     * @return the trace
     */
    public boolean getTrace() {
        return mTrace;
    }

    /**
     * @param trace the trace to set
     */
    public void setTrace(boolean trace) {
        this.mTrace = trace;
    } 
    
    public void beginConfigure(CompositeMap config){
        mModel = (BusinessModel)DynamicObject.cast(config, BusinessModel.class);
        mModel.makeReady();
    }

    public void endConfigure(){
        
    }
    
    public BusinessModel asBusinessModel(){
        return mModel;
    }

    public ResultSetLoader getResultSetLoader() {
        return mRsLoader;
    }

    public void setResultSetLoader(ResultSetLoader rsLoader) {
        mRsLoader = rsLoader;
    }

    public String getDatabaseType() {
        return mDatabaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.mDatabaseType = databaseType;
    }
}
