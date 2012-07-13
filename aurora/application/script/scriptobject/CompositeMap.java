package aurora.application.script.scriptobject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

public class CompositeMap extends ScriptableObject {

	private static final long serialVersionUID = 5555217288330437262L;

	private uncertain.composite.CompositeMap data;

	/**
	 * default zero-argument constructor
	 */
	public CompositeMap() {
		super();
		this.data = new uncertain.composite.CompositeMap();
	}

	public CompositeMap(String name) {
		this();
		jsSet_name(name);
	}

	public CompositeMap(uncertain.composite.CompositeMap data) {
		this.data = data;
	}

	/**
	 * constructor
	 * 
	 * @param cx
	 * @param args
	 * @param ctorObj
	 * @param inNewExpr
	 * @return
	 */
	public static CompositeMap jsConstructor(Context cx, Object[] args,
			Function ctorObj, boolean inNewExpr) {
		if (args.length == 0 || args[0] == Context.getUndefinedValue())
			return new CompositeMap();// no valid arguments
		if (args[0] instanceof uncertain.composite.CompositeMap)// init data
																// with argument
			return new CompositeMap((uncertain.composite.CompositeMap) args[0]);
		else if (args[0] instanceof String)// compositemap name
			return new CompositeMap((String) args[0]);
		else if (args[0] instanceof CompositeMap)// new copy of data
			return new CompositeMap(
					(uncertain.composite.CompositeMap) (((CompositeMap) args[0])
							.getData().clone()));
		return new CompositeMap();// unknown arguments
	}

	public String jsGet_name() {
		return data.getName();
	}

	public void jsSet_name(String name) {
		data.setName(name);
	}

	public uncertain.composite.CompositeMap getData() {
		return data;
	}

	public void setData(uncertain.composite.CompositeMap ctx) {
		this.data = ctx;
	}

	@Override
	public String getClassName() {
		return CompositeMap.class.getSimpleName();
	}

	public Object jsFunction_get(String name) {
		Object d = data.get(name);
		if (d instanceof uncertain.composite.CompositeMap) {
			CompositeMap c = newMap();
			c.setData((uncertain.composite.CompositeMap) d);
			return c;
		} else if (d instanceof NativeObject) {
		}
		return d;
	}

	public void jsFunction_put(String name, Object value) {
		data.put(name, value);
	}

	public void jsFunction_putObject(String key, Object value) {
		data.putObject(key, value, true);
	}

	public Object jsFunction_getObject(String key) {
		Object d = data.getObject(key);
		if (d instanceof uncertain.composite.CompositeMap) {
			CompositeMap c = newMap();
			c.setData((uncertain.composite.CompositeMap) d);
			return c;
		}
		return d;
	}

	public CompositeMap jsFunction_getChild(String name) {
		CompositeMap c = newMap();
		c.setData(data.getChild(name));
		return c;
	}

	public void jsFunction_addChild(Object obj) {
		if (obj instanceof uncertain.composite.CompositeMap) {
			data.addChild((uncertain.composite.CompositeMap) obj);
		} else if (obj instanceof CompositeMap) {
			data.addChild(((CompositeMap) obj).getData());
		}
	}

	protected CompositeMap newMap() {
		return (CompositeMap) ScriptUtil.newObject(this, getClassName());
	}

	public String toString() {
		return getClassName() + ":"
				+ Integer.toHexString(hashCode()).toUpperCase() + "\n"
				+ (data == null ? "null" : data.toXML());
	}
}
