package aurora.application.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class AuroraCookie extends AbstractEntry {

	private String name;
	private String value;
    private int maxAge=-1;
	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	public void run(ProcedureRunner runner) throws Exception {

		CompositeMap mContext = runner.getContext();
		HttpServiceInstance mService = (HttpServiceInstance) ServiceInstance.getInstance(mContext);
		HttpServletResponse response = mService.getResponse();
		HttpServletRequest request = mService.getRequest();
		this.setValue  (TextParser.parse(this.getValue(), mContext));
		Cookie cookie = new Cookie(name, value);
		String path = request.getContextPath();
		path = (path == null || path.length()==0) ? "/" : path;
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);

	}

}
