package aurora.application.sourcecode;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.editor.AttributeValue;
import uncertain.schema.editor.CompositeMapEditor;
import aurora.schema.AuroraSchemaManager;
import aurora.security.ResourceNotDefinedException;

public class SourceCodeSchemaManager {
	public static CompositeMap getElementChildArray(IObjectRegistry registry, String filePath, String id)
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
		AuroraSchemaManager asm = AuroraSchemaManager.getInstance();
		Element ele = asm.getSchemaManager().getElement(node);
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
			record.put("index_field", array.getIdField());
			record.put("document", array.getDocument());
			result.addChild(record);
		}
		return result;
	}

	public static CompositeMap getAttributeValues(IObjectRegistry registry, String filePath, String id,
			CompositeMap dbRecords) throws IOException, SAXException {
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
		AuroraSchemaManager asm = AuroraSchemaManager.getInstance();
		Element ele = asm.getSchemaManager().getElement(node);
		if (ele == null)
			throw new RuntimeException("elment:" + node.getQName().toString() + " is not defined.");
		CompositeMap dbAttribs = new CompositeMap("dbAttribs");
		if (dbRecords != null && dbRecords.getChilds() != null) {
			for (Iterator it = dbRecords.getChildIterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap) it.next();
				if (!filePath.equals(record.getString(CustomSourceCode.KEY_SOURCE_FILE))
						|| !id.equals(record.getString(CustomSourceCode.KEY_ID_VALUE))
						|| !"set_attrib".equals(record.getString(CustomSourceCode.KEY_MOD_TYPE))) {
					continue;
				}
				dbAttribs.put(record.getString(CustomSourceCode.KEY_ATTRIB_KEY), record);
			}
		}
		CompositeMapEditor editor = new CompositeMapEditor(asm.getSchemaManager(), node);
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
					CompositeMap dbRecord = (CompositeMap)dbAttribs.get(attr.getLocalName());
					if (dbRecord != null) {
						record.put(CustomSourceCode.KEY_RECORD_ID, dbRecord.getString(CustomSourceCode.KEY_RECORD_ID));
						record.put(CustomSourceCode.KEY_ATTRIB_VALUE, dbRecord.getString(CustomSourceCode.KEY_ATTRIB_VALUE));
						dbAttribs.remove(attr.getLocalName());
					}
				}
				record.put("document", attr.getDocument());
				result.addChild(record);
			}
		}
		if (!dbAttribs.isEmpty()) {
			for (Iterator it = dbAttribs.values().iterator(); it.hasNext();) {
				CompositeMap record = (CompositeMap)it.next();
				result.addChild(record);
			}

		}
		return result;
	}
	public static CompositeMap getArrayList(IObjectRegistry registry, String filePath, String id,String array_name,
			CompositeMap dbRecords) throws IOException, SAXException {
		CompositeMap empty = new CompositeMap("result");
		if (registry == null)
			throw new RuntimeException("paramter error. 'registry' can not be null.");
		if (id == null||array_name == null)
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
		AuroraSchemaManager asm = AuroraSchemaManager.getInstance();
		Element ele = asm.getSchemaManager().getElement(node);
		if (ele == null)
			throw new RuntimeException("elment:" + node.getQName().toString() + " is not defined.");
		if(dbRecords != null && dbRecords.getChilds()!= null){
			for(Iterator it = dbRecords.getChildIterator();it.hasNext();){
				CompositeMap dbRecord = (CompositeMap)it.next();
				if (!filePath.equals(dbRecord.getString(CustomSourceCode.KEY_SOURCE_FILE))
						|| !id.equals(dbRecord.getString(CustomSourceCode.KEY_ID_VALUE))
						|| !array_name.equals(dbRecord.getString(CustomSourceCode.KEY_ARRAY_NAME))) {
					continue;
				}else{
					String record_id = dbRecord.getString(CustomSourceCode.KEY_RECORD_ID);
					if (record_id == null)
						throw BuiltinExceptionFactory.createAttributeMissing(dbRecord.asLocatable(), CustomSourceCode.KEY_RECORD_ID);
					String mode_type = dbRecord.getString(CustomSourceCode.KEY_MOD_TYPE);
					if (mode_type == null)
						throw SourceCodeUtil.createAttributeMissingException(CustomSourceCode.KEY_RECORD_ID, record_id, CustomSourceCode.KEY_MOD_TYPE, dbRecord.asLocatable());
					CompositeMap objectNode = CustomSourceCode.getObjectNode(node, dbRecord, record_id);
					if("insert".equals(mode_type)){
						CompositeMap newChild= CustomSourceCode.insertNode(dbRecord, record_id, objectNode);
						newChild.put(CustomSourceCode.KEY_MOD_TYPE,mode_type);
						newChild.put(CustomSourceCode.KEY_RECORD_ID, dbRecord.getString(CustomSourceCode.KEY_RECORD_ID));
					}else if("delete".equals(mode_type)){
						objectNode.put(CustomSourceCode.KEY_MOD_TYPE,mode_type);
						objectNode.put(CustomSourceCode.KEY_RECORD_ID, dbRecord.getString(CustomSourceCode.KEY_RECORD_ID));
					}
				}
			}
		}
		CompositeMap result = node.getChild(array_name);
		if(result != null && result.getChilds() != null){
			for(Iterator it = result.getChildIterator();it.hasNext();){
				CompositeMap record = (CompositeMap)it.next();
				record.setName("record");
			}
		}else{
			return empty;
		}
		return result;
	}
}
