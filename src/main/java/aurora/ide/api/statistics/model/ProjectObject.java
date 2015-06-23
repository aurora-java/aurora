package aurora.ide.api.statistics.model;

import java.util.ArrayList;
import java.util.List;

import uncertain.composite.CompositeMap;

public class ProjectObject {
	public static final String BM = "bm";
	public static final String SCREEN = "screen";
	public static final String SVC = "svc";
	public static final String UNSUPPORT = "unsupport";
	// 当前版本，项目ID
	private String projectId="";
	// 对应表ID
	private String objectId="";
	// 文件大小，单位byte
	private int fileSize=0;
	// sql或javascrit大小，单位byte
	private int scriptSize=0;
	// 标签
	private List<Tag> tags = new ArrayList<Tag>();
	// 关系
	private List<Dependency> dependencies = new ArrayList<Dependency>();
	
	private int referenced;
	
	// bm,view,service
	private String type="";

	private StatisticsProject project;

	private String name="";
	private String path="";
	private CompositeMap rootMap;
	
	//private IFile file;
public ProjectObject(){
	
}
	public CompositeMap getRootMap() {
		return rootMap;
	}

	public void setRootMap(CompositeMap rootMap) {
		this.rootMap = rootMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	// 单位：byte
	public int getScriptSize() {
		return scriptSize;
	}

	public void setScriptSize(int scriptSize) {
		this.scriptSize = scriptSize;
	}

	public void appendScriptSize(int scriptSize) {
		this.scriptSize += scriptSize;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public void addDependency(Dependency dependency) {
		if (dependencies == null) {
			dependencies = new ArrayList<Dependency>();
		}
		dependency.setProject(project);
		dependencies.add(dependency);
	}

	public void addTag(Tag tag) {
		if (tags == null) {
			tags = new ArrayList<Tag>();
		}
		for (Tag t : tags) {
			boolean appendTag = t.appendTag(tag);
			if (appendTag) {
				return;
			}
		}
		tag.setObject(this);
		tag.setProject(project);
		tags.add(tag);
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public StatisticsProject getProject() {
		return project;
	}

	public void setProject(StatisticsProject project) {
		this.project = project;
	}

	public boolean isEquals(ProjectObject o) {
		return this.project.equals(o.getProject())
				&& this.path.equals(o.getPath());
	}

	public static String getType(String fileExtension) {
		if ("bm".equalsIgnoreCase(fileExtension)) {
			return BM;
		}
		if ("svc".equalsIgnoreCase(fileExtension)) {
			return SVC;
		}
		if ("screen".equalsIgnoreCase(fileExtension)) {
			return SCREEN;
		}
		return UNSUPPORT;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public int getReferenced() {
		return referenced;
	}

	public void setReferenced(int referenced) {
		this.referenced = referenced;
	}

//	public IFile getFile() {
//		return file;
//	}
//
//	public void setFile(IFile file) {
//		this.file = file;
//	}
}
