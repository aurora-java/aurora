/*
 * Created on 2008-6-13
 */
package aurora.database.actions;

import uncertain.core.UncertainEngine;
import uncertain.ocm.ClassMapping;
import uncertain.ocm.ClassRegistry;
import aurora.database.service.DatabaseServiceFactory;

public class ServiceInitiator {
    
    UncertainEngine             uncertainEngine;
    DatabaseServiceFactory      factory;
    
    /**
     * @param uncertainEngine
     */
    public ServiceInitiator(UncertainEngine uncertainEngine) {
        super();
        this.uncertainEngine = uncertainEngine;
    }
    
    public void onInitialize(){
        factory = new DatabaseServiceFactory( uncertainEngine );
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
        uncertainEngine.getLogger().info("BusinessModel service started");
    }
    

}
