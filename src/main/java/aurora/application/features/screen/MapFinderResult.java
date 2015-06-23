package aurora.application.features.screen;

import java.util.ArrayList;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;

public class MapFinderResult {
	private CompositeMap map;

	private List<Attribute> attributes;

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public void addAttribute(Attribute o) {
		if (attributes == null)
			attributes = new ArrayList<Attribute>();
		this.attributes.add(o);
	}

	public MapFinderResult(CompositeMap map, List<Attribute> attributes) {
		super();
		this.map = map;
		this.attributes = attributes;
	}

}
