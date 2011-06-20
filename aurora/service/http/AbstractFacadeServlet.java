/*
 * Created on 2009-9-3 上午10:56:14
 * Author: Zhou Fan
 */
package aurora.service.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.event.IParticipantManager;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.IProcedureRegistry;
import uncertain.proc.Procedure;
import aurora.events.E_DetectProcedure;
import aurora.service.IService;
import aurora.service.ServiceController;
import aurora.service.ServiceThreadLocal;
import aurora.transaction.ITransactionService;
import aurora.transaction.UserTransactionImpl;

public abstract class AbstractFacadeServlet extends HttpServlet {

	public static final String POST_SERVICE = "post-service";
	public static final String PRE_SERVICE = "pre-service";
	UncertainEngine mUncertainEngine;
	IProcedureManager mProcManager;
	ServletConfig mConfig;
	ServletContext mContext;
	IProcedureRegistry mProcRegistry;

	//Procedure mPreServiceProc;
	//Procedure mPostServiceProc;
	
	Configuration  mGlobalServiceConfig;

	protected abstract IService createServiceInstance(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	protected abstract void populateService(HttpServletRequest request,
			HttpServletResponse response, IService service) throws Exception;

	protected abstract void handleException(HttpServletRequest request,
			HttpServletResponse response, Exception ex) throws IOException, ServletException;

	protected abstract void cleanUp(IService service);

	public static Procedure getProcedureToRun(IProcedureManager procManager ,IService service) throws Exception {
		String procedure_name = null;
		Configuration config = service.getConfig();
		config
				.fireEvent(E_DetectProcedure.EVENT_NAME,
						new Object[] { service });
		ServiceController controller = ServiceController
				.createServiceController(service.getServiceContext()
						.getObjectContext());
		if(!controller.getContinueFlag())
		    return null;
		procedure_name = controller.getProcedureName();
		Procedure proc = procManager.loadProcedure(procedure_name);
		return proc;
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	    if(!mUncertainEngine.isRunning()){
	        StringBuffer msg = new StringBuffer("Application failed to initialize");
	        Throwable thr = mUncertainEngine.getInitializeException();
	        if(thr!=null)
	            msg.append(":").append(thr.getMessage());
	        response.sendError(500, msg.toString());
	        return;
	    }
	    
	    
		request.setCharacterEncoding("UTF-8");//form post encoding
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		IService svc = null;
		boolean is_success = true;
		UserTransaction trans = null;
		IObjectRegistry or = mUncertainEngine.getObjectRegistry();
		ITransactionService ts = (ITransactionService) or
				.getInstanceOfType(ITransactionService.class);
		if (ts == null)
			throw new ServletException("ITransactionService instance not found");		
		trans = ts.getUserTransaction();
		try {
			trans.begin();
			svc = createServiceInstance(request, response);
			ServiceThreadLocal.setCurrentThreadContext(svc.getServiceContext().getObjectContext());
			populateService(request, response, svc);

			Procedure pre_service_proc=null, post_service_proc=null;
	        
	        if (mProcRegistry != null) {
	            pre_service_proc = mProcRegistry.getProcedure(PRE_SERVICE);
	            post_service_proc = mProcRegistry.getProcedure(POST_SERVICE);
	        }

			
			if (pre_service_proc != null)
				is_success = svc.invoke(pre_service_proc);

			if (is_success) {
				Procedure proc = getProcedureToRun(mProcManager, svc);
				if(proc!=null){
				    is_success = svc.invoke(proc);
				}
			}
			if (is_success) {
				if (post_service_proc != null)
					is_success = svc.invoke(post_service_proc);
			}
		} catch (Exception ex) {
			is_success = false;
			mUncertainEngine.logException("Error when executing service "
					+ request.getRequestURI(), ex);
			handleException(request, response, ex);
		} finally {			
			if (trans instanceof UserTransactionImpl) {				
				((UserTransactionImpl) trans).initialize(svc);				
			}
			if (is_success) {
				try {
					trans.commit();
				} catch (Exception e) {
					mUncertainEngine.logException("Error when commit service "
							+ request.getRequestURI(), e);
				}
			} else {
				try {
					trans.rollback();
				} catch (Exception e) {
					mUncertainEngine.logException(
							"Error when rollback service "
									+ request.getRequestURI(), e);
				}
			}
			ServiceThreadLocal.remove();
			cleanUp(svc);
			ts.stop();
		}		
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		mConfig = config;
		mContext = config.getServletContext();
		mUncertainEngine = WebContextInit.getUncertainEngine(mContext);
		if (mUncertainEngine == null)
			throw new ServletException("Uncertain engine not initialized");
		mProcManager = mUncertainEngine.getProcedureManager();

		mProcRegistry = (IProcedureRegistry) mUncertainEngine
				.getObjectRegistry()
				.getInstanceOfType(IProcedureRegistry.class);
		
		// get global service config
		IObjectRegistry reg = getObjectRegistry();
		IParticipantManager pm = (IParticipantManager)reg.getInstanceOfType(IParticipantManager.class);
		if(pm!=null){
		    mGlobalServiceConfig = pm.getParticipantsAsConfig("service");
		}
	}
	
	public UncertainEngine getUncertainEngine(){
	    return mUncertainEngine;
	}
	
	public IObjectRegistry getObjectRegistry(){
	    return mUncertainEngine == null?null:mUncertainEngine.getObjectRegistry();
	}
	
	public Configuration getGlobalServiceConfig(){
	    return mGlobalServiceConfig;
	}

}
