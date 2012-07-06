package aurora.application.features.transform;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import uncertain.composite.CompositeComparator;
import uncertain.composite.CompositeMap;
import uncertain.composite.transform.AbstractTransform;
import uncertain.core.ConfigurationError;
import uncertain.proc.ProcedureRunner;
import uncertain.util.StringSplitter;

/**
 * * <code>
 *     	<matrix-transform source="/model/data" target="/model/target" put-value-only="true" shared-field="SHARE_FIELD1,SHARE_FIELD2..." column-field="COLUMN1,COLUMN2..." value-field="VALUE1,VALUE2..." group-field="GROUP_FIELD"/>
 *   </code>
 */
public class MatrixTransformerElement extends AbstractTransform {
	final String KEY_SOURCE = "source";
	final String KEY_TARGET = "target";

	CompositeMap elementConifg;

	public static final String KEY_GROUP_FIELD = "group-field";
	public static final String KEY_COLUMN_FIELD = "column-field";
	public static final String KEY_VALUE_FIELD = "value-field";
	public static final String KEY_SHARED_FIELD = "shared-field";
	public static final String KEY_PUT_VALUE_ONLY = "put-value-only";
	public static final String KEY_COLUMN_LIST = "_column_list";

	// fields for getter/setter
	Object rowKeyField;
	Object colKeyField;
	String[] colValueFields;
	String[] columnFields;
	String[] sharedFields;
	boolean createMetaData = true;

	List transformedColumns;
	HashMap grouped_rows;
	HashMap grouped_cols;
	boolean fieldValueOnly = true;
	HashSet sharedFieldSet;

	/**
	 * @param rowKeyField
	 * @param colValueField
	 * @param columnFields
	 * @param fieldValueOnly
	 */
	public MatrixTransformerElement(Object rowKeyField, String columnFieldStr,
			String colValueFieldStr, boolean fieldValueOnly) {
		this();
		this.rowKeyField = rowKeyField;
		this.colValueFields = StringSplitter.splitToArray(colValueFieldStr,
				',', true);
		;
		this.columnFields = StringSplitter.splitToArray(columnFieldStr, ',',
				true);
		this.colKeyField = columnFields[0];
		this.fieldValueOnly = fieldValueOnly;
	}

	public MatrixTransformerElement(CompositeMap params) {
		this();
		init(params);
	}

	void assertParamNotNull(String name, Object v) {
		if (v == null)
			throw new ConfigurationError(
					"[MatrixTransformer] required parameter " + name
							+ " is not set");
	}

	public void init(CompositeMap params) {
		this.rowKeyField = params.get(KEY_GROUP_FIELD);
		this.colValueFields = StringSplitter.splitToArray(
				params.getString(KEY_VALUE_FIELD), ',', true);
		this.columnFields = StringSplitter.splitToArray(
				params.getString(KEY_COLUMN_FIELD), ',', true);
		this.sharedFields = StringSplitter.splitToArray(
				params.getString(KEY_SHARED_FIELD), ',', true);
		assertParamNotNull(KEY_GROUP_FIELD, this.rowKeyField);
		// assertParamNotNull(KEY_VALUE_FIELD,this.colValueFields);
		assertParamNotNull(KEY_COLUMN_FIELD, this.columnFields);
		this.colKeyField = columnFields[0];
		this.fieldValueOnly = params.getBoolean(KEY_PUT_VALUE_ONLY, true);

		if (sharedFields != null)
			if (sharedFields.length > 0) {
				sharedFieldSet = new HashSet();
				for (int i = 0; i < sharedFields.length; i++)
					sharedFieldSet.add(sharedFields[i]);
			}
	}

	/**
	 * Default
	 */
	public MatrixTransformerElement() {
		grouped_rows = new HashMap();
		grouped_cols = new HashMap();
		transformedColumns = new LinkedList();
	}

	public CompositeMap transform(CompositeMap source,
			CompositeMap transform_config) {
		init(transform_config);
		return transform(source);
	}

	public List getColumns() {
		return transformedColumns;
	}

