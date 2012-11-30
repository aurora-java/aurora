package aurora.application.features;

import java.io.IOException;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;
import aurora.bm.BusinessModel;
import aurora.bm.IModelFactory;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.EventConfig;
import aurora.presentation.component.std.config.FormConfig;
import aurora.presentation.component.std.config.TextFieldConfig;
import aurora.service.ServiceContext;

public class AutoForm implements IFeature{
	private static final String PROPERTITY_MODEL = "model";
	
	private static final String PROPERTITY_ENTERDOWN_HANDLER = "enterdownhandler";
	
	IModelFactory mFactory;
	CompositeMap view;

    public AutoForm(IModelFactory factory) {
        this.mFactory = factory;
    }
    
    public int onCreateView(ProcedureRunner runner ) throws IOException {
        ServiceContext sc = ServiceContext.createServiceContext(runner.getContext());
        CompositeMap model = sc.getModel();
    	
    	FormConfig formConfig = FormConfig.getInstance(view);
		formConfig.setCellspacing(0);
		
		String target = view.getString(ComponentConfig.PROPERTITY_BINDTARGET,"");
		String handler = view.getString(PROPERTITY_ENTERDOWN_HANDLER);
		String labelWidth = uncertain.composite.TextParser.parse(view.getString("labelwidth"), model);
		formConfig.put("labelwidth","".equals(labelWidth)?null:labelWidth);
		String href = view.getString(PROPERTITY_MODEL, "");
		if(!"".equals(href)){
			href = uncertain.composite.TextParser.parse(href, model);
			BusinessModel bm = null;
			//TODO:.....
			try {
				bm = mFactory.getModelForRead(href);
			}catch(Exception e){
				bm = mFactory.getModelForRead(href,"xml");
			}
			aurora.bm.Field[] fields = bm.getFields();
			int fl = fields.length;
			for(int n=0;n<fl;n++){
				aurora.bm.Field field = fields[n];
				if(field.isForQuery()){
					TextFieldConfig textField = TextFieldConfig.getInstance(field.getObjectContext());
					textField.setWidth(field.getQueryWidth());
					if(!"".equals(target))textField.setBindTarget(target);
					if(handler!=null){
					EventConfig ec = EventConfig.getInstance();
						ec.setEventName("enterdown");
						ec.setHandler(handler);
						textField.addEvent(ec);
					}
					formConfig.addChild(textField.getObjectContext());
				}
			}
		}
		view.getParent().replaceChild(view, formConfig.getObjectContext());
    	return EventModel.HANDLE_NORMAL;
    }
	
	public int attachTo(CompositeMap v, Configuration procConfig) {
		view = v;
		return IFeature.NORMAL;
	}
}
