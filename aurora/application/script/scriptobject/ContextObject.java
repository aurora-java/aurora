package aurora.application.script.scriptobject;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.annotations.JSFunction;

import aurora.service.ServiceContext;

public class ContextObject extends CompositeMap {
	private static final long serialVersionUID = -8390151756372174865L;
	public static final String CLASS_NAME = "ContextMap";
	private Object parameter = null, session = null, cookie = null;

	public ContextObject() {
		super();
		setData(ScriptUtil.getContext());
	}

	public String getClassName() {
		return CLASS_NAME;
	}

	@JSFunction
	public Object getParameter() {
		if (parameter == null) {
			parameter = jsFunction_getChild("parameter");
		}
		return parameter;
	}

	@JSFunction
	public Object getSession() {
		if (session == null) {
			session = jsFunction_getChild("session");
		}
		return session;
	}

	@JSFunction
	public Object getCookie() {
		if (cookie == null) {
			cookie = jsFunction_getChild("cookie");
		}
		return cookie;
	}

	@JSFunction
	public Object getModel() {
		return jsFunction_get(ServiceContext.KEY_MODEL);
	}

	@Override
	public String jsGet_name() {
		return super.jsGet_name();
	}

	@Override
	public void jsSet_name(String name) {
		super.jsSet_name(name);
	}

	@Override
	public Object jsFunction_get(Object name) {
		return super.jsFunction_get(name);
	}

	@Override
	public int jsGet_length() {
		return super.jsGet_length();
	}

	@Override
	public NativeArray jsGet_children() {
		return super.jsGet_children();
	}

	@Override
	public NativeArray jsFunction_getChildren() {
		return super.jsFunction_getChildren();
	}

	@Override
	public void jsFunction_put(String name, Object value) {
		super.jsFunction_put(name, value);
	}

	@Override
	public void jsFunction_putObject(String key, Object value) {
		super.jsFunction_putObject(key, value);
	}

	@Override
	public Object jsFunction_getObject(String key) {
		return super.jsFunction_getObject(key);
	}

	@Override
	public CompositeMap jsFunction_getChild(String name) {
		return super.jsFunction_getChild(name);
	}

	@Override
	public void jsFunction_addChild(Object obj) {
		super.jsFunction_addChild(obj);
	}

	@Override
	public String jsFunction_toXML() {
		return super.jsFunction_toXML();
	}

	@Override
	public String jsFunction_toString() {
		return super.jsFunction_toString();
	}

}
