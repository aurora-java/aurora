package aurora.ide.api.statistics.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesTag {

	private PreferencesTag() {

	}

	private static PreferencesTag tag;

	private Map<String, List<String>> noStatisticsTag = new HashMap<String, List<String>>();

	public Map<String, List<String>> getNoStatisticsTag() {
		return noStatisticsTag;
	}

	private String[] auroraApplicationAction = { "create-config", "process-config" };
	private String[] auroraDatabaseLocalOracle = { "sequence-pk", "sequence-use" };
	private String[] application = { "service-output", "map", "layout", "model-query", "AbstractModelAction", "button", "Component", "Axi", "title", "DataSetReference", "JavaScript", "yAxi", "CaseType", "menuBar", "Field", "tree", "model-execute", "toolBar", "lov", "radio", "model-insert", "model-delete", "autoForm", "column", "tooltip", "numberField", "HasName", "textArea", "tabPanel", "label", "record", "URLReference", "event", "BindTarget", "HasClassName", "RectangleComponent", "HasStyle", "HasPrompt", "HasID", "template", "hBox", "RawSql", "tab", "table", "datePicker", "upload", "vBox", "caseType", "box", "selectType", "checkBox", "placeHolder", "screen-include", "xAxi", "grid", "autoGrid", "comboBox", "passWord", "fieldSet", "view", "screenBody", "dataSet", "chart", "init-procedure",
			"model-batch-update", "textField", "treeGrid", "field", "mapping", "chartType", "service", "BaseService", "item", "screen", "view-config", "alignType", "navBarType", "dateTimePicker", "model-update", "vAlignType" };
	private String[] noNamespace = { "center", "event", "data-filter", "view", "model-delete", "service-output", "model-update", "param", "mapping", "map", "model-load", "model-query", "field", "ref-field", "model-insert", "script", "input", "column", "query-field", "model-execute", "model", "batch-apply", "pk-field", "parameter", "service" };
	private String[] bm = { "ref-field", "ModelReference", "reference", "model", "description", "ref-fields", "query-fields", "data-filters", "order-by", "cascade-operations", "primary-key", "feature", "query-sql", "JoinType", "ExtendModeType", "update-sql", "pk-field", "parameter", "operation", "field", "query-field", "order-field", "relation", "data-filter" };
	private String[] uncertainProc = { "assert", "UncertainBuiltinAction", "dump-map", "procedure", "UncertainBuiltinCompositeAction", "exception-handles", "AbstractAction", "switch", "case", "loop", "field", "action", "catch", "set", "echo" };
	private String[] auroraDatabaseFeatures = { "multi-language-storage", "tag-delete-field", "standard-who", "tag-delete" };

	public static PreferencesTag INSTANCE() {
		if (tag == null) {
			tag = new PreferencesTag();
		}
		return tag;
	}

	private Map<String, List<String>> namespaceMap = new HashMap<String, List<String>>();

	public void setNamespaceMap(Map<String, List<String>> namespaceMap) {
		this.namespaceMap = namespaceMap;
	}

	private Map<String, List<String>> defaultMap = new HashMap<String, List<String>>() {
		private static final long serialVersionUID = -2136894063010579177L;
		{
			this.put("aurora.application.action", new ArrayList<String>());
			for (String s : auroraApplicationAction) {
				this.get("aurora.application.action").add(s);
			}
			this.put("aurora.database.local.oracle", new ArrayList<String>());
			for (String s : auroraDatabaseLocalOracle) {
				this.get("aurora.database.local.oracle").add(s);
			}
			this.put("http://www.aurora-framework.org/application", new ArrayList<String>());
			for (String s : application) {
				this.get("http://www.aurora-framework.org/application").add(s);
			}
			this.put("No namespace", new ArrayList<String>());
			for (String s : noNamespace) {
				this.get("No namespace").add(s);
			}
			this.put("http://www.aurora-framework.org/schema/bm", new ArrayList<String>());
			for (String s : bm) {
				this.get("http://www.aurora-framework.org/schema/bm").add(s);
			}
			this.put("uncertain.proc", new ArrayList<String>());
			for (String s : uncertainProc) {
				this.get("uncertain.proc").add(s);
			}
			this.put("aurora.database.features", new ArrayList<String>());
			for (String s : auroraDatabaseFeatures) {
				this.get("aurora.database.features").add(s);
			}
		}
	};

	public Map<String, List<String>> getDefaultMap() {
		return defaultMap;
	}

	public void addTag(String namespace, String tagName) {
		List<String> tags = namespaceMap.get(namespace);
		if (tags == null) {
			tags = new ArrayList<String>();
			namespaceMap.put(namespace, tags);
		}
		if (tags.contains(tagName))
			return;
		tags.add(tagName);
	}

	public List<String> getTags(String namespace) {
		return getNSMap().get(namespace);
	}

	public boolean hasTag(String namespace, String tagName) {
		if (null == namespace || "".equals(namespace.trim())) {
			namespace = "No namespace";
		}
		List<String> tags = this.getTags(namespace);
		if (null == tags) {
			if (noStatisticsTag.get(namespace) == null) {
				noStatisticsTag.put(namespace, new ArrayList<String>());
			}
			noStatisticsTag.get(namespace).add(tagName);
			return false;
		}
		boolean bool = tags.contains(tagName);
		if (!bool) {
			if (noStatisticsTag.get(namespace) == null) {
				noStatisticsTag.put(namespace, new ArrayList<String>());
			}
			noStatisticsTag.get(namespace).add(tagName);
		}
		return bool;
	}

	public Collection<String> getNamespaces() {
		return getNSMap().keySet();
	}

	private Map<String, List<String>> getNSMap() {
		return this.namespaceMap;
	}

	public String getType(String namespace, String tagName) {
		return null;
	}

}
