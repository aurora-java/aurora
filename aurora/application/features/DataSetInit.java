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
import uncertain.event.EventModel;
import aurora.application.config.ScreenConfig;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.IModelFactory;
import aurora.database.actions.config.ActionConfigManager;
import aurora.database.actions.config.ModelQueryConfig;
import aurora.events.E_PrepareServiceConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.IDGenerator;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.DataSetFieldConfig;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;

@SuppressWarnings("unchecked")
public class DataSetInit implements IViewBuilder, E_PrepareServiceConfig {

    IModelFactory mFactory;
		
    public DataSetInit(IModelFactory factory) {
        this.mFactory = factory;
    }
    
    
	public int onPrepareServiceConfig( IService service ) throws Exception {
        
        ServiceContext sc = service.getServiceContext();
        CompositeMap model = sc.getModel();
        ServiceInstance svc = (ServiceInstance)service;
        CompositeMap screen_config_map = svc.getServiceConfigData();
        if(!"screen".equals(screen_config_map.getName()))
            return EventModel.HANDLE_NORMAL;
        ScreenConfig screen = ScreenConfig.createScreenConfig(screen_config_map);
        CompositeMap datasets = screen.getDataSetsConfig();
        if(datasets==null)
            return EventModel.HANDLE_NORMAL;
        List list = datasets.getChildsNotNull();
        List dslist = new ArrayList();
        Iterator it = list.iterator();
        while(it.hasNext()){
            CompositeMap dataset = (CompositeMap)it.next();
            processDataSet(dataset,model,dslist,screen);
        }
        datasets.getChilds().addAll(dslist);
        return EventModel.HANDLE_NORMAL;
    
    }
    
    /*
    public void onInitService( ProcedureRunner runner ) throws Exception{
        CompositeMap context = runner.getContext();
        mConfig = runner.getConfiguration();
        ServiceContext sc = ServiceContext.createServiceContext(runner.getContext());
        CompositeMap model = sc.getModel();
        ServiceInstance svc = ServiceInstance.getInstance(context);
        ScreenConfig screen = ScreenConfig.createScreenConfig(svc.getServiceConfigData(), svc.getConfig());
        CompositeMap datasets = screen.getDataSetsConfig();
        List list = datasets.getChildsNotNull();
        List dslist = new ArrayList();
        Iterator it = list.iterator();
        while(it.hasNext()){
        	CompositeMap dataset = (CompositeMap)it.next();
        	processDataSet(dataset,model,dslist,screen);
        }
        datasets.getChilds().addAll(dslist);
    }
    */

    private void processDataSet(CompositeMap ds,CompositeMap model,List dslist,ScreenConfig screen) throws Exception{
    	DataSetConfig dsc = DataSetConfig.getInstance(ds);
		String queryUrl = dsc.getQueryUrl();
		String submitUrl = dsc.getSubmitUrl();
		String baseModel = dsc.getModel();
		boolean cq = dsc.isCanQuery();
		boolean cs = dsc.isCanSubmit();
		
		
		if(baseModel!=null && dsc.getLoadData() == true){
			ModelQueryConfig mqc = ActionConfigManager.createModelQuery();
			mqc.setModel(baseModel);
			mqc.setRootPath("/model/"+ dsc.getId() == null ? baseModel : dsc.getId());
			mqc.setAutoCount(false);
			mqc.setFetchAll(true);
			screen.addInitProcedureAction(mqc.getObjectContext());
			//mConfig.loadConfig(mqc.getObjectContext());
			CompositeMap datas = ds.getChild(DataSetConfig.PROPERTITY_DATAS);
			if(datas == null){
				datas = ds.createChild(DataSetConfig.PROPERTITY_DATAS);
			}
			datas.putString(DataSetConfig.PROPERTITY_DATASOURCE, "/model/"+ dsc.getId() == null ? baseModel : dsc.getId());
		}
		if(cq && "".equals(queryUrl) && baseModel!=null){
			ds.putString(DataSetConfig.PROPERTITY_QUERYURL, model.getObject("/request/@context_path").toString() + "/autocrud/"+baseModel+"/query");
		}
		if(cs && "".equals(submitUrl) && baseModel!=null){
			ds.putString(DataSetConfig.PROPERTITY_SUBMITURL, model.getObject("/request/@context_path").toString() + "/autocrud/"+baseModel+"/batch_update");
		}
		if(baseModel!=null){
			baseModel = uncertain.composite.TextParser.parse(baseModel, model);
			BusinessModel bm = null;
            bm = mFactory.getModelForRead(baseModel);
			
			Field[] bmfields = bm.getFields();
			if(bmfields != null){
				CompositeMap fields = ds.getChild(DataSetConfig.PROPERTITY_FIELDS);
				if(fields == null){
					fields = new CompositeMap(DataSetConfig.PROPERTITY_FIELDS);
					ds.addChild(fields);
				}
				List childs = new ArrayList();
				List list = fields.getChildsNotNull();
				
				int fl = bmfields.length;
				for(int n=0;n<fl;n++){
					Field field = bmfields[n];
					processField(model,field,dslist);
					DataSetFieldConfig fieldConfig = DataSetFieldConfig.getInstance(field.getObjectContext());
					Iterator lit = list.iterator();
					while(lit.hasNext()){
						CompositeMap lfield = (CompositeMap)lit.next();
						if(field.getString("name").equalsIgnoreCase(lfield.getString("name"))){
							fieldConfig.getObjectContext().copy(lfield);
							if(fieldConfig.getPrompt() == null){
								fieldConfig.setPrompt(bm.getFieldPrompt(field));
							}
						}
					}
					childs.add(fieldConfig.getObjectContext());
				}
				
				Iterator lit = list.iterator();
				while(lit.hasNext()){
					CompositeMap lfield = (CompositeMap)lit.next();
					boolean has = false;
					for(int n=0;n<fl;n++){
						Field field = bmfields[n];
//						DataSetFieldConfig fieldConfig = DataSetFieldConfig.getInstance(field.getObjectContext());
						if(field.getString("name").equalsIgnoreCase(lfield.getString("name"))){
							has = true;
							break;
						}
					}
					if(!has) childs.add(lfield);
				}
				fields.getChilds().clear();
				fields.getChilds().addAll(childs);
			}
		}
    }
    
