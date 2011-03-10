/*
 * Created on 2008-6-13
 */
package aurora.database.actions;

import javax.sql.DataSource;

import uncertain.core.UncertainEngine;
import uncertain.logging.ILogger;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IObjectRegistry;
import aurora.application.action.AuroraCookie;
import aurora.application.action.FileUpload;
import aurora.application.action.ImageValidate;
import aurora.application.action.RefreshPrompts;
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
        reg.addClassMapping( "model-insert", ModelInsert.class );
        reg.addClassMapping( "model-delete", ModelDelete.class );
        reg.addClassMapping( "model-execute", ModelExecute.class );
        reg.addClassMapping("model-batch-update", ModelBatchUpdate.class);
        reg.addClassMapping( "sql-execute", SqlExecute.class);
        reg.addClassMapping( "sql-query", SqlQuery.class);
        reg.addClassMapping("batch-apply", BatchApply.class);

        /** @todo move into optional package */
        reg.addClassMapping( "put-cookie", AuroraCookie.class);
//        reg.addClassMapping( "import-csv", DataImport.class);
        reg.addClassMapping("img-validate", ImageValidate.class);
        reg.addClassMapping("file-upload", FileUpload.class);
        reg.addClassMapping("refresh-prompts", RefreshPrompts.class);
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
