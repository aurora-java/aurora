package aurora.ide.api.statistics;

public class RepositoryInfo {
	public static final String SVN = "SVN";
	public static final String CVS = "CVS";
	private String repoPath;
	private String revision;
	private String userName;
	private String type;

	public RepositoryInfo(String repoPath, String revision) {
		super();
		this.repoPath = repoPath;
		this.revision = revision;
	}

	public String getRepoPath() {
		return repoPath;
	}

	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
