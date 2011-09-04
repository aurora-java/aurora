/*
 * Created on 2011-9-4 上午08:58:00
 * $Id$
 */
package aurora.application.features.cstm;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;
import aurora.events.E_PrepareServiceConfig;
import aurora.service.IService;
import aurora.service.ServiceContext;

public class ConfigCustomizationListener implements E_PrepareServiceConfig {

    public static final String KEY_CUSTOMIZATION_ENABLED = "customizationenabled";
    
    ICustomizationDataProvider  mCustomizationDataProvider;

    /**
     * @param mCustomizationDataProvider
     */
    public ConfigCustomizationListener(
            ICustomizationDataProvider mCustomizationDataProvider) {
        this.mCustomizationDataProvider = mCustomizationDataProvider;
    }

    public int onPrepareServiceConfig(IService service) throws Exception {
        CompositeMap config = service.getServiceConfigData();
        boolean customization_enabled = config.getBoolean(KEY_CUSTOMIZATION_ENABLED, true);
        if(customization_enabled){
            ServiceContext svc_context = service.getServiceContext();
            CompositeMap    data = mCustomizationDataProvider.getCustomizationData( svc_context.getServiceName() , svc_context.getObjectContext());
            if(data!=null){
                // do customization with config
            }
        }
        return EventModel.HANDLE_NORMAL;
    }

}
