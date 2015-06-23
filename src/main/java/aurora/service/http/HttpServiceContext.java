/*
 * Created on 2009-9-1
 */
package aurora.service.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aurora.service.ServiceContext;

public class HttpServiceContext extends ServiceContext {
    
    public HttpServletRequest getRequest() {
        return (HttpServletRequest)getInstanceOfType(HttpServletRequest.class);
    }
    /**
     * @param request the request to set
     */
    public void setRequest(HttpServletRequest request) {
        setInstanceOfType(HttpServletRequest.class, request);
    }

    /**
     * @return the response
     */
    public HttpServletResponse getResponse() {
        return (HttpServletResponse)getInstanceOfType(HttpServletResponse.class);
    }

    /**
     * @param response the response to set
     */
    public void setResponse(HttpServletResponse response) {
        setInstanceOfType(HttpServletResponse.class, response);
    }
    

}
