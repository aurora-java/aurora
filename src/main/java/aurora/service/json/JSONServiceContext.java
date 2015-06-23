/*
 * Created on 2007-11-26
 */
package aurora.service.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.event.RuntimeContext;

import aurora.service.ServiceContext;

public class JSONServiceContext extends ServiceContext {
    
    public static final String KEY_REQUEST = "httprequest";
    public static final String KEY_RESPONSE = "httpresponse";    

    /**
     * @return the request
     */
    public HttpServletRequest getRequest() {
        HttpServletRequest request = (HttpServletRequest)get(KEY_REQUEST);
        if(request==null){
            request = (HttpServletRequest)get(RuntimeContext.getTypeKey(HttpServletRequest.class));
        }
        return request;
    }
    /**
     * @param request the request to set
     */
    public void setRequest(HttpServletRequest request) {
        put(KEY_REQUEST,request);
    }
    /**
     * @return the response
     */
    public HttpServletResponse getResponse() {
        HttpServletResponse response = (HttpServletResponse)get(KEY_RESPONSE);
        if(response==null){
            response = (HttpServletResponse)get(RuntimeContext.getTypeKey(HttpServletResponse.class));
        }
        return response;        
    }
    /**
     * @param response the response to set
     */
    public void setResponse(HttpServletResponse response) {
        put(KEY_RESPONSE, response);
    }
    
    
    

}
