package aurora.application.script.engine;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.CompositeMap;
import aurora.application.script.ScriptContext;
import aurora.application.script.ScriptEngine;
import aurora.application.script.ScriptException;
import aurora.application.script.scriptobject.ContextWraper;

public class AuroraScriptEngine extends RhinoScriptEngine {

	private CompositeMap context;

	public AuroraScriptEngine(CompositeMap context) {
		super();
		this.context = context;
	}

	private void preDefine(Context cx, Scriptable scope) {
		if (context != null) {
			ContextWraper.init_data = context;
			try {
				ScriptableObject.defineClass(scope, ContextWraper.class);
				Scriptable object = cx.newObject(scope,
						ContextWraper.class.getSimpleName());
				ScriptableObject.defineProperty(scope, "ctx", object, 0);
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

}
