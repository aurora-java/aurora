/*
 * Created on 2008-6-17
 */
package aurora.database.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.ocm.OCManager;
import uncertain.proc.ProcedureRunner;
import aurora.database.SqlRunner;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

/**
 * <model-batch-update statusField="" sourcePath="" model="">
 */
public class ModelBatchUpdate extends AbstractModelAction {
    
    /**
     * @param modelFactory
     * @param ocManager
     */
    public ModelBatchUpdate(DatabaseServiceFactory factory, OCManager ocManager) {
        super(factory);
        mOcManager = ocManager;
    }

    String                  mSourcePath = "/parameter";
    OCManager               mOcManager;
   
    String                  statusField = "_status";
    
    void doUpdate( int record_no, CompositeMap item ) throws Exception {
        String status = item.getString(statusField);
        if(status==null){
            mLogger.warning("No status field in record No."+record_no+", content:"+item.toXML());
            return;
        }
        mLogger.log(Level.CONFIG, "execute {0} on record No.{1}", new Object[]{status,new Integer(record_no)} );        
        if("insert".equals(status)){
            mService.insert(item);
        }else if("update".equals(status)){
            mService.updateByPK(item);
        }else if("delete".equals(status)){
            mService.deleteByPK(item);
        }else{
            throw new IllegalArgumentException("Invalid status:'"+status+"' in record "+item.toXML());
        }
    }

    public void run(ProcedureRunner runner) throws Exception {
        prepareRun(runner);
        CompositeMap map = runner.getContext();
        SqlServiceContext svcContext = SqlServiceContext.createSqlServiceContext(map);        
        CompositeMap old_current_param = svcContext.getCurrentParameter();
        Collection records = SqlRunner.getSourceParameter(map, mSourcePath);
        if(records!=null){
            mLogger.log(Level.CONFIG,"Running model batch update with data from path {0}, total {1} records", new Object[]{ mSourcePath, new Integer(records.size()) } );
            Iterator it = records.iterator();
            int mod_count = 0;
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
                doUpdate(mod_count,item);
                mod_count++;                
                //super.run(runner);
            }
            if(mod_count>0)
                svcContext.setCurrentParameter(old_current_param);
        }else{
            mLogger.info("[model-batch-update] Data from '"+mSourcePath+"' is null");
        }
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

}
