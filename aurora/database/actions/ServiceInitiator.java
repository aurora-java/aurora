/*
 * Created on 2008-6-13
 */
package aurora.database.actions;

import javax.sql.DataSource;

import uncertain.core.UncertainEngine;
import uncertain.logging.ILogger;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IObjectRegistry;
import aurora.bm.IModelFactory;
import aurora.database.service.DatabaseServiceFactory;

public class ServiceInitiator {
    
    UncertainEngine             uncertainEngine;
    DatabaseServiceFactory      factory;
    ILogger                     logger;
    
    /**
     * @param uncertainEngine
     */
    public ServiceInitiator(UncertainEngine uncertainEngine) {
        super();
        this.uncertainEngine = uncertainEngine;
        init();
    }
    
    public void init(){
        factory = new DatabaseServiceFactory( uncertainEngine );
        IObjectRegistry objreg = uncertainEngine.getObjectRegistry(); 
        objreg.registerInstance(DatabaseServiceFactory.class, factory);
        //objreg.registerInstance(IModelFactory.class, factory.getModelFactory());
        ClassRegistry reg =  uncertainEngine.getClassRegistry();
        reg.addClassMapping( "model-query", ModelQuery.class );
        reg.addClassMapping( "model-update", ModelUpdate.class );
        reg.addClassMapping( "sql-execute", SqlExecute.class);
        reg.addClassMapping( "sql-query", SqlQuery.class);
        reg.addClassMapping("batch-apply", BatchApply.class);
        /* ======= pending ============== */
        reg.addClassMapping( "model-insert", ModelUpdate.class );
        reg.addClassMapping( "model-delete", ModelUpdate.class );
        reg.addClassMapping( "model-invoke", ModelUpdate.class );
        logger = uncertainEngine.getLogger("aurora.database");
        logger.info("BusinessModel service started");
    }
    
    
    public void onInitialize(){
        DataSource ds = factory.getDataSource();
        if(ds==null){
            ds = (DataSource)uncertainEngine.getObjectRegistry().getInstanceOfType(DataSource.class);
            factory.setDataSource(ds);
        }
        logger.info("Using DataSource:"+ds);
    }
    

}
