/*
 * Created on 2008-1-24
 */
package aurora.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import aurora.bm.BusinessModel;
import aurora.bm.Field;

public class ResultSetLoader {

	String rootName = "records";
	String elementName = "record";
	DataTypeRegistry datatypeRegistry = DataTypeRegistry.getInstance();
	byte fieldNameCase = Character.UNASSIGNED;

	static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String convertDate(Date date) {
		return DEFAULT_DATE_FORMAT.format(date);
	}

	public ResultSetLoader() {

	}

	public ResultSetLoader(DataTypeRegistry registry) {
		datatypeRegistry = registry;
	}

	String getFieldName(String name) {
		String key = null;
		if (name != null) {
			if (fieldNameCase == Character.UPPERCASE_LETTER)
				key = name.toUpperCase();
			else if (fieldNameCase == Character.LOWERCASE_LETTER)
				key = name.toLowerCase();
			else
				key = name;
		}
		return key;
	}

	void fetchRowByMetaData(ResultSet rs, ResultSetMetaData meta, IResultSetConsumer consumer) throws SQLException {
		consumer.newRow(elementName);
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String name = meta.getColumnLabel(i);
			name = getFieldName(name);
			Object value = rs.getObject(i);
			// 日期数据按照格式化生成
			if (value instanceof java.sql.Timestamp) {
				Timestamp t = (Timestamp) value;
				long lms = t.getTime();
				Date date = new Date(lms);
				value = convertDate(date);
			} else if (value instanceof java.sql.Date) {
				value = convertDate((java.util.Date) value);
			} else if (value instanceof java.util.Date) {
				value = convertDate((java.util.Date) value);
			}
			consumer.loadField(name, value);
		}
		consumer.endRow();
	}

	void fetchRowByStructure(ResultSet rs, BusinessModel struct, IResultSetConsumer consumer) throws SQLException {
		consumer.newRow(elementName);
		Field[] fields = struct.getFields();
		DataType[] types = struct.getFieldTypeArray(datatypeRegistry);
		if (fields == null)
			throw new IllegalArgumentException("Can't get fields from model");
		for (int i = 0; i < fields.length; i++) {
			if (!fields[i].isForSelect())
				continue;
			Field fld = fields[i];
			if (fld.isReferenceField())
				fld = fld.getReferredField();
			String name = getFieldName(fields[i].getName());
			if (name == null)
				throw new IllegalArgumentException(
						"must specify name property in field config: " + fields[i].getObjectContext().toXML());
			// String physical_name = fld.getPhysicalName();
			String physical_name = fields[i].getName();
			DataType type = types[i];
			Object value = null;
			try {
				if (type != null)
					value = type.getObject(rs, rs.findColumn(physical_name));
				else
					value = rs.getObject(physical_name);
			} catch (Throwable ex) {
				throw new SQLException("can't load value for field No. " + (i + 1) + ", named '" + fld.getName() + "':"
						+ ex.getClass().getName() + " " + ex.getMessage());
			}
			consumer.loadField(name, value);
		}
		consumer.endRow();
	}

	public void loadByResultSet(ResultSet rs, FetchDescriptor desc, IResultSetConsumer consumer) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		consumer.begin(rootName);
		if (desc.fetchAll) {
			while (rs.next()) {
				fetchRowByMetaData(rs, meta, consumer);
			}
		} else {
			// System.out.println("fetch begin");
			if (!desc.locate(rs))
				return;
			for (int i = 0; i < desc.getPageSize(); i++) {
				// System.out.println("fetch No."+i);
				fetchRowByMetaData(rs, meta, consumer);
				if (!rs.next())
					break;
			}
		}
		consumer.end();
	}

	public void loadByConfig(ResultSet rs, FetchDescriptor desc, BusinessModel meta, IResultSetConsumer consumer)
			throws SQLException {
		consumer.begin(rootName);
		if (desc.fetchAll) {
			while (rs.next()) {
				fetchRowByStructure(rs, meta, consumer);
			}
		} else {
			if (!desc.locate(rs))
				return;
			for (int i = 0; i < desc.getPageSize(); i++) {
				fetchRowByStructure(rs, meta, consumer);
				if (!rs.next())
					break;
			}
		}
		consumer.end();

	}

	public byte getFieldNameCase() {
		return fieldNameCase;
	}

	public void setFieldNameCase(byte fieldNameCase) {
		this.fieldNameCase = fieldNameCase;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

}
