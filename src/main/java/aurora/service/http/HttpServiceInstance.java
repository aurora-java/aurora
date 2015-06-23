/*
 * Created on 2009-9-2 下午03:13:19
 * Author: Zhou Fan
 */
package aurora.service.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;
import uncertain.proc.IProcedureManager;
import aurora.service.ServiceInstance;

public class HttpServiceInstance extends ServiceInstance {

    HttpServletRequest request;
    HttpServletResponse response;
    HttpServlet servlet;

    public HttpServiceInstance(String name, IProcedureManager proc_manager ) {
        super(name, proc_manager);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
        mServiceContext.setInstanceOfType(HttpServletRequest.class, request);
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
        mServiceContext.setInstanceOfType(HttpServletResponse.class, response);
    }

    public HttpServlet getServlet() {
        return servlet;
    }

    public void setServlet(HttpServlet servlet) {
        this.servlet = servlet;
    }

}
