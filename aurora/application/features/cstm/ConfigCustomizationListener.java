/*
 * Created on 2011-9-4 上午08:58:00
 * $Id$
 */
package aurora.application.features.cstm;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import uncertain.ocm.IObjectRegistry;
import aurora.events.E_PrepareServiceConfig;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;

public class ConfigCustomizationListener implements E_PrepareServiceConfig {

    public static final String KEY_CUSTOMIZATION_ENABLED = "customizationenabled";
    
    ICustomizationDataProvider  mCustomizationDataProvider;
	IObjectRegistry registry;

	public ConfigCustomizationListener(IObjectRegistry registry) {
		this.registry = registry;
		this.mCustomizationDataProvider = (ICustomizationDataProvider)registry.getInstanceOfType(ICustomizationDataProvider.class);
	}

    public int onPrepareServiceConfig(IService service) throws Exception {
        ServiceInstance svc = (ServiceInstance)service;
        CompositeMap config = svc.getServiceConfigData();
        boolean customization_enabled = config.getBoolean(KEY_CUSTOMIZATION_ENABLED, mCustomizationDataProvider.getDefaultCustomizationEnabled());
        if(customization_enabled){
            ServiceContext svc_context = svc.getServiceContext();
            String svc_name = svc.getName();
            CompositeMap    data = mCustomizationDataProvider.getCustomizationData( svc_name , svc_context.getObjectContext());
            if(data!=null){
            	CustomSourceCode.custom(registry,config, data);
            }
        }
        return EventModel.HANDLE_NORMAL;
    }

}
