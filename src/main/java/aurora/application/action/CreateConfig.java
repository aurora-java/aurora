package aurora.application.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import aurora.application.features.PlaceHolder;
import aurora.service.ServiceInstance;

public class CreateConfig extends Procedure {
	
	public static final String PATH_NAME = "/@_create_config";
	
	private String targetId;
	
	private List resultList = new ArrayList();
    
    
	public CreateConfig(){
        super();
    }
    
    public CreateConfig(OCManager om){
        super(om);
    }
	
	public void run(ProcedureRunner runner) throws Exception {
		runner.getContext().putObject(PATH_NAME, this, true);
        super.run(runner);
        
        ServiceInstance svc = ServiceInstance.getInstance(runner.getContext());
		CompositeMap root = svc.getServiceConfigData().getRoot();
		Map holders = (Map) root.get(PlaceHolder.PLACEHOLDER);
		if (holders != null) {
			CompositeMap holder = (CompositeMap) holders.get(targetId);
			if (holder != null) {
				CompositeMap parent = holder.getParent();
				Iterator it = getResultList().iterator();
				while (it.hasNext()) {
					CompositeMap cm = (CompositeMap) it.next();
					cm.setParent(parent);
				}
				List children = parent.getChilds();
				children.addAll(children.indexOf(holder), getResultList());
				children.remove(holder);
			}
		}
	}

	public void endConfigure() {
	}
	
	public List getResultList() {
		return resultList;
	}

	
	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
}
