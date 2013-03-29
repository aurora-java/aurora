/*
 * Created on 2011-7-26 下午12:13:12
 * $Id$
 */
package aurora.service;

import java.sql.Connection;

import uncertain.composite.CompositeMap;
import uncertain.proc.Procedure;
import aurora.database.service.SqlServiceContext;

public class ServiceInvoker {

    public static void invokeProcedureWithTransaction(String service_name, Procedure app_proc, IServiceFactory service_factory, CompositeMap context )
        throws Exception
    {
        ServiceContext scx = ServiceContext.createServiceContext( context);
        try{
            service_factory.beginService(context);
            IService service = (IService)service_factory.createService(service_name, context);
            service.invoke(app_proc);
            service.release();
        }catch(Exception ex){
            scx.setSuccess(false);
            throw ex;
        }finally{
            service_factory.finishService(context);
        }
        
    }
    public static void invokeProcedureWithTransaction(String service_name, Procedure app_proc, IServiceFactory service_factory,IService service, CompositeMap context ) throws Exception{
    	ServiceContext scx = ServiceContext.createServiceContext( context);
        try{
            service_factory.beginService(context);
            service.invoke(app_proc);
            service.release();
        }catch(Exception ex){
            scx.setSuccess(false);
            throw ex;
        }finally{
            service_factory.finishService(context);
        }
    }

    public static void invokeProcedureWithTransaction(String service_name, Procedure app_proc, IServiceFactory service_factory )
        throws Exception
    {
        CompositeMap context = new CompositeMap(service_name + "context");
        try{
            invokeProcedureWithTransaction(service_name, app_proc, service_factory, context);
        }finally{
            context.clear();
        }
    }

	public static void invokeProcedureWithTransaction(String service_name, Procedure app_proc, IServiceFactory service_factory,
			CompositeMap context, Connection borrowConnection) throws Exception {
		ServiceContext scx = ServiceContext.createServiceContext(context);
		SqlServiceContext ctx = SqlServiceContext.createSqlServiceContext(context);
		ctx.setConnection(borrowConnection);
		try {
			service_factory.beginService(context);
			IService service = (IService) service_factory.createService(service_name, context);
			service.invoke(app_proc);
			service.release();
		} catch (Exception ex) {
			scx.setSuccess(false);
			throw ex;
		} finally {
			ctx.getAllConnection().remove(borrowConnection);
			service_factory.finishService(context);
		}
	}

	public static void invokeProcedureWithTransaction(String service_name, Procedure app_proc, IServiceFactory service_factory,
			Connection borrowConnection) throws Exception {
		CompositeMap context = new CompositeMap(service_name + "context");
		try {
			invokeProcedureWithTransaction(service_name, app_proc, service_factory, context, borrowConnection);
		} finally {
			context.clear();
		}
	}

}
