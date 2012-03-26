package aurora.ide.api.statistics;

import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import aurora.ide.api.statistics.map.StatisticsMap;
import aurora.ide.api.statistics.model.Dependency;
import aurora.ide.api.statistics.model.ProjectObject;

public interface IStatisticsManager {
	public final static QualifiedName BmReference = new QualifiedName("http://www.aurora-framework.org/schema/bm", "model");

	public final static QualifiedName ScreenReference = new QualifiedName("http://www.aurora-framework.org/application", "screen");

	public final static QualifiedName UrlReference = new QualifiedName("http://www.aurora-framework.org/application", "screenBm");

	public final static QualifiedName RawSql = new QualifiedName("http://www.aurora-framework.org/application", "RawSql");

	public List<Dependency> getDependency(ProjectObject po, List<ProjectObject> poList, StatisticsMap sm, QualifiedName qName);

	public String getValueIgnoreCase(Attribute a, CompositeMap cMap);
}
