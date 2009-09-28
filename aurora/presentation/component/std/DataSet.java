package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.bm.IModelFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;

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
	
	
    public DataSet() {
    }
    
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);	
		addJavaScript(session, context, "core/DataSet.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		
		Map map = context.getMap();
		List fieldList = new ArrayList(); 
		
		CompositeMap fields = view.getChild(PROPERTITY_FIELDS);
		if(fields != null) {
			Iterator it = fields.getChildIterator();
			while(it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				field.putBoolean("required", field.getBoolean("required", false));
				field.putBoolean("readonly", field.getBoolean("readonly", false));
				JSONObject json = new JSONObject(field);
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
				CompositeMap model = context.getModel();
				CompositeMap data = (CompositeMap)model.getObject(ds);
				if(data!= null){
					list = data.getChilds();
				}				
			}
			if(list != null){
				Iterator dit = list.iterator();
				while(dit.hasNext()){
					CompositeMap item = (CompositeMap)dit.next();
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
		map.put(PROPERTITY_DATAS, dataList.toString());	
		map.put(PROPERTITY_QUERYURL, view.getString(PROPERTITY_QUERYURL, ""));	
		map.put(PROPERTITY_SUBMITURL, view.getString(PROPERTITY_SUBMITURL, ""));	
		map.put(PROPERTITY_QUERYDATASET, view.getString(PROPERTITY_QUERYDATASET, ""));
		map.put(PROPERTITY_FETCHALL, view.getString(PROPERTITY_FETCHALL, "false"));
		map.put(PROPERTITY_PAGESIZE, view.getString(PROPERTITY_PAGESIZE, "10"));
		map.put(PROPERTITY_AUTOCOUNT, view.getString(PROPERTITY_AUTOCOUNT, "true"));
		
		
		
	}
}
