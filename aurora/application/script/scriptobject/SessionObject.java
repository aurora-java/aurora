package aurora.application.script.scriptobject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.CompositeMap;
import aurora.application.action.HttpSessionOperate;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class SessionObject extends ScriptableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7934572854688154878L;
	public static final String CLASS_NAME = "Session";
	private CompositeMap context;
	private HttpSessionOperate hso;
	private HttpServletRequest request;

	public SessionObject() {
		super();
		hso = new HttpSessionOperate();
		context = ScriptUtil.getContext();
	}

	private void init() {
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(context);
		request = svc.getRequest();
	}

	public CompositeMap getContext() {
		return context;
	}

	public void setContext(CompositeMap context) {
		this.context = context;
	}

	public void jsFunction_write(String target, String source) {
		init();
		hso.setTarget(target);
		hso.setSource(source);
		hso.writeSession(request, context);
	}

	public void jsFunction_create() {
		init();
		request.getSession(true);
	}

	public void jsFunction_clear() {
		init();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	public Object jsFunction_get(String key) {
		CompositeMap sessionMap = (CompositeMap) context.getObject("/session");
		return Context.javaToJS(sessionMap.get(key), getTopLevelScope(this));
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
}
