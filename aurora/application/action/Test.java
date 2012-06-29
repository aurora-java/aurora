package aurora.application.action;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.ProcedureRunner;
import aurora.application.util.LanguageUtil;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;

public class Test extends Check {

	private OCManager oc_manager;

	public Test(OCManager oc_manager, IObjectRegistry registry) {
		this.oc_manager = oc_manager;
		this.registry = registry;
	}

	private String errorMessage;
	IObjectRegistry registry;

	public Test() {
	}

	public void run(ProcedureRunner runner) {
		super.run(runner);
		boolean result = this.getResult();
		if (result) {
			Object checkvalue = this.getValue();
			CompositeMap context = runner.getContext();
			ServiceContext sc = ServiceContext.createServiceContext(context);
			context.putBoolean("success", false);
			String msg = errorMessage == null ? "" + checkvalue : errorMessage;
			msg = LanguageUtil.getTranslatedMessage(registry, msg, context);
			ErrorMessage em = new ErrorMessage("" + checkvalue, msg, null);
			sc.setError(em.getObjectContext());
			runner.stop();
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}