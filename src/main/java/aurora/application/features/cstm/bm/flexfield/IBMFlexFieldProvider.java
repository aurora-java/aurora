package aurora.application.features.cstm.bm.flexfield;

import aurora.bm.BusinessModel;
import uncertain.composite.CompositeMap;

public interface IBMFlexFieldProvider {

	 public CompositeMap getFlexFieldData(BusinessModel model,CompositeMap context );
}
