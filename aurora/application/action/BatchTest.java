package aurora.application.action;

import java.util.LinkedList;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IChildContainerAcceptable;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class BatchTest extends AbstractEntry implements
		IChildContainerAcceptable {
	LinkedList<Test> testList = new LinkedList<Test>();
	private OCManager oc_manager;

	public BatchTest(OCManager oc_manager) {
		this.oc_manager = oc_manager;
	}

	IObjectRegistry registry;

	public BatchTest() {
	}

	public void addTest(Test c) {
		testList.add(c);
		c.setOwner(this);
	}

	public void run(ProcedureRunner runner) throws Exception {
		for (Test t : testList) {
			if (runner.isRunning())
				t.run(runner);
		}

		// runner.run();
		// runner.getProcedure();
		// runner.getProcedure().run(runner)
		// CompositeMap context = runner.getContext();
		// ServiceContext sc = ServiceContext.createServiceContext(context);
		//
		// String fieldvalue = (String)context.getObject(this.getField());
		// String checkvalue = this.getValue();
		// if (fieldvalue!= null && fieldvalue.equals(checkvalue)) {
		// context.putBoolean("success", false);
		// String url = TextParser.parse(this.getDispatchUrl(), context);
		// context.put("dispatch_url", url);
		// context.put("dispatch_type", getDispatchType());
		//
		// String msg = message==null?checkvalue:message;
		// msg = LanguageUtil.getTranslatedMessage(registry, msg, context);
		//
		// ErrorMessage em = new ErrorMessage(checkvalue, msg, null);
		// sc.setError(em.getObjectContext());
		// }
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addChild(CompositeMap child) {

	}
}