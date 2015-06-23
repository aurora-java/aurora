package aurora.application.action;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class Redirect extends AbstractEntry{

	private String directUrl = null;
	
	public String getDirectUrl() {
		return directUrl;
	}

	public void setDirectUrl(String directUrl) {
		this.directUrl = directUrl;
	}

	public void run(ProcedureRunner runner) throws Exception {
		String directUrl = getDirectUrl();
		if(directUrl!=null){
			CompositeMap context = runner.getContext();
			directUrl = TextParser.parse(directUrl, context);
			HttpServiceInstance serviceInstance = (HttpServiceInstance) ServiceInstance.getInstance(context);
			serviceInstance.getResponse().sendRedirect(directUrl);
		}
	}

}
