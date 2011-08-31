package aurora.application.sourcecode;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import aurora.schema.AuroraSchemaManager;
import aurora.security.ResourceNotDefinedException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import uncertain.schema.Array;
import uncertain.schema.Element;


public class SourceCodeSchemaManager {
	public static CompositeMap getElementChildArray(IObjectRegistry registry,String filePath,String id) throws IOException, SAXException{
		if (registry == null)
			throw new RuntimeException("paramter error. 'registry' can not be null.");
		if(id == null)
			return null;
		File webHome = SourceCodeUtil.getWebHome(registry);
		File sourceFile = new File(webHome,filePath);
		if(sourceFile == null||!sourceFile.exists())
			throw new ResourceNotDefinedException(filePath);
		Map idMap = new LinkedHashMap();
		CompositeLoader cl = new CompositeLoader();
		CompositeMap source = cl.loadByFullFilePath(sourceFile.getCanonicalPath());
		SourceCodeUtil.getKeyNodeMap(source, SourceCodeUtil.KEY_ID, idMap);
		if (idMap.isEmpty())
			return null;
		CompositeMap node = (CompositeMap)idMap.get(id);
		if(node == null){
			throw SourceCodeUtil.createNodeMissingException(SourceCodeUtil.KEY_ID, id, source.asLocatable());
		}
		AuroraSchemaManager asm = AuroraSchemaManager.getInstance();
		Element ele = asm.getSchemaManager().getElement(node);
		if(ele == null)
			throw new RuntimeException("elment:"+node.getQName().toString()+" is not defined.");
		List arrays = ele.getAllArrays();
		CompositeMap result = new CompositeMap("result");
		if(arrays == null || arrays.isEmpty())
			return result;
		for(Iterator it = arrays.iterator();it.hasNext();){
			Array array = (Array) it.next();
			CompositeMap record = new CompositeMap("record");
			record.put("array_name", array.getName());
			record.put("document", array.getDocument());
			result.addChild(record);
		}
		System.out.println(result.toXML());
		return result;
	}
}
