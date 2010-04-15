/*
 * Created on 2008-6-17
 */
package aurora.database.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.ocm.OCManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.database.SqlRunner;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;

public class BatchApply extends Procedure {
    
    String  mSourcePath;
    
    public BatchApply(){
        super();
    }
    
    public BatchApply(OCManager ocm){
        super(ocm);
    }

    public void run(ProcedureRunner runner) throws Exception {
        CompositeMap map = runner.getContext();
        ILogger logger = DatabaseServiceFactory.getLogger(map);
        SqlServiceContext svcContext = SqlServiceContext.createSqlServiceContext(map);        
        CompositeMap old_current_param = svcContext.getCurrentParameter();
        Collection records = SqlRunner.getSourceParameter(map, mSourcePath);
        if(records!=null){
            logger.log(Level.CONFIG,"Running batch-apply with data from path {0}, total {1} records", new Object[]{ mSourcePath, new Integer(records.size()) } );            
            Iterator it = records.iterator();
            int mod_count = 0;
            while(it.hasNext()){
                Object obj = it.next();
                if( obj==null ){
                    logger.config("Record No."+mod_count+" is null");
                    continue;
                }
                if(! (obj instanceof CompositeMap) )
                    throw new IllegalStateException("item in batch source collection should be instance of CompositeMap: "+obj);
                CompositeMap item = (CompositeMap)obj;
                svcContext.setCurrentParameter(item);
                logger.config("Executing batch-apply on parameter No."+mod_count);
                mod_count++;
                super.run(runner);
            }
            if(mod_count>0)
                svcContext.setCurrentParameter(old_current_param);
        }else{
            logger.info("[batch-apply] Data from '"+mSourcePath+"' is null");
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
