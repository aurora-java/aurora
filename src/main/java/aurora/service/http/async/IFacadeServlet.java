/*
 * Created on 2014年12月28日 下午4:13:14
 * $Id$
 */
package aurora.service.http.async;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.core.UncertainEngine;
import uncertain.proc.IProcedureManager;
import uncertain.proc.IProcedureRegistry;
import aurora.service.IService;

public interface IFacadeServlet {

    public void doService(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException;

    public IService createServiceInstance(HttpServletRequest request,
            HttpServletResponse response) throws Exception;

    public void populateService(HttpServletRequest request,
            HttpServletResponse response, IService service) throws Exception;

    public void handleException(HttpServletRequest request,
            HttpServletResponse response, Throwable ex);

    public void cleanUp(IService svc);

    public IProcedureRegistry getProcedureRegistry();

    public IProcedureManager getProcedureManager();

    public void init(ServletConfig config) throws ServletException;
    
    public UncertainEngine getUncertainEngine();

}