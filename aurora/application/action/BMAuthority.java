package aurora.application.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import aurora.bm.BusinessModel;
import aurora.events.E_CheckBMAccess;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class BMAuthority {
	 public BMAuthority(){
		 
	 }
	public static final String DEFAULT_JSON_CONTENT_TYPE = "application/json;charset=utf-8";
	public void onAuthorityFailResponse(ServiceContext context) throws IOException,
			JSONException {
		CompositeMap cm = context.getObjectContext();
		HttpServiceInstance svc = (HttpServiceInstance) ServiceInstance
				.getInstance(cm);
		JSONObject json = new JSONObject();
		json.put("success", false);

		boolean write_result = context.getBoolean("write_result", true);
		if (write_result) {

			//CompositeMap result = null;
			JSONObject error = new JSONObject();
			error.put("message", svc.getName() +cm.getString("error_msg"));
			error.put("code", cm.getString("error_msg"));
			json.put("error", error);
		}
		prepareResponse(svc.getResponse());
		PrintWriter out = svc.getResponse().getWriter();
		json.write(out);
	}
	void prepareResponse(HttpServletResponse response)

	{
		response.setContentType(DEFAULT_JSON_CONTENT_TYPE);
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
	}
}
