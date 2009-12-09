/*
 * Created on 2009-9-9 下午01:53:12
 * Author: Zhou Fan
 */
package aurora.service.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.database.service.DatabaseServiceFactory;
import aurora.service.IService;

public abstract class AbstractAutoServiceServlet extends FacadeServlet {
    
    static final String CONFIG_PROTOTYPE_FILE = AbstractAutoServiceServlet.class.getPackage().getName()+".EmptyServiceConfig";
    
    DatabaseServiceFactory      mDatabaseServiceFactory; 
    CompositeMap                mServiceConfig;
    CompositeLoader             mCompositeLoader;

    protected abstract void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        IObjectRegistry reg = mUncertainEngine.getObjectRegistry();    
        mDatabaseServiceFactory = (DatabaseServiceFactory)reg.getInstanceOfType(DatabaseServiceFactory.class);
        if (mDatabaseServiceFactory == null)
            throw new ServletException(
                    "No DatabaseServiceFactory instance registered in UncertainEngine");
        mCompositeLoader = CompositeLoader.createInstanceForOCM();
        try{
            mServiceConfig = mCompositeLoader.loadFromClassPath(CONFIG_PROTOTYPE_FILE, "xml");
        }catch(Exception ex){
            throw new ServletException("Can't load builtin resource:"+CONFIG_PROTOTYPE_FILE, ex);
        }
    }    

}
