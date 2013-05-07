package aurora.application.features.cstm;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.ConfigurationFileException;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.CompositeMapSchemaUtil;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.editor.AttributeValue;
import uncertain.schema.editor.CompositeMapEditor;
import uncertain.util.resource.ILocatable;
import aurora.application.AuroraApplication;
import aurora.application.sourcecode.SourceCodeUtil;
import aurora.application.util.LanguageUtil;
import aurora.i18n.ILocalizedMessageProvider;
import aurora.i18n.IMessageProvider;
import aurora.security.ResourceNotDefinedException;
import aurora.service.ServiceThreadLocal;

public class CustomSourceCode {

	public static final String KEY_RECORD_ID = "record_id";
	public static final String KEY_ID_VALUE = "id_value";
	public static final String KEY_MOD_TYPE = "mod_type";
	public static final String KEY_ARRAY_NAME = "array_name";
	public static final String KEY_INDEX_FIELD = "index_field";
	public static final String KEY_INDEX_VALUE = "index_value";
	public static final String KEY_ATTRIB_KEY = "attrib_key";
	public static final String KEY_ATTRIB_VALUE = "attrib_value";
	public static final String KEY_CONFIG_CONTENT = "config_content";
	public static final String KEY_POSITION = "position";
	public static final String KEY_SOURCE_FILE = "source_file";
	public static final String KEY_FIELDS_ORDER = "fields_order";
	public static final String KEY_DIMENSION_TYPE = "dimension_type";

	public static final String KEY_PLACE_HOLDER = "placeHolder";

	private static final String ILLEGAL_OPERATION_FOR_ROOT = "aurora.application.features.cstm.illegal_operation_for_root";
	private static final String ILLEGAL_POSITION_FOR_OPERATION = "aurora.application.features.cstm.illegal_position_for_operation";
	private static final String ILLEGAL_OPERATION = "aurora.application.features.cstm.illegal_operation";
	public static final String RE_ORDER_CHILD_COUNT = "aurora.application.features.cstm.re_order_child_count";

	public static CompositeMap custom(IObjectRegistry registry, String filePath, CompositeMap customConfig) throws Exception {
		CompositeMap source = getFileContent(registry, filePath);
		return custom(registry, source, customConfig);
	}

