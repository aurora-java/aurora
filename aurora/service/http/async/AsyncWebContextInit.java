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
    
    public static final String KEY_REQUEST_PIPE = "request_process_pipe";
    public static final String KEY_AUTOCRUD_PIPE = "auto_crud_pipe";
    
    IPipe       requestQueue;
    IPipe       autocrudQueue;
    

    public AsyncWebContextInit() {
        super();
    }
    
    protected IPipe createPipe(ServletContext servlet_context, String id, int min_threads, String key_in_request )
        throws Exception
    {
        IPipe pipe = new SimplePipe(id, min_threads);
        String name = super.getUncertainEngine().getMBeanName("pipe", "name="+id);
        MBeanRegister.resiterMBean(name, pipe);
        servlet_context.setAttribute(key_in_request, pipe);  
        return pipe;
    }

    @Override
    public void init(ServletContext servlet_context) throws Exception {
        super.init(servlet_context);
        requestQueue = createPipe(servlet_context, "request_process", 3, KEY_REQUEST_PIPE);
        autocrudQueue = createPipe(servlet_context, "autocrud_process", 3, KEY_AUTOCRUD_PIPE);
        /*
        requestQueue = new SimplePipe("application.request["+servlet_context.getContextPath()+"]", 2);
        String name = super.getUncertainEngine().getMBeanName("pipe", "name=request_process");
        MBeanRegister.resiterMBean(name, requestQueue);
        servlet_context.setAttribute(KEY_REQUEST_PIPE, requestQueue);
        */
    }
    
    public void closePipe(IPipe pipe){
        if(pipe!=null)
            try{
                pipe.shutdown();
            }catch(Exception ex){
                ex.printStackTrace();
            }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try{
           closePipe(requestQueue);
           closePipe(autocrudQueue);
        }finally{
            super.contextDestroyed(event);
        }
    }
   
    

}
