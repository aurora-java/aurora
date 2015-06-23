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

@SuppressWarnings("unchecked")
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
				process(ic,datas);
				result.add(ic);
			}
		}
		CreateConfig createConfig = (CreateConfig) runner.getContext().getObject(CreateConfig.PATH_NAME);
		createConfig.getResultList().addAll(result);
	}
	
	private void process(CompositeMap view ,CompositeMap model){
		Set set = view.keySet();
		if (set != null) {
			Iterator sit = set.iterator();
			while (sit.hasNext()) {
				String key = (String) sit.next();
				view.put(key, TextParser.parse((String) view.get(key),model));
			}
		}
		String text = view.getText();
		if(text!=null)view.setText(TextParser.parse(text,model));
		
		
		List children = view.getChilds();
		if(children!=null){
			Iterator it = children.iterator();
			while(it.hasNext()){
				CompositeMap child = (CompositeMap)it.next();
				process(child,model);
			}
		}
	}

	public void beginConfigure(CompositeMap config) {
		this.config = config;
	}

	public void endConfigure() {
	}
}