	public static CompositeMap custom(IObjectRegistry registry, CompositeMap source, CompositeMap customConfig) {
		if (source == null || customConfig == null || customConfig.getChilds() == null) {
			return source;
		}
		if (registry == null)
			throw new RuntimeException("paramter error. 'registry' can not be null.");
		ISchemaManager schemaManager = (ISchemaManager) registry.getInstanceOfType(ISchemaManager.class);
		if (schemaManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(), ISchemaManager.class,
					CustomSourceCode.class.getCanonicalName());
		Map result = new LinkedHashMap();
		SourceCodeUtil.getKeyNodeMap(source, SourceCodeUtil.KEY_ID, result);
		if (result.isEmpty())
			return source;
		CompositeMap currentNode = null;
		String currentId = null;
		for (Iterator it = customConfig.getChildIterator(); it.hasNext();) {
			CompositeMap dbRecord = (CompositeMap) it.next();
			String record_id = dbRecord.getString(KEY_RECORD_ID);
			if (record_id == null)
				throw BuiltinExceptionFactory.createAttributeMissing(dbRecord.asLocatable(), KEY_RECORD_ID);
			String idValue = dbRecord.getString(KEY_ID_VALUE);
			if (idValue == null)
				throw SourceCodeUtil.createAttributeMissingException(KEY_RECORD_ID, record_id, KEY_ID_VALUE, dbRecord.asLocatable());
			if (!idValue.equals(currentId)) {
				currentNode = (CompositeMap) result.get(idValue);
				if (currentNode == null)
					throw SourceCodeUtil.createNodeMissingException(SourceCodeUtil.KEY_ID, idValue, source.asLocatable());
				currentId = idValue;
			}
			String mode_type = dbRecord.getString(KEY_MOD_TYPE);
			if (mode_type == null)
				throw SourceCodeUtil.createAttributeMissingException(KEY_RECORD_ID, record_id, KEY_MOD_TYPE, dbRecord.asLocatable());
			CompositeMap objectNode = getObjectNode(currentNode, dbRecord, record_id);
			if ("set_attrib".equals(mode_type)) {
				String attrib_key = dbRecord.getString(KEY_ATTRIB_KEY);
				if (attrib_key == null)
					throw SourceCodeUtil.createAttributeMissingException(KEY_RECORD_ID, record_id, KEY_ATTRIB_KEY, dbRecord.asLocatable());
				String attrib_value = dbRecord.getString(KEY_ATTRIB_VALUE);
				objectNode.put(attrib_key.toLowerCase(), attrib_value);
			} else if ("replace".equals(mode_type)) {
				String config_content = dbRecord.getString(KEY_CONFIG_CONTENT);
				if (objectNode.getParent() == null) {
					throw createIllegalOperationForRootException(mode_type, objectNode.asLocatable());
				} else {
					if (config_content == null || "".equals(config_content))
						objectNode.getParent().removeChild(objectNode);
					CompositeLoader compositeLoader = new CompositeLoader();
					try {
						objectNode.getParent().replaceChild(objectNode, compositeLoader.loadFromString(config_content));
					} catch (Throwable e) {
						if (e instanceof SAXParseException) {
							SAXParseException saxPe = (SAXParseException) e;
							throw new ConfigurationFileException("uncertain.exception.source_file", new String[] { "CONFIG_CONTENT",
									String.valueOf(saxPe.getLineNumber()), String.valueOf(saxPe.getColumnNumber()) }, null);
						}
						throw new ConfigurationFileException("uncertain.exception.code", new String[] { e.getMessage() }, null);
					}
				}
			} else if ("insert".equals(mode_type)) {
				insertNode(schemaManager, dbRecord, record_id, objectNode);
			} else if ("delete".equals(mode_type)) {
				if (objectNode.getParent() == null)
					throw createIllegalOperationForRootException(mode_type, objectNode.asLocatable());
				objectNode.getParent().removeChild(objectNode);
			} else if ("cdata_replace".equals(mode_type)) {
				String config_content = dbRecord.getString(KEY_CONFIG_CONTENT);
				objectNode.setText(config_content);
			} else if ("cdata_append".equals(mode_type)) {
				String key_position = dbRecord.getString(KEY_POSITION);
				if (key_position == null)
					throw BuiltinExceptionFactory.createAttributeMissing(dbRecord.asLocatable(), KEY_POSITION);
				String cdata = objectNode.getText() != null ? objectNode.getText() : "";
				String config_content = dbRecord.getString(KEY_CONFIG_CONTENT, "");
				if ("before".equals(key_position)) {
					objectNode.setText(config_content + cdata);
				} else if ("after".equals(key_position)) {
					objectNode.setText(cdata + config_content);
				} else {
					throw createIllegalPositionForOperation(key_position, mode_type, dbRecord.asLocatable());
				}
			} else if ("re_order".equals(mode_type)) {
				reOrder(dbRecord, record_id, objectNode, true);
			} else {
				throw createIllegalOperation(mode_type, dbRecord.asLocatable());
			}
		}
		return source;
	}

