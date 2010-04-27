package aurora.application.features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;
import aurora.bm.BusinessModel;
import aurora.bm.IModelFactory;
import aurora.presentation.component.std.IDGenerator;
import aurora.presentation.component.std.config.CheckBoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;
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
    
    public int onCreateView(ProcedureRunner runner ) throws IOException {
        ServiceContext sc = ServiceContext.createServiceContext(runner.getContext());
        CompositeMap model = sc.getModel();
        CompositeMap gridView = processColumns(model);
        gridView = processEditors(gridView);
        view.getParent().replaceChild(view,gridView);
        System.out.println(gridView.toXML());
    	return EventModel.HANDLE_NORMAL;
    }
    
    
    private CompositeMap processEditors(CompositeMap gv) throws IOException{
    	GridConfig grid = GridConfig.getInstance(gv);
    	CompositeMap columns = grid.getColumns();
    	if(columns!=null){
    		List list = columns.getChilds();
    		Iterator it = list.iterator();
			while(it.hasNext()){
				CompositeMap column = (CompositeMap)it.next();
				GridColumnConfig cf = GridColumnConfig.getInstance(column);
				String eid = cf.getEditor();
				if(eid==null || "".equals(eid)){
					ComponentConfig editor = getEditor(column);
					if(editor!=null){
						grid.addEditor(editor);
						cf.setEditor(editor.getId());
						column.getParent().replaceChild(column, cf.getObjectContext());
					}
				}
			}
//			view = grid.getObjectContext();
//			view.getParent().replaceChild(view,grid.getObjectContext());
    	}
    	CompositeMap gridView = grid.getObjectContext();
    	return gridView;
    }
    
    //TODO:改进???
    private ComponentConfig getEditor(CompositeMap column) {
    	String checkedvalue = column.getString(CheckBoxConfig.PROPERTITY_CHECKEDVALUE,"");
    	if(!"".equals(checkedvalue)){
    		CheckBoxConfig checkbox = CheckBoxConfig.getInstance();
    		checkbox.setId(IDGenerator.getInstance().generate());
    		return checkbox;
    	}
    	return null;
    }
    
    
    private CompositeMap processColumns(CompositeMap model) throws IOException{
    	GridConfig grid = GridConfig.getInstance(view);
		List bmColumns = new ArrayList();
		String href = view.getString(PROPERTITY_HREF, "");
		if(!"".equals(href)){
			href = uncertain.composite.TextParser.parse(href, model);
			BusinessModel bm = null;
			try {
				bm = mFactory.getModelForRead(href);
			}catch(Exception e){
				bm = mFactory.getModelForRead(href,"xml");
			}
			aurora.bm.Field[] fields = bm.getFields();
			int fl = fields.length;
			for(int n=0;n<fl;n++){
				aurora.bm.Field field = fields[n];
				if(field.isForDisplay()){
					GridColumnConfig column = GridColumnConfig.getInstance(field.getObjectContext());
					column.setWidth(field.getDisplayWidth());
					column.setDataIndex(column.getName());
					bmColumns.add(column);
//						grid.addColumn(column);
				}
			}
		}
		
		//将view中的column属性配置覆盖掉bm中的column配置	
		CompositeMap columns = view.getChild(GridConfig.PROPERTITY_COLUMNS);
		if(columns != null){
			List cls = columns.getChilds();
			Iterator vit = cls.iterator();
			while(vit.hasNext()){
				CompositeMap column = (CompositeMap)vit.next();
				boolean defined = false;
				Iterator bit = bmColumns.iterator();
				while(bit.hasNext()){
					GridColumnConfig bmColumn = (GridColumnConfig)bit.next();
					String bmDataIndex = bmColumn.getDataIndex();
					String griddataIndex = column.getString(GridColumnConfig.PROPERTITY_DATAINDEX);
					if(bmDataIndex.equals(griddataIndex)){
						defined = true;
						bmColumn.getObjectContext().copy(column);
						break;								
					}
				}
				if(!defined) {
					GridColumnConfig gc = GridColumnConfig.getInstance(column);
					bmColumns.add(gc);
				}
			}
		}
		CompositeMap viewColumns  = new CompositeMap(GridConfig.PROPERTITY_COLUMNS);
		Iterator bit = bmColumns.iterator();
		while(bit.hasNext()){
			GridColumnConfig gc = (GridColumnConfig)bit.next();
			viewColumns.addChild(gc.getObjectContext());
		}
		grid.getObjectContext().replaceChild(grid.getColumns(), viewColumns);
		CompositeMap gridView = grid.getObjectContext();
//		view.getParent().replaceChild(view,gridView);
		return gridView;
    }
	
	public int attachTo(CompositeMap v, Configuration procConfig) {
		view = v;
		return IFeature.NORMAL;
	}
}
