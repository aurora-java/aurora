package aurora.application.action;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.application.script.engine.ScriptRunner;

public class Script extends AbstractEntry {
	String exp = "";
	String resultpath = null;
	String debug = "false";

	public String getResultpath() {
		return resultpath;
	}

	public void setResultpath(String resultpath) {
		this.resultpath = resultpath;
	}

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
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
		ScriptRunner sr = new ScriptRunner(exp, context);
		try {
			Object res = sr.run();
			if (resultpath != null)
				context.putObject(resultpath, res, true);
			if (Boolean.parseBoolean(debug)) {
				System.out.println("original script:" + sr.getOriginalScript());
				System.out.println("parsed script:" + sr.getParsedScript());
				System.out.println("result:" + res);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
