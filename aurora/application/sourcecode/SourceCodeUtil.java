package aurora.application.sourcecode;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.exception.ConfigurationFileException;
import uncertain.exception.GeneralException;
import uncertain.ocm.IObjectRegistry;
import uncertain.util.resource.ILocatable;

public class SourceCodeUtil {
	
	public static final String KEY_ID = "id";
	public static void getKeyNodeMap(CompositeMap source, Object key, Map result) {
		if (source == null || key == null)
			return;
		Object value = source.get(key);
		if (value != null) {
			result.put(value, source);
		}
		if (source.getChilds()!= null) {
			for (Iterator it = source.getChildIterator(); it.hasNext();) {
				getKeyNodeMap((CompositeMap) it.next(), key, result);
			}
		}
	}
	public static CompositeMap searchNodeById(CompositeMap source, String targetId) {
		if (source == null || targetId == null)
			return null;
		CompositeMap targetNode = null;
		String nodeId = source.getString("id");
		if (nodeId != null && targetId.equals(nodeId)) {
			return source;
		}
		List<CompositeMap> childList = source.getChilds();
		if (childList!= null) {
			for (CompositeMap child:childList) {
				targetNode = searchNodeById(child, targetId);
				if(targetNode != null)
					break;
			}
		}
		return targetNode;
	}
	public static File getWebHome(IObjectRegistry registry){
		if (registry == null)
			throw new RuntimeException("paramter error. 'registry' can not be null.");
		UncertainEngine ue = (UncertainEngine) registry.getInstanceOfType(UncertainEngine.class);
		if (ue == null)
			throw new GeneralException("uncertain.exception.instance_not_found", new Object[]{UncertainEngine.class.getName(), null}, (Throwable)null, (CompositeMap)null );
		File configDirectory = ue.getConfigDirectory();
		if (configDirectory == null)
			throw new RuntimeException("Not defind configDirectory in UncertainEngine.");
		File webHomeFile = configDirectory.getParentFile();
		return webHomeFile;
	}
	public static ConfigurationFileException createAttributeMissingException(String keyAttrib,String kyeValue,String missingAttrib,ILocatable iLocatable){
		return new ConfigurationFileException(SourceCodeUtil.ATTRIBUTE_MISSING_CODE,new String[]{keyAttrib,kyeValue,missingAttrib},iLocatable);
	}
	public static ConfigurationFileException createNodeMissingException(String attrib,String value,ILocatable iLocatable){
		return new ConfigurationFileException(SourceCodeUtil.NODE_MISSING,new String[]{attrib,value},iLocatable);
	}
	public static final String ATTRIBUTE_MISSING_CODE="aurora.application.sourcecode.attribute_missing";
	public static final String NODE_MISSING = "aurora.application.sourcecode.node_missing";


}
