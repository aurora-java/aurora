package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class GridBoxConfig extends BoxConfig {

	public static final String VERSION = "$Revision$";

	public static final String TAG_NAME = "gridBox";

	public static final String PROPERTITY_COLUMNS = "columns";
	public static final String PROPERTITY_EDITOR = "editor";
	public static final String PROPERTITY_EDITORS = "editors";
	public static final String PROPERTITY_UNDERBOX = "underbox";

	public static GridBoxConfig getInstance() {
		GridBoxConfig model = new GridBoxConfig();
		model.initialize(GridBoxConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static GridBoxConfig getInstance(CompositeMap context) {
		GridBoxConfig model = new GridBoxConfig();
		model.initialize(GridBoxConfig.createContext(context, TAG_NAME));
		return model;
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

	public boolean getUnderBox() {
		return getBoolean(PROPERTITY_UNDERBOX, false);
	}

	public void setUnderBox(boolean underBox) {
		putBoolean(PROPERTITY_UNDERBOX, underBox);
	}

	public Integer getColumn() {
		Integer cols = getInteger(PROPERTITY_COLUMN);
		if (null == cols) {
			cols = new Integer(getColumns().getChilds().size());
		}
		return cols;
	}

	public void setColumn(int column) {
		putInt(PROPERTITY_COLUMN, column);
	}
}
