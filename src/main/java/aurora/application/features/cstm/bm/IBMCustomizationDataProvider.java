package aurora.application.features.cstm.bm;

import aurora.bm.BusinessModel;
import uncertain.composite.CompositeMap;

public interface IBMCustomizationDataProvider {

	 public CompositeMap getCustomizationData(BusinessModel model,String function_code,CompositeMap context );
}
