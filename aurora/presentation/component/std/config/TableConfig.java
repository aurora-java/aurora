package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class TableConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";

	public static final String TAG_NAME = "table";

	public static final String PROPERTITY_TITLE = "title";
	public static final String PROPERTITY_COLUMNS = "columns";
	public static final String PROPERTITY_EDITORS = "editors";
	public static final String PROPERTITY_DATASET = "dataset";
	public static final String PROPERTITY_NAVBAR = "navbar";
	public static final String PROPERTITY_ROW_RENDERER = "rowrenderer";
	public static final String PROPERTITY_PERCENT_WIDTH = "percentwidth";
	public static final String PROPERTITY_SHOW_HEAD = "showhead";
	public static final String PROPERTITY_CAN_WHEEL = "canwheel";
	public static final String PROPERTITY_AUTO_APPEND = "autoappend";
	public static final String PROPERTITY_GROUP_SELECT = "groupselect";

	public static TableConfig getInstance() {
		TableConfig model = new TableConfig();
		model.initialize(TableConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static TableConfig getInstance(CompositeMap context) {
		TableConfig model = new TableConfig();
		model.initialize(TableConfig.createContext(context, TAG_NAME));
		return model;
	}

	public boolean isShowHead() {
		return getBoolean(PROPERTITY_SHOW_HEAD, true);
	}

	public void setShowHead(boolean isShow) {
		putBoolean(PROPERTITY_SHOW_HEAD, isShow);
	}

	public String getTitle(CompositeMap model) {
		return uncertain.composite.TextParser.parse(getString(PROPERTITY_TITLE,""), model);
	}
	
	public void setTitle(String title) {
		putString(PROPERTITY_TITLE, title);
	}
	public String getRowRenderer() {
		return getString(PROPERTITY_ROW_RENDERER);
	}

	public void setRowRenderer(String renderer) {
		putString(PROPERTITY_ROW_RENDERER, renderer);
	}

	public String getPercentWidth() {
		return getString(PROPERTITY_PERCENT_WIDTH);
	}

	public void setPercentWidth(String percentWidth) {
		putString(PROPERTITY_PERCENT_WIDTH, percentWidth);
	}

	public String getDataSet() {
		return getString(PROPERTITY_DATASET);
	}

	public void setDataSet(String ds) {
		putString(PROPERTITY_DATASET, ds);
	}

	public CompositeMap getColumns() {
		CompositeMap context = getObjectContext();
		CompositeMap columns = context.getChild(PROPERTITY_COLUMNS);
		if (columns == null) {
			columns = new CompositeMap(PROPERTITY_COLUMNS);
			context.addChild(columns);
		}
		return columns;
	}

	public void addColumn(GridColumnConfig column) {
		CompositeMap columns = getColumns();
		columns.addChild(column.getObjectContext());
	}

	public CompositeMap getEditors() {
		CompositeMap context = getObjectContext();
		CompositeMap editors = context.getChild(PROPERTITY_EDITORS);
		if (editors == null) {
			editors = new CompositeMap(PROPERTITY_EDITORS);
			context.addChild(editors);
		}
		return editors;
	}

	public void addEditor(ComponentConfig editor) {
		CompositeMap editors = getEditors();
		editors.addChild(editor.getObjectContext());
	}

	public boolean hasNavBar() {
		return getBoolean(PROPERTITY_NAVBAR, false);
	}

	public void setNavBar(boolean nb) {
		putBoolean(PROPERTITY_NAVBAR, nb);
	}

	public boolean isCanWheel() {
		return getBoolean(PROPERTITY_CAN_WHEEL, true);
	}

	public void setCanWheel(boolean canPaste) {
		putBoolean(PROPERTITY_CAN_WHEEL, canPaste);
	}

	public boolean isAutoAppend() {
		return getBoolean(PROPERTITY_AUTO_APPEND, true);
	}

	public void setAutoAppend(boolean append) {
		putBoolean(PROPERTITY_AUTO_APPEND, append);
	}

	public Boolean isGroupSelect(){
		return getBoolean(PROPERTITY_GROUP_SELECT, true);
	}
	public void setGroupSelect(boolean nb){
		putBoolean(PROPERTITY_GROUP_SELECT, nb);
	}
	public String getWidthStr() {
		return getString(PROPERTITY_WIDTH);
	}
}
