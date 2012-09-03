package aurora.application.script.engine;

import java.io.File;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import uncertain.composite.CompositeMap;
import aurora.application.script.scriptobject.ScriptShareObject;
import aurora.application.sourcecode.SourceCodeUtil;

public class ScriptImportor {
	private static final String abs_start = "/";
	private static final String std_path = "WEB-INF/server-script";

	public static void organizeUserImport(Context cx, Scriptable scope,
			CompositeMap context) {
		ScriptShareObject sso = (ScriptShareObject) context
				.get(AuroraScriptEngine.KEY_SSO);
		if (sso == null)
			return;
		String str = (String) sso.get(ScriptShareObject.KEY_IMPORT);
		if (str == null || str.trim().length() == 0)
			return;
		String[] jss = str.split(";");
		if (jss.length == 0)
			return;
		File webHome = SourceCodeUtil.getWebHome(sso.getObjectRegistry());
		File jsFile = null;
		for (String js : jss) {
			js = js.trim();
			if (js.startsWith(abs_start)) {
				jsFile = new File(webHome, std_path + js);
			} else {
				File cfile = new File(webHome,
						context.getString("service_name"));
				jsFile = new File(cfile.getParentFile(), js);
			}
			addImport(cx, scope, jsFile);
		}
	}

	private static void addImport(Context cx, Scriptable scope, File jsFile) {
		if (jsFile == null || !jsFile.exists() || !jsFile.isFile())
			return;
		Script script = CompiledScriptCache.getInstance().getScript(jsFile, cx);
		if (script != null)
			script.exec(cx, scope);
	}
}
