package aurora.ide.api.statistics.model;

import java.util.Arrays;

public class StatisticsProject {
	// eclipse 工程名
	private String eclipseProjectName;

	// 项目名称
	private String projectName = "";
	// 资源库类型，SVN或CVS
	private String repositoryType = "";
	// CVS 取得在head，还是在某个vesion或branch或具体date
	// svn取得当前最高版本revesion。
	private String repositoryRevision = "";
	// 资源库路径
	private String repositoryPath = "";
	// 保存人
	private String storer = "";
	// 保存唯一版本key，默认10,20,30,自动增长。
	// private String storeKey;
	// 保存日期
	private String storeDate = "";
	// id
	private String projectId = "";

	static final public String[] PROPERTIES = { "projectName", "storer", "storeDate", "repositoryType", "repositoryRevesion", "repositoryPath" };

	public static final StatisticsProject NONE_PROJECT = new StatisticsProject("no project");

	public StatisticsProject(String string) {
		this.projectName = string;
	}

	public StatisticsProject(String name, String eclipseProjectName) {
		this(name);
		this.setEclipseProjectName(eclipseProjectName);
	}

	public String getEclipseProjectName() {
		return eclipseProjectName;
	}

	public void setEclipseProjectName(String eclipseProjectName) {
		this.eclipseProjectName = eclipseProjectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProperty(String key) {
		return this.getProperty(Arrays.binarySearch(PROPERTIES, key));
	}

	public String getProperty(int index) {
		switch (index) {
		case 0: {
			return this.getProjectName();
		}
		case 1: {
			return this.getStorer();
		}
		case 2: {
			return this.getStoreDate();
		}
		case 3: {
			return this.getRepositoryType();
		}
		case 4: {
			return this.getRepositoryRevision();
		}
		case 5: {
			return this.getRepositoryPath();
		}
		}
		return null;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRepositoryType() {
		return repositoryType;
	}

	public void setRepositoryType(String repositoryType) {
		this.repositoryType = repositoryType;
	}

	public void setRepositoryRevision(String repositoryRevision) {
		this.repositoryRevision = repositoryRevision;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public String getStorer() {
		return storer;
	}

	public void setStorer(String storer) {
		this.storer = storer;
	}

	public String getStoreDate() {
		return storeDate;
	}

	public void setStoreDate(String storeDate) {
		this.storeDate = storeDate;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getRepositoryRevision() {
		return repositoryRevision;
	}

}