	public static void reOrder(CompositeMap dbRecord, String record_id, CompositeMap objectNode, boolean isAfterRealDelete) {
		String field_order = dbRecord.getString(CustomSourceCode.KEY_FIELDS_ORDER);
		if (field_order == null)
			throw SourceCodeUtil.createAttributeMissingException(CustomSourceCode.KEY_RECORD_ID, record_id,
					CustomSourceCode.KEY_FIELDS_ORDER, dbRecord.asLocatable());
		String[] filedsOrder = field_order.split(",");
		if (filedsOrder == null)
			throw SourceCodeUtil.createAttributeMissingException(CustomSourceCode.KEY_RECORD_ID, record_id,
					CustomSourceCode.KEY_FIELDS_ORDER, dbRecord.asLocatable());
		// if(!isAfterRealDelete)
		// if(filedsOrder.length != objectNode.getChilds().size())
		// throw
		// CustomSourceCode.createChildCountException(objectNode.getChilds().size(),filedsOrder.length,objectNode.asLocatable());
		String index_field = dbRecord.getString(CustomSourceCode.KEY_INDEX_FIELD);
		if (index_field == null)
			throw SourceCodeUtil.createAttributeMissingException(CustomSourceCode.KEY_RECORD_ID, record_id,
					CustomSourceCode.KEY_INDEX_FIELD, dbRecord.asLocatable());
		CompositeMap clone = (CompositeMap) objectNode.clone();
		clone.getChilds().clear();
		for (int i = 0; i < filedsOrder.length; i++) {
			CompositeMap record = objectNode.getChildByAttrib(index_field, filedsOrder[i]);
			if (record != null) {
				clone.addChild(record);
				objectNode.removeChild(record);
			}
		}
		// objectNode.getChilds().clear();
		objectNode.addChilds(clone.getChilds());
	}

	public static CompositeMap getObjectNode(CompositeMap currentNode, CompositeMap dbRecord, String record_id) {
		CompositeMap objectNode = currentNode;
		String array_name = dbRecord.getString(KEY_ARRAY_NAME);
		if (array_name != null) {
			CompositeMap array = currentNode.getChild(array_name);
			if (array == null)
				array = currentNode.createChild(array_name);
			String index_field = dbRecord.getString(KEY_INDEX_FIELD);
			String mode_type = dbRecord.getString(KEY_MOD_TYPE);
			if (mode_type == null)
				throw SourceCodeUtil.createAttributeMissingException(KEY_RECORD_ID, record_id, KEY_MOD_TYPE, dbRecord.asLocatable());
			if (index_field == null || "insert".equals(mode_type) || "re_order".equals(mode_type))
				objectNode = array;
			else {
				String index_value = dbRecord.getString(KEY_INDEX_VALUE);
				if (index_value == null)
					throw SourceCodeUtil.createAttributeMissingException(KEY_RECORD_ID, record_id, KEY_INDEX_VALUE, dbRecord.asLocatable());
				CompositeMap child = array.getChildByAttrib(index_field, index_value);
				if (child == null)
					throw SourceCodeUtil.createNodeMissingException(index_field, index_value, array.asLocatable());
				objectNode = child;
			}
		}
		return objectNode;
	}

	public static CompositeMap insertNode(ISchemaManager schemaManager, CompositeMap dbRecord, String record_id, CompositeMap objectNode) {
		String mode_type = "insert";
		if (objectNode.getParent() == null)
			throw createIllegalOperationForRootException(mode_type, objectNode.asLocatable());
		String key_position = dbRecord.getString(KEY_POSITION);
		if (key_position == null)
			throw SourceCodeUtil.createAttributeMissingException(KEY_RECORD_ID, record_id, KEY_POSITION, dbRecord.asLocatable());
		Element ele = schemaManager.getElement(objectNode);
		if (ele != null && ele.isArray()) {
			String index_field = dbRecord.getString(KEY_INDEX_FIELD);
			CompositeMap newChild = CompositeMapSchemaUtil.addElement(schemaManager, objectNode, ele.getElementType().getQName());
			if (index_field != null) {
				String index_value = dbRecord.getString(KEY_INDEX_VALUE);
				if (index_value == null)
					throw SourceCodeUtil.createAttributeMissingException(KEY_RECORD_ID, record_id, KEY_INDEX_VALUE, dbRecord.asLocatable());
				newChild.put(index_field.toLowerCase(), index_value);
				return newChild;
			}
		}
		String config_content = dbRecord.getString(KEY_CONFIG_CONTENT);
		CompositeLoader compositeLoader = new CompositeLoader();
		CompositeMap newChild = null;
		try {
			newChild = compositeLoader.loadFromString(config_content);
		} catch (Throwable e) {
			if (e instanceof SAXParseException) {
				SAXParseException saxPe = (SAXParseException) e;
				throw new ConfigurationFileException("uncertain.exception.source_file", new String[] { "CONFIG_CONTENT",
						String.valueOf(saxPe.getLineNumber()), String.valueOf(saxPe.getColumnNumber()) }, null);
			}
			throw new ConfigurationFileException("uncertain.exception.code", new String[] { e.getMessage() }, null);
		}
		int index = objectNode.getParent().getChilds().indexOf(objectNode);
		if ("before".equals(key_position)) {
			objectNode.getParent().addChild(index, newChild);
		} else if ("after".equals(key_position)) {
			objectNode.getParent().addChild(index + 1, newChild);
		} else if ("first_child".equals(key_position)) {
			objectNode.addChild(0, newChild);
		} else if ("last_child".equals(key_position)) {
			objectNode.addChild(newChild);
		} else {
			throw createIllegalPositionForOperation(key_position, mode_type, dbRecord.asLocatable());
		}
		return newChild;
	}

