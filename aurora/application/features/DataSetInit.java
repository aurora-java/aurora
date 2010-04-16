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
import uncertain.proc.ProcedureRunner;
import aurora.application.config.ScreenConfig;
import aurora.bm.BusinessModel;
import aurora.bm.IModelFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.DataSetFieldConfig;
import aurora.presentation.component.std.config.TextFieldConfig;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;

public class DataSetInit implements IViewBuilder {
	IModelFactory mFactory;

    public DataSetInit(IModelFactory factory) {
        this.mFactory = factory;
    }
	
    public void onInitService( ProcedureRunner runner ) throws Exception{
        CompositeMap context = runner.getContext();
        ServiceContext sc = ServiceContext.createServiceContext(runner.getContext());
        CompositeMap model = sc.getModel();
        ServiceInstance svc = ServiceInstance.getInstance(context);
        ScreenConfig screen = ScreenConfig.createScreenConfig(svc.getServiceConfigData());
        CompositeMap datasets = screen.getDataSetsConfig();
        List list = datasets.getChildsNotNull();
        Iterator it = list.iterator();
        while(it.hasNext()){
        	CompositeMap dataset = (CompositeMap)it.next();
        	processDataSet(dataset,model);
        }
    }
    
    private void processDataSet(CompositeMap view,CompositeMap model) throws Exception{
		String href = view.getString(DataSetConfig.PROPERTITY_HREF, "");
		String queryUrl = view.getString(DataSetConfig.PROPERTITY_QUERYURL,"");
		if(!"".equals(queryUrl)){
			queryUrl = uncertain.composite.TextParser.parse(queryUrl, model);
			view.putString(DataSetConfig.PROPERTITY_QUERYURL, queryUrl);
		}
		if(!"".equals(href)){
			href = uncertain.composite.TextParser.parse(href, model);
			BusinessModel bm = null;
			try {
				bm = mFactory.getModelForRead(href);
			}catch(Exception e){
				bm = mFactory.getModelForRead(href,"xml");
			}
			aurora.bm.Field[] bmfields = bm.getFields();
			if(bmfields != null){
				CompositeMap fields = view.getChild(DataSetConfig.PROPERTITY_FIELDS);
				if(fields == null){
					fields = new CompositeMap(DataSetConfig.PROPERTITY_FIELDS);
					view.addChild(fields);
				}
				List childs = new ArrayList();
				List list = fields.getChildsNotNull();
				
				int fl = bmfields.length;
				for(int n=0;n<fl;n++){
					aurora.bm.Field field = bmfields[n];
					DataSetFieldConfig fieldConfig = DataSetFieldConfig.getInstance(field.getObjectContext());
					Iterator lit = list.iterator();
					while(lit.hasNext()){
						CompositeMap lfield = (CompositeMap)lit.next();
						if(field.getString("name").equalsIgnoreCase(lfield.getString("name"))){
							fieldConfig.getObjectContext().copy(lfield);
						}
					}
					childs.add(fieldConfig.getObjectContext());
				}
				
				Iterator lit = list.iterator();
				while(lit.hasNext()){
					CompositeMap lfield = (CompositeMap)lit.next();
					boolean has = false;
					for(int n=0;n<fl;n++){
						aurora.bm.Field field = bmfields[n];
						DataSetFieldConfig fieldConfig = DataSetFieldConfig.getInstance(field.getObjectContext());
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
