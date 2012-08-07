package aurora.application.script.scriptobject;

import java.util.HashMap;

import org.mozilla.javascript.ScriptableObject;

import uncertain.ocm.IObjectRegistry;
import aurora.application.script.engine.AuroraScriptEngine;

public class ScriptShareObject extends ScriptableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2510382181951321090L;

	public static final String KEY_ENGINE = "aurora-script-engine";
	public static final String KEY_REGISTRY = "iobject-registry";
	public static final String KEY_IMPORT = "import";
	public static final String KEY_RUNNER = "procedure-runner";
	private HashMap<String, Object> map = new HashMap<String, Object>();

	@Override
	public String getClassName() {
		return getClass().getSimpleName();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		Object o = map.get(key);
		return (T) o;
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

	public AuroraScriptEngine getEngine() {
		return get(KEY_ENGINE);
	}

	public void put(AuroraScriptEngine engine) {
		put(KEY_ENGINE, engine);
	}

	public IObjectRegistry getObjectRegistry() {
		return get(KEY_REGISTRY);
	}

	public void put(IObjectRegistry or) {
		put(KEY_REGISTRY, or);
	}

	public boolean has(String key) {
		return map.get(key) != null;
	}

	public Object clone() {
		return this;
	}

	public boolean equals(Object o) {
		return o == this;
	}

	public String toString() {
		return "(" + getClassName() + " : " + map.size() + " elements)";
	}

	public String jsFunction_toString() {
		return toString();
	}

}
