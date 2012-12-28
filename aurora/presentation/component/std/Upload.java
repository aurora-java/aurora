package aurora.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;

import aurora.application.AuroraApplication;
import aurora.application.config.ScreenConfig;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.DataSetConfig;
import aurora.presentation.component.std.config.GridConfig;
import aurora.presentation.component.std.config.TableColumnConfig;
import aurora.presentation.component.std.config.TableConfig;
import aurora.service.IService;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class Upload extends Component {
	
	public static final String VERSION = "$Revision$";
	
	public static final String PROPERTITY_TEXT = "text";
	public static final String PROPERTITY_SOURCE_TYPE = "sourcetype";
	public static final String PROPERTITY_PK_VALUE = "pkvalue";
	public static final String PROPERTITY_FILE_SIZE = "filesize";
	public static final String PROPERTITY_FILE_TYPE = "filetype";
	public static final String PROPERTITY_BUTTON_WIDTH = "buttonwidth";
	public static final String PROPERTITY_UPLOAD_URL = "uploadurl";
	public static final String PROPERTITY_DELETE_URL = "deleteurl";
	public static final String PROPERTITY_DOWNLOAD_URL = "downloadurl";
	public static final String PROPERTITY_SHOW_DELETE = "showdelete";
	public static final String PROPERTITY_SHOW_UPLOAD = "showupload";
	public static final String PROPERTITY_SHOW_LIST = "showlist";

	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		addStyleSheet(session, context, "table/Table-min.css");
		addStyleSheet(session, context, "upload/upload.css");
		addJavaScript(session, context, "table/Table-min.js");
		addJavaScript(session, context, "upload/swfupload.js");
		addJavaScript(session, context, "upload/swfupload.queue.js");
		addJavaScript(session, context, "upload/handler.js");
	}
	
	public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{	
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		CompositeMap model = context.getModel();
		String id = view.getString(ComponentConfig.PROPERTITY_ID);
		
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
		
		boolean showUpload = view.getBoolean(PROPERTITY_SHOW_UPLOAD, true);
		if(!showUpload) {
			map.put(PROPERTITY_SHOW_UPLOAD, "none");
		}
		boolean showDelete = view.getBoolean(PROPERTITY_SHOW_DELETE, true);
		if(!showDelete) {
			tb_column.put(TableColumnConfig.PROPERTITY_RENDERER, "atmNotDeleteRenderer");
		}else {
			tb_column.put(TableColumnConfig.PROPERTITY_RENDERER, "atmRenderer");
		}
		
		tb_columns.addChild(tb_column);
		try {
			boolean showList = view.getBoolean(PROPERTITY_SHOW_LIST, true);
			map.put("linestyle", showList ? "block" : "none");
			if(showList)
			map.put("up_table", session.buildViewAsString(model, tb));
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		
		String text = view.getString(PROPERTITY_TEXT,"upload");
		text = session.getLocalizedPrompt(text);
		map.put(PROPERTITY_TEXT, text);
		String st = view.getString(PROPERTITY_SOURCE_TYPE, "sourcetype");
		st = uncertain.composite.TextParser.parse(st, model);
		map.put(PROPERTITY_SOURCE_TYPE, st);
		String pk = view.getString(PROPERTITY_PK_VALUE, "pkvalue");
		pk = uncertain.composite.TextParser.parse(pk, model);
		map.put(PROPERTITY_PK_VALUE, pk);
		String context_path = model.getObject("/request/@context_path").toString();
		map.put("context_path", context_path);
		
		map.put(PROPERTITY_BUTTON_WIDTH, new Integer(view.getInt(PROPERTITY_BUTTON_WIDTH, 50)));
		map.put(PROPERTITY_FILE_SIZE, new Integer(view.getInt(PROPERTITY_FILE_SIZE, 0)));
		map.put(PROPERTITY_FILE_TYPE, view.getString(PROPERTITY_FILE_TYPE, "*.*"));
		String uplloadUrl = view.getString(PROPERTITY_UPLOAD_URL, context_path + "/atm_upload.svc");
		map.put(PROPERTITY_UPLOAD_URL, uncertain.composite.TextParser.parse(uplloadUrl, model));
		String deleteUrl = view.getString(PROPERTITY_DELETE_URL, context_path + "/atm_delete.svc");
		map.put(PROPERTITY_DELETE_URL, uncertain.composite.TextParser.parse(deleteUrl, model));
		String downloadUrl = view.getString(PROPERTITY_DOWNLOAD_URL, context_path + "/atm_download.svc");
		map.put(PROPERTITY_DOWNLOAD_URL, uncertain.composite.TextParser.parse(downloadUrl, model));
		
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(model.getRoot());
		map.put("sessionId", serviceInstance.getRequest().getSession().getId());
		map.put(CONFIG, getConfigString());
	}
}
