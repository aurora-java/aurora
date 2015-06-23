package aurora.application.features.screen;

import java.util.ArrayList;
import java.util.List;

import uncertain.composite.IterationHandle;


public abstract class CompositeMapIteator implements IterationHandle {
	private IDataFilter filter;

	private List<MapFinderResult> result = new ArrayList<MapFinderResult>();

	public IDataFilter getFilter() {
		return filter;
	}

	public void setFilter(IDataFilter filter) {
		this.filter = filter;
	}

	public List<MapFinderResult> getResult() {
		return result;
	}

}
