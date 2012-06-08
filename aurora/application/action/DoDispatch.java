package aurora.application.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class DoDispatch {
	public static final String DEFAULT_JSON_CONTENT_TYPE = "application/json;charset=utf-8";

	Set arrayNameSet;
	String output;
	String type;

	public void onDoDispatch(ServiceContext context) throws IOException,
			JSONException, ServletException {
		CompositeMap cm = context.getObjectContext();
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(cm);
		// 2B 青年的写法
		//String httptype = svc.getRequest().getHeader("x-requested-with") == null ? "null"
		//		: svc.getRequest().getHeader("x-requested-with").toString();
		// if (httptype.equals("XMLHttpRequest")) {

        // 文艺青年的写法
        String httptype = svc.getRequest().getHeader("x-requested-with");
		if ("XMLHttpRequest".equals(httptype)) {
			/*
		    JSONObject json = new JSONObject();
			json.put("success", false);
			boolean write_result = context.getBoolean("write_result", true);
			if (write_result) {
				//CompositeMap result = null;
				JSONObject error = new JSONObject();
				error.put("message", svc.getName() + cm.getString("error_msg"));
				error.put("code", cm.getString("error_msg"));
				json.put("error", error);
			}
			*/
		    JSONObject json = new JSONObject();
            json.put("success", false);
		    CompositeMap error_map = svc.getServiceContext().getError();
		    if(error_map!=null){
		        JSONObject err = JSONAdaptor.toJSONObject(error_map);
		        json.put("error", err);
		    }
			prepareResponse(svc.getResponse());
			PrintWriter out = svc.getResponse().getWriter();
			json.write(out);
		} else {
		    String url = context.getString("dispatch_url");
		    if(url!=null){
		        String uri = svc.getRequest().getRequestURI();
		        if(uri.indexOf(url)<0) {
		        	if("forward".equals(type)){
		        		svc.getRequest().getRequestDispatcher(url).forward(svc.getRequest(), svc.getResponse());
		        	}else{
		        		svc.getResponse().sendRedirect(url);
		        	}
		        }
		    }
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
