package aurora.presentation.component.std;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.application.config.ScreenConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.service.IService;
import aurora.service.ServiceInstance;

/**
 * Box
 * @version $Id: Box.java v 1.0 2009-7-31 上午10:37:19 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class Box extends GridLayout {
	
	private static final String DEFAULT_TH_CLASS = "layout-th";
	private static final String DEFAULT_HEAD_CLASS = "layout-head";
	private static final String PROPERTITY_LABEL_WIDTH = "labelwidth";
	
	protected void buildHead(BuildSession session, CompositeMap model,CompositeMap view, int rows ,int columns) throws Exception{
		Writer out = session.getWriter();
		String title = view.getString("title", "");
		if(!"".equals(title)) {
			out.write("<thead><tr><th class='"+DEFAULT_HEAD_CLASS+"' colspan="+columns*2+">");
			out.write(title);
			out.write("</th></tr></thead>");
		}
	}
	
	protected void afterBuildTop(BuildSession session, CompositeMap model,CompositeMap view) throws Exception{
//		Writer out = session.getWriter();
//		String title = view.getString("title", "");
//		if(!"".equals(title)) {
//			out.write("<tr height=5><td><td></tr>");
//		}
	}
	
	protected int getLabelWidth(CompositeMap view){
		int labelWidth = view.getInt(PROPERTITY_LABEL_WIDTH, 75);
		return labelWidth;
	}
	
	protected void beforeBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		Writer out = session.getWriter();
		String vlabel = field.getString(PROPERTITY_LABEL);
		String label = vlabel==null ? getComponentLabel(session, field) : vlabel;
		int labelWidth = view.getInt(PROPERTITY_LABEL_WIDTH, 75);
		if(!"".equals(label))
		out.write("<th class='"+DEFAULT_TH_CLASS+"' width="+labelWidth+"><div>"+label+":</div></th>");
	}
	
	private String getComponentLabel(BuildSession session, CompositeMap field){
		String label = field.getString(PROPERTITY_LABEL, "");
		
		String dataset = field.getString(PROPERTITY_BINDTARGET, "");
		if(!"".equals(dataset)){
			String name = field.getString(PROPERTITY_BINDNAME, "");
			if("".equals(name)) name = field.getString(PROPERTITY_NAME, "");
			CompositeMap ds = getDataSet(session, dataset).getChild(DataSet.PROPERTITY_FIELDS);
			List fields = ds.getChilds();
			Iterator it = fields.iterator();
			while(it.hasNext()){
				CompositeMap fieldMap = (CompositeMap)it.next();
				String fn = fieldMap.getString(PROPERTITY_NAME,"");
				if(name.equals(fn)){
					label = fieldMap.getString(PROPERTITY_LABEL);
					break;
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
        List list = datasets.getChilds();
        Iterator it =list.iterator();
        while(it.hasNext()){
        	CompositeMap ds = (CompositeMap)it.next();
        	String dsname = ds.getString("name", "");
        	if(dataSetName.equals(dsname)){
        		dataset = ds;
        		break;
        	}
        }
        return dataset;
	}
}
