package aurora.application.script.scriptobject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.CompositeMap;

public class ContextWraper extends ScriptableObject {

	private static final long serialVersionUID = 5555217288330437262L;
	public static CompositeMap init_data;
	public static Context init_context;
	private CompositeMap data;

	public ContextWraper() {
		super();
		this.data = init_data;
	}

	public CompositeMap getContext() {
		return data;
	}

	public void setContext(CompositeMap ctx) {
		this.data = ctx;
	}

	@Override
	public String getClassName() {
		return ContextWraper.class.getSimpleName();
	}

	public Object jsFunction_get(String name) {
		return data.get(name);
	}

	public void jsFunction_put(String name, Object value) {
		data.put(name, value);
	}

	public ContextWraper jsFunction_getChild(String name) {
		ContextWraper c = new ContextWraper();
		c.setContext(data.getChild(name));
		c.setPrototype(getPrototype());
		return c;
	}

	public String toString() {
		return getClassName() + ":" + hashCode() + "\n"
				+ (data == null ? "null" : data.toXML());
	}
}
