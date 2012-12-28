package aurora.presentation.component.std.config;

import java.util.List;

import uncertain.composite.CompositeMap;

public class TableColumnConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "column";

	public static final String PROPERTITY_EDITOR = "editor";
	public static final String PROPERTITY_ALIGN = "align";
	public static final String PROPERTITY_HIDDEN = "hidden";
	public static final String PROPERTITY_RENDERER = "renderer";
	public static final String PROPERTITY_FOOTER_RENDERER = "footerrenderer";
	public static final String PROPERTITY_PERCENT_WIDTH = "percentwidth";

	private static final String DEFAULT_ALIGN = "left";

	public static TableColumnConfig getInstance() {
		TableColumnConfig model = new TableColumnConfig();
		model.initialize(TableColumnConfig.createContext(null, TAG_NAME));
		model.removeMapping();
		return model;
	}

	public static TableColumnConfig getInstance(CompositeMap context) {
		TableColumnConfig model = new TableColumnConfig();
		model.initialize(TableColumnConfig.createContext(context, TAG_NAME));
		model.removeMapping();
		return model;
	}

	private void removeMapping() {
		List childs = object_context.getChilds();
		if (childs != null) {
			Object[] array = childs.toArray();
			for (int i = 0; i < array.length; i++) {
				CompositeMap map = (CompositeMap) array[i];
				object_context.removeChild(map);
			}
		}
	}

	public boolean isHidden() {
		return getBoolean(PROPERTITY_HIDDEN, false);
	}

	public void setHidden(boolean hidden) {
		putBoolean(PROPERTITY_HIDDEN, hidden);
	}

	public String getAlign() {
		return getString(PROPERTITY_ALIGN, DEFAULT_ALIGN);
	}

	public void setAlign(String align) {
		putString(PROPERTITY_ALIGN, align);
	}

	public String getPrompt() {
		return getString(PROPERTITY_PROMPT);
	}

	public void setPrompt(String prompt) {
		putString(PROPERTITY_PROMPT, prompt);
	}

	public String getPercentWidth() {
		return getString(PROPERTITY_PERCENT_WIDTH);
	}

	public void setPercentWidth(String percentWidth) {
		putString(PROPERTITY_PERCENT_WIDTH, percentWidth);
	}

	public String getFooterRenderer() {
		return getString(PROPERTITY_FOOTER_RENDERER);
	}

	public void setFooterRenderer(String renderer) {
		putString(PROPERTITY_FOOTER_RENDERER, renderer);
	}

	public String getRenderer() {
		return getString(PROPERTITY_RENDERER, "");
	}

	public void setRenderer(String renderer) {
		putString(PROPERTITY_RENDERER, renderer);
	}

	public String getEditor() {
		return getString(PROPERTITY_EDITOR);
	}

	public void setEditor(String editor) {
		putString(PROPERTITY_EDITOR, editor);
	}
}
