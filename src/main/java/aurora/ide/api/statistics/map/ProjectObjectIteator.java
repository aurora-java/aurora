package aurora.ide.api.statistics.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.api.statistics.IStatisticsManager;
import aurora.ide.api.statistics.IStatisticsReporter;
import aurora.ide.api.statistics.Statistician;
import aurora.ide.api.statistics.model.Dependency;
import aurora.ide.api.statistics.model.ProjectObject;

public class ProjectObjectIteator implements IterationHandle {
	private ProjectObject po;
	private IStatisticsReporter reporter;
	private Statistician statistician;
	private IStatisticsManager statisticsManager;
	private List<ProjectObject> poList;

	public ProjectObjectIteator(Statistician statistician, ProjectObject po, List<ProjectObject> poList) {
		this.statistician = statistician;
		this.statisticsManager = statistician.getStatisticsManager();
		this.po = po;
		this.poList = poList;
	}

	public void process(IStatisticsReporter reporter) {
		this.reporter = reporter;
		CompositeMap rootMap = po.getRootMap();
		rootMap.iterate(this, true);
	}

	public int process(CompositeMap map) {
		StatisticsMap sm = new StatisticsMap(map);
		report(sm);
		return IterationHandle.IT_CONTINUE;
	}

	private void report(StatisticsMap sm) {
		if (sm.isRoot()) {
			this.reporter.reportRoot(po, sm);
		}
		if (sm.isTag()) {
			this.reporter.reportTag(po, sm);
		}
		if (isScript(po, sm)) {
			this.reporter.reportScript(po, sm);
		}
		this.reporter.reportDependency(po, getDependency(po, sm));
	}

	private boolean isScript(ProjectObject po, StatisticsMap sm) {
		if ("script".equalsIgnoreCase(sm.getName())) {
			return true;
		}
		Element element = getElement(sm.getMap());
		if (element == null) {
			return false;
		}
		if (element.getType() != null && element.getElementType() instanceof SimpleType && IStatisticsManager.RawSql.equals(element.getElementType().getQName())) {
			return true;
		}

		return isAttributeType(sm, IStatisticsManager.RawSql);
	}

	public Element getElement(CompositeMap map) {
		try {
			ISchemaManager schemaManager = statistician.getSchemaManager();
			return schemaManager.getElement(map);
		} catch (IllegalArgumentException e) {
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private boolean isAttributeType(StatisticsMap sm, QualifiedName qName) {
		Element element = getElement(sm.getMap());
		if (element != null) {
			List attrib_list = element.getAllAttributes();
			for (Iterator it = attrib_list.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				IType attributeType = attrib.getAttributeType();
				if (attributeType != null && qName.equals(attributeType.getQName())
				// && sm.getMap().getBoolean(attrib.getName()) != null
						&& statisticsManager.getValueIgnoreCase(attrib, sm.getMap()) != null) {
					return true;
				}
			}
		}
		return false;
	}

	private List<Dependency> getDependency(ProjectObject po, StatisticsMap sm) {
		List<Dependency> dependencys = new ArrayList<Dependency>();
		dependencys.addAll(statisticsManager.getDependency(po, poList, sm, IStatisticsManager.BmReference));
		dependencys.addAll(statisticsManager.getDependency(po, poList, sm, IStatisticsManager.ScreenReference));
		dependencys.addAll(statisticsManager.getDependency(po, poList, sm, IStatisticsManager.UrlReference));
		// if (sm.getMap().getName() == null &&
		// statistician.isDependecyContainJS()) {
		// return true;
		// }
		return dependencys;
	}
}
