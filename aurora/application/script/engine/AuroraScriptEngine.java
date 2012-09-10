package aurora.application.script.engine;

import java.lang.reflect.InvocationTargetException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.mbean.MBeanRegister;
import uncertain.ocm.IObjectRegistry;
import aurora.application.script.scriptobject.ActionEntryObject;
import aurora.application.script.scriptobject.CompositeMapObject;
import aurora.application.script.scriptobject.CookieObject;
import aurora.application.script.scriptobject.ModelServiceObject;
import aurora.application.script.scriptobject.ScriptShareObject;
import aurora.application.script.scriptobject.ScriptUtil;
import aurora.application.script.scriptobject.SessionObject;
import aurora.service.ServiceInstance;

public class AuroraScriptEngine /* extends RhinoScriptEngine */{
	public static final String aurora_core_js = "aurora-core.js";
	public static final String KEY_SERVICE_CONTEXT = "service_context";
	public static final String KEY_SSO = "sso";
	private static boolean mbeanregistered = false;
	private static String js = ScriptUtil.loadAuroraCore();
	private static TopLevel topLevel = null;
	private Scriptable scope = null;
	static {
		RhinoException.useMozillaStackStyle(false);
		initTopLevel(Context.enter());
		Context.exit();
		ContextFactory.initGlobal(new ContextFactory() {
			protected Context makeContext() {
				Context cx = super.makeContext();
				cx.setLanguageVersion(Context.VERSION_1_8);
				cx.setOptimizationLevel(-1);
				cx.setClassShutter(RhinoClassShutter.getInstance());
				cx.setWrapFactory(RhinoWrapFactory.getInstance());
				return cx;
			}
		});
	}

	private CompositeMap service_context;
	private int optimizeLevel = -1;

	public AuroraScriptEngine(CompositeMap context) {
		super();
		if (context == null)
			throw new NullPointerException(
					"init context for 'AuroraScriptEngine' can not be null.");
		this.service_context = context;
		if (!mbeanregistered) {
			mbeanregistered = true;
			IObjectRegistry or = ((ScriptShareObject) service_context
					.get(KEY_SSO)).getObjectRegistry();
			UncertainEngine engine = (UncertainEngine) or
					.getInstanceOfType(UncertainEngine.class);
			String mbean_name = engine.getMBeanName("cache", "name=script");
			MBeanRegister.resiterMBean(mbean_name,
					CompiledScriptCache.getInstance());
		}
	}

	private void preDefine(Context cx, Scriptable scope) {
		Scriptable ctx = cx.newObject(scope, CompositeMapObject.CLASS_NAME,
				new Object[] { service_context });
		ScriptableObject.defineProperty(scope, "$ctx", ctx,
				ScriptableObject.READONLY);
		// define property for $ctx
		definePropertyForCtx((CompositeMapObject) ctx, cx, service_context);

		Scriptable ses = cx.newObject(scope, SessionObject.CLASS_NAME,
				new Object[] { service_context });
		ScriptableObject.defineProperty(scope, "$session", ses,
				ScriptableObject.READONLY);
		Scriptable cok = cx.newObject(scope, CookieObject.CLASS_NAME);
		ScriptableObject.defineProperty(scope, "$cookie", cok,
				ScriptableObject.READONLY);
	}

	private void definePropertyForCtx(CompositeMapObject ctx, Context cx,
			CompositeMap service_context) {
		String[] names = { "parameter", "session", "cookie", "model" };
		for (String s : names) {
			Object p = service_context.getChild(s);
			if (p == null)
				p = service_context.createChild(s);
			ctx.definePrivateProperty(s, cx.newObject(ctx,
					CompositeMapObject.CLASS_NAME, new Object[] { p }));
		}
	}

	private static void initTopLevel(Context cx) {
		topLevel = new ImporterTopLevel(cx);
		try {
			ScriptableObject.defineClass(topLevel, CompositeMapObject.class);
			ScriptableObject.defineClass(topLevel, SessionObject.class);
			ScriptableObject.defineClass(topLevel, CookieObject.class);
			ScriptableObject.defineClass(topLevel, ModelServiceObject.class);
			ScriptableObject.defineClass(topLevel, ActionEntryObject.class);
			topLevel.defineFunctionProperties(new String[] { "print",
					"println", "raise_app_error", "$instance", "$cache",
					"$config", "$bm" }, AuroraScriptEngine.class,
					ScriptableObject.DONTENUM);
			Script scr = CompiledScriptCache.getInstance().getScript(js, cx,
					aurora_core_js);
			if (scr != null)
				scr.exec(cx, topLevel);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public Object eval(String source) throws Exception {
		Object ret = null;
		Context cx = Context.enter();
		try {
			cx.putThreadLocal(KEY_SERVICE_CONTEXT, service_context);
			cx.setOptimizationLevel(optimizeLevel);
			if (scope == null) {
				scope = cx.newObject(topLevel);
				scope.setParentScope(null);
				scope.setPrototype(topLevel);
				preDefine(cx, scope);
			}
			ScriptImportor.organizeUserImport(cx, scope, service_context);
			Script scr = CompiledScriptCache.getInstance()
					.getScript(source, cx);
			ret = scr == null ? null : scr.exec(cx, scope);
		} catch (RhinoException re) {
			if (re.getCause() instanceof InterruptException)
				throw (InterruptException) re.getCause();
			throw re;
		} finally {
			Context.exit();
		}

		if (ret instanceof Wrapper) {
			ret = ((Wrapper) ret).unwrap();
		} else if (ret instanceof Undefined)
			ret = null;
		return ret;
	}

	public static void print(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		for (int i = 0; i < args.length; i++) {
			if (i > 0)
				System.out.print(" ");
			// Convert the arbitrary JavaScript value into a string form.
			String s = Context.toString(args[i]);
			System.out.print(s);
		}
	}

	public static void println(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		print(cx, thisObj, args, funObj);
		System.out.println();
	}

	public static void raise_app_error(String err_code)
			throws InterruptException {
		throw new InterruptException(err_code);
	}

	public static Object $instance(String className) {
		return ScriptUtil.getInstanceOfType(className);
	}

	public static Object $cache(String cacheName) {
		CompositeMap ctx = ScriptUtil.getContext();
		IObjectRegistry reg = ScriptUtil.getObjectRegistry(ctx);
		INamedCacheFactory cf = (INamedCacheFactory) reg
				.getInstanceOfType(INamedCacheFactory.class);
		return cf.getNamedCache(cacheName);
	}

	public static Object $config(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		ServiceInstance si = ServiceInstance.getInstance(ScriptUtil
				.getContext());
		Script scr = CompiledScriptCache.getInstance().getScript(
				"importClass(Packages.uncertain.composite.CompositeUtil)", cx,
				"<Import CompositeUtil>");
		if (scr != null)
			scr.exec(cx, thisObj);
		return si.getServiceConfigData();
	}

	public static Object $bm(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		ModelServiceObject bm = (ModelServiceObject) cx.newObject(thisObj,
				ModelServiceObject.CLASS_NAME, args);
		if (args.length > 1)
			bm.jsSet_option(args[1]);
		return bm;
	}

	public void setOptimizeLevel(int level) {
		optimizeLevel = level;
	}
}
