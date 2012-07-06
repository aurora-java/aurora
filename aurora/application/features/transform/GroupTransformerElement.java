package aurora.application.features.transform;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.transform.AbstractTransform;
import uncertain.composite.transform.CompositeTransformer;
import uncertain.composite.transform.GroupConfig;
import uncertain.composite.transform.GroupTransformer;
import uncertain.composite.transform.Transformer;
import uncertain.proc.ProcedureRunner;

/**
 * Group child CompositeMap on specified field <code>
 *     	<group-transform source="/model/data" target="/model/target" group-name="modular" sub-group-name="services" group-field="MODULAR_ID">
 *   		<group-field name="MODULAR_NAME" remove-field="true"/>
 *   		<group-field name="MODULAR_ID" remove-field="true" />
 *   	</group-transform>
 *   </code> or <code>
 *     	<group-transform source="/model/data" target="/model/target" group-config="/model/config"/>
 *   </code>
 */
public class GroupTransformerElement extends AbstractTransform {
	public static final String KEY_GROUP_NAME = "group-name";
	public static final String KEY_SUB_GROUP_NAME = "sub-group-name";
	public static final String KEY_GROUP_FIELD_NAME = "name";
	public static final String KEY_REMOVE_FIELD = "remove-field";
	public static final String KEY_GROUP_FIELD = "group-field";
	final String KEY_GROUP_CONFIG = "group-config";
	final String KEY_SOURCE = "source";
	final String KEY_TARGET = "target";
	CompositeMap elementConifg;

	public static CompositeMap createGroupTransform(String group_field,
			String group_name) {
		CompositeMap config = Transformer
				.createTransformConfig(GroupTransformer.class.getName());
		config.setName(CompositeTransformer.KEY_TRANSFORM);
		config.put(KEY_GROUP_NAME, group_name);
		config.put(KEY_GROUP_FIELD, group_field);
		return config;
	}

	public static CompositeMap addGroupField(CompositeMap config,
			String field_name, boolean remove) {
		CompositeMap field = config.createChild(KEY_GROUP_FIELD);
		field.put(KEY_GROUP_FIELD_NAME, field_name);
		field.putBoolean(KEY_REMOVE_FIELD, remove);
		return field;
	}

	public CompositeMap transform(CompositeMap source,
			CompositeMap transform_config) {
		if (source == null || transform_config == null)
			return null;
		boolean has_group_fields = transform_config.getChilds() != null;

		String group_field = transform_config.getString(KEY_GROUP_FIELD);
		if (group_field == null)
			return source;

		String sub_group_name = transform_config.getString(KEY_SUB_GROUP_NAME);

		Iterator childs = source.getChildIterator();
		if (childs == null)
			return source;

		String group_name = transform_config.getString(KEY_GROUP_NAME, "group");
		// group-field-value -> group items
		Map groups = new LinkedHashMap();

		while (childs.hasNext()) {
			CompositeMap item = (CompositeMap) childs.next();
			Object value = item.get(group_field);
			CompositeMap group_item = (CompositeMap) groups.get(value);
			if (group_item == null) {
				group_item = new CompositeMap(group_name);
				// put group fields into group_item
				if (has_group_fields) {
					Iterator gfields = transform_config.getChildIterator();
					while (gfields.hasNext()) {
						CompositeMap gf = (CompositeMap) gfields.next();
						Object gf_name = gf.get(KEY_GROUP_FIELD_NAME);
						group_item.put(gf_name, item.get(gf_name));
					}
				}
				groups.put(value, group_item);
			}
			if (sub_group_name != null) {
				CompositeMap sub_group = group_item.getChild(sub_group_name);
				if (sub_group == null)
					sub_group = group_item.createChild(sub_group_name);
				sub_group.addChild(item);
			} else
				group_item.addChild(item);
		}

		source.getChilds().clear();
		source.addChilds(groups.values());

		return source;
	}

