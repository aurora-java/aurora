package aurora.application.action;

import javax.script.ScriptException;

import org.mozilla.javascript.JavaScriptException;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.script.engine.ScriptRunner;

public class ServerScript extends AbstractEntry {
	String exp = null;
	String resultpath = null;
	String cdata = null;
	int lineno = -1;
	private IObjectRegistry registry;

	public ServerScript(OCManager oc_manager, IObjectRegistry registry) {
		this.registry = registry;
	}

	public String getResultpath() {
		return resultpath;
	}

	public void setResultpath(String resultpath) {
		this.resultpath = resultpath;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	@Override
	public void run(ProcedureRunner runner) {
		CompositeMap context = runner.getContext();
		if (exp == null)
			exp = cdata;
		try {
			ScriptRunner sr = new ScriptRunner(exp, context, registry);
			Object res = sr.run();
			if (resultpath != null)
				context.putObject(resultpath, res, true);
		} catch (ScriptException e) {
			if (e.getCause() instanceof JavaScriptException) {
				JavaScriptException jse = (JavaScriptException) e.getCause();
				Object v = jse.getValue();
				if (v instanceof String) {
					ActionUtil.raiseApplicationError(runner, registry,
							v.toString());
					return;
				}
			}
			StringBuilder sb = new StringBuilder(500);
			sb.append("ScriptException<br/>\n");
			sb.append("source:" + source + "<br/>\n");
			sb.append("line :" + (lineno + e.getLineNumber() - 1) + "<br/>\n");
			sb.append(e.getMessage() + "<br/>\n");
			throw new RuntimeException(sb.toString());
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}

	}

	@Override
	public void beginConfigure(CompositeMap config) {
		super.beginConfigure(config);
		lineno = config.getLocationNotNull().getStartLine();
		cdata = config.getText();
		if (cdata == null)
			cdata = "";
	}
}
