package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

public class PortalConfig extends ComponentConfig {

	public static final String VERSION = "$Revision$";

	public static final String TAG_NAME = "portalPanel";

	public static final String PROPERTITY_PORTALS = "portals";

	public static final String PROPERTITY_BLOCK_HEIGHT = "blockheight";
	public static final String PROPERTITY_BLOCK_WIDTH = "blockwidth";
	public static final String PROPERTITY_CELLSPACING = "cellspacing";
	public static final String PROPERTITY_MOVABLE = "movable";

	public static PortalConfig getInstance() {
		PortalConfig model = new PortalConfig();
		model.initialize(GridConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static PortalConfig getInstance(CompositeMap context) {
		PortalConfig model = new PortalConfig();
		model.initialize(GridConfig.createContext(context, TAG_NAME));
		return model;
	}

	public CompositeMap getPortals() {
		return getObjectContext().getChild(PROPERTITY_PORTALS);
	}

	public int getBlockHeight() {
		return getInt(PROPERTITY_BLOCK_HEIGHT, 200);
	}

	public void setBlockHeight(int blockHeight) {
		putInt(PROPERTITY_BLOCK_HEIGHT, blockHeight);
	}

	public int getBlockWidth() {
		return getInt(PROPERTITY_BLOCK_WIDTH, 200);
	}

	public void setBlockWidth(int blockWidth) {
		putInt(PROPERTITY_BLOCK_WIDTH, blockWidth);
	}

	public int getCellSpacing() {
		return getInt(PROPERTITY_CELLSPACING, 30);
	}

	public void setCellSpacing(int cellspacing) {
		putInt(PROPERTITY_CELLSPACING, cellspacing);
	}
	
	public boolean isMovable(CompositeMap model) {
		String str = uncertain.composite.TextParser.parse(getString(PROPERTITY_MOVABLE), model);
		if(null == str||"".equals(str)){
			return true;
		}
		return Boolean.valueOf(str);
	}
	
	public void setMovable(boolean movable) {
		putBoolean(PROPERTITY_MOVABLE, movable);
	}
}
