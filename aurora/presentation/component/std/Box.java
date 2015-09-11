package aurora.presentation.component.std;

import java.io.Writer;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationViewConfig;
import aurora.presentation.BuildSession;
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
	
	public Box(IObjectRegistry registry) {
		super(registry);
	}
	private String getLabel(BuildSession session, CompositeMap field){
		String vlabel = field.getString(ComponentConfig.PROPERTITY_PROMPT);
		return session.getLocalizedPrompt(vlabel==null ? getFieldPrompt(session, field, field.getString(ComponentConfig.PROPERTITY_BINDTARGET, "")) : vlabel);
	}
	
	private String beforeOrAfterBuildCell(BuildSession session, CompositeMap model,CompositeMap view, CompositeMap field,String align) throws Exception{
		BoxConfig bc = new BoxConfig();
		bc.initialize(view);
		String labelPosition = bc.getLabelPosition();
		if(align.equals(labelPosition)){
			StringBuilder sb = new StringBuilder("<th class='"+DEFAULT_TH_CLASS+"' ");
			if(null != field){
				String label = getLabel(session,field);
				int rowspan = field.getInt(BoxConfig.PROPERTITY_ROWSPAN, 1);
				if(rowspan>1){
					sb.append(" rowspan='"+rowspan+"'");
				}
				if(!"".equals(label)) {
					String id = field.getString(ComponentConfig.PROPERTITY_ID,"");
					String mDefaultLabelSeparator = ApplicationViewConfig.DEFAULT_LABEL_SEPARATOR;
					if(null!=mApplicationConfig){
						ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
						if(null!=view_config){
							mDefaultLabelSeparator = view_config.getDefaultLabelSeparator();
						}
					}
					int labelWidth = bc.getLabelWidth(model);
					String labelAlign = bc.getLabelAlign("left".equals(align)?"right":"left");
					String labelSeparator = bc.getLabelSeparator(mDefaultLabelSeparator);
					String ps = field.getString(ComponentConfig.PROPERTITY_PROMPT_STYLE);
					boolean hidden = field.getBoolean(ComponentConfig.PROPERTITY_HIDDEN, false);
					if(!"".equals(ps))sb.append(" style='"+ps+"' ");
					sb.append("width="+labelWidth+"><div style='text-align:"+labelAlign+"'");
					if(!"".equals(id)){
						sb.append(" id='"+id+"_prompt'");
					}
					if(hidden){
						sb.append(" style='visibility:hidden'");
					}
					sb.append(">");
					if("left".equals(align)){
						sb.append(label);
						sb.append(labelSeparator);
					}else{
						sb.append(labelSeparator);
						sb.append(label);
					}
					sb.append("</div");
				}
			}
			sb.append("></th>");
			return sb.toString();
		}else{
			return "";
		}
	}
	private String upperOrUnderBuildCell(BuildSession session, CompositeMap model,CompositeMap view, CompositeMap field,String align) throws Exception{
		BoxConfig bc = new BoxConfig();
		bc.initialize(view);
		String labelPosition = bc.getLabelPosition();
		if(align.equals(labelPosition)){
			StringBuilder sb = new StringBuilder("<th class='"+DEFAULT_TH_CLASS+"' ");
			if(null != field){
				String label = getLabel(session,field);
				int colspan = field.getInt(BoxConfig.PROPERTITY_COLSPAN, 1);
				if(colspan>1){
					sb.append(" colspan='"+colspan+"'");
				}
				if(!"".equals(label)) {
					String labelAlign = bc.getLabelAlign("left");
					String id = field.getString(ComponentConfig.PROPERTITY_ID,"");
					String ps = field.getString(ComponentConfig.PROPERTITY_PROMPT_STYLE);
					boolean hidden = field.getBoolean(ComponentConfig.PROPERTITY_HIDDEN, false);
					if(!"".equals(ps))sb.append(" style='"+ps+"' ");
					sb.append("><div style='text-align:"+labelAlign+"'");
					if(!"".equals(id)){
						sb.append(" id='"+id+"_prompt'");
					}
					if(hidden){
						sb.append(" style='visibility:hidden'");
					}
					sb.append(">");
					sb.append(label+"</div");
				}
			}
			sb.append("></th>");
			return sb.toString();
		}else{
			return "";
		}
	}
	protected String beforeBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		return beforeOrAfterBuildCell(session,model,view,field,"left");
	}
	protected String afterBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		return beforeOrAfterBuildCell(session,model,view,field,"right");
	}
	protected String upperBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		return upperOrUnderBuildCell(session,model,view,field,"top");
	}
	protected String underBuildCell(BuildSession session, CompositeMap model, CompositeMap view, CompositeMap field) throws Exception{
		return upperOrUnderBuildCell(session,model,view,field,"bottom");
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
