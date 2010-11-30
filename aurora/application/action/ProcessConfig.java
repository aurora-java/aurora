package aurora.application.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IConfigurable;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;

public class ProcessConfig extends Procedure implements IConfigurable {

	private CompositeMap config;

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap datas = runner.getContext();
		List childs = config.getChilds();
		List result = new ArrayList();
		if (childs != null) {
			if (datas == null)
				return;
			Iterator cit = childs.iterator();
			while (cit.hasNext()) {
				CompositeMap iconfig = (CompositeMap) cit.next();
				CompositeMap ic = new CompositeMap(iconfig);
				Set set = ic.keySet();
				if (set != null) {
					Iterator sit = set.iterator();
					while (sit.hasNext()) {
						String key = (String) sit.next();
						ic.put(key, TextParser.parse((String) ic.get(key),
								datas));
					}
				}
				result.add(ic);
			}
		}
		CreateConfig createConfig = (CreateConfig) runner.getContext()
				.getObject(CreateConfig.PATH_NAME);
		createConfig.getResultList().addAll(result);
	}

	public void beginConfigure(CompositeMap config) {
		this.config = config;
	}

	public void endConfigure() {
	}
}
