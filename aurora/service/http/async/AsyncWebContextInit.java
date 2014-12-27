/*
 * Created on 2014年12月24日 下午3:23:29
 * $Id$
 */
package aurora.service.http.async;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import pipe.base.IPipe;
import pipe.simple.SimplePipe;
import uncertain.mbean.MBeanRegister;
import aurora.service.http.WebContextInit;

public class AsyncWebContextInit extends WebContextInit {
    
    public static final String KEY_REQUEST_PIPE = "request_pipe";
    
    IPipe       requestQueue;
    

    public AsyncWebContextInit() {
        super();
    }

    @Override
    public void init(ServletContext servlet_context) throws Exception {
        super.init(servlet_context);
        requestQueue = new SimplePipe("application.request["+servlet_context.getContextPath()+"]", 2);
        String name = super.getUncertainEngine().getMBeanName("pipe", "name=request_process");
        MBeanRegister.resiterMBean(name, requestQueue);
        servlet_context.setAttribute(KEY_REQUEST_PIPE, requestQueue);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try{
            if(requestQueue!=null)
                requestQueue.shutdown();
        }finally{
            super.contextDestroyed(event);
        }
    }
   
    

}
