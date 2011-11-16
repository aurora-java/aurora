package aurora.service;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class SetParameterParsed extends AbstractEntry {
	private boolean value = false;
	public static String PARAMETER_PARSED_FIELD="/parameter/@__parameter_parsed__";
	@Override
	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		context.putObject(PARAMETER_PARSED_FIELD, TextParser.parse(String.valueOf(value),context), true);
	}
	
	public boolean getValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}

	
}
