package aurora.presentation.component.std;

import java.io.Writer;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import aurora.presentation.BuildSession;
import aurora.presentation.component.TemplateRenderer;
import aurora.presentation.component.std.config.BoxConfig;
import aurora.presentation.component.std.config.ComponentConfig;

/**
 * Box
 * @version $Id$
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class Box extends GridLayout {
	
	public static final String VERSION = "$Revision$";
	private static final String DEFAULT_TH_CLASS = "layout-th";
	UncertainEngine ue;
	protected int getLabelWidth(CompositeMap view){
		BoxConfig bc = new BoxConfig();
		bc.initialize(view);
		int labelWidth = bc.getLabelWidth();
		return labelWidth;
	}
	
	protected void beforeBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		BoxConfig bc = new BoxConfig();
		bc.initialize(view);
		Writer out = session.getWriter();
		String vlabel = field.getString(ComponentConfig.PROPERTITY_PROMPT);
		String label = vlabel==null ? getFieldPrompt(session, field, field.getString(ComponentConfig.PROPERTITY_BINDTARGET, "")) : vlabel;
		String id = field.getString(ComponentConfig.PROPERTITY_ID,"");
		label = session.getLocalizedPrompt(label);
		int labelWidth = bc.getLabelWidth();
		
		String defaultLabelSeparator = session.getSessionContext().getString(TemplateRenderer.KEY_LABEL_SEPARATOR);
		String labelSeparator = view.getString(BoxConfig.PROPERTITY_LABEL_SEPARATOR,(defaultLabelSeparator == null ? ":" : defaultLabelSeparator));
		
		if(!"".equals(label)) {
			StringBuffer str = new StringBuffer();
			str.append("<th class='"+DEFAULT_TH_CLASS+"' ");
			String ps = field.getString(ComponentConfig.PROPERTITY_PROMPT_STYLE);
			boolean hidden = field.getBoolean(ComponentConfig.PROPERTITY_HIDDEN, false);
			if(!"".equals(ps))str.append(" style='"+ps+"' ");
			str.append("width="+labelWidth+"><div");
			if(!"".equals(id)){
				str.append(" id='"+id+"_prompt'");
			}
			if(hidden){
				str.append(" style='visibility:hidden'");
			}
			str.append(">");
			str.append(label+labelSeparator+"</div></th>");
			out.write(str.toString());
		}
	}
	
//	private String getComponentLabel(BuildSession session, CompositeMap field){
//		String label = field.getString(ComponentConfig.PROPERTITY_PROMPT, "");
//		
//		String dataset = field.getString(ComponentConfig.PROPERTITY_BINDTARGET, "");
//		if(!"".equals(dataset)){
//			String name = field.getString(ComponentConfig.PROPERTITY_NAME, "");
//			CompositeMap ds = getDataSet(session, dataset);
//			if(ds!=null){
//				CompositeMap fieldcm = ds.getChild(DataSetConfig.PROPERTITY_FIELDS);
//				if(fieldcm !=null){
//					List fields = fieldcm.getChilds();
//					Iterator it = fields.iterator();
//					while(it.hasNext()){
//						CompositeMap fieldMap = (CompositeMap)it.next();
//						String fn = fieldMap.getString(ComponentConfig.PROPERTITY_NAME,"");
//						if(name.equals(fn)){
//							label = fieldMap.getString(ComponentConfig.PROPERTITY_PROMPT,"");
//							break;
//						}
//					}
//				}
//			}
//		}
//		return label;
//	}
//	
//	private CompositeMap getDataSet(BuildSession session, String dataSetName){
//		CompositeMap dataset = null;
//		ServiceInstance svc = (ServiceInstance)session.getInstanceOfType(IService.class);
//        ScreenConfig screen = ScreenConfig.createScreenConfig(svc.getServiceConfigData());
//        CompositeMap datasets = screen.getDataSetsConfig();
//        if(datasets!=null){
//	        List list = datasets.getChilds();
//	        Iterator it =list.iterator();
//	        while(it.hasNext()){
//	        	CompositeMap ds = (CompositeMap)it.next();
//	        	String dsname = ds.getString("id", "");
//	        	if(dataSetName.equals(dsname)){
//	        		dataset = ds;
//	        		break;
//	        	}
//	        }
//        }
//        return dataset;
//	}
}
