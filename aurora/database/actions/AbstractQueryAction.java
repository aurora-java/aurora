/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.TextParser;
import uncertain.composite.transform.Transformer;
import uncertain.event.IContextAcceptable;
import uncertain.ocm.OCManager;
import uncertain.proc.AbstractDeferredEntry;
import uncertain.proc.ProcedureRunner;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.rsconsumer.IRootMapAcceptable;
import aurora.database.service.ServiceOption;
import aurora.database.service.SqlServiceContext;

public abstract class AbstractQueryAction  extends AbstractDeferredEntry {
    
    String      mode = ServiceOption.MODE_FREE_QUERY;
    String      parameter;
    boolean     fetchAll = false;
    boolean     trace = false;
    boolean     autoCount = false;
    Integer     pageSize;
    String      fieldNameCase = "lower";
    byte        fieldNameCaseValue = Character.LOWERCASE_LETTER;
    // Is attributes set from client request parameters( such as autocrud )? If so, extra security check is needed.
    boolean     attribFromRequest = false;

    String      rootPath;
    String      recordName;
    List        transform_list;
    
    String      connectionName;  
    
    IResultSetConsumer  rsConsummer;
    
    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    protected abstract void doQuery( CompositeMap param, IResultSetConsumer consumer, FetchDescriptor desc ) throws Exception ;
    
    protected abstract void prepare( CompositeMap context_map ) throws Exception ;
    
    protected abstract void cleanUp( CompositeMap context_map );
    
    public AbstractQueryAction( OCManager oc_manager ){
        super(oc_manager);
    }
    
    protected void transferServiceOption( ServiceOption option, String key ){
        option.getObjectContext().put( key, super.mEntryConfig.get(key));        
    }
    
    protected CompositeMap getMapFromRootPath( CompositeMap context, String root_path ){
    	if(root_path == null)
    		return context;
        CompositeMap data = (CompositeMap)context.getObject(root_path);
        if(data==null)
            data = context.createChildByTag(root_path);
        return data;
    }
    
    public void query( CompositeMap context_map ) throws Exception
    {
        super.doPopulate();
        prepare( context_map );
        SqlServiceContext context = (SqlServiceContext)DynamicObject.cast(context_map, SqlServiceContext.class);
        //context.setTrace(trace);      
        ServiceOption option = ServiceOption.createInstance();
        option.setQueryMode(mode);
        option.setAutoCount(autoCount);
        option.setConnectionName(connectionName);
        option.setFieldCase(fieldNameCaseValue);
        transferServiceOption(option, ServiceOption.KEY_DEFAULT_WHERE_CLAUSE);
        transferServiceOption(option, ServiceOption.KEY_QUERY_ORDER_BY);
        context.setServiceOption(option);        
        
        IResultSetConsumer consumer = null;
//        CompositeMapCreator compositeCreator = null;
        try{
            // get parameter
            CompositeMap param = context.getCurrentParameter();
            if(parameter!=null){
                Object obj = param.getObject(parameter);
                if(obj!=null){
                    if(! (obj instanceof CompositeMap))
                        throw new IllegalArgumentException("query parameter should be instance of CompositeMap, but actually got "+obj.getClass().getName());
                    param = (CompositeMap)obj;                    
                }  
            }
            
            // page settings
            FetchDescriptor desc = FetchDescriptor.createFromParameter(context.getParameter());
            desc.setFetchAll(fetchAll);
            if(pageSize!=null)
                desc.setPageSize(pageSize.intValue());
            
            // ResultSet consumer
            if(this.rsConsummer!=null)
                consumer = this.rsConsummer;
            else{
                consumer = (IResultSetConsumer)context.getInstanceOfType(IResultSetConsumer.class);            
                if(consumer==null){
                    consumer = new CompositeMapCreator();
                    mOCManager.populateObject(mEntryConfig, consumer);
                }
            }
            if(consumer instanceof IContextAcceptable)
                ((IContextAcceptable) consumer).setContext(context_map);
            // set root path
            if(consumer instanceof IRootMapAcceptable){
                CompositeMap result = getMapFromRootPath(context.getModel(), TextParser.parse(this.rootPath,context.getObjectContext()));
                if(result!=null)
                	((IRootMapAcceptable)consumer).setRoot(result);
            }
            context.setResultsetConsumer(consumer);
            doQuery(param, consumer, desc);
            if( transform_list != null && consumer instanceof  IRootMapAcceptable){ 
                CompositeMap root = ((IRootMapAcceptable)consumer).getRoot();
                Transformer.doBatchTransform( root, transform_list );
            }
        }finally{
            context.setServiceOption(null);
            context.setResultsetConsumer(null);
            cleanUp( context_map );
        }        
    }

    public void run(ProcedureRunner runner) throws Exception {
        query(runner.getContext());
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * @return the recordName
     */
    public String getRecordName() {
        return recordName;
    }

    /**
     * @param recordName the recordName to set
     */
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    /**
     * @return the rootPath
     */
    public String getRootPath() {
        return rootPath;
    }

    /**
     * @param rootPath the rootPath to set
     */
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
    
    public void addTransformList( CompositeMap tlist ){
        transform_list = tlist.getChilds();
    }

    /**
     * @return the fetchAll
     */
    public boolean getFetchAll() {
        return fetchAll;
    }

    /**
     * @param fetchAll the fetchAll to set
     */
    public void setFetchAll(boolean fetchAll) {
        this.fetchAll = fetchAll;
    }

    /**
     * @return the pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the trace
     */
    public boolean getTrace() {
        return trace;
    }

    /**
     * @param trace the trace to set
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    /**
     * @return the autoCount
     */
    public boolean getAutoCount() {
        return autoCount;
    }

    /**
     * @param autoCount the autoCount to set
     */
    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }

    public String getFieldNameCase() {
        return fieldNameCase;
    }

    public void setFieldNameCase(String fieldNameCase) {
        this.fieldNameCase = fieldNameCase;
        if("upper".equalsIgnoreCase(fieldNameCase))
            fieldNameCaseValue = Character.UPPERCASE_LETTER;
        else if("lower".equalsIgnoreCase(fieldNameCase))
            fieldNameCaseValue = Character.LOWERCASE_LETTER;
        else if("unassigned".equalsIgnoreCase(fieldNameCase))
            fieldNameCaseValue = Character.UNASSIGNED;            
    }
    
    protected byte getFieldNameCaseValue(){
        return fieldNameCaseValue;
    }
    
    public void addConsumer( CompositeMap processor ){
        List childs = processor.getChilds();
        if(childs==null);
        if(childs.size()!=1);
        CompositeMap child = (CompositeMap)childs.get(0);
        Object inst = mOCManager.createObject(child);
        if(inst==null || !(inst instanceof IResultSetConsumer));
        rsConsummer = (IResultSetConsumer)inst;
    }

    public boolean getAttribFromRequest() {
        return attribFromRequest;
    }

    public void setAttribFromRequest(boolean attribFromRequest) {
        this.attribFromRequest = attribFromRequest;
    }
    
}
