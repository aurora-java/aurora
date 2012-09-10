package aurora.application.script.scriptobject;

import java.util.List;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.CompositeMap;

public class CompositeMapObject extends ScriptableObject {

	private static final long serialVersionUID = 5555217288330437262L;
	public static final String CLASS_NAME = "CompositeMap";
	private CompositeMap data;

	/**
	 * default zero-argument constructor
	 */
	public CompositeMapObject() {
		super();
		this.data = new CompositeMap();
	}

	public CompositeMapObject(String name) {
		this();
		data.setName(name);
	}

	public CompositeMapObject(CompositeMap data) {
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
	public static CompositeMapObject jsConstructor(Context cx, Object[] args,
			Function ctorObj, boolean inNewExpr) {
		if (args.length == 0 || args[0] == Context.getUndefinedValue())
			return new CompositeMapObject();// no valid arguments
		if (args[0] instanceof CompositeMap)// init data
											// with argument
			return new CompositeMapObject((CompositeMap) args[0]);
		else if (args[0] instanceof String) {
			if (args.length == 1)// compositemap name
				return new CompositeMapObject((String) args[0]);
			if (args.length == 2 && (args[1] instanceof String)) {
				// compositemap prefix and name
				CompositeMapObject cmo = new CompositeMapObject(
						(String) args[1]);
				cmo.data.setPrefix((String) args[0]);
				return cmo;
			}
			if (args.length == 3 && (args[1] instanceof String)
					&& (args[2] instanceof String)) {
				// compositemap prefix ,namespace uri and name
				CompositeMapObject cmo = new CompositeMapObject(
						(String) args[2]);
				cmo.data.setNameSpace((String) args[0], (String) args[1]);
				return cmo;
			}
		} else if (args[0] instanceof CompositeMapObject)// new copy of data
			return new CompositeMapObject(
					(CompositeMap) (((CompositeMapObject) args[0]).getData()
							.clone()));
		else if (args[0] instanceof NativeJavaObject) {
			NativeJavaObject njo = (NativeJavaObject) args[0];
			return jsConstructor(cx, new Object[] { njo.unwrap() }, ctorObj,
					inNewExpr);
		}
		return new CompositeMapObject();// unknown arguments
	}

	public CompositeMap getData() {
		return data;
	}

	public void setData(CompositeMap ctx) {
		this.data = ctx;
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	public int jsGet_length() {
		return data.getChildsNotNull().size();
	}

	public Object jsFunction_getData() {
		return getData();
	}

	public void jsFunction_setData(Object obj) {
		if (obj instanceof NativeJavaObject) {
			NativeJavaObject njo = (NativeJavaObject) obj;
			setData((CompositeMap) njo.unwrap());
		}
	}

	public NativeArray jsGet_children() {
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = data.getChildsNotNull();
		int length = list.size();
		NativeArray arr = ScriptUtil.newArray(this, length);
		for (int i = 0; i < length; i++) {
			CompositeMapObject m = newMap();
			m.setData(list.get(i));
			arr.put(i, arr, m);
		}
		return arr;
	}

	public String jsFunction_getPrefix() {
		return data.getPrefix();
	}

	public void jsFunction_setPrefix(String p) {
		data.setPrefix(p);
	}

	public String jsFunction_getNamespaceURI() {
		return data.getNamespaceURI();
	}

	public void jsFunction_setNameSpaceURI(String uri) {
		data.setNameSpaceURI(uri);
	}

	public NativeArray jsFunction_getChildren() {
		return jsGet_children();
	}

	public Object composite_get(Object name) {
		Object d = data.get(name);
		if (d instanceof CompositeMap) {
			CompositeMapObject c = newMap();
			c.setData((CompositeMap) d);
			return c;
		}
		return Context.javaToJS(d, getTopLevelScope(this));
	}

	public void composite_put(String name, Object value) {
		data.put(name, value);
	}

	public void jsFunction_remove(String key) {
		data.remove(key);
	}

	public Object jsFunction_get(String path) {
		return jsFunction_getObject(path);
	}

	public void jsFunction_put(String path, Object value) {
		jsFunction_putObject(path, value);
	}

	public void jsFunction_putObject(String key, Object value) {
		data.putObject(key, value, true);
	}

	public Object jsFunction_getObject(String key) {
		Object d = data.getObject(key);
		if (d instanceof CompositeMap) {
			CompositeMapObject c = newMap();
			c.setData((CompositeMap) d);
			return c;
		} else if (d instanceof java.sql.Date) {
		}
		Object obj = Context.javaToJS(d, getTopLevelScope(this));
		return obj;
	}

	public CompositeMapObject jsFunction_getChild(String name) {
		CompositeMap d = data.getChild(name);
		if (d == null)
			return null;
		CompositeMapObject c = newMap();
		c.setData(d);
		return c;
	}

	public void jsFunction_addChild(Object obj) {
		if (obj instanceof CompositeMap) {
			data.addChild((CompositeMap) obj);
		} else if (obj instanceof CompositeMapObject) {
			data.addChild(((CompositeMapObject) obj).getData());
		}
	}

	public CompositeMapObject jsFunction_createChild(String name) {
		CompositeMapObject map = newMap();
		map.setData(data.createChild(name));
		return map;
	}

	public CompositeMapObject jsFunction_createChildByTag(String path) {
		CompositeMapObject m = newMap();
		m.setData(data.createChildByTag(path));
		return m;
	}

	public void jsFunction_removeChild(CompositeMapObject m) {
		data.removeChild(m.getData());
	}

	protected CompositeMapObject newMap() {
		return (CompositeMapObject) ScriptUtil.newObject(this,
				CompositeMapObject.CLASS_NAME);
	}

	@Override
	public boolean has(String name, Scriptable start) {
		if (data.containsKey(name))
			return true;
		return super.has(name, start);
	}

	@Override
	public Object get(String name, Scriptable start) {
		Object obj = composite_get(name);
		if (ScriptUtil.isValid(obj))
			return obj;
		return super.get(name, start);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		if (!(value instanceof Callable))
			composite_put(name, value);
		if (!isSealed())
			super.put(name, start, value);
	}

	/**
	 * define special property for only this object,(NOT on prototype and its
	 * java CompositeMap data )<br/>
	 * e.g. define property for $ctx
	 * 
	 * @param name
	 * @param value
	 */
	public void definePrivateProperty(String name, Object value) {
		super.put(name, this, value);
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
	 * delegate for CompositeMap.toXML()
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
