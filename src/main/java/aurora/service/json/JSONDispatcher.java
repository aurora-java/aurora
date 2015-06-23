/*
 * Created on 2007-11-4
 */
package aurora.service.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.JSONAdaptor;
import uncertain.event.EventModel;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;

/**
 * Parse JSON request parameter into CompositeMap JSONRequest
 * 
 * @author Zhou Fan
 * 
 */

public class JSONDispatcher {
    // public static final String PARAMETER = "parameter";

    public static final String DEFAULT_JSON_CONTENT_TYPE = "application/json;charset=utf-8";

    public static final String HEAD_JSON_PARAMETER = "json-parameter";

    public static final String DEFAULT_JSON_PARAMETER = "_request_data";

    public static final String KEY_WRITE_BACK_INPUT = "write_back_input";

    static final JSONObject EMPTY_JSON_OBJECT = new JSONObject();

    /* Variable for internal use */
    ServiceContext service_context;
    HttpServletRequest request;
    HttpServletResponse response;

    String output;
    String array;
    Set arrayNameSet;

    public JSONDispatcher() {
    }

    public int preParseParameter(JSONServiceContext ct) throws Exception {
        service_context = ct;
        request = ct.getRequest();
        response = ct.getResponse();
        request.setCharacterEncoding("utf-8");
        String jparam = request.getHeader(HEAD_JSON_PARAMETER);
        if (jparam == null)
            jparam = DEFAULT_JSON_PARAMETER;

        String content = request.getParameter(jparam);
        if (content != null) {
            JSONObject jobj = new JSONObject(content);
            CompositeMap root = JSONAdaptor.toMap(jobj);
            if (root == null)
                return EventModel.HANDLE_STOP;
            CompositeMap param = root.getChild("parameter");
            if (param != null)
                service_context.setParameter(param);
            return EventModel.HANDLE_STOP;
        } else
            return EventModel.HANDLE_NORMAL;

    }

    void prepareResponse(HttpServletResponse response)

    {
        response.setContentType(DEFAULT_JSON_CONTENT_TYPE);
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
    }

    void prepareArrayNameSet() {
        if (arrayNameSet == null)
            arrayNameSet = new HashSet();
        arrayNameSet.clear();
        Iterator it = service_context.getModel().getChildIterator();
        if (it != null)
            while (it.hasNext()) {
                CompositeMap item = (CompositeMap) it.next();
                arrayNameSet.add(item.getName());
            }
    }

    public void writeResponse() throws IOException, JSONException {
        JSONObject json = new JSONObject();
        // Write success flag
        json.put("success", service_context.isSuccess());
        // Write service invoke result
        boolean write_result = service_context.getBoolean("write_result", true);
        if (write_result) {
            // CompositeMap result = context_map.getChild("result");
            CompositeMap result = null;
            prepareArrayNameSet();
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
                JSONObject o = JSONAdaptor.toJSONObject(result, arrayNameSet);
                json.put("result", o);
            }
        }
        prepareResponse(response);
        PrintWriter out = response.getWriter();
        json.write(out);
    }

    public void onCreateSuccessResponse() throws IOException, JSONException {
        writeResponse();
    }

    public void onCreateFailResponse(ServiceContext context)
            throws IOException, JSONException {
        prepareResponse(response);
        PrintWriter out = response.getWriter();
        out.println("{ \"success\":false ");
        CompositeMap error = context.getError();
        if (error != null) {
            out.println(",error:");
            out.println(JSONAdaptor.toJSONObject(error).toString());
        }
        out.println("} ");
        out.flush();
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return output;
    }

    /**
     * @param output
     *            the output to set
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * @return the array
     */
    public String getArray() {
        return array;
    }

    /**
     * @param array
     *            the array to set
     */
    public void setArray(String array) {
        this.array = array;
        if (arrayNameSet != null)
            arrayNameSet.clear();
        arrayNameSet = new HashSet();
        if (array != null) {
            String[] s = array.split(",");
            for (int i = 0; i < s.length; i++)
                arrayNameSet.add(s[i].trim());
        }
    }

}
