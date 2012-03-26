package aurora.ide.api.statistics.map;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.api.statistics.model.ProjectObject;
import aurora.ide.api.statistics.model.StatisticsProject;

public class StatisticsResult {
	private StatisticsProject project;
	private List<ProjectObject> bms = new ArrayList<ProjectObject>();
	private List<ProjectObject> screens = new ArrayList<ProjectObject>();

	public StatisticsProject getProject() {
		return project;
	}

	public void setProject(StatisticsProject project) {
		this.project = project;
	}

	public List<ProjectObject> getBms() {
		return bms;
	}

	public void setBms(List<ProjectObject> bms) {
		this.bms = bms;
	}

	public void addBm(ProjectObject bm) {
		bms.add(bm);
	}

	public void addScreen(ProjectObject screen) {
		screens.add(screen);
	}

	public List<ProjectObject> getScreens() {
		return screens;
	}

	public void setScreens(List<ProjectObject> screens) {
		this.screens = screens;
	}

	public void addProjectObject(ProjectObject po) {
		if (ProjectObject.BM.equals(po.getType())) {
			addBm(po);
		} else {
			addScreen(po);
		}
	}

	public List<ProjectObject> getProjectObjects() {
		List<ProjectObject> list = new ArrayList<ProjectObject>();
		list.addAll(screens);
		list.addAll(bms);
		return list;

	}

	public ObjectStatisticsResult getBMStatisticsResult() {
		if (this.bms == null)
			return null;
		return new ObjectStatisticsResult(this.project, this.bms, ProjectObject.BM);
	}

	public ObjectStatisticsResult getSVCStatisticsResult() {
		List<ProjectObject> result = new ArrayList<ProjectObject>();
		if (this.screens == null)
			return null;
		for (ProjectObject po : this.screens) {
			if (ProjectObject.SVC.equals(po.getType())) {
				result.add(po);
			}
		}
		return new ObjectStatisticsResult(this.project, result, ProjectObject.SVC);
	}

	public ObjectStatisticsResult getSreenStatisticsResult() {
		if (this.screens == null)
			return null;
		List<ProjectObject> result = new ArrayList<ProjectObject>();
		for (ProjectObject po : this.screens) {
			if (ProjectObject.SCREEN.equals(po.getType())) {
				result.add(po);
			}
		}
		return new ObjectStatisticsResult(this.project, result, ProjectObject.SCREEN);
	}
}
