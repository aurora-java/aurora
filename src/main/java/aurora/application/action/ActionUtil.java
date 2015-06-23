package aurora.application.action;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.application.util.LanguageUtil;
import aurora.service.ServiceContext;
import aurora.service.validation.ErrorMessage;

public class ActionUtil {
	public static void raiseApplicationError(ProcedureRunner runner,
			IObjectRegistry registry, String code) {
		CompositeMap context = runner.getContext();
		ServiceContext sc = ServiceContext.createServiceContext(context);
		context.putBoolean("success", false);
		String msg = LanguageUtil.getTranslatedMessage(registry, code, context);
		ErrorMessage em = new ErrorMessage(code, msg, null);
		sc.setError(em.getObjectContext());
		runner.stop();
	}
}