	private static ConfigurationFileException createIllegalOperationForRootException(String modeType, ILocatable iLocatable) {
		return new ConfigurationFileException(ILLEGAL_OPERATION_FOR_ROOT, new String[] { modeType }, iLocatable);
	}

	private static ConfigurationFileException createIllegalPositionForOperation(String position, String operation, ILocatable iLocatable) {
		return new ConfigurationFileException(ILLEGAL_POSITION_FOR_OPERATION, new String[] { position, operation }, iLocatable);
	}

	private static ConfigurationFileException createIllegalOperation(String operation, ILocatable iLocatable) {
		return new ConfigurationFileException(ILLEGAL_OPERATION, new String[] { operation }, iLocatable);
	}

	public static CompositeMap getElementChildArray(IObjectRegistry registry, String filePath, String id) throws IOException, SAXException {
		CompositeMap empty = new CompositeMap("result");
		if (registry == null)
			throw new RuntimeException("paramter error. 'registry' can not be null.");
		if (id == null)
			return empty;
		int idx = filePath.indexOf('?');
		if (idx > 0)
			filePath = filePath.substring(0, idx);
		File webHome = SourceCodeUtil.getWebHome(registry);
		File sourceFile = new File(webHome, filePath);
		if (sourceFile == null || !sourceFile.exists())
			throw new ResourceNotDefinedException(filePath);
		Map idMap = new LinkedHashMap();
		CompositeLoader cl = new CompositeLoader();
		CompositeMap source = cl.loadByFullFilePath(sourceFile.getCanonicalPath());
		SourceCodeUtil.getKeyNodeMap(source, SourceCodeUtil.KEY_ID, idMap);
		if (idMap.isEmpty())
			return empty;
		CompositeMap node = (CompositeMap) idMap.get(id);
		if (node == null) {
			throw SourceCodeUtil.createNodeMissingException(SourceCodeUtil.KEY_ID, id, source.asLocatable());
		}
		ISchemaManager schemaManager = (ISchemaManager) registry.getInstanceOfType(ISchemaManager.class);
		if (schemaManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(), ISchemaManager.class,
					CustomSourceCode.class.getCanonicalName());
		Element ele = schemaManager.getElement(node);
		if (ele == null)
			throw new RuntimeException("elment:" + node.getQName().toString() + " is not defined.");
		List arrays = ele.getAllArrays();
		CompositeMap result = new CompositeMap("result");
		if (arrays == null || arrays.isEmpty())
			return empty;
		for (Iterator it = arrays.iterator(); it.hasNext();) {
			Array array = (Array) it.next();
			CompositeMap record = new CompositeMap("record");
			record.put("array_name", array.getLocalName());
			record.put("index_field", array.getIndexField());
			record.put("document", array.getDocument());
			result.addChild(record);
		}
		return result;
	}