	public static CompositeMap transform(CompositeMap source, GroupConfig config) {
		if (source == null || config == null)
			return source;
		Iterator childs = source.getChildIterator();
		if (childs == null)
			return source;
		Map groups = new LinkedHashMap();
		for (; childs.hasNext();) {
			CompositeMap child = (CompositeMap) childs.next();
			StringBuffer key = new StringBuffer("<");
			String[] groupFields = config.getGroupKeyFields();
			for (int i = 0; i < groupFields.length; i++) {
				key.append(" ").append(groupFields[i]).append("=\"")
						.append(child.getString(groupFields[i]) + "\"");
			}
			key.append(">");
			CompositeMap group_item = (CompositeMap) groups.get(key.toString());
			if (group_item == null) {
				if (config.isExtendParentAttributes()) {
					group_item = (CompositeMap) source.clone();
					group_item.getChilds().clear();
					group_item.setName(config.getRecordName());
				} else {
					group_item = new CompositeMap(config.getRecordName());
				}
				for (int i = 0; i < groupFields.length; i++) {
					group_item.put(groupFields[i], child.get(groupFields[i]));
				}
				String[] attributes = config.getGroupAttributes();
				if (attributes != null) {
					for (int i = 0; i < attributes.length; i++) {
						group_item.put(attributes[i], child.get(attributes[i]));
					}
				}
				groups.put(key.toString(), group_item);
			}
			group_item.addChild(child);
		}
		source.getChilds().clear();
		source.addChilds(groups.values());
		return source;
	}

	public static CompositeMap transform(CompositeMap source,
			GroupConfig config, int childLevel) {
		List result = new LinkedList();
		CompositeUtil.getLevelChilds(source, childLevel, result);
		if (result == null || result.isEmpty())
			return null;
		for (Iterator it = result.iterator(); it.hasNext();) {
			transform((CompositeMap) it.next(), config);
		}
		return source;
	}

	public static CompositeMap transform(CompositeMap source,
			GroupConfig[] configs) {
		if (source == null || configs == null)
			return source;
		for (int i = 0; i < configs.length; i++) {
			transform(source, configs[i], i);
		}
		return source;
	}

	public static CompositeMap transformByConfig(CompositeMap source,
			CompositeMap config) {
		if (source == null || config == null)
			return source;
		GroupConfig[] configs = GroupConfig.createGroupConfigs(config);
		return transform(source, configs);
	}

	public static void main(String[] args) {
		CompositeMap root = new CompositeMap("root");
		for (int i = 0; i < 10; i++) {
			CompositeMap record = new CompositeMap("recrod");
			record.put("name", "name_" + Math.round(Math.random() * 10));
			record.put("a", "a_" + Math.round(Math.random() * 10));
			record.put("b", "b_" + Math.round(Math.random() * 10));
			root.addChild(record);
		}
		System.out.println(root.toXML());
		CompositeMap copyRoot = (CompositeMap) root.clone();
		CompositeMap source = transform(root, new GroupConfig(
				new String[] { "name" }));
		System.out.println(source.toXML());

		CompositeMap test = new CompositeMap();
		CompositeMap record1 = new CompositeMap();
		record1.put(GroupConfig.KEY_GROUP_KEY_FIELDS, "name,a");
		record1.put(GroupConfig.KEY_RECORD_NAME, "level");
		test.addChild(record1);
		CompositeMap record2 = new CompositeMap();
		record2.put("group_key_fields", "b");
		record2.put(GroupConfig.KEY_RECORD_NAME, "test");
		test.addChild(record2);
		CompositeMap copy2 = transformByConfig(copyRoot, test);
		System.out.println(copy2.toXML());
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		// TODO Auto-generated method stub
		String source = elementConifg.getString(this.KEY_SOURCE);
		assert source != null;
		String target = elementConifg.getString(this.KEY_TARGET);
		assert target != null;
		String groupConfig = elementConifg.getString(this.KEY_GROUP_CONFIG);

		CompositeMap context = runner.getContext();
		CompositeMap sourceMap = (CompositeMap) context.getObject(source);

		if (groupConfig != null) {
			CompositeMap groupConfigMap = (CompositeMap) context
					.getObject(groupConfig);
			context.putObject(target,
					transformByConfig(sourceMap, groupConfigMap));
		} else {
			CompositeMap targetMap = (CompositeMap) sourceMap.clone();
			context.putObject(target, targetMap, true);
			this.transform(targetMap, this.elementConifg);
		}
	}

	@Override
	public void beginConfigure(CompositeMap config) {
		this.elementConifg = config;
	}
}
