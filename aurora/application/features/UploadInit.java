/*
 * Created on 2009-9-16 上午11:22:05
 * Author: Zhou Fan
 */
package aurora.application.features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.ocm.IObjectRegistry;
import aurora.application.AuroraApplication;
import aurora.application.config.ScreenConfig;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.IModelFactory;
import aurora.database.actions.config.ActionConfigManager;
import aurora.database.actions.config.ModelQueryConfig;
import aurora.database.service.ServiceOption;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.IDGenerator;
import aurora.presentation.component.std.Upload;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.DataSetFieldConfig;
import aurora.presentation.component.std.config.DataSetsConfig;
import aurora.presentation.component.std.config.TableConfig;
import aurora.presentation.component.std.config.UploadConfig;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.events.E_PrepareServiceConfig;

public class UploadInit implements IViewBuilder, E_PrepareServiceConfig {
	
	private static final String DEFAULT_ATM_BM = "fnd.fnd_atm_attachment";

    IModelFactory mFactory;
		
    public UploadInit(IModelFactory factory) {
        this.mFactory = factory;
    }
    
    public int onPrepareServiceConfig( IService service ) throws Exception {
        
        ServiceContext sc = service.getServiceContext();
        CompositeMap model = sc.getModel();
        ServiceInstance svc = (ServiceInstance)service;
        CompositeMap screen_config_map = svc.getServiceConfigData();
        if(!"screen".equals(screen_config_map.getName())) return EventModel.HANDLE_NORMAL;
        ScreenConfig screen = ScreenConfig.createScreenConfig(screen_config_map);
        
        List list = CompositeUtil.findChilds(screen.getObjectContext(), "upload");
        if(list!=null){
        	Iterator it = list.iterator();
        	while(it.hasNext()){
        		CompositeMap view = (CompositeMap)it.next();
        		UploadConfig uc = UploadConfig.getInstance(view);
        		String id = view.getString(ComponentConfig.PROPERTITY_ID);
        		if(id==null){
        			id = "up_"+IDGenerator.getInstance().generate();
        			view.put(ComponentConfig.PROPERTITY_ID, id);
        		}
        		
        		ModelQueryConfig mqc = ActionConfigManager.createModelQuery();
	    		mqc.setModel(DEFAULT_ATM_BM);
    			if(uc.isShowList()) {
	    			mqc.setRootPath("/model/"+id);
	    			mqc.setAutoCount(false);
	    			mqc.setFetchAll(true);
	    			String st = view.getString(UploadConfig.PROPERTITY_SOURCE_TYPE, "");
	    			if(st.indexOf("${") == -1) {
	    				st = "'" + st + "'";
	    			}
	    			String pk = view.getString(UploadConfig.PROPERTITY_PK_VALUE, "-1");
	    			if(pk.indexOf("${") == -1) {
	    				pk = "'" + pk + "'";
	    			}
	    			mqc.putString(ServiceOption.KEY_DEFAULT_WHERE_CLAUSE, "fam.table_name = " + st + " and fam.table_pk_value = " + pk + " order by fa." + uc.getSortSql());
        		}
    			screen.addInitProcedureAction(mqc.getObjectContext());
        		
        		
        		CompositeMap ds = new CompositeMap(DataSetConfig.TAG_NAME);
        		ds.putBoolean(DataSetConfig.PROPERTITY_AUTO_COUNT, false);
        		ds.putBoolean(DataSetConfig.PROPERTITY_AUTO_QUERY, false);
        		ds.putBoolean(DataSetConfig.PROPERTITY_FETCHALL, true);
        		ds.putBoolean(DataSetConfig.PROPERTITY_CAN_QUERY, true);
        		ds.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
        		ds.putString(DataSetConfig.PROPERTITY_MODEL, DEFAULT_ATM_BM);
        		ds.put(ComponentConfig.PROPERTITY_ID, id+"_ds");
        		CompositeMap datas = ds.getChild(DataSetConfig.PROPERTITY_DATAS);
    			if(datas == null){
    				datas = ds.createChild(DataSetConfig.PROPERTITY_DATAS);
    			}
    			datas.putString(DataSetConfig.PROPERTITY_DATASOURCE, "/model/"+id);
    			String context_path = model.getObject("/request/@context_path").toString();
    			ds.putString(DataSetConfig.PROPERTITY_SUBMITURL, view.getString(UploadConfig.PROPERTITY_DELETE_URL, context_path + "/atm_delete.svc"));
        		screen.addDataSet(ds);
        	}
        }
        return EventModel.HANDLE_NORMAL;
    
    }
    

	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		try {
			session.buildViews(view_context.getModel(), view_context.getView().getChilds());
		} catch (Exception e) {
			throw new ViewCreationException(e);
		}
		
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}

}
