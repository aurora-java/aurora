/*
 * Created on 2008-6-17
 */
package aurora.database.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.ProcedureRunner;
import aurora.bm.CascadeOperation;
import aurora.bm.DefaultAccessChecker;
import aurora.bm.DisabledOperationException;
import aurora.bm.IBusinessModelAccessChecker;
import aurora.bm.IBusinessModelAccessCheckerFactory;
import aurora.bm.Operation;
import aurora.database.SqlRunner;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.ServiceOption;
import aurora.database.service.SqlServiceContext;

/**
 * <model-batch-update statusField="" sourcePath="" model="">
 */
public class ModelBatchUpdate extends AbstractModelAction {
    
    /**
     * @param modelFactory
     * @param ocManager
     */
    public ModelBatchUpdate(DatabaseServiceFactory factory, OCManager ocManager, IObjectRegistry reg ) {
        super(factory);
        mOcManager = ocManager;
        mObjectRegistry = reg;
        mModelCheckerFactory = (IBusinessModelAccessCheckerFactory)mObjectRegistry.getInstanceOfType(IBusinessModelAccessCheckerFactory.class);
        //System.out.println(mModelCheckerFactory);
    }

    String                  mSourcePath = "/parameter";
    OCManager               mOcManager;
    IObjectRegistry         mObjectRegistry;
    IBusinessModelAccessCheckerFactory  mModelCheckerFactory;
    IBusinessModelAccessChecker         mModelChecker;
   
    String                  statusField = "_status";
    Set                     mEnabledOperations;
    
    void doUpdate( int record_no, CompositeMap item  ) throws Exception {
        String status = item.getString(statusField);
        if(status==null){
            mLogger.warning("No status field in record No."+record_no+", content:"+item.toXML());
            return;
        }
        status = status.toLowerCase();
        if(mEnabledOperations!=null){
            if(!mEnabledOperations.contains(status))
                return;
        }
        /** Model operation access check */
        if(mModelChecker!=null)
            if(!mModelChecker.canPerformOperation(status))
                throw new DisabledOperationException("Can't perform operation "+status+" on BusinessModel "+getModel());
        mLogger.log(Level.CONFIG, "execute {0} on record No.{1} for model {2}", new Object[]{status,new Integer(record_no), mService.getBusinessModel().getName()} );        
        if(Operation.INSERT.equals(status)){
            mService.insert(item);
        }else if(Operation.UPDATE.equals(status)){
            mService.updateByPK(item);
        }else if(Operation.DELETE.equals(status)){
            mService.deleteByPK(item);
        }else if(Operation.EXECUTE.equals(status)){
            mService.execute(item);
        }else{
            throw new IllegalArgumentException("Invalid status:'"+status+"' in record "+item.toXML());
        }
    }
    
    void doBatchUpdateInternal( Collection records, SqlServiceContext  svcContext, ModelBatchUpdate[] ba_array )
        throws Exception
    {
        mLogger.log(Level.CONFIG,"============ Running model batch update with data from path {0}, total {1} records", new Object[]{ mSourcePath, new Integer(records.size()) } );        
        int mod_count=0;
        CompositeMap old_current_param = svcContext.getCurrentParameter();        
        Iterator it = records.iterator();
        while(it.hasNext()){
            Object obj = it.next();
            if( obj==null ){
                mLogger.config("Record No."+mod_count+" is null");
                continue;
            }
            if(! (obj instanceof CompositeMap) )
                throw new IllegalStateException("item in batch source collection should be instance of CompositeMap: "+obj);
            CompositeMap item = (CompositeMap)obj;
            svcContext.setCurrentParameter(item);
            doUpdate(mod_count,item );
            //execute cascade operations
            if( ba_array!=null ){
                for(int i=0; i<ba_array.length; i++){
                    ModelBatchUpdate mbu = ba_array[i];
                    mLogger.config("Performing batch update on child BM, parameter path:" + mbu.getSourcePath());
                    Collection child_records = SqlRunner.getSourceParameter(item, mbu.getSourcePath());
                    if(child_records!=null)
                        mbu.doBatchUpdate(child_records, svcContext.getObjectContext());
                    else
                        mLogger.config("No data in this path");
                }
            }            
            mod_count++;                
            //super.run(runner);
        }
        mLogger.config("============ End of batch update for "+mSourcePath);
        if(mod_count>0)
            svcContext.setCurrentParameter(old_current_param);        
    }
    
    public void doBatchUpdate( Collection records, CompositeMap context ) throws Exception {
        prepareRun(context);        
        SqlServiceContext svcContext = SqlServiceContext.createSqlServiceContext(context);  
        ModelBatchUpdate[] ba_array = null;
        CascadeOperation[]  cascade_ops = mService.getBusinessModel().getCascadeOperations();
        if(cascade_ops!=null){
            ba_array = new ModelBatchUpdate[cascade_ops.length];
            for( int i=0; i<cascade_ops.length; i++)
            {
                CascadeOperation op = cascade_ops[i];
                ba_array[i] = new ModelBatchUpdate(mServiceFactory, mOcManager, mObjectRegistry);
                ba_array[i].setSourcePath(op.getInputPath());
                ba_array[i].setModel(op.getModel());
                ba_array[i].setEnabledOperations(op.getEnabledOperations());
            }
            mLogger.config("Total "+ba_array.length+" cascade operation(s)");
        }
        
        if(records!=null){
            doBatchUpdateInternal( records, svcContext, ba_array );
        }else{
            mLogger.info("[model-batch-update] Data from '"+mSourcePath+"' is null");
        }

    }

    public void run(ProcedureRunner runner) throws Exception {        
        CompositeMap map = runner.getContext(); 
        if(mModelCheckerFactory!=null){
            mModelChecker = mModelCheckerFactory.getChecker( getModel(), map);
            if(mModelChecker==null){
                mModelChecker = DefaultAccessChecker.ALWAYS_DENY;
            }
        }
        Collection records = SqlRunner.getSourceParameter(map, mSourcePath);
        doBatchUpdate(records, map);
    }

    /**
     * @return the sourcePath
     */
    public String getSourcePath() {
        return mSourcePath;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(String sourcePath) {
        this.mSourcePath = sourcePath;
    }

    public String getStatusField() {
        return statusField;
    }

    public void setStatusField(String statusField) {
        this.statusField = statusField;
    }
    
    protected void prepareServiceOption(ServiceOption option) {
        transferServiceOption(option, ServiceOption.KEY_UPDATE_PASSED_FIELD_ONLY);
    }

    public Set getEnabledOperations() {
        return mEnabledOperations;
    }

    public void setEnabledOperations(Set enabledOperations) {
        mEnabledOperations = enabledOperations;
    }    

}
