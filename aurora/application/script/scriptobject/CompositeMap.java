package aurora.application.script.scriptobject;

import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

public class CompositeMap extends IdScriptableObject {

	private static final long serialVersionUID = 5555217288330437262L;
	public static final String CLASS_NAME = "CompositeMap";
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
		return CLASS_NAME;
	}

	public Object jsFunction_get(Object name) {
		Object d = data.get(name);
		if (d instanceof uncertain.composite.CompositeMap) {
			CompositeMap c = newMap();
			c.setData((uncertain.composite.CompositeMap) d);
			return c;
		} else if (d instanceof java.sql.Date) {
		}
		return Context.javaToJS(d, getTopLevelScope(this));
	}

	public int jsGet_length() {
		return data.getChildsNotNull().size();
	}

	public NativeArray jsGet_children() {
		@SuppressWarnings("unchecked")
		List<uncertain.composite.CompositeMap> list = data.getChildsNotNull();
		int length = list.size();
		NativeArray arr = ScriptUtil.newArray(this, length);
		for (int i = 0; i < length; i++) {
			CompositeMap m = newMap();
			m.setData(list.get(i));
			arr.put(i, arr, m);
		}
		return arr;
	}

	public NativeArray jsFunction_getChildren() {
		return jsGet_children();
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
		} else if (d instanceof java.sql.Date) {
		}
		Object obj = Context.javaToJS(d, getTopLevelScope(this));
		return obj;
	}

	public CompositeMap jsFunction_getChild(String name) {
		uncertain.composite.CompositeMap d = data.getChild(name);
		if (d == null)
			return null;
		CompositeMap c = newMap();
		c.setData(d);
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
		return (CompositeMap) ScriptUtil.newObject(this,
				CompositeMap.CLASS_NAME);
	}

	@Override
	public boolean has(String name, Scriptable start) {
		if (data.containsKey(name))
			return true;
		return super.has(name, start);
	}

	@Override
	public Object get(String name, Scriptable start) {
		Object obj = jsFunction_get(name);
		if (obj != null)
			return obj;
		return super.get(name, start);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		jsFunction_put(name, value);
		super.put(name, start, value);
	}

	/**
	 * toString method for java code
	 */
	public String toString() {
		return getClassName() + ":"
				+ Integer.toHexString(hashCode()).toUpperCase() + "\n"
				+ jsFunction_toXML();
	}

	/**
	 * delegate for uncertain.composite.CompositeMap.toXML()
	 * 
	 * @return
	 */
	public String jsFunction_toXML() {
		return data == null ? "null" : data.toXML();
	}

	/**
	 * toString method for js code
	 * 
	 * @return
	 */
	public String jsFunction_toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append(getClassName());
		sb.append("[name=");
		sb.append(data.getName());
		sb.append(';');
		sb.append("propertyCount=");
		sb.append(data.keySet().size());
		sb.append(';');
		sb.append("childCount=");
		sb.append(data.getChildsNotNull().size());
		sb.append(']');
		return sb.toString();
	}
}
