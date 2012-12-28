package aurora.presentation.component.std.config;

import uncertain.composite.CompositeMap;

/**
 * 
 * @version $Id: NumberFieldConfig.java 1242 2010-08-30 04:28:30Z
 *          njq.niu@gmail.com $
 * @author <a href="mailto:njq.niu@hand-china.com">vincent</a>
 */
public class SpinnerConfig extends InputFieldConfig {

	public static final String VERSION = "$Revision$";
	
	public static final String TAG_NAME = "spinner";
	public static final String PROPERTITY_MIN = "min";
	public static final String PROPERTITY_MAX = "max";
	public static final String PROPERTITY_STEP = "step";

	public static SpinnerConfig getInstance() {
		SpinnerConfig model = new SpinnerConfig();
		model.initialize(SpinnerConfig.createContext(null, TAG_NAME));
		return model;
	}

	public static SpinnerConfig getInstance(CompositeMap context) {
		SpinnerConfig model = new SpinnerConfig();
		model.initialize(SpinnerConfig.createContext(context, TAG_NAME));
		return model;
	}

	public String getMin() {
		return getString(PROPERTITY_MIN);
	}

	public void setMin(String min) {
		putString(PROPERTITY_MIN, min);
	}

	public String getStep() {
		return getString(PROPERTITY_STEP, "1");
	}

	public void setStep(String step) {
		putString(PROPERTITY_STEP, step);
	}

	public String getMax() {
		return getString(PROPERTITY_MAX);
	}

	public void setMax(String max) {
		putString(PROPERTITY_MAX, max);
	}
}
