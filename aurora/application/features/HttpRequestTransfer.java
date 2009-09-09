/*
 * Created on 2009-9-8 下午01:27:55
 * Author: Zhou Fan
 */
package aurora.application.features;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceInstance;
import aurora.service.http.HttpServiceInstance;

public class HttpRequestTransfer implements ISingleton {
    
    public static void copyParameter( HttpServletRequest request, ServiceInstance svc ){
        Enumeration ep = request.getParameterNames();
        
        CompositeMap params = svc.getServiceContext().getParameter();
        while(ep.hasMoreElements()){
            String name = (String)ep.nextElement();
            String[] values = request.getParameterValues(name);
            if(values==null)
                params.put(name, null);
            else{
                if(values.length==1)
                    params.put(name, values[0]);
                else if(values.length>1)
                    params.put(name, values);
            }
        }        
    }
    
    public static void copyHeader( HttpServletRequest request, ServiceInstance svc ){
        CompositeMap req_map = svc.getContextMap().getChild("request");
        if(req_map==null)
            req_map = svc.getContextMap().createChild("request");
        Enumeration head_enum = request.getHeaderNames();
        while(head_enum.hasMoreElements()){
            String head = (String)head_enum.nextElement();
            String head_value = request.getHeader(head);
            req_map.put(head, head_value);
        }
        req_map.put("address", request.getRemoteAddr());    
    }
    
    public static void copyRequest( HttpServiceInstance svc ){
        copyParameter( svc.getRequest(), svc);
        copyHeader( svc.getRequest(), svc);
    }

}
