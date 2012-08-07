package aurora.application.script.scriptobject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.application.script.engine.AuroraScriptEngine;

public class ScriptUtil {
	public static Scriptable newObject(Scriptable scope, String clsName) {
		Context ctx = Context.getCurrentContext();
		Scriptable topScope = ScriptableObject.getTopLevelScope(scope);
		return ctx.newObject(topScope, clsName);
	}

	public static NativeArray newArray(Scriptable scope, int length) {
		Context ctx = Context.getCurrentContext();
		Scriptable topScope = ScriptableObject.getTopLevelScope(scope);
		return (NativeArray) ctx.newArray(topScope, length);
	}

	public static AuroraScriptEngine getEngine(CompositeMap context) {
		ScriptShareObject sso = (ScriptShareObject) context
				.get(AuroraScriptEngine.KEY_SSO);
		if (sso == null)
			return null;
		return sso.getEngine();
	}

	public static IObjectRegistry getObjectRegistry(CompositeMap context) {
		ScriptShareObject sso = (ScriptShareObject) context
				.get(AuroraScriptEngine.KEY_SSO);
		if (sso == null)
			return null;
		return sso.getObjectRegistry();
	}

	public static CompositeMap getContext() {
		return (CompositeMap) Context.getCurrentContext().getThreadLocal(
				AuroraScriptEngine.KEY_SERVICE_CONTEXT);
	}

	public static ProcedureRunner getProcedureRunner() {
		CompositeMap ctx = getContext();
		ScriptShareObject sso = (ScriptShareObject) ctx
				.get(AuroraScriptEngine.KEY_SSO);
		ProcedureRunner runner = sso.get(ScriptShareObject.KEY_RUNNER);
		if (runner == null) {
			runner = new ProcedureRunner();
			runner.setContext(ctx);
		}
		return runner;
	}

	public static boolean isValid(Object obj) {
		return !(obj == null || obj == Context.getUndefinedValue() || obj == Scriptable.NOT_FOUND);
	}

	public synchronized static final String loadAuroraCore() {
		try {
			InputStream is = AuroraScriptEngine.class
					.getResourceAsStream(AuroraScriptEngine.aurora_core_js);
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
				return sb.toString();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return "";
	}

}
