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

public class DataSet extends Component {
	
	public static final String PROPERTITY_HREF = "href";
	public static final String PROPERTITY_FIELDS = "fields";
	public static final String PROPERTITY_DATAS = "datas";
	public static final String PROPERTITY_DATASOURCE = "datasource";
	public static final String PROPERTITY_CREATERECORD = "autocreate";
	public static final String PROPERTITY_QUERYURL = "queryurl";
	public static final String PROPERTITY_SUBMITURL = "submiturl";
	public static final String PROPERTITY_QUERYDATASET = "querydataset";
	public static final String PROPERTITY_FETCHALL = "fecthall";
	public static final String PROPERTITY_PAGESIZE = "pagesize";
	public static final String PROPERTITY_AUTOCOUNT = "autocount";
	public static final String PROPERTITY_PAGEID = "pageid";	
	public static final String PROPERTITY_MAPPING = "mapping";
	public static final String PROPERTITY_MAP = "map";
	
    public DataSet() {
    }
    

	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		List fieldList = new ArrayList(); 
		
		CompositeMap fields = view.getChild(PROPERTITY_FIELDS);
		if(fields != null) {
			Iterator it = fields.getChildIterator();
			while(it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				String validator = field.getString("validator", "");
				if(!"".equals(validator))
				field.putString("validator", validator);
				field.putString(PROPERTITY_NAME, field.getString(PROPERTITY_NAME,"").toLowerCase());
				field.putBoolean("required", field.getBoolean("required", false));
				field.putBoolean("readonly", field.getBoolean("readonly", false));
				
				JSONObject json = new JSONObject(field);
				CompositeMap mapping = field.getChild(PROPERTITY_MAPPING);
				if(mapping != null){
					Iterator mit = mapping.getChildIterator();
					List maplist = new ArrayList();
					while(mit.hasNext()){
						CompositeMap mapfield = (CompositeMap)mit.next();
						JSONObject mj = new JSONObject(mapfield);
						maplist.add(mj);
					}
					try {
						json.put(PROPERTITY_MAPPING, maplist);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				fieldList.add(json);
			}
		}
		map.put(PROPERTITY_FIELDS, fieldList.toString());
		
		CompositeMap datas = view.getChild(PROPERTITY_DATAS);
		List dataList = new ArrayList(); 
		List list = null;
		if(datas != null){
			String ds = datas.getString(PROPERTITY_DATASOURCE, "");
			if(ds.equals("")){
				list = datas.getChilds();				
			}else{
				
				CompositeMap data = (CompositeMap)model.getObject(ds);
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
						String value = uncertain.composite.TextParser.parse(item.getString(key), model);
						item.put(key, value);
					}
					JSONObject json = new JSONObject(item);
					dataList.add(json);
				}						
			}
		}
		boolean create = view.getBoolean(PROPERTITY_CREATERECORD, false);
		if(dataList.size() == 0 && create) {
			JSONObject json = new JSONObject();
			dataList.add(json);
			
		}
		
		map.put(PROPERTITY_PAGEID, session.getSessionContext().getString("pageid", ""));
		map.put(PROPERTITY_DATAS, dataList.toString());	
		map.put(PROPERTITY_QUERYURL, view.getString(PROPERTITY_QUERYURL, ""));	
		map.put(PROPERTITY_SUBMITURL, view.getString(PROPERTITY_SUBMITURL, ""));	
		map.put(PROPERTITY_QUERYDATASET, view.getString(PROPERTITY_QUERYDATASET, ""));
		map.put(PROPERTITY_FETCHALL, view.getString(PROPERTITY_FETCHALL, "false"));
		map.put(PROPERTITY_PAGESIZE, view.getString(PROPERTITY_PAGESIZE, "10"));
		map.put(PROPERTITY_AUTOCOUNT, view.getString(PROPERTITY_AUTOCOUNT, "true"));
		
		
		
	}
}
