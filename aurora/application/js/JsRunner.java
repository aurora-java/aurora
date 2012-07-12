package aurora.application.js;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

import com.sun.script.javascript.RhinoScriptEngine;

public class JsRunner {
	String exp = "";

	private CompositeMap context = null;

	public JsRunner(String sent) {
		this.exp = sent;
	}

	public JsRunner(String sent, CompositeMap context) {
		this.exp = sent;
		this.context = context;
	}

	private String convertTag() {
		if (context == null)
			return exp;
		return TextParser.parse(exp, context);
	}

	public Object run() {
		ScriptEngine engin = new RhinoScriptEngine();
		String str = convertTag();
		try {
			return engin.eval(str);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		JsRunner exp = new JsRunner("var t='f';t");
		System.out.println(exp.run());
	}
}
