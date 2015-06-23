package aurora.presentation.component.std;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FieldBoxConfig;
import aurora.presentation.component.std.config.FieldGroupConfig;

public class FieldGroup extends HBox {

	public static final String VERSION = "$Revision$";

	public FieldGroup(IObjectRegistry registry) {
		super(registry);
	}

	protected void beforeBuildCell(BuildSession session, CompositeMap model,
			CompositeMap view, CompositeMap field) throws Exception {
		FieldGroupConfig fgc = FieldGroupConfig.getInstance(view);
		int padding = fgc.getPadding(model, FieldGroupConfig.DEFAULT_PADDING);
		view.putInt(FieldGroupConfig.PROPERTITY_PADDING, 0);
		Iterator it = view.getChildIterator();
		Integer fieldHeight = fgc.getFieldHeight(model);
		String fontStyle = fgc.getFontStyle(model);
		while (it.hasNext()) {
			CompositeMap child = (CompositeMap) it.next();
			if(null == child.getString(FieldGroupConfig.PROPERTITY_HEIGHT) && null != fieldHeight){
				child.putInt(FieldGroupConfig.PROPERTITY_HEIGHT, fieldHeight.intValue());
			}
			String fieldFontStyle = TextParser.parse(child.getString(FieldBoxConfig.PROPERTITY_FONT_STYLE),model);
			if(null != fontStyle && null == fieldFontStyle){
				child.putString(FieldBoxConfig.PROPERTITY_FONT_STYLE, fontStyle);
			}
			child.putString(
					ComponentConfig.PROPERTITY_STYLE,
					"margin-right:" + padding + "px;"
							+ child.getString(ComponentConfig.PROPERTITY_STYLE));
		}
	}
}
