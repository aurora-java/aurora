package aurora.ide.api.statistics;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.schema.ISchemaManager;
import aurora.ide.api.statistics.map.ProjectObjectIteator;
import aurora.ide.api.statistics.map.StatisticsMap;
import aurora.ide.api.statistics.map.StatisticsResult;
import aurora.ide.api.statistics.model.Dependency;
import aurora.ide.api.statistics.model.ProjectObject;
import aurora.ide.api.statistics.model.StatisticsProject;
import aurora.ide.api.statistics.model.Tag;

/**
 * process and save the statistical data
 * 
 * */
public class Statistician implements IStatisticsReporter {

	private StatisticsProject project;
	private StatisticsResult result = new StatisticsResult();
	private List<ProjectObject> poList = new LinkedList<ProjectObject>();
	private ISchemaManager schemaManager;
	private IStatisticsManager statisticsManager;
	private boolean isDependecyContainJS;
	private List<IRunningListener> runnningListeners;

	private Map<String, List<String>> reference = new HashMap<String, List<String>>();
	private Map<String, Integer> referenced = new HashMap<String, Integer>();

	public void setDependecyContainJS(boolean isDependecyContainJS) {
		this.isDependecyContainJS = isDependecyContainJS;
	}

	public Statistician(StatisticsProject project, ISchemaManager schemaManager, IStatisticsManager statisticsManager) {
		this.project = project;
		this.result.setProject(project);
		this.schemaManager = schemaManager;
		this.setStatisticsManager(statisticsManager);
	}

	public void addProjectObject(ProjectObject po) {
		this.poList.add(po);
		result.addProjectObject(po);
		po.setProject(project);
	}

	public ISchemaManager getSchemaManager() {
		return schemaManager;
	}

	public void setSchemaManager(ISchemaManager schemaManager) {
		this.schemaManager = schemaManager;
	}

	public IStatisticsManager getStatisticsManager() {
		return statisticsManager;
	}

	public void setStatisticsManager(IStatisticsManager statisticsManager) {
		this.statisticsManager = statisticsManager;
	}

	public StatisticsProject getProject() {
		return project;
	}

	public void setProject(StatisticsProject project) {
		this.project = project;
	}

	public StatisticsResult getResult() {
		return result;
	}

	public void setResult(StatisticsResult result) {
		this.result = result;
	}

	public List<IRunningListener> getRunningListeners() {
		return runnningListeners;
	}

	public void addRuningListener(IRunningListener l) {
		if (runnningListeners == null) {
			runnningListeners = new ArrayList<IRunningListener>();
		}
		this.runnningListeners.add(l);
	}

	private boolean noticeRunning(ProjectObject po, int poIndex) {
		if (runnningListeners != null) {
			for (IRunningListener l : runnningListeners) {
				if (false == l.notice(po, poIndex)) {
					return false;
				}
			}
		}
		return true;
	}

	public List<ProjectObject> getPoList() {
		return poList;
	}

	public void setPoList(List<ProjectObject> poList) {
		this.poList = poList;
	}

	// Iteration CompositeMap
	public StatisticsResult doStatistic() {
		int i = 0;
		for (ProjectObject po : this.poList) {
			if (false == noticeRunning(po, i)) {
				return result;
			}
			ProjectObjectIteator it = new ProjectObjectIteator(this, po, poList);
			it.process(this);
			i++;
		}
		fillReferenced();
		for (ProjectObject po : result.getProjectObjects()) {
			if (referenced.get(po.getPath()) != null) {
				po.setReferenced(referenced.get(po.getPath()).intValue());
			}
		}
		return result;
	}

	public Status save(Connection connection) {
		DatabaseAction action = new DatabaseAction(this);
		return action.saveAll(connection);
	}

	public StatisticsResult read(Connection connection) throws SQLException {
		DatabaseAction action = new DatabaseAction(this);
		return action.readAll(connection);
	}

	public void reportRoot(ProjectObject po, StatisticsMap sm) {
		po.setFileSize(sm.getSize());
	}

	public void reportDependency(ProjectObject po, List<Dependency> Dependencys) {
		for (Dependency d : Dependencys) {
			if (reference.get(po.getPath()) == null) {
				reference.put(po.getPath(), new ArrayList<String>());
			}
			reference.get(po.getPath()).add(d.getDependencyObject().getPath());
			d.setProject(project);
			d.setObject(po);
			po.addDependency(d);
		}
	}

	public void reportTag(ProjectObject po, StatisticsMap sm) {
		Tag tag = new Tag(sm);
		tag.setProject(project);
		tag.setObject(po);
		po.addTag(tag);
	}

	public void reportScript(ProjectObject po, StatisticsMap sm) {
		int size = sm.getSize();
		po.appendScriptSize(size);
	}

	private void fillReferenced() {
		for (List<String> list : reference.values()) {
			for (String s : list) {
				referenced.put(s, referenced.get(s) == null ? 1 : referenced.get(s) + 1);
			}
		}
	}

	public boolean isDependecyContainJS() {

		return isDependecyContainJS;
	}
}
