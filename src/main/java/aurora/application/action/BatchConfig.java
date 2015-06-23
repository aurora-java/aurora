package aurora.application.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.OCManager;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.features.PlaceHolder;
import aurora.service.ServiceInstance;

public class BatchConfig extends AbstractEntry implements IConfigurable {

	private String source;

	private String targetId;

	private CompositeMap config;

	public BatchConfig() {
		super();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		List childs = config.getChilds();
		CompositeMap datas = null;
		List result = new ArrayList();
		if (source != null && childs != null) {
			datas = (CompositeMap) context.getObject(source);
			if (datas == null)
				return;
			Iterator dit = datas.getChildIterator();
			if (dit != null) {
				while (dit.hasNext()) {
					CompositeMap item = (CompositeMap) dit.next();
					Iterator cit = childs.iterator();
					while (cit.hasNext()) {
						CompositeMap iconfig = (CompositeMap) cit.next();
						CompositeMap ic = new CompositeMap(iconfig);
						Set set = ic.keySet();
						if (set != null) {
							Iterator sit = set.iterator();
							while (sit.hasNext()) {
								String key = (String) sit.next();
								ic.put(key, TextParser.parse((String) ic
										.get(key), item));
							}
						}
						result.add(ic);
					}
				}
			}
		}
		ServiceInstance svc = ServiceInstance.getInstance(runner.getContext());
		CompositeMap root = svc.getServiceConfigData().getRoot();
		Map holders = (Map) root.get(PlaceHolder.PLACEHOLDER);
		if (holders != null) {
			CompositeMap holder = (CompositeMap) holders.get(targetId);
			if (holder != null) {
				CompositeMap parent = holder.getParent();
				Iterator it = result.iterator();
				while (it.hasNext()) {
					CompositeMap cm = (CompositeMap) it.next();
					cm.setParent(parent);
				}
				List children = parent.getChilds();
				children.addAll(children.indexOf(holder), result);
				children.remove(holder);
			}
		}
	}

	public void beginConfigure(CompositeMap config) {
		this.config = config;
	}

	public void endConfigure() {

	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
}
