package aurora.presentation.component.std;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.TableColumnConfig;
import aurora.presentation.component.std.config.TableConfig;
import aurora.presentation.component.std.config.UploadConfig;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;
import aurora.service.http.UserAgentTools;

@SuppressWarnings("unchecked")
public class Upload extends Component {
	
	public Upload(IObjectRegistry registry) {
		super(registry);
	}


	public static final String VERSION = "$Revision$";
	public static final String HTML5_TEMPLATE = "upload_html5.tplt";
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "table/Table-min.css");
		addStyleSheet(session, context, "upload/upload.css");
		addJavaScript(session, context, "table/Table-min.js");
		addJavaScript(session, context, "upload/html5upload.js");
		addJavaScript(session, context, "upload/swfupload.js");
		addJavaScript(session, context, "upload/swfupload.queue.js");
		addJavaScript(session, context, "upload/handler.js");
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{	
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		UploadConfig uc = UploadConfig.getInstance(view);
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(model.getRoot());
		
		boolean showList = uc.isShowList();
		map.put("linestyle", showList ? "block" : "none");
		
		
		if(!UploadConfig.DEFAULT_TYPE.equals(uc.getType()) && isSupportFileAPI(serviceInstance.getRequest())) {
			context.setTemplate(session.getTemplateByName(HTML5_TEMPLATE));
			processHtml5Upload(view,map,model,session);
		}else {
			processNormalUpload(view,map,model,session);
		}
		
	}
	
	private void processHtml5Upload(CompositeMap view, Map  map, CompositeMap model,BuildSession session) throws IOException{
		UploadConfig uc = UploadConfig.getInstance(view);
		addUploadCard(uc,map,session,model);
		if(!uc.isShowUpload()) {
			map.put(UploadConfig.PROPERTITY_SHOW_UPLOAD, "none");
		}else {
			HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(model.getRoot());
			String sid = "";
			if(uc.isRequireSession()) {
				sid = serviceInstance.getRequest().getSession(true).getId();
			}
			map.put("sessionId", sid);			
		}
		map.put(UploadConfig.PROPERTITY_TEXT, session.getLocalizedPrompt(uc.getText()));
		addConfig(UploadConfig.PROPERTITY_SHOW_UPLOAD, uc.isShowUpload());
		addConfig(UploadConfig.PROPERTITY_SOURCE_TYPE, uncertain.composite.TextParser.parse(uc.getSourceType(), model));
		addConfig(UploadConfig.PROPERTITY_PK_VALUE, uncertain.composite.TextParser.parse(uc.getPKValue(), model));
		String context_path = model.getObject("/request/@context_path").toString();
		map.put("context_path", context_path);
		addConfig(UploadConfig.PROPERTITY_FILE_SIZE, new Integer(uc.getFileSize()));
		addConfig(UploadConfig.PROPERTITY_TOTAL_FILE_SIZE, new Integer(uc.getTotalFileSize()));
		addConfig(UploadConfig.PROPERTITY_FILE_TYPE, uc.getFileType());
		addConfig(UploadConfig.PROPERTITY_UPLOAD_URL, uncertain.composite.TextParser.parse(uc.getUploadURL(context_path + "/atm_upload.svc"), model));
		addConfig(UploadConfig.PROPERTITY_DELETE_URL, uncertain.composite.TextParser.parse(uc.getDeleteURL(context_path + "/atm_delete.svc"), model));
		addConfig(UploadConfig.PROPERTITY_DOWNLOAD_URL, uncertain.composite.TextParser.parse(uc.getDownloadURL(context_path + "/atm_download.svc"), model));
		map.put(CONFIG, getConfigString());
	}
	
	private void addUploadCard(UploadConfig uc,Map map,BuildSession session,CompositeMap model) {
		String uid = uc.getId() + "_ul";
		map.put("up_table", "<div id=\""+uid+"\"> </div>"); 
		JSONObject config = new JSONObject();
		String context_path = model.getObject("/request/@context_path").toString();
		try {
			config.put(ComponentConfig.PROPERTITY_ID, uid);
			config.put(ComponentConfig.PROPERTITY_BINDTARGET, uc.getId() + "_ds");
			config.put(UploadConfig.PROPERTITY_SHOW_DELETE, uc.isShowDelete());
			config.put(UploadConfig.PROPERTITY_DELETE_URL, uncertain.composite.TextParser.parse(uc.getDeleteURL(context_path + "/atm_delete.svc"), model));
			config.put(UploadConfig.PROPERTITY_DOWNLOAD_URL, uncertain.composite.TextParser.parse(uc.getDownloadURL(context_path + "/atm_download.svc"), model));
			
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		
		map.put("upload_list", "new Aurora.UploadList("+config.toString()+")");
	}
	
	
	private void addUploadList(UploadConfig uc,Map map,BuildSession session,CompositeMap model) throws IOException{
		CompositeMap tb = new CompositeMap(TableConfig.TAG_NAME);
		tb.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		tb.put(TableConfig.PROPERTITY_PERCENT_WIDTH, new Integer(100));
		tb.put(ComponentConfig.PROPERTITY_CLASSNAME, "atmList");
		tb.put(TableConfig.PROPERTITY_SHOW_HEAD, new Boolean(false));
		tb.put(ComponentConfig.PROPERTITY_BINDTARGET, id + "_ds");
		tb.put(ComponentConfig.PROPERTITY_STYLE, "border:none;background-color:#fff");
		CompositeMap tb_columns = new CompositeMap(TableConfig.PROPERTITY_COLUMNS);
		tb_columns.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		tb.addChild(tb_columns);
		CompositeMap tb_column = new CompositeMap(TableColumnConfig.TAG_NAME);
		tb_column.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
		tb_column.put(TableColumnConfig.PROPERTITY_PERCENT_WIDTH, new Integer(100));
		tb_column.put(TableColumnConfig.PROPERTITY_NAME, "file_name");
		
		if(!uc.isShowDelete()) {
			tb_column.put(TableColumnConfig.PROPERTITY_RENDERER, "atmNotDeleteRenderer");
		}else {
			tb_column.put(TableColumnConfig.PROPERTITY_RENDERER, "atmRenderer");
		}
		
		tb_columns.addChild(tb_column);
		try {
			boolean showList = uc.isShowList();
//			map.put("linestyle", showList ? "block" : "none");
			if(showList)
			map.put("up_table", session.buildViewAsString(model, tb));
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	
	
	private void processNormalUpload(CompositeMap view, Map  map, CompositeMap model,BuildSession session) throws IOException{
		
		UploadConfig uc = UploadConfig.getInstance(view);
		String id = uc.getId();
		if(!uc.isShowUpload()) {
			map.put(UploadConfig.PROPERTITY_SHOW_UPLOAD, "none");
		}else {
			String sid = "";
			if(uc.isRequireSession()) {
				HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(model.getRoot());
				sid = serviceInstance.getRequest().getSession(true).getId();
			}
			map.put("sessionId", sid);
		}
		
		
		if(UploadConfig.DEFAULT_TYPE.equals(uc.getType())) {
			addUploadList(uc,map,session,model);
		}else {
			addUploadCard(uc,map,session,model);
		}
		
		map.put(UploadConfig.PROPERTITY_TEXT, session.getLocalizedPrompt(uc.getText()));
		map.put(UploadConfig.PROPERTITY_SOURCE_TYPE, uncertain.composite.TextParser.parse(uc.getSourceType(), model));
		map.put(UploadConfig.PROPERTITY_PK_VALUE, uncertain.composite.TextParser.parse(uc.getPKValue(), model));
		String context_path = model.getObject("/request/@context_path").toString();
		map.put("context_path", context_path);
		
		map.put(UploadConfig.PROPERTITY_BUTTON_WIDTH, new Integer(uc.getButtonWidth()));
		map.put(UploadConfig.PROPERTITY_FILE_SIZE, new Integer(uc.getFileSize()));
		map.put(UploadConfig.PROPERTITY_TOTAL_FILE_SIZE, new Integer(uc.getTotalFileSize()));
		map.put(UploadConfig.PROPERTITY_FILE_TYPE, uc.getFileType());
		map.put(UploadConfig.PROPERTITY_UPLOAD_URL, uncertain.composite.TextParser.parse(uc.getUploadURL(context_path + "/atm_upload.svc"), model));
		map.put(UploadConfig.PROPERTITY_DELETE_URL, uncertain.composite.TextParser.parse(uc.getDeleteURL(context_path + "/atm_delete.svc"), model));
		map.put(UploadConfig.PROPERTITY_DOWNLOAD_URL, uncertain.composite.TextParser.parse(uc.getDownloadURL(context_path + "/atm_download.svc"), model));
		
		
		
		map.put(CONFIG, getConfigString());
	}
	
	
	private boolean isSupportFileAPI(HttpServletRequest request){
		Map m = new HashMap();
		m.put("chrome", 7);
		m.put("firefox", 4);
		m.put("opera", 12);
		m.put("safari", 5);
		String agent = request.getHeader("User-Agent");
		String[] browsers = UserAgentTools.getBrowser(agent);
		String browser1 = browsers[0];
		if("MSIE 10.0".equals(browser1)) return true;
		String browser = browsers[1];
		String version = browsers[2];
		Iterator it = m.keySet().iterator();
		while(it.hasNext()){
			String br = (String)it.next();
			if(br.equalsIgnoreCase(browser)){
				int bigVersion = Integer.parseInt(version.split("\\.")[0]);
				if(bigVersion>=(Integer)m.get(br)) return true;
			}
		}
		return false;
	}
}
