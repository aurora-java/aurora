package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;

public class DataSet extends Component{
	
	private static final String PROPERTITY_FIELDS = "fields";
	private static final String PROPERTITY_FIELD = "field";
	private static final String PROPERTITY_DATAS = "datas";
	private static final String PROPERTITY_DATASOURCE = "dataSource";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);	
		addJavaScript(session, context, "core/DataSet.js");
	}

	public void onCreateViewContent(BuildSession session, ViewContext context)  {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		List fieldList = new ArrayList(); 
		CompositeMap fields = view.getChild(PROPERTITY_FIELDS);
		if(fields != null) {
			Iterator it = fields.getChildIterator();			
			while(it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				field.put("required", field.getBoolean("required", false));
				field.put("readonly", field.getBoolean("readonly", false));
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
		map.put(PROPERTITY_DATAS, dataList.toString());
	}
}
