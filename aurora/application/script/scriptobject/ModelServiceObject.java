package aurora.application.script.scriptobject;

import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.TextParser;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;
import aurora.application.script.engine.AuroraScriptEngine;
import aurora.database.FetchDescriptor;
import aurora.database.service.BusinessModelService;
import aurora.database.service.DatabaseServiceFactory;

public class ModelServiceObject extends ScriptableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8195408589085036558L;
	public static final String CLASS_NAME = "ModelService";
	private BusinessModelService service;
	private uncertain.composite.CompositeMap context;
	private DatabaseServiceFactory svcFactory;

	private FetchDescriptor desc = FetchDescriptor.fetchAll();

	public ModelServiceObject() {
		super();
		context = (uncertain.composite.CompositeMap) Context
				.getCurrentContext().getThreadLocal(
						AuroraScriptEngine.KEY_SERVICE_CONTEXT);
		IObjectRegistry registry = ScriptUtil.getObjectRegistry(context);
		UncertainEngine uEngine = (UncertainEngine) registry
				.getInstanceOfType(UncertainEngine.class);
		svcFactory = new DatabaseServiceFactory(uEngine);
	}

	public ModelServiceObject(String model) {
		this();
		model = TextParser.parse(model, context);
		try {
			service = svcFactory.getModelService(model, context);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Object jsGet_fetchDescriptor() {
		NativeObject no = (NativeObject) ScriptUtil.newObject(this, "Object");
		ScriptableObject.putProperty(no, "offset", desc.getOffSet());
		ScriptableObject.putProperty(no, "pagesize", desc.getPageSize());
		ScriptableObject.putProperty(no, "fetchAll", desc.getFetchAll());
		return Context.javaToJS(no, this);
	}

	public void jsSet_fetchDescriptor(Object obj) {
		if (!(obj instanceof NativeObject)) {
			desc = FetchDescriptor.fetchAll();
			return;
		}
		FetchDescriptor fd = new FetchDescriptor();
		NativeObject no = (NativeObject) obj;
		Object o = no.get("offset");
		if (ScriptUtil.isValid(o))
			fd.setOffSet(((Double) o).intValue());
		o = no.get("pagesize");
		if (ScriptUtil.isValid(o))
			fd.setPageSize(((Double) o).intValue());
		o = no.get("fetchAll");
		fd.setFetchAll(Boolean.TRUE.equals(o));
		desc = fd;
	}

	public static ModelServiceObject jsConstructor(Context cx, Object[] args,
			Function ctorObj, boolean inNewExpr) {
		if (args.length == 0 || args[0] == Context.getUndefinedValue())
			return new ModelServiceObject();
		if (args[0] instanceof String) {
			return new ModelServiceObject((String) args[0]);
		}
		return new ModelServiceObject();
	}

	private uncertain.composite.CompositeMap convert(Object obj) {
		if (obj instanceof CompositeMap) {// js CompositeMap
			return ((CompositeMap) obj).getData();
		} else if (obj instanceof uncertain.composite.CompositeMap)// uncertain
																	// CompositeMap
			return (uncertain.composite.CompositeMap) obj;
		else if (obj instanceof NativeObject) {// json object
			uncertain.composite.CompositeMap map = new uncertain.composite.CompositeMap();
			NativeObject no = (NativeObject) obj;
			for (Object o : no.keySet()) {
				if (o instanceof String) {
					map.put(o, no.get(o));
				}
			}
			return map;
		}
		return new uncertain.composite.CompositeMap();
	}

	public void jsFunction_execute(Object parameter) {
		if (!ScriptUtil.isValid(parameter))
			parameter = context.getChild("parameter");
		jsFunction_executeDml(parameter, "Execute");
	}

	public void jsFunction_insert(Object parameter) {
		if (!ScriptUtil.isValid(parameter))
			parameter = context.getChild("parameter");
		jsFunction_executeDml(parameter, "Insert");
	}

	public void jsFunction_updateByPK(Object parameter) {
		if (!ScriptUtil.isValid(parameter))
			parameter = context.getChild("parameter");
		jsFunction_executeDml(parameter, "Update");
	}

	public void jsFunction_deleteByPK(Object parameter) {
		if (!ScriptUtil.isValid(parameter))
			parameter = context.getChild("parameter");
		jsFunction_executeDml(parameter, "Delete");
	}

	public CompositeMap jsFunction_queryAsMap(Object parameter) {
		if (!ScriptUtil.isValid(parameter))
			parameter = context.getChild("parameter");
		try {
			uncertain.composite.CompositeMap data = service.queryAsMap(
					convert(parameter), desc);
			CompositeMap map = (CompositeMap) ScriptUtil.newObject(this,
					"CompositeMap");
			map.setData(data);
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param operation
	 *            Update,Insert,Execute,Delete
	 */
	public void jsFunction_executeDml(Object parameter, String operation) {
		try {
			if (parameter == null || parameter == Context.getUndefinedValue())
				parameter = context.getChild("parameter");
			service.executeDml(convert(parameter), operation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
}
