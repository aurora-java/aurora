package aurora.application.features.transform;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.transform.AbstractTransform;
import uncertain.proc.ProcedureRunner;

/**
 * * <code>
 *     	<map-transform source="/model/data" target="/model/target" key-field="KEY_FIELD"/>
 *   </code>
 */
public class MapTransformerElement extends AbstractTransform {
	public static final String KEY_FIELD = "key-field";
	final String KEY_SOURCE = "source";
	final String KEY_TARGET = "target";

	CompositeMap elementConifg;

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		// TODO Auto-generated method stub
		String source = elementConifg.getString(this.KEY_SOURCE);
		assert source != null;
		String target = elementConifg.getString(this.KEY_TARGET);
		assert target != null;

		CompositeMap context = runner.getContext();
		CompositeMap sourceMap = (CompositeMap) context.getObject(source);

		CompositeMap targetMap = (CompositeMap) sourceMap.clone();
		context.putObject(target, targetMap, true);
		this.transform(targetMap, this.elementConifg);

	}

	@Override
	public void beginConfigure(CompositeMap config) {
		this.elementConifg = config;
	}

	public CompositeMap transform(CompositeMap source,
			CompositeMap transform_config) {

		if (source == null || transform_config == null)
			return null;

		Iterator it = source.getChildIterator();
		if (it == null)
			return null;
		Object key_field_name = transform_config.get(KEY_FIELD);

		while (it.hasNext()) {
			CompositeMap child = (CompositeMap) it.next();
			Object key_field_value = child.get(key_field_name);
			if (key_field_value != null)
				source.put(key_field_value.toString(), child);
		}

		source.getChilds().clear();
		return source;

	}

}
