package aurora.application.script.engine;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import aurora.application.script.scriptobject.CompositeMap;
import aurora.application.script.scriptobject.CookieObject;
import aurora.application.script.scriptobject.ModelServiceObject;
import aurora.application.script.scriptobject.ScriptUtil;
import aurora.application.script.scriptobject.SessionObject;

public class AuroraScriptEngine extends RhinoScriptEngine {
	public static final String aurora_core_js = "aurora-core.js";
	public static final String KEY_SERVICE_CONTEXT = "service_context";
	public static final String KEY_SSO = "sso";
	private static String js = ScriptUtil.loadAuroraCore();

	private uncertain.composite.CompositeMap service_context;

	public AuroraScriptEngine(uncertain.composite.CompositeMap context) {
		super();
		if (context == null)
			throw new NullPointerException(
					"init context for 'AuroraScriptEngine' can not be null.");
		this.service_context = context;
	}

	private void preDefine(Context cx, Scriptable scope) {
		try {
			cx.putThreadLocal(KEY_SERVICE_CONTEXT, service_context);
			ScriptableObject.defineClass(scope, CompositeMap.class);
			// ScriptableObject.defineClass(scope, ContextObject.class);
			Scriptable object = cx.newObject(scope, CompositeMap.CLASS_NAME,
					new Object[] { service_context });
			ScriptableObject.defineProperty(scope, "$ctx", object, 0);
			ScriptableObject.defineClass(scope, SessionObject.class);
			object = cx.newObject(scope, SessionObject.CLASS_NAME);
			ScriptableObject.defineProperty(scope, "$session", object, 0);
			ScriptableObject.defineClass(scope, CookieObject.class);
			object = cx.newObject(scope, CookieObject.CLASS_NAME);
			ScriptableObject.defineProperty(scope, "$cookie", object, 0);

			ScriptableObject.defineClass(scope, ModelServiceObject.class);

			cx.evaluateString(scope, js, aurora_core_js, 1, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object eval(Reader reader, ScriptContext ctxt)
			throws ScriptException {
		Object ret;

		Context cx = enterContext();
		try {
			Scriptable scope = getRuntimeScope(ctxt);
			preDefine(cx, scope);
			String filename = (String) get(ScriptEngine.FILENAME);
			filename = filename == null ? "<Unknown source>" : filename;

			ret = cx.evaluateReader(scope, reader, filename, 1, null);
		} catch (RhinoException re) {
			if (DEBUG)
				re.printStackTrace();
			int line = (line = re.lineNumber()) == 0 ? -1 : line;
			String msg;
			if (re instanceof JavaScriptException) {
				msg = String.valueOf(((JavaScriptException) re).getValue());
			} else {
				msg = re.toString();
			}
			msg = formatExceptionMessage(msg);
			ScriptException se = new ScriptException(msg, re.sourceName(), line);
			se.initCause(re);
			throw se;
		} catch (IOException ee) {
			throw new ScriptException(ee);
		} finally {
			Context.exit();
		}

		return unwrapReturnValue(ret);
	}

	private String formatExceptionMessage(String msg) {
		int idx = msg.indexOf(':');
		if (idx == -1)
			return msg;
		String s = msg.substring(0, idx);
		if (s.indexOf("mozilla") != -1)
			return msg.substring(idx + 1);
		return msg;
	}

}
