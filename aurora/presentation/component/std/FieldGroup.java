package aurora.presentation.component.std;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.presentation.BuildSession;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.FieldGroupConfig;
import aurora.presentation.component.std.config.GridLayoutConfig;

public class FieldGroup extends HBox {

	public static final String VERSION = "$Revision$";

	public FieldGroup(IObjectRegistry registry) {
		super(registry);
	}

	protected void beforeBuildCell(BuildSession session, CompositeMap model,
			CompositeMap view, CompositeMap field) throws Exception {
		int padding = view.getInt(GridLayoutConfig.PROPERTITY_PADDING, 3);
		view.putInt(GridLayoutConfig.PROPERTITY_PADDING, 0);
		Iterator it = view.getChildIterator();
		FieldGroupConfig fgc = FieldGroupConfig.getInstance(view);
		Integer fieldHeight = fgc.getFieldHeight(model);
		while (it.hasNext()) {
			CompositeMap child = (CompositeMap) it.next();
			if(null == child.getString(FieldGroupConfig.PROPERTITY_HEIGHT) && null != fieldHeight){
				child.putInt(FieldGroupConfig.PROPERTITY_HEIGHT, fieldHeight.intValue());
			}
			child.putString(
					ComponentConfig.PROPERTITY_STYLE,
					"margin-right:" + padding + "px;"
							+ child.getString(ComponentConfig.PROPERTITY_STYLE));
		}
	}
}
