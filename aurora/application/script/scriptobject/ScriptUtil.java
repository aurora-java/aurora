package aurora.application.script.scriptobject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

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
}
