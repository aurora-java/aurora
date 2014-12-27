/*
 * Created on 2014年12月21日 下午6:24:19
 * $Id$
 */
package aurora.service.http.async;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pipe.base.IPipe;
import uncertain.proc.IProcedureManager;
import uncertain.proc.IProcedureRegistry;
import aurora.service.IService;
import aurora.service.http.FacadeServlet;

@WebServlet(asyncSupported = true)
public class AsyncServlet extends FacadeServlet {

    IPipe outputPipe;

    @Override
    protected void doService(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // check if UncertainEngine is inited properly
        if (!mUncertainEngine.isRunning()) {
            StringBuffer msg = new StringBuffer(
                    "Application failed to initialize");
            Throwable thr = mUncertainEngine.getInitializeException();
            if (thr != null)
                msg.append(":").append(thr.getMessage());
            response.sendError(500, msg.toString());
            return;
        }

        AsyncContext context = request.startAsync();
        outputPipe.addData(context);

    }
    
    

    @Override
    protected IService createServiceInstance(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return super.createServiceInstance(request, response);
    }

    @Override
    protected void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception {
        super.populateService(request, response, service);
    }

    @Override
    protected void handleException(HttpServletRequest request,
            HttpServletResponse response, Throwable ex) {
        try {
            super.handleException(request, response, ex);
        } catch (Exception nex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void cleanUp(IService svc) {
        super.cleanUp(svc);
    }

    public IProcedureRegistry getProcedureRegistry() {
        return super.mProcRegistry;
    }

    public IProcedureManager getProcedureManager() {
        return super.mProcManager;
    }



    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        RequestProcessor processor = new RequestProcessor(this);
        outputPipe = (IPipe)config.getServletContext().getAttribute(AsyncWebContextInit.KEY_REQUEST_PIPE);
        if(outputPipe==null)
            throw new IllegalStateException("Initial pipe not created");
        outputPipe.setEndPoint(processor);
        outputPipe.start();
        System.out.println("Request process pipe created");
    }

}