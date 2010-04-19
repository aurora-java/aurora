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
import aurora.presentation.component.std.config.FormConfig;
import aurora.presentation.component.std.config.GridColumnConfig;
import aurora.presentation.component.std.config.GridConfig;
import aurora.service.ServiceContext;

public class AutoGrid implements IFeature{
	
	private static final String PROPERTITY_HREF = "href";
	
	IModelFactory mFactory;
	CompositeMap view;

    public AutoGrid(IModelFactory factory) {
        this.mFactory = factory;
    }
    
    public int onCreateView(ProcedureRunner runner ) {
        ServiceContext sc = ServiceContext.createServiceContext(runner.getContext());
        CompositeMap model = sc.getModel();
    	GridConfig grid = GridConfig.getInstance(view);
		
		String href = view.getString(PROPERTITY_HREF, "");
		if(!"".equals(href)){
			try {
				href = uncertain.composite.TextParser.parse(href, model);
				BusinessModel bm = null;
				try {
					bm = mFactory.getModelForRead(href);
				}catch(Exception e){
					bm = mFactory.getModelForRead(href,"xml");
				}
//				BusinessModel bm = mFactory.getModelForRead(href, "xml");
				aurora.bm.Field[] fields = bm.getFields();
				int fl = fields.length;
				for(int n=0;n<fl;n++){
					aurora.bm.Field field = fields[n];
					if(field.isForDisplay()){
						GridColumnConfig column = GridColumnConfig.getInstance(field.getObjectContext());
						column.setWidth(field.getDisplayWidth());
						column.setDataIndex(column.getName());
						grid.addColumn(column);
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		view.getParent().replaceChild(view, grid.getObjectContext());
    	return EventModel.HANDLE_NORMAL;
    }
	
	public int attachTo(CompositeMap v, Configuration procConfig) {
		view = v;
		return IFeature.NORMAL;
	}
}
