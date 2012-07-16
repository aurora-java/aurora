package aurora.application.script.engine;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import aurora.application.script.ScriptEngine;
import aurora.application.script.ScriptException;

public class ScriptRunner {
	private String exp;
	private ScriptEngine engine;

	private CompositeMap context = null;

	public ScriptRunner(String script) {
		this.exp = script;
	}

	public ScriptRunner(String script, CompositeMap context) {
		this.exp = script;
		this.context = context;
	}

	public String getOriginalScript() {
		return exp;
	}

	public String getParsedScript() {
		if (context == null)
			return exp;
		return TextParser.parse(exp, context);
	}

	public Object run() throws ScriptException {
		if (engine == null)
			engine = new AuroraScriptEngine(context);
		String str = getParsedScript();
		return engine.eval(str);
	}
}
