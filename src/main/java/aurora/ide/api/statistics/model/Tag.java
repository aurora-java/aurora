package aurora.ide.api.statistics.model;

import aurora.ide.api.statistics.map.StatisticsMap;

public class Tag {
	private String tagId="";
	private String objectId="";
	private String projectId="";
	private String type="";
	private String name="";
	private int count=0;
	private int size=0;
	private String namespace="";
	private String qName="";
	private String rawName="";
	private String prefix="";

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	private StatisticsProject project;
	private ProjectObject object;
	private StatisticsMap sm;

	public Tag(StatisticsMap sm) {
		this.sm = sm;
		this.setSize(sm.getSize());
		this.setCount(1);
		this.setName(sm.getName());
		this.setNamespace(sm.getNamespaceURI() == null ? "" : sm
				.getNamespaceURI());
		this.setqName(sm.getQName().toString());
		this.setRawName(sm.getRawName());
		this.setType(sm.getType());
		this.setPrefix(sm.getPrefix() == null ? "" : sm.getPrefix());
	}

	public Tag() {
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}


	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getqName() {
		return qName;
	}

	public void setqName(String qName) {
		this.qName = qName;
	}

	public String getRawName() {
		return rawName;
	}

	public void setRawName(String rawName) {
		this.rawName = rawName;
	}

	public StatisticsProject getProject() {
		return project;
	}

	public void setProject(StatisticsProject project) {
		this.project = project;
	}

	public ProjectObject getObject() {
		return object;
	}

	public StatisticsMap getSm() {
		return sm;
	}

	public void setObject(ProjectObject object) {
		this.object = object;
	}

	public boolean isSubTag(Tag o) {
		return this.getObject().isEquals(o.getObject())
				&& this.name.equals(o.getName())
				&& this.namespace.equals(o.getNamespace());
	}

	public boolean appendTag(Tag t) {
		if (this.isSubTag(t)) {
			this.count++;
			this.size += t.getSize() - t.getNamespaceSize();
			return true;
		}
		return false;
	}

	private int getNamespaceSize() {
		String p = "xmlns:=\"\"";
		return getNamespace().getBytes().length + getPrefix().getBytes().length
				+ p.getBytes().length;
	}
}
