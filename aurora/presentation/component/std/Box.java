package aurora.presentation.component.std;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.application.config.ScreenConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.service.IService;
import aurora.service.ServiceInstance;

/**
 * Box
 * @version $Id: Box.java v 1.0 2009-7-31 上午10:37:19 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class Box extends GridLayout {
	
	private static final String DEFAULT_TH_CLASS = "layout-th";
	private static final String PROPERTITY_LABEL_WIDTH = "labelwidth";
	
	protected int getLabelWidth(CompositeMap view){
		int labelWidth = view.getInt(PROPERTITY_LABEL_WIDTH, 75);
		return labelWidth;
	}
	
	protected void beforeBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		Writer out = session.getWriter();
		String vlabel = field.getString(ComponentConfig.PROPERTITY_LABEL);
		String label = vlabel==null ? getComponentLabel(session, field) : vlabel;
		int labelWidth = view.getInt(PROPERTITY_LABEL_WIDTH, 75);
		if(!"".equals(label))
		out.write("<th class='"+DEFAULT_TH_CLASS+"' width="+labelWidth+"><div>"+label+":</div></th>");
	}
	
	private String getComponentLabel(BuildSession session, CompositeMap field){
		String label = field.getString(ComponentConfig.PROPERTITY_LABEL, "");
		
		String dataset = field.getString(ComponentConfig.PROPERTITY_BINDTARGET, "");
		if(!"".equals(dataset)){
			String name = field.getString(ComponentConfig.PROPERTITY_NAME, "");
			CompositeMap ds = getDataSet(session, dataset);
			if(ds!=null){
				CompositeMap fieldcm = ds.getChild(DataSetConfig.PROPERTITY_FIELDS);
				if(fieldcm !=null){
					List fields = fieldcm.getChilds();
					Iterator it = fields.iterator();
					while(it.hasNext()){
						CompositeMap fieldMap = (CompositeMap)it.next();
						String fn = fieldMap.getString(ComponentConfig.PROPERTITY_NAME,"");
						if(name.equals(fn)){
							label = fieldMap.getString(ComponentConfig.PROPERTITY_LABEL,"");
							break;
						}
					}
				}
			}
		}
		return label;
	}
	
	private CompositeMap getDataSet(BuildSession session, String dataSetName){
		CompositeMap dataset = null;
		ServiceInstance svc = (ServiceInstance)session.getInstanceOfType(IService.class);
        ScreenConfig screen = ScreenConfig.createScreenConfig(svc.getServiceConfigData());
        CompositeMap datasets = screen.getDataSetsConfig();
        if(datasets!=null){
	        List list = datasets.getChilds();
	        Iterator it =list.iterator();
	        while(it.hasNext()){
	        	CompositeMap ds = (CompositeMap)it.next();
	        	String dsname = ds.getString("id", "");
	        	if(dataSetName.equals(dsname)){
	        		dataset = ds;
	        		break;
	        	}
	        }
        }
        return dataset;
	}
}
