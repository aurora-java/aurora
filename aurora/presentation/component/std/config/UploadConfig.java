package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class UploadConfig extends ComponentConfig {
	
	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "upload";
	
	public static final String PROPERTITY_TYPE = "type";
	public static final String PROPERTITY_TEXT = "text";
	public static final String PROPERTITY_SOURCE_TYPE = "sourcetype";
	public static final String PROPERTITY_PK_VALUE = "pkvalue";
	public static final String PROPERTITY_TOTAL_FILE_SIZE = "totalfilesize";
	public static final String PROPERTITY_FILE_SIZE = "filesize";
	public static final String PROPERTITY_FILE_TYPE = "filetype";
	public static final String PROPERTITY_BUTTON_WIDTH = "buttonwidth";
	public static final String PROPERTITY_UPLOAD_URL = "uploadurl";
	public static final String PROPERTITY_DELETE_URL = "deleteurl";
	public static final String PROPERTITY_DOWNLOAD_URL = "downloadurl";
	public static final String PROPERTITY_SHOW_DELETE = "showdelete";
	public static final String PROPERTITY_SHOW_UPLOAD = "showupload";
	public static final String PROPERTITY_SHOW_LIST = "showlist";
	public static final String PROPERTITY_SORT_SQL = "sortsql";
	public static final String PROPERTITY_REQUIRE_SESSION = "requiresession";

	public static final String DEFAULT_TYPE = "default";
	public static final String DEFAULT_SORT_SQL = "creation_date desc";
	
	public static UploadConfig getInstance() {
		UploadConfig model = new UploadConfig();
		model.initialize(GridConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static UploadConfig getInstance(CompositeMap context) {
		UploadConfig model = new UploadConfig();
		model.initialize(GridConfig.createContext(context, TAG_NAME));
		return model;
	}
	
	public String getType() {
		return getString(PROPERTITY_TYPE);
	}
	
	public void setType(String type){
		putString(PROPERTITY_TYPE, type);
	}

	
	public String getText() {
		return getString(PROPERTITY_TEXT,"upload");
	}
	
	public void setText(String text){
		putString(PROPERTITY_TEXT, text);
	}
	
	public String getSourceType() {
		return getString(PROPERTITY_SOURCE_TYPE,"sourcetype");
	}
	
	public void setSourceType(String sourceType){
		putString(PROPERTITY_SOURCE_TYPE, sourceType);
	}
	
	public String getPKValue() {
		return getString(PROPERTITY_PK_VALUE,"pkvalue");
	}
	
	public void setPKValue(String pkValue){
		putString(PROPERTITY_PK_VALUE, pkValue);
	}
	
	public String getFileType() {
		return getString(PROPERTITY_FILE_TYPE,"*.*");
	}
	
	public void setFileType(String fileType){
		putString(PROPERTITY_FILE_TYPE, fileType);
	}
	
	public String getUploadURL() {
		return getString(PROPERTITY_UPLOAD_URL);
	}
	
	
	public void setSortSql(String sql){
		putString(PROPERTITY_SORT_SQL, sql);
	}
	
	public String getSortSql() {
		return getString(PROPERTITY_SORT_SQL,DEFAULT_SORT_SQL);
	}
	
	public String getUploadURL(String defaultValue) {
		return getString(PROPERTITY_UPLOAD_URL,defaultValue);
	}
	
	public String getDeleteURL() {
		return getString(PROPERTITY_DELETE_URL);
	}
	
	public String getDeleteURL(String defaultValue) {
		return getString(PROPERTITY_DELETE_URL,defaultValue);
	}
	
	public void setDeleteURL(String deleteURL){
		putString(PROPERTITY_DELETE_URL, deleteURL);
	}
	
	public String getDownloadURL() {
		return getString(PROPERTITY_DOWNLOAD_URL);
	}
	
	public String getDownloadURL(String defaultValue) {
		return getString(PROPERTITY_DOWNLOAD_URL,defaultValue);
	}
	
	public void setDownloadURL(String downloadURL){
		putString(PROPERTITY_DOWNLOAD_URL, downloadURL);
	}
	
	public boolean isShowUpload(){
		return getBoolean(PROPERTITY_SHOW_UPLOAD, true);
	}
	
	public void setShowUpload(boolean showUpload){
		putBoolean(PROPERTITY_SHOW_UPLOAD, showUpload);
	}
	
	public boolean isShowDelete(){
		return getBoolean(PROPERTITY_SHOW_DELETE, true);
	}
	
	public void setShowDelete(boolean showDelete){
		putBoolean(PROPERTITY_SHOW_DELETE, showDelete);
	}
	
	public boolean isRequireSession(){
		return getBoolean(PROPERTITY_REQUIRE_SESSION, true);
	}
	
	public void setRequireSession(boolean requireSession){
		putBoolean(PROPERTITY_REQUIRE_SESSION, requireSession);
	}
	
	
	public boolean isShowList(){
		return getBoolean(PROPERTITY_SHOW_LIST, true);
	}
	
	public void setShowList(boolean showList){
		putBoolean(PROPERTITY_SHOW_LIST, showList);
	}
	
	public int getButtonWidth(){
		return getInt(PROPERTITY_BUTTON_WIDTH, 50);
	}
	
	public void setButtonWidth(int buttonWidth){
		putInt(PROPERTITY_BUTTON_WIDTH, buttonWidth);
	}
	
	public int getFileSize(){
		return getInt(PROPERTITY_FILE_SIZE, 0);
	}
	
	public void setFileSize(int fileSize){
		putInt(PROPERTITY_FILE_SIZE, fileSize);
	}
	
	
	public int getTotalFileSize(){
		return getInt(PROPERTITY_TOTAL_FILE_SIZE, 0);
	}
	
	public void setTotalFileSize(int tfs){
		putInt(PROPERTITY_TOTAL_FILE_SIZE, tfs);
	}
	
	
}
