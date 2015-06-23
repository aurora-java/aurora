package aurora.ide.api.statistics;

import java.util.List;

import aurora.ide.api.statistics.map.StatisticsMap;
import aurora.ide.api.statistics.model.Dependency;
import aurora.ide.api.statistics.model.ProjectObject;

public interface IStatisticsReporter {
	public void reportRoot(ProjectObject po, StatisticsMap sm);

	public void reportDependency(ProjectObject po, List<Dependency> Dependencys);

	public void reportTag(ProjectObject po, StatisticsMap sm);

	public void reportScript(ProjectObject po, StatisticsMap sm);
}
