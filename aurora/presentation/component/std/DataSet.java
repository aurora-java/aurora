package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.application.features.ILookupCodeProvider;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.DataSetFieldConfig;

/**
 * 
 * @version $Id: DataSet.java v 1.0 2010-8-24 下午01:28:18 IBM Exp $
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class DataSet extends Component {
	
	private static final String VALID_SCRIPT = "validscript";
    
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		JSONArray fieldList = new JSONArray(); 
		
		DataSetConfig dsc = DataSetConfig.getInstance(view);
		CompositeMap fields = dsc.getFields();
		if(fields != null) {
			Iterator it = fields.getChildIterator();
			if(it != null)
			while(it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				DataSetFieldConfig sdfc = DataSetFieldConfig.getInstance(field);
				if(sdfc.getRequired())field.putBoolean(DataSetFieldConfig.PROPERTITY_REQUIRED, true);
				if(sdfc.getReadOnly())field.putBoolean(DataSetFieldConfig.PROPERTITY_READONLY, true);
				if(sdfc.getDefaultValue()!=null)field.putString(DataSetFieldConfig.PROPERTITY_DEFAULTVALUE, session.parseString(sdfc.getDefaultValue(), model));
				String lovService = field.getString(Lov.PROPERTITY_LOV_SERVICE);
				if(lovService!=null){
					field.putString(Lov.PROPERTITY_LOV_SERVICE, uncertain.composite.TextParser.parse(lovService, model));
				}
				String lovTitle = field.getString(Lov.PROPERTITY_TITLE);
				if(lovTitle!=null){
					field.putString(Lov.PROPERTITY_TITLE, session.getLocalizedPrompt(lovTitle));
				}
				String returnField = sdfc.getReturnField();//field.getString(DataSetFieldConfig.PROPERTITY_RETURN_FIELD, "");
				boolean addReturn = returnField!=null;//!"".equals(returnField);
				
				//删除不必要的信息
				field.remove("databasetype");
				String datatype = field.getString("datatype");
				if("java.lang.String".equals(datatype)) field.remove("datatype");
				
				JSONObject json = new JSONObject(field);
				CompositeMap mapping = sdfc.getMapping();//field.getChild(DataSetConfig.PROPERTITY_MAPPING);
				List maplist = new ArrayList();
				if(mapping != null){
					Iterator mit = mapping.getChildIterator();
					while(mit.hasNext()){
						CompositeMap mapfield = (CompositeMap)mit.next();
						if(returnField!=null && returnField.equals(mapfield.getString("to"))) {
							addReturn = false;
						}
						JSONObject mj = new JSONObject(mapfield);
						maplist.add(mj);
					}
				}
				if(addReturn) {
					CompositeMap returnmap = new CompositeMap("map");
					returnmap.putString("from", sdfc.getValueField());
					returnmap.putString("to", returnField);
					JSONObject jo = new JSONObject(returnmap);
					maplist.add(jo);
				}
				if(maplist.size() > 0){
					try {
						json.put(DataSetConfig.PROPERTITY_MAPPING, maplist);
					} catch (JSONException e) {
						throw new IOException(e.getMessage());
					}
				}
				fieldList.put(json);
			}
		}
		
		StringBuffer sb = new StringBuffer();
		String attachTab = dsc.getValidListener();
		if(attachTab != null){
			String[] ts = attachTab.split(",");
			for(int i=0;i<ts.length;i++){
				String tid = ts[i];
				sb.append("$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').on('valid',function(ds, record, name, valid){if(!valid && !Ext.get('"+tid+"').hasActiveFx()) Ext.get('"+tid+"').frame('ff0000', 3, { duration: 1 })});\n");
			}
		}
		map.put(VALID_SCRIPT, sb.toString());
		
		CompositeMap datas = dsc.getDatas();
		JSONArray dataList = new JSONArray(); 
		List list = null;
		Set dataHead = new HashSet();
		if(datas != null){
			String ds = datas.getString(DataSetConfig.PROPERTITY_DATASOURCE, "");
			if(ds.equals("")){
				list = datas.getChilds();
				Iterator dit = list.iterator();
				while(dit.hasNext()){
					CompositeMap item = (CompositeMap)dit.next();
					Iterator it = item.keySet().iterator();
					dataHead.addAll(item.keySet());
					while(it.hasNext()){
						String key = (String)it.next();
						Object valueKey = item.get(key);
						if(valueKey!=null){
							String value = uncertain.composite.TextParser.parse(valueKey.toString(), model);
							if(value.equals(valueKey.toString())){
								item.put(key, valueKey);							
							}else{
								item.put(key, value);
							}
						}
					}
				}
			}else{
				CompositeMap data= (CompositeMap)model.getObject(ds);
				if(data!= null){
					list = data.getChilds();
				}				
			}
		}
		String lcode = dsc.getLookupCode();
		if(lcode!=null){
			ILookupCodeProvider provider = session.getLookupProvider();
			if(provider!=null){
				list = new ArrayList();
				try {
					list = provider.getLookupList(session.getLanguage(), lcode);
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}
//				if(llist!=null){
//					Iterator it = llist.iterator();
//					while(it.hasNext()){
//						JSONObject json = new JSONObject((CompositeMap)it.next());
//						dataList.put(json);					
//					}
//				}
			}
		}
		if(list != null&&!list.isEmpty()){
			dataHead.addAll(((CompositeMap)list.get(0)).keySet());
			addConfig(DataSetConfig.PROPERTITY_DATA_HEAD, new JSONArray(dataHead));
			Iterator dit = list.iterator();
			while(dit.hasNext()){
				CompositeMap item = (CompositeMap)dit.next();
				JSONArray json = new JSONArray();
				Iterator it = dataHead.iterator();
				while(it.hasNext()){
					json.put(item.get(it.next()));
				}
				dataList.put(json);
			}						
		}
		if(fieldList.length()!=0)addConfig(DataSetConfig.PROPERTITY_FIELDS, fieldList);
		if(dataList.length()!=0)addConfig(DataSetConfig.PROPERTITY_DATAS, dataList);
		if(!"".equals(dsc.getQueryDataSet()))addConfig(DataSetConfig.PROPERTITY_QUERYDATASET, dsc.getQueryDataSet());
		if(!"".equals(dsc.getQueryUrl())){
			String queryUrl = uncertain.composite.TextParser.parse(dsc.getQueryUrl(), model);
			addConfig(DataSetConfig.PROPERTITY_QUERYURL, queryUrl);
		}
		if(!"".equals(dsc.getSubmitUrl())) {
			String submitUrl = uncertain.composite.TextParser.parse(dsc.getSubmitUrl(), model);
			addConfig(DataSetConfig.PROPERTITY_SUBMITURL,submitUrl);
		}
		if(!"".equals(dsc.getBindTarget()))addConfig(DataSetConfig.PROPERTITY_BINDTARGET, dsc.getBindTarget());
		if(!"".equals(dsc.getBindName()))addConfig(DataSetConfig.PROPERTITY_BINDNAME, dsc.getBindName());
		if(dsc.isFetchAll())addConfig(DataSetConfig.PROPERTITY_FETCHALL, new Boolean(dsc.isFetchAll()));
		String autoQuery = dsc.getString(DataSetConfig.PROPERTITY_AUTO_QUERY,"false");
		Boolean isAutoQuery = Boolean.FALSE;
		if(!"false".equals(autoQuery)){
			autoQuery = uncertain.composite.TextParser.parse(autoQuery, model);
			if("true".equalsIgnoreCase(autoQuery)){
				isAutoQuery = Boolean.TRUE;
			}
		}
		if(isAutoQuery.booleanValue())addConfig(DataSetConfig.PROPERTITY_AUTO_QUERY, isAutoQuery);
		if(dsc.isAutoPageSize())addConfig(DataSetConfig.PROPERTITY_AUTO_PAGE_SIZE, new Boolean(dsc.isAutoPageSize()));
		addConfig(DataSetConfig.PROPERTITY_PAGEID, session.getSessionContext().getString("pageid", ""));
		addConfig(DataSetConfig.PROPERTITY_PAGESIZE, new Integer(dsc.getPageSize()));
		addConfig(DataSetConfig.PROPERTITY_AUTO_COUNT, new Boolean(dsc.isAutoCount()));
		if(dsc.isAutoCreate())addConfig(DataSetConfig.PROPERTITY_AUTO_CREATE, new Boolean(dsc.isAutoCreate()));
		if(dsc.isSelectable())addConfig(DataSetConfig.PROPERTITY_SELECTABLE, new Boolean(dsc.isSelectable()));
		if(null!=dsc.getSelectFunction())addConfig(DataSetConfig.PROPERTITY_SELECT_FUNCTION,dsc.getSelectFunction());
		if(!DataSetConfig.DEFAULT_SELECTION_MODEL.equals(dsc.getSelectionModel()))addConfig(DataSetConfig.PROPERTITY_SELECTION_MODEL, dsc.getSelectionModel());
		if(!"".equals(dsc.getProcessFunction())) addConfig(DataSetConfig.PROPERTITY_PROCESS_FUNCTION, dsc.getProcessFunction());
		map.put(CONFIG, getConfigString());
	}
}
