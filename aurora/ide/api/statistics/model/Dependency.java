package aurora.ide.api.statistics.model;

public class Dependency {
	private String dependencyID="";
//	private String objectID="";
//	private String dependencyObjectID="";
	private String projectId="";

	private StatisticsProject project;
	private ProjectObject object;
	private ProjectObject dependencyObject;

	public String getDependencyID() {
		return dependencyID;
	}

	public void setDependencyID(String dependencyID) {
		this.dependencyID = dependencyID;
	}

	public String getObjectID() {
		return object.getObjectId();
	}

//	public void setObjectID(String objectID) {
//		this.objectID = objectID;
//	}

	public String getDependencyObjectID() {
		return dependencyObject.getObjectId();
	}

//	public void setDependencyObjectID(String dependencyObjectID) {
//		this.dependencyObjectID = dependencyObjectID;
//	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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

	public void setObject(ProjectObject object) {
		this.object = object;
	}

	public ProjectObject getDependencyObject() {
		return dependencyObject;
	}

	public void setDependencyObject(ProjectObject dependencyObject) {
		this.dependencyObject = dependencyObject;
	}

}