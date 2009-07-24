/*
 * Created on 2008-6-17
 */
package aurora.database.actions;

import java.util.Collection;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.database.SqlRunner;
import aurora.database.service.SqlServiceContext;

public class BatchApply extends Procedure {
    
    String  sourcePath;
    
    public BatchApply(){
        super();
    }
    
    public BatchApply(OCManager ocm){
        super(ocm);
    }

    public void run(ProcedureRunner runner) throws Exception {
        CompositeMap map = runner.getContext();
        SqlServiceContext svcContext = SqlServiceContext.createSqlServiceContext(map);        
        CompositeMap old_current_param = svcContext.getCurrentParameter();
        Collection records = SqlRunner.getSourceParameter(map, sourcePath);
        if(records!=null){
            Iterator it = records.iterator();
            int mod_count = 0;
            while(it.hasNext()){
                Object obj = it.next();
                if( obj==null ) continue;
                if(! (obj instanceof CompositeMap) )
                    throw new IllegalStateException("item in batch source collection should be instance of CompositeMap: "+obj);
                CompositeMap item = (CompositeMap)obj;
                svcContext.setCurrentParameter(item);
                mod_count++;
                super.run(runner);
            }
            if(mod_count>0)
                svcContext.setCurrentParameter(old_current_param);
        }
        
    }

    /**
     * @return the sourcePath
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

}
