package aurora.application.action;

import uncertain.proc.ProcedureRunner;

public class And extends MultiCheck {

	@Override
	public void run(ProcedureRunner runner) {
		for (Check ck : getCheckList()) {
			ck.run(runner);
			setResult(ck.getResult());
			if (!getResult())
				break;
		}
		writeResult(runner);
	}
}