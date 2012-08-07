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

import aurora.application.script.scriptobject.ActionEntryObject;
import aurora.application.script.scriptobject.CompositeMapObject;
import aurora.application.script.scriptobject.CookieObject;
import aurora.application.script.scriptobject.ModelServiceObject;
import aurora.application.script.scriptobject.ScriptUtil;
import aurora.application.script.scriptobject.SessionObject;

public class AuroraScriptEngine extends RhinoScriptEngine {
	public static final String aurora_core_js = "aurora-core.js";
	public static final String KEY_SERVICE_CONTEXT = "service_context";
	public static final String KEY_SSO = "sso";
	private static String js = ScriptUtil.loadAuroraCore();
	private Scriptable scope = null;

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
			// cx.putThreadLocal(KEY_SERVICE_CONTEXT, service_context);
			ScriptableObject.defineClass(scope, CompositeMapObject.class);
			ScriptableObject.defineClass(scope, SessionObject.class);
			ScriptableObject.defineClass(scope, CookieObject.class);
			ScriptableObject.defineClass(scope, ModelServiceObject.class);
			ScriptableObject.defineClass(scope, ActionEntryObject.class);
			// ScriptableObject.defineClass(scope, ContextObject.class);
			Scriptable ctx = cx.newObject(scope, CompositeMapObject.CLASS_NAME,
					new Object[] { service_context });
			ScriptableObject.defineProperty(scope, "$ctx", ctx,
					ScriptableObject.READONLY);
			// define property for $ctx
			definePropertyForCtx(ctx.getPrototype(), cx, service_context);

			Scriptable ses = cx.newObject(scope, SessionObject.CLASS_NAME);
			ScriptableObject.defineProperty(scope, "$session", ses,
					ScriptableObject.READONLY);
			Scriptable cok = cx.newObject(scope, CookieObject.CLASS_NAME);
			ScriptableObject.defineProperty(scope, "$cookie", cok,
					ScriptableObject.READONLY);

			cx.evaluateString(scope, js, aurora_core_js, 1, null);
			// seal all builtin objects,so user can not modify them
			for (Object o : new Object[] { ctx, ses, cok }) {
				if (o instanceof ScriptableObject) {
					((ScriptableObject) o).sealObject();
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void definePropertyForCtx(Scriptable ctxp, Context cx,
			uncertain.composite.CompositeMap service_context) {
		String[] names = { "parameter", "session", "cookie", "model" };
		for (String s : names) {
			Object p = service_context.getChild(s);
			if (p == null)
				p = service_context.createChild(s);
			ScriptableObject.defineProperty(ctxp.getPrototype(), s, cx
					.newObject(ctxp, CompositeMapObject.CLASS_NAME,
							new Object[] { p }), ScriptableObject.READONLY);
		}
	}

	@Override
	public Object eval(Reader reader, ScriptContext ctxt)
			throws ScriptException {
		Object ret;
		Context cx = enterContext();
		cx.putThreadLocal(KEY_SERVICE_CONTEXT, service_context);
		try {
			if (scope == null) {
				scope = getRuntimeScope(ctxt);
				preDefine(cx, scope);
			}
			ScriptImportor.organizeUserImport(cx, scope, service_context);
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

	protected Scriptable getRuntimeScope(ScriptContext ctxt) {
		Scriptable scope = super.getRuntimeScope(ctxt);
		Context cx = Context.enter();
		Scriptable rScope = cx.newObject(scope);
		rScope.setParentScope(null);
		rScope.setPrototype(scope);
		Context.exit();
		return rScope;
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
