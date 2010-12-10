/*
 * Created on 2010-12-10 上午11:15:10
 * $Id$
 */
package aurora.bm;

/**
 * Creates BM Access checker by perform database query
 */
import uncertain.composite.CompositeMap;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;

public class DefaultAccessCheckerFactory implements
        IBusinessModelAccessCheckerFactory, IBusinessModelAccessChecker {
    
    /**
     * @param serviceFactory
     */
    public DefaultAccessCheckerFactory(DatabaseServiceFactory serviceFactory) {
        super();
        mServiceFactory = serviceFactory;
    }

    DatabaseServiceFactory          mServiceFactory;
    String                          mCheckServiceName;
    BusinessModel                   mCheckServiceModel;

    public IBusinessModelAccessChecker getChecker(String model_name,
            CompositeMap session_context) {
        BusinessModelService svc = null;
        // TODO Auto-generated method stub
        try{
            svc = mServiceFactory.getModelService(mCheckServiceModel, session_context);
            CompositeMap result = svc.queryAsMap(null);
            // fetch result set and put all operations into a Set
        }catch(Exception ex){
            throw new RuntimeException("Error when loading service "+mCheckServiceName,ex);
        }
        return null;
    }

    public boolean canPerformOperation(String operation) {
        // TODO Auto-generated method stub
        return false;
    }

    public String getCheckServiceName() {
        return mCheckServiceName;
    }

    public void setCheckServiceName(String checkServiceName) {
        mCheckServiceName = checkServiceName;
    }

}
