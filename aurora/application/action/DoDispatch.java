package aurora.application.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class DoDispatch {
	public static final String DEFAULT_JSON_CONTENT_TYPE = "text/html;charset=utf-8";

	Set arrayNameSet;
	String output;

	public void onDoDispatch(ServiceContext context) throws IOException,
			JSONException {
		CompositeMap cm = context.getObjectContext();
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(cm);
		String httptype = svc.getRequest().getHeader("x-requested-with")==null?"null":svc.getRequest().getHeader("x-requested-with").toString();
	
		if (httptype.equals("XMLHttpRequest")){
		JSONObject json = new JSONObject();
		json.put("success", false);
		
		boolean write_result = context.getBoolean("write_result", true);
		if (write_result) {
			
			CompositeMap result = null;
			JSONObject error = new JSONObject();
			error.put("message", svc.getName()+"没有注册，请联系系统管理员");
		   json.put("error",error);
		}
		prepareResponse(svc.getResponse());
		PrintWriter out = svc.getResponse().getWriter();
		json.write(out);
		} else {
		svc.getResponse().sendRedirect(cm.get("dispatch_url").toString()+"?url="+svc.getName());
	}
	}

	void prepareArrayNameSet(ServiceContext context) {
		if (arrayNameSet == null)
			arrayNameSet = new HashSet();
		arrayNameSet.clear();
		Iterator it = context.getModel().getChildIterator();
		if (it != null)
			while (it.hasNext()) {
				CompositeMap item = (CompositeMap) it.next();
				arrayNameSet.add(item.getName());

			}
	}

	void prepareResponse(HttpServletResponse response)

	{
		response.setContentType(DEFAULT_JSON_CONTENT_TYPE);
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
	}
}
