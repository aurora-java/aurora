/*
 * Created on 2008-6-13
 */
package aurora.database.actions;

import javax.sql.DataSource;

import uncertain.core.UncertainEngine;
import uncertain.exception.MessageFactory;
import uncertain.logging.ILogger;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.PackageMapping;
import aurora.application.action.AttachmentManager;
import aurora.application.action.AuroraCookie;
import aurora.application.action.HttpSessionCopy;
import aurora.application.action.HttpSessionOperate;
import aurora.application.action.ImageValidate;
import aurora.application.action.RefreshPrompts;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.service.DatabaseServiceFactory;

public class ServiceInitiator {
    /*
    static {
        MessageFactory.loadResource("resources.aurora_validation_exceptions");
    }
    */
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
       /*
        factory = new DatabaseServiceFactory( uncertainEngine );
        IObjectRegistry objreg = uncertainEngine.getObjectRegistry(); 
        objreg.registerInstance(DatabaseServiceFactory.class, factory);
        logger = uncertainEngine.getLogger("aurora.database");
        logger.info("BusinessModel service started");
        */

    }
    
    
    public void onInitialize(){
        DataSource ds = factory.getDataSource();
        if(ds==null){
            ds = (DataSource)uncertainEngine.getObjectRegistry().getInstanceOfType(DataSource.class);
            factory.setDataSource(ds);
        }
        logger.info("Using DataSource:"+ds);
        //factory.onInitialize();
    }
    

}
