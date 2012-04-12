/*
 * Created on 2007-11-4
 */
package aurora.service.json;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.ProcedureRunner;
import uncertain.util.LoggingUtil;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;
import aurora.service.ServiceOutputConfig;
import aurora.service.http.HttpServiceInstance;

/**
 * Parse JSON request parameter into CompositeMap JSONRequest
 * 
 * @author Zhou Fan
 * 
 */

public class JSONServiceInterpreter {
    // public static final String PARAMETER = "parameter";

    public static final String DEFAULT_JSON_CONTENT_TYPE = "application/json;charset=utf-8";

    public static final String HEAD_JSON_PARAMETER = "json-parameter";

    public static final String DEFAULT_JSON_PARAMETER = "_request_data";

    public static final String KEY_WRITE_BACK_INPUT = "write_back_input";

    static final JSONObject EMPTY_JSON_OBJECT = new JSONObject();

    public static String JSON_ACCESS_PARAMTER = "json_access";
    public JSONServiceInterpreter() {
    }

    public int preParseParameter(JSONServiceContext ct) throws Exception {
    	if(!isJSONRequest(ct.getRequest(), ct))
    		return EventModel.HANDLE_NORMAL;
        ServiceContext service_context = ct;
        HttpServletRequest request = ct.getRequest();
        //HttpServletResponse response = ct.getResponse();
        request.setCharacterEncoding("utf-8");
        String jparam = request.getHeader(HEAD_JSON_PARAMETER);
        if (jparam == null)
            jparam = DEFAULT_JSON_PARAMETER;

        String content = request.getParameter(jparam);
        if (content != null) {
            service_context.getParameter().remove(jparam);
            JSONObject jobj = new JSONObject(content);
            CompositeMap root = JSONAdaptor.toMap(jobj);
            if (root == null)
                return EventModel.HANDLE_STOP;
            CompositeMap param = root.getChild("parameter");
            if (param != null){
                param.putAll(root);
                service_context.setParameter(param);
            }
            return EventModel.HANDLE_STOP;
        } else
            return EventModel.HANDLE_NORMAL;

    }

    void prepareResponse(HttpServletResponse response)

    {
        response.setContentType(DEFAULT_JSON_CONTENT_TYPE);
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
    }

    public void writeResponse( ServiceContext service_context ) throws IOException, JSONException {
    	if(!isJSONRequest(service_context))
    		 return;
        HttpServiceInstance svc = (HttpServiceInstance)ServiceInstance.getInstance(service_context.getObjectContext());
        String output = null;
        ServiceOutputConfig cfg = svc.getServiceOutputConfig();
        if(cfg!=null)
            output = cfg.getOutput();
        JSONObject json = new JSONObject();
        // Write success flag
        json.put("success", service_context.isSuccess());
        // Write service invoke result
        boolean write_result = service_context.getBoolean("write_result", true);
        if (write_result) {
            // CompositeMap result = context_map.getChild("result");
            CompositeMap result = null;
            if (output != null) {
                Object obj = service_context.getObjectContext().getObject(
                        output);
                if (!(obj instanceof CompositeMap))
                    throw new IllegalArgumentException(
                            "Target for JSON output is not instance of CompositeMap: "
                                    + obj);
                result = (CompositeMap) obj;
            } else
                result = service_context.getModel();
            if (result != null) {
                JSONObject o = JSONAdaptor.toJSONObject(result);
                json.put("result", o);
            }
        }
        prepareResponse(svc.getResponse());
        PrintWriter out = svc.getResponse().getWriter();
        json.write(out);
    }

    public void onCreateSuccessResponse( ServiceContext service_context ) throws IOException, JSONException {
        if(isJSONRequest(service_context))
        	writeResponse(service_context);
    }

    public void onCreateFailResponse(ProcedureRunner runner)
            throws IOException, JSONException {
        ServiceContext context = ServiceContext.createServiceContext(runner.getContext());
        if(!isJSONRequest(context))
        	return;
        // log exception
        ILogger logger = LoggingContext.getLogger(context.getObjectContext(), ServiceInstance.LOGGING_TOPIC);
        Throwable thr = runner.getException();
        if(thr!=null){
            LoggingUtil.logException(thr, logger);
        }
        HttpServiceInstance svc = (HttpServiceInstance)ServiceInstance.getInstance(context.getObjectContext());        
        HttpServletResponse response = svc.getResponse();
        /*
        if(response.isCommitted()){
            logger.info("response is already commited, no JSON response will be written");
            return;
        }
        */
        prepareResponse(response);
        PrintWriter out = response.getWriter();
        out.println("{ \"success\":false ");
        CompositeMap error = context.getError();
        if (error != null) {
            out.println(",\"error\":");
            out.println(JSONAdaptor.toJSONObject(error).toString());
        }
        out.println("} ");
        out.flush();
    }
    private boolean isJSONRequest(ServiceContext service_context){
    	HttpServiceInstance svc = (HttpServiceInstance)ServiceInstance.getInstance(service_context.getObjectContext());
    	return isJSONRequest(svc.getRequest(), service_context);
    }
    private boolean isJSONRequest(HttpServletRequest request, ServiceContext context){
    	String httptype = request.getHeader("x-requested-with");
 		if ("XMLHttpRequest".equals(httptype)) {
 			return true;
 		}else if("true".equals(request.getParameter(JSON_ACCESS_PARAMTER))){
 			return true;
 		}else if(context.getRequestType()!=null){
 		    return false;
 		}
    	return true;
    }
}
