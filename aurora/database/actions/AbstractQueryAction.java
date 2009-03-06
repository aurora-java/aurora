/*
 * Created on 2008-6-11
 */
package aurora.database.actions;

import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.transform.Transformer;
import uncertain.ocm.OCManager;
import uncertain.proc.AbstractDeferredEntry;
import uncertain.proc.ProcedureRunner;
import aurora.database.CompositeMapCreator;
import aurora.database.FetchDescriptor;
import aurora.database.IResultSetConsumer;
import aurora.database.service.ServiceOption;
import aurora.database.service.SqlServiceContext;

public abstract class AbstractQueryAction  extends AbstractDeferredEntry {
    
    String      mode = ServiceOption.MODE_FREE_QUERY;
    String      parameter;
    boolean     fetchAll = false;
    boolean     trace = false;
    boolean     autoCount = false;
    Integer     pageSize;

    String      rootPath;
    String      recordName;
    List        transform_list;
    
    protected abstract void doQuery( CompositeMap param, IResultSetConsumer consumer, FetchDescriptor desc ) throws Exception ;
    
    protected abstract void prepare( ProcedureRunner runner ) throws Exception ;
    
    protected abstract void cleanUp( ProcedureRunner runner );
    
    public AbstractQueryAction( OCManager oc_manager ){
        super(oc_manager);
    }

    public void run(ProcedureRunner runner) throws Exception {
        super.doPopulate();
        prepare( runner );
        SqlServiceContext context = (SqlServiceContext)DynamicObject.cast(runner.getContext(), SqlServiceContext.class);
        context.setTrace(trace);
        
        ServiceOption option = ServiceOption.createInstance();
        option.setQueryMode(mode);
        option.setAutoCount(autoCount);
        context.setServiceOption(option);
        
        IResultSetConsumer consumer = null;
        CompositeMapCreator compositeCreator = null;
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
            //System.out.println(parameter);
            //System.out.println(param.toXML());
            
            // page settings
            FetchDescriptor desc = FetchDescriptor.createFromParameter(context.getParameter());
            desc.setFetchAll(fetchAll);
            if(pageSize!=null)
                desc.setPageSize(pageSize.intValue());
            
            // ResultSet consumer
            consumer = (IResultSetConsumer)context.getInstanceOfType(IResultSetConsumer.class);            
            if(consumer==null){
                CompositeMap result = context.getModel();
                if(rootPath!=null) result = result.createChildByTag(rootPath);
                compositeCreator = new CompositeMapCreator(result);
                consumer = compositeCreator;
            }
            context.setResultsetConsumer(consumer);
            doQuery(param, consumer, desc);
            if( transform_list != null && compositeCreator != null ) 
                Transformer.doBatchTransform( compositeCreator.getCompositeMap(), transform_list );
        }finally{
            context.setServiceOption(null);
            context.setResultsetConsumer(null);
            cleanUp( runner );
        }
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

}
