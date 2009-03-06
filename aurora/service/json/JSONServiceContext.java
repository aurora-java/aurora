/*
 * Created on 2007-11-26
 */
package aurora.service.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aurora.service.ServiceContext;

public class JSONServiceContext extends ServiceContext {
    
    public static final String KEY_REQUEST = "httprequest";
    public static final String KEY_RESPONSE = "httpresponse";    
    
    HttpServletRequest request;
    HttpServletResponse response;
    /**
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return (HttpServletRequest)get(KEY_REQUEST);
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
        return (HttpServletResponse)get(KEY_RESPONSE);
    }
    /**
     * @param response the response to set
     */
    public void setResponse(HttpServletResponse response) {
        put(KEY_RESPONSE, response);
    }
    
    
    

}
