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
import uncertain.ocm.IObjectRegistry;
import aurora.application.ApplicationConfig;
import aurora.application.ApplicationViewConfig;
import aurora.application.IApplicationConfig;
import aurora.application.features.ILookupCodeProvider;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.IModelFactory;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.DataSetFieldConfig;
import aurora.presentation.component.std.config.LovConfig;

/**
 * 
 * @version $Id$
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class DataSet extends Component {
	
	public static final String VERSION = "$Revision$";
	
	private static final String VALID_SCRIPT = "validscript";
	private IModelFactory mFactory;
	private ILookupCodeProvider lookupProvider;
	
	public DataSet(IObjectRegistry registry,IModelFactory factory, ILookupCodeProvider lookupProvider) {
		super(registry);
        this.mFactory = factory;
        this.lookupProvider = lookupProvider;
    }
	
//	public DataSet(IObjectRegistry registry,IModelFactory factory){
//        this.mRegistry = registry;
//        this.mFactory = factory;
//        mApplicationConfig = (ApplicationConfig) mRegistry.getInstanceOfType(IApplicationConfig.class);
//	}
	
	private void initLovService(String baseModel,BuildSession session,CompositeMap field) throws IOException{
		BusinessModel bm = null;
        bm = mFactory.getModelForRead(baseModel.split("\\?")[0]);
        Field[] bmfields = bm.getFields();
        JSONArray lovDisplayFields = new JSONArray();
        if(null!=bmfields){
        	for(int i =0,l = bmfields.length;i<l;i++){
        		Field f = bmfields[i];
        		if(f.isForDisplay()){
        			DataSetFieldConfig dfc = DataSetFieldConfig.getInstance(f.getObjectContext());
        			dfc.setPrompt(session.getLocalizedPrompt(dfc.getPrompt()));
        			lovDisplayFields.put(new JSONObject(dfc.getObjectContext()));
        		}
        	}
        }
        field.put("displayFields",lovDisplayFields);
	}
	
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		CompositeMap model = context.getModel();
		Map map = context.getMap();
		JSONArray fieldList = new JSONArray(); 
		
		int mDefaultPageSize = -1;
		boolean mDefaultModifiedCheck = true;
		boolean autoCount = true;
		if (mApplicationConfig != null) {
	   	     ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
	   	     if (view_config != null) {
	   	    	mDefaultPageSize = view_config.getDefaultPageSize();      
	   	    	mDefaultModifiedCheck = view_config.getDefaultModifiedCheck();      
	   	    	autoCount = view_config.getDefaultAutoCount();
	   	     }
	   	}
		ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
		DataSetConfig dsc = DataSetConfig.getInstance(view);
		CompositeMap fields = dsc.getFields();
		if(fields != null) {
			Iterator it = fields.getChildIterator();
			if(it != null)
			while(it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				DataSetFieldConfig sdfc = DataSetFieldConfig.getInstance(field);

				if(null!=field.getString(DataSetFieldConfig.PROPERTITY_REQUIRED))
					field.putBoolean(DataSetFieldConfig.PROPERTITY_REQUIRED, sdfc.getRequired());
				if(null!=field.getString(DataSetFieldConfig.PROPERTITY_READONLY))
					field.putBoolean(DataSetFieldConfig.PROPERTITY_READONLY, sdfc.getReadOnly());
				if(null!=field.getString(DataSetFieldConfig.PROPERTITY_EDITABLE))
					field.putBoolean(DataSetFieldConfig.PROPERTITY_EDITABLE, sdfc.getEditable());
//				if(null!=field.getString(DataSetFieldConfig.PROPERTITY_TOOLTIP))
//					field.putString(DataSetFieldConfig.PROPERTITY_TOOLTIP, sdfc.getTooltip());
				if(null!=field.getString(DataSetFieldConfig.PROPERTITY_AUTO_COMPLETE))
					field.putBoolean(DataSetFieldConfig.PROPERTITY_AUTO_COMPLETE, sdfc.getAutoComplete());
				field.putBoolean(DataSetFieldConfig.PROPERTITY_FUZZY_FETCH, sdfc.getFuzzyFetch()==null?view_config.getDefaultFuzzyFetch():sdfc.getFuzzyFetch());
				if(sdfc.getDefaultValue()!=null)field.putString(DataSetFieldConfig.PROPERTITY_DEFAULTVALUE, session.parseString(sdfc.getDefaultValue(), model));
				
				String options = field.getString(DataSetFieldConfig.PROPERTITY_OPTIONS);
				if(options!=null){
					field.putString(DataSetFieldConfig.PROPERTITY_OPTIONS, uncertain.composite.TextParser.parse(options, model));
				}
				
				if(null!=field.getString(DataSetFieldConfig.PROPERTITY_FETCH_REMOTE))
					field.putBoolean(DataSetFieldConfig.PROPERTITY_FETCH_REMOTE, sdfc.getFetchRemote());
				if(null!=field.getString(DataSetFieldConfig.PROPERTITY_FETCH_SINGLE))
					field.putBoolean(DataSetFieldConfig.PROPERTITY_FETCH_SINGLE, sdfc.getFetchSingle());
				
				String lovService = field.getString(DataSetFieldConfig.PROPERTITY_LOV_SERVICE);
				if(lovService!=null && lovService.length()>0){
					String baseModel = uncertain.composite.TextParser.parse(lovService, model);
					field.putString(DataSetFieldConfig.PROPERTITY_LOV_SERVICE,baseModel);
					initLovService(baseModel,session,field);
				}
				String lovModel = field.getString(DataSetFieldConfig.PROPERTITY_LOV_MODEL);
				if(lovModel!=null){
					String baseModel = uncertain.composite.TextParser.parse(lovModel, model);
					field.putString(DataSetFieldConfig.PROPERTITY_LOV_MODEL,baseModel);
					initLovService(baseModel,session,field);
				}
				String lovUrl = field.getString(DataSetFieldConfig.PROPERTITY_LOV_URL);
				if(lovUrl!=null){
					field.putString(DataSetFieldConfig.PROPERTITY_LOV_URL, uncertain.composite.TextParser.parse(lovUrl, model));
				}
				String lovTitle = field.getString(DataSetFieldConfig.PROPERTITY_TITLE);
				if(lovTitle!=null){
					field.putString(DataSetFieldConfig.PROPERTITY_TITLE, session.getLocalizedPrompt(lovTitle));
				}
				String requiredMessage = field.getString(DataSetFieldConfig.PROPERTITY_REQUIRED_MESSAGE);
				if(requiredMessage!=null){
					field.putString(DataSetFieldConfig.PROPERTITY_REQUIRED_MESSAGE, uncertain.composite.TextParser.parse(requiredMessage,model));
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
						throw new IOException(e);
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
			ILookupCodeProvider provider = this.lookupProvider;
			if(provider!=null){
				list = new ArrayList();
				try {
					list = provider.getLookupList(session.getLanguage(), lcode);
				} catch (Exception e) {
					throw new IOException(e);
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
			Iterator lit = list.iterator();
			while(lit.hasNext()){
				dataHead.addAll(((CompositeMap)lit.next()).keySet());
			}
			
			
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
		if(!"".equals(dsc.getQueryUrl(model))){
			String queryUrl = uncertain.composite.TextParser.parse(dsc.getQueryUrl(model), model);
			addConfig(DataSetConfig.PROPERTITY_QUERYURL, queryUrl);
		}
		if(!"".equals(dsc.getSubmitUrl())) {
			String submitUrl = uncertain.composite.TextParser.parse(dsc.getSubmitUrl(), model);
			addConfig(DataSetConfig.PROPERTITY_SUBMITURL,submitUrl);
		}
		if(!"".equals(dsc.getBindTarget()))addConfig(DataSetConfig.PROPERTITY_BINDTARGET, uncertain.composite.TextParser.parse(dsc.getBindTarget(), model));
		if(!"".equals(dsc.getBindName()))addConfig(DataSetConfig.PROPERTITY_BINDNAME, uncertain.composite.TextParser.parse(dsc.getBindName(), model));
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
		addConfig(DataSetConfig.PROPERTITY_TOTALCOUNT_FIELD, dsc.getTotalCountField());
		addConfig(DataSetConfig.PROPERTITY_MODIFIED_CHECK, dsc.isModifiedCheck(mDefaultModifiedCheck));
		
		BusinessModel bm = null;
		Integer mps = null;
		String md = dsc.getModel();
		if(md!=null)bm = mFactory.getModelForRead(uncertain.composite.TextParser.parse(md, model));
		if(bm!=null){
			mps = bm.getMaxPageSize();
		}
		if(mps!=null) {
			addConfig(DataSetConfig.PROPERTITY_MAX_PAGESIZE, new Integer(mps));
		}else{
			addConfig(DataSetConfig.PROPERTITY_MAX_PAGESIZE, new Integer(dsc.getMaxPageSize()));
		}
		int page_size = mDefaultPageSize < 0 ? dsc.getPageSize(model) : mDefaultPageSize;
		addConfig(DataSetConfig.PROPERTITY_PAGESIZE, new Integer(page_size));
		
		Boolean isAutoCount = (dsc.isAutoCount() != null) ? dsc.isAutoCount() : autoCount;
		addConfig(DataSetConfig.PROPERTITY_AUTO_COUNT, isAutoCount);
		if(dsc.getSortType() !=null) addConfig(DataSetConfig.PROPERTITY_SORT_TYPE, dsc.getSortType());		
		if(dsc.getNotification() !=null) addConfig(DataSetConfig.PROPERTITY_NOTIFICATION, dsc.getNotification());
		
		if(dsc.isAutoCreate())addConfig(DataSetConfig.PROPERTITY_AUTO_CREATE, new Boolean(dsc.isAutoCreate()));
		if(dsc.isSelectable())addConfig(DataSetConfig.PROPERTITY_SELECTABLE, new Boolean(dsc.isSelectable()));
		if(null!=dsc.getSelectFunction())addConfig(DataSetConfig.PROPERTITY_SELECT_FUNCTION,dsc.getSelectFunction());
		if(!DataSetConfig.DEFAULT_SELECTION_MODEL.equals(dsc.getSelectionModel()))addConfig(DataSetConfig.PROPERTITY_SELECTION_MODEL, dsc.getSelectionModel());
		String pf = uncertain.composite.TextParser.parse(dsc.getProcessFunction(),model);
		if(!"".equals(pf)){
			addConfig(DataSetConfig.PROPERTITY_PROCESS_FUNCTION, pf);
		}
		map.put(CONFIG, getConfigString());
	}
}