	public CompositeMap transform(CompositeMap source) {

		if (source == null) {
			System.out.println("[MatrixTransformer] model is null");
			return null;
		}
		List lst = source.getChilds();
		if (lst == null) {
			// System.out.println("[MatrixTransformer] model has no child record");
			return source;
		}
		// System.out.println("Before transform: child "+lst.size());
		ListIterator it = lst.listIterator();

		while (it.hasNext()) {
			boolean new_row = false;
			CompositeMap item = (CompositeMap) it.next();
			Object row_value = item.get(rowKeyField);
			if (row_value == null) {
				System.out
						.println("[MatrixTransformer] record has not key field:"
								+ item.toXML());
			}
			// System.out.println("grouped_rows:"+grouped_rows.size());
			// group rows with same PK value
			CompositeMap target_map = (CompositeMap) grouped_rows
					.get(row_value);
			if (target_map == null) {
				grouped_rows.put(row_value, item);
				target_map = item;
				new_row = true;
				// System.out.println("new row "+row_value);
			} else {
				it.remove();

			}
			String col_field = item.getString(colKeyField);
			// Object col_value = item.get(colValueField);

			// create column
			CompositeMap col_config = (CompositeMap) grouped_cols
					.get(col_field);
			if ((col_config) != null) {
			} else {
				CompositeMap column = new CompositeMap("column");
				for (int i = 0; i < columnFields.length; i++)
					column.put(columnFields[i], item.get(columnFields[i]));
				transformedColumns.add(column);
				col_config = column;
				grouped_cols.put(col_field, col_config);
			}
			// add shared field into row
			if (new_row)
				for (int i = 0; i < columnFields.length; i++) {
					if (sharedFieldSet != null)
						if (sharedFieldSet.contains(columnFields[i]))
							continue;
					target_map.remove(columnFields[i]);
				}

			if (fieldValueOnly) {
				if (colValueFields != null)
					target_map.put(col_field, item.get(colValueFields[0]));
			} else {
				col_config = (CompositeMap) col_config.clone();
				if (colValueFields != null)
					for (int i = 0; i < colValueFields.length; i++)
						col_config.put(colValueFields[i],
								item.get(colValueFields[i]));
				if (sharedFields != null)
					for (int i = 0; i < sharedFields.length; i++)
						col_config.put(sharedFields[i],
								target_map.get(sharedFields[i]));
				target_map.put(col_field, col_config);
			}
			;

			if (colValueFields != null)
				for (int i = 0; i < colValueFields.length; i++)
					target_map.remove(colValueFields[i]);
		}
		// System.out.println("After transform: child "+source.getChildsNotNull().size());
		if (createMetaData) {
			Collections.sort(transformedColumns, new CompositeComparator(
					colKeyField.toString()));
			CompositeMap meta = new CompositeMap("columns");
			meta.addChilds(transformedColumns);
			source.put(KEY_COLUMN_LIST, meta);
			// System.out.println(this.transformedColumns);
		}
		return source;
	}

	/**
	 * @return Returns the fieldValueOnly.
	 */
	public boolean isFieldValueOnly() {
		return fieldValueOnly;
	}

	/**
	 * @param fieldValueOnly
	 *            The fieldValueOnly to set.
	 */
	public void setFieldValueOnly(boolean fieldValueOnly) {
		this.fieldValueOnly = fieldValueOnly;
	}

	public Object getColumnValue(CompositeMap column) {
		return column == null ? null : column.get(colKeyField);
	}

	public String[] getColumnValueFields() {
		return colValueFields;
	}

	/**
	 * @return the createMetaData
	 */
	public boolean getCreateMetaData() {
		return createMetaData;
	}

	/**
	 * @param createMetaData
	 *            the createMetaData to set
	 */
	public void setCreateMetaData(boolean createMetaData) {
		this.createMetaData = createMetaData;
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		// TODO Auto-generated method stub
		String source = elementConifg.getString(this.KEY_SOURCE);
		assert source != null;
		String target = elementConifg.getString(this.KEY_TARGET);
		assert target != null;

		CompositeMap context = runner.getContext();
		CompositeMap sourceMap = (CompositeMap) context.getObject(source);

		CompositeMap targetMap = (CompositeMap) sourceMap.clone();
		context.putObject(target, targetMap, true);
		this.transform(targetMap, this.elementConifg);

	}

	@Override
	public void beginConfigure(CompositeMap config) {
		this.elementConifg = config;
	}

}
