package aurora.application.script.scriptobject;

import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import uncertain.composite.TextParser;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;
import aurora.database.FetchDescriptor;
import aurora.database.rsconsumer.CompositeMapCreator;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.ServiceOption;
import aurora.database.service.SqlServiceContext;

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
	private BusinessModelServiceContext serviceContext;

	public ModelServiceObject() {
		super();
		context = ScriptUtil.getContext();
		IObjectRegistry registry = ScriptUtil.getObjectRegistry(context);
		if (registry != null) {
			UncertainEngine uEngine = (UncertainEngine) registry
					.getInstanceOfType(UncertainEngine.class);
			svcFactory = new DatabaseServiceFactory(uEngine);
		}
	}

	public ModelServiceObject(String model) {
		this();
		model = TextParser.parse(model, context);
		try {
			service = svcFactory.getModelService(model, context);
			serviceContext = service.getServiceContext();
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
		jsFunction_executeDml(parameter, "Execute");
	}

	public void jsFunction_insert(Object parameter) {
		jsFunction_executeDml(parameter, "Insert");
	}

	public void jsFunction_update(Object parameter) {
		jsFunction_executeDml(parameter, "Update");
	}

	public void jsFunction_delete(Object parameter) {
		jsFunction_executeDml(parameter, "Delete");
	}

	public CompositeMap jsFunction_queryAsMap(Object parameter) {
		if (!ScriptUtil.isValid(parameter))
			parameter = context.getChild("parameter");
		try {
			uncertain.composite.CompositeMap data = service.queryAsMap(
					convert(parameter), desc);
			CompositeMap map = (CompositeMap) ScriptUtil.newObject(this,
					CompositeMap.CLASS_NAME);
			map.setData(data);
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			jsSet_option(null);
		}
	}

	public CompositeMap jsFunction_queryIntoMap(CompositeMap root,
			Object parameter) {
		if (!(root instanceof CompositeMap))
			throw new RuntimeException("invalid root");
		if (!ScriptUtil.isValid(parameter))
			parameter = context.getChild("parameter");
		try {
			service.queryIntoMap(convert(parameter), desc, root.getData());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			jsSet_option(null);
		}
		return root;
	}

	public void jsFunction_query() {
		try {
			ServiceOption so = (ServiceOption) context
					.get(SqlServiceContext.KEY_SERVICE_OPTION);
			if (so != null) {
				String path = so.getString("rootPath");
				uncertain.composite.CompositeMap root = getMapFromRootPath(path);
				CompositeMapCreator cmc = new CompositeMapCreator(root);
				serviceContext.setResultsetConsumer(cmc);
			}
			service.query();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			jsSet_option(null);
		}
	}

	private uncertain.composite.CompositeMap getMapFromRootPath(String rootPath) {
		uncertain.composite.CompositeMap model = context.getChild("model");
		if (model == null)
			model = context.createChild("model");
		uncertain.composite.CompositeMap root = (uncertain.composite.CompositeMap) model
				.getObject(rootPath);
		if (root == null)
			root = model.createChildByTag(rootPath);
		return root;
	}

	/**
	 * 
	 * @param operation
	 *            Update,Insert,Execute,Delete
	 */
	public void jsFunction_executeDml(Object parameter, String operation) {
		try {
			if (!ScriptUtil.isValid(parameter))
				parameter = context.getChild("parameter");
			service.executeDml(convert(parameter), operation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			jsSet_option(null);
		}
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	public Object jsGet_option() {
		ServiceOption so = (ServiceOption) context
				.get(SqlServiceContext.KEY_SERVICE_OPTION);
		if (so == null)
			return null;
		uncertain.composite.CompositeMap map = so.getObjectContext();
		NativeObject no = (NativeObject) ScriptUtil.newObject(this, "Object");
		for (Object o : map.keySet()) {
			if (o instanceof String)
				ScriptableObject.putProperty(no, (String) o, map.get(o));
		}
		return no;
	}

	public void jsSet_option(Object obj) {
		if (!(obj instanceof NativeObject)) {
			context.put(SqlServiceContext.KEY_SERVICE_OPTION, null);
			return;
		}

		NativeObject no = (NativeObject) obj;
		ServiceOption so = ServiceOption.createInstance();
		for (Object o : no.keySet()) {
			if (o instanceof String) {
				// The original key and lowcase key put the same value
				// To avoid some CASE problem
				so.put(o, no.get(o));
				so.put(o.toString().toLowerCase(), no.get(o));
			}
		}
		context.put(SqlServiceContext.KEY_SERVICE_OPTION, so);
	}
}
