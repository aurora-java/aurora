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
        Collection records = SqlRunner.getSourceParameter(map, sourcePath);
        if(records!=null){
            Iterator it = records.iterator();
            while(it.hasNext()){
                Object obj = it.next();
                if( obj==null ) continue;
                if(! (obj instanceof CompositeMap) )
                    throw new IllegalStateException("item in batch source collection should be instance of CompositeMap: "+obj);
                CompositeMap item = (CompositeMap)obj;
                //System.out.println("batch on "+item.toXML());
                svcContext.setCurrentParameter(item);
                super.run(runner);
            }
        }
        svcContext.setCurrentParameter(null);
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
