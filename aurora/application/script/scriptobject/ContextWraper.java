package aurora.application.script.scriptobject;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.CompositeMap;

public class ContextWraper extends ScriptableObject {

	private static final long serialVersionUID = 5555217288330437262L;
	public static CompositeMap init_data;
	private CompositeMap data;

	public ContextWraper() {
		super();
		this.data = init_data;
	}

	public CompositeMap getData() {
		return data;
	}

	public String jsGet_name() {
		return data.getName();
	}

	public void jsSet_name(String name) {
		data.setName(name);
	}

	public void setData(CompositeMap ctx) {
		this.data = ctx;
	}

	@Override
	public String getClassName() {
		return ContextWraper.class.getSimpleName();
	}

	public Object jsFunction_get(String name) {
		Object d = data.get(name);
		if (d instanceof CompositeMap) {
			ContextWraper c = newContextWraper();
			c.setData((CompositeMap) d);
			return c;
		} else if (d instanceof NativeObject) {
		}
		return d;
	}

	public void jsFunction_put(String name, Object value) {
		data.put(name, value);
	}

	public ContextWraper jsFunction_getChild(String name) {
		ContextWraper c = newContextWraper();
		c.setData(data.getChild(name));
		return c;
	}

	public void jsFunction_addChild(Object obj) {
		if (obj instanceof CompositeMap) {
			data.addChild((CompositeMap) obj);
		} else if (obj instanceof ContextWraper) {
			data.addChild(((ContextWraper) obj).getData());
		}
	}

	protected ContextWraper newContextWraper() {
		return (ContextWraper) ScriptUtil.newObject(this, getClassName());
	}

	public String toString() {
		return getClassName() + ":" + hashCode() + "\n"
				+ (data == null ? "null" : data.toXML());
	}
}
