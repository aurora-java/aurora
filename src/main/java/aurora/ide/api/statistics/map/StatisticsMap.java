package aurora.ide.api.statistics.map;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

public class StatisticsMap {

	private CompositeMap map;

	public StatisticsMap(CompositeMap map) {
		this.map = map;
	}

	public boolean isRoot() {
		// return map.getRoot().equals(map);
		return map.getParent() == null;
	}

	public int getSize() {
		String xml = map.toXML();
		return xml.getBytes().length;
	}

	public String getName() {
		return map.getName();
	}

	public String getNamespaceURI() {
		return map.getNamespaceURI();
	}

	public String getRawName() {
		return map.getRawName();
	}

	public String toXML() {
		return map.toXML();
	}

	public QualifiedName getQName() {
		return map.getQName();
	}

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	public String getType() {
		return PreferencesTag.INSTANCE().getType(map.getNamespaceURI(), map.getName());

	}

	public boolean isTag() {
		String tagName = map.getName();
		String namespace = map.getNamespaceURI();
		return PreferencesTag.INSTANCE().hasTag(namespace, tagName);
	}

	public String getPrefix() {
		return map.getPrefix();
	}

}
