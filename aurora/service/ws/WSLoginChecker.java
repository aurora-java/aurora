package aurora.service.ws;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.sun.xml.internal.messaging.saaj.util.Base64;

import aurora.application.util.LanguageUtil;
import aurora.bm.BusinessModel;
import aurora.database.actions.config.ActionConfigManager;
import aurora.database.actions.config.ModelQueryConfig;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.service.IServiceFactory;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.ServiceInvoker;
import aurora.service.http.HttpServiceInstance;
import aurora.service.validation.ErrorMessage;
import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.AbstractEntry;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureConfigManager;
import uncertain.proc.ProcedureRunner;

/*
 * see aurora.service.ws.SOAPServiceInterpreter
 */
public class WSLoginChecker extends AbstractEntry {

	private IDatabaseServiceFactory mDatabaseServiceFactory;
	private OCManager mOcManager;
	private IObjectRegistry registry;

	private String model;
	private String modelaction = "execute";
	private String field = "/parameter/@return_value";
	private String value = "false";
	private String message = "username or password is wrong";

	public WSLoginChecker(OCManager ocManager, IDatabaseServiceFactory databaseServiceFactory, IObjectRegistry registry) {
		this.mOcManager = ocManager;
		this.mDatabaseServiceFactory = databaseServiceFactory;
		this.registry = registry;
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		try {
			if (model == null)
				throw BuiltinExceptionFactory.createAttributeMissing(this, "model");
			HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(context);
			HttpServletRequest request = svc.getRequest();
			if (!isSOAPRequest(request))
				throw new IllegalStateException("This is not a soap request!");
			CompositeMap bmRunContext = createBMContext(context, request);

			CompositeMap config = createAction(bmRunContext);
			runBM(config, bmRunContext);
			String fieldvalue = (String) bmRunContext.getObject(this.getField());
			String checkvalue = getValue();
			if (fieldvalue != null && fieldvalue.equals(checkvalue)) {
				String msg = message == null ? checkvalue : message;
				msg = LanguageUtil.getTranslatedMessage(registry, msg, context);
				setError(context, checkvalue, msg);
			}
		} catch (Throwable e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.close();
			setError(context, "Exception", sw.toString());
		}
	}

	private void setError(CompositeMap context, String code, String message) {
		context.putBoolean("success", false);
		ErrorMessage em = new ErrorMessage(code, message, null);
		ServiceContext sc = ServiceContext.createServiceContext(context);
		sc.setError(em.getObjectContext());
	}

	private boolean isSOAPRequest(HttpServletRequest svc) {
		String soapParam = svc.getHeader("soapaction");
		if (soapParam != null)
			return true;
		return false;
	}

	private CompositeMap createBMContext(CompositeMap context, HttpServletRequest request) {
		CompositeMap bmRunContext = new CompositeMap("context");
		CompositeMap paramter = parseAuthorization(context);
		String url = request.getRequestURL().toString();
		paramter.put("url", url);
		bmRunContext.addChild(paramter);
		return bmRunContext;
	}

	private CompositeMap parseAuthorization(CompositeMap context) {
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance.getInstance(context);
		String authorization = svc.getRequest().getHeader("Authorization");
		CompositeMap paramter = new CompositeMap("parameter");
		if (authorization != null) {
			String encodeAuth = authorization.substring("Basic ".length());
			String decode = Base64.base64Decode(encodeAuth);
			String[] strs = decode.split(":");
			paramter.put("user", strs[0]);
			paramter.put("password", strs[1]);
		}
		return paramter;
	}

	private CompositeMap createAction(CompositeMap context) throws Exception {
		BusinessModel bm = mDatabaseServiceFactory.getModelFactory().getModelForRead(model);
		if (bm == null)
			throw new ServletException("Can't load model:" + model);
		CompositeMap action_config = null;
		if ("query".equals(modelaction)) {
			ModelQueryConfig mq = ActionConfigManager.createModelQuery(model);
			mq.setParameters(context.getChild("parameter"));
			mq.setAttribFromRequest(true);
			action_config = mq.getObjectContext();
		} else if ("update".equals(modelaction)) {
			action_config = ActionConfigManager.createModelUpdate(model);
		} else if ("insert".equals(modelaction)) {
			action_config = ActionConfigManager.createModelInsert(model);
		} else if ("delete".equals(modelaction)) {
			action_config = ActionConfigManager.createModelDelete(model);
		} else if ("batch_update".equals(modelaction)) {
			action_config = ActionConfigManager.createModelBatchUpdate(model);
		} else if ("execute".equals(modelaction)) {
			action_config = ActionConfigManager.createModelAction("model-execute", model);
		} else
			throw new ServletException("Unknown command:" + modelaction);
		return action_config;
	}

	private void runBM(CompositeMap config, CompositeMap context) throws Exception {
		CompositeMap proc_config = ProcedureConfigManager.createConfigNode("procedure");
		proc_config.addChild(config);
		Procedure proc = (Procedure) mOcManager.createObject(proc_config);

		String name = "WSLoginChecker." + model;
		IServiceFactory serviceFactory = (IServiceFactory) registry.getInstanceOfType(IServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, IServiceFactory.class, this.getClass().getCanonicalName());
		ServiceInvoker.invokeProcedureWithTransaction(name, proc, serviceFactory, context);
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModelaction() {
		return modelaction;
	}

	public void setModelaction(String modelaction) {
		this.modelaction = modelaction;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
