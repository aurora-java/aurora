package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;

public class DataSet extends Component {
	
	private static final String VALID_SCRIPT = "validscript";
	private static final String VALID_LISTENER = "validlistener";
	
    
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		List fieldList = new ArrayList(); 
		
		CompositeMap fields = view.getChild(DataSetConfig.PROPERTITY_FIELDS);
		if(fields != null) {
			Iterator it = fields.getChildIterator();
			if(it != null)
			while(it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				String validator = field.getString("validator", "");
				if(!"".equals(validator))
				field.putString("validator", validator);
				field.putString(ComponentConfig.PROPERTITY_NAME, field.getString(ComponentConfig.PROPERTITY_NAME,""));
				field.putBoolean("required", field.getBoolean("required", false));
				field.putBoolean("readonly", field.getBoolean("readonly", false));
				String dv = field.getString(DataSetConfig.PROPERTITY_DEFAULTVALUE, "");
				if(!"".equals(dv))field.putString(DataSetConfig.PROPERTITY_DEFAULTVALUE, dv);
				
				String returnField = field.getString("returnfield", "");
				boolean addReturn = !"".equals(returnField);
				JSONObject json = new JSONObject(field);
				CompositeMap mapping = field.getChild(DataSetConfig.PROPERTITY_MAPPING);
				List maplist = new ArrayList();
				if(mapping != null){
					Iterator mit = mapping.getChildIterator();
					while(mit.hasNext()){
						CompositeMap mapfield = (CompositeMap)mit.next();
						if(returnField.equals(mapfield.getString("to"))){
							addReturn = false;
						}
						JSONObject mj = new JSONObject(mapfield);
						maplist.add(mj);
					}
				}
				if(addReturn) {
					CompositeMap returnmap = new CompositeMap("map");
					returnmap.putString("from", field.getString("valuefield"));
					returnmap.putString("to", returnField);
					JSONObject jo = new JSONObject(returnmap);
					maplist.add(jo);
				}
				if(maplist.size() > 0){
					try {
						json.put(DataSetConfig.PROPERTITY_MAPPING, maplist);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					fieldList.add(json);
				}
			}
		}
		map.put(DataSetConfig.PROPERTITY_SELECTABLE, new Boolean(view.getBoolean(DataSetConfig.PROPERTITY_SELECTABLE, true)));
		map.put(DataSetConfig.PROPERTITY_SELECTIONMODEL, view.getString(DataSetConfig.PROPERTITY_SELECTIONMODEL, "multiple"));
		map.put(DataSetConfig.PROPERTITY_FIELDS, fieldList.toString());
		
		StringBuffer sb = new StringBuffer();
		String attachTab = view.getString(VALID_LISTENER, "");
		if(!"".equals(attachTab)){
			String[] ts = attachTab.split(",");
			for(int i=0;i<ts.length;i++){
				String tid = ts[i];
				sb.append("$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').on('valid',function(ds, record, name, valid){if(!valid && !Ext.get('"+tid+"').hasActiveFx()) Ext.get('"+tid+"').frame('ff0000', 3, { duration: 1 })});\n");
			}
		}
		map.put(VALID_SCRIPT, sb.toString());
		
		CompositeMap datas = view.getChild(DataSetConfig.PROPERTITY_DATAS);
		List dataList = new ArrayList(); 
		List list = null;
		if(datas != null){
			String ds = datas.getString(DataSetConfig.PROPERTITY_DATASOURCE, "");
			if(ds.equals("")){
				list = datas.getChilds();				
			}else{
				
				CompositeMap data= (CompositeMap)model.getObject(ds);
				if(data!= null){
					list = data.getChilds();
				}				
			}
			if(list != null){
				Iterator dit = list.iterator();
				while(dit.hasNext()){
					CompositeMap item = (CompositeMap)dit.next();
					Iterator it = item.keySet().iterator();
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
					JSONObject json = new JSONObject(item);
					dataList.add(json);
				}						
			}
		}
		boolean create = view.getBoolean(DataSetConfig.PROPERTITY_CREATERECORD, false);
		if(dataList.size() == 0 && create) {
			JSONObject json = new JSONObject();
			dataList.add(json);
			
		}
		map.put(DataSetConfig.PROPERTITY_BINDTARGET, view.getString(DataSetConfig.PROPERTITY_BINDTARGET, ""));
		map.put(DataSetConfig.PROPERTITY_BINDNAME, view.getString(DataSetConfig.PROPERTITY_BINDNAME, ""));
		map.put(DataSetConfig.PROPERTITY_PAGEID, session.getSessionContext().getString("pageid", ""));
		map.put(DataSetConfig.PROPERTITY_DATAS, dataList.toString());	
		map.put(DataSetConfig.PROPERTITY_AUTOQUERY, view.getString(DataSetConfig.PROPERTITY_AUTOQUERY, "false"));	
		map.put(DataSetConfig.PROPERTITY_QUERYURL, view.getString(DataSetConfig.PROPERTITY_QUERYURL, ""));	
		map.put(DataSetConfig.PROPERTITY_SUBMITURL, view.getString(DataSetConfig.PROPERTITY_SUBMITURL, ""));	
		map.put(DataSetConfig.PROPERTITY_QUERYDATASET, view.getString(DataSetConfig.PROPERTITY_QUERYDATASET, ""));
		map.put(DataSetConfig.PROPERTITY_FETCHALL, view.getString(DataSetConfig.PROPERTITY_FETCHALL, "false"));
		map.put(DataSetConfig.PROPERTITY_PAGESIZE, view.getString(DataSetConfig.PROPERTITY_PAGESIZE, "10"));
		map.put(DataSetConfig.PROPERTITY_AUTOCOUNT, view.getString(DataSetConfig.PROPERTITY_AUTOCOUNT, "true"));
	}
}