	public static CompositeMap getAttributeValues(IObjectRegistry registry, String filePath, String id, CompositeMap dbRecords)
			throws IOException, SAXException {
		CompositeMap empty = new CompositeMap("result");
		if (registry == null)
			throw new RuntimeException("paramter error. 'registry' can not be null.");
		if (id == null)
			return empty;
		File webHome = SourceCodeUtil.getWebHome(registry);
		File sourceFile = new File(webHome, filePath);
		if (sourceFile == null || !sourceFile.exists())
			throw new ResourceNotDefinedException(filePath);
		Map idMap = new LinkedHashMap();
		CompositeLoader cl = new CompositeLoader();
		CompositeMap source = cl.loadByFullFilePath(sourceFile.getCanonicalPath());
		SourceCodeUtil.getKeyNodeMap(source, SourceCodeUtil.KEY_ID, idMap);
		if (idMap.isEmpty())
			return empty;
		CompositeMap node = (CompositeMap) idMap.get(id);
		if (node == null) {
			throw SourceCodeUtil.createNodeMissingException(SourceCodeUtil.KEY_ID, id, source.asLocatable());
		}
		ISchemaManager schemaManager = (ISchemaManager) registry.getInstanceOfType(ISchemaManager.class);
		if (schemaManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(), ISchemaManager.class,
					CustomSourceCode.class.getCanonicalName());
		Element ele = schemaManager.getElement(node);
		if (ele == null)
			throw new RuntimeException("elment:" + node.getQName().toString() + " is not defined.");
		CompositeMap dbAttribs = new CompositeMap("dbAttribs");
		if (dbRecords != null && dbRecords.getChilds() != null) {
			for (Iterator it = dbRecords.getChildIterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap) it.next();
				if (!filePath.equals(record.getString(KEY_SOURCE_FILE)) || !id.equals(record.getString(KEY_ID_VALUE))
						|| !"set_attrib".equals(record.getString(KEY_MOD_TYPE))) {
					continue;
				}
				dbAttribs.put(record.getString(KEY_ATTRIB_KEY), record);
			}
		}
		CompositeMapEditor editor = new CompositeMapEditor(schemaManager, node);
		AttributeValue[] avs = editor.getAttributeList();
		CompositeMap result = new CompositeMap("result");
		if (avs != null) {
			for (int i = 0; i < avs.length; i++) {
				Attribute attr = avs[i].getAttribute();
				if (attr == null)
					continue;
				CompositeMap record = new CompositeMap("record");
				record.put("attrib_key", attr.getLocalName());
				record.put("source_value", avs[i].getValueString());
				if (!dbAttribs.isEmpty()) {
					CompositeMap dbRecord = (CompositeMap) dbAttribs.get(attr.getLocalName());
					if (dbRecord != null) {
						record.put(KEY_RECORD_ID, dbRecord.getString(KEY_RECORD_ID));
						record.put(KEY_ATTRIB_VALUE, dbRecord.getString(KEY_ATTRIB_VALUE));
						dbAttribs.remove(attr.getLocalName());
					}
				}
				record.put("document", attr.getDocument());
				result.addChild(record);
			}
		}
		if (!dbAttribs.isEmpty()) {
			for (Iterator it = dbAttribs.values().iterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap) it.next();
				result.addChild(record);
			}

		}
		return result;
	}

	public static CompositeMap getArrayList(IObjectRegistry registry, String filePath, String id, String array_name, CompositeMap dbRecords)
			throws IOException, SAXException {
		return CustomSourceCode.getArrayList(registry, filePath, id, array_name, dbRecords, true);
	}

	public static CompositeMap getArrayList(IObjectRegistry registry, String filePath, String id, String array_name,
			CompositeMap dbRecords, boolean isChangeName) throws IOException, SAXException {
		CompositeMap empty = new CompositeMap("result");
		if (registry == null)
			throw new RuntimeException("paramter error. 'registry' can not be null.");
		if (id == null || array_name == null)
			return empty;
		File webHome = SourceCodeUtil.getWebHome(registry);
		File sourceFile = new File(webHome, filePath);
		if (sourceFile == null || !sourceFile.exists())
			throw new ResourceNotDefinedException(filePath);
		Map idMap = new LinkedHashMap();
		CompositeLoader cl = new CompositeLoader();
		CompositeMap source = cl.loadByFullFilePath(sourceFile.getCanonicalPath());
		SourceCodeUtil.getKeyNodeMap(source, SourceCodeUtil.KEY_ID, idMap);
		if (idMap.isEmpty())
			return empty;
		CompositeMap node = (CompositeMap) idMap.get(id);
		if (node == null) {
			throw SourceCodeUtil.createNodeMissingException(SourceCodeUtil.KEY_ID, id, source.asLocatable());
		}
		ISchemaManager schemaManager = (ISchemaManager) registry.getInstanceOfType(ISchemaManager.class);
		if (schemaManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(), ISchemaManager.class,
					CustomSourceCode.class.getCanonicalName());
		Element ele = schemaManager.getElement(node);
		CompositeMap re_order = null;
		if (ele == null)
			throw new RuntimeException("elment:" + node.getQName().toString() + " is not defined.");
		if (dbRecords != null && dbRecords.getChilds() != null) {
			for (Iterator it = dbRecords.getChildIterator(); it.hasNext();) {
				CompositeMap dbRecord = (CompositeMap) it.next();
				if (!filePath.equals(dbRecord.getString(KEY_SOURCE_FILE)) || !id.equals(dbRecord.getString(KEY_ID_VALUE))
						|| !array_name.equals(dbRecord.getString(KEY_ARRAY_NAME))) {
					continue;
				} else {
					String record_id = dbRecord.getString(KEY_RECORD_ID);
					if (record_id == null)
						throw BuiltinExceptionFactory.createAttributeMissing(dbRecord.asLocatable(), KEY_RECORD_ID);
					String mode_type = dbRecord.getString(KEY_MOD_TYPE);
					if (mode_type == null)
						throw SourceCodeUtil
								.createAttributeMissingException(KEY_RECORD_ID, record_id, KEY_MOD_TYPE, dbRecord.asLocatable());
					if ("re_order".equals(mode_type)) {
						re_order = dbRecord;
						continue;
					}
					CompositeMap objectNode = getObjectNode(node, dbRecord, record_id);
					if ("insert".equals(mode_type)) {
						CompositeMap newChild = insertNode(schemaManager, dbRecord, record_id, objectNode);
						newChild.put(KEY_MOD_TYPE, mode_type);
						newChild.put(KEY_RECORD_ID, dbRecord.getString(KEY_RECORD_ID));
					} else if ("delete".equals(mode_type)) {
						objectNode.put(KEY_MOD_TYPE, mode_type);
						objectNode.put(KEY_RECORD_ID, dbRecord.getString(KEY_RECORD_ID));
					}
				}
			}
		}
		CompositeMap result = node.getChild(array_name);
		if (result != null && result.getChilds() != null) {
			if (isChangeName) {
				for (Iterator it = result.getChildIterator(); it.hasNext();) {
					CompositeMap record = (CompositeMap) it.next();
					if (KEY_PLACE_HOLDER.toLowerCase().equals(record.getName().toLowerCase()))
						it.remove();
					else
						record.setName("record");
				}
			}
			if (re_order != null) {
				String record_id = re_order.getString(KEY_RECORD_ID);
				reOrder(re_order, record_id, result, false);
			}
		} else {
			return empty;
		}
		return result;
	}

	public static CompositeMap getAttributeValues(IObjectRegistry registry, String filePath, String id, String array_name,
			String index_field, String index_value, CompositeMap dbRecords) throws IOException, SAXException {
		CompositeMap empty = new CompositeMap("result");
		if (registry == null)
			throw new RuntimeException("paramter error. 'registry' can not be null.");
		if (id == null || array_name == null || index_field == null || index_value == null)
			return empty;
		ISchemaManager schemaManager = (ISchemaManager) registry.getInstanceOfType(ISchemaManager.class);
		if (schemaManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException((new CompositeMap()).asLocatable(), ISchemaManager.class,
					CustomSourceCode.class.getCanonicalName());
		CompositeMap arrayList = getArrayList(registry, filePath, id, array_name, new CompositeMap(), false);
		if (arrayList == null || arrayList.getChilds() == null)
			return empty;
		boolean fromDB = false;
		CompositeMap node = arrayList.getChildByAttrib(index_field, index_value);
		if (node == null) {
			fromDB = true;
			Element ele = schemaManager.getElement(arrayList);
			if (ele == null)
				throw new RuntimeException("elment:" + arrayList.getQName().toString() + " is not defined.");
			node = CompositeMapSchemaUtil.addElement(schemaManager, arrayList, ele.getElementType().getQName());
			node.put(index_field.toLowerCase(), index_value);
		} else {
			if (node.getString(KEY_RECORD_ID) != null) {
				fromDB = true;
				node.remove(KEY_RECORD_ID);
				node.remove(KEY_MOD_TYPE);
			}
		}
		Element ele = schemaManager.getElement(node);
		if (ele == null)
			throw new RuntimeException("elment:" + node.getQName().toString() + " is not defined.");
		CompositeMap dbAttribs = new CompositeMap("dbAttribs");
		if (dbRecords != null && dbRecords.getChilds() != null) {
			for (Iterator it = dbRecords.getChildIterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap) it.next();
				if (!filePath.equals(record.getString(KEY_SOURCE_FILE)) || !id.equals(record.getString(KEY_ID_VALUE))
						|| !array_name.equals(record.getString(KEY_ARRAY_NAME)) || !index_field.equals(record.getString(KEY_INDEX_FIELD))
						|| !index_value.equals(record.getString(KEY_INDEX_VALUE)) || !"set_attrib".equals(record.getString(KEY_MOD_TYPE))) {
					continue;
				}
				dbAttribs.put(record.getString(KEY_ATTRIB_KEY), record);
			}
		}
		CompositeMapEditor editor = new CompositeMapEditor(schemaManager, node);
		AttributeValue[] avs = editor.getAttributeList();
		CompositeMap result = new CompositeMap("result");
		if (avs != null) {
			for (int i = 0; i < avs.length; i++) {
				Attribute attr = avs[i].getAttribute();
				if (attr == null)
					continue;
				CompositeMap record = new CompositeMap("record");
				record.put("attrib_key", attr.getLocalName());
				if (fromDB) {
					record.put(KEY_ATTRIB_VALUE, avs[i].getValueString());
				} else
					record.put("source_value", avs[i].getValueString());
				if (!dbAttribs.isEmpty()) {
					CompositeMap dbRecord = (CompositeMap) dbAttribs.get(attr.getLocalName());
					if (dbRecord != null) {
						record.put(KEY_RECORD_ID, dbRecord.getString(KEY_RECORD_ID));
						record.put(KEY_ATTRIB_VALUE, dbRecord.getString(KEY_ATTRIB_VALUE));
						dbAttribs.remove(attr.getLocalName());
					}
				}
				record.put("document", attr.getDocument());
				result.addChild(record);
			}
		}
		if (!dbAttribs.isEmpty()) {
			for (Iterator it = dbAttribs.values().iterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap) it.next();
				result.addChild(record);
			}

		}
		return result;
	}

	public static CompositeMap getFileContent(IObjectRegistry registry, String filePath) throws IOException, SAXException {
		if (registry == null)
			throw new RuntimeException("parameter error. 'registry' can not be null.");
		File webHome = SourceCodeUtil.getWebHome(registry);
		File sourceFile = new File(webHome, filePath);
		if (sourceFile == null || !sourceFile.exists())
			throw new ResourceNotDefinedException(filePath);
		CompositeLoader cl = new CompositeLoader();
		CompositeMap source = cl.loadByFullFilePath(sourceFile.getCanonicalPath());
		return source;
	}

	public static ILogger getLogger(IObjectRegistry registry) {
		CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
		if (context != null) {
			return LoggingContext.getLogger(context, CustomSourceCode.class.getCanonicalName());
		} else {
			return LoggingContext.getLogger(CustomSourceCode.class.getCanonicalName(), registry);
		}
	}

	private static void serachContainer(IObjectRegistry registry, CompositeMap node, CompositeMap forms, CompositeMap grids) {
		if (node == null)
			return;
		String nodeName = node.getName();
		String nodeUri = node.getNamespaceURI();

		ILogger logger = getLogger(registry);
		IMessageProvider messageProvider = (IMessageProvider) registry.getInstanceOfType(IMessageProvider.class);
		ILocalizedMessageProvider promptProvider = null;
		if (messageProvider == null)
			logger.log(Level.SEVERE, "", BuiltinExceptionFactory.createInstanceNotFoundException(null, IMessageProvider.class));
		else {
			CompositeMap context = ServiceThreadLocal.getCurrentThreadContext();
			String languageCode = null;
			if (context != null) {
				languageCode = LanguageUtil.getSessionLanguage(registry, context);
			}
			if (languageCode == null)
				languageCode = messageProvider.getDefaultLang();
			promptProvider = messageProvider.getLocalizedMessageProvider(languageCode);

		}
		if ("form".equalsIgnoreCase(nodeName) && AuroraApplication.AURORA_FRAMEWORK_NAMESPACE.equals(nodeUri)) {
			String formId = node.getString("id");
			// form必须包含id属性，并且id属性内容不包含@
			if (formId != null && !formId.contains("@")) {
				String name = node.getString("title");
				if (promptProvider != null)
					name = promptProvider.getMessage(name);
				if (name == null || "".equals(name)) {
					name = formId;
				}
				CompositeMap form = new CompositeMap("form");
				form.put("id", formId);
				form.put("name", name);
				forms.addChild(form);
			}
		} else if (node.getName().equalsIgnoreCase("grid") && AuroraApplication.AURORA_FRAMEWORK_NAMESPACE.equals(nodeUri)) {
			String gridId = node.getString("id");
			// grid必须包含id属性，并且id属性内容不包含@
			if (gridId != null && !gridId.contains("@")) {
				String name = node.getString("title");
				if (promptProvider != null)
					name = promptProvider.getMessage(name);
				if (name == null || "".equals(name)) {
					name = gridId;
				}
				CompositeMap grid = new CompositeMap("grid");
				grid.put("id", gridId);
				grid.put("name", name);
				grids.addChild(grid);
			}
		}
		List<CompositeMap> childList = node.getChilds();
		if (childList != null) {
			for (CompositeMap child : childList) {
				serachContainer(registry, child, forms, grids);
			}
		}
	}

	public static CompositeMap getContainer(IObjectRegistry registry, String filePath) throws IOException, SAXException {
		CompositeMap fileContent = getFileContent(registry, filePath);
		CompositeMap forms = new CompositeMap("forms");
		forms.put("name", "forms");
		CompositeMap grids = new CompositeMap("grids");
		grids.put("name", "grids");

		serachContainer(registry, fileContent, forms, grids);

		CompositeMap tabs = new CompositeMap("tabs");
		tabs.put("name", "tabs");

		CompositeMap screen = new CompositeMap("screen");
		screen.put("name", "screen");
		screen.addChild(forms);
		screen.addChild(grids);
		screen.addChild(tabs);
		getLogger(registry).config(filePath + " getContainer result is:"+XMLOutputter.LINE_SEPARATOR + screen.toXML());
		return screen;

	}

	public static ConfigurationFileException createChildCountException(int sourceCount, int reOrderCount, ILocatable iLocatable) {
		return new ConfigurationFileException(RE_ORDER_CHILD_COUNT, new Integer[] { sourceCount, reOrderCount }, iLocatable);
	}
}
