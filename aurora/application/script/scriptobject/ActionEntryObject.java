package aurora.application.script.scriptobject;

import java.lang.reflect.Method;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class ActionEntryObject extends ScriptableObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2709907538259764391L;
	public static final String CLASS_NAME = "ActionEntry";
	private String uri;
	private String name;

	public ActionEntryObject() {
		super();
	}

	public ActionEntryObject(String uri, String name) {
		this();
		this.uri = uri;
		this.name = name;
	}

	public static ActionEntryObject jsConstructor(Context cx, Object[] args,
			Function ctorObj, boolean inNewExpr) {
		if (args.length == 0 || args[0] == Context.getUndefinedValue())
			return new ActionEntryObject();
		if (args.length == 1) {
			return new ActionEntryObject("uncertain.proc", (String) args[0]);
		}
		if (args.length == 2)
			return new ActionEntryObject((String) args[0], (String) args[1]);
		return new ActionEntryObject();
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	public void jsFunction_run(Object param) {
		IObjectRegistry ior = ScriptUtil.getObjectRegistry(ScriptUtil
				.getContext());
		ClassRegistry cr = (ClassRegistry) ior
				.getInstanceOfType(ClassRegistry.class);
		String className = cr.getClassName(new CompositeMap("", uri, name));
		if (className == null)
			throw new RuntimeException("Can not find class for '" + name
					+ "' in '" + uri + "'.");
		AbstractEntry entry = null;
		try {
			entry = (AbstractEntry) ((ObjectRegistryImpl) ior)
					.createInstanceSilently(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		if (entry == null)
			return;
		try {
			if (param instanceof NativeObject)
				autoAssignParameter(entry, (NativeObject) param);
			ProcedureRunner runner = ScriptUtil.getProcedureRunner();
			entry.run(runner);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void autoAssignParameter(Object obj, NativeObject no)
			throws Exception {
		Class<?> cls = obj.getClass();
		StringBuilder sb = new StringBuilder();
		for (Object key : no.keySet()) {
			if (key instanceof String) {
				sb.delete(0, sb.length());
				sb.append(key);
				sb.setCharAt(0, Character.toTitleCase(sb.charAt(0)));
				Method m = null;
				try {
					m = cls.getMethod("set" + sb.toString(), String.class);
				} catch (Exception e) {
					continue;
				}
				m.invoke(obj, "" + no.get(key));
			}
		}
	}
}
