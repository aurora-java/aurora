/*
 * Created on 2010-8-30 下午01:02:48
 * $Id$
 */
package aurora.bm;

import java.io.IOException;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;
import aurora.application.IApplicationConfig;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;
import aurora.events.E_CheckBMAccess;
import aurora.service.ServiceInstance;

/**
 * @deprecated No longer use this method to check BM access
 */
public class DBModelAccessChecker implements E_CheckBMAccess {

    /**
     * @param appConfig
     * @param dbSvcFactory
     */
    public DBModelAccessChecker(IApplicationConfig appConfig,
            DatabaseServiceFactory dbSvcFactory) 
        throws IOException
    {
        super();
        mAppConfig = appConfig;
        mDbSvcFactory = dbSvcFactory;
        if(mAppConfig!=null){
            Object value = mAppConfig.getApplicationConfig().getObject("/access-control-config/@bmcheckservice");
            if(value!=null)
                mBMCheckService = value.toString();
        }
        if(mBMCheckService==null)
            throw new ConfigurationError("Must set 'BMCheckService' property in 'access-control-config' element in application config file");

        //BusinessModelService bmsc = mDbSvcFactory.getModelService(mBMCheckService);
        BusinessModel bm_check = mDbSvcFactory.getModelFactory().getModelForRead(mBMCheckService);
        if(bm_check==null)
            throw new ConfigurationError("Can't load BM check service "+mBMCheckService);            
    }

    //IObjectRegistry               mObjectRegistry;
    IApplicationConfig         mAppConfig;
    DatabaseServiceFactory     mDbSvcFactory;
    String                     mBMCheckService;
    
    public static String getModelNameForAccessCheck( BusinessModel model ){
        String bm_name = model.getName();
        String mode = model.getAccessControlMode();
        BusinessModel parent = model.getParent();
        if(parent==null)
            return bm_name;
        else{
            if(BusinessModel.ACCESS_CONTROL_MODE_SEPARATE.equalsIgnoreCase(mode))
                return bm_name;
            else
                return getModelNameForAccessCheck(parent);
        }        
        
    }


    public void onCheckBMAccess( BusinessModel model, String operation_name, ServiceInstance svc )
        throws Exception
    {
        String bm_name = model.getName();
/*
        String mode = model.getAccessControlMode();
        ILogger logger = svc.getServiceLogger();

        if(BusinessModel.ACCESS_CONTROL_MODE_NONE.equalsIgnoreCase(mode)){
            logger.log(Level.FINE,"No access control required for BM "+bm_name);
            return;
        }
*/        
        bm_name = getModelNameForAccessCheck(model);
        CompositeMap context = svc.getContextMap();
        context.putObject("/request/@bm_name", bm_name, true);
        context.putObject("/request/@operation_name", operation_name, true);
        BusinessModelService bmsc = mDbSvcFactory.getModelService(mBMCheckService);
        bmsc.execute(svc.getServiceContext().getParameter());
        //System.out.println(context.toXML());
    }

}
