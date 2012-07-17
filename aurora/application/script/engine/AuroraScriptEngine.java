package aurora.application.script.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import aurora.application.script.ScriptContext;
import aurora.application.script.ScriptEngine;
import aurora.application.script.ScriptException;
import aurora.application.script.scriptobject.CompositeMap;

public class AuroraScriptEngine extends RhinoScriptEngine {
	public static final String aurora_core_js = "aurora-core.js";
	private static String js = "";
	static {
		try {
			InputStream is = AuroraScriptEngine.class
					.getResourceAsStream(aurora_core_js);
			if (is != null) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder(1024);
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
				is.close();
				br.close();
				js = sb.toString();
				sb = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private uncertain.composite.CompositeMap service_context;

	public AuroraScriptEngine(uncertain.composite.CompositeMap context) {
		super();
		this.service_context = context;
	}

	private void preDefine(Context cx, Scriptable scope) {
		if (service_context != null) {
			try {
				ScriptableObject.defineClass(scope, CompositeMap.class);
				Scriptable object = cx.newObject(scope,
						CompositeMap.class.getSimpleName(),
						new Object[] { service_context });
				ScriptableObject.defineProperty(scope, "ctx", object, 0);
				cx.evaluateString(scope, js, aurora_core_js, 1, null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
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
