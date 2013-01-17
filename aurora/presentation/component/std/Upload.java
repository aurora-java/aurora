package aurora.presentation.component.std;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.TableColumnConfig;
import aurora.presentation.component.std.config.TableConfig;
import aurora.presentation.component.std.config.UploadConfig;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class Upload extends Component {
	
	public static final String VERSION = "$Revision$";
	
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
		UploadConfig uc = UploadConfig.getInstance(view);
		String id = uc.getId();
		
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
		
		if(!uc.isShowUpload()) {
			map.put(UploadConfig.PROPERTITY_SHOW_UPLOAD, "none");
		}
		if(!uc.isShowDelete()) {
			tb_column.put(TableColumnConfig.PROPERTITY_RENDERER, "atmNotDeleteRenderer");
		}else {
			tb_column.put(TableColumnConfig.PROPERTITY_RENDERER, "atmRenderer");
		}
		
		tb_columns.addChild(tb_column);
		try {
			boolean showList = uc.isShowList();
			map.put("linestyle", showList ? "block" : "none");
			if(showList)
			map.put("up_table", session.buildViewAsString(model, tb));
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		
		map.put(UploadConfig.PROPERTITY_TEXT, session.getLocalizedPrompt(uc.getText()));
		map.put(UploadConfig.PROPERTITY_SOURCE_TYPE, uncertain.composite.TextParser.parse(uc.getSourceType(), model));
		map.put(UploadConfig.PROPERTITY_PK_VALUE, uncertain.composite.TextParser.parse(uc.getPKValue(), model));
		String context_path = model.getObject("/request/@context_path").toString();
		map.put("context_path", context_path);
		
		map.put(UploadConfig.PROPERTITY_BUTTON_WIDTH, new Integer(uc.getButtonWidth()));
		map.put(UploadConfig.PROPERTITY_FILE_SIZE, new Integer(uc.getFileSize()));
		map.put(UploadConfig.PROPERTITY_FILE_TYPE, uc.getFileType());
		map.put(UploadConfig.PROPERTITY_UPLOAD_URL, uncertain.composite.TextParser.parse(uc.getUploadURL(context_path + "/atm_upload.svc"), model));
		map.put(UploadConfig.PROPERTITY_DELETE_URL, uncertain.composite.TextParser.parse(uc.getDeleteURL(context_path + "/atm_delete.svc"), model));
		map.put(UploadConfig.PROPERTITY_DOWNLOAD_URL, uncertain.composite.TextParser.parse(uc.getDownloadURL(context_path + "/atm_download.svc"), model));
		
		HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(model.getRoot());
		map.put("sessionId", serviceInstance.getRequest().getSession().getId());
		map.put(CONFIG, getConfigString());
	}
}
