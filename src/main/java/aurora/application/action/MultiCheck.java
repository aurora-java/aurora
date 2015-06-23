package aurora.application.action;

import java.util.ArrayList;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IChildContainerAcceptable;
import uncertain.proc.ProcedureRunner;

public abstract class MultiCheck extends Check implements
		IChildContainerAcceptable {
	String resultpath;
	ArrayList<Check> checkList = new ArrayList<Check>();

	@Override
	public void run(ProcedureRunner runner) {
	}

	public ArrayList<Check> getCheckList() {
		return checkList;
	}

	protected void writeResult(ProcedureRunner runner) {
		if (resultpath == null)
			return;
		runner.getContext().putObject(resultpath, result, true);
	}

	public void addCheck(Check check) {
		checkList.add(check);
		check.setOwner(this);
	}

	public void addAnd(And and) {
		addCheck(and);
	}

	public void addOr(Or or) {
		addCheck(or);
	}

	@Override
	public void addChild(CompositeMap child) {

	}

	public String getResultpath() {
		return resultpath;
	}

	public void setResultpath(String resultpath) {
		this.resultpath = resultpath;
	}
}