    private void processField(CompositeMap model,Field field,List list) {
    	String type = field.getEditorType();
    	if("combobox".equalsIgnoreCase(type)){
    		String sourceModel = field.getObjectContext().getString("sourcemodel", "");
    		if(!"".equals(sourceModel)){
    			String id = IDGenerator.getInstance().generate();
    			DataSetConfig ds = DataSetConfig.getInstance();    			
    			ds.setModel(sourceModel);
    			ds.setFetchAll(true);
    			ds.setQueryUrl(model.getObject("/request/@context_path").toString() + "/autocrud/"+sourceModel+"/query");
    			ds.setAutoQuery(true);
    			ds.setId(id);
    			list.add(ds.getObjectContext());
    			field.setOptions(id);
    		}
    	}
    }
    
//	private void processDataSet(CompositeMap view) throws Exception{
//		String href = view.getString(PROPERTITY_HREF, "");
//		if(!"".equals(href)){
//			CompositeMap bm = mFactory.getModelConfig(href);
//			CompositeMap bmfields = bm.getChild(PROPERTITY_FIELDS);
//			if(bmfields != null){
//				CompositeMap fields = view.getChild(PROPERTITY_FIELDS);
//				if(fields == null){
//					fields = new CompositeMap(PROPERTITY_FIELDS);
//					view.addChild(fields);
//				}
//				List childs = new ArrayList();
//				List list = fields.getChildsNotNull();
//				List bmlist = bmfields.getChilds();
//				
//				Iterator bit = bmlist.iterator();
//				while(bit.hasNext()){
//					CompositeMap field = (CompositeMap)bit.next();
//					Iterator lit = list.iterator();
//					while(lit.hasNext()){
//						CompositeMap lfield = (CompositeMap)lit.next();
//						if(field.getString("name").equalsIgnoreCase(lfield.getString("name"))){
////							field.putAll(lfield);
//							field.copy(lfield);
//						}
//					}
//					childs.add(field);
//				}
//				
//				Iterator lit = list.iterator();
//				while(lit.hasNext()){
//					CompositeMap lfield = (CompositeMap)lit.next();
//					Iterator bmit = bmlist.iterator();
//					boolean has = false;
//					while(bmit.hasNext()){
//						CompositeMap field = (CompositeMap)bmit.next();
//						if(field.getString("name").equalsIgnoreCase(lfield.getString("name"))){
//							has = true;
//							break;
//						}
//					}
//					if(!has) childs.add(lfield);
//				}
//				fields.getChilds().clear();
//				fields.getChilds().addAll(childs);
//			}
//		}
//    }

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
